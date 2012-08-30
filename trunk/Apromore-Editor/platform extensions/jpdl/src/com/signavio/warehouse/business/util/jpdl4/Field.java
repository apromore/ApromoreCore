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

public class Field {
	private IWireObjectGroup child = null;
	private String name;
	
	protected String elementName = "field";
	
	public Field(String name) {
		this.name = name;
	}
	
	public Field (JSONObject field) {
		try {
			this.name = field.getString("f_name");
		} catch (JSONException e) {}
		
		try {
			if(field.getString("type").toLowerCase().equals("string")) {
				String sName = field.getString("name");
				String sValue = field.getString("value");
				this.child = new WireString(sName, sValue);
			}
			if(field.getString("type").toLowerCase().equals("int")) {
				String sName = field.getString("name");
				String sValue = field.getString("value");
				this.child = new WireInt(sName, sValue);
			}
			if(field.getString("type").toLowerCase().equals("long")) {
				String sName = field.getString("name");
				String sValue = field.getString("value");
				this.child = new WireLong(sName, sValue);
			}
			if(field.getString("type").toLowerCase().equals("float")) {
				String sName = field.getString("name");
				String sValue = field.getString("value");
				this.child = new WireFloat(sName, sValue);
			}
			if(field.getString("type").toLowerCase().equals("double")) {
				String sName = field.getString("name");
				String sValue = field.getString("value");
				this.child = new WireDouble(sName, sValue);
			}
			if(field.getString("type").toLowerCase().equals("true")) {
				String sName = field.getString("name");
				String sValue = field.getString("value");
				this.child = new WireTrue(sName, sValue);
			}
			if(field.getString("type").toLowerCase().equals("false")) {
				String sName = field.getString("name");
				String sValue = field.getString("value");
				this.child = new WireFalse(sName, sValue);
			}
			
			if(field.getString("type").toLowerCase().equals("object")) {
				String oName = field.getString("value");
				this.child = new WireObjectType(oName);
			}
		} catch (JSONException e) {}
	}
	
	public String toJpdl() throws InvalidModelException {
		StringWriter jpdl = new StringWriter();
		jpdl.write("    <" + elementName + " ");
		jpdl.write(JsonToJpdl.transformAttribute("name", name));
		jpdl.write(" >\n");
		if(child != null) {
			jpdl.write(child.toJpdl());
		} else {
			throw new InvalidModelException("Invalid Field. Object or String is missing");
		}
		jpdl.write("    </" + elementName + ">\n");
		return jpdl.toString();
	}

	public IWireObjectGroup getChild() {
		return child;
	}

	public void setChild(IWireObjectGroup child) {
		this.child = child;
	}
}
