package org.apromore.graph.canonical;

/**
 * Implementation of the Canonical Message Event.
 *
 * @author Cameron James
 */
public class Message extends Event implements IMessage {

    private DirectionEnum direction;

    /**
     * Empty constructor.
     */
    public Message() {
        super();
    }

    /**
     * Constructor with label of the place parameter.
     * @param label String to use as a label of this place.
     */
    public Message(String label) {
        super(label);
    }

    /**
     * Constructor with label and description of the place parameters.
     * @param label String to use as a label of this place.
     * @param desc  String to use as a description of this place.
     */
    public Message(String label, String desc) {
        super(label, desc);
    }



    /**
     * @see IMessage#setDirection(org.apromore.graph.canonical.Message.DirectionEnum)
     */
    public void setDirection(final DirectionEnum newDirection) {
        direction = newDirection;
    }

    /**
     * @see IMessage#setDirection(org.apromore.graph.canonical.Message.DirectionEnum)
     */
    public DirectionEnum getDirection() {
        return direction;
    }

}
