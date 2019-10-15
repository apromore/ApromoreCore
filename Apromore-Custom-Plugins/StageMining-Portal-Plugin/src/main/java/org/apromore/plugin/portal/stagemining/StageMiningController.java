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

package org.apromore.plugin.portal.stagemining;

import org.apromore.plugin.portal.PortalContext;
import org.zkoss.zk.ui.Session;
import org.zkoss.zk.ui.Sessions;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zk.ui.event.UploadEvent;
import org.zkoss.zul.*;

import java.io.IOException;
import java.text.ParseException;
import java.util.Map;
import org.apromore.service.stagemining.StageMiningService;
import org.deckfour.xes.model.XLog;
import org.json.JSONException;
import org.processmining.stagemining.models.DecompositionTree;
import org.processmining.stagemining.utils.LogUtilites;
import org.zkoss.zk.ui.SuspendNotAllowedException;
import org.zkoss.zul.ext.Selectable;


public class StageMiningController {

    private static final long serialVersionUID = 1L;
    private final PortalContext portalContext;
    private final StageMiningService stageMiningService;
    private Window entryW;
    private boolean showImportButton = true;
    private Button logFileUpload;
    private Button OKbutton;

    private org.zkoss.util.media.Media logFile = null;
    private byte[] logByteArray = null;
    private String logFileName = null;
    private XLog log = null;

    /**
     * @throws IOException if the <code>stagemining.zul</code> template can't be read from the classpath
     */
    public StageMiningController(PortalContext portalContext, StageMiningService stageMiningService, Map<XLog, String> logs) throws IOException {
        this.portalContext = portalContext;
        this.stageMiningService = stageMiningService;
        
        if(logs.size() > 0) {
            if(logs.size() > 1) {
                showError("Please select only one log!");
                return;
            } 
            else {
                this.showImportButton = false;
                this.log = logs.keySet().iterator().next();
                try {
                    LogUtilites.addStartEndEvents(log);
                }
                catch (Exception e) {
                    showError(e.getMessage());
                }
            }
        }
        else {
            this.showImportButton = true;
        }
        
        this.entryW = (Window) portalContext.getUI().createComponent(getClass().getClassLoader(), "zul/stagemining.zul", null, null);
        this.entryW.setTitle("Stage Mining Parameters");
        this.logFileUpload = (Button) this.entryW.getFellow("logFileUpload");
        final Label l = (Label) this.entryW.getFellow("fileName");
        this.logFileUpload.setVisible(showImportButton);
        this.OKbutton = (Button) this.entryW.getFellow("OKButton");
        OKbutton.setDisabled(showImportButton);
        Button cancelButton = (Button) this.entryW.getFellow("CancelButton");

        this.logFileUpload.addEventListener("onUpload", new EventListener<Event>() {
            public void onEvent(Event event) throws Exception {
                UploadEvent uEvent = (UploadEvent) event;
                logFile = uEvent.getMedia();
                if (logFile == null) {
                    showError("Upload error. No file uploaded.");
                    return;
                }
                logFileName = logFile.getName();
                OpenLogFilePlugin logImporter = new OpenLogFilePlugin();
                try {
                    System.out.println("Import log file");
                    log = (XLog)logImporter.importFromStream(logFile.getStreamData(), logFileName);
                    LogUtilites.addStartEndEvents(log);
                    OKbutton.setDisabled(false);
                }
                catch (Exception e) {
                    showError(e.getMessage());
                    OKbutton.setDisabled(true);
                }

                Session sess = Sessions.getCurrent();
                sess.setAttribute("log", log);
            }
        });

        this.OKbutton.addEventListener("onClick", new EventListener<Event>() {
            public void onEvent(Event event) throws Exception {
                mineStage();
            }
        });
        
        this.OKbutton.addEventListener("onOK", new EventListener<Event>() {
            public void onEvent(Event event) throws Exception {
                mineStage();
            }
        });
        cancelButton.addEventListener("onClick", new EventListener<Event>() {
            public void onEvent(Event event) throws Exception {
                cancel();
            }
        });
        
        this.entryW.doModal();
    }

    public void showError(String error) {
        Messagebox.show(error, "Error", 0, Messagebox.ERROR);
//        Label errorLabel = (Label) this.entryW.getFellow("errorLabel");
//        errorLabel.setValue(error);
    }

    protected void cancel() {
        showError(""); this.entryW.detach();
    }

    protected void mineStage() {
        try {
            Intbox txtMinSS = (Intbox) this.entryW.getFellow("minStageSize");
            DecompositionTree tree = stageMiningService.mine(this.log, Integer.valueOf(txtMinSS.getValue()).intValue());
            showResults(tree);
        } catch (Exception e) {
            String message = "StageMining failed (" + e.getMessage() + ")";
            showError(message);
        }
    }
    
    protected void showResults(DecompositionTree tree) {
        try {

            new StageMiningShowResult(portalContext, tree, log);

        } catch (IOException | JSONException | SuspendNotAllowedException e) {
            Messagebox.show(e.getMessage(), "Attention", Messagebox.OK, Messagebox.ERROR);
        } catch (Exception e) {
            Messagebox.show(e.getMessage(), "Attention", Messagebox.OK, Messagebox.ERROR);
        }
    }

}
