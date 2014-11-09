package de.hpi.bpmn2_0.replay;

public class InvalidReplayException extends Exception {
    String message = "";
    
    public InvalidReplayException(String msg) {
        this.message = msg;
    }

    @Override
    public String toString(){
        return "Invalid Replay. " + message;
    }    
    
}