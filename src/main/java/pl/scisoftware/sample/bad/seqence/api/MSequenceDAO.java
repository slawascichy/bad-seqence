package pl.scisoftware.sample.bad.seqence.api;

import java.io.Serializable;

/**
 * 
 * MSequenceDAO
 *
 * @author Sławomir Cichy &lt;slawomir.cichy@ibpm.pro&gt;
 * @version $Revision: 1.1 $
 *
 */
public interface MSequenceDAO extends Serializable {

	/**
	 * Inkrementacja sekwencji o domyślną wartość
	 * 
	 * @param entityClass
	 *            klasa encji na podstawie której zostanie utworzona nazwa sekwencji
	 * @return
	 * @throws MercuryException
	 */
	Object createNewId(Class<? extends MEntity> entityClass) throws MercuryException;

	/**
	 * Inkremetacja sekwencji o zadaną wartość
	 * 
	 * @param entityClass
	 *            klasa encji na podstawie której zostanie utworzona nazwa sekwencji
	 * @param value
	 *            wartość o jaką ma być zwiększona sekwencja
	 * @return
	 * @throws MercuryException
	 */
	Object incremetSequenceId(Class<? extends MEntity> entityClass, Long value) throws MercuryException;

	/**
	 * Inkremetacja sekwencji o zadaną wartość
	 * 
	 * @param seqClazzName
	 *            nazwa klasy sekwencji (&lt;?>.class.getName())
	 * @param value
	 *            wartość o jaką ma być zwiększona sekwencja
	 * @return
	 * @throws MercuryException
	 */
	Object incremetSequenceId(String seqClazzName, Long value) throws MercuryException;

}