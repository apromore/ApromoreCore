package de.hpi.bpmn2_0.replay;

import de.hpi.bpmn2_0.model.connector.SequenceFlow;
import java.util.Set;

/*
* ORJoinLabel represents set of incoming edges of an OR-join that an external edge can reach
* Thus, each edge with a path to one or more input edges of an OR-join has a label
* Multiple edges may share the same label because they can reach the same incoming edges of an OR-join
* Each label has a counter which counts the number of tokens residing on all edges sharing the same label
*/
public class ORJoinLabel {
    private Set<SequenceFlow> edges;
    private ORJoinCounter counter = new ORJoinCounter();
    private ORJoinLabelColor color = ORJoinLabelColor.NONE;
    

    
    //Each added edge is an incoming edge of an OR-Join
    public ORJoinLabel(Set<SequenceFlow> edges) {
        this.edges = edges;
    }
    
    public Set<SequenceFlow> getEdges() {
        return this.edges;
    }
    
    public ORJoinCounter getCounter() {
        return counter;
    }
    
    public boolean contains(SequenceFlow edge) {
        return this.edges.contains(edge);
    }
    
    public ORJoinLabelColor getColor() {
        return color;
    }
    
    public void setColor(ORJoinLabelColor color) {
        this.color = color;
    }
    
    public boolean isRed() {
        return (this.color == ORJoinLabelColor.RED);
    }
    
    public boolean isBlue() {
        return (this.color == ORJoinLabelColor.BLUE);
    }
    
    public void reset() {
        counter.reset();
    }
}