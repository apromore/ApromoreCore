/*
 * This file is part of "Apromore".
 *
 * Copyright (C) 2019 - 2020 The University of Melbourne.
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

package org.apromore.portal.dialogController;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.math.BigDecimal;
import org.apromore.canoniser.Canoniser;
import org.apromore.dao.model.Folder;
import org.apromore.helper.Version;
import org.apromore.model.EditSessionType;
import org.apromore.model.ProcessSummaryType;
import org.apromore.model.VersionSummaryType;
import org.apromore.portal.common.Constants;
import org.apromore.portal.exception.ExceptionFormats;
import org.apromore.service.ProcessService;
import org.apromore.service.WorkspaceService;
import org.zkoss.spring.SpringUtil;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.SuspendNotAllowedException;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zk.ui.event.EventQueue;
import org.zkoss.zk.ui.event.EventQueues;
import org.zkoss.zul.Button;
import org.zkoss.zul.Grid;
import org.zkoss.zul.Messagebox;
import org.zkoss.zul.Row;
import org.zkoss.zul.Rows;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Window;

/**
 * This class is used to show a common diaglog for saving process models
 * It is used by editor plugins as they are opened by users to edit a new/existing model 
 * It receives input passed by the calling plugin via the EditSessionType object
 * It communicates back to the calling plugin by updating the EditSessionType object.
 * This dialog uses manager services to create or update a process model in the system
 * 
 * The model being saved has three possible states (see BPMNEditorController)
 * 1. The model is being saved to an existing EMPTY model which has been pre-created (when users select to create a new model): same name, the version remains 1.0 
 * 2. The model is opened from an existing model and now being saved to a new version (Save): same name, increasing version number
 * 3. The model is being saved to a new model (Save As): different name, starts from 1.0 version number
 * 
 * Note that state (1) above is special because the system first creates an empty model in the database, then opens
 * the empty model in the editor (not an intuitive UI design). In this case, a question is that if the model is saved with the same name 
 * and version number, will a conflict happen and not allowable as in the state (2)? Answer: the empty model can be 
 * OVERRIDEN in this case because as being an empty model it has empty canonical format (no edges, no nodes) and 
 * no root fragment, leading to no new versions of the same process (model) and branch created, and thus
 * ProcessServiceImpl.updateExistingProcess() can pass without failure. 
 * 
 * In the case of state (2), if the same version number is saved, the current process version already has a root fragment 
 * (non-empty model) leading to multiple versions of the same version number created which will make 
 * ProcessServiceImpl.updateExistingProcess() fail.
 * 
 * @author: Apromore
 * @modifier: Bruce Nguyen
 *
 */
public class SaveAsDialogController extends BaseController {

    private EventQueue<Event> qe = EventQueues.lookup(Constants.EVENT_QUEUE_REFRESH_SCREEN, EventQueues.SESSION, true);

    private static final BigDecimal VERSION_INCREMENT = new BigDecimal("0.1");

    private Window saveAsW;
    private Textbox modelName;
    private Textbox versionNumber;
    private Textbox branchName;
//    private ProcessSummaryType process; 
//    private VersionSummaryType version; 
    private PluginPropertiesHelper pluginPropertiesHelper;
    private EditSessionType editSession;
    private Boolean isSaveCurrent; //null: save first time for new model, true: save existing model, false: save as
    private String modelData;
    private String originalVersionNumber;
    ProcessService processService;
    WorkspaceService workspaceService;

