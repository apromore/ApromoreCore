package de.hpi.bpmn2_0.replay;

/*
* Implement counter for one ORJoinLabel
*/
public class ORJoinCounter {
    private int value = 0;
    private boolean isIgnored = false;
    
    public int getValue() {
        return value;
    }
    
    public void setValue(int newValue) {
        this.value = newValue;
    }
    
    public boolean isIgnored() {
        return isIgnored;
    }
    
    public void setIgnored(boolean isIgnored) {
        this.isIgnored = isIgnored;
    }
    
    public void reset() {
        value = 0;
        isIgnored = false;
    }
}