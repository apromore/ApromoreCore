/*
 * Copyright © 2009-2018 The Apromore Initiative.
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

import java.util.ArrayList;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.xmappr.Element;
import org.xmappr.RootElement;

@RootElement("Artifacts")
public class XPDLArtifacts extends XMLConvertible {

    @Element("Artifact")
    protected ArrayList<XPDLArtifact> artifacts;

    public void add(XPDLArtifact newArtifact) {
        initializeArtifacts();

        getArtifacts().add(newArtifact);
    }

    public void createAndDistributeMapping(Map<String, XPDLThing> mapping) {
        if (getArtifacts() != null) {
            for (XPDLThing thing : getArtifacts()) {
                thing.setResourceIdToObject(mapping);
                mapping.put(thing.getId(), thing);
            }
        }
    }

    public ArrayList<XPDLArtifact> getArtifacts() {
        return artifacts;
    }

    public void readJSONartifactsunknowns(JSONObject modelElement) {
        readUnknowns(modelElement, "artifactsunknowns");
    }

    public void setArtifacts(ArrayList<XPDLArtifact> artifacts) {
        this.artifacts = artifacts;
    }

    public void writeJSONartifactsunknowns(JSONObject modelElement) throws JSONException {
        writeUnknowns(modelElement, "artifactsunknowns");
    }

    public void writeJSONchildShapes(JSONObject modelElement) throws JSONException {
        ArrayList<XPDLArtifact> artifactList = getArtifacts();
        if (artifactList != null) {
            initializeChildShapes(modelElement);

            JSONArray childShapes = modelElement.getJSONArray("childShapes");
            for (int i = 0; i < artifactList.size(); i++) {
                JSONObject newArtifact = new JSONObject();
                artifactList.get(i).write(newArtifact);
                childShapes.put(newArtifact);
            }
        }
    }

    protected void initializeChildShapes(JSONObject modelElement) throws JSONException {
        if (modelElement.optJSONArray("childShapes") == null) {
            modelElement.put("childShapes", new JSONArray());
        }
    }

    protected void initializeArtifacts() {
        if (getArtifacts() == null) {
            setArtifacts(new ArrayList<XPDLArtifact>());
        }
    }
}
