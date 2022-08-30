/*-
 * #%L
 * This file is part of "Apromore Enterprise Edition".
 * %%
 * Copyright (C) 2019 - 2022 Apromore Pty Ltd. All Rights Reserved.
 * %%
 * NOTICE:  All information contained herein is, and remains the
 * property of Apromore Pty Ltd and its suppliers, if any.
 * The intellectual and technical concepts contained herein are
 * proprietary to Apromore Pty Ltd and its suppliers and may
 * be covered by U.S. and Foreign Patents, patents in process,
 * and are protected by trade secret or copyright law.
 * Dissemination of this information or reproduction of this
 * material is strictly forbidden unless prior written permission
 * is obtained from Apromore Pty Ltd.
 * #L%
 */

package org.apromore.portal.controller.process;

import com.google.common.base.Strings;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.text.MessageFormat;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import javax.annotation.Nullable;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.apromore.commons.datetime.DateTimeUtils;
import org.apromore.dao.model.ProcessModelVersion;
import org.apromore.portal.common.Constants;
import org.apromore.portal.common.FolderItem;
import org.apromore.portal.common.FolderTreeNode;
import org.apromore.portal.dialogController.BPMNEditorController;
import org.apromore.portal.dialogController.MainController;
import org.apromore.portal.dialogController.dto.ApromoreSession;
import org.apromore.portal.helper.Version;
import org.apromore.portal.model.EditSessionType;
import org.apromore.portal.model.FolderType;
import org.apromore.portal.model.ImportProcessResultType;
import org.apromore.portal.model.ProcessSummaryType;
import org.apromore.portal.model.VersionSummaryType;
import org.apromore.service.ProcessService;
import org.apromore.service.WorkspaceService;
import org.apromore.zk.notification.Notification;
import org.zkoss.bind.annotation.AfterCompose;
import org.zkoss.bind.annotation.BindingParam;
import org.zkoss.bind.annotation.Command;
import org.zkoss.bind.annotation.ContextParam;
import org.zkoss.bind.annotation.ContextType;
import org.zkoss.bind.annotation.ExecutionArgParam;
import org.zkoss.bind.annotation.GlobalCommand;
import org.zkoss.bind.annotation.Init;
import org.zkoss.bind.annotation.NotifyChange;
import org.zkoss.util.resource.Labels;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventQueue;
import org.zkoss.zk.ui.event.EventQueues;
import org.zkoss.zk.ui.util.Clients;
import org.zkoss.zul.Messagebox;
import org.zkoss.zul.Window;

@Slf4j
@Getter
@Setter
public class ProcessSaveViewModel {

    private static final String UNTITLED_PROCESS_NAME = "Untitled";
    private static final int MAX_NAME_LENGTH = 100;
    private static final String NEW_NAME_SUFFIX = "_new";
    private static final String UNABLE_TO_SAVE_MESSAGE = "portal_unableSave_message";

    private EventQueue<Event> qePortal =
        EventQueues.lookup(Constants.EVENT_QUEUE_REFRESH_SCREEN, EventQueues.SESSION, true);
    private EventQueue<Event> qeBPMNEditor =
        EventQueues.lookup(Constants.EVENT_QUEUE_BPMN_EDITOR, EventQueues.DESKTOP, true);

    MainController mainController;

    private EditSessionType editSession; // EditSession for BPMN editor
    private ApromoreSession session; // ApromoreSession for BPMN editor
    /**
     * isSaveCurrent
     * null: save first time for new model, the file is already created in the current folder
     * true: save existing model
     * false: save as
     */
    private Boolean isSaveCurrent;
    private String modelData;
    private String modelExtendedData;
    private ProcessSummaryType process;

    ProcessService processService;
    WorkspaceService workspaceService;

    String userName;
    String processName;
    String nativeType;

    String versionNumber;
    Boolean versionNumberEnabled;

    String windowTitle;
    Boolean selectFolderEnabled;
    Boolean processNameEditable;
    Integer targetFolderId;
    String targetFolderPath;

    String extensionLabel;
    Boolean extensionEnabled;
    Boolean extensionChecked = false;
    String footnoteLabel;
    Boolean footnoteEnabled;

    Boolean isEditor; // Is this used by the BPMN editor or plugin (e.g. PD)
    Boolean isPublic = false;

    Window window;

    private void updateTargetFolder(Integer folderId) {
        targetFolderId = folderId;
        FolderItem folderItem = mainController.getFolderPath(targetFolderId);
        targetFolderPath = folderItem.getPath();
    }

