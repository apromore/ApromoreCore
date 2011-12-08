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
import java.util.UUID;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.NamedNodeMap;

public class Xor extends Node {

	private String expression;
	private String handler;

	public Xor(JSONObject xor) {

		this.name = JsonToJpdl.getAttribute(xor, "name");
		this.expression = JsonToJpdl.getAttribute(xor, "expr");
		this.handler = JsonToJpdl.getAttribute(xor, "handler");
		this.bounds = JsonToJpdl.getBounds(xor);
		this.outgoings = JsonToJpdl.getOutgoings(xor);
		
		this.bounds.setUlx(this.bounds.getUlx() - 4);
		this.bounds.setUly(this.bounds.getUly() - 4);
		this.bounds.setWidth(48);
		this.bounds.setHeight(48);

	}

	public Xor(org.w3c.dom.Node xor) {
		this.uuid = "oryx_" + UUID.randomUUID().toString();
		NamedNodeMap attributes = xor.getAttributes();
		this.name = JpdlToJson.getAttribute(attributes, "name");
		this.expression = JpdlToJson.getAttribute(attributes, "expression");
		if (xor.hasChildNodes())
			for (org.w3c.dom.Node a = xor.getFirstChild(); a != null; a = a.getNextSibling())
				if(a.getNodeName().equals("handler")) {
					this.handler = a.getAttributes().getNamedItem("class").getNodeValue();
					break;
				}
		this.bounds = JpdlToJson.getBounds(attributes.getNamedItem("g"));
		
		this.bounds.setUlx(this.bounds.getUlx() + 4);
		this.bounds.setUly(this.bounds.getUly() + 4);
		this.bounds.setWidth(40);
		this.bounds.setHeight(40);		
	}

	@Override
	public String toJpdl() throws InvalidModelException {
		StringWriter jpdl = new StringWriter();
		jpdl.write("  <decision");

		jpdl.write(JsonToJpdl.transformAttribute("name", name));
		jpdl.write(JsonToJpdl.transformAttribute("expr", expression));

		if (bounds != null) {
			jpdl.write(bounds.toJpdl());
		} else {
			throw new InvalidModelException(
					"Invalid Exclusive gateway. Bounds is missing.");
		}

		jpdl.write(" >\n");

		if (handler != null && handler.length() > 0)
			jpdl.write("    <handler class=\"" + handler + "\" />\n");

		for (Transition t : outgoings) {
			jpdl.write(t.toJpdl());
		}

		jpdl.write("  </decision>\n\n");

		return jpdl.toString();
	}

	@Override
	public JSONObject toJson() throws JSONException {
		JSONObject stencil = new JSONObject();
		stencil.put("id", "Exclusive_Databased_Gateway");

		JSONArray outgoing = JpdlToJson.getTransitions(outgoings);

		JSONObject properties = new JSONObject();
		properties.put("bgcolor", "#ffffff");
		if (name != null)
			properties.put("name", name);
		if (expression != null)
			properties.put("expr", expression);
		if (handler != null)
			properties.put("handler", handler);

		JSONArray childShapes = new JSONArray();

		return JpdlToJson.createJsonObject(uuid, stencil, outgoing, properties,
				childShapes, bounds.toJson());
	}

}
