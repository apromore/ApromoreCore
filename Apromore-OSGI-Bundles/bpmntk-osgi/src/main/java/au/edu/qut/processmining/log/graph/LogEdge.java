package au.edu.qut.processmining.log.graph;

/**
 * Created by Adriano on 15/06/2016.
 */

public class LogEdge implements Comparable {
    private String id;
    private int weight;
    private LogNode source;
    private LogNode target;

    public LogEdge(LogNode source, LogNode target){
        this.source = source;
        this.target = target;
        weight = 0;
        id = Long.toString(System.currentTimeMillis());
    }

    public LogEdge(LogNode source, LogNode target, int weight){
        this.source = source;
        this.target = target;
        this.weight = weight;
        id = Long.toString(System.currentTimeMillis());
    }

    public int increaseWeight() {
        weight++;
        return weight;
    }

    public int increaseWeight(int amount) {
        weight += amount;
        return weight;
    }

    public String getID() { return id; }
    public int getWeight(){ return weight; }
    public LogNode getSource(){ return source; }
    public LogNode getTarget(){ return target; }

    @Override
    public int compareTo(Object o) {
        if( o instanceof LogEdge ) return id.compareTo(((LogEdge)o).getID());
        else return -1;
    }

    @Override
    public boolean equals(Object o) {
        if( o instanceof LogEdge ) return id.equals(((LogEdge)o).getID());
        else return false;
    }
}
