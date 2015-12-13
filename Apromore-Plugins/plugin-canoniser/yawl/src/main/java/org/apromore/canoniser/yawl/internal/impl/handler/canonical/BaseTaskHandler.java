/*
 * Copyright © 2009-2015 The Apromore Initiative.
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

package org.apromore.canoniser.yawl.internal.impl.handler.canonical;

import org.apromore.cpf.NodeType;
import org.yawlfoundation.yawlschema.ControlTypeCodeType;
import org.yawlfoundation.yawlschema.ControlTypeType;
import org.yawlfoundation.yawlschema.ExternalTaskFactsType;

public abstract class BaseTaskHandler<T,E> extends CanonicalElementHandler<T, E> {

    protected ExternalTaskFactsType createTask(final NodeType node) {
        final ExternalTaskFactsType taskFacts = YAWL_FACTORY.createExternalTaskFactsType();
        if (node.getName() != null && !node.getName().isEmpty()) {
            taskFacts.setName(node.getName());   
        }        
        taskFacts.setId(generateUUID(node.getId()));
        getContext().getControlFlowContext().setElement(node.getId(), taskFacts);
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
