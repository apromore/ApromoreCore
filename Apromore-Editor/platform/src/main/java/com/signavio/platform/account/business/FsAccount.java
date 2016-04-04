/*
 * Copyright © 2009-2016 The Apromore Initiative.
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
