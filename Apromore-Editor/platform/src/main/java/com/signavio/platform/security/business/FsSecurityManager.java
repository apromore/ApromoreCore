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

import java.io.File;
import java.net.URLDecoder;

import com.signavio.platform.account.business.FsAccount;
import com.signavio.platform.account.business.FsAccountManager;
import com.signavio.platform.exceptions.AccountInActiveException;
import com.signavio.platform.exceptions.IncorrectPasswordException;
import com.signavio.platform.exceptions.PrincipalException;
import com.signavio.platform.exceptions.TenantException;
import com.signavio.platform.exceptions.TenantInActiveException;
import com.signavio.platform.security.business.exceptions.BusinessObjectDoesNotExistException;
import com.signavio.platform.tenant.business.FsTenant;
import com.signavio.platform.tenant.business.FsTenantManager;
import com.signavio.usermanagement.business.FsRoleManager;
import com.signavio.usermanagement.user.business.FsUser;
import com.signavio.warehouse.business.FsEntityManager;
import com.signavio.warehouse.directory.business.FsDirectory;
import com.signavio.warehouse.directory.business.FsRootDirectory;
import com.signavio.warehouse.model.business.FsModel;
import com.signavio.warehouse.revision.business.FsComment;
import com.signavio.warehouse.revision.business.FsModelRevision;



/**
 * Security Manager implementation for file system accessing Oryx..
 * 
 * @author Stefan Krumnow
 */
public class FsSecurityManager {

	private static final FsSecurityManager INSTANCE;
	
//	private final Logger logger = Logger.getLogger(SecurityManager.class);	

	/**
	 * Creating singleton
	 */
	static {
		INSTANCE = new FsSecurityManager();
	}

	public static FsSecurityManager getInstance() {
		return INSTANCE;
	}

	/**
	 * PRIVATE CONSTRUCTOR
	 */
	private FsSecurityManager() {
		// empty
	}
	
	public static  String hashPassword(String plainPassword) {
		return plainPassword;
	}
	
	public static FsAccessToken createToken(String principal, String password, String tenantPrincipal) throws PrincipalException, IncorrectPasswordException, TenantException, TenantInActiveException, AccountInActiveException {
		return FsAccessToken.getDummy(); 
	}
	
	
	@SuppressWarnings("unchecked")
	public <T extends FsSecureBusinessObject> T createObject(Class<T> businessObjectClass, FsSecureBusinessObject parentObject, FsAccessToken token, Object ... parameters){
		
		if (FsDirectory.class.isAssignableFrom(businessObjectClass)) {
			assert (parentObject instanceof FsDirectory) ;
			FsDirectory parent = (FsDirectory)parentObject;
			if (parameters.length > 0 && parameters[0] instanceof String){
				return (T)parent.createDirectory((String)parameters[0], "");
			}
			throw new IllegalArgumentException("Could not create Directory");
		// ISSUE : necessary ?		
		// } else if (Model.class.equals(businessObjectClass)) {
		//	
		} else if (FsBusinessObjectManager.class.isAssignableFrom(businessObjectClass)) {
			return (T)FsBusinessObjectManager.getGlobalManagerInstance((Class<FsBusinessObjectManager>)businessObjectClass, token);

		} else if (FsUser.class.isAssignableFrom(businessObjectClass)) {
			throw new UnsupportedOperationException("Creation of users is not supported");
		} else {
			throw new UnsupportedOperationException("Creation of this type is not supported");
		}
		
	}
	
	@SuppressWarnings("unchecked")
	public <T extends FsSecureBusinessObject> T loadObject(Class<T> businessObjectClass, String id, FsAccessToken token) {
		
		if (id.length() == 0){
			return null;
			
		} else if (FsModel.class.isAssignableFrom(businessObjectClass) ){
			String id2 = id.replace(";", File.separator);
			if (id2.startsWith(FsRootDirectory.ID_OF_SINGLETON)) {
				id2 = FsRootDirectory.getSingleton().getPath() + id2.substring(FsRootDirectory.ID_OF_SINGLETON.length());
			}
			try {
				return (T)(new FsModel(id2));
			} catch (Exception e) {
				try {
					return (T)(new FsModel(URLDecoder.decode(id2, "utf8").replace(";", File.separator)));
				} catch (Exception e2) {
					throw new BusinessObjectDoesNotExistException(id);
				}
			}
			
		} else if (FsDirectory.class.isAssignableFrom(businessObjectClass)){
			 if (id.equals(FsRootDirectory.ID_OF_SINGLETON)) {
				return (T)FsRootDirectory.getSingleton();
			} else {
				String id2 = id.replace(";", File.separator);
				if (id2.startsWith(FsRootDirectory.ID_OF_SINGLETON)) {
					id2 = FsRootDirectory.getSingleton().getPath() + id2.substring(FsRootDirectory.ID_OF_SINGLETON.length());
				}
				try {
					return (T)(new FsDirectory(id2));
				} catch (Exception e) {
					try {
						return (T)(new FsDirectory(URLDecoder.decode(id2).replace(";", File.separator)));
					} catch (Exception e2) {
						throw new BusinessObjectDoesNotExistException(id);
					}
				}
			}
			 
		} else {
			return (T)loadObject(id, token);
		}
	}
	
