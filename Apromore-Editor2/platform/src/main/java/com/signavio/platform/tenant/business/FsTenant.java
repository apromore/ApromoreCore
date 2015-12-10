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
package com.signavio.platform.tenant.business;

import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import com.signavio.platform.account.business.FsAccount;
import com.signavio.platform.exceptions.TenantException;
import com.signavio.platform.security.business.FsSecureBusinessObject;
import com.signavio.warehouse.business.FsEntityManager;
import com.signavio.warehouse.directory.business.FsRootDirectory;


/**
 * Dummy implementation of a tenant in the file accessing Oryx backend.
 * 
 * @author Stefan Krumnow
 *
 */
public class FsTenant extends FsSecureBusinessObject {
	
	public static final String STANDARD_TYPE = "standard";
	
//	private License license;
	private static final Date goodThru =  new Date(Long.MAX_VALUE) ;
	
	private static final FsTenant SINGLETON;
	public static final String ID_OF_SINGLETON = "tenant-object";
	private static final Set<FsTenant> SINGLETON_IN_SET;

	static {
		SINGLETON = new FsTenant();
		SINGLETON_IN_SET = new HashSet<FsTenant>(1);
		SINGLETON_IN_SET.add(SINGLETON);
	}
	
	public static FsTenant getSingleton() {
		return SINGLETON;
	}
	public static Set<FsTenant> getSingletonSet() {
		return SINGLETON_IN_SET;
	}
	
	public FsTenant() {
		// // empty - for now..
	}
	
	
	public FsAccount getOwner() { return FsAccount.getDummy(); }
	public FsRootDirectory getRootDirectory() { return FsRootDirectory.getSingleton(); }
	
	public int getStandardUserCount() { return 1; }
	public int getTrialUserCount() { return 0; }
	public Date getGoodThru() { return goodThru; }
	public String getType() { return STANDARD_TYPE;	}
	
	public String getName(){ return emptyString; }
	public String getDescription() { return emptyString; }
	public String getPrincipal() { return emptyString; }
	public String getCompanyName() { return emptyString; }
	public String getFirstName() { return emptyString; }
	public String getLastName() { return emptyString; }
	public String getStreet() { return emptyString; }
	public String getStreet2() { return emptyString; }
	public String getCity() { return emptyString; }
	public String getZipcode() { return emptyString; }
	public String getState() { return emptyString; }
	public String getCountry() { return emptyString; }
	public String getPhone() { return emptyString; }	
	public String getMail() { return emptyString; }
	public String getFax() { return emptyString; }
	public String getPaymentMethod() { return emptyString; }
	public String getCreditCard() { return emptyString; }
	public String getCardNumber() { return emptyString; }
	public String getCvcNumber() { return emptyString; }
	public String getCardGoodThru() { return emptyString; }
	public String getAccountName() { return emptyString; }
	public String getAccountNo() { return emptyString; }
	public String getBankName() { return emptyString; }
	public String getBankNumber() { return emptyString; }
	public String getAddress() { return emptyString; }
	public boolean isActive() {  return true; }
	
	
	public void setActive(boolean b) { return ; }	
	public void setMail(String s) { return ; }
	public void setName(String s) throws TenantException { return ; }
	public void setDescription(String s) { return ; }
	public void setAddress(String ...strings) { return ; }
	public void payByInvoice() { return ;}
	public void payByCreditCard(String creditCard, String cardNumber, String cvcNumber, String goodThru) { return ; }
	public void payByDirectDebit(String accountName, String accountNo, String bankName, String bankNumber) { return ; }
	public void setType(String type) { return ;}


	
	@Override
	@SuppressWarnings("unchecked")
	public <T extends FsSecureBusinessObject> Set<T> getChildren(Class<T> type) {
		if (FsEntityManager.class.isAssignableFrom(type)){
			return (Set<T>)FsEntityManager.getSingletonSet();
		} else {
			return super.getChildren(type);
		}
	}
	
	@Override
	public String getId() {
		return ID_OF_SINGLETON;
	}



}
