package org.apromore.canoniser.yawl.internal.impl.handler.canonical.macros;

import java.util.Collection;

import org.apromore.canoniser.exception.CanoniserException;
import org.apromore.canoniser.yawl.internal.impl.context.CanonicalConversionContext;
import org.apromore.cpf.CanonicalProcessType;
import org.apromore.cpf.EventType;
import org.apromore.cpf.NetType;
import org.apromore.cpf.NodeType;
import org.apromore.cpf.ObjectFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class EnsureInputOutputCondition extends ContextAwareRewriteMacro {

    private static final Logger LOGGER = LoggerFactory.getLogger(CheckValidModelMacro.class.getName());

    public EnsureInputOutputCondition(final CanonicalConversionContext context) {
        super(context);
    }

    @Override
    public boolean rewrite(final CanonicalProcessType cpf) throws CanoniserException {
        boolean hasRewritten = false;
        for (final NetType net : cpf.getNet()) {
            ensureInputCondition(net);
            ensureOutputCondition(net);
        }
        return hasRewritten;
    }

    private void ensureOutputCondition(final NetType net) {
        Collection<NodeType> sinkNodes = getContext().getSinkNodes(net);
        if (sinkNodes.size() == 1) {
            NodeType endNode = sinkNodes.iterator().next();
            if (!(endNode instanceof EventType)) {
                // Add start Event before this Node to ensure Net has a Input Condition
                final EventType endEvent = createEvent();
                createEdge(endNode, endEvent);
                LOGGER.info("Ensure Net {} stops with an Ouput Condition, adding End Event {}", net.getId(), endEvent.getId());
            }
        } else {
            LOGGER.warn("Net {} contains multiple sink nodes, can't add an Output Condition", net.getId());
        }
    }

    private void ensureInputCondition(final NetType net) {
        Collection<NodeType> sourceNodes = getContext().getSourceNodes(net);
        if (sourceNodes.size() == 1) {
            NodeType startNode = sourceNodes.iterator().next();
            if (!(startNode instanceof EventType)) {
                // Add start Event before this Node to ensure Net has a Input Condition
                final EventType startEvent = createEvent();
                createEdge(startEvent, startNode);
                LOGGER.info("Ensure Net starts with an Input Condition, adding Start Event {}", net.getId(), startEvent.getId());
            }
        } else {
            LOGGER.warn("Net {} contains multiple source nodes, can't add an Input Condition", net.getId());
        }
    }

    private EventType createEvent() {
        final ObjectFactory cpfFactory = new ObjectFactory();
        final EventType event = cpfFactory.createEventType();
        event.setId(generateUUID());
        return event;
    }

}
