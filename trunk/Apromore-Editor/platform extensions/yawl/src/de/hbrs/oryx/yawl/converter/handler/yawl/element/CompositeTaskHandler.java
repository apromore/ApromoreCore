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
package de.hbrs.oryx.yawl.converter.handler.yawl.element;

import de.hbrs.oryx.yawl.converter.context.YAWLConversionContext;
import de.hbrs.oryx.yawl.converter.layout.NetElementLayout;
import org.yawlfoundation.yawl.elements.YCompositeTask;

import java.util.HashMap;

/**
 * Converst a YAWL composite task to an Oryx shape
 *
 * @author Felix Mannhardt (Bonn-Rhein-Sieg University of Applied Sciences)
 */
public class CompositeTaskHandler extends TaskHandler {

    public CompositeTaskHandler(YAWLConversionContext context, YCompositeTask compositeTask) {
        super(context, compositeTask, "CompositeTask", compositeTask.getDecompositionPrototype());
    }

    /*
      * (non-Javadoc)
      *
      * @see de.hbrs.oryx.yawl.converter.handler.yawl.element.TaskHandler#
      * convertTaskProperties
      * (de.hbrs.oryx.yawl.converter.layout.NetElementLayout)
      */
    @Override
    protected HashMap<String, String> convertTaskProperties(NetElementLayout layout) {
        // First convert all common task properties
        HashMap<String, String> properties = super.convertTaskProperties(layout);
        properties.putAll(convertDecompositionProperties());
        return properties;
    }

}
