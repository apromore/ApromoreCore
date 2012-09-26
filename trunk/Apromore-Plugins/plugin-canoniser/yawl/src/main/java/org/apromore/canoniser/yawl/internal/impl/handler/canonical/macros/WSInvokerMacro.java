package org.apromore.canoniser.yawl.internal.impl.handler.canonical.macros;

import org.apromore.canoniser.exception.CanoniserException;
import org.apromore.canoniser.yawl.internal.impl.context.CanonicalConversionContext;
import org.apromore.canoniser.yawl.internal.utils.ExtensionUtils;
import org.apromore.cpf.CanonicalProcessType;
import org.apromore.cpf.DirectionType;
import org.apromore.cpf.MessageType;
import org.apromore.cpf.NetType;
import org.apromore.cpf.NodeType;
import org.apromore.cpf.TaskType;
import org.apromore.cpf.TypeAttribute;
import org.yawlfoundation.yawlschema.ObjectFactory;
import org.yawlfoundation.yawlschema.WebServiceGatewayFactsType.YawlService;

public class WSInvokerMacro extends ContextAwareRewriteMacro {

    public WSInvokerMacro(final CanonicalConversionContext context) {
        super(context);
    }

    @Override
    public boolean rewrite(final CanonicalProcessType cpf) throws CanoniserException {
        boolean hasRewritten = false;

        for (final NetType net : cpf.getNet()) {
            for (int i = 0; i < net.getNode().size(); i++) {
                final NodeType node = net.getNode().get(i);
                if (checkCondition(node)) {
                    if (rewriteWSInvoker((MessageType) node, net)) {
                        hasRewritten = true;
                        i = -1;
                    }
                }
            }
        }

        return hasRewritten;
    }

    private boolean checkCondition(final NodeType node) {
        return node instanceof MessageType && ((MessageType) node).getDirection().equals(DirectionType.OUTGOING);
    }

    private boolean rewriteWSInvoker(final MessageType nodeMessageOutgoing, final NetType net) throws CanoniserException {
        NodeType nodeTask = getContext().getFirstSuccessor(nodeMessageOutgoing.getId());
        if (!(nodeTask instanceof TaskType)) {
            return false;
        }

        NodeType nodeMessageIncoming = getContext().getFirstSuccessor(nodeTask.getId());
        if (!(nodeMessageIncoming instanceof MessageType)) {
            return false;
        }

        // WSInvoker Pattern detected collapsing both Messages to Task
        deleteNodeLater(nodeMessageOutgoing);
        deleteNodeLater(nodeMessageIncoming);
        addEdgeLater(createEdge(getContext().getFirstPredecessor(nodeMessageOutgoing.getId()), nodeTask));
        addEdgeLater(createEdge(nodeTask, getContext().getFirstSuccessor(nodeMessageOutgoing.getId())));

        TypeAttribute serviceAttr = ExtensionUtils.getFromExtensions(nodeTask, ExtensionUtils.YAWL_SERVICE);
        if (serviceAttr != null) {
            YawlService service = ExtensionUtils
                    .unmarshalYAWLFragment(serviceAttr.getAny(), YawlService.class);
            getContext().getElementInfo(nodeTask.getId()).setYawlService(service);
        } else {
            YawlService defaultService = new ObjectFactory().createWebServiceGatewayFactsTypeYawlService();
            defaultService.setDocumentation("Unkown Service");
            defaultService.setId(generateUUID());
            getContext().getElementInfo(nodeTask.getId()).setYawlService(defaultService);
        }
        getContext().getElementInfo(nodeTask.getId()).setAutomatic(true);

        cleanupNet(net);

        return true;
    }

}
