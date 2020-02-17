package org.scam.shared.utils;

import java.util.HashMap;
import java.util.Map;

public class LockGeneratorUtils {

	private static final Map<String, Lock> LOCKS_MAP = new HashMap<>();
	private static final Object GET_LOCK_LOCK = new Object();

	private LockGeneratorUtils() {
	}

	public static Lock checkLockExists(Object source, String sourceMethodName, String uniqueValue) {
		return checkLockExistsByClass(source.getClass(), sourceMethodName, uniqueValue);
	}

	public static Lock checkLockExistsByClass(Class<?> sourceClass, String sourceMethodName, String uniqueValue) {
		StringBuilder sb = buildLockName(sourceClass, sourceMethodName, uniqueValue);
		String lockKey = sb.toString();
		return LOCKS_MAP.get(lockKey);
	}

	/**
	 * Generowanie obiektu blokady do synchronizacji
	 * 
	 * @param sourceClass
	 *            klasa, dla której potrzebujemy wygenerować blokadę
	 * @param sourceMethodName
	 *            nazwa metody, dla której ma być wygenerowana blokada
	 * @param uniqueValue
	 *            unikalna wartość, dla której ma być wygenerowana blokada
	 * @return obiekt blokady
	 */
	public static Object generateLockByClass(Class<?> sourceClass, String sourceMethodName, String uniqueValue) {
		synchronized (GET_LOCK_LOCK) {
			StringBuilder sb = buildLockName(sourceClass, sourceMethodName, uniqueValue);
			String lockKey = sb.toString();
			Lock currentLock = LOCKS_MAP.get(lockKey);
			if (currentLock == null) {
				currentLock = new Lock(lockKey);
				LOCKS_MAP.put(lockKey, currentLock);
			} else {
				currentLock.upThreadCounter();
			}
			return currentLock.getLockObject();
		}
	}

	private static StringBuilder buildLockName(Class<?> sourceClass, String sourceMethodName, String uniqueValue) {
		StringBuilder sb = new StringBuilder(sourceClass.getName());
		sb.append('.').append(sourceMethodName).append('.').append(uniqueValue);
		return sb;
	}

	/**
	 * Generowanie obiektu blokady do synchronizacji
	 * 
	 * @param source
	 *            instancja obiektu, dla którego ma być wygenerowana blokada.
	 * @param sourceMethodName
	 *            nazwa metody, dla której ma być wygenerowana blokada
	 * @param uniqueValue
	 *            unikalna wartość, dla której ma być wygenerowana blokada
	 * @return obiekt blokady
	 */
	public static Object generateLock(Object source, String sourceMethodName, String uniqueValue) {
		return generateLockByClass(source.getClass(), sourceMethodName, uniqueValue);
	}

	/**
	 * Usuwanie obiektu blokady do synchronizacji
	 * 
	 * @param sourceClass
	 *            klasa, dla której potrzebujemy wygenerować blokadę
	 * @param sourceMethodName
	 *            nazwa metody, dla której ma być wygenerowana blokada
	 * @param uniqueValue
	 *            unikalna wartość, dla której ma być wygenerowana blokada
	 */
	public static void removeLockByClass(Class<?> sourceClass, String sourceMethodName, String uniqueValue) {
		synchronized (GET_LOCK_LOCK) {
			StringBuilder sb = new StringBuilder(sourceClass.getName());
			sb.append('.').append(sourceMethodName).append('.').append(uniqueValue);
			String lockKey = sb.toString();
			Lock currentLock = LOCKS_MAP.get(lockKey);
			if (currentLock != null) {
				int status = currentLock.downThreadCounter();
				if (status < 1) {
					LOCKS_MAP.remove(lockKey);
				}
			}
		}
	}

	/**
	 * Usuwanie obiektu blokady do synchronizacji
	 * 
	 * @param source
	 *            instancja obiektu, dla którego ma być wygenerowana blokada.
	 * @param sourceMethodName
	 *            nazwa metody, dla której ma być wygenerowana blokada
	 * @param uniqueValue
	 *            unikalna wartość, dla której ma być wygenerowana blokada
	 */
	public static void removeLock(Object source, String sourceMethodName, String uniqueValue) {
		removeLockByClass(source.getClass(), sourceMethodName, uniqueValue);
	}

	/**
	 * 
	 * Lock - klasa reprezentująca blokadę
	 *
	 * @author Sławomir Cichy &lt;slawomir.cichy@ibpm.pro&gt;
	 * @version $Revision: 1.1 $
	 *
	 */
	public static class Lock {

		/** nazwa blokady */
		private final String lockName;

		/** Obiekt blokady (będzie wykorzystywany w {@code synchronize} */
		private final Object lockObject = new Object();
		/**
		 * licznik wątków, które wykorzystują blokadę - w celu weryfikacji możliwości
		 * usunięcia obiektu blokady z mapy blokad.
		 */
		private int threadCounter = 1;

		private Lock(String lockName) {
			super();
			this.lockName = lockName;
		}

		private void upThreadCounter() {
			threadCounter++;
		}

		private int downThreadCounter() {
			threadCounter--;
			return threadCounter;
		}

		/**
		 * @return the {@link #lockObject}
		 */
		public Object getLockObject() {
			return lockObject;
		}

		/**
		 * @return the {@link #lockName}
		 */
		public String getLockName() {
			return lockName;
		}

		/**
		 * @return the {@link #threadCounter}
		 */
		public int getThreadCounter() {
			return threadCounter;
		}
	}
}
