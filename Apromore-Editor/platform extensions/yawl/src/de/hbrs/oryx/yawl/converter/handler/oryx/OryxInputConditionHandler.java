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
package de.hbrs.oryx.yawl.converter.handler.oryx;

import org.oryxeditor.server.diagram.basic.BasicShape;
import org.yawlfoundation.yawl.elements.YInputCondition;
import org.yawlfoundation.yawl.elements.YNet;

import de.hbrs.oryx.yawl.converter.context.OryxConversionContext;

/**
 * Converts a Input Condition
 * 
 * @author Felix Mannhardt (Bonn-Rhein-Sieg University of Applied Sciences)
 * 
 */
public class OryxInputConditionHandler extends OryxConditionHandler {

    public OryxInputConditionHandler(final OryxConversionContext context, final BasicShape shape) {
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

        final String yawlId = convertYawlId(parentNet, shape);
        final String name = (shape.hasProperty("name") && !shape.getProperty("name").isEmpty()) ? shape.getProperty("name") : null;
        final YInputCondition inputCondition = new YInputCondition(yawlId, name, parentNet);
        convertConditionProperties(inputCondition);
        convertConditionLayout(inputCondition, parentNet);
        parentNet.setInputCondition(inputCondition);
    }

}
