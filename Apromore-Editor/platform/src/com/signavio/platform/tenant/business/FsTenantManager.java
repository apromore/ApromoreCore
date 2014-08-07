/*
 * Copyright © 2009-2014 The Apromore Initiative.
 *
 * This file is part of “Apromore”.
 *
 * “Apromore” is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 3 of the License, or
 * (at your option) any later version.
 *
 * “Apromore” is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.
 * If not, see <http://www.gnu.org/licenses/lgpl-3.0.html>.
 */
package com.signavio.platform.tenant.business;

import java.util.Set;

import com.signavio.platform.account.business.FsAccount;
import com.signavio.platform.exceptions.TenantException;
import com.signavio.platform.security.business.FsBusinessObjectManager;
import com.signavio.platform.security.business.FsSecureBusinessObject;

/**
 * Implementation of a tenant manager in the file accessing Oryx backend.
 * 
 * @author Stefan Krumnow
 *
 */
public class FsTenantManager extends FsBusinessObjectManager {
	
	private static final FsTenantManager SINGLETON;
	public static final String ID_OF_SINGLETON = "tenant-mgr-object";
	static {
		SINGLETON = new FsTenantManager();
	}

	public static FsTenantManager getSingleton() {
		return SINGLETON;
	}

	public FsTenant createTenant(FsAccount account, String string, String id) throws TenantException {
		throw new UnsupportedOperationException("This tenant manager does not support the creation of new tenants.");
	}

	@Override
	@SuppressWarnings("unchecked")
	public <T extends FsSecureBusinessObject> Set<T> getChildren(Class<T> type) {
		if (FsTenant.class.isAssignableFrom(type)){
			return (Set<T>) FsTenant.getSingletonSet();
		} else {
			return super.getChildren(type);
		}
	}
	
	@Override
	public String getId() {
		return ID_OF_SINGLETON;
	}

}
