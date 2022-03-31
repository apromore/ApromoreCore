/*-
 * #%L
 * This file is part of "Apromore Core".
 * 
 * Copyright (C) 2013 - 2017 Queensland University of Technology.
 * %%
 * Copyright (C) 2018 - 2022 Apromore Pty Ltd.
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
import java.nio.charset.StandardCharsets;
import java.text.DateFormat;
import java.text.MessageFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Objects;
import javax.annotation.Nullable;

import org.apromore.dao.model.Folder;
import org.apromore.dao.model.ProcessModelVersion;
import org.apromore.plugin.portal.PortalLoggerFactory;
import org.apromore.portal.common.Constants;
import org.apromore.portal.dialogController.dto.ApromoreSession;
import org.apromore.portal.model.EditSessionType;
import org.apromore.portal.model.ImportProcessResultType;
import org.apromore.portal.model.ProcessSummaryType;
import org.apromore.portal.model.VersionSummaryType;
import org.apromore.service.ProcessService;
import org.apromore.service.WorkspaceService;
import org.slf4j.Logger;
import org.zkoss.util.resource.Labels;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zk.ui.event.EventQueue;
import org.zkoss.zk.ui.event.EventQueues;
import org.zkoss.zul.Button;
import org.zkoss.zul.Messagebox;
import org.zkoss.zul.Row;
import org.zkoss.zul.Rows;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Window;

import static org.apromore.common.Constants.DATE_FORMAT;

/**
 * ApromoreSession and ApromoreSession.EditSessionType contain data of the process model being
 * opened in the editor Remember to update them after actions on the process model to keep things in
 * consistent states. The XML model native data can be retrieved right from the editor
 * 
 * @todo there is a duplication between ApromoreSession and EditSessionType, they need to be clean
 *       later.
 * 
 * @author: Apromore
 * @modifier: Bruce Nguyen
 *
 */
public class SaveAsDialogController extends BaseController {

  static final Logger LOGGER = PortalLoggerFactory.getLogger(SaveAsDialogController.class);

  private static final int MAX_NAME_LENGTH = 100;

  private EventQueue<Event> qePortal =
      EventQueues.lookup(Constants.EVENT_QUEUE_REFRESH_SCREEN, EventQueues.SESSION, true);
  private EventQueue<Event> qeBPMNEditor =
      EventQueues.lookup(Constants.EVENT_QUEUE_BPMN_EDITOR, EventQueues.DESKTOP, true);

  private Window saveAsW;
  private Textbox modelName;
  private Textbox versionNumber;

  private EditSessionType editSession;
  private ApromoreSession session;
  /**
   * isSaveCurrent
   * null: save first time for new model
   * true: save existing model
   * false: save as
   */
  private final Boolean isSaveCurrent;
  private String modelData;

  ProcessService processService;
  WorkspaceService workspaceService;
  MainController mainController;

