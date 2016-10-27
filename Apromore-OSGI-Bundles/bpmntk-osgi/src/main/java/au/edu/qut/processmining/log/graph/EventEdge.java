package au.edu.qut.processmining.log.graph;

/**
 * Created by Adriano on 15/06/2016.
 */

public class EventEdge implements Comparable {
    protected String id;
    protected String label;
    protected EventNode source;
    protected EventNode target;

    public EventEdge() {
        id = Long.toString(System.currentTimeMillis());
        source = null;
        target = null;
    }

    public EventEdge(EventNode source, EventNode target){
        id = Long.toString(System.currentTimeMillis());
        this.source = source;
        this.target = target;
    }
    public EventEdge(EventNode source, EventNode target, String label){
        id = Long.toString(System.currentTimeMillis());
        this.source = source;
        this.target = target;
        this.label = label;
    }

    public String getID() { return id; }

    public void setLabel(String label) { this.label = label; }
    public String getLabel() { return label; }

    public void setSource(EventNode source){ this.source = source; }
    public EventNode getSource(){ return source; }

    public void setTarget(EventNode target) { this.target = target; }
    public EventNode getTarget(){ return target; }

    @Override
    public int compareTo(Object o) {
        if( o instanceof EventEdge) return id.compareTo(((EventEdge)o).getID());
        else return -1;
    }

    @Override
    public boolean equals(Object o) {
        if( o instanceof EventEdge) return id.equals(((EventEdge)o).getID());
        else return false;
    }
}
