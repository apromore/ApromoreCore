/*
 * Copyright © 2009-2015 The Apromore Initiative.
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

import java.awt.Rectangle;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Date;

import javax.xml.datatype.Duration;

import org.jdom2.Element;
import org.jdom2.Namespace;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.oryxeditor.server.diagram.basic.BasicShape;
import org.yawlfoundation.yawl.editor.core.layout.DecoratorPosition;
import org.yawlfoundation.yawl.editor.core.layout.DecoratorType;
import org.yawlfoundation.yawl.editor.core.layout.YDecoratorLayout;
import org.yawlfoundation.yawl.editor.core.layout.YNetLayout;
import org.yawlfoundation.yawl.editor.core.layout.YTaskLayout;
import org.yawlfoundation.yawl.elements.YNet;
import org.yawlfoundation.yawl.elements.YTask;
import org.yawlfoundation.yawl.engine.time.YTimer;
import org.yawlfoundation.yawl.engine.time.YWorkItemTimer;
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

    private static final String YAWL_DEFAULT_ICON_PATH = "/org/yawlfoundation/yawl/editor/resources/taskicons/";

    public OryxTaskHandler(final OryxConversionContext context, final BasicShape shape) {
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
            YTask task = createTask(convertYawlId(parentNet, shape), parentNet);
            convertTaskProperties(task);
            convertTaskLayout(task, parentNet);
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

    private void convertTaskLayout(final YTask task, final YNet parentNet) {
        YNetLayout netLayout = getContext().getLayout().getNetLayout(parentNet.getID());

        YTaskLayout taskLayout = new YTaskLayout(task, getContext().getNumberFormat());
        taskLayout.setBounds(convertTaskBounds(getShape()));

        String joinRouting = getShape().getProperty("join");
        if (joinRouting != null && !joinRouting.equalsIgnoreCase("none")) {
            YDecoratorLayout joinDecorator = taskLayout.newDecoratorLayoutInstance();
            joinDecorator.setPosition(convertDecoratorPosition(joinRouting));
            joinDecorator.setType(convertJoinType(joinRouting));
            joinDecorator.setBounds(convertDecoratorBounds(joinDecorator, taskLayout.getBounds()));
            taskLayout.setJoinLayout(joinDecorator);
        }

        String splitRouting = getShape().getProperty("split");
        if (splitRouting != null && !splitRouting.equalsIgnoreCase("none")) {
            YDecoratorLayout splitDecorator = taskLayout.newDecoratorLayoutInstance();
            splitDecorator.setPosition(convertDecoratorPosition(splitRouting));
            splitDecorator.setType(convertSplitType(splitRouting));
            splitDecorator.setBounds(convertDecoratorBounds(splitDecorator, taskLayout.getBounds()));
            taskLayout.setSplitLayout(splitDecorator);
        }

        taskLayout.setIconPath(convertIconPath());

        netLayout.addTaskLayout(taskLayout);
    }

    private String convertIconPath() {
        String icon = getShape().getProperty("icon");
        if (icon != null && !icon.isEmpty() && !icon.equalsIgnoreCase("none")) {
            return YAWL_DEFAULT_ICON_PATH + icon + ".png";
        } else {
            return null;
        }
    }

    private Rectangle convertDecoratorBounds(final YDecoratorLayout decorator, final Rectangle taskBounds) {
        Rectangle decoratorBounds = new Rectangle(taskBounds);

        switch (decorator.getPosition()) {
        case East:
            decoratorBounds.height = 32;
            decoratorBounds.width = 11;
            decoratorBounds.x += 31;
            break;

        case West:
            decoratorBounds.height = 32;
            decoratorBounds.width = 11;
            decoratorBounds.x -= 10;
            break;

        case North:
            decoratorBounds.height = 11;
            decoratorBounds.width = 32;
            decoratorBounds.y -= 10;
            break;

        case South:
            decoratorBounds.height = 11;
            decoratorBounds.width = 32;
            decoratorBounds.y += 31;
            break;
        }

        return decoratorBounds;
    }

    private DecoratorType convertJoinType(final String joinProperty) {
        String type = joinProperty.substring(0, joinProperty.length() - 1);
        if (type.equalsIgnoreCase(TaskHandler.AND_CONNECTOR)) {
            return DecoratorType.AndJoin;
        } else if (type.equalsIgnoreCase(TaskHandler.OR_CONNECTOR)) {
            return DecoratorType.OrJoin;
        } else if (type.equalsIgnoreCase(TaskHandler.XOR_CONNECTOR)) {
            return DecoratorType.XorJoin;
        }
        return DecoratorType.XorJoin;
    }

    private DecoratorType convertSplitType(final String SplitProperty) {
        String type = SplitProperty.substring(0, SplitProperty.length() - 1);
        if (type.equalsIgnoreCase(TaskHandler.AND_CONNECTOR)) {
            return DecoratorType.AndSplit;
        } else if (type.equalsIgnoreCase(TaskHandler.OR_CONNECTOR)) {
            return DecoratorType.OrSplit;
        } else if (type.equalsIgnoreCase(TaskHandler.XOR_CONNECTOR)) {
            return DecoratorType.XorSplit;
        }
        return DecoratorType.XorSplit;
    }

    private DecoratorPosition convertDecoratorPosition(final String routingProperty) {
        String position = routingProperty.substring(routingProperty.length() - 1, routingProperty.length());
        if (position.equalsIgnoreCase(TaskHandler.TOP_POSITION)) {
            return DecoratorPosition.North;
        } else if (position.equalsIgnoreCase(TaskHandler.BOTTOM_POSITION)) {
            return DecoratorPosition.South;
        } else if (position.equalsIgnoreCase(TaskHandler.LEFT_POSITION)) {
            return DecoratorPosition.West;
        } else if (position.equalsIgnoreCase(TaskHandler.RIGHT_POSITION)) {
            return DecoratorPosition.East;
        }
        return DecoratorPosition.East;
    }

    protected Rectangle convertTaskBounds(final BasicShape shape) {
        return new Rectangle(shape.getUpperLeft().getX().intValue() + 12, shape.getUpperLeft().getY().intValue() + 12, 32, 32);
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
    protected int convertConnectorType(String oryxType, final int noneType) {

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

    private void convertInputParamMappings(final YTask task) throws JSONException {
        if (getShape().hasProperty("inputparameters")) {
            JSONObject parameters = getShape().getPropertyJsonObject("inputparameters");
            if (parameters != null) {
                JSONArray expressionArray = parameters.getJSONArray("items");
                for (int i = 0; i < expressionArray.length(); i++) {
                    JSONObject expression = expressionArray.getJSONObject(i);
                    task.setDataBindingForInputParam(expression.getString("expression"), expression.getString("taskvariable"));
                }
            }
        }
    }

    private void convertOutputParamMappings(final YTask task) throws JSONException {
        if (getShape().hasProperty("outputparameters")) {
            JSONObject parameters = getShape().getPropertyJsonObject("outputparameters");
            if (parameters != null) {
                JSONArray expressionArray = parameters.getJSONArray("items");
                for (int i = 0; i < expressionArray.length(); i++) {
                    JSONObject expression = expressionArray.getJSONObject(i);
                    String expressionValue = expression.has("expression") ? expression.getString("expression") : "";
                    String expressionVariable = expression.getString("taskvariable");
                    task.setDataBindingForOutputExpression(expressionValue, expressionVariable);
                }
            }
        }
    }

    private void convertCancelledBySet(final YTask task) throws JSONException {
        if (getShape().hasProperty("cancelationset") && getShape().getPropertyJsonObject("cancelationset") != null) {
            JSONArray cancelationSet = getShape().getPropertyJsonObject("cancelationset").getJSONArray("items");
            if (cancelationSet != null) {
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
        }

    }

    private void convertTimer(final YTask task) throws ConversionException {
        if (getShape().hasProperty("timer") && !getShape().getProperty("timer").isEmpty()) {
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
     * Copyright (c) 2004-2012 The YAWL Foundation. All rights reserved. The YAWL Foundation is a collaboration of individuals and organisations who
     * are committed to improving workflow technology.
     * 
     * @param task
     * @param taskElem
     * @param _yawlNS
     */
    private void parseTimerParameters(final YTask task, final Element taskElem, final Namespace _yawlNS) {
        Element timerElem = taskElem.getChild("timer", _yawlNS);
        if (timerElem != null) {
            String netParam = timerElem.getChildText("netparam", _yawlNS);

            // net-level param holds values at runtime
            if (netParam != null) {
                task.setTimerParameters(netParam);
            } else {
                // get the triggering event
                String triggerStr = timerElem.getChildText("trigger", _yawlNS);
                YWorkItemTimer.Trigger trigger = YWorkItemTimer.Trigger.valueOf(triggerStr);

                // expiry is a stringified long value representing a specific
                // datetime
                String expiry = timerElem.getChildText("expiry", _yawlNS);
                if (expiry != null) {
                    task.setTimerParameters(trigger, new Date(new Long(expiry)));
                } else {
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
