package org.apromore.graph.canonical;

/**
 * Implementation of the Canonical State Node.
 *
 * @author Cameron James
 */
public class Routing extends Node implements IRouting {

    /**
     * Empty constructor.
     */
    public Routing() {
        super();
    }

    /**
     * Constructor with label of the place parameter.
     * @param label String to use as a label of this place.
     */
    public Routing(String label) {
        super(label);
    }

    /**
     * Constructor with label and description of the place parameters.
     * @param label String to use as a label of this place.
     * @param desc  String to use as a description of this place.
     */
    public Routing(String label, String desc) {
        super(label, desc);
    }
}
