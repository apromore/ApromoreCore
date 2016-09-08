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

package org.apromore.portal.dialogController;

import org.apromore.canoniser.Canoniser;
import org.apromore.model.ImportLogResultType;
import org.apromore.model.ImportProcessResultType;
import org.apromore.portal.common.UserSessionManager;
import org.apromore.portal.exception.DialogException;
import org.apromore.portal.exception.ExceptionAllUsers;
import org.apromore.portal.exception.ExceptionDomains;
import org.apromore.portal.exception.ExceptionImport;
import org.deckfour.xes.extension.std.XConceptExtension;
import org.deckfour.xes.factory.XFactory;
import org.deckfour.xes.factory.XFactoryNaiveImpl;
import org.deckfour.xes.in.*;
import org.deckfour.xes.model.XLog;
import org.deckfour.xes.out.XSerializer;
import org.deckfour.xes.out.XesXmlSerializer;
import org.processmining.plugins.bpmn.miner.preprocessing.functionaldependencies.DiscoverERmodel;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.SuspendNotAllowedException;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zk.ui.event.UploadEvent;
import org.zkoss.zk.ui.util.Clients;
import org.zkoss.zul.*;

import javax.xml.bind.JAXBException;
import javax.xml.datatype.DatatypeFactory;
import java.io.*;
import java.util.ArrayList;
import java.util.Collection;
import java.util.GregorianCalendar;
import java.util.List;

public class ImportLogController extends BaseController {

    private MainController mainC;
    private Window importLogWindow;
    private String extension;
    private boolean isPublic;
    private String supportedExtS = "xes, xes.gz, mxml, mxml.gz";

    private org.zkoss.util.media.Media logFile = null;
    private byte[] logByteArray = null;
    String logFileName = null;
    private Label fileNameLabel;
    private XLog log;

    private Button okButton;

    public ImportLogController(MainController mainC) throws DialogException {
        this.mainC = mainC;

        try {
            final Window win = (Window) Executions.createComponents("macros/importLog.zul", null, null);
            importLogWindow = (Window) win.getFellow("importLogWindow");
            Button uploadButton = (Button) this.importLogWindow.getFellow("uploadButton");
            okButton = (Button) this.importLogWindow.getFellow("okButtonImportLog");
            Button cancelButton = (Button) this.importLogWindow.getFellow("cancelButtonImportLog");
            fileNameLabel = (Label) this.importLogWindow.getFellow("fileName");
            Label supportedExtL = (Label) this.importLogWindow.getFellow("supportedExt");
            isPublic = ((Checkbox) this.importLogWindow.getFellow("public")).isChecked();

            // build the list of supported extensions to display
            supportedExtL.setValue(supportedExtS);

            // event listeners
            uploadButton.addEventListener("onUpload", new EventListener<Event>() {
                public void onEvent(Event event) throws Exception {
                    uploadFile((UploadEvent) event);
                }
            });
            okButton.addEventListener("onClick", new EventListener<Event>() {
                public void onEvent(Event event) throws Exception {
                    importLog();
                }
            });
            cancelButton.addEventListener("onClick", new EventListener<Event>() {
                public void onEvent(Event event) throws Exception {
                    cancel();
                }
            });

            win.doModal();
        } catch (Exception e) {
            throw new DialogException("Error in importProcesses controller: " + e.getMessage());
        }
    }

    private void cancel() throws IOException {
        this.importLogWindow.detach();
    }

    private void uploadFile(UploadEvent event) {
        logFile = event.getMedia();
        fileNameLabel.setStyle("color: blue");
        fileNameLabel.setValue(logFile.getName());
        logByteArray = logFile.getByteData();
        logFileName = logFile.getName();
        okButton.setDisabled(false);
    }

    private void importLog() {
        try {
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            bos.write(logByteArray, 0, logByteArray.length);
            InputStream inputStream = new ByteArrayInputStream(bos.toByteArray());
//            log = importFromStream(new XFactoryNaiveImpl(), inputStream, logFileName);
//        } catch (IOException e) {
//            e.printStackTrace();
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//
//        try {
            Integer folderId = 0;
            if (UserSessionManager.getCurrentFolder() != null) {
                folderId = UserSessionManager.getCurrentFolder().getId();
            }

            extension = discoverExtension(logFileName);
            System.out.println(logFileName);
            System.out.println(extension);
            String fileName = logFileName.substring(0, logFileName.indexOf(extension) - 1);
            System.out.println(fileName);

            ImportLogResultType importResult = getService().importLog(UserSessionManager.getCurrentUser().getUsername(), folderId, fileName, inputStream,
                    extension, "", DatatypeFactory.newInstance().newXMLGregorianCalendar(new GregorianCalendar()).toString(), isPublic);

//            this.mainC.showPluginMessages(importResult.getMessage());
//            this.mainC.displayNewProcess(importResult.getProcessSummary());
//
//            this.domainCB.addItem(domain);
//            this.importProcessesC.deleteFromToBeImported(this);
        } catch (Exception e) {
            Messagebox.show("Import failed (" + e.getMessage() + ")", "Attention", Messagebox.OK, Messagebox.ERROR);
        }finally {
            closePopup();
        }
    }

    private void closePopup() {
        importLogWindow.detach();
    }

    private String discoverExtension(String logFileName) {
        if(logFileName.endsWith("mxml")) {
            return "mxml";
        }else if(logFileName.endsWith("mxml.gz")) {
            return "mxml.gz";
        }else if(logFileName.endsWith("xes")) {
            return "xes";
        }else if(logFileName.endsWith("xes.gz")) {
            return "xes.gz";
        }
        return null;
    }

}
