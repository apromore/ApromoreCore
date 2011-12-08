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

public class EndEvent extends Node {

	protected String state;
	protected String ends;

	public EndEvent(JSONObject endEvent) {

		this.name = JsonToJpdl.getAttribute(endEvent, "name");
		this.ends = JsonToJpdl.getAttribute(endEvent, "ends");
		this.state = JsonToJpdl.getAttribute(endEvent, "state");
		this.bounds = JsonToJpdl.getBounds(endEvent);
		
		this.bounds.setUlx(this.bounds.getUlx() - 10);
		this.bounds.setUly(this.bounds.getUly() - 10);
		this.bounds.setWidth(48);
		this.bounds.setHeight(48);
		

	}
	
	public EndEvent(org.w3c.dom.Node endEvent) {
		this.uuid = "oryx_" + UUID.randomUUID().toString();
		NamedNodeMap attributes = endEvent.getAttributes();
		this.name = JpdlToJson.getAttribute(attributes, "name");
		this.ends = JpdlToJson.getAttribute(attributes, "ends");
		this.state = JpdlToJson.getAttribute(attributes, "state");
		this.bounds = JpdlToJson.getBounds(attributes.getNamedItem("g"));
		
		this.bounds.setUlx(this.bounds.getUlx() + 10);
		this.bounds.setUly(this.bounds.getUly() + 10);
		this.bounds.setWidth(28);
		this.bounds.setHeight(28);
		
	}

	public String getState() {
		return state;
	}

	public void setState(String state) {
		this.state = state;
	}

	public String getEnds() {
		return ends;
	}

	public void setEnds(String ends) {
		this.ends = ends;
	}

	@Override
	public String toJpdl() throws InvalidModelException {
		String id = "end";
		return writeJpdlAttributes(id).toString();

	}

	@Override
	public JSONObject toJson() throws JSONException {
		String id = "EndEvent";

		return writeJsonAttributes(id);
	}

	protected JSONObject writeJsonAttributes(String id) throws JSONException {
		JSONObject stencil = new JSONObject();
		stencil.put("id", id);

		JSONArray outgoing = new JSONArray();

		JSONObject properties = new JSONObject();
		properties.put("bgcolor", "#ffffff");
		if (name != null)
			properties.put("name", name);
		if (state != null)
			properties.put("state", state);
		if (ends != null)
			properties.put("ends", ends);
		else
			properties.put("ends", "processinstance"); // default value

		JSONArray childShapes = new JSONArray();

		return JpdlToJson.createJsonObject(uuid, stencil, outgoing, properties,
				childShapes, bounds.toJson());
	}

	protected String writeJpdlAttributes(String id)
			throws InvalidModelException {
		StringWriter jpdl = new StringWriter();
		jpdl.write("  <" + id);
		jpdl.write(JsonToJpdl.transformAttribute("name", name));
		//if (!ends.equals("process-instance")) // processinstance is default value
			jpdl.write(JsonToJpdl.transformAttribute("ends", ends));
		jpdl.write(JsonToJpdl.transformAttribute("state", state));

		if (bounds != null) {
			jpdl.write(bounds.toJpdl());
		} else {
			throw new InvalidModelException(
					"Invalid End Event. Bounds is missing.");
		}

		jpdl.write(" />\n\n");

		return jpdl.toString();
	}
}
