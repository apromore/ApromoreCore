/*
 * Copyright © 2009-2018 The Apromore Initiative.
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
package com.signavio.warehouse.revision.business;

import java.util.Date;
import java.util.HashSet;
import java.util.Set;


import com.signavio.platform.security.business.FsSecureBusinessObject;
import com.signavio.usermanagement.user.business.FsUser;
import com.signavio.warehouse.model.business.FsModel;

/**
 * Implementation of a model revision in the file accessing Oryx backend.
 * 
 * @author Stefan Krumnow
 *
 */
public class FsModelRevision extends FsSecureBusinessObject {
	
	public static final String ID_PREFIX = "head-revision-of-";
	FsModel parentModel;
	
	public FsModelRevision(FsModel parentModel) {
		super();
		this.parentModel = parentModel;
	}

	public String getAuthor() {
		return FsUser.getDummy().getFullName();
	}
	
	public void setAuthor(String id) { 
		return ; 
	}

	public String getComment() {
		return emptyString;
	}

	public int getRevisionNumber() {
		return 0;
	}

	public Date getCreationDate() {
		return parentModel.getCreationDate();
	}

	public FsModelRepresentationInfo getRepresentation(RepresentationType type) {
		return parentModel.getRepresentation(type);
	}
	
	public FsModelRepresentationInfo createRepresentation(RepresentationType type, byte[] content) {
		return parentModel.createRepresentation(type, content);
	}
	
	public FsComment getCommentObj() {
		return new FsComment(this);
	}


	/*
	 * 
	 * INTERFACE COMPLIANCE METHODS 
	 * 
	 */

	@Override
	@SuppressWarnings("unchecked")
	public <T extends FsSecureBusinessObject> Set<T> getChildren(Class<T> type) {
		if (FsComment.class.isAssignableFrom(type)){
			return emptySet;
		} else {
			return super.getChildren(type);
		}
	}
	
	@Override
	@SuppressWarnings("unchecked")
	public <T extends FsSecureBusinessObject> Set<T> getParents(Class<T> businessObjectClass) {
		if (FsModel.class.isAssignableFrom(businessObjectClass)){
			return (Set<T>)getParents(false, FsModel.class);
		} else {
			return super.getParents(businessObjectClass);
		}
	}

	public Set<FsModel> getParents(boolean deleted, Class<FsModel> type) {
		assert (!parentModel.isDeleted());
		Set<FsModel> parents = new HashSet<FsModel>(1);
		parents.add(parentModel);
		return parents;
	}

	@Override
	public String getId() {
		//System.out.println("head-revision-of-" + parentModel.getId());
		return "head-revision-of-" + parentModel.getId() ;
	}



}
