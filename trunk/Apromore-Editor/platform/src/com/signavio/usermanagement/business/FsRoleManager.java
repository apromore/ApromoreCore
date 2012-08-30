/**
 * Copyright (c) 2009, Signavio GmbH
 * 
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 * 
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
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
