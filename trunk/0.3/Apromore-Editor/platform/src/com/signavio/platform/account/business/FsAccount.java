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
package com.signavio.platform.account.business;

import java.util.HashSet;
import java.util.Set;

import com.signavio.platform.security.business.FsAccessToken;
import com.signavio.platform.security.business.FsSecureBusinessObject;
import com.signavio.platform.security.business.FsSecureBusinessSubject;
import com.signavio.platform.tenant.business.FsTenant;
import com.signavio.usermanagement.user.business.FsUser;


/**
 * Implementation of an Account.
 * 
 * @author Stefan Krumnow
 *
 */
public class FsAccount extends FsSecureBusinessObject {
	
	private static final FsAccount DUMMY;
	public static final String ID_OF_DUMMY = "account-object";
	private static final Set<FsAccount> DUMMY_SET;
	

	static {
		DUMMY = new FsAccount();
		DUMMY_SET = new HashSet<FsAccount>(1);
		DUMMY_SET.add(DUMMY);
	}
	
	public static FsAccount getDummy() {
		return DUMMY;
	}
	public static Set<FsAccount> getDummySet() {
		return DUMMY_SET;
	}
	
	public static FsAccount getAccountByPrincipal(String principal, FsAccessToken rootToken) {
		return getDummy();
	}
	
	public FsAccount() {
		
	}
	
	public String getFullName() { return emptyString; }
	public String getFirstName() { return emptyString; }
	public String getLastName() { return emptyString; }
	public String getTitle() { return emptyString; }
	public String getMailAddress() { return emptyString; }
	public String getPrincipal() { return emptyString; }
	public String getEmployees() { return emptyString; }
	public String getLanguage() { return emptyString; }
	public String getCompany()  { return emptyString; }
	public String getCountry()  { return emptyString; }
	public String getPhone()  { return emptyString; }
	public String getLanguageCode() { return emptyString; }
	public String getCountryCode() { return emptyString; }
	
	public boolean isActive() { return true; }
	public boolean isValidated() { return true; }
	public boolean isAcceptNewsletter() { return false; }
	
	public FsAccountInfo getAccountInfo() { return FsAccountInfo.getDummy(); }
	
	public FsSecureBusinessSubject getUserObject(FsTenant tenant) { return FsUser.getDummy(); }
	public Set<? extends FsSecureBusinessSubject> getUserObjects() {return FsUser.getDummySet();}
	
	public void setFirstName(String s) { return ; }
	public void setLastName(String s) { return ; }
	public void setTitle(String s) { return ; }
	public void setMailAddress(String s) { return ; }
	public void setPrincipal(String s) { return ; }
	public void setEmployees(String s) { return ; }
	public void setLanguage(String s) { return ; }
	public void changePassword(String s) { return ; }
	public void setJobtitle(String s) { return ; }
	public void setActive(boolean b) { return ; }
	public void setIsValidated(boolean b) { return ; }
	public void setAcceptNewsletter(boolean b) { return ; }
	public void setCountry(String trim)  { return ; }
	public void setCompany(String trim)  { return ; }
	public void setPhone(String trim) { return ; }
	
	public void sendResetPasswordMail() { return ; }
	public void resetPassword(String secureId, String password) { return ; }

	public boolean validate(String validationKey) { return true; }

	
	
	@Override
	@SuppressWarnings("unchecked")
	public <T extends FsSecureBusinessObject> Set<T> getChildren(Class<T> type) {
		if (FsUser.class.isAssignableFrom(type)){
			return (Set<T>)FsUser.getDummySet();
		} else {
			return super.getChildren(type);
		}
	}
	
	@Override
	public String getId() {
		return ID_OF_DUMMY;
	}
	public Set<FsTenant> getUserTenants() {
		return FsTenant.getSingletonSet();
	}

}
