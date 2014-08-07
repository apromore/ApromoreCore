/*
 * Copyright © 2009-2014 The Apromore Initiative.
 *
 * This file is part of “Apromore”.
 *
 * “Apromore” is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 3 of the License, or
 * (at your option) any later version.
 *
 * “Apromore” is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.
 * If not, see <http://www.gnu.org/licenses/lgpl-3.0.html>.
 */
package de.hbrs.oryx.yawl.converter.handler.yawl.element;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;

import org.jdom2.Element;
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

import de.hbrs.oryx.yawl.converter.context.YAWLConversionContext;
import de.hbrs.oryx.yawl.converter.exceptions.ConversionException;
import de.hbrs.oryx.yawl.converter.layout.NetElementLayout;
import de.hbrs.oryx.yawl.converter.layout.NetElementLayout.DecoratorType;
import de.hbrs.oryx.yawl.util.YAWLUtils;

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
     * @param decomposition
     */
    public TaskHandler(final YAWLConversionContext context, final YNetElement netElement, final String taskType, final YDecomposition decomposition) {
        super(context, netElement, decomposition);
        setTaskType(taskType);
        // TaskType should not be used here, as it may be overridden from a
        // subclass constructor
    }

    /*
     * (non-Javadoc)
     *
     * @see de.hbrs.oryx.yawl.converter.handler.yawl.YAWLHandler#convert(java.lang .String)
     */
    @Override
    public void convert(final String parentId) {
        BasicShape task = convertTask(parentId);
        getContext().putShape(parentId, getNetElement().getID(), task);
        super.convert(parentId);
    }

    private BasicShape convertTask(final String netId) {
        NetElementLayout layout = getContext().getVertexLayout(netId, getTask().getID());
        if (layout != null) {
            BasicShape atomicTaskShape = new BasicNode(getTask().getID(), getTaskType());
            atomicTaskShape.setProperties(convertTaskProperties(layout));
            atomicTaskShape.setBounds(layout.getBounds());
            return atomicTaskShape;
        } else {
            getContext().addConversionWarnings(new ConversionException("Missing layout for task "+ getTask().getID()));
            return new BasicNode(getTask().getID(), getTaskType());
        }
    }

    /**
     * Converting the properties of a net.<br/>
     * CONTRACT: Sub-Classes have to call this method first, adding their properties afterwards.
     *
     * @param layout
     *            of the YAWL task read from layout XML
     * @return Oryx Property HashMap
     */
    protected HashMap<String, String> convertTaskProperties(final NetElementLayout layout) {
        HashMap<String, String> properties = new HashMap<String, String>();
        properties.put("yawlid", getTask().getID());
        if (layout.hasJoinDecorator()) {
            properties.put("join", convertConnectorType(getTask().getJoinType(), layout.getJoinDecorator()));
        }
        if (layout.hasSplitDecorator()) {
            properties.put("split", convertConnectorType(getTask().getSplitType(), layout.getSplitDecorator()));
        }

        properties.put("name", getTask().getName());

        if (getTask().getDocumentation() != null && !getTask().getDocumentation().isEmpty()) {
            properties.put("documentation", getTask().getDocumentation());
        }

        try {
            properties.put("cancelationset", convertCancelationSet(getTask()));
        } catch (JSONException e) {
            getContext().addConversionWarnings("Could not convert Cancelation Set " + getTask().getID(), e);
        }

        try {
            properties.put("flowsinto", convertFlowsInto(getTask()));
        } catch (JSONException e) {
            getContext().addConversionWarnings("Could not convert Flows Into " + getTask().getID(), e);
        }

        String iconPath = layout.getIconPath();
        if (iconPath != null && !iconPath.isEmpty()) {
            String[] splitPath = iconPath.split("/");
            String filename = splitPath[splitPath.length - 1];
            properties.put("icon", filename.substring(0, filename.length() - 4));
        }

        if (getTask().getCustomFormURL() != null) {
            properties.put("customform", getTask().getCustomFormURL().toString());
        }

        // The name ConfigurationElement is misleading, it contains the whole
        // Task XML
        Element configurationElement = getTask().getConfigurationElement();
        if (configurationElement != null) {
            Element realConfigurationElement = configurationElement.getChild("configuration", configurationElement.getNamespace());
            if (realConfigurationElement != null) {
                properties.put("configuration", YAWLUtils.elementToString(realConfigurationElement));
            }
        }

        if (getTask().getTimeParameters() != null) {
            properties.put("timer", getTask().timerParamsToXML());
        }

        if (hasDecomposition()) {
            // Only needs decomposition as lookup, does not really convert
            // information of the decomposition

            try {
                properties.put("inputparameters", convertInputParamMappings());
            } catch (JSONException e) {
                getContext().addConversionWarnings("Could not convert Input Parameters " + getTask().getID(), e);
            }
            try {
                properties.put("outputparameters", convertOutputParamMappings());
            } catch (JSONException e) {
                getContext().addConversionWarnings("Could not convert Output Parameters " + getTask().getID(), e);
            }
        }

        return properties;
    }

    /**
     * Does sort the Flows with built-in Java Collections.sort. As JDOM does not use generics, it is unchecked!
     */
    @SuppressWarnings("unchecked")
    private String convertFlowsInto(final YTask task) throws JSONException {

        // Only convert flow predicates if there are more than 1 successors
        // (e.g. SPLIT)
        if (getTask().getPostsetFlows().size() <= 1) {
            return "";
        }

        JSONObject flowsInto = new JSONObject();
        JSONArray items = new JSONArray();
        ArrayList<YFlow> flowList = new ArrayList<YFlow>(getTask().getPostsetFlows());
        Collections.sort(flowList);
        for (YFlow flow : flowList) {
            JSONObject elementJSON = new JSONObject();
            elementJSON.put("task", YAWLUtils.getNextVisibleElement(flow).getID());
            elementJSON.put("predicate", flow.getXpathPredicate());
            elementJSON.put("isdefault", flow.isDefaultFlow());
            elementJSON.put("ordering", flow.getEvalOrdering());
            items.put(elementJSON);
        }
        flowsInto.put("items", items);
        return flowsInto.toString();
    }

    private String convertCancelationSet(final YTask task) throws JSONException {
        JSONObject cancelationSet = new JSONObject();
        JSONArray items = new JSONArray();
        for (YExternalNetElement element : getTask().getRemoveSet()) {
            JSONObject elementJSON = new JSONObject();
            if (YAWLUtils.isElementVisible(element)) {
                elementJSON.put("element", element.getID());
            } else {
                // A flow should be added to the CancelationSet
                YExternalNetElement previousElement = element.getPresetElements().iterator().next();
                YExternalNetElement nextElement = element.getPostsetElements().iterator().next();
                elementJSON.put("element", previousElement.getID() + "|-|" + nextElement.getID());
            }
            items.put(elementJSON);
        }
        cancelationSet.put("items", items);
        return cancelationSet.toString();
    }

    private String convertInputParamMappings() throws JSONException {
        JSONObject startingMappings = new JSONObject();
        JSONArray items = new JSONArray();
        for (String inputParamName : getDecomposition().getInputParameterNames()) {
            JSONObject dataBinding = new JSONObject();
            dataBinding.put("taskvariable", inputParamName);
            dataBinding.put("expression", getTask().getDataBindingForInputParam(inputParamName));
            items.put(dataBinding);
        }
        startingMappings.put("items", items);
        return startingMappings.toString();
    }

    private String convertOutputParamMappings() throws JSONException {
        JSONObject startingMappings = new JSONObject();
        JSONArray items = new JSONArray();
        for (String outputParamName : getTask().getParamNamesForTaskCompletion()) {
            JSONObject dataBinding = new JSONObject();
            dataBinding.put("taskvariable", outputParamName);
            dataBinding.put("expression", getTask().getDataBindingForOutputParam(outputParamName));
            items.put(dataBinding);
        }
        startingMappings.put("items", items);
        return startingMappings.toString();
    }

    private YTask getTask() {
        return (YTask) getNetElement();
    }

    private String convertConnectorType(final int splitType, final DecoratorType splitDecorator) {
        String connectorType = convertConnectorType(splitType);
        String connectorPosition = convertConnectorPosition(splitDecorator);

        if (!connectorType.equals(NONE_CONNECTOR) && !connectorPosition.equals(NONE_CONNECTOR)) {
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
    private String convertConnectorPosition(final DecoratorType decorator) {
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

    private String convertConnectorType(final int type) {
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
    public void setTaskType(final String taskType) {
        this.taskType = taskType;
    }

    /**
     * @return StencilId of Task
     */
    public String getTaskType() {
        return taskType;
    }

}