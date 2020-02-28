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

import java.util.List;

import org.apromore.model.ProcessSummaryType;
import org.apromore.model.VersionSummaryType;
import org.apromore.portal.common.UserSessionManager;
import org.apromore.portal.exception.ExceptionAllUsers;
import org.apromore.portal.exception.ExceptionDomains;
import org.apromore.service.ProcessService;
import org.zkoss.spring.SpringUtil;
import org.zkoss.zk.ui.Executions;
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

public class EditOneProcessDataController extends BaseController {

    private Window editDataWindow;

    private MainController mainController;
    private EditListProcessDataController editDataListProcessesC;
    private Radio r0;
    private Radio r1;
    private Radio r2;
    private Radio r3;
    private Radio r4;
    private Radio r5;
    private Radio r6; // uncheck all
    private ProcessSummaryType process;
    private VersionSummaryType preVersion;
    private Textbox processNameT;
    private Textbox versionNumberT;
    private Checkbox makePublicCb;
    private Radiogroup rankingRG;
    private SelectDynamicListController ownerCB;
    private SelectDynamicListController domainCB;

    public EditOneProcessDataController(MainController mainC, EditListProcessDataController editListProcessDataController,
            ProcessSummaryType process, VersionSummaryType version)
            throws SuspendNotAllowedException, InterruptedException, ExceptionAllUsers, ExceptionDomains {
        this.mainController = mainC;
        this.editDataListProcessesC = editListProcessDataController;
        this.process = process;
        this.preVersion = version;

        this.editDataWindow = (Window) Executions.createComponents("macros/editprocessdata.zul", null, null);
        this.editDataWindow.setTitle("Edit process model metadata");

        Rows rows = (Rows) this.editDataWindow.getFirstChild().getFirstChild().getFirstChild().getNextSibling();
        Row processNameR = (Row) rows.getFirstChild();
        this.processNameT = (Textbox) processNameR.getFirstChild().getNextSibling();
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

        List<String> domains = mainC.getDomains();
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

        //publicR.setVisible(false);
        cancelAllB.setVisible(this.editDataListProcessesC.getToEditList().size() > 0);
        this.r6.setChecked(true);
        reset();

        okB.addEventListener("onClick",
                new EventListener<Event>() {
                    public void onEvent(Event event) throws Exception {
                        editDataProcess();
                    }
                });
        this.editDataWindow.addEventListener("onOK",
                new EventListener<Event>() {
                    public void onEvent(Event event) throws Exception {
                        editDataProcess();
                    }
                });
        cancelB.addEventListener("onClick",
                new EventListener<Event>() {
                    public void onEvent(Event event) throws Exception {
                        cancel();
                    }
                });
        cancelAllB.addEventListener("onClick",
                new EventListener<Event>() {
                    public void onEvent(Event event) throws Exception {
                        cancelAll();
                    }
                });
        resetB.addEventListener("onClick",
                new EventListener<Event>() {
                    public void onEvent(Event event) throws Exception {
                        reset();
                    }
                });
        this.editDataWindow.doModal();
    }

    protected void editDataProcess() throws Exception {
        Integer processId = this.process.getId();
        String processName = this.processNameT.getValue();
        String domain = this.domainCB.getValue();
        String username = this.ownerCB.getValue();
        String preVersion = this.preVersion.getVersionNumber();
        String newVersion = this.versionNumberT.getValue();
        boolean isPublic = this.makePublicCb.isChecked();
        String ranking = null;
        if (this.rankingRG.getSelectedItem() != null && "uncheck all".compareTo(this.rankingRG.getSelectedItem().getLabel()) != 0) {
            ranking = this.rankingRG.getSelectedItem().getLabel();
        }
        if (this.processNameT.getValue().compareTo("") == 0 || this.versionNumberT.getValue().compareTo("") == 0) {
            Messagebox.show("Please enter a value for each mandatory field.", "Attention", Messagebox.OK, Messagebox.ERROR);
        } else {
            getService().editProcessData(processId, processName, domain, username, preVersion, newVersion, ranking, isPublic);
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
        this.editDataWindow.detach();
    }

    protected void cancelAll() throws Exception {
        this.editDataListProcessesC.cancelAll();
    }

    protected void reset() {
        this.processNameT.setValue(this.process.getName());
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

    public Window getEditDataOneProcessWindow() {
        return editDataWindow;
    }

}
