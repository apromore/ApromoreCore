package org.apromore.service.csvimporter.impl;

import org.apromore.service.csvimporter.CSVImporterLogic;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Arrays;
import java.util.List;
//import org.deckfour.xes.model.XLog;
import org.zkoss.util.media.Media;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zul.*;
import org.zkoss.zul.Messagebox;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.event.UploadEvent;
import org.zkoss.zk.ui.select.SelectorComposer;
import org.zkoss.zk.ui.select.annotation.Listen;
import org.zkoss.zk.ui.select.annotation.Wire;
import com.opencsv.CSVReader;
/**
 * Sample service implementation.
 */
class CSVImporterLogicImpl implements CSVImporterLogic {

    /**
     * The my grid.
     */
    @Wire
    private Grid myGrid;

    @Wire
    private Div attrBox;

    @Wire
    private Button toXESButton;

    private Media media;


    @Wire
    private Div popUPBox;

    private static String popupID = "pop_";
    private static String textboxID = "txt_";
    private static String labelID = "lbl_";
    String[] returnHeader;
    String[] returnLine;
    private static Integer AttribWidth = 300;
    /**
     * Spring bean constructor.
     */
    public CSVImporterLogicImpl() {
        // more to come
    }


    /**
     * {@inheritDoc}
     *
     * This implementation has noteworthy features.
     */
    public String method(int n) {
        return "METHOD IS CALLED!";
//        throw new SampleException();
    }



}
