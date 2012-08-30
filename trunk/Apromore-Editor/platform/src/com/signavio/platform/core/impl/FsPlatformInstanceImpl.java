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
package com.signavio.platform.core.impl;

import java.io.File;

import javax.servlet.ServletContext;

import com.signavio.platform.account.business.FsAccountManager;
import com.signavio.platform.core.HandlerDirectory;
import com.signavio.platform.core.PlatformInstance;
import com.signavio.platform.core.PlatformProperties;
import com.signavio.platform.exceptions.InitializationException;
import com.signavio.platform.security.business.FsAccessToken;
import com.signavio.platform.security.business.FsRootObject;
import com.signavio.platform.security.business.FsSecurityManager;
import com.signavio.platform.tenant.business.FsTenant;
import com.signavio.platform.tenant.business.FsTenantManager;
import com.signavio.usermanagement.business.FsRoleManager;
import com.signavio.warehouse.business.FsEntityManager;
import com.signavio.warehouse.directory.business.FsDirectory;
import com.signavio.warehouse.directory.business.FsRootDirectory;
import com.signavio.warehouse.model.business.ModelTypeManager;

public class FsPlatformInstanceImpl implements PlatformInstance {

	private HandlerDirectory handlerManger;
	private ServletContext servletContext;
	private FsPlatformPropertiesImpl platformProperties;
	
	public void bootInstance(Object... parameters) {
		if (parameters.length < 1 || (parameters.length >= 1 && !(parameters[0] instanceof ServletContext))) {
			throw new InitializationException("Boot of servlet container PlatformInstance failed, because ServletContext parameter is missing.");
		}
		// load configuration
		this.servletContext = (ServletContext) parameters[0];

		this.platformProperties = new FsPlatformPropertiesImpl(servletContext);
		
		FsRootDirectory.createInstance(this.platformProperties.getRootDirectoryPath());
		ModelTypeManager.createInstance();
		
		this.handlerManger = new HandlerDirectory(servletContext);
		this.handlerManger.start();
	
		FsAccessToken token = null;
		try {
			token = FsSecurityManager.createToken("root", "root", null);
		} catch (Exception e) {
			// cannot happen
		}
		FsRootObject root = FsRootObject.getRootObject(token);
		@SuppressWarnings("unused")
		FsAccountManager accountManager = root.getAccountManager();
		FsTenantManager tenantManager = root.getTenantManager();
		
		FsTenant onlyTenant = tenantManager.getChildren(FsTenant.class).iterator().next();
		@SuppressWarnings("unused")
		FsRoleManager roleManagerForTenant = FsRoleManager.getTenantManagerInstance(FsRoleManager.class, onlyTenant, token);
		FsEntityManager entityManagerForTenant = FsEntityManager.getTenantManagerInstance(FsEntityManager.class, onlyTenant, token);
		@SuppressWarnings("unused")
		FsDirectory rootDir = entityManagerForTenant.getTenantRootDirectory();
		


	}
	
	public void shutdownInstance() {
		
	}

	public File getFile(String path) {
		return new File(servletContext.getRealPath(path));
	}

	public HandlerDirectory getHandlerDirectory() {
		return handlerManger;
	}

	public PlatformProperties getPlatformProperties() {
		return platformProperties;
	}


}
