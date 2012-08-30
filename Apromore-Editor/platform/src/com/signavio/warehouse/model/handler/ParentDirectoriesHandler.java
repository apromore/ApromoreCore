package com.signavio.warehouse.model.handler;

import javax.servlet.ServletContext;

import com.signavio.platform.annotations.HandlerConfiguration;
import com.signavio.warehouse.directory.handler.AbstractParentDirectoriesHandler;

@HandlerConfiguration(context=ModelHandler.class, uri="/parents", rel="parents")
public class ParentDirectoriesHandler extends AbstractParentDirectoriesHandler {

	public ParentDirectoriesHandler(ServletContext servletContext) {
		super(servletContext);
	}

}
