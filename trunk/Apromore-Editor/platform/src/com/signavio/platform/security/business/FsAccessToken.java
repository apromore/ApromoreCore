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
package com.signavio.platform.security.business;

import java.io.Serializable;

import com.signavio.platform.account.business.FsAccount;
import com.signavio.platform.tenant.business.FsTenant;
import com.signavio.usermanagement.user.business.FsUser;

/**
 * Implementation of an Access Token for file system accessing Oryx.
 * 
 * @author Stefan Krumnow
 *
 */
public class FsAccessToken implements Serializable {

	private static final long serialVersionUID = -8581096924234627701L;
	private static final String emptyString = "";
	
	private static final FsAccessToken DUMMY;

	static {
		DUMMY = new FsAccessToken();
	}
	
	public static FsAccessToken getDummy() {
		return DUMMY;
	}
	
	
	public FsAccessToken() {
		super();
	}
	
	public FsAccessToken(String string, String id, Object object) {
		this();
	}


	public String getPrincipal() {
		return emptyString;
	}
	
	
	public String getTenantId() {
		return FsTenant.ID_OF_SINGLETON;
	}
	
	public String getUserId() {
		return FsUser.ID_OF_DUMMY;
	}
	
	public FsAccount getAccount() {
		return FsAccount.getDummy();
	}
	
	public FsUser getUser() {
		return FsUser.getDummy();
	}


	public boolean hasLoginLicense() {
		return true;
	}


}
