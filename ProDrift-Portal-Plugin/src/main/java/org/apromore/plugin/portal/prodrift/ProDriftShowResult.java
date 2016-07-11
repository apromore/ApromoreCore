/*
 * Copyright Â© 2009-2016 The Apromore Initiative.
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
import ee.ut.eventstr.util.XLogManager;
import org.apromore.plugin.portal.PortalContext;
import org.zkoss.zk.ui.Session;
import org.zkoss.zk.ui.Sessions;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zul.*;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.*;
import java.math.BigInteger;
import java.util.List;
import java.util.zip.GZIPOutputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;


public class ProDriftShowResult extends Window {

    private static final long serialVersionUID = 1L;
    private final PortalContext portalContext;
    private Window proDriftW;



    private org.zkoss.zul.Image pValueDiagramImg;
    private Listbox resultDescription;
    private Button saveSublogs;

    /**
     * @throws IOException if the <code>prodriftshowresult.zul</code> template can't be read from the classpath
     */
    public ProDriftShowResult(PortalContext portalContext, ProDriftDetectionResult result) throws IOException {
        this.portalContext = portalContext;

        this.proDriftW = (Window) portalContext.getUI().createComponent(getClass().getClassLoader(), "zul/prodriftshowresult.zul", null, null);
        this.proDriftW.setTitle("ProDrift: Drift Detection Result.");

        this.saveSublogs = (Button) this.proDriftW.getFellow("savesublogs");

        this.saveSublogs.addEventListener("onClick", new EventListener<Event>() {
            public void onEvent(Event event) throws Exception {
                downloadSulogs();
            }
        });

        this.pValueDiagramImg = (org.zkoss.zul.Image) this.proDriftW.getFellow("pValueDiagramImg");
        BufferedImage img = new BufferedImage(result.getpValuesDiagram().getWidth(null), result.getpValuesDiagram().getHeight(null), BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2d = img.createGraphics();
        g2d.drawImage(result.getpValuesDiagram(), 0, 0, null);
        g2d.dispose();
        this.pValueDiagramImg.setContent(img);

        this.resultDescription = (Listbox) this.proDriftW.getFellow("resultDescription");
        resultDescription.setDisabled(true);
        for(int i = 0; i < result.getDriftStatements().size(); i++)
        {

            Listitem listItem = new Listitem();
            listItem.setLabel("(" + (i+1) + ") " + result.getDriftStatements().get(i));
            this.resultDescription.appendChild(listItem);
            listItem.setSelected(false);

        }

        Session sess = Sessions.getCurrent();
        sess.setAttribute("startOfTransitionPoints", result.getStartOfTransitionPoints());
        sess.setAttribute("endOfTransitionPoints", result.getEndOfTransitionPoints());


        this.proDriftW.doModal();
    }

    public void downloadSulogs() {


        byte[] downloadContent = null;
        Session sess = Sessions.getCurrent();
        java.util.List<BigInteger> startOfTransitionPoints = (java.util.List<BigInteger>)sess.getAttribute("startOfTransitionPoints");
        java.util.List<BigInteger> endOfTransitionPoints = (java.util.List<BigInteger>)sess.getAttribute("endOfTransitionPoints");
        byte[] log = (byte[])sess.getAttribute("logDrift");
        String logName = (String)sess.getAttribute("logNameDrift");
        Boolean isEventBased = (Boolean)sess.getAttribute("isEventBased");


        List<ByteArrayOutputStream> eventLogList = null;
        try {

            eventLogList = XLogManager.getSubLogs(log, logName, startOfTransitionPoints, endOfTransitionPoints, isEventBased);

        }catch (Exception ex)
        {
            showError(ex.getMessage());
            return;
        }


        ByteArrayOutputStream baos = null;
        ZipOutputStream zos = null;
        String extension = XLogManager.getExtension(logName);
        try
        {

            baos = new ByteArrayOutputStream();
            zos = new ZipOutputStream(baos);


            for (int i = 0; i < eventLogList.size(); i++)
            {

                int start = endOfTransitionPoints.get(i).intValue();
                int end  = startOfTransitionPoints.get(i).intValue();

                ByteArrayOutputStream ba = eventLogList.get(i);
                String filename = logName.substring(0, logName.indexOf(extension) - 1) + "_sublog" + "_" + start+"_" + end + "." + extension;
  
                ZipEntry entry = new ZipEntry(filename);
  
                entry.setSize(ba.toByteArray().length);
                zos.putNextEntry(entry);
                zos.write(ba.toByteArray());
                zos.closeEntry();
            }
            zos.close();
            baos.close();

            // this is the zip file as byte[]
            downloadContent = baos.toByteArray();

            Filedownload.save(downloadContent, "application/zip", logName.substring(0, logName.indexOf(extension) - 1) + "_sublogs.zip");

        }catch (IOException e)
        {
            showError("Failed to download sublogs!!");
            return;
        }


    }

    public void showError(String error) {
        portalContext.getMessageHandler().displayError(error, null);
    }

    protected void close() {
        this.proDriftW.detach();
    }


}
