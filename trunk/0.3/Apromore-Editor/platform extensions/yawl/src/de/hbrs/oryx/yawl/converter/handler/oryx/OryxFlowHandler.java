/**
 * Copyright (c) 2011-2012 Felix Mannhardt, felix.mannhardt@smail.wir.h-brs.de
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.

 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.

 * You should have received a copy of the GNU Lesser General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 * 
 * See: http://www.gnu.org/licenses/lgpl-3.0
 * 
 */
package de.hbrs.oryx.yawl.converter.handler.oryx;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.oryxeditor.server.diagram.basic.BasicEdge;
import org.oryxeditor.server.diagram.basic.BasicShape;
import org.yawlfoundation.yawl.elements.YExternalNetElement;
import org.yawlfoundation.yawl.elements.YFlow;
import org.yawlfoundation.yawl.elements.YNet;

import de.hbrs.oryx.yawl.converter.context.OryxConversionContext;
import de.hbrs.oryx.yawl.converter.exceptions.ConversionException;

/**
 * Converts a Flow
 * 
 * @author Felix Mannhardt (Bonn-Rhein-Sieg University of Applied Sciences)
 * 
 */
public class OryxFlowHandler extends OryxShapeHandler {

	private final BasicShape netShape;
	private final BasicEdge edgeShape;

	public OryxFlowHandler(OryxConversionContext context, BasicEdge shape, BasicShape netShape) {
		super(context, shape);
		edgeShape = shape;
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

		YExternalNetElement incomingElement = net.getNetElement(incomingShape.getProperty("yawlid"));
		YExternalNetElement outgoingElement = net.getNetElement(outgoingShape.getProperty("yawlid"));

		YFlow flow = new YFlow(incomingElement, outgoingElement);
		flow.setDocumentation(edgeShape.getProperty("documentation"));

		try {
			convertFlowsInto(flow, incomingShape, outgoingShape);
		} catch (JSONException e) {
			getContext().addConversionWarnings("Can not convert flow predicates and ordering", e);
		} catch (ConversionException e) {
			getContext().addConversionWarnings(e);
		}

		outgoingElement.addPreset(flow);
	}

	private void convertFlowsInto(YFlow flow, BasicShape incomingShape, BasicShape outgoingShape) throws JSONException, ConversionException {
		if (incomingShape.hasProperty("flowsinto") && !incomingShape.getProperty("flowsinto").isEmpty()) {
			JSONObject flowsInto = lookUpFlowsInto(outgoingShape.getProperty("yawlid"), incomingShape);
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

	private JSONObject lookUpFlowsInto(String id, BasicShape shape) throws JSONException, ConversionException {
		JSONObject object = shape.getPropertyJsonObject("flowsinto");
		JSONArray items = object.getJSONArray("items");
		for (int index = 0; index < items.length(); index++) {
			JSONObject flowObj = items.getJSONObject(index);
			if (flowObj.getString("task").equals(id)) {
				return flowObj;
			}
		}
		throw new ConversionException("Could not find flow predicated for flow to: " + id);
	}

}
