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
