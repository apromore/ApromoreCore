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

public class Task extends Node {

	private String assignee;
	private String candidateGroups;
	private String candidateUsers;
	private String swimlane;
	
	public Task(JSONObject task) {

		this.name = JsonToJpdl.getAttribute(task, "name");
		this.assignee = JsonToJpdl.getAttribute(task, "assignee");
		this.candidateGroups = JsonToJpdl.getAttribute(task,
				"candidate-groups");
		this.candidateUsers = JsonToJpdl.getAttribute(task,
				"candidate-users");
		this.swimlane = JsonToJpdl.getAttribute(task, "swimlane");
		this.bounds = JsonToJpdl.getBounds(task);
		this.outgoings = JsonToJpdl.getOutgoings(task);

	}
	
	public Task(org.w3c.dom.Node task) {
		this.uuid = "oryx_" + UUID.randomUUID().toString();
		NamedNodeMap attributes = task.getAttributes();
		this.name = JpdlToJson.getAttribute(attributes, "name");
		this.assignee = JpdlToJson.getAttribute(attributes, "assignee");
		this.candidateGroups = JpdlToJson.getAttribute(attributes, "candidate-groups");
		this.candidateUsers = JpdlToJson.getAttribute(attributes, "candidate-users");
		this.swimlane = JpdlToJson.getAttribute(attributes, "swimlane");

		this.bounds = JpdlToJson.getBounds(attributes.getNamedItem("g"));
	}

	public String getSwimlane() {
		return swimlane;
	}

	public void setSwimlane(String swimlane) {
		this.swimlane = swimlane;
	}

	public String getCandidateGroups() {
		return candidateGroups;
	}

	public void setCandidateGroups(String candidateGroups) {
		this.candidateGroups = candidateGroups;
	}

	public String getCandidateUsers() {
		return candidateUsers;
	}

	public void setCandidateUsers(String candidateUsers) {
		this.candidateUsers = candidateUsers;
	}

	public String getAssignee() {
		return assignee;
	}

	public void setAssignee(String assignee) {
		this.assignee = assignee;
	}

	@Override
	public String toJpdl() throws InvalidModelException {
		StringWriter jpdl = new StringWriter();
		jpdl.write("  <task");

		jpdl.write(JsonToJpdl.transformAttribute("name", name));
		if (assignee != null && assignee.length() > 0)
			jpdl.write(JsonToJpdl.transformAttribute("assignee", assignee));
		if (candidateGroups != null && candidateGroups.length() > 0) {
			jpdl.write(JsonToJpdl.transformAttribute("candidate-groups",
					candidateGroups));
		}
		if (candidateUsers != null && candidateUsers.length() > 0) {
			jpdl.write(JsonToJpdl.transformAttribute("candidate-users",
					candidateUsers));
		} 
		if (swimlane != null) {
			jpdl.write(JsonToJpdl.transformAttribute("swimlane", swimlane));
		}

		if (bounds != null) {
			jpdl.write(bounds.toJpdl());
		} else {
			throw new InvalidModelException("Invalid Task. Bounds is missing.");
		}

		if (outgoings.size() > 0) {
			jpdl.write(" >\n");
			for (Transition t : outgoings) {
				jpdl.write(t.toJpdl());
			}
			jpdl.write("  </task>\n\n");
		} else {
			jpdl.write(" />\n\n");
		}

		return jpdl.toString();
	}

	@Override
	public JSONObject toJson() throws JSONException {
		JSONObject stencil = new JSONObject();
		stencil.put("id", "Task");

		JSONArray outgoing = JpdlToJson.getTransitions(outgoings);

		JSONObject properties = new JSONObject();
		properties.put("bgcolor", "#ffffcc");
		if (name != null)
			properties.put("name", name);
		if (assignee != null)
			properties.put("assignee", assignee);
		if (candidateGroups != null)
			properties.put("candidate-groups", candidateGroups);
		if (candidateUsers != null)
			properties.put("candidate-users", candidateUsers);
		if (swimlane != null)
			properties.put("swimlane", swimlane);

		JSONArray childShapes = new JSONArray();

		return JpdlToJson.createJsonObject(uuid, stencil, outgoing, properties,
				childShapes, bounds.toJson());
	}

}
