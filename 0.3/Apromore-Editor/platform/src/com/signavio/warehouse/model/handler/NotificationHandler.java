package com.signavio.warehouse.model.handler;

import javax.servlet.ServletContext;

import org.json.JSONObject;

import com.signavio.platform.annotations.HandlerConfiguration;
import com.signavio.platform.handler.AbstractHandler;
import com.signavio.platform.security.business.FsAccessToken;
import com.signavio.platform.security.business.FsSecureBusinessObject;

@HandlerConfiguration(context=ModelHandler.class, uri="/notify", rel="notify")
public class NotificationHandler extends AbstractHandler {

	public NotificationHandler(ServletContext servletContext) {
		super(servletContext);
	}
	
	@Override
	public <T extends FsSecureBusinessObject> Object getRepresentation(T sbo, Object params, FsAccessToken token) {
		return new JSONObject();
	}
}
