package de.hpi.petrinet.serialization;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import de.hpi.petrinet.FlowRelationship;
import de.hpi.petrinet.LabeledTransition;
import de.hpi.petrinet.Node;
import de.hpi.petrinet.PetriNet;
import de.hpi.petrinet.Place;
import de.hpi.petrinet.Transition;

public class PetriNetJSONExporter {

    private static PetriNetJSONExporter eINSTANCE = null;

    public static PetriNetJSONExporter getInstance() {
        if (eINSTANCE == null)
            eINSTANCE = new PetriNetJSONExporter();

        return eINSTANCE;
    }

    protected PetriNetJSONExporter() {

    }

    public JSONObject getJSONForPetriNet(PetriNet pn) {

        JSONObject json = new JSONObject();

        int id = 0;
        for (Node n : pn.getNodes()) {
            n.setResourceId(String.valueOf(id));
            id++;
        }
        for (FlowRelationship f : pn.getFlowRelationships()) {
            f.setResourceId(String.valueOf(id));
            id++;
        }

        try {

            JSONArray shapeArray = new JSONArray();

            for (Node n : pn.getNodes())
                shapeArray.put(getJSONForNode(pn, n));

            for (FlowRelationship f : pn.getFlowRelationships())
                shapeArray.put(getJSONForFlow(f));

            json.put("resourceId", "oryx-canvas123");
            json.put("properties", getJSONProperties());
            json.put("stencil", getJSONStencil());
            json.put("childShapes", shapeArray);
            json.put("bounds", getJSONBoundsForNet());
            json.put("stencilset", getJSONStencilSet());
            json.put("ssextensions", new JSONArray());

        } catch (JSONException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        return json;

    }

    protected JSONObject getJSONForFlow(FlowRelationship f) throws JSONException {
        JSONObject nodeObject = new JSONObject();
        nodeObject.put("resourceId", f.getResourceId());

        JSONObject nodeProperties = new JSONObject();
        nodeProperties.put("id", "");
        nodeProperties.put("title", "");
        nodeProperties.put("transformation", "");
        nodeObject.put("properties", nodeProperties);

        nodeObject.put("stencil", new JSONObject("{\"id\":\"Arc\"}"));
        nodeObject.put("childShapes", new JSONArray());

        JSONArray outArray = new JSONArray();
        outArray.put(new JSONObject("{\"resourceId\":\"" + f.getTarget().getResourceId() + "\"}"));

        nodeObject.put("outgoing", outArray);

        JSONObject boundsObject = new JSONObject();
        JSONObject lowerRight = new JSONObject();
        JSONObject upperLeft = new JSONObject();

        lowerRight.put("x", 0);
        lowerRight.put("y", 0);
        upperLeft.put("x", 0);
        upperLeft.put("y", 0);

        boundsObject.put("lowerRight", lowerRight);
        boundsObject.put("upperLeft", upperLeft);

        nodeObject.put("bounds", boundsObject);

        JSONArray dockerArray = new JSONArray();

        int width = f.getSource().getBounds().getX2() - f.getSource().getBounds().getX1();
        int height = f.getSource().getBounds().getY2() - f.getSource().getBounds().getY1();
        dockerArray.put(new JSONObject("{\"x\":" + width / 2 + ",\"y\":" + height / 2 + "}"));

        width = f.getTarget().getBounds().getX2() - f.getTarget().getBounds().getX1();
        height = f.getTarget().getBounds().getY2() - f.getTarget().getBounds().getY1();
        dockerArray.put(new JSONObject("{\"x\":" + width / 2 + ",\"y\":" + height / 2 + "}"));

        nodeObject.put("dockers", dockerArray);
        nodeObject.put("target", new JSONObject("{\"resourceId\":\"" + f.getTarget().getResourceId() + "\"}"));

        return nodeObject;
    }

    protected JSONObject getJSONForNodeProperties(PetriNet pn, Node n) throws JSONException {
        JSONObject nodeProperties = new JSONObject();

        nodeProperties.put("id", n.getId());
        if (n instanceof Transition) {

            if (n instanceof LabeledTransition)
                nodeProperties.put("title", ((LabeledTransition) n).getLabel());
            else
                nodeProperties.put("title", "");

            nodeProperties.put("firetype", "Automatic");
            nodeProperties.put("href", "");
            nodeProperties.put("omodel", "");
            nodeProperties.put("oform", "");
            nodeProperties.put("guard", "");
            nodeProperties.put("communicationchannel", "");
            nodeProperties.put("communicationtype", "Default");
        } else if (n instanceof Place) {
            nodeProperties.put("title", "");

            if (pn.getInitialMarking() == null) {
                nodeProperties.put("numberoftokens", "");
                nodeProperties.put("numberoftokens_text", "");
                nodeProperties.put("numberoftokens_drawing", "");
            } else {
                nodeProperties.put("numberoftokens", pn.getInitialMarking().getNumTokens((Place) n));
                nodeProperties.put("numberoftokens_text", pn.getInitialMarking().getNumTokens((Place) n));
                nodeProperties.put("numberoftokens_drawing", pn.getInitialMarking().getNumTokens((Place) n));
            }

            nodeProperties.put("external", false);
            nodeProperties.put("exttype", "Push");
            nodeProperties.put("href", "");
            nodeProperties.put("locatornames", "");
            nodeProperties.put("locatortypes", "");
            nodeProperties.put("locatorexpr", "");
        }

        return nodeProperties;
    }


    protected JSONObject getJSONForNode(PetriNet pn, Node n) throws JSONException {
        JSONObject nodeObject = new JSONObject();
        nodeObject.put("resourceId", n.getResourceId());

        if (n instanceof Transition) {
            if (n instanceof LabeledTransition)
                nodeObject.put("stencil", new JSONObject("{\"id\":\"Transition\"}"));
            else
                nodeObject.put("stencil", new JSONObject("{\"id\":\"VerticalEmptyTransition\"}"));
        } else if (n instanceof Place) {
            nodeObject.put("stencil", new JSONObject("{\"id\":\"Place\"}"));
        } else {
            nodeObject.put("stencil", new JSONObject("{\"id\":\"\"}"));
        }

        nodeObject.put("properties", getJSONForNodeProperties(pn, n));
        nodeObject.put("childShapes", new JSONArray());

        JSONArray outArray = new JSONArray();

        for (FlowRelationship f : n.getOutgoingFlowRelationships())
            outArray.put(new JSONObject("{\"resourceId\":\"" + f.getResourceId() + "\"}"));

        nodeObject.put("outgoing", outArray);

        nodeObject.put("bounds", this.getJSONBounds(n.getBounds()));
        nodeObject.put("dockers", new JSONArray());

        return nodeObject;
    }


    protected JSONObject getJSONBoundsForNet() throws JSONException {
        JSONObject bounds = new JSONObject();
        bounds.put("lowerRight", new JSONObject("{\"x\":3000,\"y\":1500}"));
        bounds.put("upperLeft", new JSONObject("{\"x\":0,\"y\":0}"));
        return bounds;
    }

    protected JSONObject getJSONStencil() throws JSONException {
        JSONObject stencil = new JSONObject();
        stencil.put("id", "Diagram");
        return stencil;
    }

    protected JSONObject getJSONStencilSet() throws JSONException {
        JSONObject stencilSet = new JSONObject();
        stencilSet.put("url", "/oryx///stencilsets/petrinets/petrinet.json");
        stencilSet.put("namespace", "http://b3mn.org/stencilset/petrinet#");
        return stencilSet;
    }


    protected JSONObject getJSONProperties() throws JSONException {
        JSONObject properties = new JSONObject();
        properties.put("title", "");
        properties.put("engine", false);
        properties.put("version", "");
        properties.put("author", "");
        properties.put("language", "English");
        properties.put("creationdate", "");
        properties.put("modificationdate", "");
        properties.put("documentation", "");
        return properties;
    }

    protected JSONObject getJSONBounds(de.hpi.util.Bounds bounds) throws JSONException {
        if (bounds != null) {
            JSONObject boundsObject = new JSONObject();
            JSONObject lowerRight = new JSONObject();
            JSONObject upperLeft = new JSONObject();

            lowerRight.put("x", bounds.getX1());
            lowerRight.put("y", bounds.getY1());

            upperLeft.put("x", bounds.getX2());
            upperLeft.put("y", bounds.getY2());

            boundsObject.put("lowerRight", lowerRight);
            boundsObject.put("upperLeft", upperLeft);

            return boundsObject;
        }

        return new JSONObject();
    }

}
