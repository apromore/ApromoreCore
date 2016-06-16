package au.edu.qut.processmining.parser.graph;

/**
 * Created by Adriano on 15/06/2016.
 */
public class LogNode implements Comparable {
    private String id;
    private String name;
    private int weight;

    public LogNode(String name, int weight) {
        this.name = name;
        this.weight = weight;
        id = Long.toString(System.currentTimeMillis());
    }

    public LogNode(String name) {
        this.name = name;
        weight = 0;
    }

    public String getID() { return id; }
    public String getName() { return name; }
    public int getWeight(){ return weight; }

    @Override
    public int compareTo(Object o) {
        if( o instanceof LogNode ) return id.compareTo(((LogNode)o).getID());
        else return -1;
    }

    @Override
    public boolean equals(Object o) {
        if( o instanceof LogNode ) return id.equals(((LogNode)o).getID());
        else return false;
    }

}
