/*-
 * #%L
 * This file is part of "Apromore Core".
 * %%
 * Copyright (C) 2018 - 2022 Apromore Pty Ltd.
 * %%
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Lesser Public License for more details.
 * 
 * You should have received a copy of the GNU General Lesser Public
 * License along with this program.  If not, see
 * <http://www.gnu.org/licenses/lgpl-3.0.html>.
 * #L%
 */
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
