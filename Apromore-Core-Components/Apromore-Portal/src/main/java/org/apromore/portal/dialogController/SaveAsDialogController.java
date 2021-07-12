/*-
 * #%L
 * This file is part of "Apromore Core".
 * 
 * Copyright (C) 2013 - 2017 Queensland University of Technology.
 * %%
 * Copyright (C) 2018 - 2021 Apromore Pty Ltd.
 * %%
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Lesser Public License for more details.
 * 
 * You should have received a copy of the GNU General Lesser Public
 * License along with this program.  If not, see
 * <http://www.gnu.org/licenses/lgpl-3.0.html>.
 * #L%
 */

package org.apromore.portal.dialogController;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.text.MessageFormat;

import org.apromore.dao.model.Folder;
import org.apromore.dao.model.ProcessModelVersion;
import org.apromore.portal.common.Constants;
import org.apromore.portal.dialogController.dto.ApromoreSession;
import org.apromore.portal.helper.Version;
import org.apromore.portal.model.EditSessionType;
import org.apromore.portal.model.ImportProcessResultType;
import org.apromore.portal.model.ProcessSummaryType;
import org.apromore.portal.model.VersionSummaryType;
import org.apromore.service.ProcessService;
import org.apromore.service.WorkspaceService;
import org.zkoss.spring.SpringUtil;
import org.zkoss.util.resource.Labels;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zk.ui.event.EventQueue;
import org.zkoss.zk.ui.event.EventQueues;
import org.zkoss.zul.Button;
import org.zkoss.zul.Messagebox;
import org.zkoss.zul.Messagebox.ClickEvent;
import org.zkoss.zul.Row;
import org.zkoss.zul.Rows;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Window;

/**
 * ApromoreSession and ApromoreSession.EditSessionType contain data of the process model being opened in the editor 
 * Remember to update them after actions on the process model to keep things in consistent states.
 * The XML model native data can be retrieved right from the editor
 * @todo there is a duplication between ApromoreSession and EditSessionType, they need to be clean later.
 * 
 * @author: Apromore
 * @modifier: Bruce Nguyen
 *
 */
public class SaveAsDialogController extends BaseController {

    private EventQueue<Event> qePortal = EventQueues.lookup(Constants.EVENT_QUEUE_REFRESH_SCREEN, EventQueues.SESSION, true);
    private EventQueue<Event> qeBPMNEditor = EventQueues.lookup(Constants.EVENT_QUEUE_BPMN_EDITOR, EventQueues.SESSION, true);

    private Window saveAsW;
    private Textbox modelName;
    private Textbox versionNumber;
    
    private EditSessionType editSession;
    private ApromoreSession session;
    private Boolean isSaveCurrent; //null: save first time for new model, true: save existing model, false: save as
    private String modelData;
    
    ProcessService processService;
    WorkspaceService workspaceService;

    public SaveAsDialogController(ProcessSummaryType process, VersionSummaryType version, ApromoreSession session,
            Boolean isUpdate, String data, Window window) {
        this.session = session;
        this.editSession = session.getEditSession();
        this.isSaveCurrent = isUpdate;
        this.saveAsW = window;
        this.modelData = data;
        
        processService = (ProcessService) SpringUtil.getBean("processService");
        workspaceService = (WorkspaceService) SpringUtil.getBean("workspaceService");

        Rows rows = (Rows) this.saveAsW.getFirstChild().getFirstChild().getFirstChild().getNextSibling();
        Row modelNameR = (Row) rows.getChildren().get(0);
        Row versionNumberR = (Row) rows.getChildren().get(1);
        Row buttonGroupR = (Row) rows.getChildren().get(2);
        this.modelName = (Textbox) modelNameR.getFirstChild().getNextSibling();
        this.modelName.setText(this.isSaveCurrent ? this.editSession.getProcessName() : 
                                this.editSession.getProcessName() + "_new");
        this.modelName.setReadonly(this.isSaveCurrent );
        this.versionNumber = (Textbox) versionNumberR.getFirstChild().getNextSibling();
        this.versionNumber.setText(this.editSession.getCurrentVersionNumber());
        Button saveB = (Button) buttonGroupR.getFirstChild().getFirstChild();
        Button cancelB = (Button) saveB.getNextSibling();
        if (isUpdate) {
            saveAsW.setTitle("Save BPMN model");
        } else {
            saveAsW.setTitle("Save BPMN model as");
        }
        saveAsW.addEventListener("onOK",
                new EventListener<Event>() {
                    @Override
                    public void onEvent(Event event) throws Exception {
                        saveModel();
                    }
                });
        saveB.addEventListener("onClick",
                new EventListener<Event>() {
                    @Override
                    public void onEvent(Event event) throws Exception {
                        saveModel();
                    }
                });
        cancelB.addEventListener("onClick",
                new EventListener<Event>() {
                    @Override
                    public void onEvent(Event event) throws Exception {
                        cancel();
                    }
                });

        this.saveAsW.doModal();
    }
    
