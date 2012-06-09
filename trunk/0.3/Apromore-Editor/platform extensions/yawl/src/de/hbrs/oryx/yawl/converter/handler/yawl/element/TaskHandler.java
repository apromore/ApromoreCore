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
package de.hbrs.oryx.yawl.converter.handler.yawl.element;

import java.util.HashMap;

import org.jdom.Element;
import org.jdom.output.Format;
import org.jdom.output.XMLOutputter;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.oryxeditor.server.diagram.basic.BasicNode;
import org.oryxeditor.server.diagram.basic.BasicShape;
import org.yawlfoundation.yawl.elements.YDecomposition;
import org.yawlfoundation.yawl.elements.YExternalNetElement;
import org.yawlfoundation.yawl.elements.YFlow;
import org.yawlfoundation.yawl.elements.YNetElement;
import org.yawlfoundation.yawl.elements.YTask;
import org.yawlfoundation.yawl.elements.data.YParameter;

import de.hbrs.oryx.yawl.YAWLUtils;
import de.hbrs.oryx.yawl.converter.context.YAWLConversionContext;
import de.hbrs.oryx.yawl.converter.layout.NetElementLayout;
import de.hbrs.oryx.yawl.converter.layout.NetElementLayout.DecoratorType;

/**
 * Base class for all YAWL tasks
 * 
 * @author Felix Mannhardt (Bonn-Rhein-Sieg University of Applied Sciences)
 * 
 */
public abstract class TaskHandler extends NetElementHandler {

	public static final String NONE_CONNECTOR = "none";
	public static final String XOR_CONNECTOR = "xor";
	public static final String OR_CONNECTOR = "or";
	public static final String AND_CONNECTOR = "and";

	public static final String NONE_POSITION = "N";
	public static final String RIGHT_POSITION = "R";
	public static final String LEFT_POSITION = "L";
	public static final String BOTTOM_POSITION = "B";
	public static final String TOP_POSITION = "T";

	private String taskType;

	/**
	 * Constructs a basic Task Handler.
	 * 
	 * @param context
	 * @param netElement
	 *            the YAWL Task
	 * @param taskType
	 *            defines the StencilId that is created
	 */
	public TaskHandler(YAWLConversionContext context, YNetElement netElement,
			String taskType) {
		super(context, netElement);
		setTaskType(taskType);
		// TaskType should not be used here, as it may be overridden from a
		// subclass constructor
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
		BasicShape task = convertTask(parentId);

		getContext().putShape(parentId, getNetElement().getID(), task);
		getContext().addPostsetFlows(parentId,
				((YExternalNetElement) getNetElement()).getPostsetFlows());
	}

	private BasicShape convertTask(String netId) {
		NetElementLayout layout = getContext().getVertexLayout(netId,
				getTask().getID());
		BasicShape atomicTaskShape = new BasicNode(getTask().getID(),
				getTaskType());
		atomicTaskShape.setProperties(convertTaskProperties(layout));
		atomicTaskShape.setBounds(layout.getBounds());
		return atomicTaskShape;
	}

	/**
	 * Converting the properties of a net.<br/>
	 * CONTRACT: Sub-Classes have to call this method first, adding their
	 * properties afterwards.
	 * 
	 * @param layout
	 *            of the YAWL task read from layout XML
	 * @return Oryx Property HashMap
	 */
	protected HashMap<String, String> convertTaskProperties(
			NetElementLayout layout) {
		HashMap<String, String> properties = new HashMap<String, String>();
		properties.put("yawlid", getTask().getID());
		if (layout.hasJoinDecorator()) {
			properties.put(
					"join",
					convertConnectorType(getTask().getJoinType(),
							layout.getJoinDecorator()));
		}
		if (layout.hasSplitDecorator()) {
			properties.put(
					"split",
					convertConnectorType(getTask().getSplitType(),
							layout.getSplitDecorator()));
		}
		properties.put("name", getTask().getName().equals("null") ? ""
				: getTask().getName());

		final YDecomposition taskDecomposition = getTask()
				.getDecompositionPrototype();
		
		if (taskDecomposition != null) {
			try {
				properties.put("inputparameters",
						convertInputParamMappings(taskDecomposition));
			} catch (JSONException e) {
				getContext().addConversionWarnings(
						"Could not convert Input Parameters "
								+ getTask().getID(), e);
			}
			try {
				properties.put("outputparameters",
						convertOutputParamMappings(taskDecomposition));
			} catch (JSONException e) {
				getContext().addConversionWarnings(
						"Could not convert Output Parameters "
								+ getTask().getID(), e);
			}
		}

		// These properties does not need the DecompositionPrototype of a Task

		try {
			properties.put("cancelationset", convertCancelationSet(getTask()));
		} catch (JSONException e) {
			getContext()
					.addConversionWarnings(
							"Could not convert Cancelation Set "
									+ getTask().getID(), e);
		}

		try {
			properties.put("flowsinto", convertFlowsInto(getTask()));
		} catch (JSONException e) {
			getContext().addConversionWarnings(
					"Could not convert Flows Into " + getTask().getID(), e);
		}

		String iconPath = layout.getIconPath();
		if (iconPath != null && !iconPath.isEmpty()) {
			String[] splitPath = iconPath.split("/");
			String filename = splitPath[splitPath.length - 1];
			properties.put("icon", filename.substring(0, filename.length() - 4)
					.toLowerCase());
		}

		if (getTask().getCustomFormURL() != null) {
			properties.put("customform", getTask().getCustomFormURL()
					.toString());
		}

		if (getTask().getDecompositionPrototype() != null) {
			properties.put("externalinteraction", String.valueOf(getTask()
					.getDecompositionPrototype().requiresResourcingDecisions()));
		}			

		if (getTask().getDocumentation() != null
				&& !getTask().getDocumentation().isEmpty()) {
			properties.put("documentation", getTask().getDocumentation());
		}

		if (getTask().getConfiguration() != null) {
			properties.put("configuration", getTask().getConfiguration());
		}

		return properties;
	}