    @AfterCompose
    public void afterCompose(@ContextParam(ContextType.VIEW) Component view) {
        this.window = (Window) view;
    }

    @Init
    public void init(
        @ExecutionArgParam("mainController") @Nullable MainController mainController,
        @ExecutionArgParam("session") @Nullable ApromoreSession session,
        @ExecutionArgParam("userName") String userName,
        @ExecutionArgParam("process") @Nullable ProcessSummaryType process,
        @ExecutionArgParam("processName") String processName,
        @ExecutionArgParam("versionNumber") String versionNumber,
        @ExecutionArgParam("targetFolderId") Integer targetFolderId,
        @ExecutionArgParam("isSaveCurrent") @Nullable Boolean isSaveCurrent,
        @ExecutionArgParam("modelData") String modelData,
        @ExecutionArgParam("modelExtendedData") @Nullable String modelExtendedData,
        @ExecutionArgParam("extensionLabel") @Nullable String extensionLabel,
        @ExecutionArgParam("footnoteLabel") @Nullable String footnoteLabel
    ) {
        this.mainController = mainController;
        try {
            processService = mainController.getProcessService();
            workspaceService = mainController.getWorkspaceService();
        } catch (NullPointerException e) {
            log.error("Failed to initialise Save Dialog");
            return;
        }

        this.session = session;
        if (session != null) {
            this.editSession = session.getEditSession();
            isEditor = Boolean.TRUE;
        } else {
            isEditor = Boolean.FALSE;
        }
        this.userName = userName;

        this.process = process;

        this.processName = processName;
        this.versionNumber = versionNumber;
        updateTargetFolder(targetFolderId);

        this.isSaveCurrent = isSaveCurrent;
        this.modelData = modelData;
        this.modelExtendedData = modelExtendedData;
        this.extensionLabel = extensionLabel;
        this.footnoteLabel = footnoteLabel;

        extensionEnabled = !Strings.isNullOrEmpty(extensionLabel);
        footnoteEnabled = !Strings.isNullOrEmpty(footnoteLabel);

        if (Boolean.TRUE.equals(isEditor)) {
            if (!Boolean.TRUE.equals(this.isSaveCurrent)) { // save as or first time
                this.processName = processName + NEW_NAME_SUFFIX;
            }
            nativeType = this.editSession.getNativeType();
            isPublic = (
                Boolean.TRUE.equals(this.isSaveCurrent) &&
                    processService.isPublicProcess(this.editSession.getProcessId())
            );
            versionNumberEnabled = true;
        } else {
            versionNumberEnabled = false;
        }

        if (Boolean.TRUE.equals(isSaveCurrent)) { // Save current
            windowTitle = Labels.getLabel("bpmnEditor_saveBPMN_text", "Save BPMN model");
            processNameEditable = false;
            selectFolderEnabled = false;
        } else {
            windowTitle = Labels.getLabel("bpmnEditor_saveBPMNAs_text", "Save BPMN model as");
            if (Boolean.FALSE.equals(isSaveCurrent)) { // Save as
                processNameEditable = true;
                selectFolderEnabled = true;
            } else { // isSaveCurrent = null, Save first time
                processNameEditable = true;
                selectFolderEnabled = false;
            }
        }
    }

    private void updateClientProcessName(String processName) {
        if (Boolean.TRUE.equals(isEditor)) {
            Clients.evalJavaScript("Apromore.BPMNEditor.updateProcessName(\"" + processName + "\")");
        }
    }

    @Command
    public void browseCmd() {
        Map<String, Object> args = new HashMap<>();
        args.put("mainController", mainController);
        args.put("selectedFolderId", targetFolderId);

        Window folderTreeWindow = (Window) Executions
            .createComponents("~./macros/folder/folderTreeWindow.zul", null, args);
        folderTreeWindow.doModal();
    }

    @GlobalCommand
    @NotifyChange({"targetFolderId", "targetFolderPath"})
    public void onFolderSelect(@BindingParam("selectedItem") FolderTreeNode selectedItem) {
        FolderType folderType = (FolderType) selectedItem.getData();
        updateTargetFolder(folderType.getId());
    }

    @Command
    public void cancelCmd() {
        window.detach();
    }

    @Command
    public void saveCmd() {
        if (Boolean.TRUE.equals(isEditor)) {
            save();
        } else {
            export();
        }
    }

