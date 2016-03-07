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

import org.jdom2.Element;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.oryxeditor.server.diagram.basic.BasicShape;
import org.yawlfoundation.yawl.elements.YAWLServiceGateway;
import org.yawlfoundation.yawl.elements.YAtomicTask;
import org.yawlfoundation.yawl.elements.YDecomposition;
import org.yawlfoundation.yawl.elements.YNet;
import org.yawlfoundation.yawl.elements.YTask;
import org.yawlfoundation.yawl.elements.data.YParameter;

import de.hbrs.oryx.yawl.converter.context.OryxConversionContext;
import de.hbrs.oryx.yawl.converter.exceptions.ConversionException;
import de.hbrs.oryx.yawl.util.YAWLUtils;

/**
 * Converts a Atomic Task
 * 
 * @author Felix Mannhardt (Bonn-Rhein-Sieg University of Applied Sciences)
 * 
 */
public class OryxAtomicTaskHandler extends OryxTaskHandler {

    public OryxAtomicTaskHandler(final OryxConversionContext context, final BasicShape shape) {
        super(context, shape);
    }

    /*
     * (non-Javadoc)
     * 
     * @see de.hbrs.oryx.yawl.converter.handler.oryx.OryxTaskHandler#createTask(java .lang.String, org.yawlfoundation.yawl.elements.YNet)
     */
    @Override
    protected YTask createTask(final String taskId, final YNet parentNet) throws JSONException, ConversionException {
        int joinType = convertConnectorType(getShape().getProperty("join"), YTask._XOR);
        int splitType = convertConnectorType(getShape().getProperty("split"), YTask._AND);

        YAtomicTask yAtomicTask = new YAtomicTask(taskId, joinType, splitType, parentNet);

        if (hasDecomposition()) {
            yAtomicTask.setDecompositionPrototype(createDecomposition(new YAWLServiceGateway(getDecompositionId(), getContext().getSpecification())));
        }
        return yAtomicTask;
    }

    /*
     * (non-Javadoc)
     * 
     * @see de.hbrs.oryx.yawl.converter.handler.oryx.OryxTaskHandler# convertTaskProperties(org.yawlfoundation.yawl.elements.YTask)
     */
    @Override
    protected void convertTaskProperties(final YTask task) {
        super.convertTaskProperties(task);

        try {
            convertResourcing((YAtomicTask) task);
        } catch (ConversionException e) {
            getContext().addConversionWarnings("Could not convert Resourcing for Task " + task.getID(), e);
        }

    }

    /*
     * (non-Javadoc)
     * 
     * @see de.hbrs.oryx.yawl.converter.handler.oryx.OryxTaskHandler#convertDecomposition (org.oryxeditor.server.diagram.basic.BasicShape)
     */
    @Override
    protected YDecomposition createDecomposition(final YDecomposition existingDecomposition) throws JSONException, ConversionException {
        YDecomposition decomposition = super.createDecomposition(existingDecomposition);
        convertDecompositionVariables(decomposition);
        return decomposition;
    }

    private void convertDecompositionVariables(final YDecomposition taskDecomposition) throws JSONException, ConversionException {
        if (getShape().hasProperty("decompositionvariables")) {
            JSONArray varArray = getShape().getPropertyJsonObject("decompositionvariables").getJSONArray("items");
            for (int i = 0; i < varArray.length(); i++) {
                JSONObject parameter = varArray.getJSONObject(i);
                convertSingleDecompositionVariables(taskDecomposition, i, parameter);

            }
        }
    }

    private void convertSingleDecompositionVariables(final YDecomposition decomposition, final int index, final JSONObject param) throws JSONException,
            ConversionException {
        String usage = param.getString("usage");

        YParameter convertParameter = convertParameter(decomposition, param);
        convertParameter.setOrdering(index);

        if (usage.equals("input")) {
            decomposition.addInputParameter(convertParameter);
        } else if (usage.equals("output")) {
            decomposition.addOutputParameter(convertParameter);
        } else {
            // Is Both
            param.put("usage", "input");
            YParameter inputParameter = convertParameter(decomposition, param);
            inputParameter.setOrdering(index);
            decomposition.addInputParameter(inputParameter);

            // Add both to Input and Output Parameters
            param.put("usage", "output");
            YParameter outputParameter = convertParameter(decomposition, param);
            outputParameter.setOrdering(index);
            decomposition.addOutputParameter(outputParameter);
        }
    }

    private void convertResourcing(final YAtomicTask task) throws ConversionException {

        String startInitiator = getShape().getProperty("startinitiator");
        String startInteraction = getShape().getProperty("startinteraction");
        String allocateInitiator = getShape().getProperty("allocateinitiator");
        String allocateInteraction = getShape().getProperty("allocateinteraction");
        String offerInitiator = getShape().getProperty("offerinitiator");
        String offerInteraction = getShape().getProperty("offerinteraction");
        String privilegesSource = getShape().getProperty("privileges");

        Element resourcingSpecs = new Element("resourcing", YAWLUtils.YAWL_NS);

        // Add in order Offer, Allocate, Start, Privileges to ensure exact same
        // result as on import
        if (offerInteraction != null) {
            Element offer = YAWLUtils.parseToElement("<offer xmlns=\"" + YAWLUtils.YAWL_NS + "\">" + offerInteraction + "</offer>")
                    .detachRootElement();
            if (offerInitiator != null) {
                offer.setAttribute("initiator", offerInitiator);
            }
            resourcingSpecs.addContent(offer);
        }

        if (allocateInteraction != null) {
            Element allocate = YAWLUtils.parseToElement("<allocate xmlns=\"" + YAWLUtils.YAWL_NS + "\">" + allocateInteraction + "</allocate>")
                    .detachRootElement();
            if (allocateInitiator != null) {
                allocate.setAttribute("initiator", allocateInitiator);
            }
            resourcingSpecs.addContent(allocate);
        }

        if (startInteraction != null) {
            Element start = YAWLUtils.parseToElement("<start xmlns=\"" + YAWLUtils.YAWL_NS + "\">" + startInteraction + "</start>")
                    .detachRootElement();
            if (startInitiator != null) {
                start.setAttribute("initiator", startInitiator);
            }
            resourcingSpecs.addContent(start);
        }

        Element privileges = new Element("privileges", YAWLUtils.YAWL_NS);
        if (privilegesSource != null && !privilegesSource.isEmpty()) {
            privileges.addContent(YAWLUtils.parseToElement("<privileges>" + privilegesSource + "</privileges>").getRootElement().cloneContent());
            resourcingSpecs.addContent(privileges);
        }

        if (resourcingSpecs.getChildren().size() > 0) {
            task.setResourcingSpecs(resourcingSpecs);
        }
    }
}