  public SaveAsDialogController(ProcessSummaryType process, VersionSummaryType version,
      ApromoreSession session, @Nullable Boolean isUpdate, String data, MainController mainController) {
    this.session = session;
    this.editSession = session.getEditSession();
    this.isSaveCurrent = isUpdate;
    this.saveAsW = (Window) Executions.createComponents("~./macros/saveAsDialog.zul", null, null);
    this.modelData = data;
    this.mainController = mainController;
    processService = mainController.getProcessService();
    workspaceService = mainController.getWorkspaceService();

    Rows rows =
        (Rows) this.saveAsW.getFirstChild().getFirstChild().getFirstChild().getNextSibling();
    Row modelNameR = (Row) rows.getChildren().get(0);
    Row versionNumberR = (Row) rows.getChildren().get(1);
    Row buttonGroupR = (Row) rows.getChildren().get(2);
    this.modelName = (Textbox) modelNameR.getFirstChild().getNextSibling();
    String processName = this.editSession.getProcessName();
    String newProcessNameSuffix = "_new";
    this.modelName.setText(!Boolean.FALSE.equals(this.isSaveCurrent)
        || processName.length() > MAX_NAME_LENGTH - newProcessNameSuffix.length()
        ? processName
        : processName + newProcessNameSuffix);
    this.modelName.setReadonly(Boolean.TRUE.equals(this.isSaveCurrent) && !UNTITLED_PROCESS_NAME.equals(processName));
    this.versionNumber = (Textbox) versionNumberR.getFirstChild().getNextSibling();
    this.versionNumber.setText(this.editSession.getCurrentVersionNumber());
    Button saveB = (Button) buttonGroupR.getFirstChild().getFirstChild();
    Button cancelB = (Button) saveB.getNextSibling();
    if (Boolean.TRUE.equals(isUpdate)) {
      saveAsW.setTitle(Labels.getLabel("bpmnEditor_saveBPMN_text", "Save BPMN model"));
    } else {
      saveAsW.setTitle(Labels.getLabel("bpmnEditor_saveBPMNAs_text", "Save BPMN model as"));
    }
    saveAsW.addEventListener("onOK", new EventListener<Event>() {
      @Override
      public void onEvent(Event event) throws Exception {
        saveModel();
      }
    });
    saveB.addEventListener("onClick", new EventListener<Event>() {
      @Override
      public void onEvent(Event event) throws Exception {
        saveModel();
      }
    });
    cancelB.addEventListener("onClick", new EventListener<Event>() {
      @Override
      public void onEvent(Event event) throws Exception {
        cancel();
      }
    });

    this.saveAsW.doModal();
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
    DateFormat dateFormat = new SimpleDateFormat(DATE_FORMAT);
    String created = dateFormat.format(new Date());

    boolean makePublic = (Boolean.TRUE.equals(this.isSaveCurrent) && processService.isPublicProcess(processId));
    int containingFolderId = this.editSession.getFolderId();
    InputStream is = new ByteArrayInputStream(this.modelData.getBytes());

    if (validateFields()) {
      Folder folder = workspaceService.getFolder(editSession.getFolderId());
      String containingFolderName = (folder == null) ? "Home" : folder.getName();
      if (Boolean.FALSE.equals(this.isSaveCurrent)) { // Save As new model
        saveAsNewModel(userName, containingFolderId, processName, versionNo, nativeType, is, "", "",
            created, makePublic, containingFolderName);
      } else {
        if (session.containVersion(versionNo)) {
          Messagebox.show(
            MessageFormat.format(Labels.getLabel("portal_versionExisted_message"), versionNo),
            Labels.getLabel("brand_name"),
            new Messagebox.Button[] {Messagebox.Button.YES, Messagebox.Button.NO},
            Messagebox.QUESTION,
            e -> {
              switch (e.getButton()) {
                case YES:
                  saveCurrentModelVersion(processId, processName, versionNo, nativeType, is,
                      userName, containingFolderName);
                  break;
                case NO: // Cancel is clicked
                  break;
                default: // if the Close button is clicked, e.getButton() returns null
              }
            }
          );
        } else { // save first time for new model
          createNewModelVersion(processId, processName, versionNo, nativeType, is, userName,
              containingFolderName);
        }

      }

    }

  }

  private void saveAsNewModel(String userName, Integer folderId, String processName,
      String versionNumber, String nativeType, InputStream nativeStream, String domain,
      String documentation, String created, boolean publicModel,
      String containingFolderName) {
    try {
      ImportProcessResultType importResult = mainController.getManagerService().importProcess(
          userName, folderId, nativeType, processName, versionNumber, nativeStream, domain,
          documentation, created, null, publicModel);

      LOGGER.info("User {} save new model \"{}\" version {} in folder {}", userName, processName, versionNumber,
              containingFolderName);

      Integer processId = importResult.getProcessSummary().getId();

      // Create draft to associated with new model
      mainController.getManagerService().createDraft(processId, processName, versionNumber,
              nativeType, nativeStream, userName);
      // Update process data with the new process to keep a consistent state
      editSession.setProcessId(processId);
      editSession.setProcessName(processName);
      editSession.setOriginalVersionNumber(versionNumber);
      editSession.setCurrentVersionNumber(versionNumber);
      session.setProcess(importResult.getProcessSummary());
      session.setVersion(importResult.getProcessSummary().getVersionSummaries().get(0));

      qePortal.publish(new Event(Constants.EVENT_MESSAGE_SAVE, null, Boolean.TRUE));
      qeBPMNEditor.publish(new Event(BPMNEditorController.EVENT_MESSAGE_SAVE, null,
          new String[] {processName, versionNumber}));
      closePopup();
    } catch (Exception e) {
      Messagebox.show(Labels.getLabel("portal_unableSave_message"), null, Messagebox.OK,
          Messagebox.ERROR);
    }
  }

