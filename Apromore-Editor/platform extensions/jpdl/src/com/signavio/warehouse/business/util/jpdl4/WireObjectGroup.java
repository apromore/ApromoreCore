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

import org.json.JSONException;
import org.json.JSONObject;

import java.io.StringWriter;

public class WireObjectGroup implements IWireObjectGroup {

    protected String name;
    protected String value;
    protected String elementName;

    protected WireObjectGroup(String name, String value, String elementName) {
        this.name = name;
        this.value = value;
        this.elementName = elementName;
    }

    public WireObjectGroup(String name, String value) {
        throw new RuntimeException("Not allowed! Use WireObjectGroup(String name, String value, String elementName)");
    }


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    /* (non-Javadoc)
      * @see com.signavio.warehouse.business.util.jpdl4.IWireObjectGroup#toJpdl()
      */
    public String toJpdl() {

        StringWriter jpdl = new StringWriter();
        jpdl.write("<" + elementName + " ");

        if (name != null && !name.isEmpty()) {
            jpdl.write(JsonToJpdl.transformAttribute("name", name));
        }

        if (value != null && !value.isEmpty()) {
            jpdl.write(JsonToJpdl.transformAttribute("value", value));
        }

        jpdl.write(" />");

        return jpdl.toString();
    }

    /* (non-Javadoc)
      * @see com.signavio.warehouse.business.util.jpdl4.IWireObjectGroup#toJson()
      */
    public JSONObject toJson() throws JSONException {
        JSONObject string = new JSONObject();
        if (name != null)
            string.put("name", name);
        if (value != null)
            string.put("value", value);
        string.put("type", elementName);
        return string;
    }
}