	private String convertFlowsInto(YTask task) throws JSONException {

		// Only convert flow predicates if there are more than 1 successors
		// (e.g. SPLIT)
		if (getTask().getPostsetFlows().size() <= 1) {
			return "";
		}

		JSONObject flowsInto = new JSONObject();
		JSONArray items = new JSONArray();
		for (YFlow flow : getTask().getPostsetFlows()) {
			JSONObject elementJSON = new JSONObject();
			elementJSON.put("task", YAWLUtils.getNextVisibleElement(flow));
			elementJSON.put("predicate", flow.getXpathPredicate());
			elementJSON.put("isdefault", flow.isDefaultFlow());
			items.put(elementJSON);
		}
		flowsInto.put("items", items);
		return flowsInto.toString();
	}

	private String convertCancelationSet(YTask task) throws JSONException {
		JSONObject cancelationSet = new JSONObject();
		JSONArray items = new JSONArray();
		for (YExternalNetElement element : getTask().getRemoveSet()) {
			JSONObject elementJSON = new JSONObject();
			if (YAWLUtils.isElementVisible(element)) {
				elementJSON.put("element", element.getID());
			} else {
				// A flow should be added to the CancelationSet
				YExternalNetElement previousElement = element
						.getPresetElements().iterator().next();
				YExternalNetElement nextElement = element.getPostsetElements()
						.iterator().next();
				elementJSON.put("element", previousElement.getID() + "|-|"
						+ nextElement.getID());
			}
			items.put(elementJSON);
		}
		cancelationSet.put("items", items);
		return cancelationSet.toString();
	}

	private String convertInputParamMappings(YDecomposition taskDecomposition)
			throws JSONException {
		JSONObject startingMappings = new JSONObject();
		JSONArray items = new JSONArray();
		for (String inputParamName : taskDecomposition.getInputParameterNames()) {
			JSONObject dataBinding = new JSONObject();
			dataBinding.put("taskvariable", inputParamName);
			dataBinding.put("expression", getTask()
					.getDataBindingForInputParam(inputParamName));
			items.put(dataBinding);
		}
		startingMappings.put("items", items);
		return startingMappings.toString();
	}

	private String convertOutputParamMappings(YDecomposition taskDecomposition)
			throws JSONException {
		JSONObject startingMappings = new JSONObject();
		JSONArray items = new JSONArray();
		for (String outputParamName : taskDecomposition
				.getOutputParameterNames()) {
			JSONObject dataBinding = new JSONObject();
			dataBinding.put("taskvariable", outputParamName);
			dataBinding.put("expression", getTask()
					.getDataBindingForOutputParam(outputParamName));
			items.put(dataBinding);
		}
		startingMappings.put("items", items);
		return startingMappings.toString();
	}



	private YTask getTask() {
		return (YTask) getNetElement();
	}

	private String convertConnectorType(int splitType,
			DecoratorType splitDecorator) {
		String connectorType = convertConnectorType(splitType);
		String connectorPosition = convertConnectorPosition(splitDecorator);

		if (!connectorType.equals(NONE_CONNECTOR)
				&& !connectorPosition.equals(NONE_CONNECTOR)) {
			return connectorType + connectorPosition;
		} else {
			return NONE_CONNECTOR;
		}
	}

	/**
	 * Convert the Position to exact one character
	 * 
	 * @param decorator
	 * @return
	 */
	private String convertConnectorPosition(DecoratorType decorator) {
		switch (decorator) {
		case TOP:
			return TOP_POSITION;

		case BOTTOM:
			return BOTTOM_POSITION;

		case LEFT:
			return LEFT_POSITION;

		case RIGHT:
			return RIGHT_POSITION;

		case NONE:
			return NONE_POSITION;
		}
		return NONE_POSITION;
	}

	private String convertConnectorType(int type) {
		switch (type) {
		case YTask._AND:
			return AND_CONNECTOR;

		case YTask._OR:
			return OR_CONNECTOR;

		case YTask._XOR:
			return XOR_CONNECTOR;

		default:
			return NONE_CONNECTOR;
		}
	}

	/**
	 * Set the StencilId of the Task
	 * 
	 * @param taskType
	 */
	public void setTaskType(String taskType) {
		this.taskType = taskType;
	}

	/**
	 * @return StencilId of Task
	 */
	public String getTaskType() {
		return taskType;
	}

}