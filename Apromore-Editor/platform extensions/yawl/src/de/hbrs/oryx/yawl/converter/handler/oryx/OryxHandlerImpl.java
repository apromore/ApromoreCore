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

import java.util.UUID;

/**
 * Base class implementation for a OryxHandler
 *
 * @author Felix Mannhardt (Bonn-Rhein-Sieg University of Applied Sciences)
 */
public abstract class OryxHandlerImpl implements OryxHandler {

    private final OryxConversionContext context;

    public OryxHandlerImpl(OryxConversionContext context) {
        this.context = context;
    }

    /**
     * @return current conversion context
     */
    protected OryxConversionContext getContext() {
        return context;
    }

    /**
     * Checks if there is a YAWL ID (yawlid) property set either by the user or
     * by a former import. If there is no such ID, a unique ID is generated and
     * both returned and added to the properties of the supplied Shape.
     *
     * @param shape to get the yawlid property from
     * @return either a UUID or a manually specified ID
     */
    protected String convertYawlId(BasicShape shape) {
        String yawlId = shape.getProperty("yawlid");
        if (yawlId == null || yawlId.isEmpty()) {
            // Generate a globally unique identifier
            String uuid = "id" + UUID.randomUUID().toString();
            shape.setProperty("yawlid", uuid);
            return uuid;
        } else {
            return yawlId;
        }
    }

}
