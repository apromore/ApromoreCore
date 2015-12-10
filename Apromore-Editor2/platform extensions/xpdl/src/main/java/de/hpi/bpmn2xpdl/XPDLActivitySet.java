/*
 * Copyright © 2009-2015 The Apromore Initiative.
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
import java.util.Arrays;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.xmappr.Attribute;
import org.xmappr.Element;

public class XPDLActivitySet extends XPDLThing {

    @Attribute("AdHoc")
    protected String adHoc;
    @Attribute("AdHocOrdering")
    protected String adHocOrdering;
    @Attribute("AdHocCompletionCondition")
    protected String adHocCompletionCondition;

    @Element("Activities")
    protected XPDLActivities activities;
    @Element("Transitions")
    protected XPDLTransitions transitions;
    @Element("Associations")
    protected XPDLAssociations associations;
    @Element("Artifacts")
    protected XPDLArtifacts artifacts;

    public static boolean handlesStencil(String stencil) {
        String[] types = {
                "Subprocess",
                "CollapsedSubprocess"};
        return Arrays.asList(types).contains(stencil);
    }

    public void createAndDistributeMapping(Map<String, XPDLThing> mapping) {
        if (getActivities() != null) {
            getActivities().createAndDistributeMapping(mapping);
        }
        if (getTransitions() != null) {
            getTransitions().createAndDistributeMapping(mapping);
        }
    }

    public String getAdHoc() {
        return adHoc;
    }

    public String getAdHocOrdering() {
        return adHocOrdering;
    }

    public String getAdHocCompletionCondition() {
        return adHocCompletionCondition;
    }

    public XPDLActivities getActivities() {
        return activities;
    }

    public XPDLTransitions getTransitions() {
        return transitions;
    }

    public XPDLAssociations getAssociations() {
        return associations;
    }

    public XPDLArtifacts getArtifacts() {
        return artifacts;
    }

    public void readJSONadhoccompletioncondition(JSONObject modelElement) {
        setAdHocCompletionCondition(modelElement.optString("adhoccompletioncondition"));
    }

    public void readJSONadhocordering(JSONObject modelElement) {
        setAdHocOrdering(modelElement.optString("adhocordering"));
    }

    public void readJSONchildShapes(JSONObject modelElement) throws JSONException {
        JSONArray childShapes = modelElement.optJSONArray("childShapes");

        if (childShapes != null) {
            for (int i = 0; i < childShapes.length(); i++) {
                JSONObject childShape = childShapes.getJSONObject(i);
                String stencil = childShape.getJSONObject("stencil").getString("id");

                if (XPDLTransition.handlesStencil(stencil)) {
                    createTransition(childShape);
                } else if (XPDLActivity.handlesStencil(stencil)) {
                    createActivity(childShape);
                }else if (XPDLAssociation.handlesStencil(stencil)){
                    createAssociation(childShape);
                }else if (XPDLArtifact.handlesStencil(stencil)){
                    createArtifact(childShape);
                }
            }
        }
    }

    public void readJSONentry(JSONObject modelElement) {
    }

    public void readJSONid(JSONObject modelElement) {
        setId(getProperId(modelElement) + "-activitySet");
    }

    public void readJSONinputmaps(JSONObject modelElement) {
    }

    public void readJSONisadhoc(JSONObject modelElement) {
        setAdHoc(modelElement.optString("isadhoc"));
    }

    public void setAdHoc(String adHoc) {
        this.adHoc = adHoc;
    }

    public void setAdHocOrdering(String adHocOrdering) {
        this.adHocOrdering = adHocOrdering;
    }

    public void setAdHocCompletionCondition(String adHocCompletionCondition) {
        this.adHocCompletionCondition = adHocCompletionCondition;
    }

    public void setActivities(XPDLActivities activities) {
        this.activities = activities;
    }

    public void setTransitions(XPDLTransitions transitions) {
        this.transitions = transitions;
    }

    public void setAssociations(XPDLAssociations associations) {
        this.associations = associations;
    }

    public void setArtifacts(XPDLArtifacts artifacts) {
        this.artifacts = artifacts;
    }

    public void writeJSONactivities(JSONObject modelElement) {
        XPDLActivities activitiesList = getActivities();
        if (activitiesList != null) {
            activitiesList.write(modelElement);
        }
    }

    public void writeJSONadhoc(JSONObject modelElement) throws JSONException {
        putProperty(modelElement, "isadhoc", Boolean.parseBoolean(getAdHoc()));
    }

    public void writeJSONadhoccompletioncondition(JSONObject modelElement) throws JSONException {
        putProperty(modelElement, "adhoccompletioncondition", getAdHocCompletionCondition());
    }

    public void writeJSONadhocordering(JSONObject modelElement) throws JSONException {
        putProperty(modelElement, "adhocordering", getAdHocOrdering());
    }

    public void writeJSONtransitions(JSONObject modelElement) {
        XPDLTransitions transitionsList = getTransitions();
        if (transitionsList != null) {
            transitionsList.write(modelElement);
        }
    }

    public void writeJSONassociations(JSONObject modelElement) {
        XPDLAssociations associations1=getAssociations();
        if (associations1 != null) {
            associations1.write(modelElement);
        }
    }

    public void writeJSONartifacts(JSONObject modelElement) {
      XPDLArtifacts artifactsList=getArtifacts();
        if (artifactsList != null) {
           artifactsList.write(modelElement);
        }
    }

    public void writeUnmapped(JSONObject modelElement) throws JSONException {
        writeActivities(modelElement);
        writeTransitions(modelElement);
        writeAssociations(modelElement);
        writeArtifacts(modelElement);

    }

    protected void createActivity(JSONObject modelElement) {
        initializeActivities();

        XPDLActivity nextActivity = new XPDLActivity();
        nextActivity.setResourceIdToShape(getResourceIdToShape());
        nextActivity.parse(modelElement);
        getActivities().add(nextActivity);
    }

    protected void createTransition(JSONObject modelElement) {
        initializeTransitions();

        XPDLTransition nextTranistion = new XPDLTransition();
        nextTranistion.setResourceIdToShape(getResourceIdToShape());
        nextTranistion.parse(modelElement);
        getTransitions().add(nextTranistion);
    }

    protected void createAssociation(JSONObject modelElement) {
        initializeAssociations();

        XPDLAssociation nextAssociation = new XPDLAssociation();
        nextAssociation.setResourceIdToShape(getResourceIdToShape());
        nextAssociation.parse(modelElement);
        getAssociations().add(nextAssociation);
    }

    protected void initializeAssociations() {
        if (getAssociations() == null) {
            setAssociations(new XPDLAssociations());
        }
    }

    protected void createArtifact(JSONObject modelElement) {
        initializeArtifacts();

        XPDLArtifact nextArtifact = new XPDLArtifact();
        nextArtifact.setResourceIdToShape(getResourceIdToShape());
        nextArtifact.parse(modelElement);
        getArtifacts().add(nextArtifact);
    }

    protected void initializeArtifacts() {
        if (getArtifacts() == null) {
            setArtifacts(new XPDLArtifacts());
        }
    }


    protected void initializeActivities() {
        if (getActivities() == null) {
            setActivities(new XPDLActivities());
        }
    }

    protected void initializeChildShapes(JSONObject modelElement) throws JSONException {
        if (modelElement.optJSONArray("childShapes") == null) {
            modelElement.put("childShapes", new JSONArray());
        }
    }

    protected void initializeTransitions() {
        if (getTransitions() == null) {
            setTransitions(new XPDLTransitions());
        }
    }

    protected void writeActivities(JSONObject modelElement) throws JSONException {
        if (getActivities() != null) {
            ArrayList<XPDLActivity> activitiesList = getActivities().getActivities();
            if (activitiesList != null) {
                initializeChildShapes(modelElement);
                JSONArray childShapes = modelElement.getJSONArray("childShapes");

                for (int i = 0; i < activitiesList.size(); i++) {
                    JSONObject newActivity = new JSONObject();
                    XPDLActivity activity = activitiesList.get(i);
                    activity.write(newActivity);
                    childShapes.put(newActivity);
                }
            }
        }
    }

    protected void writeTransitions(JSONObject modelElement) throws JSONException {
        if (getTransitions() != null) {
            ArrayList<XPDLTransition> transitionsList = getTransitions().getTransitions();
            if (transitionsList != null) {
                initializeChildShapes(modelElement);
                JSONArray childShapes = modelElement.getJSONArray("childShapes");

                for (int i = 0; i < transitionsList.size(); i++) {
                    JSONObject newTransition = new JSONObject();
                    XPDLTransition transition = transitionsList.get(i);
                    transition.write(newTransition);
                    childShapes.put(newTransition);
                }
            }
        }
    }

    protected void writeAssociations(JSONObject modelElement) throws JSONException {
        if (getAssociations() != null) {
            ArrayList<XPDLAssociation> associationsList = getAssociations().getAssociations();
            if (associationsList != null) {
                initializeChildShapes(modelElement);
                JSONArray childShapes = modelElement.getJSONArray("childShapes");

                for (int i = 0; i < associationsList.size(); i++) {
                    JSONObject newAssociation = new JSONObject();
                    XPDLAssociation association = associationsList.get(i);
                    association.write(newAssociation);
                    childShapes.put(newAssociation);
                }
            }
        }
    }

    protected void writeArtifacts(JSONObject modelElement) throws JSONException {
        if (getArtifacts() != null) {
            ArrayList<XPDLArtifact> artifactsList = getArtifacts().getArtifacts();
            if (artifactsList != null) {
                initializeChildShapes(modelElement);
                JSONArray childShapes = modelElement.getJSONArray("childShapes");

                for (int i = 0; i < artifactsList.size(); i++) {
                    JSONObject newArtifact = new JSONObject();
                    XPDLArtifact artifact = artifactsList.get(i);
                    artifact.write(newArtifact);
                    childShapes.put(newArtifact);
                }
            }
        }
    }
}
