package au.edu.qut.processmining.log.graph;

import java.util.UUID;

/**
 * Created by Adriano on 15/06/2016.
 */
public class LogNode implements Comparable {
    protected String id;
    protected String label;
    protected int code;

    protected int frequency;
    protected int startFrequency;
    protected int endFrequency;

    public LogNode() {
        id = UUID.randomUUID().toString();
        label = "null";
        frequency = 0;
        startFrequency = 0;
        endFrequency = 0;
    }

    public LogNode(String label) {
        id = UUID.randomUUID().toString();
        frequency = 0;
        startFrequency = 0;
        endFrequency = 0;
        this.label = label;
    }
    public LogNode(String label, int code) {
        id = Integer.toString(code);
        frequency = 0;
        startFrequency = 0;
        endFrequency = 0;
        this.label = label;
        this.code = code;
    }

    public String getID() { return id; }

    public void setLabel(String label) { this.label = label; }
    public String getLabel() { return label; }

    public void setCode(int code) { this.code = code; }
    public int getCode() { return code; }

    public void increaseFrequency() { frequency++; }
    public void increaseFrequency(int amount) { frequency += amount; }

    public int getFrequency(){ return frequency; }

    public void incStartFrequency() { startFrequency++; }
    public void incEndFrequency() { endFrequency++; }

    public int getStartFrequency(){ return startFrequency;}
    public int getEndFrequency(){ return endFrequency;}

    public boolean isStartEvent() { return startFrequency != 0; }
    public boolean isEndEvent() { return endFrequency != 0; }

    @Override
    public int compareTo(Object o) {
        if( o instanceof LogNode) return id.compareTo(((LogNode)o).getID());
        else return -1;
    }

    @Override
    public boolean equals(Object o) {
        if( o instanceof LogNode) return id.equals(((LogNode)o).getID());
        else return false;
    }

}
