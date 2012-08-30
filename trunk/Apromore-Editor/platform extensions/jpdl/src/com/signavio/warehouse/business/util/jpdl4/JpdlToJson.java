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

import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;

public class JpdlToJson {
	private static Process process;
	
	public static String transform(Document doc) {
		// trigger for transformation
		Node root = getRootNode(doc);
		if (root == null)
			return "";
			
		process = new Process(root);
		process.createTransitions();
		
		try {
			return process.toJson();
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return "";

	}

	public static JSONObject createJsonObject(String uuid, JSONObject stencil,
			JSONArray outgoing, JSONObject properties, JSONArray childShapes,
			JSONObject bounds) throws JSONException {
		// create Oryx compliant JSONObject for Node
		JSONObject node = new JSONObject();

		node.put("bounds", bounds);
		node.put("resourceId", uuid);
		node.put("stencil", stencil);
		node.put("outgoing", outgoing);
		node.put("properties", properties);
		node.put("childShapes", childShapes);
		return node;
	}

	private static Node getRootNode(Document doc) {
		Node node = doc.getDocumentElement();
		if (node == null || !node.getNodeName().equals("process"))
			return null;
		return node;
	}
	
	public static Bounds getBounds(Node node) {
		if (node != null) {
			String bounds = node.getNodeValue();
			return new Bounds(bounds.split(","));
		} else {
			return new Bounds();
		}
	}
	
	public static String getAttribute(NamedNodeMap attributes, String name) {
		if(attributes.getNamedItem(name) != null)
			return attributes.getNamedItem(name).getNodeValue();
		return null;
	}
	
	public static Process getProcess() {
		return process;
	}
	
	public static JSONArray getTransitions(List<Transition> outgoings) throws JSONException {
		JSONArray outgoing = new JSONArray();

		for(Transition t : outgoings) {
			JSONObject tt = new JSONObject();
			tt.put("resourceId", t.getUuid());
			outgoing.put(tt);
		}
		return outgoing;
	}
}
