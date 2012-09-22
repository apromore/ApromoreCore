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
package org.apromore.canoniser.yawl.internal.impl.handler.canonical.macros;

import java.util.Collection;
import java.util.List;

import org.apromore.canoniser.exception.CanoniserException;
import org.apromore.canoniser.yawl.internal.impl.context.CanonicalConversionContext;
import org.apromore.cpf.CanonicalProcessType;
import org.apromore.cpf.EdgeType;
import org.apromore.cpf.NetType;
import org.apromore.cpf.NodeType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author <a href="felix.mannhardt@smail.wir.h-brs.de">Felix Mannhardt (Bonn-Rhein-Sieg University oAS)</a>
 *
 */
public class CheckValidModelMacro extends ContextAwareRewriteMacro {

    private static final Logger LOGGER = LoggerFactory.getLogger(CheckValidModelMacro.class.getName());

    public CheckValidModelMacro(final CanonicalConversionContext context) {
        super(context);
    }

    /*
     * (non-Javadoc)
     *
     * @see org.apromore.canoniser.yawl.internal.impl.handler.canonical.macros.RewriteMacro#rewrite(org.apromore.cpf.CanonicalProcessType)
     */
    @Override
    public boolean rewrite(final CanonicalProcessType cpf) throws CanoniserException {
        for (final NetType net : cpf.getNet()) {
            for (final EdgeType edge : net.getEdge()) {
                if (edge.getSourceId() == null) {
                    throw new CanoniserException("Invalid Canonical Process with Uri " + cpf.getUri() + "! Edge " + edge.getId()
                            + " is missing a Source!");
                }
                if (edge.getTargetId() == null) {
                    throw new CanoniserException("Invalid Canonical Process with Uri " + cpf.getUri() + "! Edge " + edge.getId()
                            + " is missing a Target!");
                }
            }
            for (final NodeType node : net.getNode()) {
                final List<NodeType> postSet = getContext().getPostSet(node.getId());
                final List<NodeType> preSet = getContext().getPreSet(node.getId());
                if (postSet.isEmpty() && preSet.isEmpty()) {
                    LOGGER.warn("Node " + node.getId() + " is disconnected!");
                }
            }
            Collection<NodeType> sourceNodes = getContext().getSourceNodes(net);
            if (sourceNodes.isEmpty()) {
                throw new CanoniserException("Can not canonise a process model without any source node!");
            }
            Collection<NodeType> sinkNodes = getContext().getSourceNodes(net);
            if (sinkNodes.isEmpty()) {
                throw new CanoniserException("Can not canonise a process model without any sink node!");
            }
        }
        return false;
    }

}
