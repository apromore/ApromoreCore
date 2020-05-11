package org.apromore.service.csvimporter.impl;

import lombok.Data;
import org.apromore.service.csvimporter.LogErrorReport;

@Data
class LogErrorReportImpl implements LogErrorReport {
    int rowIndex;
    int columnIndex;
    String error;
    String header;

    public LogErrorReportImpl(int rowIndex, int columnIndex, String header, String error) {
        this.rowIndex = rowIndex;
        this.error = error;
        this.columnIndex = columnIndex;
        this.header = header;
    }
}
