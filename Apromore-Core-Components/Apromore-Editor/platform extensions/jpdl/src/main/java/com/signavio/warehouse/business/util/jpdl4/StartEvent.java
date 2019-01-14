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

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.NamedNodeMap;

import java.io.StringWriter;
import java.util.UUID;

public class StartEvent extends Node {

    public StartEvent(JSONObject startEvent) {

        this.name = JsonToJpdl.getAttribute(startEvent, "name");
        this.bounds = JsonToJpdl.getBounds(startEvent);
        this.outgoings = JsonToJpdl.getOutgoings(startEvent);

        this.bounds.setUlx(this.bounds.getUlx() - 9);
        this.bounds.setUly(this.bounds.getUly() - 9);
        this.bounds.setWidth(48);
        this.bounds.setHeight(48);

    }

    public StartEvent(org.w3c.dom.Node startEvent) {
        this.uuid = "oryx_" + UUID.randomUUID().toString();
        NamedNodeMap attributes = startEvent.getAttributes();
        this.name = JpdlToJson.getAttribute(attributes, "name");
        this.bounds = JpdlToJson.getBounds(attributes.getNamedItem("g"));

        this.bounds.setUlx(this.bounds.getUlx() + 9);
        this.bounds.setUly(this.bounds.getUly() + 9);
        this.bounds.setWidth(30);
        this.bounds.setHeight(30);
    }

    @Override
    public String toJpdl() throws InvalidModelException {
        StringWriter jpdl = new StringWriter();
        jpdl.write("  <start");
        jpdl.write(JsonToJpdl.transformAttribute("name", name));

        if (bounds != null) {
            jpdl.write(bounds.toJpdl());
        } else {
            throw new InvalidModelException(
                    "Invalid Start Event. Bounds is missing.");
        }

        if (outgoings.size() > 0) {
            jpdl.write(" >\n");
            for (Transition t : outgoings) {
                jpdl.write(t.toJpdl());
            }
            jpdl.write("  </start>\n\n");
        } else {
            jpdl.write(" />\n\n");
        }

        return jpdl.toString();
    }

    @Override
    public JSONObject toJson() throws JSONException {

        JSONObject stencil = new JSONObject();
        stencil.put("id", "StartEvent");

        JSONArray outgoing = JpdlToJson.getTransitions(outgoings);

        JSONObject properties = new JSONObject();
        properties.put("bgcolor", "#ffffff");
        if (name != null)
            properties.put("name", name);

        JSONArray childShapes = new JSONArray();

        return JpdlToJson.createJsonObject(uuid, stencil, outgoing, properties,
                childShapes, bounds.toJson());
    }
}
