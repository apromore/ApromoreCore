/**
 * Copyright (c) 2011-2012 Felix Mannhardt
 * 
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 * 
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 * 
 * See: http://www.opensource.org/licenses/mit-license.php
 * 
 */
package de.hbrs.oryx.yawl.converter.handler.yawl.decomposition;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.oryxeditor.server.diagram.Bounds;
import org.oryxeditor.server.diagram.basic.BasicNode;
import org.oryxeditor.server.diagram.basic.BasicShape;
import org.yawlfoundation.yawl.elements.YDecomposition;
import org.yawlfoundation.yawl.elements.YExternalNetElement;
import org.yawlfoundation.yawl.elements.YFlow;
import org.yawlfoundation.yawl.elements.YNet;
import org.yawlfoundation.yawl.elements.data.YParameter;
import org.yawlfoundation.yawl.elements.data.YVariable;

import de.hbrs.oryx.yawl.converter.context.YAWLConversionContext;
import de.hbrs.oryx.yawl.converter.handler.yawl.YAWLHandler;

/**
 * Converts a YAWL net to an Oryx diagram and calling the converters of all
 * child elements of the YAWL net.
 * 
 * @author Felix Mannhardt (Bonn-Rhein-Sieg University of Applied Sciences)
 * 
 */
public class NetHandler extends DecompositionHandler {

	public NetHandler(YAWLConversionContext context,
			YDecomposition decomposition) {
		super(context, decomposition);
	}

	protected YNet getNet() {
		return (YNet) getDecomposition();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * de.hbrs.oryx.yawl.converter.handler.yawl.YAWLHandler#convert(java.lang
	 * .String)
	 */
	@Override
	public void convert(String parentId) {

		final String netId = getNet().getID();

		final BasicShape netShape = new BasicNode(netId, "Net");
		netShape.setProperties(convertProperties());
		netShape.setBounds(getNetLayout(getNet()));
		getContext().addNet(netId, netShape);

		// Convert all children (NetElements) of root net
		for (Entry<String, YExternalNetElement> netElementEntry : getNet()
				.getNetElements().entrySet()) {
			YExternalNetElement yElement = netElementEntry.getValue();

			YAWLHandler netElementHandler = getContext().getHandlerFactory()
					.createYAWLConverter(yElement);
			netElementHandler.convert(netId);

		}

		// Convert all flows between the NetElements
		for (YFlow yFlow : getContext().getNetLayout(netId).getFlowSet()) {
			YAWLHandler flowHandler = getContext().getHandlerFactory()
					.createYAWLConverter(yFlow);
			flowHandler.convert(netId);
		}

	}

	/**
	 * Converting the properties of a net.<br/>
	 * CONTRACT: Sub-Classes have to call this method first, adding their
	 * properties afterwards.
	 * 
	 * @return Oryx Property HashMap
	 */
	protected HashMap<String, String> convertProperties() {
		HashMap<String, String> properties = new HashMap<String, String>();
		properties.put("name", getNet().getName() != null ? getNet().getName()
				: "");
		properties.put("yawlid", getNet().getID());
		properties.put("isrootnet", "false");
		try {
			properties.put("decompositionvariables",
					convertDecompositionVariables());
		} catch (JSONException e) {
			getContext().addConversionWarnings("Could not convert decomposition variables.", e);
		}
		return properties;
	}

	private String convertDecompositionVariables() throws JSONException {

		JSONObject variables = new JSONObject();
		JSONArray items = new JSONArray();
		final Map<String, YParameter> inputParameters = getNet()
				.getInputParameters();
		final Map<String, YParameter> outputParameters = getNet()
				.getOutputParameters();

		for (YParameter inputParam : inputParameters.values()) {
			JSONObject inputVariable = new JSONObject();
			inputVariable.put("name", inputParam.getName());
			inputVariable.put("type", inputParam.getDataTypeName());
			if (outputParameters.containsKey(inputParam.getName())) {
				inputVariable.put("usage", "inputandoutput");
			} else {
				inputVariable.put("usage", "input");
			}
			items.put(inputVariable);
		}
		for (YParameter outputParam : outputParameters.values()) {
			// Only if not already added as inputandoutput paramter
			if (!inputParameters.containsKey(outputParam.getName())) {
				JSONObject outputVariable = new JSONObject();
				outputVariable.put("name", outputParam.getName());
				outputVariable.put("type", outputParam.getDataTypeName());
				outputVariable.put("usage", "output");
				items.put(outputVariable);
			}
		}
		for (YVariable localVariables : getNet().getLocalVariables().values()) {
			// Only if not already added above
			if (!inputParameters.containsKey(localVariables.getName())
					&& !outputParameters.containsKey(localVariables.getName())) {
				JSONObject inputVariable = new JSONObject();
				inputVariable.put("name", localVariables.getName());
				inputVariable.put("type", localVariables.getDataTypeName());
				inputVariable.put("usage", "local");
				items.put(inputVariable);
			}
		}
		variables.put("items", items);
		return variables.toString();
	}

	private Bounds getNetLayout(YNet net) {
		return getContext().getNetLayout(net.getID()).getBounds();
	}

}