    private Boolean validateFields() {
        Boolean valid = Boolean.TRUE;
        String message = "";
        String title = Labels.getLabel("missing_fields", "Missing Fields");

        try {
            if (Boolean.FALSE.equals(this.isSaveCurrent)) {
                if (Strings.isNullOrEmpty(this.processName)) {
                    valid = Boolean.FALSE;
                    message += Labels.getLabel("common_noEmptyModelName_message", "Model name cannot be empty");
                    title = Labels.getLabel("common_noEmptyModelName_title", "Model Name Empty");
                }
                if (Objects.equals(this.processName, this.editSession.getProcessName())) {
                    valid = Boolean.FALSE;
                    message += MessageFormat.format(
                        Labels.getLabel("bpmnEditor_sameModelName_message", "Model Name has to be different from {0}"),
                        this.editSession.getProcessName());
                    title = Labels.getLabel("bpmnEditor_sameModelName_title", "Same Model Name");
                }
            }
            if (Strings.isNullOrEmpty(this.versionNumber)) {
                valid = Boolean.FALSE;
                message += Labels.getLabel("common_noEmptyVersionNumber_message", "Version number cannot be empty");
                title = Labels.getLabel("common_noEmptyVersionNumber_title", "Version Number Empty");
            }
            if (!"".equals(message)) {
                Messagebox.show(message, title, Messagebox.OK, Messagebox.INFORMATION);
            }
        } catch (Exception e) {
            valid = Boolean.FALSE;
        }
        return valid;
    }

    private void updateSessionInfo(String originalNumber, String currentVersion, String lastUpdateDate) {
        if (editSession != null) {
            editSession.setOriginalVersionNumber(originalNumber);
            editSession.setCurrentVersionNumber(currentVersion);
            editSession.setLastUpdate(lastUpdateDate);
        }
    }

    public void save() {
        String now = DateTimeUtils.now();
        InputStream modelStream = new ByteArrayInputStream(this.modelData.getBytes());
        Integer processId = this.editSession.getProcessId();

        if (validateFields()) {
            if (Boolean.FALSE.equals(this.isSaveCurrent)) { // Save As existing
                saveAsModel(
                    userName,
                    targetFolderId,
                    processName,
                    versionNumber,
                    nativeType,
                    modelStream,
                    "",
                    "",
                    now,
                    isPublic,
                    targetFolderPath
                );
                editSession.setFolderId(targetFolderId);
                updateClientProcessName(processName);
            } else {
                if (session.containVersion(versionNumber)) {
                    Messagebox.show(
                        MessageFormat.format(Labels.getLabel("portal_versionExisted_message"), versionNumber),
                        Labels.getLabel("brand_name"),
                        new Messagebox.Button[] {Messagebox.Button.YES, Messagebox.Button.NO},
                        Messagebox.QUESTION,
                        e -> {
                            switch (e.getButton()) {
                                case YES:
                                    saveCurrentModel(processId, processName, versionNumber, nativeType, modelStream,
                                        userName, targetFolderPath);
                                    updateClientProcessName(processName);
                                    break;
                                case NO: // Cancel is clicked
                                    break;
                                default: // if the Close button is clicked, e.getButton() returns null
                            }
                        }
                    );
                } else { // save first time for new model
                    createNewModel(processId, processName, versionNumber, nativeType, modelStream, userName,
                        targetFolderPath);
                    updateClientProcessName(processName);
                }
            }
        }
    }

    public void syncSessionInfo(Integer processId, ProcessModelVersion newVersion) throws Exception {
        // Update process name if it's a new process
        if (UNTITLED_PROCESS_NAME.equals(this.editSession.getProcessName())) {
            mainController.getManagerService().editProcessData(processId, processName, "", userName, versionNumber,
                versionNumber, null, false);
            editSession.setProcessName(processName);
            process.setName(processName);
        }

        // Update process data with the new process to keep a consistent state
        updateSessionInfo(versionNumber, versionNumber, newVersion.getLastUpdateDate());
        session.getVersion().setLastUpdate(newVersion.getLastUpdateDate());
        session.getVersion().setVersionNumber(versionNumber);
        epilog();
    }

    public void epilog() {
        qePortal.publish(new Event(Constants.EVENT_MESSAGE_SAVE, null, Boolean.TRUE));
        qeBPMNEditor.publish(new Event(BPMNEditorController.EVENT_MESSAGE_SAVE, null,
            new String[] {processName, versionNumber}));
        window.detach();
    }

