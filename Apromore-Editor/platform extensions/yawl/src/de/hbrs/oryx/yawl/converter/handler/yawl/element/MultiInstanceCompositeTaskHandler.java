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

import org.yawlfoundation.yawl.elements.YCompositeTask;
import org.yawlfoundation.yawl.elements.YTask;

import de.hbrs.oryx.yawl.converter.context.YAWLConversionContext;
import de.hbrs.oryx.yawl.converter.layout.NetElementLayout;
import de.hbrs.oryx.yawl.util.MultiInstanceConverter;

/**
 * Converts a YAWL composite task with multiple instances to a Oryx shape
 * 
 * @author Felix Mannhardt (Bonn-Rhein-Sieg University of Applied Sciences)
 * 
 */
public class MultiInstanceCompositeTaskHandler extends CompositeTaskHandler {

    public MultiInstanceCompositeTaskHandler(final YAWLConversionContext context, final YCompositeTask netElement) {
        super(context, netElement);
        // Override setting of CompositeTaskHandler
        setTaskType("CompositeMultipleTask");
    }

    /*
     * (non-Javadoc)
     * 
     * @see de.hbrs.oryx.yawl.converter.handler.yawl.element.AtomicTaskHandler# convertTaskProperties
     * (de.hbrs.oryx.yawl.converter.layout.NetElementLayout)
     */
    @Override
    protected HashMap<String, String> convertTaskProperties(final NetElementLayout layout) {
        HashMap<String, String> properties = super.convertTaskProperties(layout);

        YTask task = (YTask) getNetElement();
        properties.putAll(MultiInstanceConverter.convert(task));

        return properties;
    }

}
