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

import java.util.ArrayList;

import org.json.JSONException;
import org.json.JSONObject;
import org.xmappr.Element;
import org.xmappr.RootElement;

@RootElement("ConnectorGraphicsInfos")
public class XPDLConnectorGraphicsInfos extends XMLConvertible {

    @Element("ConnectorGraphicsInfo")
    protected ArrayList<XPDLConnectorGraphicsInfo> connectorGraphicsInfos;

    public void add(XPDLConnectorGraphicsInfo newConnectorGraphicsInfos) {
        initializeConnectorGraphicsInfos();

        getConnectorGraphicsInfos().add(newConnectorGraphicsInfos);
    }

    public XPDLConnectorGraphicsInfo get(int index) {
        return connectorGraphicsInfos.get(index);
    }

    public ArrayList<XPDLConnectorGraphicsInfo> getConnectorGraphicsInfos() {
        return connectorGraphicsInfos;
    }

    public void readJSONgraphicsinfounknowns(JSONObject modelElement) throws JSONException {
        JSONObject passObject = new JSONObject();
        passObject.put("graphicsinfounknowns", modelElement.optString("graphicsinfounknowns"));
        getFirstGraphicsInfo().parse(passObject);
    }

    public void readJSONgraphicsinfosunknowns(JSONObject modelElement) {
        readUnknowns(modelElement, "graphicsinfosunknowns");
    }

    public void setConnectorGraphicsInfos(ArrayList<XPDLConnectorGraphicsInfo> newConnectorGraphicsInfos) {
        this.connectorGraphicsInfos = newConnectorGraphicsInfos;
    }

    public void writeJSONgraphicsinfosunknowns(JSONObject modelElement) throws JSONException {
        writeUnknowns(modelElement, "graphicsinfosunknowns");
    }

    public void writeJSONgraphicsinfo(JSONObject modelElement) {
        ArrayList<XPDLConnectorGraphicsInfo> infos = getConnectorGraphicsInfos();
        if (infos != null) {
            for (int i = 0; i < infos.size(); i++) {
                if (infos.get(i).getToolId().equals("Oryx")) {
                    infos.get(i).write(modelElement);
                    break;
                }
            }
            infos.get(0).write(modelElement);
        }
    }

    protected XPDLConnectorGraphicsInfo getFirstGraphicsInfo() {
        return getConnectorGraphicsInfos().get(0);
    }

    protected void initializeConnectorGraphicsInfos() {
        if (getConnectorGraphicsInfos() == null) {
            setConnectorGraphicsInfos(new ArrayList<XPDLConnectorGraphicsInfo>());
        }
    }
}
