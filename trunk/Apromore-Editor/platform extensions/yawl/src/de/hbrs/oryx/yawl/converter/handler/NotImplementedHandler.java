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
package de.hbrs.oryx.yawl.converter.handler;

import de.hbrs.oryx.yawl.converter.handler.oryx.OryxHandler;
import de.hbrs.oryx.yawl.converter.handler.yawl.YAWLHandler;

/**
 * Handler that does nothing at all.
 * 
 * @author Felix Mannhardt (Bonn-Rhein-Sieg University of Applied Sciences)
 * 
 */
public class NotImplementedHandler implements YAWLHandler, OryxHandler {

    /*
     * (non-Javadoc)
     * 
     * @see de.hbrs.oryx.yawl.converter.YAWLHandler#convert()
     */
    @Override
    public void convert(final String parentId) {
        // NoOp
    }

    /*
     * (non-Javadoc)
     * 
     * @see de.hbrs.oryx.yawl.converter.handler.oryx.OryxHandler#convert()
     */
    @Override
    public void convert() {
        // NoOp
    }

}
