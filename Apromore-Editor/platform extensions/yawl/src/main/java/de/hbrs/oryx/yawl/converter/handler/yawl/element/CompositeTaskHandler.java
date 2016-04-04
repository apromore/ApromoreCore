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

import java.util.HashMap;

import org.yawlfoundation.yawl.elements.YCompositeTask;

import de.hbrs.oryx.yawl.converter.context.YAWLConversionContext;
import de.hbrs.oryx.yawl.converter.layout.NetElementLayout;

/**
 * Converst a YAWL composite task to an Oryx shape
 * 
 * @author Felix Mannhardt (Bonn-Rhein-Sieg University of Applied Sciences)
 * 
 */
public class CompositeTaskHandler extends TaskHandler {

    public CompositeTaskHandler(final YAWLConversionContext context, final YCompositeTask compositeTask) {
        super(context, compositeTask, "CompositeTask", compositeTask.getDecompositionPrototype());
    }

    /*
     * (non-Javadoc)
     * 
     * @see de.hbrs.oryx.yawl.converter.handler.yawl.element.TaskHandler# convertTaskProperties (de.hbrs.oryx.yawl.converter.layout.NetElementLayout)
     */
    @Override
    protected HashMap<String, String> convertTaskProperties(final NetElementLayout layout) {
        // First convert all common task properties
        HashMap<String, String> properties = super.convertTaskProperties(layout);
        properties.putAll(convertDecompositionProperties());
        return properties;
    }

}
