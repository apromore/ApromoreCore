package org.apromore.graph.canonical;

/**
 * Implementation of the Canonical Split Node.
 *
 * @author Cameron James
 */
public abstract class Split extends Routing implements ISplit {

    /**
     * Empty constructor.
     */
    public Split() {
        super();
    }

    /**
     * Constructor with label of the place parameter.
     * @param label String to use as a label of this place.
     */
    public Split(String label) {
        super(label);
    }

    /**
     * Constructor with label and description of the place parameters.
     * @param label String to use as a label of this place.
     * @param desc  String to use as a description of this place.
     */
    public Split(String label, String desc) {
        super(label, desc);
    }

}
