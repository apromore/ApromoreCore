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
package org.apromore.canoniser.yawl.internal.impl.context;

import org.apromore.cpf.NodeType;
import org.yawlfoundation.yawlschema.FlowsIntoType;

/**
 * Adapter to keep predecessors together with their 'flowsInto' information.
 *
 * @author <a href="mailto:felix.mannhardt@smail.wir.h-brs.de">Felix Mannhardt</a>
 *
 */
public class PredecessorAdapater {

    private final NodeType node;
    private final FlowsIntoType flowsInto;

    public PredecessorAdapater(final NodeType node, final FlowsIntoType flowsInto) {
        this.node = node;
        this.flowsInto = flowsInto;
    }

    public FlowsIntoType getFlowsInto() {
        return flowsInto;
    }

    public NodeType getNode() {
        return node;
    }

}
