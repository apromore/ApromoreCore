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
import org.yawlfoundation.yawl.elements.YNet;

import de.hbrs.oryx.yawl.converter.context.OryxConversionContext;
import de.hbrs.oryx.yawl.util.YAWLUtils;

/**
 * Base class implementation for a OryxHandler
 * 
 * @author Felix Mannhardt (Bonn-Rhein-Sieg University of Applied Sciences)
 * 
 */
public abstract class OryxHandlerImpl implements OryxHandler {

    private final OryxConversionContext context;

    public OryxHandlerImpl(final OryxConversionContext context) {
        this.context = context;
    }

    /**
     * @return current conversion context
     */
    protected OryxConversionContext getContext() {
        return context;
    }

    protected String convertYawlId(final YNet net, final BasicShape shape) {
        return YAWLUtils.convertYawlId(net, shape);
    }

    protected String convertYawlId(final BasicShape shape) {
        return YAWLUtils.convertYawlId(shape);
    }

    protected String convertYawlId() {
        return YAWLUtils.convertYawlId();
    }

}
