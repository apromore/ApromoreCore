package de.hpi.bpmn2_0.replay;

import de.hpi.bpmn2_0.model.connector.SequenceFlow;

public class AnimatedSequenceFlow extends SequenceFlow {
    private boolean isVirtual = false;
    
    public boolean isVirtual() {
        return this.isVirtual;
    }
    
    public void setIsVirtual(boolean isVirtual) {
        this.isVirtual = isVirtual;
    }
}