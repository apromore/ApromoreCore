/*
 * Copyright © 2009-2014 The Apromore Initiative.
 *
 * This file is part of “Apromore”.
 *
 * “Apromore” is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 3 of the License, or
 * (at your option) any later version.
 *
 * “Apromore” is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.
 * If not, see <http://www.gnu.org/licenses/lgpl-3.0.html>.
 */

package com.signavio.warehouse.directory.handler;

import java.util.List;

import javax.servlet.ServletContext;

import org.json.JSONArray;

import com.signavio.platform.core.Directory;
import com.signavio.platform.handler.AbstractHandler;
import com.signavio.platform.security.business.FsAccessToken;
import com.signavio.platform.security.business.FsSecureBusinessObject;
import com.signavio.warehouse.directory.business.FsDirectory;
import com.signavio.warehouse.model.business.FsModel;

public abstract class AbstractParentDirectoriesHandler extends AbstractHandler {

	public AbstractParentDirectoriesHandler(ServletContext servletContext) {
		super(servletContext);
	}

	/**
	 * Get an ordered list of all (indirect) parent directories 
	 */
	@Override 
	public  <T extends FsSecureBusinessObject> Object getRepresentation(T sbo, Object params, FsAccessToken token) {
		JSONArray result = new JSONArray();
		
		List<FsDirectory> parents = null;
		if(sbo instanceof FsModel) {
			parents = ((FsModel)sbo).getParentDirectories();
		} else if(sbo instanceof FsDirectory) {
			parents = ((FsDirectory)sbo).getParentDirectories();
		}
		
		if(parents != null) {
			DirectoryHandler dirHandler = new DirectoryHandler(this.getServletContext());
			for(FsDirectory parent : parents) {
				result.put(dirHandler.getDirectoryInfo(parent));
			}
		}
		
		return result;
	}
}
