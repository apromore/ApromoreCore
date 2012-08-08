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

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Date;

import javax.xml.datatype.Duration;

import org.oryxeditor.server.diagram.basic.BasicShape;
import org.yawlfoundation.yawl.elements.YNet;
import org.yawlfoundation.yawl.elements.YTask;
import org.yawlfoundation.yawl.engine.time.YTimer;
import org.yawlfoundation.yawl.engine.time.YWorkItemTimer;
import org.jdom.Element;
import org.jdom.Namespace;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.yawlfoundation.yawl.util.StringUtil;

import de.hbrs.oryx.yawl.converter.context.OryxConversionContext;
import de.hbrs.oryx.yawl.converter.exceptions.ConversionException;
import de.hbrs.oryx.yawl.converter.handler.yawl.element.TaskHandler;
import de.hbrs.oryx.yawl.util.YAWLUtils;

/**
 * Abstract base class for all Task BasicShape conversions
 * 
 * @author Felix Mannhardt (Bonn-Rhein-Sieg University of Applied Sciences)
 * 
 */
public abstract class OryxTaskHandler extends OryxNetElementHandler {

	public OryxTaskHandler(OryxConversionContext context, BasicShape shape) {
		super(context, shape);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see de.hbrs.oryx.yawl.converter.handler.oryx.OryxHandler#convert()
	 */
	@Override
	public void convert() {

		final BasicShape shape = getShape();
		final YNet parentNet = getContext().getNet(shape.getParent());

		try {
			YTask task = createTask(convertYawlId(shape), parentNet);
			convertTaskProperties(task);
			parentNet.addNetElement(task);
		} catch (JSONException e) {
			getContext().addConversionWarnings("Could not convert Task", e);
		} catch (ConversionException e) {
			getContext().addConversionWarnings("Could not convert Task", e);
		}

		// Remember Flows for later conversion
		rememberOutgoings();
		rememberIncomings();

	}

	/**
	 * Convert all additional properties of this particular YTask instance.
	 * 
	 * @param shape
	 * @param task
	 */
	protected void convertTaskProperties(final YTask task) {

		BasicShape shape = getShape();

		task.setConfiguration(shape.getProperty("configuration"));
		task.setName(shape.getProperty("name"));

		try {
			if (shape.hasProperty("customform") && !shape.getProperty("customform").isEmpty()) {
				task.setCustomFormURI(new URL(shape.getProperty("customform")));
			}
		} catch (MalformedURLException e) {
			getContext().addConversionWarnings("Could not convert URL of CustomForm for Task " + task.getID(), e);
		}

		try {
			convertTimer(task);
		} catch (ConversionException e) {
			getContext().addConversionWarnings("Could not convert Timer for Task " + task.getID(), e);
		}

		try {
			convertCancelledBySet(task);
		} catch (JSONException e) {
			getContext().addConversionWarnings("Could not convert CancellationSet for Task " + task.getID(), e);
		}

		try {
			convertInputParamMappings(task);
		} catch (JSONException e) {
			getContext().addConversionWarnings("Could not convert Input Parameter Mappings for Task " + task.getID(), e);
		}

		try {
			convertOutputParamMappings(task);
		} catch (JSONException e) {
			getContext().addConversionWarnings("Could not convert Ouput Parameter Mappings for Task " + task.getID(), e);
		}

	}

	/**
	 * Subclass should create the YTask instance for this Task
	 * 
	 * @param taskId
	 *            already converted ID of the Task
	 * @param parentNet
	 *            parent YNet
	 * @return YTask
	 * @throws ConversionException
	 * @throws JSONException
	 */
	protected abstract YTask createTask(String taskId, YNet parentNet) throws JSONException, ConversionException;

	/**
	 * Return the YAWL connector type
	 * 
	 * @param oryxType
	 * @return connector type as specified in YTask
	 */
	protected int convertConnectorType(String oryxType, int noneType) {

		// NULL means no Decorator/Connector added on import
		if (oryxType == null || oryxType.equalsIgnoreCase(TaskHandler.NONE_CONNECTOR)) {
			// YAWL uses _XOR for NONE
			return noneType;
		}

		// Remove information about position (skip last character!)
		oryxType = oryxType.substring(0, oryxType.length() - 1);

		if (oryxType.equalsIgnoreCase(TaskHandler.AND_CONNECTOR)) {
			return YTask._AND;
		} else if (oryxType.equalsIgnoreCase(TaskHandler.OR_CONNECTOR)) {
			return YTask._OR;
		} else if (oryxType.equalsIgnoreCase(TaskHandler.XOR_CONNECTOR)) {
			return YTask._XOR;
		}
		return noneType; // YAWL uses _XOR for NONE
	}

	private void convertInputParamMappings(YTask task) throws JSONException {
		if (getShape().hasProperty("inputparameters")) {
			JSONObject parameters = getShape().getPropertyJsonObject("inputparameters");
			JSONArray expressionArray = parameters.getJSONArray("items");
			for (int i = 0; i < expressionArray.length(); i++) {
				JSONObject expression = expressionArray.getJSONObject(i);
				task.setDataBindingForInputParam(expression.getString("expression"), expression.getString("taskvariable"));
			}
		}
	}

	private void convertOutputParamMappings(YTask task) throws JSONException {
		if (getShape().hasProperty("outputparameters")) {
			JSONObject parameters = getShape().getPropertyJsonObject("outputparameters");
			JSONArray expressionArray = parameters.getJSONArray("items");
			for (int i = 0; i < expressionArray.length(); i++) {
				JSONObject expression = expressionArray.getJSONObject(i);
				String expressionValue = expression.has("expression") ? expression.getString("expression") : "";
				String expressionVariable = expression.getString("taskvariable");
				task.setDataBindingForOutputExpression(expressionValue, expressionVariable);
			}
		}
	}

	private void convertCancelledBySet(YTask task) throws JSONException {
		JSONArray cancelationSet = getShape().getPropertyJsonObject("cancelationset").getJSONArray("items");
		for (int i = 0; i < cancelationSet.length(); i++) {
			// Just add the Cancelled Task to RemoveSet of this Task, the
			// RemoveSet of all Tasks will be populated after all Tasks are
			// converted
			JSONObject jsonObject = cancelationSet.getJSONObject(i);
			if (jsonObject.has("element")) {
				getContext().addToCancellationSet(task, jsonObject.getString("element"));
			}
		}
	}

	private void convertTimer(YTask task) throws ConversionException {
		if (getShape().hasProperty("timer")) {
			Element root = YAWLUtils.parseToElement(getShape().getProperty("timer")).detachRootElement();
			Element artificalParent = new Element("task", YAWLUtils.YAWL_NS);
			artificalParent.addContent(root);
			// TODO replace this by some other code
			parseTimerParameters(task, artificalParent, root.getNamespace());
		}
	}

	/**
	 * Code copied from YDecompositionParser.java licensed under LGPL:
	 * 
	 * Copyright (c) 2004-2012 The YAWL Foundation. All rights reserved. The
	 * YAWL Foundation is a collaboration of individuals and organisations who
	 * are committed to improving workflow technology.
	 * 
	 * @param task
	 * @param taskElem
	 * @param _yawlNS
	 */
	private void parseTimerParameters(YTask task, Element taskElem, Namespace _yawlNS) {
		Element timerElem = taskElem.getChild("timer", _yawlNS);
		if (timerElem != null) {
			String netParam = timerElem.getChildText("netparam", _yawlNS);

			// net-level param holds values at runtime
			if (netParam != null)
				task.setTimerParameters(netParam);
			else {
				// get the triggering event
				String triggerStr = timerElem.getChildText("trigger", _yawlNS);
				YWorkItemTimer.Trigger trigger = YWorkItemTimer.Trigger.valueOf(triggerStr);

				// expiry is a stringified long value representing a specific
				// datetime
				String expiry = timerElem.getChildText("expiry", _yawlNS);
				if (expiry != null)
					task.setTimerParameters(trigger, new Date(new Long(expiry)));
				else {
					// duration type - specified as a Duration?
					String durationStr = timerElem.getChildText("duration", _yawlNS);
					if (durationStr != null) {
						Duration duration = StringUtil.strToDuration(durationStr);
						if (duration != null) {
							task.setTimerParameters(trigger, duration);
						}
					} else {
						// ticks / interval durationparams type
						Element durationElem = timerElem.getChild("durationparams", _yawlNS);
						String tickStr = durationElem.getChildText("ticks", _yawlNS);
						String intervalStr = durationElem.getChildText("interval", _yawlNS);
						YTimer.TimeUnit interval = YTimer.TimeUnit.valueOf(intervalStr);
						task.setTimerParameters(trigger, new Long(tickStr), interval);
					}
				}
			}
		}
	}

}