    private void saveAsModel(String userName, Integer folderId, String processName,
                             String versionNumber, String nativeType, InputStream nativeStream, String domain,
                             String documentation, String created, boolean publicModel,
                             String containingFolderName) {
        try {
            ImportProcessResultType importResult = mainController.getManagerService().importProcess(
                userName, folderId, nativeType, processName, versionNumber, nativeStream, domain,
                documentation, created, null, publicModel);

            log.info("User {} save new model \"{}\" version {} in folder {}", userName, processName, versionNumber,
                containingFolderName);

            Integer processId = importResult.getProcessSummary().getId();

            // Reuse stream requires reset to prevent empty draft
            nativeStream.reset();

            // Create draft to associated with new model
            mainController.getManagerService().createDraft(processId, processName, versionNumber,
                nativeType, nativeStream, userName);
            // Update process data with the new process to keep a consistent state
            ProcessSummaryType processSummaryType = importResult.getProcessSummary();
            VersionSummaryType versionSummaryType = processSummaryType.getVersionSummaries().get(0);

            editSession.setProcessId(processId);
            editSession.setProcessName(processName);
            updateSessionInfo(versionNumber, versionNumber, versionSummaryType.getLastUpdate());
            session.setProcess(processSummaryType);
            session.setVersion(versionSummaryType);
            epilog();
        } catch (Exception e) {
            Messagebox.show(Labels.getLabel(UNABLE_TO_SAVE_MESSAGE), null, Messagebox.OK,
                Messagebox.ERROR);
        }
        window.detach();
    }

    private void saveCurrentModel(Integer processId, String processName, String versionNumber,
                                  String nativeType, InputStream nativeStream, String userName,
                                  String containingFolderName) {
        try {
            String bpmnXml = new String(nativeStream.readAllBytes(), StandardCharsets.UTF_8);
            ProcessModelVersion newVersion = mainController.getManagerService().updateProcessModelVersion(
                processId, editSession.getOriginalBranchName(), versionNumber, userName, "", nativeType,
                new ByteArrayInputStream(bpmnXml.getBytes()));
            log.info("User {} save current model \"{}\" version {} in folder {}", userName, processName, versionNumber,
                containingFolderName);
            nativeStream.reset();
            mainController.getManagerService().updateDraft(processId,
                editSession.getOriginalVersionNumber(), nativeType, new ByteArrayInputStream(bpmnXml.getBytes()),
                userName);
            syncSessionInfo(processId, newVersion);
        } catch (Exception e) {
            Messagebox.show(Labels.getLabel(UNABLE_TO_SAVE_MESSAGE), null, Messagebox.OK,
                Messagebox.ERROR);
        }
        window.detach();
    }

    private void createNewModel(Integer processId, String processName, String versionNumber,
                                String nativeType, InputStream nativeStream, String userName,
                                String containingFolderName) {
        try {
            ProcessModelVersion newVersion =
                mainController.getManagerService().createProcessModelVersion(editSession.hashCode(),
                    userName, nativeType, processId, editSession.getOriginalBranchName(), versionNumber,
                    editSession.getOriginalVersionNumber(), "", nativeStream);
            log.info("User {} save new model \"{}\" version {} in folder {}", userName, processName, versionNumber,
                containingFolderName);
            // Create draft version for new PMV
            nativeStream.reset();
            mainController.getManagerService().createDraft(processId, processName,
                versionNumber, nativeType, nativeStream, userName);
            syncSessionInfo(processId, newVersion);
        } catch (Exception e) {
            Messagebox.show(Labels.getLabel(UNABLE_TO_SAVE_MESSAGE), null, Messagebox.OK,
                Messagebox.ERROR);
        }
        window.detach();
    }

    protected void export() {
        String data = (Boolean.TRUE.equals(extensionChecked)) ? modelExtendedData : modelData;
        String now = DateTimeUtils.now();

        try {
            processService.importProcess(
                userName,
                targetFolderId,
                processName,
                new Version(versionNumber),
                "BPMN 2.0",
                new ByteArrayInputStream(data.getBytes()),
                "",
                "Model generated by the Apromore BPMN process mining service.",
                now, // created at
                now, // last updated at
                false
            );
            String message = MessageFormat.format(
                Labels.getLabel("bpmnEditor_SaveBPMN_message"),
                "<strong>" + processName + "</strong>",
                "<strong>" + targetFolderPath + "</strong>"
            );
            Notification.info(message);
            epilog();
        } catch (Exception ex) {
            Messagebox.show(Labels.getLabel(UNABLE_TO_SAVE_MESSAGE), null, Messagebox.OK,
                Messagebox.ERROR);
        }
        window.detach();
    }
}
