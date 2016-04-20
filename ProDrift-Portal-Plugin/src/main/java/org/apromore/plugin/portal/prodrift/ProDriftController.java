/*
 * Copyright © 2009-2016 The Apromore Initiative.
 *
 * This file is part of "Apromore".
 *
 * "Apromore" is free software; you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 3 of the
 * License, or (at your option) any later version.
 *
 * "Apromore" is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this program.
 * If not, see <http://www.gnu.org/licenses/lgpl-3.0.html>.
 */

package org.apromore.plugin.portal.prodrift;

import ee.ut.eventstr.model.ProDriftDetectionResult;
import ee.ut.eventstr.test.AlphaBasedPosetReaderTest;
import org.apromore.plugin.portal.PortalContext;
import org.apromore.service.prodrift.ProDriftDetectionService;
import org.zkoss.zk.ui.Session;
import org.zkoss.zk.ui.Sessions;
import org.zkoss.zk.ui.SuspendNotAllowedException;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zk.ui.event.UploadEvent;
import org.zkoss.zul.*;

import java.io.IOException;
import java.math.BigInteger;


public class ProDriftController {

    private static final long serialVersionUID = 1L;
    private final PortalContext portalContext;
    private final ProDriftDetectionService proDriftDetectionService;
    private Window proDriftW;

    private Button logFileUpload;
    private Row winSize;
    private Listbox fWinOrAwin;
    private Button OKbutton;

    private org.zkoss.util.media.Media logFile = null;
    private byte[] logByteArray = null;
    String logFileName = null;

    /**
     * @throws IOException if the <code>prodrift.zul</code> template can't be read from the classpath
     */
    public ProDriftController(PortalContext portalContext, ProDriftDetectionService proDriftDetectionService) throws IOException {
        this.portalContext = portalContext;
        this.proDriftDetectionService = proDriftDetectionService;

        this.proDriftW = (Window) portalContext.getUI().createComponent(getClass().getClassLoader(), "zul/prodrift.zul", null, null);
        this.proDriftW.setTitle("ProDrift: Set Parameters.");

        this.logFileUpload = (Button) this.proDriftW.getFellow("logFileUpload");
        final Label l = (Label) this.proDriftW.getFellow("fileName");


        this.logFileUpload.addEventListener("onUpload", new EventListener<Event>() {
            public void onEvent(Event event) throws Exception {
                UploadEvent uEvent = (UploadEvent) event;
                logFile = uEvent.getMedia();


                boolean isValid = AlphaBasedPosetReaderTest.validateLog(logFile.getStreamData(), logFile.getName());
                if (!isValid) {

                    l.setStyle("color: red");
                    l.setValue("Unacceptable File Format.");

                    showError("Please select a log file(.mxml, .xml, .xes)");

                } else {

                    l.setStyle("color: blue");
                    l.setValue(logFile.getName());
                    logByteArray = logFile.getByteData();
                    logFileName = logFile.getName();

                    Session sess = Sessions.getCurrent();
                    sess.setAttribute("log", logByteArray);
                    sess.setAttribute("logName", logFileName);


                }


            }
        });

        this.winSize = (Row) this.proDriftW.getFellow("winsize");
        Row fWinOrAwinChoiceR = (Row) this.proDriftW.getFellow("fWinOrAwinChoice");
        this.fWinOrAwin = (Listbox) fWinOrAwinChoiceR.getFirstChild().getNextSibling();
        Listitem listItem = new Listitem();
        listItem.setLabel("Adaptive Window");
        this.fWinOrAwin.appendChild(listItem);
        listItem.setSelected(true);
        listItem = new Listitem();
        listItem.setLabel("Fixed Window");
        this.fWinOrAwin.appendChild(listItem);

        this.OKbutton = (Button) this.proDriftW.getFellow("proDriftOKButton");
        Button cancelButton = (Button) this.proDriftW.getFellow("proDriftCancelButton");

        this.OKbutton.addEventListener("onClick", new EventListener<Event>() {
            public void onEvent(Event event) throws Exception {
                proDriftDetector();
            }
        });
        this.OKbutton.addEventListener("onOK", new EventListener<Event>() {
            public void onEvent(Event event) throws Exception {
                proDriftDetector();
            }
        });
        cancelButton.addEventListener("onClick", new EventListener<Event>() {
            public void onEvent(Event event) throws Exception {
                cancel();
            }
        });



        this.proDriftW.doModal();
    }

    public void showError(String error) {
        portalContext.getMessageHandler().displayInfo(error);
    }

    protected void cancel() {
        showError(""); this.proDriftW.detach();
    }

    protected void proDriftDetector() {
        String message;
        if (logByteArray != null)
        {
            try {

                String selectedWinType = this.fWinOrAwin.getSelectedItem().getLabel();
                if(selectedWinType.startsWith("F"))
                    selectedWinType = "FWIN";
                else
                    selectedWinType = "AWIN";

                ProDriftDetectionResult result = proDriftDetectionService.proDriftDetector(logByteArray,
                        ((Intbox) this.winSize.getFirstChild().getNextSibling()).getValue(),
                        selectedWinType, logFileName);

                proDriftShowResults_(result.getpValuesDiagram(), result.getDriftPoints(), result.getLastReadTrace(), result.getStartOfTransitionPoints(), result.getEndOfTransitionPoints());
                message = "Completed Successfully";

            } catch (Exception e) {
                message = "ProDrift failed (" + e.getMessage() + ")";
                e.printStackTrace();
            }
            portalContext.getMessageHandler().displayInfo(message);
//            this.proDriftW.detach();
        }else
        {
            showError("Please select a log file first.");
        }
    }


    protected void proDriftShowResults_(java.awt.Image pValuesDiagram, java.util.List<BigInteger> driftPoints, java.util.List<BigInteger> lastReadTrace,
                                         java.util.List<BigInteger> startOfTransitionPoints, java.util.List<BigInteger> endOfTransitionPoints) {
        try {

            new ProDriftShowResult(portalContext, pValuesDiagram, driftPoints, lastReadTrace, startOfTransitionPoints, endOfTransitionPoints);

        } catch (IOException | SuspendNotAllowedException e) {
            Messagebox.show(e.getMessage(), "Attention", Messagebox.OK, Messagebox.ERROR);
        }
    }

}
