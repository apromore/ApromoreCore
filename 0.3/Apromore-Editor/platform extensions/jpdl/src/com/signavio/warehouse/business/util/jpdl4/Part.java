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

import org.json.JSONException;
import org.json.JSONObject;

public class Part {
	private String expression = null;
	private String name = null;
	private IWireObjectGroup child = null;

	public Part(JSONObject part) {
		try {
			this.name = part.getString("p_name");
		} catch (JSONException e) {}
		
		try {
			this.expression = part.getString("expr");
		} catch (JSONException e) {}
		
		try {
			if(part.getString("type").toLowerCase().equals("string")) {
				String sName = part.getString("name");
				String sValue = part.getString("value");
				this.child = new WireString(sName, sValue);
			}
			if(part.getString("type").toLowerCase().equals("int")) {
				String sName = part.getString("name");
				String sValue = part.getString("value");
				this.child = new WireInt(sName, sValue);
			}
			if(part.getString("type").toLowerCase().equals("long")) {
				String sName = part.getString("name");
				String sValue = part.getString("value");
				this.child = new WireLong(sName, sValue);
			}
			if(part.getString("type").toLowerCase().equals("float")) {
				String sName = part.getString("name");
				String sValue = part.getString("value");
				this.child = new WireFloat(sName, sValue);
			}
			if(part.getString("type").toLowerCase().equals("double")) {
				String sName = part.getString("name");
				String sValue = part.getString("value");
				this.child = new WireDouble(sName, sValue);
			}
			if(part.getString("type").toLowerCase().equals("true")) {
				String sName = part.getString("name");
				String sValue = part.getString("value");
				this.child = new WireTrue(sName, sValue);
			}
			if(part.getString("type").toLowerCase().equals("false")) {
				String sName = part.getString("name");
				String sValue = part.getString("value");
				this.child = new WireFalse(sName, sValue);
			}
			
			if(part.getString("type").toLowerCase().equals("object")) {
				String oName = part.getString("value");
				this.child = new WireObjectType(oName);
			}
		} catch (JSONException e) {}
	}

	public String getExpression() {
		return expression;
	}

	public void setExpression(String expression) {
		this.expression = expression;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public IWireObjectGroup getChild() {
		return child;
	}

	public void setChild(IWireObjectGroup child) {
		this.child = child;
	}
	
	public String toJpdl() {
		StringWriter jpdl = new StringWriter();
		jpdl.write("    <part ");
		jpdl.write(JsonToJpdl.transformAttribute("name", name));
		jpdl.write(JsonToJpdl.transformAttribute("expr", expression));
		if(child != null) {
			jpdl.write(" >");
			jpdl.write(child.toJpdl());
			jpdl.write("</part>\n");
		} else {
			jpdl.write(" />\n");
		}
		return jpdl.toString();
	}

}
