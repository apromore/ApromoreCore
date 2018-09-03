/*
 * Copyright © 2009-2018 The Apromore Initiative.
 *
 * This file is part of "Apromore".
 *
 * "Apromore" is free software; you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 3 of the
 * License, or (at your option) any later version.
 *
 * "Apromore" is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty
 * of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this program.
 * If not, see <http://www.gnu.org/licenses/lgpl-3.0.html>.
 */

package org.apromore.plugin.portal.prodrift;

import org.apromore.model.LogSummaryType;
import org.apromore.prodrift.model.ProDriftDetectionResult;
import org.apromore.prodrift.util.XLogManager;
import org.apromore.plugin.portal.PortalContext;
import org.apromore.plugin.portal.prodrift.model.prodrift.Drift;
import org.apromore.service.EventLogService;
import org.deckfour.xes.model.XLog;
import org.zkoss.zk.ui.Session;
import org.zkoss.zk.ui.Sessions;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zul.*;
import org.zkoss.zul.Button;
import org.zkoss.zul.Label;
import org.zkoss.zul.Window;

import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Map;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;


public class ProDriftShowResult extends Window  {

    private static final long serialVersionUID = 1L;
    private final PortalContext portalContext;
    private Window proDriftW;

    private XLog xlog = null;
    private String logFileName = null;
    private Boolean isEventBased = true;

    private ProDriftDetectionResult result = null;


    private org.zkoss.zul.Image pValueDiagramImg;
    private Button saveSublogs;

    private Label saveMessage;

    EventLogService eventLogService = null;
    LogSummaryType logSummaryType = null;

//    private ListModel<Drift> driftsModel = new ListModelList<Drift>();

