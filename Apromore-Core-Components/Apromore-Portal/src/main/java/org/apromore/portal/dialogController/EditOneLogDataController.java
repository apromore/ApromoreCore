/*
 * This file is part of "Apromore".
 *
 * Copyright (C) 2018 - 2020 The University of Melbourne.
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

import org.apromore.model.LogSummaryType;
import org.apromore.portal.exception.ExceptionAllUsers;
import org.apromore.portal.exception.ExceptionDomains;
import org.apromore.service.EventLogService;
import org.zkoss.spring.SpringUtil;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.SuspendNotAllowedException;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zul.*;

import java.util.List;

public class EditOneLogDataController extends BaseController {

    private Window editDataWindow;

    private MainController mainController;
    private EditListLogDataController editListLogDataController;
    private LogSummaryType log;
    private Textbox logNameT;
    private Checkbox makePublicCb;

    public EditOneLogDataController(MainController mainC, EditListLogDataController editListLogDataController,
                                    LogSummaryType log)
            throws SuspendNotAllowedException, InterruptedException, ExceptionAllUsers, ExceptionDomains {
        this.mainController = mainC;
        this.editListLogDataController = editListLogDataController;
        this.log = log;

        this.editDataWindow = (Window) Executions.createComponents("macros/editlogdata.zul", null, null);
        this.editDataWindow.setTitle("Edit log metadata");

        this.logNameT = (Textbox) editDataWindow.getFellow("logname");
        this.makePublicCb = (Checkbox) editDataWindow.getFellow("logpublic");

        Button okB = (Button) editDataWindow.getFellow("ok");
        Button cancelB = (Button) editDataWindow.getFellow("cancel");
        Button cancelAllB = (Button) editDataWindow.getFellow("cancelall");
        Button resetB = (Button) editDataWindow.getFellow("reset");

        //publicR.setVisible(false);
        cancelAllB.setVisible(this.editListLogDataController.getToEditList().size() > 0);
        reset();

        okB.addEventListener("onClick",
                new EventListener<Event>() {
                    public void onEvent(Event event) throws Exception {
                        editLogData();
                    }
                });
        this.editDataWindow.addEventListener("onOK",
                new EventListener<Event>() {
                    public void onEvent(Event event) throws Exception {
                        editLogData();
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

    protected void editLogData() throws Exception {
        Integer logId = this.log.getId();
        String logName = this.logNameT.getValue();
        boolean isPublic = this.makePublicCb.isChecked();
        String ranking = null;
        if (this.logNameT.getValue().compareTo("") == 0) {
            Messagebox.show("Please enter a value for each mandatory field.", "Attention", Messagebox.OK, Messagebox.ERROR);
        } else {
            getService().editLogData(logId, logName, "", isPublic);
            this.editListLogDataController.getEditedList().add(this);
            this.editListLogDataController.deleteFromToBeEdited(this);
            closePopup();
        }
    }

    protected void cancel() throws Exception {
        this.editListLogDataController.deleteFromToBeEdited(this);
        closePopup();
    }

    private void closePopup() {
        mainController.clearProcessVersions();
        this.editDataWindow.detach();
    }

    protected void cancelAll() throws Exception {
        this.editListLogDataController.cancelAll();
    }

    protected void reset() {
        this.logNameT.setValue(this.log.getName());
        EventLogService eventLogService = (EventLogService) SpringUtil.getBean("eventLogService");
        this.makePublicCb.setChecked(eventLogService.isPublicLog(this.log.getId()));
    }

    public Window getEditDataOneProcessWindow() {
        return editDataWindow;
    }
}
