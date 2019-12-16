package org.apromore.service.csvimporter;

import java.util.List;
import java.util.Map;
import org.zkoss.zul.Div;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Window;

/**
 * A sample of a CSV log.
 */
public interface LogSample {

    // Constants (TODO: remove these, as they are UI-related)

    final String popupID = "pop_";
    final String textboxID = "txt_";
    final String labelID = "lbl_";


    // Accessors

    List<String> getHeader();

    List<List<String>> getLines();

    Map<String, Integer> getHeads();

    String getTimestampFormat();

    void setTimestampFormat(String s);

    String getStartTsFormat();

    void setStartTsFormat(String s);

    List<Listbox> getLists();

    List<Integer> getIgnoredPos();

    Map<Integer, String> getOtherTimeStampsPos();

    Div getPopUPBox();

    void setPopUPBox(Div popUPBox);


    // Public methods

    void automaticFormat(List<String[]> result, List<String> myHeader, LogSample sample);
    void openPopUp(LogSample sample);
    void setIgnoreAll(Window window, List<String> sampleLine, LogSample sample);
    void setOtherAll(Window window, List<String> line, LogSample sample);
    void setOtherTimestamps(List<String[]> result, List<String> sampleLine, LogSample sample);
    void tryParsing(String format, int colPos, List<String> sampleLine, LogSample sample);
}