    // Note: process and version parameters are not used and redundant
    // They may not be used because all parameters are passed in EditSessionType for consistency
    // They are left in the method signature for compatibility purpose only
    // isUpdate = null is used to indicate the special state (1) above.
    public SaveAsDialogController(ProcessSummaryType process, VersionSummaryType version, EditSessionType editSession,
            Boolean isUpdate, String data, Window window) throws SuspendNotAllowedException, InterruptedException, ExceptionFormats {
//        this.process = process;
//        this.version = version;
        this.editSession = editSession;
        this.isSaveCurrent = isUpdate;
        this.saveAsW = window;
        this.modelData = data;
        this.originalVersionNumber = this.editSession.getCurrentVersionNumber();

        Rows rows = (Rows) this.saveAsW.getFirstChild().getFirstChild().getFirstChild().getNextSibling();
        Row modelNameR = (Row) rows.getChildren().get(0);
        Row versionNumberR = (Row) rows.getChildren().get(1);
        Row branchNameR = (Row) rows.getChildren().get(2);
        Row buttonGroupR = (Row) rows.getChildren().get(3);
        this.modelName = (Textbox) modelNameR.getFirstChild().getNextSibling();
        this.versionNumber = (Textbox) versionNumberR.getFirstChild().getNextSibling();
        this.branchName = (Textbox) branchNameR.getFirstChild().getNextSibling();

        pluginPropertiesHelper = new PluginPropertiesHelper(getService(), (Grid) this.saveAsW.getFellow("saveAsGrid"));
        Button saveB = (Button) buttonGroupR.getFirstChild().getFirstChild();
        Button cancelB = (Button) saveB.getNextSibling();
        this.modelName.setText(this.editSession.getProcessName());
        
        processService = (ProcessService) SpringUtil.getBean("processService");
        workspaceService = (WorkspaceService) SpringUtil.getBean("workspaceService");

        saveB.addEventListener("onClick",
                new EventListener<Event>() {
                    public void onEvent(Event event) throws Exception {
                        saveModel();
                    }
                });
        this.saveAsW.addEventListener("onOK",
                new EventListener<Event>() {
                    public void onEvent(Event event) throws Exception {
                        saveModel();
                    }
                });
        cancelB.addEventListener("onClick",
                new EventListener<Event>() {
                    public void onEvent(Event event) throws Exception {
                        cancel();
                    }
                });

        if (isUpdate == null) {
        	this.saveAsW.setTitle("Save model");
            this.branchName.setText("MAIN");
            this.branchName.setReadonly(true);
            this.versionNumber.setText("1.0");
            this.versionNumber.setReadonly(true);
            this.modelName.setText(this.modelName.getText());
            this.modelName.setReadonly(true);
        }
        else if (isUpdate) {
        	this.saveAsW.setTitle("Save model");
            String branchName = null;
            BigDecimal versionNumber;
            BigDecimal currentVersion = new BigDecimal(editSession.getCurrentVersionNumber());
//            BigDecimal maxVersion = new BigDecimal(editSession.getMaxVersionNumber());
            versionNumber = createNewVersionNumber(currentVersion);
            branchName = this.editSession.getOriginalBranchName();
            
//            BigDecimal maxVersion = new BigDecimal(editSession.getMaxVersionNumber());
//            if (maxVersion.compareTo(currentVersion) > 0) {
//                branchName = createNewBranchName(this.editSession.getOriginalBranchName());
//            } else {
//                branchName = this.editSession.getOriginalBranchName();
//            }
            
            this.modelName.setReadonly(true);
            this.branchName.setText(branchName);
            this.branchName.setReadonly(true);
//            This version.isEmpty() is unused here to indicate this is a new process
//            as the flag isUpdate has been used above.
//            if (version.isEmpty()) {
//                this.versionNumber.setText("1.0");
//            } else {
//                this.versionNumber.setText(String.format("%1.1f", versionNumber));
//            }
            this.versionNumber.setText(String.format("%1.1f", versionNumber));
        } else {
        	this.saveAsW.setTitle("Save model as");
            this.branchName.setText("MAIN");
            this.branchName.setReadonly(true);
            this.versionNumber.setText("1.0");
            //this.versionNumber.setReadonly(true);
            this.modelName.setText(this.modelName.getText() + "_new"); //18.08: add to make it a new name
        }

        this.saveAsW.doModal();
    }
    
    public SaveAsDialogController(ProcessSummaryType process, VersionSummaryType version, EditSessionType editSession,
            Boolean isUpdate, String data) throws SuspendNotAllowedException, InterruptedException, ExceptionFormats {
    	this(process, version, editSession, isUpdate, data, 
    			(Window) Executions.createComponents("saveAsDialog.zul", null, null));
    }
    
    protected void cancel() throws Exception {
        closePopup();
    }

    private void closePopup() {
        this.saveAsW.detach();
    }

