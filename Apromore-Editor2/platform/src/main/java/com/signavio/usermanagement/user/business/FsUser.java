/*
 * Copyright © 2009-2015 The Apromore Initiative.
 *
 * This file is part of "Apromore".
 *
 * "Apromore" is free software; you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 3 of the
 * License, or (at your option) any later version.
 *
 * "Apromore" is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this program.
 * If not, see <http://www.gnu.org/licenses/lgpl-3.0.html>.
 */
package com.signavio.usermanagement.user.business;

import java.util.HashSet;
import java.util.Set;

import com.signavio.platform.account.business.FsAccount;
import com.signavio.platform.security.business.FsSecureBusinessObject;
import com.signavio.platform.security.business.FsSecureBusinessSubject;
import com.signavio.usermanagement.business.FsRoleManager;

/**
 * Dummy implementation of a user in the file accessing Oryx backend.
 * 
 * @author Stefan Krumnow
 *
 */
public class FsUser extends FsSecureBusinessSubject{
	
	private static final FsUser DUMMY;
	public static final String ID_OF_DUMMY = "user-object";
	private static final Set<FsUser> DUMMY_SET;

	private static final Set<FsSecureBusinessObject> PARENTS;
	
	static {
		DUMMY = new FsUser();
		DUMMY_SET = new HashSet<FsUser>(1);
		DUMMY_SET.add(DUMMY);
		PARENTS = new HashSet<FsSecureBusinessObject>(2);
		PARENTS.add(FsRoleManager.getSingleton());
	}
	
	public static FsUser getDummy() {
		return DUMMY;
	}
	public static Set<FsUser> getDummySet() {
		return DUMMY_SET;
	}
	
	
	
	
	public FsUser() {
		
	}


	public String getFullName(){ return emptyString; }
	
	public void setFirstLogin(boolean b) { return ;	}

	public FsAccount getAccount() {
		return FsAccount.getDummy();
	}


	public void setRole(String string) { return ; }
	
	public Set<? extends FsSecureBusinessObject> getParents() {
		return PARENTS;
	}

	public boolean isFirstLogin() { return false; }
	
	@Override
	public String getId() {
		return ID_OF_DUMMY;
	}
	public boolean isFirstStart() { return false; }
	public void setFirstStart(boolean b) { return ; }


}
