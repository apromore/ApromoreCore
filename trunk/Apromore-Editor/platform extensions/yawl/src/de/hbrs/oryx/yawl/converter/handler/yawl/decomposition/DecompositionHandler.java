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
package de.hbrs.oryx.yawl.converter.handler.yawl.decomposition;

import de.hbrs.oryx.yawl.converter.context.YAWLConversionContext;
import de.hbrs.oryx.yawl.converter.handler.yawl.YAWLHandlerImpl;
import org.yawlfoundation.yawl.elements.YDecomposition;

import java.util.HashMap;

/**
 * Base class for conversion of all different decompositions.
 *
 * @author Felix Mannhardt (Bonn-Rhein-Sieg University of Applied Sciences)
 */
public abstract class DecompositionHandler extends YAWLHandlerImpl {

    private final YDecomposition decomposition;

    public DecompositionHandler(YAWLConversionContext context, YDecomposition decomposition) {
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
            properties.put("decompositionlogpredicate",
                    d.getLogPredicate() != null ? d.getLogPredicate().toXML().replace("<logPredicate>", "").replace("</logPredicate>", "")
                            : "");
        }

        return properties;
    }

    protected boolean hasDecomposition() {
        return getDecomposition() != null;
    }

}
