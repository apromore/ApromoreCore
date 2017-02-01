/*
 * Copyright © 2009-2017 The Apromore Initiative.
 *
 * This file is part of "Apromore".
 *
 * "Apromore" is free software; you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 3 of the
 * License, or (at your option) any later version.
 *
 * "Apromore" is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty
 * of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this program.
 * If not, see <http://www.gnu.org/licenses/lgpl-3.0.html>.
 */

package org.apromore.canoniser.yawl.internal.impl.handler.canonical.macros;

import org.apromore.canoniser.exception.CanoniserException;
import org.apromore.canoniser.yawl.internal.impl.context.CanonicalConversionContext;
import org.apromore.canoniser.yawl.internal.utils.ExtensionUtils;
import org.apromore.cpf.CanonicalProcessType;
import org.apromore.cpf.DirectionEnum;
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
                if (checkCondition(node) && rewriteWSInvoker((MessageType) node, net)) {
                    hasRewritten = true;
                    i = -1;
                }
            }
        }

        return hasRewritten;
    }

    private boolean checkCondition(final NodeType node) {
        return node instanceof MessageType && ((MessageType) node).getDirection().equals(DirectionEnum.OUTGOING);
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
        addEdgeLater(createEdge(nodeTask, getContext().getFirstSuccessor(nodeMessageIncoming.getId())));

        TypeAttribute serviceAttr = ExtensionUtils.getExtensionAttribute(nodeTask, ExtensionUtils.YAWL_SERVICE);
        if (serviceAttr != null) {
            YawlService service = ExtensionUtils.unmarshalYAWLFragment(serviceAttr.getAny(), YawlService.class);
            getContext().getControlFlowContext().getElementInfo(nodeTask.getId()).setYawlService(service);
        } else {
            YawlService defaultService = new ObjectFactory().createWebServiceGatewayFactsTypeYawlService();
            defaultService.setDocumentation("Unkown Service");
            defaultService.setId(generateUUID());
            getContext().getControlFlowContext().getElementInfo(nodeTask.getId()).setYawlService(defaultService);
        }
        getContext().getControlFlowContext().getElementInfo(nodeTask.getId()).setAutomatic(true);

        cleanupNet(net);

        return true;
    }

}
