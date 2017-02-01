/*
 * Copyright © 2009-2017 The Apromore Initiative.
 *
 * This file is part of "Apromore".
 *
 * "Apromore" is free software; you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 3 of the
 * License, or (at your option) any later version.
 *
 * "Apromore" is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty
 * of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this program.
 * If not, see <http://www.gnu.org/licenses/lgpl-3.0.html>.
 */
package com.signavio.platform.security.business;

import java.io.Serializable;

import com.signavio.platform.account.business.FsAccount;
import com.signavio.platform.tenant.business.FsTenant;
import com.signavio.usermanagement.user.business.FsUser;

/**
 * Implementation of an Access Token for file system accessing Oryx.
 * 
 * @author Stefan Krumnow
 *
 */
public class FsAccessToken implements Serializable {

	private static final long serialVersionUID = -8581096924234627701L;
	private static final String emptyString = "";
	
	private static final FsAccessToken DUMMY;

	static {
		DUMMY = new FsAccessToken();
	}
	
	public static FsAccessToken getDummy() {
		return DUMMY;
	}
	
	
	public FsAccessToken() {
		super();
	}
	
	public FsAccessToken(String string, String id, Object object) {
		this();
	}


	public String getPrincipal() {
		return emptyString;
	}
	
	
	public String getTenantId() {
		return FsTenant.ID_OF_SINGLETON;
	}
	
	public String getUserId() {
		return FsUser.ID_OF_DUMMY;
	}
	
	public FsAccount getAccount() {
		return FsAccount.getDummy();
	}
	
	public FsUser getUser() {
		return FsUser.getDummy();
	}


	public boolean hasLoginLicense() {
		return true;
	}


}
