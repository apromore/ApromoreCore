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

import java.util.HashMap;

import org.oryxeditor.server.diagram.Bounds;
import org.oryxeditor.server.diagram.basic.BasicNode;
import org.oryxeditor.server.diagram.basic.BasicShape;
import org.yawlfoundation.yawl.elements.YCondition;
import org.yawlfoundation.yawl.elements.YNetElement;

import de.hbrs.oryx.yawl.converter.context.YAWLConversionContext;
import de.hbrs.oryx.yawl.converter.exceptions.ConversionException;
import de.hbrs.oryx.yawl.converter.layout.NetElementLayout;

/**
 * Converts a YAWL condition to an Oryx shape
 *
 * @author Felix Mannhardt (Bonn-Rhein-Sieg University of Applied Sciences)
 *
 */
public class ConditionHandler extends NetElementHandler {

    public ConditionHandler(final YAWLConversionContext context, final YNetElement netElement) {
        super(context, netElement, null);
    }

    /*
     * (non-Javadoc)
     *
     * @see de.hbrs.oryx.yawl.converter.handler.yawl.YAWLHandler#convert(java.lang .String)
     */
    @Override
    public void convert(final String parentId) {
        if (isElementVisible(getNetElement())) {
            BasicShape condition = convertCondition(parentId, "Condition");
            getContext().putShape(parentId, getNetElement().getID(), condition);
            super.convert(parentId);
        }
    }

    private YCondition getCondition() {
        return (YCondition) getNetElement();
    }

    protected BasicShape convertCondition(final String netId, final String stencilType) {
        BasicShape outputShape = new BasicNode(getCondition().getID(), stencilType);
        outputShape.setBounds(getConditionLayout(netId, getCondition()));
        outputShape.setProperties(convertProperties());
        return outputShape;
    }

    private HashMap<String, String> convertProperties() {
        HashMap<String, String> props = new HashMap<String, String>();
        props.put("yawlid", getCondition().getID());
        if (getCondition().getName() != null) {
            props.put("name", getCondition().getName());
        }
        if (getCondition().getDocumentation() != null) {
            props.put("documentation", getCondition().getDocumentation());
        }
        return props;
    }

    private Bounds getConditionLayout(final String netId, final YCondition condition) {
        NetElementLayout conditionLayout = getContext().getVertexLayout(netId, condition.getID());
        if (conditionLayout != null) {
            return conditionLayout.getBounds();
        } else {
            getContext().addConversionWarnings(new ConversionException("Missing layout for condition "+condition.getID()));
            return new Bounds();
        }
    }

    // Explicit means visible in Oryx
    private boolean isElementVisible(final YNetElement yElement) {
        return !(yElement instanceof YCondition) || (yElement instanceof YCondition) && (!((YCondition) yElement).isImplicit());
    }

}
