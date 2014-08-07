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
package org.apromore.canoniser.yawl.internal.impl.handler.canonical.macros;

import java.util.Collection;
import java.util.List;

import org.apromore.canoniser.exception.CanoniserException;
import org.apromore.canoniser.yawl.internal.impl.context.CanonicalConversionContext;
import org.apromore.canoniser.yawl.internal.utils.ConversionUtils;
import org.apromore.cpf.CanonicalProcessType;
import org.apromore.cpf.EdgeType;
import org.apromore.cpf.NetType;
import org.apromore.cpf.NodeType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author <a href="mailto:felix.mannhardt@smail.wir.h-brs.de">Felix Mannhardt (Bonn-Rhein-Sieg University oAS)</a>
 *
 */
public class CheckValidModelMacro extends ContextAwareRewriteMacro {

    private static final Logger LOGGER = LoggerFactory.getLogger(CheckValidModelMacro.class);

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
                    LOGGER.error("Missing source on Edge {} (Available Nodes: {})", ConversionUtils.toString(edge),
                            ConversionUtils.nodesToString(net.getNode()));
                    throw new CanoniserException("Invalid Canonical Process! Edge " + edge.getId() + " is missing a Source!");
                }
                if (edge.getTargetId() == null) {
                    LOGGER.error("Missing target on Edge {} (Available Nodes: {})", ConversionUtils.toString(edge),
                            ConversionUtils.nodesToString(net.getNode()));
                    throw new CanoniserException("Invalid Canonical Process! Edge " + edge.getId() + " is missing a Target!");
                }
            }
            for (final NodeType node : net.getNode()) {
                final List<NodeType> postSet = getContext().getPostSet(node.getId());
                final List<NodeType> preSet = getContext().getPreSet(node.getId());
                if (postSet.isEmpty() && preSet.isEmpty()) {
                    // We want to be able to Canonise incomplete models
                    getContext().getMessageInterface().addMessage("Node %s is disconnected!", node.getId());
                    LOGGER.warn("Node {} is disconnected!", ConversionUtils.toString(node));
                }
            }
            final Collection<NodeType> sourceNodes = getContext().getSourceNodes(net);
            if (sourceNodes.isEmpty()) {
                LOGGER.warn("Net {} contains no source nodes!", ConversionUtils.toString(net));
                throw new CanoniserException("Net "+net.getId()+" contains no source nodes!");
            }
            final Collection<NodeType> sinkNodes = getContext().getSinkNodes(net);
            if (sinkNodes.isEmpty()) {
                LOGGER.warn("Net {} contains no sink nodes!", ConversionUtils.toString(net));
                throw new CanoniserException("Net "+net.getId()+" contains no sink nodes!");
            }
        }
        return false;
    }

}
