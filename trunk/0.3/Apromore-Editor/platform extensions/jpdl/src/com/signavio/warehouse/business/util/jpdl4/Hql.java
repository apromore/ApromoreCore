/**
 * Copyright (c) 2009, Ole Eckermann, Stefan Krumnow & Signavio GmbH
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
package com.signavio.warehouse.business.util.jpdl4;

import java.io.StringWriter;

import org.apache.commons.lang.StringEscapeUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class Hql extends Sql {

	public Hql(JSONObject hql) {
		super(hql);
	}
	
	public Hql(org.w3c.dom.Node hql) {
		super(hql);
	}

	@Override
	public String toJpdl() throws InvalidModelException {
		StringWriter jpdl = new StringWriter();
		jpdl.write("  <hql");

		jpdl.write(JsonToJpdl.transformAttribute("name", name));
		jpdl.write(JsonToJpdl.transformAttribute("var", var));
		if (unique != null)
			jpdl.write(JsonToJpdl.transformAttribute("unique", unique
					.toString()));

		if (bounds != null) {
			jpdl.write(bounds.toJpdl());
		} else {
			throw new InvalidModelException(
					"Invalid HQL activity. Bounds is missing.");
		}

		jpdl.write(" >\n");

		if (query != null) {
			jpdl.write("    <query>");
			jpdl.write(StringEscapeUtils.escapeXml(query));
			jpdl.write("</query>\n");
		} else {
			throw new InvalidModelException(
					"Invalid HQL activity. Query is missing.");
		}

		if (parameters != null) {
			jpdl.write(parameters.toJpdl());
		}

		for (Transition t : outgoings) {
			jpdl.write(t.toJpdl());
		}

		jpdl.write("  </hql>\n");
		return jpdl.toString();
	}
	
	
	
	@Override
	public JSONObject toJson() throws JSONException {
		JSONObject stencil = new JSONObject();
		stencil.put("id", "hql");

		JSONArray outgoing = JpdlToJson.getTransitions(outgoings);

		JSONObject properties = new JSONObject();
		properties.put("bgcolor", "#ffffcc");
		if (name != null)
			properties.put("name", name);
		if (var != null)
			properties.put("var", var);
		if (unique != null)
			properties.put("unique", unique.toString());
		if (query != null)
			properties.put("query", query);
		if(parameters != null)
			properties.put("parameters", parameters.toJson());

		JSONArray childShapes = new JSONArray();

		return JpdlToJson.createJsonObject(uuid, stencil, outgoing, properties,
				childShapes, bounds.toJson());
	}

}
