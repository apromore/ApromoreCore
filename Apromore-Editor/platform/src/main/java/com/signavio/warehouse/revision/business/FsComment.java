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
package com.signavio.warehouse.revision.business;

import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import com.signavio.platform.security.business.FsAccessToken;
import com.signavio.platform.security.business.FsSecureBusinessObject;
import com.signavio.platform.security.business.FsSecurityManager;
import com.signavio.usermanagement.user.business.FsUser;
import com.signavio.warehouse.model.business.FsModel;

/**
 * Implementation of an Comment for file system accessing Oryx.
 * 
 * @author Stefan Krumnow
 *
 */
public class FsComment extends FsSecureBusinessObject {
	
	public static final String ID_PREFIX = "comment-of-";
	
	String revisionId;
	
	public static FsComment createComment(FsModelRevision revision, Object ... params) {
		return new FsComment(revision);
	}
	
	public FsComment(FsModelRevision parentRevision) {
		revisionId = parentRevision.getId();
	}

	public String getTitle() { return emptyString; }
	public String getDescription() { return emptyString; }
	public String getAuthorName() { return emptyString; }
	public String getResourceId() { return emptyString; }
	
	public Date getCreated() { return null; }

	public FsUser getUser() { return FsUser.getDummy(); }
	
	@Override
	@SuppressWarnings("unchecked")
	public <T extends FsSecureBusinessObject> Set<T> getParents(Class<T> businessObjectClass) {
		if (FsModelRevision.class.isAssignableFrom(businessObjectClass)){
			String modelId = revisionId.substring(FsModelRevision.ID_PREFIX.length());
			FsModel foundModel = FsSecurityManager.getInstance().loadObject(FsModel.class, modelId, FsAccessToken.getDummy());
			if (foundModel != null) {
				Set<T> result = new HashSet<T>();
				result.add((T)foundModel.getHeadRevision());
				return result;
			} else {
				return (Set<T>)emptySet;
			}
		} else {
			return super.getParents(businessObjectClass);
		}
	}
	
	@Override
	public String getId() {
		return ID_PREFIX + revisionId;
	}

}
