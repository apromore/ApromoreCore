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
package de.hbrs.oryx.yawl.converter.handler.yawl.decomposition;

import java.util.HashMap;

import org.yawlfoundation.yawl.elements.YDecomposition;

import de.hbrs.oryx.yawl.converter.context.YAWLConversionContext;

/**
 * Converts the extra properties of a YAWL root net
 * 
 * @author Felix Mannhardt (Bonn-Rhein-Sieg University of Applied Sciences)
 * 
 */
public class RootNetHandler extends NetHandler {

    public RootNetHandler(final YAWLConversionContext context, final YDecomposition decomposition) {
        super(context, decomposition);
    }

    /*
     * (non-Javadoc)
     * 
     * @see de.hbrs.oryx.yawl.converter.handler.yawl.decomposition.NetHandler#convert (java.lang.String)
     */
    @Override
    public void convert(final String parentId) {
        super.convert(parentId);
        getContext().setRootNetId(getNet().getID());
    }

    /*
     * (non-Javadoc)
     * 
     * @see de.hbrs.oryx.yawl.converter.handler.yawl.decomposition.NetHandler# convertProperties()
     */
    @Override
    protected HashMap<String, String> convertProperties() {
        HashMap<String, String> metadata = super.convertProperties();
        // Removed as every Net could be a Root Net
        // metadata.put("isrootnet", "true");
        return metadata;
    }

}
