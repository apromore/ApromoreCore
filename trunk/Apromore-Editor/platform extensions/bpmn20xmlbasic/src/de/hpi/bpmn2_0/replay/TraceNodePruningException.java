package de.hpi.bpmn2_0.replay;

public class TraceNodePruningException extends Exception {
    String message = "";
    
    public TraceNodePruningException(String msg) {
        this.message = msg;
    }

    @Override
    public String toString(){
        return "Replay Trace Pruning Error: " + message;
    }    
    
}