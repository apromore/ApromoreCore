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
package de.hbrs.oryx.yawl.converter.handler.oryx;

import java.awt.geom.Point2D.Double;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.oryxeditor.server.diagram.Point;
import org.oryxeditor.server.diagram.basic.BasicEdge;
import org.oryxeditor.server.diagram.basic.BasicShape;
import org.yawlfoundation.yawl.editor.core.layout.LineStyle;
import org.yawlfoundation.yawl.editor.core.layout.YFlowLayout;
import org.yawlfoundation.yawl.editor.core.layout.YNetLayout;
import org.yawlfoundation.yawl.elements.YCondition;
import org.yawlfoundation.yawl.elements.YExternalNetElement;
import org.yawlfoundation.yawl.elements.YFlow;
import org.yawlfoundation.yawl.elements.YNet;
import org.yawlfoundation.yawl.elements.YTask;

import de.hbrs.oryx.yawl.converter.context.OryxConversionContext;
import de.hbrs.oryx.yawl.converter.exceptions.ConversionException;
import de.hbrs.oryx.yawl.util.YAWLMapping;

/**
 * Converts a Oryx Flow Shape to YAWL
 * 
 * @author Felix Mannhardt (Bonn-Rhein-Sieg University of Applied Sciences)
 * 
 */
public class OryxFlowHandler extends OryxShapeHandler {

    private final BasicShape netShape;
    private final BasicEdge edgeShape;

    public OryxFlowHandler(final OryxConversionContext context, final BasicEdge shape, final BasicShape netShape) {
        super(context, shape);
        this.edgeShape = shape;
        this.netShape = netShape;
    }

    /*
     * (non-Javadoc)
     * 
     * @see de.hbrs.oryx.yawl.converter.handler.oryx.OryxHandler#convert()
     */
    @Override
    public void convert() {
        YNet net = getContext().getNet(netShape);

        BasicShape incomingShape = getShape().getIncomingsReadOnly().get(0);
        BasicShape outgoingShape = getShape().getOutgoingsReadOnly().get(0);

        YExternalNetElement incomingElement = net.getNetElement(convertYawlId(net, incomingShape));
        YExternalNetElement outgoingElement = net.getNetElement(convertYawlId(net, outgoingShape));

        if (incomingElement == null || outgoingElement == null || incomingElement.getID() == null || outgoingElement.getID() == null) {
            getContext().addConversionWarnings(
                    new ConversionException("Missing source or target for Edge with ID " + getShape().getResourceId() + " in Net " + net.getID()));
            return;
        }

        YFlow flow = new YFlow(incomingElement, outgoingElement);
        flow.setDocumentation(edgeShape.getProperty("documentation"));

        try {
            convertFlowsInto(flow, incomingShape, outgoingShape, net);
        } catch (JSONException e) {
            getContext().addConversionWarnings("Can not convert flow predicates and ordering", e);
        } catch (ConversionException e) {
            getContext().addConversionWarnings(e);
        }

        convertFlowLayout(incomingElement, outgoingElement, net);

        outgoingElement.addPreset(flow);
    }

    private void convertFlowLayout(final YExternalNetElement incomingElement, final YExternalNetElement outgoingElement, final YNet net) {
        YNetLayout netLayout = getContext().getLayout().getNetLayout(net.getID());
        YFlowLayout flowLayout = new YFlowLayout(incomingElement, outgoingElement, getContext().getNumberFormat());
        flowLayout.setBounds(convertShapeBounds(getShape()));
        flowLayout.setLineStyle(convertLineStyle(getShape()));
        flowLayout.setSourcePort(convertPort(incomingElement, edgeShape.getDockerAt(0)));
        flowLayout.setPoints(convertDockers(edgeShape.getDockersReadOnly()));
        flowLayout.setTargetPort(convertPort(outgoingElement, edgeShape.getDockerAt(edgeShape.getNumDockers() - 1)));
        netLayout.addFlowLayout(flowLayout);
    }

    private LineStyle convertLineStyle(final BasicShape shape) {
        if (getShape().hasProperty("linestyle")) {
            int lineStyle = getShape().getPropertyInteger("linestyle");
            switch (lineStyle) {
            case 11:
                return LineStyle.Orthogonal;
            case 12:
                return LineStyle.Bezier;
            case 13:
                return LineStyle.Spline;
            }
        }
        return LineStyle.Orthogonal;
    }

    private List<Double> convertDockers(final List<Point> dockers) {
        List<Double> pointList = new ArrayList<Double>();
        // Omitting the first an last Docker
        for (int i = 0; i < dockers.size(); i++) {
            Point dockerPoint = dockers.get(i);
            pointList.add(new Double(dockerPoint.getX(), dockerPoint.getY()));
        }
        return pointList;
    }

    private int convertPort(final YExternalNetElement element, final Point magnetPoint) {

        if (element instanceof YCondition) {
            Integer port = YAWLMapping.getKeyByValue(YAWLMapping.CONDITION_PORT_MAP, magnetPoint);
            if (port != null) {
                return port;
            } else {
                return 14;
            }
        } else if (element instanceof YTask) {
            Integer port = YAWLMapping.getKeyByValue(YAWLMapping.TASK_PORT_MAP, magnetPoint);
            if (port != null) {
                return port;
            }
            port = YAWLMapping.getKeyByValue(YAWLMapping.BOTTOM_DECORATOR_PORT_MAP, magnetPoint);
            if (port != null) {
                return port;
            }
            port = YAWLMapping.getKeyByValue(YAWLMapping.TOP_DECORATOR_PORT_MAP, magnetPoint);
            if (port != null) {
                return port;
            }
            port = YAWLMapping.getKeyByValue(YAWLMapping.LEFT_DECORATOR_PORT_MAP, magnetPoint);
            if (port != null) {
                return port;
            }
            port = YAWLMapping.getKeyByValue(YAWLMapping.RIGHT_DECORATOR_PORT_MAP, magnetPoint);
            if (port != null) {
                return port;
            }
            return 14;
        } else {
            return 14;
        }
    }

    private void convertFlowsInto(final YFlow flow, final BasicShape incomingShape, final BasicShape outgoingShape, final YNet net) throws JSONException, ConversionException {
        if (incomingShape.hasProperty("flowsinto") && !incomingShape.getProperty("flowsinto").isEmpty()) {
            JSONObject flowsInto = lookUpFlowsInto(outgoingShape.getProperty("yawlid"), incomingShape, net);
            if (flowsInto.has("ordering")) {
                flow.setEvalOrdering(flowsInto.getInt("ordering"));
            }
            if (flowsInto.has("isdefault")) {
                flow.setIsDefaultFlow(flowsInto.getBoolean("isdefault"));
            }
            if (flowsInto.has("predicate")) {
                flow.setXpathPredicate(flowsInto.getString("predicate"));
            }
        }
    }

    private JSONObject lookUpFlowsInto(final String id, final BasicShape shape, final YNet net) throws JSONException, ConversionException {
        JSONObject object = shape.getPropertyJsonObject("flowsinto");
        JSONArray items = object.getJSONArray("items");
        for (int index = 0; index < items.length(); index++) {
            JSONObject flowObj = items.getJSONObject(index);
            if (flowObj.getString("task").equals(id) || (net.getID() + "-" + flowObj.getString("task")).equals(id)) {
                return flowObj;
            }
        }
        throw new ConversionException("Could not find flow predicated for flow to: " + id);
    }

}
