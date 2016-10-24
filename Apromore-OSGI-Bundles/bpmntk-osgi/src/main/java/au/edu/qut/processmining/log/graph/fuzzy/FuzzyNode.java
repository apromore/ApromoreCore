package au.edu.qut.processmining.log.graph.fuzzy;

import au.edu.qut.processmining.log.graph.EventNode;

/**
 * Created by Adriano on 15/06/2016.
 */
public class FuzzyNode extends EventNode implements Comparable {
        //nothing new w.r.t. EventNode

    public FuzzyNode() { super(); }

    public FuzzyNode(String label) { super(label); }

    public FuzzyNode(int frequency) { super(frequency); }

    public FuzzyNode(String label, int frequency) { super(label, frequency); }
}
