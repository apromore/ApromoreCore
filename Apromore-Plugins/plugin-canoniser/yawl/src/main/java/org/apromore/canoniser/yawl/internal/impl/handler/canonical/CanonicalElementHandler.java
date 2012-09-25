/**
 * Copyright 2012, Felix Mannhardt
 * 
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
 * FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public License along with this program. If not, see <http://www.gnu.org/licenses/>.
 */
package org.apromore.canoniser.yawl.internal.impl.handler.canonical;

import org.apromore.canoniser.yawl.internal.impl.context.CanonicalConversionContext;
import org.apromore.canoniser.yawl.internal.impl.handler.ConversionHandlerImpl;
import org.apromore.cpf.NodeType;
import org.yawlfoundation.yawlschema.ControlTypeCodeType;
import org.yawlfoundation.yawlschema.ControlTypeType;
import org.yawlfoundation.yawlschema.ExternalTaskFactsType;

/**
 * Abstract base class for all handlers that convert from CPF to YAWL
 * 
 * @author <a href="mailto:felix.mannhardt@smail.wir.h-brs.de">Felix Mannhardt (Bonn-Rhein-Sieg University oAS)</a>
 * 
 * @param <T>
 *            type of Element to be converted
 * @param <E>
 *            type of the already converted Parent
 */
public abstract class CanonicalElementHandler<T, E> extends ConversionHandlerImpl<T, E> {

    /**
     * @return the canonical conversion context
     */
    protected CanonicalConversionContext getContext() {
        return (CanonicalConversionContext) context;
    }

    protected ExternalTaskFactsType createTask(final NodeType node) {
        final ExternalTaskFactsType taskFacts = YAWL_FACTORY.createExternalTaskFactsType();
        taskFacts.setName(node.getName());
        taskFacts.setId(generateUUID(node.getId()));
        getContext().setElement(node.getId(), taskFacts);
        return taskFacts;
    }

    protected ControlTypeType getDefaultJoinType() {
        final ControlTypeType controlType = YAWL_FACTORY.createControlTypeType();
        controlType.setCode(ControlTypeCodeType.XOR);
        return controlType;
    }

    protected ControlTypeType getDefaultSplitType() {
        final ControlTypeType controlType = YAWL_FACTORY.createControlTypeType();
        controlType.setCode(ControlTypeCodeType.AND);
        return controlType;
    }

}
