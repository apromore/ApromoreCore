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

package org.apromore.portal.showresult;

import ee.ut.eventstr.test.AlphaBasedPosetReaderTest;
import ee.ut.mining.log.XLogReader;
import org.apache.commons.io.FileUtils;
import org.apromore.portal.dialogController.MainController;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.Session;
import org.zkoss.zk.ui.Sessions;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zul.*;

import java.awt.image.BufferedImage;
import java.io.*;
import java.math.BigInteger;
import java.util.List;
import java.util.zip.GZIPOutputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;


public class ProDriftShowResult extends Window {

    private static final long serialVersionUID = 1L;
    private MainController mainC;
    private Window proDriftW;



    private org.zkoss.zul.Image pValueDiagramImg;
    private Listbox resultDescription;
    private Button saveSublogs;


    public ProDriftShowResult(MainController mainC, java.awt.Image pValuesDiagram, java.util.List<BigInteger> driftPoints, java.util.List<BigInteger> lastReadTrace,
                              java.util.List<BigInteger> startOfTransitionPoints, java.util.List<BigInteger> endOfTransitionPoints){
        this.mainC = mainC;

        this.proDriftW = (Window) Executions.createComponents("macros/prodriftshowresult.zul", null, null);
        this.proDriftW.setTitle("ProDrift: Drift Detection Result.");

        this.saveSublogs = (Button) this.proDriftW.getFellow("savesublogs");

        this.saveSublogs.addEventListener("onClick", new EventListener<Event>() {
            public void onEvent(Event event) throws Exception {
                downloadSulogs();
            }
        });

        this.pValueDiagramImg = (org.zkoss.zul.Image) this.proDriftW.getFellow("pValueDiagramImg");
        BufferedImage img = (BufferedImage) pValuesDiagram;
        this.pValueDiagramImg.setContent(img);

        this.resultDescription = (Listbox) this.proDriftW.getFellow("resultDescription");
        resultDescription.setDisabled(true);
        for(int i = 0; i < driftPoints.size(); i++)
        {

            Listitem listItem = new Listitem();
            listItem.setLabel("Drift detected at " + driftPoints.get(i).intValue() + " after reading " + lastReadTrace.get(i).intValue() + " traces.");
            this.resultDescription.appendChild(listItem);
            listItem.setSelected(false);

        }

        Session sess = Sessions.getCurrent();
        sess.setAttribute("startOfTransitionPoints", startOfTransitionPoints);
        sess.setAttribute("endOfTransitionPoints", endOfTransitionPoints);


        this.proDriftW.doModal();
    }

    public void downloadSulogs() {


        byte[] downloadContent = null;
        Session sess = Sessions.getCurrent();
        java.util.List<BigInteger> startOfTransitionPoints = (java.util.List<BigInteger>)sess.getAttribute("startOfTransitionPoints");
        java.util.List<BigInteger> endOfTransitionPoints = (java.util.List<BigInteger>)sess.getAttribute("endOfTransitionPoints");
        byte[] log = (byte[])sess.getAttribute("log");
        String logName = (String)sess.getAttribute("logName");


        List<ByteArrayOutputStream> eventLogList = null;
        try {

            eventLogList = AlphaBasedPosetReaderTest.getSubLogs(log, logName, startOfTransitionPoints, endOfTransitionPoints);

        }catch (Exception ex)
        {
            showError(ex.getMessage());
            return;
        }


        ByteArrayOutputStream baos = null;
        ZipOutputStream zos = null;
        try
        {

            baos = new ByteArrayOutputStream();
            zos = new ZipOutputStream(baos);


            for (int i = 0; i < eventLogList.size(); i++)
            {

                int start = endOfTransitionPoints.get(i).intValue();
                int end  = startOfTransitionPoints.get(i).intValue();

                ByteArrayOutputStream ba = eventLogList.get(i);
                ByteArrayOutputStream outputStream;
                String filename;

                if(XLogReader.getExtension(logName).endsWith("gz")) {
                    outputStream = eventLogList.get(i);
                    filename = logName.substring(0, logName.indexOf(".")) + "_sublog" + "_" + start+"_" + end + "." + XLogReader.getExtension(logName);
                }else {
                    byte[] b = ba.toByteArray();
                    outputStream = new ByteArrayOutputStream(b.length);
                    GZIPOutputStream gzOS = new GZIPOutputStream(outputStream);
                    gzOS.write(b);
                    gzOS.close();
                    filename = logName.substring(0, logName.indexOf(".")) + "_sublog" + "_" + start+"_" + end + "." + XLogReader.getExtension(logName) + "gz";
                }

                ZipEntry entry = new ZipEntry(filename);

                entry.setSize(outputStream.toByteArray().length);
                zos.putNextEntry(entry);
                zos.write(outputStream.toByteArray());
                zos.closeEntry();
            }
            zos.close();
            baos.close();

            // this is the zip file as byte[]
            downloadContent = baos.toByteArray();

            Filedownload.save(downloadContent, "application/zip", logName.substring(0, logName.indexOf(".")) + "_sublogs.zip");

        }catch (IOException e)
        {
            showError("Failed to download sublogs!!");
            return;
        }


    }

    public void showError(String error) {
        mainC.displayMessage(error);
    }

    protected void close() {
        this.proDriftW.detach();
    }


}
