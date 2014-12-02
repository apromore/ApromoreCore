package de.hpi.bpmn2_0.replay;

import de.hpi.bpmn2_0.model.FlowNode;

public class FlowNode2 extends FlowNode {
    private Object nameRef = null;
    
    public FlowNode2(FlowNode flowNode) {
        super(flowNode);
    }
    
    public Object getNameRef() {
        return nameRef;
    }
    
    public void setNameRef(Object nameRef) {
        this.nameRef = nameRef;
    }
}