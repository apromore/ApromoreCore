/*-
 * #%L
 * This file is part of "Apromore Core".
 * 
 * Copyright (C) 2011 - 2017 Queensland University of Technology.
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

package org.apromore.plugin.portal.file.impl;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.apromore.portal.common.UserSessionManager;
import org.apromore.portal.dialogController.BaseController;
import org.apromore.portal.dialogController.MainController;
import org.apromore.portal.dialogController.SelectDynamicListController;
import org.apromore.portal.exception.ExceptionAllUsers;
import org.apromore.portal.exception.ExceptionDomains;
import org.apromore.portal.model.LogSummaryType;
import org.apromore.portal.model.ProcessSummaryType;
import org.apromore.portal.model.VersionSummaryType;
import org.apromore.service.EventLogService;
import org.apromore.service.ProcessService;
import org.apromore.zk.label.LabelSupplier;
import org.apromore.zk.notification.Notification;
import org.zkoss.zkplus.spring.SpringUtil;
import org.zkoss.util.resource.Labels;
import org.zkoss.zk.ui.SuspendNotAllowedException;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zul.Button;
import org.zkoss.zul.Checkbox;
import org.zkoss.zul.Div;
import org.zkoss.zul.Messagebox;
import org.zkoss.zul.Radio;
import org.zkoss.zul.Radiogroup;
import org.zkoss.zul.Row;
import org.zkoss.zul.Rows;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Window;

public class EditMetadataController extends BaseController implements LabelSupplier {

  private Window window;

  private MainController mainController;
  private EditListMetadataController editDataListProcessesC;
  private Radio r0;
  private Radio r1;
  private Radio r2;
  private Radio r3;
  private Radio r4;
  private Radio r5;
  private Radio r6; // uncheck all
  private LogSummaryType log;
  private ProcessSummaryType process;
  private VersionSummaryType preVersion;
  private Textbox nameT;
  private Textbox versionNumberT;
  private Checkbox makePublicCb;
  private Radiogroup rankingRG;
  private SelectDynamicListController ownerCB;
  private SelectDynamicListController domainCB;

  public EditMetadataController(MainController mainC,
      EditListMetadataController editListLogDataController, LogSummaryType log)
      throws SuspendNotAllowedException, InterruptedException, ExceptionAllUsers, ExceptionDomains {
    this.mainController = mainC;
    this.editDataListProcessesC = editListLogDataController;
    this.log = log;

    this.window = createComponent("zul/editlogdata.zul");
    // this.window.setTitle("Edit log metadata");
    if(!this.mainController.getEventLogService().hasWritePermissionOnLog(mainController.getSecurityService().getUserByName(UserSessionManager.getCurrentUser().getUsername()), Arrays.asList(this.log.getId())))
    {
  	  Notification.error(Labels.getLabel("portal_noPrivilegeRename_message"));
  	  if(this.window!=null) {
  		  this.window.detach();
  	  }
	  return;
    }

    this.nameT = (Textbox) window.getFellow("nameTextbox");
    this.makePublicCb = (Checkbox) window.getFellow("makePublicCheckbox");
    resetLog();

    Button okB = (Button) window.getFellow("okButton");
    okB.addEventListener("onClick", new EventListener<Event>() {
      public void onEvent(Event event) throws Exception {
        editDataLog();
      }
    });
    this.window.addEventListener("onOK", new EventListener<Event>() {
      public void onEvent(Event event) throws Exception {
        editDataLog();
      }
    });
    Button cancelB = (Button) window.getFellow("cancelButton");
    cancelB.addEventListener("onClick", new EventListener<Event>() {
      public void onEvent(Event event) throws Exception {
        cancel();
      }
    });
    Button cancelAllB = (Button) window.getFellow("cancelAllButton");
    cancelAllB.addEventListener("onClick", new EventListener<Event>() {
      public void onEvent(Event event) throws Exception {
        cancelAll();
      }
    });
    cancelAllB.setVisible(this.editDataListProcessesC.getToEditList().size() > 0);
    Button resetB = (Button) window.getFellow("resetButton");
    resetB.addEventListener("onClick", new EventListener<Event>() {
      public void onEvent(Event event) throws Exception {
        resetLog();
      }
    });
    this.window.doModal();
  }

  public EditMetadataController(MainController mainC,
      EditListMetadataController editListProcessDataController, ProcessSummaryType process,
      VersionSummaryType version)
      throws SuspendNotAllowedException, InterruptedException, ExceptionAllUsers, ExceptionDomains {
    this.mainController = mainC;
    this.editDataListProcessesC = editListProcessDataController;
    this.process = process;
    this.preVersion = version;

    this.window = createComponent("zul/editprocessdata.zul");
    // this.window.setTitle("Edit process model metadata");
    
    if(!this.mainController.getProcessService().hasWritePermissionOnProcess(mainController.getSecurityService().getUserByName(UserSessionManager.getCurrentUser().getUsername()), Arrays.asList(this.process.getId())))
    {
  	  Notification.error(Labels.getLabel("portal_noPrivilegeRename_message"));
  	  if(this.window!=null)
  	  this.window.detach();
	  return;
    }

    Rows rows = (Rows) this.window.getFirstChild().getFirstChild().getFirstChild().getNextSibling();
    Row processNameR = (Row) rows.getFirstChild();
    this.nameT = (Textbox) processNameR.getFirstChild().getNextSibling();
    Row versionNumberR = (Row) processNameR.getNextSibling();
    this.versionNumberT = (Textbox) versionNumberR.getFirstChild().getNextSibling();

    Row domainR = (Row) versionNumberR.getNextSibling();
    Row ownerR = (Row) domainR.getNextSibling();
    Row nativeTypesR = (Row) ownerR.getNextSibling();
    Row rankingR = (Row) nativeTypesR.getNextSibling();
    this.rankingRG = (Radiogroup) rankingR.getFirstChild().getNextSibling();
    this.r0 = (Radio) this.rankingRG.getFirstChild();
    this.r1 = (Radio) this.r0.getNextSibling();
    this.r2 = (Radio) this.r1.getNextSibling();
    this.r3 = (Radio) this.r2.getNextSibling();
    this.r4 = (Radio) this.r3.getNextSibling();
    this.r5 = (Radio) this.r4.getNextSibling();
    this.r6 = (Radio) this.r5.getNextSibling();

    Row publicR = (Row) rankingR.getNextSibling();
    this.makePublicCb = (Checkbox) publicR.getFirstChild().getNextSibling();

    Row buttonsR = (Row) publicR.getNextSibling().getNextSibling();
    Div buttonsD = (Div) buttonsR.getFirstChild();
    Button okB = (Button) buttonsD.getFirstChild();
    Button cancelB = (Button) okB.getNextSibling();
    Button cancelAllB = (Button) cancelB.getNextSibling();
    Button resetB = (Button) cancelAllB.getNextSibling();

    // List<String> domains = mainC.getDomains();
    List<String> domains = new ArrayList<>();
    this.domainCB = new SelectDynamicListController(domains);
    this.domainCB.setReference(domains);
    this.domainCB.setAutodrop(true);
    this.domainCB.setWidth("85%");
    this.domainCB.setHeight("100%");
    this.domainCB.setAttribute("hflex", "1");
    domainR.appendChild(domainCB);
    List<String> usernames = mainC.getUsers();
    this.ownerCB = new SelectDynamicListController(usernames);
    this.ownerCB.setReference(usernames);
    this.ownerCB.setValue(UserSessionManager.getCurrentUser().getUsername());
    this.ownerCB.setAutodrop(true);
    this.ownerCB.setWidth("85%");
    this.ownerCB.setHeight("100%");
    this.ownerCB.setAttribute("hflex", "1");
    ownerR.appendChild(ownerCB);

    // publicR.setVisible(false);
    cancelAllB.setVisible(this.editDataListProcessesC.getToEditList().size() > 0);
    this.r6.setChecked(true);
    resetProcess();

    okB.addEventListener("onClick", new EventListener<Event>() {
      public void onEvent(Event event) throws Exception {
        editDataProcess();
      }
    });
    this.window.addEventListener("onOK", new EventListener<Event>() {
      public void onEvent(Event event) throws Exception {
        editDataProcess();
      }
    });
    cancelB.addEventListener("onClick", new EventListener<Event>() {
      public void onEvent(Event event) throws Exception {
        cancel();
      }
    });
    cancelAllB.addEventListener("onClick", new EventListener<Event>() {
      public void onEvent(Event event) throws Exception {
        cancelAll();
      }
    });
    resetB.addEventListener("onClick", new EventListener<Event>() {
      public void onEvent(Event event) throws Exception {
        resetProcess();
      }
    });
    this.window.doModal();
  }

  protected void editDataLog() throws Exception {
    Integer logId = this.log.getId();
    String logName = this.nameT.getValue();
    boolean isPublic = this.makePublicCb.isChecked();
    if (this.nameT.getValue().compareTo("") == 0) {
      Messagebox.show(getLabel("enterMandatory"), "Apromore", Messagebox.OK,
          Messagebox.ERROR);
    } else {
      mainController.getManagerService().editLogData(logId, logName, "", isPublic);
      this.editDataListProcessesC.getEditedList().add(this);
      this.editDataListProcessesC.deleteFromToBeEdited(this);
      closePopup();
    }
  }

  protected void editDataProcess() throws Exception {
    Integer processId = this.process.getId();
    String processName = this.nameT.getValue();
    String domain = this.domainCB.getValue();
    String username = this.ownerCB.getValue();
    String preVersion = this.preVersion.getVersionNumber();
    String newVersion = this.versionNumberT.getValue();
    boolean isPublic = this.makePublicCb.isChecked();
    String ranking = null;
    if (this.rankingRG.getSelectedItem() != null
        && "uncheck all".compareTo(this.rankingRG.getSelectedItem().getLabel()) != 0) {
      ranking = this.rankingRG.getSelectedItem().getLabel();
    }
    if (this.nameT.getValue().compareTo("") == 0
        || this.versionNumberT.getValue().compareTo("") == 0) {
      Messagebox.show(getLabel("enterMandatory"), "Apromore", Messagebox.OK,
          Messagebox.ERROR);
    } else {
      mainController.getManagerService().editProcessData(processId, processName, domain, username,
          preVersion, newVersion, ranking, isPublic);
      this.editDataListProcessesC.getEditedList().add(this);
      this.editDataListProcessesC.deleteFromToBeEdited(this);
      closePopup();
    }
  }

  protected void cancel() throws Exception {
    this.editDataListProcessesC.deleteFromToBeEdited(this);
    closePopup();
  }

  private void closePopup() {
    mainController.clearProcessVersions();
    this.window.detach();
  }

  protected void cancelAll() throws Exception {
    this.editDataListProcessesC.cancelAll();
  }

  protected void resetProcess() {
    this.nameT.setValue(this.process.getName().length() > 100 ? this.process.getName().substring(0, 99) : this.process.getName());
    this.nameT.setSelectionRange(0, this.nameT.getValue().length());
    this.versionNumberT.setValue(this.preVersion.getVersionNumber());
    this.domainCB.setValue(this.process.getDomain());
    this.ownerCB.setValue(UserSessionManager.getCurrentUser().getUsername());

    ProcessService processService = (ProcessService) SpringUtil.getBean("processService");
    this.makePublicCb.setChecked(processService.isPublicProcess(this.process.getId()));

    if (this.preVersion.getRanking() != null) {
      r0.setChecked("0".compareTo(this.preVersion.getRanking()) == 0);
      r1.setChecked("1".compareTo(this.preVersion.getRanking()) == 0);
      r2.setChecked("2".compareTo(this.preVersion.getRanking()) == 0);
      r3.setChecked("3".compareTo(this.preVersion.getRanking()) == 0);
      r4.setChecked("4".compareTo(this.preVersion.getRanking()) == 0);
      r5.setChecked("5".compareTo(this.preVersion.getRanking()) == 0);
    } else {
      r0.setChecked(false);
      r1.setChecked(false);
      r2.setChecked(false);
      r3.setChecked(false);
      r4.setChecked(false);
      r5.setChecked(false);
    }
  }

  private void resetLog() {
    nameT.setValue(log.getName().length() > 100 ? log.getName().substring(0, 99) : log.getName());
    nameT.setSelectionRange(0, nameT.getValue().length());
    EventLogService eventLogService = (EventLogService) SpringUtil.getBean("eventLogService");
    makePublicCb.setChecked(eventLogService.isPublicLog(log.getId()));
  }

  public Window getWindow() {
    return window;
  }

}
