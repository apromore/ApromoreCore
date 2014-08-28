/*
 * Copyright © 2009-2014 The Apromore Initiative.
 *
 * This file is part of "Apromore".
 *
 * "Apromore" is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 3 of the License, or
 * (at your option) any later version.
 *
 * "Apromore" is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.
 * If not, see <http://www.gnu.org/licenses/lgpl-3.0.html>.
 */
package org.apromore.canoniser.yawl.internal.impl.handler.canonical.macros;

import java.util.ArrayList;
import java.util.Collection;

import org.apromore.canoniser.yawl.internal.impl.context.CanonicalConversionContext;
import org.apromore.canoniser.yawl.internal.utils.ConversionUtils;
import org.apromore.cpf.CanonicalProcessType;
import org.apromore.cpf.EventType;
import org.apromore.cpf.NetType;
import org.apromore.cpf.NodeType;
import org.apromore.cpf.ORJoinType;
import org.apromore.cpf.ObjectFactory;
import org.apromore.cpf.StateType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Converts any multiple exit Net into a single exit Net. By default it only adds an OR-JOIN, but more sophisticated techniques could be implemented.
 * 
 * @author <a href="mailto:felix.mannhardt@smail.wir.h-brs.de">Felix Mannhardt (Bonn-Rhein-Sieg University oAS)</a>
 * 
 */
public class SEMEToSESEMacro extends ContextAwareRewriteMacro implements RewriteMacro {

    private static final Logger LOGGER = LoggerFactory.getLogger(SEMEToSESEMacro.class);

    public SEMEToSESEMacro(final CanonicalConversionContext context) {
        super(context);
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.apromore.canoniser.yawl.internal.impl.handler.canonical.macros.RewriteMacro#rewrite(org.apromore.cpf.CanonicalProcessType)
     */
    @Override
    public boolean rewrite(final CanonicalProcessType cpf) {

        boolean hasRewritten = false;

        for (final NetType net : cpf.getNet()) {
            if (getContext().hasMultipleExits(net)) {
                final Collection<NodeType> exitNodes = getContext().getSinkNodes(net);
                final Collection<NodeType> exitEvents = new ArrayList<NodeType>();
                for (final NodeType node : exitNodes) {
                    if (node instanceof EventType || node instanceof StateType) {
                        exitEvents.add(node);
                    }
                }
                if (exitEvents.size() > 1) {
                    LOGGER.info("Rewriting Net with multiple exit nodes ({})", ConversionUtils.nodesToString(exitEvents));
                    fixWithORJoin(exitEvents);
                    hasRewritten = true;
                    cleanupNet(net);
                }
            }
        }
        return hasRewritten;
    }

    private void fixWithORJoin(final Collection<NodeType> exitNodes) {
        final ObjectFactory cpfFactory = new ObjectFactory();

        // Create ORJoin
        final ORJoinType orJoin = cpfFactory.createORJoinType();
        orJoin.setId(generateUUID());

        LOGGER.debug("Adding OR join {}", ConversionUtils.toString(orJoin));

        // Connect all exits nodes to the ORJoin
        for (final NodeType node : exitNodes) {
            addEdgeLater(createEdge(node, orJoin));
        }

        // Add ORJoin to Net
        addNodeLater(orJoin);

        // Create Single End Event
        final EventType endEvent = cpfFactory.createEventType();
        endEvent.setId(generateUUID());

        LOGGER.debug("Adding unique end event {}", ConversionUtils.toString(endEvent));

        // Add End Event
        addNodeLater(endEvent);

        // Connect ORJoin with EndEvent
        addEdgeLater(createEdge(orJoin, endEvent));
    }

}
