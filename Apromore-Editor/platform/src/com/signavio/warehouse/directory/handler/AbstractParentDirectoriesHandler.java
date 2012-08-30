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
