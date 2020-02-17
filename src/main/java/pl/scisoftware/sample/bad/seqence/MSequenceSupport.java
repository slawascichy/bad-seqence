package pl.scisoftware.sample.bad.seqence;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import org.scam.shared.utils.LockGeneratorUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import pl.scisoftware.sample.bad.seqence.api.MEntity;
import pl.scisoftware.sample.bad.seqence.api.MSequenceDAO;
import pl.scisoftware.sample.bad.seqence.api.MercuryException;
import pl.scisoftware.sample.bad.seqence.api.SerializableObject;
import pl.scisoftware.sample.bad.seqence.api.UnsupportedMethodException;

@SuppressWarnings("serial")
public abstract class MSequenceSupport implements MSequenceDAO {

	protected final transient Logger logger = LoggerFactory.getLogger(getClass().getName());
	/** Domyślna wartość inkrementacji sekwencji w bazie danych SQL */
	public static final String defaultIncremetValue = "100";
	public static final Long INCREMENT_VALUE = 1L;
	public static final String SEQUENCE_NAME_SUFFIX = "Seq";

	/**
	 * Pomocnicza mapa sekwencji. Kluczem jest nazwa klasy encji, wartością obiekt
	 * sekwencji {@link MSequenceObject}
	 */
	private static final Map<String, MSequenceObject> sequencesMap = new HashMap<>();

	/* Overridden (non-Javadoc) */
	@Override
	public Object createNewId(Class<? extends MEntity> persistentClass) throws MercuryException {
		return incremetSequenceId(persistentClass, INCREMENT_VALUE);
	}

	/* Overridden (non-Javadoc) */
	@Override
	public Object incremetSequenceId(Class<? extends MEntity> persistentClass, Long value) throws MercuryException {
		return incremetSequenceId(createSequenceClassName(persistentClass), value);
	}

	/* Overridden (non-Javadoc) */
	@Override
	public Object incremetSequenceId(String seqClazzName, final Long value) throws MercuryException {
		try {
			Long incremetValue = value;
			if (incremetValue == null) {
				incremetValue = 1L;
			}

			/* pobranie obiektu sekwencji START */
			MSequenceObject seqObj = null;
			final Object seqLock = LockGeneratorUtils.generateLock(this, "incremetSequenceId", seqClazzName);
			synchronized (seqLock) {
				seqObj = sequencesMap.computeIfAbsent(seqClazzName, k -> {
					try {
						return new MSequenceObject(seqClazzName);
					} catch (ClassNotFoundException e) {
						throw new IllegalStateException(e);
					}
				});
			}
			/* pobranie obiektu sekwencji KONIEC */

			synchronized (seqObj.lock) {
				if (logger.isTraceEnabled()) {
					logger.trace("[{}]--> createNewId[BEFORE]: currValue={}, buffValue={}, value={}",
							seqObj.seqClazz.getSimpleName(), seqObj.currValue, seqObj.buffValue, incremetValue);
				}
				Long newId = seqObj.currValue + incremetValue;
				if (!seqObj.isInit || newId > seqObj.buffValue) {
					/**
					 * Sekwencja nie jest zainicjalizowana lub nowa wartość przekracza wartości
					 * buforowane...
					 */
					/*
					 * Tworzę instancję encji z którą jest powiązana sekwencja
					 */
					/* value, to wartość o ile ma być zwiększona sekwencja */
					Long lInterval = (incremetValue > MSequenceObject.interval ? incremetValue
							: MSequenceObject.interval);
					Long endInterval = generateEndInterval(seqObj.seqClazz, lInterval);
					Long startInterval = endInterval - lInterval + 1;

					if (!seqObj.isInit) {
						seqObj.currValue = startInterval;
						seqObj.isInit = true;
					} else {
						seqObj.currValue = newId;
					}
					seqObj.buffValue = endInterval;
					if (logger.isTraceEnabled()) {
						logger.trace("[{}]--> createNewId[AFTER from DB]: currValue={}, buffValue={}, lInterval={}, ",
								seqObj.seqClazz.getSimpleName(), seqObj.currValue, seqObj.buffValue, lInterval);
					}
				} else {
					seqObj.currValue = newId;
					if (logger.isTraceEnabled()) {
						logger.trace("[{}]--> createNewId[AFTER from Cache]: currValue={}, buffValue={}",
								seqObj.seqClazz.getSimpleName(), seqObj.currValue, seqObj.buffValue);
					}
				}
				return seqObj.currValue;
			}
		} catch (Exception e) {
			throw new UnsupportedMethodException(e);
		}
	}

	/**
	 * Implementacja metody pobierającej dane sekwencji właściwej oraz utrwalająca
	 * dane sekwencji w bazie danych.
	 * 
	 * @param seqClazz
	 *            klasa sekwencji
	 * @param lInterval
	 *            interwał, o ile ma być zwiększona sekwencja w bazie danych
	 * @return wartość sekwencji w bazie danych.
	 * @throws InstantiationException
	 * @throws IllegalAccessException
	 */
	protected abstract Long generateEndInterval(Class<?> seqClazz, Long lInterval)
			throws InstantiationException, IllegalAccessException;

	/**
	 * 
	 * MSequenceObject obiekt sekwencji przechowywany w pamięci podręcznej serwera.
	 *
	 * @author Sławomir Cichy &lt;slawas@scisoftware.pl&gt;
	 * @version $Revision: 1.1 $
	 *
	 */
	private static class MSequenceObject implements Serializable {

		private static final long serialVersionUID = -3518892384824957538L;

		/** wartość inicjalna obiektu sekwencji */
		public static final Long initValue = -1L;
		/** wartość inicjalna inkrementacji sekwencji w bazie danych SQL */
		public static final Long interval = Long.parseLong(defaultIncremetValue);
		/** klasa sekwencji, której reprezentacją jest dana instancja obiektu */
		private final Class<?> seqClazz;
		/** blokada na akcję inkrementacji */
		private final SerializableObject lock = new SerializableObject();
		/** obecna wartość sekwencji */
		private long currValue = initValue;
		/** buforowana wartość sekwencji */
		private long buffValue = initValue;
		/** czy sekwencja została zainicjalizowana? */
		private boolean isInit = false;

		public MSequenceObject(String seqClazzName) throws ClassNotFoundException {
			super();
			this.seqClazz = Class.forName(seqClazzName);
		}

	}

	/**
	 * Utworzenie nazwy klasy sekwencji na podstawie nazwy klasy encji
	 * 
	 * @param persistentClass
	 *            klasa encji
	 * @return nazwa klasy sekwencji
	 */
	public static String createSequenceClassName(Class<? extends MEntity> persistentClass) {
		return persistentClass.getName() + SEQUENCE_NAME_SUFFIX;
	}

}
