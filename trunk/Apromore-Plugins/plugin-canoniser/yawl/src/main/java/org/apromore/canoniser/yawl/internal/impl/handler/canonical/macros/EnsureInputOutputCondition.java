package org.apromore.canoniser.yawl.internal.impl.handler.canonical.macros;

import java.util.Collection;

import org.apromore.canoniser.exception.CanoniserException;
import org.apromore.canoniser.yawl.internal.impl.context.CanonicalConversionContext;
import org.apromore.canoniser.yawl.internal.utils.ConversionUtils;
import org.apromore.cpf.CanonicalProcessType;
import org.apromore.cpf.EventType;
import org.apromore.cpf.NetType;
import org.apromore.cpf.NodeType;
import org.apromore.cpf.ObjectFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class EnsureInputOutputCondition extends ContextAwareRewriteMacro {

    private static final Logger LOGGER = LoggerFactory.getLogger(EnsureInputOutputCondition.class);

    public EnsureInputOutputCondition(final CanonicalConversionContext context) {
        super(context);
    }

    @Override
    public boolean rewrite(final CanonicalProcessType cpf) throws CanoniserException {
        boolean hasRewritten = false;
        for (final NetType net : cpf.getNet()) {
            hasRewritten = ensureInputCondition(net);
            hasRewritten = ensureOutputCondition(net);
        }
        return hasRewritten;
    }

    private boolean ensureOutputCondition(final NetType net) throws CanoniserException {
        final Collection<NodeType> sinkNodes = getContext().getSinkNodes(net);
        if (sinkNodes.size() == 1) {
            final NodeType endNode = sinkNodes.iterator().next();
            if (!(endNode instanceof EventType)) {
                // Add start Event before this Node to ensure Net has a Input Condition
                final EventType endEvent = createEvent();
                addNodeLater(endEvent);
                addEdgeLater(createEdge(endNode, endEvent));
                LOGGER.info("Ensure Net {} stops with an Ouput Condition, adding End Event {}", net.getId(), ConversionUtils.toString(endEvent));
                cleanupNet(net);
                return true;
            }
        } else {
            // Incomplete Net
            LOGGER.error("Net {} contains multiple sink nodes ({}), can't add an Output Condition", net.getId(),
                    ConversionUtils.nodesToString(sinkNodes));
            throw new CanoniserException("Net "+net.getId()+" contains no sink nodes!");
//            EventType endEvent = createEvent();
//            endEvent.setName("end");
//            TypeAttribute endAttribute = new TypeAttribute();
//            endAttribute.setName("org.apromore.canoniser.yawl.artificalOutputCondition");
//            endAttribute.setValue("true");
//            endEvent.getAttribute().add(endAttribute);
//            addNodeLater(endEvent);
//            cleanupNet(net);
//            return true;
        }
        return false;
    }

    private boolean ensureInputCondition(final NetType net) throws CanoniserException {
        final Collection<NodeType> sourceNodes = getContext().getSourceNodes(net);
        if (sourceNodes.size() == 1) {
            final NodeType startNode = sourceNodes.iterator().next();
            if (!(startNode instanceof EventType)) {
                // Add start Event before this Node to ensure Net has a Input Condition
                final EventType startEvent = createEvent();
                addNodeLater(startEvent);
                addEdgeLater(createEdge(startEvent, startNode));
                LOGGER.info("Ensure Net starts with an Input Condition, adding Start Event {}", net.getId(), ConversionUtils.toString(startEvent));
                cleanupNet(net);
                return true;
            }
        } else {
            // Incomplete Net
            LOGGER.error("Net {} contains multiple source nodes ({}), adding a disconnected Input Condition", net.getId(),
                    ConversionUtils.nodesToString(sourceNodes));
            throw new CanoniserException("Net "+net.getId()+" contains no source nodes!");
//            EventType startEvent = createEvent();
//            startEvent.setName("start");
//            TypeAttribute startAttribute = new TypeAttribute();
//            startAttribute.setName("org.apromore.canoniser.yawl.artificalInputCondition");
//            startAttribute.setValue("true");
//            startEvent.getAttribute().add(startAttribute);
//            addNodeLater(startEvent);
//            cleanupNet(net);
//            return true;
        }
        return false;
    }

    private EventType createEvent() {
        final ObjectFactory cpfFactory = new ObjectFactory();
        final EventType event = cpfFactory.createEventType();
        event.setId(generateUUID());
        return event;
    }

}
