package org.apromore.service.csvimporter;

import com.opencsv.CSVReader;
import java.io.IOException;
import java.util.List;
import org.zkoss.zul.Div;
import org.zkoss.zul.Listbox;
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

    void automaticFormat(ListModelList<String[]> result, List<String> myHeader);
    void setOtherTimestamps(ListModelList<String[]> result);
    void setOtherAll(Window window);
    void setIgnoreAll(Window window);
    List<Listbox> getLists();
    void tryParsing(String format, int colPos);
    void openPopUp();
    void setPopUPBox(Div popUPBox);
    void setPopupID(String popupID);
    void setTextboxID(String textboxID);
    void setLabelID(String labelID);

    // Business logic methods

    LogSample sampleCSV(CSVReader reader, int sampleSize) throws InvalidCSVException, IOException;
    LogModel prepareXesModel(CSVReader reader) throws InvalidCSVException, IOException;
}
