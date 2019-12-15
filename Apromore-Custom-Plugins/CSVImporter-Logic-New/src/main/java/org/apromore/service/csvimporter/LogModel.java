package org.apromore.service.csvimporter;

import java.util.List;

public class LogModel {

    private List<LogEventModel> rows;
    private long lineCount;
    private List<String> invalidRows;

    /**
     * @throws IllegalArgumentException if <var>rows</var> or <var>illegalRows</var> are <code>null</code>.i
     */
    public LogModel(List<LogEventModel> rows, long lineCount, List<String> invalidRows) {

        if (rows == null) { throw new IllegalArgumentException("Null rows"); }
        if (invalidRows == null) { throw new IllegalArgumentException("Null invalidRows"); }

        this.rows        = rows;
        this.lineCount   = lineCount;
        this.invalidRows = invalidRows;
    }

    public List<LogEventModel> getRows() { return rows; }

    public long getLineCount() { return lineCount; }

    public List<String> getInvalidRows() { return invalidRows; }
}
