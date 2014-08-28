/*
 * Copyright © 2009-2014 The Apromore Initiative.
 *
 * This file is part of "Apromore".
 *
 * "Apromore" is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 3 of the License, or
 * (at your option) any later version.
 *
 * "Apromore" is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.
 * If not, see <http://www.gnu.org/licenses/lgpl-3.0.html>.
 */
package com.signavio.platform.security.business;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.signavio.platform.tenant.business.FsTenant;

public abstract class FsSecureBusinessObject {

	protected static final String emptyString = "";
	
	@SuppressWarnings("unchecked")
	protected static final Set emptySet = new HashSet();
	@SuppressWarnings("unchecked")
	protected static final List emptyList = new ArrayList();
	
	private boolean privilegeInheritanceBlocked = false;
	private boolean deleted = false;
	
	public FsSecureBusinessObject(){
		// empty - for now
	}
	
	public abstract String getId();

	
	public FsTenant getTenant(){
		return FsTenant.getSingleton();
	}
	
	public FsAccessToken getAccessToken(){
		return FsAccessToken.getDummy();
	}
	

	public void	setDeleted(boolean bool){
		deleted = bool;
	}

	public boolean isDeleted(){
		return deleted;
	}
	
	public void	setPrivilegeInheritanceBlocked(boolean bool){
		privilegeInheritanceBlocked = bool;
	}
	
	public boolean isPrivilegeInheritanceBlocked(){
		return privilegeInheritanceBlocked;
	}
	
	@SuppressWarnings("unchecked")
	public Set<String> getGainedPrivileges(FsSecureBusinessObject object) { 
		// ISSUE: Would full set be better?
		return emptySet; 
	}

	// ISSUE: Uncomment these methods in order to get to know needed implementations..
	public <T extends FsSecureBusinessObject> Set<T> getChildren(Class<T> type){
		throw new UnsupportedOperationException("Not supported by this sub-type of SecureBusinessObject");
	}
	public void addChild(FsSecureBusinessObject Child){
		throw new UnsupportedOperationException("Not supported by this sub-type of SecureBusinessObject");
	}
	public <T extends FsSecureBusinessObject> Set<T> getParents(Class<T> businessObjectClass) {
		throw new UnsupportedOperationException("Not supported by this sub-type of SecureBusinessObject");
	}
	public <T extends FsSecureBusinessObject> Set<T> removeChild(T Child){
		throw new UnsupportedOperationException("Not supported by this sub-type of SecureBusinessObject");
	}
	
}
