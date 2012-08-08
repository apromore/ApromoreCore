/**
 * Copyright (c) 2011-2012 Felix Mannhardt, felix.mannhardt@smail.wir.h-brs.de
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.

 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.

 * You should have received a copy of the GNU Lesser General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 * 
 * See: http://www.gnu.org/licenses/lgpl-3.0
 * 
 */
package de.hbrs.oryx.yawl.converter.handler.yawl;

import org.json.JSONException;
import org.json.JSONObject;
import org.yawlfoundation.yawl.elements.data.YVariable;

import de.hbrs.oryx.yawl.converter.context.YAWLConversionContext;

/**
 * Abstract base implementation of the YAWLHandler interface. Using a
 * YAWLConversionContext to store information about the coversion state and the
 * converted objects.
 * 
 * @author Felix Mannhardt (Bonn-Rhein-Sieg University of Applied Sciences)
 * 
 */
public abstract class YAWLHandlerImpl implements YAWLHandler {

	private final YAWLConversionContext context;

	public YAWLHandlerImpl(YAWLConversionContext context) {
		this.context = context;
	}

	protected YAWLConversionContext getContext() {
		return context;
	}

	protected String convertNullable(Object obj) {
		return obj != null ? obj.toString() : "";
	}

	protected JSONObject convertParameter(YVariable variable, String usage) throws JSONException {
		JSONObject jsonParam = new JSONObject();
		jsonParam.put("name", variable.getName());
		jsonParam.put("type", variable.getDataTypeName());
		jsonParam.put("usage", usage);
		if (variable.getInitialValue() != null) {
			jsonParam.put("initialvalue", variable.getInitialValue());
		} else {
			jsonParam.put("initialvalue", variable.getDefaultValue());
		}
		jsonParam.put("namespace", variable.getDataTypeNameSpace());
		jsonParam.put("ismandatory", variable.isMandatory());
		jsonParam.put("attributes", variable.getAttributes().toXMLElements());
		if (variable.getLogPredicate() != null) {
			jsonParam.put("logpredicate", variable.getLogPredicate().toXML().replace("<logPredicate>", "").replace("</logPredicate>", ""));
		}
		return jsonParam;
	}

}