    public SaveAsDialogController(ProcessSummaryType process, VersionSummaryType version, ApromoreSession session,
            Boolean isUpdate, String data) {
    	this(process, version, session, isUpdate, data, 
    	        (Window) Executions.createComponents("saveAsDialog.zul", null, null));
    }
    
    protected void cancel() throws Exception {
        closePopup();
    }

    private void closePopup() {
        this.saveAsW.detach();
    }

    protected void saveModel() throws Exception {
    	String userName = this.editSession.getUsername();
        String nativeType = this.editSession.getNativeType();
        String processName = this.modelName.getText();
        String versionNo = versionNumber.getText();
        Integer processId = this.editSession.getProcessId();
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH-mm-ss");
        String created = dateFormat.format(new Date());

        boolean makePublic = (this.isSaveCurrent ? processService.isPublicProcess(processId) : false);
        int containingFolderId = this.editSession.getFolderId();
        InputStream is = new ByteArrayInputStream(this.modelData.getBytes());

        if (validateFields()) {
            Folder folder = workspaceService.getFolder(editSession.getFolderId());
            String containingFolderName = (folder == null) ? "Home" : folder.getName();
            if (!this.isSaveCurrent) { //Save As new model
                saveAsNewModel(userName, containingFolderId, processName, versionNo, nativeType, is, "", 
                            "", created, null, makePublic, containingFolderName);
            } else {
                if (session.containVersion(versionNo)) {
                    Messagebox.show(MessageFormat.format(Labels.getLabel("portal_versionExisted_message"), versionNo),
                                "Question", new Messagebox.Button[] { Messagebox.Button.YES, Messagebox.Button.NO}, 
                                Messagebox.QUESTION,
                                new org.zkoss.zk.ui.event.EventListener<ClickEvent>() {
                                    @Override
                                    public void onEvent(ClickEvent e) throws Exception {
                                        switch (e.getButton()) {
                                            case YES: 
                                                saveCurrentModelVersion(processId, processName, versionNo, nativeType, is, userName, containingFolderName);
                                                break;
                                            case NO: //Cancel is clicked
                                                break;
                                            default: //if the Close button is clicked, e.getButton() returns null
                                        }
                                    }
                                }
                            );
                }
                else {
                    createNewModelVersion(processId, processName, versionNo, nativeType, is, userName, containingFolderName);
                }
                
            }
            
        }
        
    }
    
