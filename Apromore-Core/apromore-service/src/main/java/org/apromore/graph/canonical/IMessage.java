package org.apromore.graph.canonical;

/**
 * Interface to a Canonical Message Event.
 *
 * @author Cameron James
 */
public interface IMessage extends IEvent {

    /**
     * sets the direction of the message.
     * @param direction the message direction.
     */
    void setDirection(DirectionEnum direction);

    /**
     * Returns the direction of this message, inbound or outbound, but can be null.
     * @return the direction.
     */
    DirectionEnum getDirection();



    /**
     * Message direction.
     */
    public enum DirectionEnum {
        INBOUND, OUTBOUND
    }
}