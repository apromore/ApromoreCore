/*
 * Copyright © 2009-2016 The Apromore Initiative.
 *
 * This file is part of "Apromore".
 *
 * "Apromore" is free software; you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 3 of the
 * License, or (at your option) any later version.
 *
 * "Apromore" is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this program.
 * If not, see <http://www.gnu.org/licenses/lgpl-3.0.html>.
 */

package de.hpi.bpmn2xpdl;

import org.json.JSONException;
import org.json.JSONObject;
import org.xmappr.Attribute;
import org.xmappr.Element;
import org.xmappr.RootElement;

@RootElement("Object")
public class XPDLObject extends XMLConvertible {

    @Element("Categories")
    protected XPDLCategories categories;
    @Element("Documentation")
    protected XPDLDocumentation documentation;
    @Attribute("Id")
    protected String id;

    public XPDLCategories getCategories() {
        return categories;
    }

    public XPDLDocumentation getDocumentation() {
        return documentation;
    }

    public String getId() {
        return id;
    }

    public void readJSONcategories(JSONObject modelElement) throws JSONException {
        passInformationToCategories(modelElement, "categories");
    }

    public void readJSONcategoriesunknowns(JSONObject modelElement) throws JSONException {
        passInformationToCategories(modelElement, "categoriesunknowns");
    }

    public void readJSONcategoryunknowns(JSONObject modelElement) throws JSONException {
        passInformationToCategories(modelElement, "categoryunknowns");
    }

    public void readJSONdocumentation(JSONObject modelElement) throws JSONException {
        passInformationToDocumentation(modelElement, "documentation");
    }

    public void readJSONdocumentationunknowns(JSONObject modelElement) throws JSONException {
        passInformationToDocumentation(modelElement, "documentationunknowns");
    }

    public void readJSONid(JSONObject modelElement) {
        setId(modelElement.optString("id") + "-object");
    }

    public void readJSONobjectunknowns(JSONObject modelElement) {
        readUnknowns(modelElement, "objectunknowns");
    }

    public void setCategories(XPDLCategories categoriesList) {
        categories = categoriesList;
    }

    public void setDocumentation(XPDLDocumentation documentationValue) {
        documentation = documentationValue;
    }

    public void setId(String idValue) {
        id = idValue;
    }

    public void writeJSONcategories(JSONObject modelElement) {
        XPDLCategories catgoriesObject = getCategories();
        if (catgoriesObject != null) {
            catgoriesObject.write(modelElement);
        }
    }

    public void writeJSONdocumentation(JSONObject modelElement) {
        XPDLDocumentation doc = getDocumentation();
        if (doc != null) {
            doc.write(modelElement);
        }
    }

    public void writeJSONobjectunknowns(JSONObject modelElement) throws JSONException {
        writeUnknowns(modelElement, "objectunknowns");
    }

    protected void initializeCategories() {
        if (getCategories() == null) {
            setCategories(new XPDLCategories());
        }
    }

    protected void initializeDocumentation() {
        if (getDocumentation() == null) {
            setDocumentation(new XPDLDocumentation());
        }
    }

    protected void passInformationToCategories(JSONObject modelElement, String key) throws JSONException {
        initializeCategories();

        JSONObject passObject = new JSONObject();
        passObject.put("id", modelElement.optString("id"));
        passObject.put(key, modelElement.optString(key));

        getCategories().parse(passObject);
    }

    protected void passInformationToDocumentation(JSONObject modelElement, String key) throws JSONException {
        initializeDocumentation();

        JSONObject passObject = new JSONObject();
        passObject.put(key, modelElement.optString(key));
        getDocumentation().parse(passObject);
    }
}