    /**
     * @throws IOException if the <code>prodriftshowresult.zul</code> template can't be read from the classpath
     */
    public ProDriftShowResult(PortalContext portalContext, ProDriftDetectionResult result,
                              boolean isEventBased, XLog xlog, String logFileName, boolean withCharacterization,
                              int cummulativeChange,
                              EventLogService eventLogService, LogSummaryType logSummaryType) throws IOException {
        this.portalContext = portalContext;
        this.result = result;
        this.xlog = xlog;
        this.logFileName = logFileName;
        this.isEventBased = isEventBased;

        this.eventLogService = eventLogService;
        this.logSummaryType = logSummaryType;

        if(isEventBased) {
            this.proDriftW = (Window) portalContext.getUI().createComponent(getClass().getClassLoader(), "zul/prodriftshowresult.zul", null, null);

            Label charLabel = (Label) this.proDriftW.getFellow("characterizationLabel");
            if(!withCharacterization)
                charLabel.setVisible(false);

        }else
            this.proDriftW = (Window) portalContext.getUI().createComponent(getClass().getClassLoader(), "zul/prodriftshowresultruns.zul", null, null);

        this.proDriftW.setTitle("ProDrift: Drift Detection Result.");

        this.saveMessage = (Label) this.proDriftW.getFellow("saveMessage");

        this.saveSublogs = (Button) this.proDriftW.getFellow("savesublogs");

        this.saveSublogs.addEventListener("onClick", new EventListener<Event>() {
            public void onEvent(Event event) throws Exception {
                saveOrDownloadSulogs();
            }
        });

        this.pValueDiagramImg = (org.zkoss.zul.Image) this.proDriftW.getFellow("pValueDiagramImg");
        BufferedImage img = new BufferedImage(result.getpValuesDiagram().getWidth(null), result.getpValuesDiagram().getHeight(null), BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2d = img.createGraphics();
        g2d.drawImage(result.getpValuesDiagram(), 0, 0, null);
        g2d.dispose();
        this.pValueDiagramImg.setContent(img);

//        this.resultDescription = (Listbox) this.proDriftW.getFellow("resultDescription");
//        resultDescription.setDisabled(true);

        List<BigInteger> driftPoints = result.getDriftPoints();
        Map<BigInteger, List<String>> characterizationMap = result.getCharacterizationMap();

        ListModel<Drift> driftsModel = new ListModelList<Drift>();

        for(int i = 0; i < result.getDriftStatements().size(); i++)
        {


            Drift drift = new Drift();
            BigInteger driftPoint = driftPoints.get(i);
            drift.setDriftPoint(driftPoint.longValue());
            drift.setDriftStatement("(" + (i+1) + ") " + result.getDriftStatements().get(i));
            List<String> charStatementsList = characterizationMap.get(driftPoint);
            ListModel<String> cs = new ListModelList<String>();

            if(charStatementsList != null){
                int ind = 0;
                for(String str : charStatementsList)
                    ((ListModelList<String>)cs).add("(" + (++ind) + ") " + str);
            }
            if(isEventBased) {
                if(withCharacterization)
                {
                    if(cs.getSize() == 0)
                    {
                        if(cummulativeChange == 100)
                            ((ListModelList<String>)cs).add("ProDrift could not characterize this drift!");
                        else
                            ((ListModelList<String>)cs).add("ProDrift could not characterize this drift! " +
                                    "Please increase the value of \"Cummulative change\" parameter in previous window and try again!");
                    }
                }
            }

            if(cs.getSize() == 0)
                ((ListModelList<String>)cs).add("");

            drift.setCharacterizationStatements(cs);

            ((ListModelList<Drift>)driftsModel).add(drift);

        }

        Grid grid = (Grid) this.proDriftW.getFellow("prodriftGrid");
        grid.setModel(driftsModel);


        this.proDriftW.doModal();

    }

    public void saveOrDownloadSulogs() {



        java.util.List<BigInteger> startOfTransitionPoints = result.getStartOfTransitionPoints();
        java.util.List<BigInteger> endOfTransitionPoints = result.getEndOfTransitionPoints();


        List<ByteArrayOutputStream> eventLogList = null;
        try {

            eventLogList = XLogManager.getSubLogs(xlog, logFileName, startOfTransitionPoints, endOfTransitionPoints, isEventBased);

        }catch (Exception ex)
        {
            showError(ex.getMessage());
            return;
        }


        if(logSummaryType != null)
        {
            saveLogs(eventLogList, startOfTransitionPoints, endOfTransitionPoints);
        }else
        {
            downloadLogs(eventLogList, startOfTransitionPoints, endOfTransitionPoints);
        }




    }


    private void saveLogs(List<ByteArrayOutputStream> eventLogList,
                          java.util.List<BigInteger> startOfTransitionPoints,
                          java.util.List<BigInteger> endOfTransitionPoints)
    {

        String extension = XLogManager.getExtension(logFileName);

        int successfulSave = 0;
        for (int i = 0; i < eventLogList.size(); i++)
        {

            int start = endOfTransitionPoints.get(i).intValue();
            int end  = startOfTransitionPoints.get(i).intValue();

            ByteArrayOutputStream outputStream = eventLogList.get(i);
            String filename = logFileName.substring(0, logFileName.indexOf(extension) - 1) + "_sublog" + "_" + start+"_" + end /*+ "." + extension*/;



            try {
                //eventLogService.exportToStream(outputStream, xlog);

                int folderId = portalContext.getCurrentFolder() == null ? 0 : portalContext.getCurrentFolder().getId();

                eventLogService.importLog(portalContext.getCurrentUser().getUsername(), folderId,
                        filename, new ByteArrayInputStream(outputStream.toByteArray()), extension,
                        logSummaryType.getDomain(), DatatypeFactory.newInstance().newXMLGregorianCalendar(new GregorianCalendar()).toString(),
                        logSummaryType.isMakePublic());

                successfulSave++;
               // portalContext.refreshContent();

            } catch (DatatypeConfigurationException e) {
                e.printStackTrace();
            } catch (Exception e) {
                e.printStackTrace();
            }

        }

        if(successfulSave > 0)
            this.saveMessage.setValue(successfulSave + " sublogs are saved next to the original log in the repository.");
        else
            this.saveMessage.setValue("No sublogs saved!");
    }

    private void downloadLogs(List<ByteArrayOutputStream> eventLogList,
                              java.util.List<BigInteger> startOfTransitionPoints,
                              java.util.List<BigInteger> endOfTransitionPoints)
    {
        byte[] downloadContent = null;
        ByteArrayOutputStream baos = null;
        ZipOutputStream zos = null;
        String extension = XLogManager.getExtension(logFileName);
        try
        {

            baos = new ByteArrayOutputStream();
            zos = new ZipOutputStream(baos);

            for (int i = 0; i < eventLogList.size(); i++)
            {

                int start = endOfTransitionPoints.get(i).intValue();
                int end  = startOfTransitionPoints.get(i).intValue();

                ByteArrayOutputStream outputStream = eventLogList.get(i);
                String filename = logFileName.substring(0, logFileName.indexOf(extension) - 1) + "_sublog" + "_" + start+"_" + end + "." + extension;

                ZipEntry entry = new ZipEntry(filename);

                entry.setSize(outputStream.size());
                zos.putNextEntry(entry);
                zos.write(outputStream.toByteArray());
                zos.closeEntry();
            }
            zos.close();
            baos.close();

            // this is the zip file as byte[]
            downloadContent = baos.toByteArray();

            Filedownload.save(downloadContent, "application/zip", logFileName.substring(0, logFileName.indexOf(extension) - 1) + "_sublogs.zip");

        }catch (IOException e)
        {
            showError("Failed to download sublogs!!");
            return;
        }
    }

    public void showError(String error) {
        Messagebox.show(error, "", Messagebox.OK, Messagebox.ERROR);
    }

    protected void close() {
        this.proDriftW.detach();
    }


}
