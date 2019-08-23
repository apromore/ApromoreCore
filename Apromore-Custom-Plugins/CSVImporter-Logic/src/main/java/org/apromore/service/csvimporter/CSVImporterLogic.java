package org.apromore.service.csvimporter;

import com.opencsv.CSVReader;
import org.apromore.service.csvimporter.impl.LogModel;
import org.deckfour.xes.model.XLog;
import org.zkoss.util.media.Media;
import org.zkoss.zul.Div;
import org.zkoss.zul.ListModel;
import org.zkoss.zul.ListModelList;
import org.zkoss.zul.Listbox;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.Reader;
import java.util.HashMap;
import java.util.List;

/**
 * Sample service API.
 */
public interface CSVImporterLogic {

    /**
     * Sample service API method.
     *
     * @param n  parameter the service requires
     * @return some result
     * @throws SampleException if something goes wrong
     */
    List<LogModel> prepareXesModel(CSVReader r);
    void setHeads(String[] line);
    HashMap<String, Integer> getHeads();
    void setLine(String[] line);
    void automaticFormat(ListModelList<String[]> result, String[] myHeader);
     void resetLine();
     void resetHead();
     void resetList() ;
     void setOtherTimestamps(ListModelList<String[]> result);
    void setLists(int cols, HashMap<String, Integer> heads, String boxwidth);
     List<Listbox> getLists();
    void tryParsing(String format, int colPos);
    void openPopUp();
     void setPopUPBox(Div popUPBox);

     void setPopupID(String popupID);

     void setTextboxID(String textboxID);

     void setLabelID(String labelID);
    XLog createXLog(List<LogModel> traces);
    void toXESfile(XLog xLog, String FileName) throws FileNotFoundException, IOException;
    /**
     * Something that might go wrong.
     */
    public static class SampleException extends Exception {
    }
}