    private void saveAsNewModel(String userName, Integer folderId, String processName, String versionNumber, String nativeType,
            InputStream nativeStream, String domain, String documentation, String created, String lastUpdate, boolean publicModel, 
            String containingFolderName) {
        try {
            ImportProcessResultType importResult = getService().importProcess(userName, folderId, nativeType, processName, versionNumber, nativeStream, domain, 
                    documentation, created, null, publicModel);
            
            // Update process data with the new process to keep a consistent state
            editSession.setProcessId(importResult.getProcessSummary().getId());
            editSession.setProcessName(processName);
            editSession.setOriginalVersionNumber(versionNumber);
            editSession.setCurrentVersionNumber(versionNumber);
            session.setProcess(importResult.getProcessSummary());
            session.setVersion(importResult.getProcessSummary().getVersionSummaries().get(0));
            
            qePortal.publish(new Event(Constants.EVENT_MESSAGE_SAVE, null, Boolean.TRUE));
            qeBPMNEditor.publish(new Event(BPMNEditorController.EVENT_MESSAGE_SAVE, null, new String[] {processName, versionNumber}));
            closePopup();
        } catch (Exception e) {
            Messagebox.show(Labels.getLabel("portal_unableSave_message"), null, Messagebox.OK, Messagebox.ERROR);
        }
    }
    
    private void saveCurrentModelVersion(Integer processId, String processName, String versionNumber, String nativeType, InputStream nativeStream,
            String userName, String containingFolderName) {
        try {
            ProcessModelVersion newVersion = getService().updateProcessModelVersion(processId, editSession.getOriginalBranchName(), 
                    versionNumber, userName, "" , nativeType, nativeStream);
            
            // Update process data with the new process to keep a consistent state
            editSession.setOriginalVersionNumber(versionNumber);
            editSession.setCurrentVersionNumber(versionNumber);
            editSession.setLastUpdate(newVersion.getLastUpdateDate());
            session.getVersion().setLastUpdate(newVersion.getLastUpdateDate());
            session.getVersion().setVersionNumber(versionNumber);
            
            qeBPMNEditor.publish(new Event(BPMNEditorController.EVENT_MESSAGE_SAVE, null, new String[] {processName, versionNumber}));
            closePopup();
        }
        catch (Exception e) {
            Messagebox.show(e.getMessage());
        }
        
    }
    
    private void createNewModelVersion(Integer processId, String processName, String versionNumber, String nativeType, InputStream nativeStream,
            String userName, String containingFolderName) {
        try {
            ProcessModelVersion newVersion = getService().createProcessModelVersion(editSession.hashCode(), userName, nativeType, processId, editSession.getOriginalBranchName(), 
                    versionNumber, editSession.getOriginalVersionNumber(), "", nativeStream);
            
            // Update process data with the new process to keep a consistent state
            editSession.setOriginalVersionNumber(versionNumber);
            editSession.setCurrentVersionNumber(versionNumber);
            editSession.setLastUpdate(newVersion.getLastUpdateDate());
            session.getVersion().setLastUpdate(newVersion.getLastUpdateDate());
            session.getVersion().setVersionNumber(versionNumber);
            
            qePortal.publish(new Event(Constants.EVENT_MESSAGE_SAVE, null, Boolean.TRUE));
            qeBPMNEditor.publish(new Event(BPMNEditorController.EVENT_MESSAGE_SAVE, null, new String[] {processName, versionNumber}));
            closePopup();
        }
        catch (Exception e) {
            Messagebox.show(e.getMessage());
        }
        
    }

    private boolean validateFields() {
        boolean valid = true;
        String message = "";
        String title = "Missing Fields";

        Version newVersion = new Version(versionNumber.getText());
        Version curVersion = new Version(editSession.getCurrentVersionNumber());
        try {
            if (!this.isSaveCurrent) {
                if (this.modelName.getText() == null || this.modelName.getText().trim().equals("")) {
                    valid = false;
                    message = message + "Model Name cannot be empty";
                    title = "Model Name Empty";
                }
                if (this.modelName.getText().equals(this.editSession.getProcessName())) {
                    valid = false;
                    message = message + "Model Name has to be different from " + this.editSession.getProcessName();
                    title = "Same Model Name";
                }
            }
           
            if (this.versionNumber.getText().equals("") || this.versionNumber.getText() == null) {
                valid = false;
                message = message + "Version Number cannot be empty";
                title = "Version Number Empty";
            }
            
            if (!message.equals("")) {
                Messagebox.show(message, title, Messagebox.OK, Messagebox.INFORMATION);
            }
        } catch (Exception e) {
            valid = false;
        }
        return valid;
    }
}
