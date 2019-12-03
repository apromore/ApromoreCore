package org.apromore.service.csvimporter;

import com.opencsv.CSVReader;
import org.deckfour.xes.model.XLog;
import org.zkoss.util.media.Media;
import org.zkoss.zul.*;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.Reader;
import java.util.HashMap;
import java.util.List;

/**
 * Service which converts event logs in CSV format to XES format.
 *
 * Conversion is currently stateful, requiring the methods {@link #sampleCSV},
 * {@link #prepareXesModel} and {@link #createXLog} to be invoked in series.
 */
public interface CSVImporterLogic {

    // Accessors

    void setHeads(List<String> line);
    HashMap<String, Integer> getHeads();
    void setLine(List<String>  line);
    void automaticFormat(ListModelList<String[]> result, List<String> myHeader);
    void resetLine();
    void resetHead();
    void resetList() ;
    void setOtherTimestamps(ListModelList<String[]> result);
    void setOtherAll(Window window);
    void setIgnoreAll(Window window);
    void setLists(int cols, HashMap<String, Integer> heads, String boxwidth);
    List<Listbox> getLists();
    void tryParsing(String format, int colPos);
    void openPopUp();
    void setPopUPBox(Div popUPBox);
    Boolean getErrorCheck();
    void setPopupID(String popupID);
    void setTextboxID(String textboxID);
    void setLabelID(String labelID);


    // Business logic methods

    LogSample sampleCSV(CSVReader reader) throws InvalidCSVException, IOException;
    LogModel prepareXesModel(CSVReader reader) throws InvalidCSVException, IOException;
    XLog createXLog(List<LogEventModel> traces);
    void toXESfile(XLog xLog, String FileName) throws FileNotFoundException, IOException;
}
