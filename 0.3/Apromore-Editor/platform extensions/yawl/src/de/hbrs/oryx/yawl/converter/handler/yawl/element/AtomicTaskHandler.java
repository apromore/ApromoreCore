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
import org.yawlfoundation.yawl.elements.YAtomicTask;
import org.yawlfoundation.yawl.elements.YDecomposition;
import org.yawlfoundation.yawl.elements.data.YParameter;

import de.hbrs.oryx.yawl.converter.context.YAWLConversionContext;
import de.hbrs.oryx.yawl.converter.layout.NetElementLayout;

/**
 * Converts a YAWL atomic task to a Oryx shape
 * 
 * @author Felix Mannhardt (Bonn-Rhein-Sieg University of Applied Sciences)
 * 
 */
public class AtomicTaskHandler extends TaskHandler {

	public AtomicTaskHandler(YAWLConversionContext context,
			YAtomicTask atomicTask) {
		super(context, atomicTask, "AtomicTask");
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see de.hbrs.oryx.yawl.converter.handler.yawl.element.TaskHandler#
	 * convertTaskProperties
	 * (de.hbrs.oryx.yawl.converter.layout.NetElementLayout)
	 */
	@Override
	protected HashMap<String, String> convertTaskProperties(
			NetElementLayout layout) {
		HashMap<String, String> properties = super
				.convertTaskProperties(layout);
		
		YAtomicTask task = (YAtomicTask) getNetElement();
		
		final YDecomposition decomp = task.getDecompositionPrototype();
		if (decomp != null) {		
			properties.put("externalinteraction", decomp.requiresResourcingDecisions() ? "manual": "automated");
		}
		
		Element resourcingSpecs = task.getResourcingSpecs();
		if (resourcingSpecs != null) {
			properties.putAll(convertResourcing(resourcingSpecs));
		} else {
			getContext().addConversionWarnings(
					"No resourcing specification " + task.getID(), null);
		}
		
		final YDecomposition taskDecomposition = task.getDecompositionPrototype();

		if (taskDecomposition != null) {		
		
			try {
				properties.put("decompositionvariables",
						convertDecompositionVariables(taskDecomposition));
			} catch (JSONException e) {
				getContext().addConversionWarnings(
						"Could not convert Decomposition Variables "
								+ task.getID(), e);
			}		
		}
		
		return properties;
	}
	
	private HashMap<String, String> convertResourcing(Element resourcingSpecs) {
		HashMap<String, String> properties = new HashMap<String, String>();

		Element offer = resourcingSpecs.getChild("offer",
				resourcingSpecs.getNamespace());
		if (offer != null) {
			properties.put("offerinitiator",
					offer.getAttributeValue("initiator"));
			properties.put("offerinteraction",
					new XMLOutputter(Format.getPrettyFormat())
							.outputString(offer.getChildren()));
		}

		Element allocate = resourcingSpecs.getChild("allocate",
				resourcingSpecs.getNamespace());
		if (allocate != null) {
			properties.put("allocateinitiator",
					allocate.getAttributeValue("initiator"));
			properties.put("allocateinteraction",
					new XMLOutputter(Format.getPrettyFormat())
							.outputString(allocate.getChildren()));
		}

		Element start = resourcingSpecs.getChild("start",
				resourcingSpecs.getNamespace());
		if (start != null) {
			properties.put("startinitiator",
					start.getAttributeValue("initiator"));
			properties.put("startinteraction",
					new XMLOutputter(Format.getPrettyFormat())
							.outputString(start.getChildren()));
		}

		Element privileges = resourcingSpecs.getChild("privileges",
				resourcingSpecs.getNamespace());
		if (privileges != null) {
			properties.put("privileges",
					new XMLOutputter(Format.getPrettyFormat())
							.outputString(privileges.getChildren()));
		}

		return properties;
	}
	
	private String convertDecompositionVariables(
			YDecomposition taskDecomposition) throws JSONException {
		JSONObject variables = new JSONObject();
		JSONArray items = new JSONArray();
		for (YParameter inputParam : taskDecomposition.getInputParameters()
				.values()) {
			JSONObject inputVariable = new JSONObject();
			inputVariable.put("name", inputParam.getName());
			inputVariable.put("type", inputParam.getDataTypeName());
			if (taskDecomposition.getOutputParameterNames().contains(
					inputParam.getName())) {
				inputVariable.put("usage", "inputandoutput");
			} else {
				inputVariable.put("usage", "input");
			}
			inputVariable.put("initialvalue", inputParam.getInitialValue());
			items.put(inputVariable);
		}
		for (YParameter outputParam : taskDecomposition.getOutputParameters()
				.values()) {
			// Only if not already added as inputandoutput paramter
			if (!taskDecomposition.getInputParameterNames().contains(
					outputParam.getName())) {
				JSONObject outputVariable = new JSONObject();
				outputVariable.put("name", outputParam.getName());
				outputVariable.put("type", outputParam.getDataTypeName());
				outputVariable.put("usage", "output");
				outputVariable.put("initialvalue",
						outputParam.getInitialValue());
				items.put(outputVariable);
			}
		}
		variables.put("items", items);
		return variables.toString();
	}	


}
