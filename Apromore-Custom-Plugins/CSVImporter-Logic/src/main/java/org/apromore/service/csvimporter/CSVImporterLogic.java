package org.apromore.service.csvimporter;

import com.opencsv.CSVReader;
import java.io.IOException;
import java.util.List;
import org.zkoss.zul.Div;
import org.zkoss.zul.ListModelList;
import org.zkoss.zul.Window;


/**
 * Service which converts event logs in CSV format to XES format.
 *
 * Conversion is currently stateful, requiring the methods {@link #sampleCSV}
 * and {@link #prepareXesModel} to be invoked in series.
 * The converted XES model is obtained using {@link LogModel#getXLog}.
 */
public interface CSVImporterLogic {

    // Accessors

    void automaticFormat(ListModelList<String[]> result, List<String> myHeader, LogSample sample);
    void setOtherTimestamps(ListModelList<String[]> result, List<String> sampleLine, LogSample sample);
    void setOtherAll(Window window, List<String> sampleLine, LogSample sample);
    void setIgnoreAll(Window window, List<String> sampleLine, LogSample sample);
    void tryParsing(String format, int colPos, List<String> sampleLine, LogSample sample);
    void openPopUp(LogSample sample);
    void setPopUPBox(Div popUPBox);
    void setPopupID(String popupID);
    void setTextboxID(String textboxID);
    void setLabelID(String labelID);

    // Business logic methods

    LogSample sampleCSV(CSVReader reader, int sampleSize) throws InvalidCSVException, IOException;
    LogModel prepareXesModel(CSVReader reader, LogSample sample) throws InvalidCSVException, IOException;
}