  private void saveCurrentModelVersion(Integer processId, String processName, String versionNumber,
      String nativeType, InputStream nativeStream, String userName, String containingFolderName) {
    try {
      String bpmnXml = new String(nativeStream.readAllBytes(), StandardCharsets.UTF_8);

      ProcessModelVersion newVersion = mainController.getManagerService().updateProcessModelVersion(
          processId, editSession.getOriginalBranchName(), versionNumber, userName, "", nativeType,
              new ByteArrayInputStream(bpmnXml.getBytes()));
      mainController.getManagerService().updateDraft(processId,
              editSession.getOriginalVersionNumber(), nativeType, new ByteArrayInputStream(bpmnXml.getBytes()),
              userName);
      // Update process name if it's a new process
      if (UNTITLED_PROCESS_NAME.equals(this.editSession.getProcessName())) {
        mainController.getManagerService().editProcessData(processId, processName, "", userName, versionNumber,
                versionNumber, null, false);
        editSession.setProcessName(processName);
        qePortal.publish(new Event(Constants.EVENT_MESSAGE_SAVE, null, Boolean.TRUE));
      }

      LOGGER.info("User {} save current model \"{}\" version {} in folder {}", userName, processName, versionNumber,
              containingFolderName);

      // Update process data with the new process to keep a consistent state
      editSession.setOriginalVersionNumber(versionNumber);
      editSession.setCurrentVersionNumber(versionNumber);
      editSession.setLastUpdate(newVersion.getLastUpdateDate());
      session.getVersion().setLastUpdate(newVersion.getLastUpdateDate());
      session.getVersion().setVersionNumber(versionNumber);

      qeBPMNEditor.publish(new Event(BPMNEditorController.EVENT_MESSAGE_SAVE, null,
          new String[] {processName, versionNumber}));
      closePopup();
    } catch (Exception e) {
      Messagebox.show(e.getMessage());
    }

  }

  private void createNewModelVersion(Integer processId, String processName, String versionNumber,
      String nativeType, InputStream nativeStream, String userName, String containingFolderName) {
    try {
      ProcessModelVersion newVersion =
          mainController.getManagerService().createProcessModelVersion(editSession.hashCode(),
              userName, nativeType, processId, editSession.getOriginalBranchName(), versionNumber,
              editSession.getOriginalVersionNumber(), "", nativeStream);
      LOGGER.info("User {} save new model \"{}\" version {} in folder {}", userName, processName, versionNumber,
              containingFolderName);

      // Create draft version for new PMV
      nativeStream.reset();
      mainController.getManagerService().createDraft(processId, processName,
              versionNumber, nativeType, nativeStream, userName);

      // Update process data with the new process to keep a consistent state
      editSession.setOriginalVersionNumber(versionNumber);
      editSession.setCurrentVersionNumber(versionNumber);
      editSession.setLastUpdate(newVersion.getLastUpdateDate());
      session.getVersion().setLastUpdate(newVersion.getLastUpdateDate());
      session.getVersion().setVersionNumber(versionNumber);

      qePortal.publish(new Event(Constants.EVENT_MESSAGE_SAVE, null, Boolean.TRUE));
      qeBPMNEditor.publish(new Event(BPMNEditorController.EVENT_MESSAGE_SAVE, null,
          new String[] {processName, versionNumber}));
      closePopup();
    } catch (Exception e) {
      Messagebox.show(e.getMessage());
    }

  }

  private boolean validateFields() {
    boolean valid = true;
    String message = "";
    String title = Labels.getLabel("missing_fields", "Missing Fields");

    try {
      if (Boolean.FALSE.equals(this.isSaveCurrent)) {
        if (this.modelName.getText() == null || "".equals(this.modelName.getText().trim())) {
          valid = false;
          message += Labels.getLabel("common_noEmptyModelName_message", "Model name cannot be empty");
          title = Labels.getLabel("common_noEmptyModelName_title", "Model Name Empty");
        }
        if (Objects.equals(this.modelName.getText(), this.editSession.getProcessName())) {
          valid = false;
          message += MessageFormat.format(
              Labels.getLabel("bpmnEditor_sameModelName_message", "Model Name has to be different from {0}"),
              this.editSession.getProcessName());
          title = Labels.getLabel("bpmnEditor_sameModelName_title", "Same Model Name");
        }
      }

      if ("".equals(this.versionNumber.getText()) || this.versionNumber.getText() == null) {
        valid = false;
        message += Labels.getLabel("common_noEmptyVersionNumber_message", "Version number cannot be empty");
        title = Labels.getLabel("common_noEmptyVersionNumber_title", "Version Number Empty");
      }

      if (!"".equals(message)) {
        Messagebox.show(message, title, Messagebox.OK, Messagebox.INFORMATION);
      }
    } catch (Exception e) {
      valid = false;
    }
    return valid;
  }
}
