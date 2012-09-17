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

import org.json.JSONObject;

import java.io.StringWriter;

public class Swimlane {
    private String name;
    private String assignee;
    private String candidateGroups;
    private String candidateUsers;

    // TODO Integrate Swimline in transformation.
    // Swimlane as child of process.
    // Swimlane is not parent of any Task.

    public Swimlane(JSONObject swimlane) {
        this.name = JsonToJpdl.getAttribute(swimlane, "name");
        this.assignee = JsonToJpdl.getAttribute(swimlane, "assignee");
        if (assignee == null) {
            this.candidateGroups = JsonToJpdl.getAttribute(swimlane, "candidate-groups");
            this.candidateUsers = JsonToJpdl.getAttribute(swimlane, "candidate-users");
        }
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAssignee() {
        return assignee;
    }

    public void setAssignee(String assignee) {
        this.assignee = assignee;
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

    public String toJpdl() throws InvalidModelException {
        StringWriter jpdl = new StringWriter();
        jpdl.write("<swimlane");

        jpdl.write(JsonToJpdl.transformAttribute("name", name));
        jpdl.write(JsonToJpdl.transformAttribute("assignee", assignee));
        jpdl.write(JsonToJpdl.transformAttribute("candidate-groups", candidateGroups));
        jpdl.write(JsonToJpdl.transformAttribute("candidate-users", candidateUsers));

        jpdl.write(" />\n");
        return jpdl.toString();
    }

}
