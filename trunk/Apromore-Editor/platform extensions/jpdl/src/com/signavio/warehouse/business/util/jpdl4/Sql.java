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

import org.apache.commons.lang.StringEscapeUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.NamedNodeMap;

public class Sql extends Node {

	protected String var;
	protected Boolean unique;
	protected String query;
	protected Parameters parameters;

	public Sql(JSONObject sql) {

		this.name = JsonToJpdl.getAttribute(sql, "name");
		this.var = JsonToJpdl.getAttribute(sql, "var");
		this.unique = new Boolean(JsonToJpdl.getAttribute(sql, "unique"));
		this.query = JsonToJpdl.getAttribute(sql, "query");
		try {
			this.parameters = new Parameters(sql.getJSONObject("properties")
					.getJSONObject("parameters"));
		} catch (JSONException e) {
			this.parameters = null;
		}

		this.bounds = JsonToJpdl.getBounds(sql);

		this.outgoings = JsonToJpdl.getOutgoings(sql);

	}

	public Sql(org.w3c.dom.Node sql) {
		this.uuid = "oryx_" + UUID.randomUUID().toString();
		NamedNodeMap attributes = sql.getAttributes();
		this.name = JpdlToJson.getAttribute(attributes, "name");
		this.unique = Boolean.parseBoolean(JpdlToJson.getAttribute(attributes,
				"unique"));
		this.var = JpdlToJson.getAttribute(attributes, "var");

		this.bounds = JpdlToJson.getBounds(attributes.getNamedItem("g"));

		if (sql.hasChildNodes())
			for (org.w3c.dom.Node a = sql.getFirstChild(); a != null; a = a
					.getNextSibling()) {
				if (a.getNodeName().equals("query"))
					this.query = a.getTextContent();
				if (a.getNodeName().equals("parameters"))
					this.parameters = new Parameters(a);
			}
	}

	public String getVar() {
		return var;
	}

	public void setVar(String var) {
		this.var = var;
	}

	public Boolean getUnique() {
		return unique;
	}

	public void setUnique(Boolean unique) {
		this.unique = unique;
	}

	public String getQuery() {
		return query;
	}

	public void setQuery(String query) {
		this.query = query;
	}

	@Override
	public String toJpdl() throws InvalidModelException {
		StringWriter jpdl = new StringWriter();
		jpdl.write("  <sql");

		jpdl.write(JsonToJpdl.transformAttribute("name", name));
		jpdl.write(JsonToJpdl.transformAttribute("var", var));
		if (unique != null)
			jpdl.write(JsonToJpdl.transformAttribute("unique", unique
					.toString()));

		if (bounds != null) {
			jpdl.write(bounds.toJpdl());
		} else {
			throw new InvalidModelException(
					"Invalid SQL activity. Bounds is missing.");
		}

		jpdl.write(" >\n");

		if (query != null) {
			jpdl.write("    <query>");
			jpdl.write(StringEscapeUtils.escapeXml(query));
			jpdl.write("</query>\n");
		} else {
			throw new InvalidModelException(
					"Invalid SQL activity. Query is missing.");
		}

		if (parameters != null) {
			jpdl.write(parameters.toJpdl());
		}

		for (Transition t : outgoings) {
			jpdl.write(t.toJpdl());
		}

		jpdl.write("  </sql>\n\n");

		return jpdl.toString();
	}

	@Override
	public JSONObject toJson() throws JSONException {
		JSONObject stencil = new JSONObject();
		stencil.put("id", "sql");

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
