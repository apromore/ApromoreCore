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
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.NamedNodeMap;

public class Java extends Node {

	private String clazz;
	private String method;
	private String var;
	private List<Arg> args;
	private List<Field> field;

	public Java(JSONObject java) {

		this.name = JsonToJpdl.getAttribute(java, "name");
		this.clazz = JsonToJpdl.getAttribute(java, "class");
		this.method = JsonToJpdl.getAttribute(java, "method");
		this.var = JsonToJpdl.getAttribute(java, "var");
		this.bounds = JsonToJpdl.getBounds(java);

		field = new ArrayList<Field>();
		try {
			JSONArray parameters = java.getJSONObject("properties")
					.getJSONObject("field").getJSONArray("items");
			for (int i = 0; i < parameters.length(); i++) {
				JSONObject item = parameters.getJSONObject(i);
				field.add(new Field(item));
			}
		} catch (JSONException e) {
		}

		args = new ArrayList<Arg>();
		try {
			JSONArray parameters = java.getJSONObject("properties")
					.getJSONObject("arg").getJSONArray("items");
			for (int i = 0; i < parameters.length(); i++) {
				JSONObject item = parameters.getJSONObject(i);
				args.add(new Arg(item));
			}
		} catch (JSONException e) {
		}

		this.outgoings = JsonToJpdl.getOutgoings(java);

	}

	public Java(org.w3c.dom.Node java) {
		this.uuid = "oryx_" + UUID.randomUUID().toString();
		NamedNodeMap attributes = java.getAttributes();
		this.name = JpdlToJson.getAttribute(attributes, "name");
		this.clazz = JpdlToJson.getAttribute(attributes, "class");
		this.method = JpdlToJson.getAttribute(attributes, "method");
		this.var = JpdlToJson.getAttribute(attributes, "var");
		this.bounds = JpdlToJson.getBounds(attributes.getNamedItem("g"));
		// TODO add args and fields
	}

	@Override
	public String toJpdl() throws InvalidModelException {
		StringWriter jpdl = new StringWriter();
		jpdl.write("  <java");

		jpdl.write(JsonToJpdl.transformAttribute("name", name));

		try {
			jpdl.write(JsonToJpdl.transformRequieredAttribute("class", clazz));
			jpdl.write(JsonToJpdl.transformRequieredAttribute("method", method));
			jpdl.write(JsonToJpdl.transformRequieredAttribute("var", var));
		} catch (InvalidModelException e) {
			throw new InvalidModelException("Invalid Java activity. "
					+ e.getMessage());
		}

		if (bounds != null) {
			jpdl.write(bounds.toJpdl());
		} else {
			throw new InvalidModelException(
					"Invalid Java activity. Bounds is missing.");
		}

		jpdl.write(" >\n");

		for (Field f : field) {
			jpdl.write(f.toJpdl());
		}

		for (Arg a : args) {
			jpdl.write(a.toJpdl());
		}

		for (Transition t : outgoings) {
			jpdl.write(t.toJpdl());
		}

		jpdl.write("  </java>\n\n");

		return jpdl.toString();
	}

	public String getClazz() {
		return clazz;
	}

	public void setClazz(String clazz) {
		this.clazz = clazz;
	}

	public String getMethod() {
		return method;
	}

	public void setMethod(String method) {
		this.method = method;
	}

	public String getVar() {
		return var;
	}

	public void setVar(String var) {
		this.var = var;
	}

	@Override
	public JSONObject toJson() throws JSONException {
		JSONObject stencil = new JSONObject();
		stencil.put("id", "java");

		JSONArray outgoing = JpdlToJson.getTransitions(outgoings);

		JSONObject properties = new JSONObject();
		properties.put("bgcolor", "#ffffcc");
		if (name != null)
			properties.put("name", name);
		if (clazz != null)
			properties.put("class", clazz);
		if (method != null)
			properties.put("method", method);
		if (var != null)
			properties.put("var", var);

		// TODO add fields and args

		JSONArray childShapes = new JSONArray();

		return JpdlToJson.createJsonObject(uuid, stencil, outgoing, properties,
				childShapes, bounds.toJson());
	}

}
