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

import com.signavio.platform.account.business.FsAccountManager;
import com.signavio.platform.tenant.business.FsTenantManager;


/**
 * Implementation of a root object in the file accessing Oryx backend.
 * 
 * @author Stefan Krumnow
 *
 */
public class FsRootObject extends FsSecureBusinessObject {
	
	private static final FsRootObject SINGLETON;
	public static final String ID_OF_SINGLETON = "root-object";
	static {
		SINGLETON = new FsRootObject();
	}

	public static FsRootObject getRootObject(FsAccessToken token) {
		return SINGLETON;
	}
	
	public FsAccountManager getAccountManager(){
		return FsAccountManager.getSingleton();
	}
	public FsTenantManager getTenantManager(){
		return FsTenantManager.getSingleton();
	}
	
	@Override
	public String getId() {
		return ID_OF_SINGLETON;
	}

}
