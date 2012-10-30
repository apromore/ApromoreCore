package org.apromore.graph.canonical;

/**
 * Implementation of the Canonical Join Node.
 *
 * @author Cameron James
 */
public abstract class Join extends Routing implements IJoin {

    /**
     * Empty constructor.
     */
    public Join() {
        super();
    }

    /**
     * Constructor with label of the place parameter.
     * @param label String to use as a label of this place.
     */
    public Join(String label) {
        super(label);
    }

    /**
     * Constructor with label and description of the place parameters.
     * @param label String to use as a label of this place.
     * @param desc  String to use as a description of this place.
     */
    public Join(String label, String desc) {
        super(label, desc);
    }

}
