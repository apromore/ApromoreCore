/*
 * Copyright © 2009-2017 The Apromore Initiative.
 *
 * This file is part of "Apromore".
 *
 * "Apromore" is free software; you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 3 of the
 * License, or (at your option) any later version.
 *
 * "Apromore" is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty
 * of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this program.
 * If not, see <http://www.gnu.org/licenses/lgpl-3.0.html>.
 */

package de.hpi.bpmn2xpdl;

import org.json.JSONException;
import org.json.JSONObject;
import org.xmappr.Attribute;
import org.xmappr.RootElement;
import org.xmappr.Text;

@RootElement("Category")
public class XPDLCategory extends XMLConvertible {

    @Attribute("Id")
    protected String id;
    @Text
    protected String content;

    public String getId() {
        return id;
    }

    public String getContent() {
        return content;
    }

    public void readJSONid(JSONObject modelElement) {
        setId(modelElement.optString("id") + "-category");
    }

    public void readJSONcategories(JSONObject modelElement) {
        setContent(modelElement.optString("categories"));
    }

    public void readJSONcategoryunknowns(JSONObject modelElement) {
        readUnknowns(modelElement, "categoryunknowns");
    }

    public void setId(String idValue) {
        id = idValue;
    }

    public void setContent(String contentValue) {
        content = contentValue;
    }

    public void writeJSONcategory(JSONObject modelElement) throws JSONException {
        modelElement.put("categories", getContent());
    }

    public void writeJSONcategoryunknowns(JSONObject modelElement) throws JSONException {
        writeUnknowns(modelElement, "categoryunknowns");
    }
}
