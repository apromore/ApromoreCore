/*-
 * #%L
 * This file is part of "Apromore Core".
 *
 * Copyright (C) 2012 - 2017 Queensland University of Technology.
 * Copyright (C) 2012 Felix Mannhardt.
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
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import org.apache.commons.io.IOUtils;
import org.apromore.exception.CircularReferenceException;
import org.apromore.exception.UserNotFoundException;
import org.apromore.plugin.portal.PortalLoggerFactory;
import org.apromore.portal.common.UserSessionManager;
import org.apromore.portal.common.Utils;
import org.apromore.portal.dialogController.dto.SubProcessItem;
import org.apromore.portal.exception.ExceptionAllUsers;
import org.apromore.portal.exception.ExceptionDomains;
import org.apromore.portal.model.FolderType;
import org.apromore.portal.model.ImportProcessResultType;
import org.apromore.portal.model.ProcessSummaryType;
import org.apromore.processmining.models.graphbased.directed.bpmn.BPMNDiagram;
import org.apromore.processmining.models.graphbased.directed.bpmn.BPMNDiagramFactory;
import org.apromore.processmining.plugins.bpmn.plugins.BpmnLayoutPlugin;
import org.apromore.util.StringUtil;
import org.slf4j.Logger;
import org.zkoss.util.resource.Labels;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.SuspendNotAllowedException;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zul.Button;
import org.zkoss.zul.Div;
import org.zkoss.zul.Messagebox;
import org.zkoss.zul.Row;
import org.zkoss.zul.Rows;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Window;

public class ImportOneProcessController extends BaseController {

  private static final Logger LOGGER =
      PortalLoggerFactory.getLogger(ImportOneProcessController.class);

  private final MainController mainC;
  private final ImportController importProcessesC;
  private final Window importOneProcessWindow;
  private final String fileName;
  private final Textbox processNameTb;
  private final InputStream nativeProcess; // the input stream read from uploaded file
  private final String nativeType;
  private final Button okButton;
  private final Button cancelButton;

  private final String username;
  private boolean isPublic;

  public ImportOneProcessController(final MainController mainC,
      final ImportController importProcessesC, final InputStream xml_is, final String processName,
      final String nativeType, final String fileName, final boolean isPublic)
      throws SuspendNotAllowedException, InterruptedException, ExceptionDomains, ExceptionAllUsers,
      IOException {
    this.importProcessesC = importProcessesC;
    this.mainC = mainC;
    this.username = UserSessionManager.getCurrentUser().getUsername();
    this.fileName = fileName;
    this.nativeProcess = new ByteArrayInputStream(IOUtils.toByteArray(xml_is));
    this.nativeType = nativeType;
    this.isPublic = isPublic;
    this.importOneProcessWindow =
        (Window) Executions.createComponents("~./macros/importOneProcess.zul", null, null);
    Rows rows = (Rows) this.importOneProcessWindow.getFirstChild().getFirstChild().getFirstChild()
        .getNextSibling();
    Row processNameR = (Row) rows.getChildren().get(0);
    this.processNameTb = (Textbox) processNameR.getChildren().get(1);
    this.processNameTb.setValue(StringUtil.normalizeFilename(processName));

    Div buttonsD = (Div) importOneProcessWindow.getFellow("div");
    this.okButton = (Button) buttonsD.getFirstChild();
    this.cancelButton = (Button) buttonsD.getChildren().get(1);

    this.okButton.addEventListener("onClick", new EventListener<Event>() {
      @Override
      public void onEvent(final Event event) throws Exception {
        importProcess("", username);
      }
    });
    this.importOneProcessWindow.addEventListener("onOK", new EventListener<Event>() {
      @Override
      public void onEvent(final Event event) throws Exception {
        if (processNameTb.getValue().trim().compareTo("") == 0) {
          Messagebox.show(Labels.getLabel("portal_enterValueEachField_message"), "Apromore",
              Messagebox.OK, Messagebox.EXCLAMATION);
        } else {
          importProcess("", username);
        }
      }
    });
    this.cancelButton.addEventListener("onClick", new EventListener<Event>() {
      @Override
      public void onEvent(final Event event) throws Exception {
        cancel();
      }
    });
    this.importOneProcessWindow.doModal();
  }

  private void cancel() throws InterruptedException, IOException {
    this.importProcessesC.deleteFromToBeImported(this);
    closePopup();
  }

  private void closePopup() {
    this.importOneProcessWindow.detach();
  }

  public void importProcess(final String domain, final String owner)
      throws InterruptedException, IOException {

    String bpmnText = new String(getNativeProcess().readAllBytes(), StandardCharsets.UTF_8);

    try (
      InputStream inputStream = new ByteArrayInputStream(bpmnText.getBytes(StandardCharsets.UTF_8));
      ) {
      Integer folderId = 0;
      FolderType currentFolder = this.mainC.getPortalSession().getCurrentFolder();
      if (currentFolder != null) {
        folderId = currentFolder.getId();
      }

      ImportProcessResultType importResult = mainC.getManagerService().importProcess(owner,
          folderId, this.nativeType, this.processNameTb.getValue(), "1.0", inputStream,
          domain, "", Utils.getDateTime(), Utils.getDateTime(), isPublic);

      //Import subprocesses as linked subprocesses
      BPMNDiagram bpmnDiagram = BPMNDiagramFactory.newDiagramFromProcessText(bpmnText);
      importLinkedSubProcesses(importResult.getProcessSummary(), bpmnDiagram, domain, owner,
          this.processNameTb.getValue(), folderId);

      this.mainC.showPluginMessages(importResult.getMessage());
      this.importProcessesC.getImportedList().add(this);
      this.mainC.displayNewProcess(importResult.getProcessSummary());
      this.importProcessesC.deleteFromToBeImported(this);
    } catch (Exception e) {
      LOGGER.error("Import failed!", e);
      Messagebox.show(Labels.getLabel("portal_failedImport_message"), "Apromore", Messagebox.OK,
          Messagebox.ERROR);
    } finally {
      closePopup();
    }
  }

  private void importLinkedSubProcesses(final ProcessSummaryType importedModel, final BPMNDiagram bpmnDiagram,
                                        final String domain, final String owner, final String name, final int folderId)
      throws Exception {

    SubProcessItem topSubProcessItem = SubProcessItem.buildSubProcessTree(bpmnDiagram, name);
    topSubProcessItem.setProcessSummaryType(importedModel);

    importSubProcesses(topSubProcessItem, domain, owner, folderId);

    linkSubProcesses(topSubProcessItem);
  }

  private void importSubProcesses(SubProcessItem item, String domain, String owner,
                                  int folderId) throws Exception {
    for (SubProcessItem subItem : item.getChildren()) {
      BPMNDiagram subProcessDiagram = subItem.getDiagram();
      if (!subProcessDiagram.getNodes().isEmpty()) {
        subItem.setProcessSummaryType(importOneSubProcess(subProcessDiagram, domain, owner,
            subItem.getName(), folderId));
        importSubProcesses(subItem, domain, owner, folderId);
      }
    }
  }

  private ProcessSummaryType importOneSubProcess(final BPMNDiagram bpmnDiagram, final String domain, final String owner,
                                                 final String name, final int folderId)
      throws Exception {
    String emptyModelXML = "<?xml version='1.0' encoding='UTF-8'?>"
        + "<bpmn:definitions xmlns:xsi='http://www.w3.org/2001/XMLSchema-instance' "
        + "xmlns:bpmn='http://www.omg.org/spec/BPMN/20100524/MODEL' "
        + "xmlns:bpmndi='http://www.omg.org/spec/BPMN/20100524/DI' "
        + "xmlns:dc='http://www.omg.org/spec/DD/20100524/DC' "
        + "targetNamespace='http://bpmn.io/schema/bpmn' " + "id='Definitions_1'>"
        + "<bpmn:process id='Process_1' isExecutable='false'/>"
        + "<bpmndi:BPMNDiagram id='BPMNDiagram_1'>"
        + "<bpmndi:BPMNPlane id='BPMNPlane_1' bpmnElement='Process_1'/>"
        + "</bpmndi:BPMNDiagram>"
        + "</bpmn:definitions>";

    String bpmnText = bpmnDiagram.getNodes().isEmpty() ? emptyModelXML : BpmnLayoutPlugin.addLayout(bpmnDiagram, "");
    ImportProcessResultType importResult = mainC.getManagerService().importProcess(owner,
        folderId, this.nativeType, name, "1.0",
        new ByteArrayInputStream(bpmnText.getBytes(StandardCharsets.UTF_8)),
        domain, "", Utils.getDateTime(), Utils.getDateTime(), isPublic);

    //Display subprocess
    this.importProcessesC.getImportedList().add(this);
    this.mainC.displayNewProcess(importResult.getProcessSummary());
    this.importProcessesC.deleteFromToBeImported(this);
    return importResult.getProcessSummary();
  }

  private void linkSubProcesses(final SubProcessItem item) throws UserNotFoundException, CircularReferenceException {
    for (SubProcessItem subItem : item.getChildren()) {
      if (subItem.getProcessSummaryType() != null) {
        mainC.getProcessService().linkSubprocess(item.getProcessSummaryType().getId(),
            subItem.getSubProcessNode().getId().toString(),
            subItem.getProcessSummaryType().getId(), username);
        linkSubProcesses(subItem);
      }
    }
  }

  public Window getImportOneProcessWindow() {
    return importOneProcessWindow;
  }

  public String getFileName() {
    return fileName;
  }

  public String getNativeType() {
    return nativeType;
  }

  public InputStream getNativeProcess() throws IOException {
    return nativeProcess;
  }


}
