package org.apromore.graph.canonical;

/**
 * Implementation of the Canonical Task Node.
 *
 * @author Cameron James
 */
public class Event extends Work implements IEvent {

    /**
     * Empty constructor.
     */
    public Event() {
        super();
    }

    /**
     * Constructor with label of the place parameter.
     * @param label String to use as a label of this place.
     */
    public Event(String label) {
        super(label);
    }

    /**
     * Constructor with label and description of the place parameters.
     * @param label String to use as a label of this place.
     * @param desc  String to use as a description of this place.
     */
    public Event(String label, String desc) {
        super(label, desc);
    }

}
