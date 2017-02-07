package au.edu.qut.processmining.log.graph;

import java.util.UUID;

/**
 * Created by Adriano on 15/06/2016.
 */

public class LogEdge implements Comparable {
    protected String id;
    protected String label;
    protected LogNode source;
    protected LogNode target;

    public LogEdge() {
        id = UUID.randomUUID().toString();
        source = null;
        target = null;
    }

    public LogEdge(LogNode source, LogNode target){
        id = UUID.randomUUID().toString();
        this.source = source;
        this.target = target;
    }
    public LogEdge(LogNode source, LogNode target, String label){
        id = UUID.randomUUID().toString();
        this.source = source;
        this.target = target;
        this.label = label;
    }

    public String getID() { return id; }

    public void setLabel(String label) { this.label = label; }
    public String getLabel() { return label; }

    public void setSource(LogNode source){ this.source = source; }
    public LogNode getSource(){ return source; }

    public void setTarget(LogNode target) { this.target = target; }
    public LogNode getTarget(){ return target; }

    @Override
    public int compareTo(Object o) {
        if( o instanceof LogEdge) return id.compareTo(((LogEdge)o).getID());
        else return -1;
    }

    @Override
    public boolean equals(Object o) {
        if( o instanceof LogEdge) return id.equals(((LogEdge)o).getID());
        else return false;
    }
}
