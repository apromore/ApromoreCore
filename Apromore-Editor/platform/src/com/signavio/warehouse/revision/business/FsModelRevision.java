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
