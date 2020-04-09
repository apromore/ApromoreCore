package org.apromore.service.csvimporter;

public interface LogErrorReport {
    int getRowIndex();
    int getColumnIndex();
    String getHeader();
    String getError();
}
