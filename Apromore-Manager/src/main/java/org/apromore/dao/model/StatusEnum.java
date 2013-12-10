package org.apromore.dao.model;

/**
 * Enumeration to definite all the different HistoryEvent Statuses that can occur.
 *
 * @author Cameron James
 * @since 1.0
 */
public enum StatusEnum {

    // Importing Models
    START("START"),
    FINISHED("FINISHED"),
    ERROR("ERROR");

    private String status;

    private StatusEnum(String status) {
        this.status = status;
    }

    /**
     * Get the name of the Enum.
     * @return the name.
     */
    public String getName() {
        return toString();
    }

    /**
     * Get the status of the Enum.
     * @return the status.
     */
    public String getStatus() {
        return status;
    }

    /**
     * Returns the status as a String.
     * @return the status as a string.
     */
    @Override
    public String toString() {
        return this.name();
    }
}
