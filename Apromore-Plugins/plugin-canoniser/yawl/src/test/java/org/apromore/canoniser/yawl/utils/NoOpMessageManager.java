package org.apromore.canoniser.yawl.utils;

import org.apromore.canoniser.yawl.internal.MessageManager;

public final class NoOpMessageManager implements MessageManager {
    @Override
    public void addMessage(final String message) {
        // Ignore
    }

    @Override
    public void addMessage(final String message, final Object... args) {
        // Ignore
    }
}