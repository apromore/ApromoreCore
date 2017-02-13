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

import java.io.StringReader;
import java.io.StringWriter;
import java.util.HashMap;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.xmappr.Xmappr;

public class BPMN2XPDLConverter {

    protected XMLConvertible convertObject;

    public XMLConvertible getConvertObject() {
        return convertObject;
    }

    public String exportXPDL(String json) throws JSONException {
        JSONObject model = new JSONObject(json);
        HashMap<String, JSONObject> mapping = new HashMap<String, JSONObject>();
        constructResourceIdShapeMapping(model, mapping);

        XPDLPackage newPackage = new XPDLPackage();
        newPackage.setResourceIdToShape(mapping);
        newPackage.parse(model);

        StringWriter writer = new StringWriter();

        Xmappr xmappr = new Xmappr(XPDLPackage.class);
        xmappr.setPrettyPrint(true);
        xmappr.toXML(newPackage, writer);

        return "<?xml version=\"1.0\" encoding=\"UTF-8\"?>" + writer.toString();
    }

    public String importXPDL(String xml) {
        String parseXML = filterXMLString(xml);

        StringReader reader = new StringReader(parseXML);

        Xmappr xmappr = new Xmappr(XPDLPackage.class);
        XPDLPackage newPackage = (XPDLPackage) xmappr.fromXML(reader);
        newPackage.createAndDistributeMapping();

        JSONObject importObject = new JSONObject();
        newPackage.write(importObject);

        return importObject.toString();
    }

    public void setConvertObject(XMLConvertible toConvert) {
        convertObject = toConvert;
    }

    protected void constructResourceIdShapeMapping(JSONObject model, HashMap<String, JSONObject> mapping) {
        JSONArray childShapes = model.optJSONArray("childShapes");

        if (childShapes != null) {
            for (int i = 0; i < childShapes.length(); i++) {
                JSONObject childShape = childShapes.optJSONObject(i);
                if (childShape == null) {
                    continue;
                }
                mapping.put(childShape.optString("resourceId"), childShape);
                constructResourceIdShapeMapping(childShape, mapping);
            }
        }
    }

    private String filterXMLString(String xml) {
        //Remove xpdl2: from tags
        String firstTagFiltered = xml.replace("<xpdl2:", "<");
        firstTagFiltered = firstTagFiltered.replace("</xpdl2:", "</");

        //Remove xpdl: from tags
        String secondTagFiltered = firstTagFiltered.replace("<xpdl:", "<");
        secondTagFiltered = secondTagFiltered.replace("</xpdl:", "</");

        //Remove namespaces
        String nameSpaceFiltered = secondTagFiltered.replaceAll(" xmlns=\"[^\"]*\"", "");
        //Remove xml namespace lookalikes
        nameSpaceFiltered = nameSpaceFiltered.replaceAll(" \\w+:\\w+=\"[^\"]*\"", "");
        //Remove schemas
        String schemaFiltered = nameSpaceFiltered.replaceAll(" xsi=\"[^\"]*\"", "");
        //Remove starting xml tag
        String xmlTagFiltered = schemaFiltered.replaceAll("<\\?xml[^\\?]*\\?>\n?", "");
        return xmlTagFiltered;
    }
}
