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
import de.hbrs.oryx.yawl.converter.handler.yawl.YAWLHandlerImpl;

/**
 * Base class for conversion of all different decompositions.
 * 
 * @author Felix Mannhardt (Bonn-Rhein-Sieg University of Applied Sciences)
 * 
 */
public abstract class DecompositionHandler extends YAWLHandlerImpl {

    private final YDecomposition decomposition;

    public DecompositionHandler(final YAWLConversionContext context, final YDecomposition decomposition) {
        super(context);
        this.decomposition = decomposition;
    }

    protected YDecomposition getDecomposition() {
        return decomposition;
    }

    /**
     * @return properties belonging to YDecomposition only
     */
    protected HashMap<String, String> convertDecompositionProperties() {
        HashMap<String, String> properties = new HashMap<String, String>();

        if (hasDecomposition()) {
            YDecomposition d = getDecomposition();
            properties.put("decompositionid", d.getID());
            properties.put("decompositionname", d.getName());
            properties.put("decompositionexternalinteraction", d.requiresResourcingDecisions() ? "manual" : "automated");
            properties.put("decompositioncodelet", d.getCodelet() != null ? d.getCodelet() : "");
            properties.put("decompositionlogpredicate", d.getLogPredicate() != null ? d.getLogPredicate().toXML().replace("<logPredicate>", "")
                    .replace("</logPredicate>", "") : "");
        }

        return properties;
    }

    protected boolean hasDecomposition() {
        return getDecomposition() != null;
    }

}
