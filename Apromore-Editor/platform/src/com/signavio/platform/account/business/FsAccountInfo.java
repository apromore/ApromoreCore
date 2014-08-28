/*
 * Copyright © 2009-2014 The Apromore Initiative.
 *
 * This file is part of "Apromore".
 *
 * "Apromore" is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 3 of the License, or
 * (at your option) any later version.
 *
 * "Apromore" is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.
 * If not, see <http://www.gnu.org/licenses/lgpl-3.0.html>.
 */
package com.signavio.platform.account.business;


public class FsAccountInfo {
	
	private static final String emptyString = "";
	private static final FsAccountInfo DUMMY;

	static {
		DUMMY = new FsAccountInfo();
	}
	
	public static FsAccountInfo getDummy() {
		return DUMMY;
	}
	
	public FsAccountInfo() {
		// empty
	}

	public String getLanguageCode() { return emptyString; }
	public String getCountryCode() { return emptyString; }
	
}
