package org.apromore.graph.canonical;

/**
 * Implementation of the Canonical Or Join Node.
 *
 * @author Cameron James
 */
public class OrJoin extends Join implements IOrJoin {

    /**
     * Empty constructor.
     */
    public OrJoin() {
        super();
    }

    /**
     * Constructor with label of the place parameter.
     * @param label String to use as a label of this place.
     */
    public OrJoin(String label) {
        super(label);
    }

    /**
     * Constructor with label and description of the place parameters.
     * @param label String to use as a label of this place.
     * @param desc  String to use as a description of this place.
     */
    public OrJoin(String label, String desc) {
        super(label, desc);
    }

}
