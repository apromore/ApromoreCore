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

@RootElement("NodeGraphicsInfos")
public class XPDLNodeGraphicsInfos extends XMLConvertible {

    @Element("NodeGraphicsInfo")
    protected ArrayList<XPDLNodeGraphicsInfo> nodeGraphicsInfos;

    public void add(XPDLNodeGraphicsInfo newNodeGraphicsInfos) {
        initializeNodeGraphicsInfos();

        getNodeGraphicsInfos().add(newNodeGraphicsInfos);
    }

    public XPDLNodeGraphicsInfo get(int index) {
        return nodeGraphicsInfos.get(index);
    }

    public ArrayList<XPDLNodeGraphicsInfo> getNodeGraphicsInfos() {
        return nodeGraphicsInfos;
    }

    public void readJSONgraphicsinfounknowns(JSONObject modelElement) throws JSONException {
        JSONObject passObject = new JSONObject();
        passObject.put("graphicsinfounknowns", modelElement.optString("graphicsinfounknowns"));
        getFirstGraphicsInfo().parse(passObject);
    }

    public void readJSONgraphicsinfosunknowns(JSONObject modelElement) {
        readUnknowns(modelElement, "graphicsinfosunknowns");
    }

    public void setNodeGraphicsInfos(ArrayList<XPDLNodeGraphicsInfo> newNodeGraphicsInfos) {
        this.nodeGraphicsInfos = newNodeGraphicsInfos;
    }

    public void writeJSONgraphicsinfosunknowns(JSONObject modelElement) throws JSONException {
        writeUnknowns(modelElement, "graphicsinfosunknowns");
    }

    public void writeJSONgraphicsinfo(JSONObject modelElement) {
        ArrayList<XPDLNodeGraphicsInfo> infos = getNodeGraphicsInfos();
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

    protected XPDLNodeGraphicsInfo getFirstGraphicsInfo() {
        return getNodeGraphicsInfos().get(0);
    }

    protected void initializeNodeGraphicsInfos() {
        if (getNodeGraphicsInfos() == null) {
            setNodeGraphicsInfos(new ArrayList<XPDLNodeGraphicsInfo>());
        }
    }
}
