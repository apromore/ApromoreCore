package org.apromore.service.csvimporter;

import org.zkoss.zul.Button;
import org.zkoss.zul.Div;
import org.zkoss.zul.Button;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Window;

import java.util.List;
import java.util.Map;

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

    Button[] getFormatBtns();

    void setFormatBtns(Button[] formatBtns);

    List<Integer> getCaseAttributesPos();
    // Public methods

    /**
     * Based on the sampled CSV lines, try to set the <var>timestampFormat</var> and <var>startTsFormat</var> properties.
     *
     * @return a non-null list of warning messages; an empty list suggests (but doesn't guarantee) correct guesses
     */
    List<String> automaticFormat();

    void openPopUp(boolean show);
    void setIgnoreAll(Window window);
    void setOtherAll(Window window);
    void setOtherTimestamps();
    void tryParsing(String format, int colPos);
}
