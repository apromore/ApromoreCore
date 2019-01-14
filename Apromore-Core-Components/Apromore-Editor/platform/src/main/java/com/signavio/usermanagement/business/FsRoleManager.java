/*
 * Copyright © 2009-2018 The Apromore Initiative.
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
package com.signavio.usermanagement.business;

import java.util.HashSet;
import java.util.Set;

import com.signavio.platform.account.business.FsAccount;
import com.signavio.platform.exceptions.PrincipalException;
import com.signavio.platform.security.business.FsBusinessObjectManager;
import com.signavio.platform.security.business.FsSecureBusinessObject;
import com.signavio.usermanagement.user.business.FsUser;

/**
 * Implementation of a role manager in the file accessing Oryx backend.
 * 
 * @author Stefan Krumnow
 *
 */
public class FsRoleManager extends FsBusinessObjectManager {
	
	private static final FsRoleManager SINGLETON;
	public static final String ID_OF_SINGLETON = "role-mgr-object";
	private static final Set<FsRoleManager> SINGLETON_IN_SET;
	

	static {
		SINGLETON = new FsRoleManager();
		SINGLETON_IN_SET = new HashSet<FsRoleManager>(1);
		SINGLETON_IN_SET.add(SINGLETON);
	}
	
	public static FsRoleManager getSingleton() {
		return SINGLETON;
	}
	public static Set<FsRoleManager> getSingletonSet() {
		return SINGLETON_IN_SET;
	}

	public void deleteUser(FsUser user) throws UserManagementException { return ; }

	public FsUser createUser(FsAccount owner) {
		throw new UnsupportedOperationException("Filesystem accessing backend cannot create users.");
		
	}

	public FsUser acceptInvitation(String invitationid, String tenantid, String mail, String password) throws PrincipalException, UserManagementException {
		return FsUser.getDummy();
	}
	
	@SuppressWarnings("unchecked")
	public <T extends FsSecureBusinessObject> Set<T> getChildren(Class<T> type) {
		if (FsUser.class.isAssignableFrom(type)) {
			return (Set<T>) FsUser.getDummySet();
		} else {
			return super.getChildren(type);
		}
	}
	
	@Override
	public String getId() {
		return ID_OF_SINGLETON;
	}
}
