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
package de.hbrs.oryx.yawl.converter.handler.yawl.element;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

import org.jdom2.Element;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.yawlfoundation.yawl.elements.YAtomicTask;
import org.yawlfoundation.yawl.elements.data.YParameter;

import de.hbrs.oryx.yawl.converter.context.YAWLConversionContext;
import de.hbrs.oryx.yawl.converter.exceptions.ConversionException;
import de.hbrs.oryx.yawl.converter.layout.NetElementLayout;
import de.hbrs.oryx.yawl.util.YAWLUtils;

/**
 * Converts a YAWL atomic task to a Oryx shape
 * 
 * @author Felix Mannhardt (Bonn-Rhein-Sieg University of Applied Sciences)
 * 
 */
public class AtomicTaskHandler extends TaskHandler {

    public AtomicTaskHandler(final YAWLConversionContext context, final YAtomicTask atomicTask) {
        super(context, atomicTask, "AtomicTask", atomicTask.getDecompositionPrototype());
    }

    /*
     * (non-Javadoc)
     * 
     * @see de.hbrs.oryx.yawl.converter.handler.yawl.element.TaskHandler# convertTaskProperties (de.hbrs.oryx.yawl.converter.layout.NetElementLayout)
     */
    @Override
    protected HashMap<String, String> convertTaskProperties(final NetElementLayout layout) {
        HashMap<String, String> properties = super.convertTaskProperties(layout);

        YAtomicTask task = (YAtomicTask) getNetElement();

        Element resourcingSpecs = task.getResourcingSpecs();
        properties.putAll(convertResourcing(resourcingSpecs));
        properties.putAll(convertDecompositionProperties());

        return properties;
    }

    /*
     * (non-Javadoc)
     * 
     * @see de.hbrs.oryx.yawl.converter.handler.yawl.decomposition.DecompositionHandler #convertDecompositionProperties()
     */
    @Override
    protected HashMap<String, String> convertDecompositionProperties() {
        HashMap<String, String> properties = super.convertDecompositionProperties();

        if (hasDecomposition()) {
            try {
                properties.put("decompositionvariables", convertDecompositionVariables());
            } catch (ConversionException e) {
                getContext().addConversionWarnings("Could not convert decomposition variables.", e);
            }
        }

        return properties;
    }

    private HashMap<String, String> convertResourcing(final Element resourcingSpecs) {
        HashMap<String, String> properties = new HashMap<String, String>();

        if (resourcingSpecs == null) {
            getContext().addConversionWarnings("No resourcing specification " + getNetElement().getID(), null);
            return properties;
        }

        Element offer = resourcingSpecs.getChild("offer", resourcingSpecs.getNamespace());
        if (offer != null) {
            properties.put("offerinitiator", offer.getAttributeValue("initiator"));
            properties.put("offerinteraction", YAWLUtils.elementToString(offer.getChildren()));
        }

        Element allocate = resourcingSpecs.getChild("allocate", resourcingSpecs.getNamespace());
        if (allocate != null) {
            properties.put("allocateinitiator", allocate.getAttributeValue("initiator"));
            properties.put("allocateinteraction", YAWLUtils.elementToString(allocate.getChildren()));
        }

        Element start = resourcingSpecs.getChild("start", resourcingSpecs.getNamespace());
        if (start != null) {
            properties.put("startinitiator", start.getAttributeValue("initiator"));
            properties.put("startinteraction", YAWLUtils.elementToString(start.getChildren()));
        }

        Element privileges = resourcingSpecs.getChild("privileges", resourcingSpecs.getNamespace());
        if (privileges != null) {
            properties.put("privileges", YAWLUtils.elementToString(privileges.getChildren()));
        }

        return properties;
    }

    private String convertDecompositionVariables() throws ConversionException {
        JSONObject variables = new JSONObject();
        JSONArray items = new JSONArray();

        List<YParameter> inputList = new ArrayList<YParameter>(getDecomposition().getInputParameters().values());
        Collections.sort(inputList);

        List<YParameter> outputList = new ArrayList<YParameter>(getDecomposition().getOutputParameters().values());
        Collections.sort(outputList);

        try {
            for (YParameter inputParam : inputList) {
                if (getDecomposition().getOutputParameterNames().contains(inputParam.getName())) {
                    items.put(convertParameter(inputParam, "inputandoutput"));
                } else {
                    items.put(convertParameter(inputParam, "input"));
                }
            }
            for (YParameter outputParam : outputList) {
                // Only if not already added as inputandoutput paramter
                if (!getDecomposition().getInputParameterNames().contains(outputParam.getName())) {
                    items.put(convertParameter(outputParam, "output"));
                }
            }
            variables.put("items", items);
        } catch (JSONException e) {
            throw new ConversionException(e);
        }

        return variables.toString();
    }

}
