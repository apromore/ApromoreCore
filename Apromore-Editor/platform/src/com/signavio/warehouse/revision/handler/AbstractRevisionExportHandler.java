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
package com.signavio.warehouse.revision.handler;

import java.util.Set;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletResponse;

import com.signavio.platform.annotations.HandlerExportConfiguration;
import com.signavio.platform.handler.ExportHandler;
import com.signavio.platform.security.business.FsSecureBusinessObject;
import com.signavio.warehouse.model.business.FsModel;
import com.signavio.warehouse.revision.business.FsModelRevision;

public abstract class AbstractRevisionExportHandler extends ExportHandler {

	public AbstractRevisionExportHandler(ServletContext servletContext) {
		super(servletContext);
	}

	@Override
	protected void setFileName(FsSecureBusinessObject sbo, HttpServletResponse res) {

		FsModel m = null;
		
		if(sbo instanceof FsModel) {
			m = (FsModel)sbo;
		} else {
			FsModelRevision mr = (FsModelRevision)sbo;
			Set<FsModel> parents = mr.getParents(false, FsModel.class);
			if(parents.size() == 0) {
				parents = mr.getParents(true, FsModel.class);
			}
			m = parents.iterator().next();
		}
		
		if (m != null) {
			HandlerExportConfiguration an = this.getClass().getAnnotation(HandlerExportConfiguration.class);
			res.setHeader("Content-Disposition", " "+(an.download()?"attachment":"inline")+"; filename=\"" + m.getName() + "." + an.name().toLowerCase()+"\"");
		}
	}
}
