/*
 * Copyright © 2009-2017 The Apromore Initiative.
 *
 * This file is part of "Apromore".
 *
 * "Apromore" is free software; you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 3 of the
 * License, or (at your option) any later version.
 *
 * "Apromore" is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty
 * of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this program.
 * If not, see <http://www.gnu.org/licenses/lgpl-3.0.html>.
 */
package de.hbrs.oryx.yawl.converter.handler.oryx;

import org.oryxeditor.server.diagram.basic.BasicShape;
import org.yawlfoundation.yawl.elements.YNet;
import org.yawlfoundation.yawl.elements.YOutputCondition;

import de.hbrs.oryx.yawl.converter.context.OryxConversionContext;

/**
 * Converts a Output Condition
 * 
 * @author Felix Mannhardt (Bonn-Rhein-Sieg University of Applied Sciences)
 * 
 */
public class OryxOutputConditionHandler extends OryxConditionHandler {

    public OryxOutputConditionHandler(final OryxConversionContext context, final BasicShape shape) {
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
        final YOutputCondition outputCondition = new YOutputCondition(yawlId, name, parentNet);
        convertConditionProperties(outputCondition);
        convertConditionLayout(outputCondition, parentNet);
        parentNet.setOutputCondition(outputCondition);
    }

}
