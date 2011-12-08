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
package com.signavio.warehouse.business;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.signavio.platform.security.business.FsBusinessObjectManager;
import com.signavio.platform.security.business.FsSecureBusinessObject;
import com.signavio.platform.tenant.business.FsTenant;
import com.signavio.usermanagement.user.business.FsUser;
import com.signavio.warehouse.directory.business.FsDirectory;
import com.signavio.warehouse.directory.business.FsRootDirectory;

/**
 * Implementation of an Entity Manager for file system accessing Oryx.
 * 
 * @author Stefan Krumnow
 *
 */
public class FsEntityManager extends FsBusinessObjectManager {

	private static FsEntityManager SINGLETON;
	public static final String ID_OF_SINGLETON = "entity-mgr-object";
	private static final Set<FsEntityManager> SINGLETON_IN_SET;
	
	private FsTenant tenant;
	
	static {
		SINGLETON = new FsEntityManager();
		SINGLETON_IN_SET = new HashSet<FsEntityManager>(1);
		SINGLETON_IN_SET.add(SINGLETON);
	}
	
	public static FsEntityManager getSingleton() {
		return SINGLETON;
	}
	
	public static Set<FsEntityManager> getSingletonSet() {
		return SINGLETON_IN_SET;
	}
	
	public FsEntityManager() {
		tenant = FsTenant.getSingleton();
	}
	

	public FsDirectory getTenantRootDirectory() {
		return tenant.getRootDirectory();
	}

	public void createPrivateRootDirectory(FsUser user) {
		throw new UnsupportedOperationException("Filesystem accessing backend cannot create private directories.");
	}

	public void createTrashFolder(FsUser user) {
		throw new UnsupportedOperationException("Filesystem accessing backend cannot create trash directories.");
		
	}

	@SuppressWarnings("unchecked")
	public List<FsSecureBusinessObject> searchWarehouse(String searchTerm) {
		List<FsSecureBusinessObject> result = new ArrayList<FsSecureBusinessObject>();
		FsRootDirectory.getSingleton().search(searchTerm, result);
		return result;
	}

	@Override
	public String getId() {
		return ID_OF_SINGLETON;
	}
	
}
