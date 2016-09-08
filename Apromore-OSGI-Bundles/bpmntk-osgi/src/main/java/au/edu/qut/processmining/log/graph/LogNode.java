package au.edu.qut.processmining.log.graph;

/**
 * Created by Adriano on 15/06/2016.
 */
public class LogNode implements Comparable {
    private String id;
    private String name;
    private int weight;

    int startingTimes;
    int endingTimes;

    public LogNode(String name, int weight) {
        this.name = name;
        this.weight = weight;
        startingTimes = 0;
        endingTimes = 0;
        id = Long.toString(System.currentTimeMillis());
    }

    public LogNode(String name) {
        this.name = name;
        weight = 0;
        startingTimes = 0;
        endingTimes = 0;
        id = Long.toString(System.currentTimeMillis());
    }

    public void incStartingTimes() { startingTimes++; }
    public void incEndingsTimes() { endingTimes++; }

    public void increaseWeight() { weight++; }
    public void increaseWeight(int amount) { weight += amount; }

    public String getID() { return id; }
    public String getName() { return name; }
    public int getWeight(){ return weight; }
    public int getStartingTimes(){ return startingTimes;}
    public int getEndingTimes(){ return endingTimes;}
    public boolean isStarting() { return startingTimes != 0; }
    public boolean isEnding() { return endingTimes != 0; }

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
