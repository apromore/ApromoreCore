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

import org.apache.commons.lang.StringEscapeUtils;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.StringWriter;

public class Arg {
    private IWireObjectGroup child = null;
    private String type;
    private String a_type = null;

    public Arg() {
        super();
    }

    public void setA_type(String aType) {
        a_type = aType;
    }

    public Arg(JSONObject arg) {
        try {
            type = arg.getString("type");
            if (type.toLowerCase().equals("string")) {
                String sName = arg.getString("name");
                String sValue = arg.getString("value");
                this.child = new WireString(sName, sValue);
            }
            if (type.toLowerCase().equals("int")) {
                String sName = arg.getString("name");
                String sValue = arg.getString("value");
                this.child = new WireInt(sName, sValue);
            }
            if (type.toLowerCase().equals("long")) {
                String sName = arg.getString("name");
                String sValue = arg.getString("value");
                this.child = new WireLong(sName, sValue);
            }
            if (type.toLowerCase().equals("float")) {
                String sName = arg.getString("name");
                String sValue = arg.getString("value");
                this.child = new WireFloat(sName, sValue);
            }
            if (type.toLowerCase().equals("double")) {
                String sName = arg.getString("name");
                String sValue = arg.getString("value");
                this.child = new WireDouble(sName, sValue);
            }
            if (type.toLowerCase().equals("true")) {
                String sName = arg.getString("name");
                String sValue = arg.getString("value");
                this.child = new WireTrue(sName, sValue);
            }
            if (type.toLowerCase().equals("false")) {
                String sName = arg.getString("name");
                String sValue = arg.getString("value");
                this.child = new WireFalse(sName, sValue);
            }

            if (type.toLowerCase().equals("object")) {
                String oName = arg.getString("value");
                this.child = new WireObjectType(oName);
            }
            if (arg.has("a_type")) {
                a_type = arg.getString("a_type");
            }
        } catch (JSONException e) {
        }
    }

    public String toJpdl() throws InvalidModelException {
        StringWriter jpdl = new StringWriter();
        jpdl.write("    <arg");
        if (a_type != null && a_type.length() > 0) {
            jpdl.write(" type=\"" + StringEscapeUtils.escapeXml(a_type) + "\">");
        } else {
            jpdl.write(">");
        }
        if (child != null) {
            jpdl.write(child.toJpdl());
        } else {
            throw new InvalidModelException("Invalid Arg. Object or String is missing");
        }
        jpdl.write("</arg>\n");
        return jpdl.toString();
    }

    public IWireObjectGroup getChild() {
        return child;
    }

    public void setChild(IWireObjectGroup child) {
        this.child = child;
    }
}
