package com.signavio.warehouse.directory.handler;

import javax.servlet.ServletContext;

import com.signavio.platform.annotations.HandlerConfiguration;

@HandlerConfiguration(context=DirectoryHandler.class, uri="/parents", rel="parents")
public class ParentDirectoriesHandler extends AbstractParentDirectoriesHandler {

	public ParentDirectoriesHandler(ServletContext servletContext) {
		super(servletContext);
	}

}
