package org.apromore.processmining.models.cast;

public class Cast {

	private Cast() {

	}

	/**
	 * Casts the given object to type T. This static method can be used to avoid
	 * "unchecked cast" warnings. Note that a runtime exception is still thrown
	 * if the cast is not valid. However, using this method eliminates the use
	 * of the @@SupressWarnings annotation, which obfuscates any valid warnings.
	 * Note that this method should be used in combination with assertions to
	 * assert the right type!
	 * 
	 * @param <T>
	 *            The type to cast to.
	 * @param x
	 *            the object to cast
	 * @return (T) x;
	 */
	@SuppressWarnings("unchecked")
	public static <T> T cast(Object x) {
		return (T) x;
	}
}