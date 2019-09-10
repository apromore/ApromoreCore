package org.apromore.logman.log.event;

public interface LogFilterListener {
    public void onLogFiltered(LogFilteredEvent filterEvent);
}
