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
