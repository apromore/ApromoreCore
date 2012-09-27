package org.apromore.canoniser.yawl.internal;

public interface MessageManager {

    void addMessage(String message);

    void addMessage(String message, Object... args);

}