    protected void saveModel() throws Exception {
//        String userName = UserSessionManager.getCurrentUser().getUsername();
    	String userName = this.editSession.getUsername();
        String nativeType = this.editSession.getNativeType();
//        String versionName = this.version.getName();
        String versionName = this.editSession.getOriginalVersionNumber();
        //String domain = this.process.getDomain();
        String domain = this.editSession.getDomain();
        String processName = this.modelName.getText();
//        Integer processId = this.process.getId();
        Integer processId = this.editSession.getProcessId();
//        String created = this.version.getCreationDate();
        String created = this.editSession.getCreationDate();
        String branch = this.branchName.getText();
        boolean makePublic = processService.isPublicProcess(processId);
        String versionNo = versionNumber.getText();
//        if (branch == null || branch.equals("")) {
//            branch = "MAIN";
//        }
        int containingFolderId = this.editSession.getFolderId();
        InputStream is = new ByteArrayInputStream(this.modelData.getBytes());

        try {
            if (validateFields()) {
                Folder folder = workspaceService.getFolder(editSession.getFolderId());
                String containingFolderName = (folder == null) ? "Home" : folder.getName();
                if (this.isSaveCurrent != null && !this.isSaveCurrent) { //Save As
                	// the branch name is by default "MAIN", see org.apromore.common.Constants.TRUNK_NAME
                    getService().importProcess(userName, containingFolderId, nativeType, processName, versionNo, is, domain, null, created, null,
                            makePublic, pluginPropertiesHelper.readPluginProperties(Canoniser.CANONISE_PARAMETER));
                    Messagebox.show("The model '" + processName + "' has been created in the '" + containingFolderName + "' folder", "Apromore", Messagebox.OK, Messagebox.NONE);
                } else {
                	//Note: the versionName parameter is never used in updateProcess(), so any value should be fine. 
                	//Update the 2nd time with same name and version number for the state (1) is allowed 
                	//because the pre-created empty model has no root fragments. 
                    getService().updateProcess(editSession.hashCode(), userName, nativeType, processId, domain, processName,
                            editSession.getOriginalBranchName(), branch, versionNo, originalVersionNumber, versionName, is);
                    Messagebox.show("The model '" + processName + "' has been updated in the '" + containingFolderName + "' folder", "Apromore", Messagebox.OK, Messagebox.NONE);
                    
                    // 18.08: update current version number to ensure it will be always auto-increment
                    // It seems that original version number and current version number are set 
                    // to the same version number all the times (see MainController.createEditSession())
                    editSession.setOriginalVersionNumber(versionNo);
                    editSession.setCurrentVersionNumber(versionNo);
                    originalVersionNumber = versionNo;
                }
                
                qe.publish(new Event(Constants.EVENT_MESSAGE_SAVE, null, Boolean.TRUE));
                closePopup();
            }
        } catch (Exception e) {
            //Messagebox.show("Unable to Save Model : Error: \n" + e.getMessage());
        	Messagebox.show("Unable to save model! Check if a model with the same name and version number has already existed.", "Apromore", Messagebox.OK, Messagebox.ERROR);
        }
    }


    private boolean validateFields() {
        boolean valid = true;
        String message = "";
        String title = "Missing Fields";

        Version newVersion = new Version(versionNumber.getText());
        Version curVersion = new Version(editSession.getCurrentVersionNumber());
        try {
        	if (this.isSaveCurrent == null) {
        		//
        	}
        	else if (this.isSaveCurrent) {
                if (newVersion.compareTo(curVersion) <= 0) {
                    valid = false;
                    message = message + "New Version number has to be greater than " + this.editSession.getCurrentVersionNumber();
                    title = "Wrong Version Number";
                }
                if (this.branchName.getText().equals("") || this.branchName.getText() == null) {
                    valid = false;
                    message = message + "Branch Name cannot be empty";
                    title = "Branch Name Empty";
                }
            } else {
                if (this.modelName.getText().equals("") || this.modelName.getText() == null) {
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

//    private String createNewBranchName(String currBranchName) {
//        String branchName;
//        if (currBranchName.equalsIgnoreCase("Main") || !currBranchName.matches("B[0-9]+")) {
//            branchName = "B1";
//        } else {
//            Integer branchVersionNumber = 0;
//            Matcher matcher = Pattern.compile("\\d+").matcher(currBranchName);
//            if (matcher.find()) {
//                branchVersionNumber = Integer.valueOf(matcher.group());
//            }
//            branchName = "B" + branchVersionNumber + 1;
//        }
//        return branchName;
//    }

    private BigDecimal createNewVersionNumber(BigDecimal currentVersion) {
        return (currentVersion).add(VERSION_INCREMENT);
    }

}
