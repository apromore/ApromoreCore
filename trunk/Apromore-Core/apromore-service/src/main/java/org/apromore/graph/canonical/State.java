package org.apromore.graph.canonical;

/**
 * Implementation of the Canonical State Node.
 *
 * @author Cameron James
 */
public class State extends Routing implements IState {

    /**
     * Empty constructor.
     */
    public State() {
        super();
    }

    /**
     * Constructor with label of the place parameter.
     * @param label String to use as a label of this place.
     */
    public State(String label) {
        super(label);
    }

    /**
     * Constructor with label and description of the place parameters.
     * @param label String to use as a label of this place.
     * @param desc  String to use as a description of this place.
     */
    public State(String label, String desc) {
        super(label, desc);
    }
}
