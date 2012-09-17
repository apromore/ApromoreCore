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

import de.hbrs.oryx.yawl.converter.context.OryxConversionContext;
import org.oryxeditor.server.diagram.basic.BasicShape;
import org.yawlfoundation.yawl.elements.YCondition;
import org.yawlfoundation.yawl.elements.YNet;

/**
 * Converts a Condition
 *
 * @author Felix Mannhardt (Bonn-Rhein-Sieg University of Applied Sciences)
 */
public class OryxConditionHandler extends OryxNetElementHandler {

    public OryxConditionHandler(OryxConversionContext context, BasicShape shape) {
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

        final String yawlId = convertYawlId(shape);
        final String name = (shape.hasProperty("name") && !shape.getProperty("name").isEmpty()) ? shape.getProperty("name") : null;
        final YCondition condition = new YCondition(yawlId, name, parentNet);
        convertConditionProperties(condition);
        parentNet.addNetElement(condition);

        // Remember Flows for later conversion
        rememberOutgoings();
        rememberIncomings();
    }

    /**
     * Fills all properties of the YCondition
     *
     * @param condition
     */
    protected void convertConditionProperties(final YCondition condition) {
        if (getShape().hasProperty("documentation")) {
            condition.setDocumentation(getShape().getProperty("documentation"));
        }
    }

}
