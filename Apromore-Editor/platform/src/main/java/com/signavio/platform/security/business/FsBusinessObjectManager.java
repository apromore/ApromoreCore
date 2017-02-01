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

import com.signavio.platform.account.business.FsAccountManager;
import com.signavio.platform.tenant.business.FsTenant;
import com.signavio.platform.tenant.business.FsTenantManager;
import com.signavio.usermanagement.business.FsRoleManager;
import com.signavio.warehouse.business.FsEntityManager;




/**
 * Abstract Implementation of an Object Manager for file system accessing Oryx.
 * 
 * @author Stefan Krumnow
 *
 */
public abstract class FsBusinessObjectManager extends FsSecureBusinessObject {
	
	
	@SuppressWarnings("unchecked")
	public static <T extends FsBusinessObjectManager> T getGlobalManagerInstance(Class<T> managerClass, FsAccessToken token) {		
		if (FsAccountManager.class.isAssignableFrom(managerClass)) {
			return (T)FsAccountManager.getSingleton();
		} else if (FsTenantManager.class.isAssignableFrom(managerClass)) {
			return (T)FsTenantManager.getSingleton();
		} else if (FsRoleManager.class.isAssignableFrom(managerClass)){
			return (T)FsRoleManager.getSingleton();
		} else if (FsEntityManager.class.isAssignableFrom(managerClass)){
			return (T)FsEntityManager.getSingleton();
		}
		return null;
	}
	
	public static <T extends FsBusinessObjectManager> T getTenantManagerInstance(Class<T> managerClass, FsTenant tenant, FsAccessToken token) {		
		return getGlobalManagerInstance(managerClass, token);
	}
	
	public static <T extends FsBusinessObjectManager> T getTenantManagerInstance(Class<T> managerClass, String tenantId, FsAccessToken token) {			
		return getGlobalManagerInstance(managerClass, token);
	}

}
