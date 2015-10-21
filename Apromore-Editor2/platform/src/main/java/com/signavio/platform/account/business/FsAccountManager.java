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

import java.util.Set;

import com.signavio.platform.security.business.FsBusinessObjectManager;
import com.signavio.platform.security.business.FsSecureBusinessObject;
import com.signavio.platform.tenant.business.FsTenant;

/**
 * Implementation of an Account Manager for file system accessing Oryx.
 * 
 * @author Stefan Krumnow
 *
 */
public class FsAccountManager extends FsBusinessObjectManager {
	
	private static final FsAccountManager SINGLETON;
	public static final String ID_OF_SINGLETON = "account-mgr-object";
	
	static {
		SINGLETON = new FsAccountManager();
	}

	public static FsAccountManager getSingleton() {
		return SINGLETON;
	}


	public FsAccount createAccount(String mail, String password) {
		return FsAccount.getDummy();
	}

	public void sendValidationKey(FsAccount account, FsTenant tenant,
			String string, String property, String property2) {
		return ;
	}

	@Override
	@SuppressWarnings("unchecked")
	public <T extends FsSecureBusinessObject> Set<T> getChildren(Class<T> type) {
		if (FsAccount.class.isAssignableFrom(type)){
			return (Set<T>)FsAccount.getDummySet();
		} else {
			return super.getChildren(type);
		}
	}
	
	@Override
	public String getId() {
		return ID_OF_SINGLETON;
	}
}
