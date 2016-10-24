package au.edu.qut.processmining.log.graph.heuristic;

import au.edu.qut.processmining.log.graph.EventNode;

/**
 * Created by Adriano on 24/10/2016.
 */
public class HeuristicNode extends EventNode {
    //nothing new w.r.t. EventNode

    public HeuristicNode() { super(); }

    public HeuristicNode(String label) { super(label); }

    public HeuristicNode(int frequency) { super(frequency); }

    public HeuristicNode(String label, int frequency) { super(label, frequency); }
}
