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

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class Parameters {
	private List<WireString> parameters;

	public Parameters(org.w3c.dom.Node params) {
		// <parameters>
		// <string name="taskName" value="%i%" />
		// </parameters>
		this.parameters = new ArrayList<WireString>();
		if (params.hasChildNodes())
			for (org.w3c.dom.Node n = params.getFirstChild(); n != null; n = n
					.getNextSibling())
				// TODO handle WireObjectType
				if (n.getNodeName().equals("string"))
					try {
						String name = n.getAttributes().getNamedItem("name")
								.getNodeValue();
						String value = n.getAttributes().getNamedItem("value")
								.getNodeValue();
						WireString m = new WireString(name, value);
						this.parameters.add(m);
					} catch (Exception e) {
						// name or value is missing for parameter.
						// parameter is ignored.
					}
	}

	public Parameters(JSONObject parameters) {
		this.parameters = new ArrayList<WireString>();
		try {
			JSONArray items = parameters.getJSONArray("items");
			for (int i = 0; i < items.length(); i++) {
				JSONObject item = items.getJSONObject(i);
				WireString newItem = null;
				try {
					if (item.getString("type").toLowerCase().equals("string")) {
						String sName = item.getString("name");
						String sValue = item.getString("value");
						newItem = new WireString(sName, sValue);
					}
					// TODO Handle WireObjectType
					//if (item.getString("type").toLowerCase().equals("object")) {
					//	String oName = item.getString("name");
					//	newItem = new WireObjectType(oName);
					//}
				} catch (JSONException d) {
				}
				if (item != null)
					this.parameters.add(newItem);
			}
		} catch (JSONException e) {
		}
	}

	public List<WireString> getParameters() {
		return parameters;
	}

	public void setParameters(List<WireString> parameters) {
		this.parameters = parameters;
	}

	public String toJpdl() {
		StringWriter jpdl = new StringWriter();
		jpdl.write("    <parameters>\n");

		for (IWireObjectGroup o : parameters) {
			jpdl.write(o.toJpdl());
		}

		jpdl.write("    </parameters>\n");
		return jpdl.toString();
	}

	public JSONObject toJson() throws JSONException {
		JSONObject params = new JSONObject();
		params.put("totalCount", parameters.size());
		JSONArray items = new JSONArray();
		for(WireString item : parameters) {
			JSONObject a = new JSONObject();
			a.put("name", item.getName());
			a.put("value", item.getValue());
			a.put("type", "string");
			items.put(a);
		}
		params.put("items", items);

		return params;
	}

}
