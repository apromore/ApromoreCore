package org.apromore.dao.model;

/**
 * Enumeration to definite all the different HistoryEvent Events that can occur.
 *
 * @author Cameron James
 * @since 1.0
 */
public enum HistoryEnum {

    IMPORT_PROCESS_MODEL("IMPORT_PROCESS_MODEL"),
    UPDATE_PROCESS_MODEL("UPDATE_PROCESS_MODEL"),
    GED_MATRIX_COMPUTATION("GED_MATRIX_COMPUTATION"),
    CLUSTERING_COMPUTATION("CLUSTERING_COMPUTATION");

    private String type;


    private HistoryEnum(String type) {
        this.type = type;
    }

    /**
     * Get the name of the Enum.
     * @return the name.
     */
    public String getName() {
        return toString();
    }

    /**
     * Get the type of the Enum.
     * @return the type.
     */
    public String getType() {
        return type;
    }

    /**
     * Returns the Role as a String.
     * @return the enum as a string.
     */
    @Override
    public String toString() {
        return this.name();
    }
}
