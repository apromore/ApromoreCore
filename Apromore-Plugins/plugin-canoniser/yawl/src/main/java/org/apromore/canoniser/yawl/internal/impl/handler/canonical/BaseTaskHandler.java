package org.apromore.canoniser.yawl.internal.impl.handler.canonical;

import org.apromore.cpf.NodeType;
import org.yawlfoundation.yawlschema.ControlTypeCodeType;
import org.yawlfoundation.yawlschema.ControlTypeType;
import org.yawlfoundation.yawlschema.ExternalTaskFactsType;

public abstract class BaseTaskHandler<T,E> extends CanonicalElementHandler<T, E> {

    protected ExternalTaskFactsType createTask(final NodeType node) {
        final ExternalTaskFactsType taskFacts = YAWL_FACTORY.createExternalTaskFactsType();
        if (node.getName() == null) {
            taskFacts.setName("");
        } else {
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
