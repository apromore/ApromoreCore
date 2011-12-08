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
package com.signavio.platform.test.fsbackend;

import junit.framework.Assert;

import org.junit.Test;

import com.signavio.platform.account.business.FsAccount;
import com.signavio.platform.account.business.FsAccountManager;
import com.signavio.platform.exceptions.AccountInActiveException;
import com.signavio.platform.exceptions.IncorrectPasswordException;
import com.signavio.platform.exceptions.PrincipalException;
import com.signavio.platform.exceptions.TenantException;
import com.signavio.platform.exceptions.TenantInActiveException;
import com.signavio.platform.security.business.FsAccessToken;
import com.signavio.platform.security.business.FsRootObject;
import com.signavio.platform.security.business.FsSecurityManager;
import com.signavio.platform.tenant.business.FsTenant;
import com.signavio.platform.tenant.business.FsTenantManager;
import com.signavio.usermanagement.business.FsRoleManager;
import com.signavio.usermanagement.user.business.FsUser;
import com.signavio.warehouse.business.FsEntityManager;
import com.signavio.warehouse.directory.business.FsDirectory;
import com.signavio.warehouse.model.business.FsModel;


public class FsAccessTest {
	
	
	@Test
	public void scriptTest() {


	}
	
	
//	@Test
	public void buildStructure() throws TenantInActiveException, AccountInActiveException, PrincipalException, IncorrectPasswordException, TenantException{
		
		// ROOT Level
		FsAccessToken token = FsSecurityManager.createToken("root", "root", null);
		FsRootObject root = FsRootObject.getRootObject(token);
		FsAccountManager accountManager = root.getAccountManager();
		FsTenantManager tenantManager = root.getTenantManager();
		
		Assert.assertEquals(accountManager.getChildren(FsAccount.class).size(), 1);
		Assert.assertEquals(tenantManager.getChildren(FsTenant.class).size(), 1);
		
		// Tenant Level
		FsTenant onlyTenant = tenantManager.getChildren(FsTenant.class).iterator().next();
		
		FsRoleManager roleManagerForTenant = FsRoleManager.getTenantManagerInstance(FsRoleManager.class, onlyTenant, token);
		FsEntityManager entityManagerForTenant = FsEntityManager.getTenantManagerInstance(FsEntityManager.class, onlyTenant, token);
		
		Assert.assertEquals(roleManagerForTenant.getChildren(FsUser.class).size(), 1);
		
		// Test Users and Groups..
		FsUser onlyUser = roleManagerForTenant.getChildren(FsUser.class).iterator().next();

		// Test Entities..
		FsDirectory d = entityManagerForTenant.getTenantRootDirectory();
		
		System.out.println("First Iteration:\n----------------");
		visitDir(d);
//		String uuidOfModel = "";
//		for (Directory c : d.getChildDirectories()) {
//			if (c.getName().equals("B")) {
//				Model m =c.createModel("NAME", "DESCRIPTION", "TYPE", "JSON", "SVG", "BLUB");
//				uuidOfModel = m.getId();
//				c.setName("BXX");
//			}
//		}
//		
//		Model loadedModel =  SecurityManager.getInstance().loadObject(Model.class, uuidOfModel, token);
//		loadedModel.setDescription("DESCRIPTION NEW");
//		loadedModel.setName("NAME X");
//		
//		System.out.println("\nSecond Iteration:\n----------------");
//		visitDir(d);
		
	}
	
	private void visitDir(FsDirectory d) {
		System.out.println("Children of "+ d.getName()+ " :");
		for (FsModel m : d.getChildModels()){
			System.out.println(" " + m.getName());
		}
		System.out.println("ChildDirectories of "+ d.getName()+ " :");
		for (FsDirectory c : d.getChildDirectories()) {
			System.out.println(" " + c.getName() + " - " + c.getDescription());
		}
		System.out.println("");
		for (FsDirectory c : d.getChildDirectories()) {
			visitDir(c);
		}
	}
}

