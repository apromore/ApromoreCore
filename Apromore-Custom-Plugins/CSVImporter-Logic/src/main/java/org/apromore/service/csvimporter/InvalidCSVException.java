package org.apromore.service.csvimporter;

import java.util.List;

/**
 * Indicates that a CSV file is too malformed to be processed at all.
 */
public class InvalidCSVException extends Exception {

    // Member fields

    /**
     * The unparsed text of invalid rows.
     *
     * This is retained in case the user needs to download it for troubleshooting purposes.
     * It's okay for it to be null; this indicates that the user doesn't need to be prompted
     * for whether they want to download the error report, for instance.
     */
    private List<String> invalidRows;


    // Constructors

    public InvalidCSVException(String message) {
        super(message);
    }

    public InvalidCSVException(String message, List<String> invalidRows) {
        super(message);
        this.invalidRows = invalidRows;
    }


    // Accessors

    /**
     * @return the unparsed text of invalid rows; may be null
     */
    public List<String> getInvalidRows() {
        return invalidRows;
    }
};
