/*
 * Copyright © 2009-2014 The Apromore Initiative.
 *
 * This file is part of "Apromore".
 *
 * "Apromore" is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 3 of the License, or
 * (at your option) any later version.
 *
 * "Apromore" is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.
 * If not, see <http://www.gnu.org/licenses/lgpl-3.0.html>.
 */

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
