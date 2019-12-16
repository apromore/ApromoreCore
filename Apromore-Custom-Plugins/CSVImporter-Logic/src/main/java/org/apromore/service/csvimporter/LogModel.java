package org.apromore.service.csvimporter;

import java.util.List;
import org.deckfour.xes.model.XLog;

public interface LogModel {

    List<LogEventModel> getRows();

    long getLineCount();

    long getErrorCount();

    List<String> getInvalidRows();

    boolean getErrorCheck();

    XLog getXLog();
}