	public FsSecureBusinessObject loadObject(String id, FsAccessToken token) {
		if (id.length() == 0){
			return null;
		} else if (id.equals(FsAccount.ID_OF_DUMMY)){
			return FsAccount.getDummy();
		} else if (id.equals(FsAccountManager.ID_OF_SINGLETON)){
			return FsAccountManager.getSingleton();
		} else if (id.equals(FsEntityManager.ID_OF_SINGLETON)){
			return FsEntityManager.getSingleton();
		} else if (id.equals(FsRoleManager.ID_OF_SINGLETON)){
			return FsRoleManager.getSingleton();
		} else if (id.equals(FsTenantManager.ID_OF_SINGLETON)){
			return FsTenantManager.getSingleton();
		} else if (id.equals(FsRootObject.ID_OF_SINGLETON)){
			return FsRootObject.getRootObject(token);
		} else if (id.equals(FsUser.ID_OF_DUMMY)){
			return FsUser.getDummy();
		} else if (id.equals(FsTenant.ID_OF_SINGLETON)){
			return FsTenant.getSingleton();
		} else if (id.equals(FsRootDirectory.ID_OF_SINGLETON)) {
			return FsRootDirectory.getSingleton();
		} else if (id.startsWith(FsComment.ID_PREFIX)){
			String modelId = id.substring(FsModelRevision.ID_PREFIX.length() + FsComment.ID_PREFIX.length());
			try {
				FsModel foundModel = loadObject(FsModel.class, modelId, token);
				return foundModel.getHeadRevision().getCommentObj();
			} catch (Exception e) {
				throw new BusinessObjectDoesNotExistException(id);
			}
		} else if (id.startsWith(FsModelRevision.ID_PREFIX)){
			String modelId = id.substring(FsModelRevision.ID_PREFIX.length());
			try {
				FsModel foundModel = loadObject(FsModel.class, modelId, token);
				return foundModel.getHeadRevision();
			} catch (Exception e) {
				throw new BusinessObjectDoesNotExistException(id);
			}
		
		} else {
			// Model..
			try {
				return loadObject(FsModel.class, id, token);
			} catch (Exception e) { 
				// Directory..
				try {
					return loadObject(FsDirectory.class, id, token);
				} catch (Exception e2) {
					// Invitation
					throw new BusinessObjectDoesNotExistException(id);
				}
			}
		}
	}
	
	public void deleteObject(FsSecureBusinessObject businessObject, FsAccessToken token) {
		if (businessObject instanceof FsDirectory) {
			((FsDirectory)businessObject).delete();
		} else if (businessObject instanceof FsModel) {
			((FsModel)businessObject).delete();
		} else {
			throw new UnsupportedOperationException("Cannot delete object of type " + businessObject.getClass());
		}
	}

	public FsAccessToken getRootToken() {
		return FsAccessToken.getDummy();
	}

	public FsAccount verifyCredentials(String principal, String password) {
		return FsAccount.getDummy();
	}
	
	@SuppressWarnings("unchecked")
	public <T extends FsSecureBusinessObject> T loadGlobalSingletonObject(Class<T> businessClass, FsAccessToken token) {
		if (FsBusinessObjectManager.class.isAssignableFrom(businessClass)) {
			return (T)FsBusinessObjectManager.getGlobalManagerInstance((Class<? extends FsBusinessObjectManager>)businessClass, token);
		}
		throw new IllegalArgumentException("Could not load global singleton of " + businessClass.getCanonicalName());
	}
	@SuppressWarnings("unchecked")
	public <T extends FsSecureBusinessObject> T loadTenantSingletonObject(Class<T> businessClass, FsTenant tenant, FsAccessToken token) {
		if (FsBusinessObjectManager.class.isAssignableFrom(businessClass)) {
			return (T)FsBusinessObjectManager.getTenantManagerInstance((Class<? extends FsBusinessObjectManager>)businessClass, tenant, token);
		}
		throw new IllegalArgumentException("Could not load tenant singleton of " + businessClass.getCanonicalName());
	}
	@SuppressWarnings("unchecked")
	public <T extends FsSecureBusinessObject> T loadTenantSingletonObject(Class<T> businessClass, String tenantId, FsAccessToken token) {
		if (FsBusinessObjectManager.class.isAssignableFrom(businessClass)) {
			return (T)FsBusinessObjectManager.getTenantManagerInstance((Class<? extends FsBusinessObjectManager>)businessClass, tenantId, token);
		}
		throw new IllegalArgumentException("Could not load tenant singleton of " + businessClass.getCanonicalName());
	}		
}
	
