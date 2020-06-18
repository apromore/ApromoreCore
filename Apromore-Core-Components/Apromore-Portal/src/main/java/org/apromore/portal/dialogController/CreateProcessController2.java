/*-
 * #%L
 * This file is part of "Apromore Core".
 * %%
 * Copyright (C) 2018 - 2020 Apromore Pty Ltd.
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

import java.util.HashMap;
import java.util.HashSet;

import org.apromore.model.AnnotationsType;
import org.apromore.model.ProcessSummaryType;
import org.apromore.model.VersionSummaryType;
import org.apromore.plugin.property.RequestParameterType;
import org.apromore.portal.common.UserSessionManager;
import org.apromore.portal.exception.ExceptionAllUsers;
import org.apromore.portal.exception.ExceptionDomains;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.SuspendNotAllowedException;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zul.Button;
import org.zkoss.zul.Row;
import org.zkoss.zul.Rows;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Window;

public class CreateProcessController2 extends BaseController {

    private final Window createProcessW;

    private final MainController mainC;
    private final Textbox processNameT;
    private final Textbox versionNumberT;

    public CreateProcessController2(final MainController mainC, final HashMap<String, String> formats_ext) throws SuspendNotAllowedException,
            InterruptedException, ExceptionAllUsers, ExceptionDomains {
        this.mainC = mainC;

        this.createProcessW = (Window) Executions.createComponents("macros/createProcess.zul", null, null);
        this.createProcessW.setTitle("Create model");
        Rows rows = (Rows) this.createProcessW.getFirstChild().getFirstChild().getFirstChild().getNextSibling();

        Row processNameR = (Row) rows.getFirstChild(); 
        this.processNameT = (Textbox) processNameR.getFirstChild().getNextSibling();
        
        Row versionNameR = (Row) processNameR.getNextSibling();
        this.versionNumberT = (Textbox) versionNameR.getFirstChild().getNextSibling();

        Row buttonR = (Row)versionNameR.getNextSibling();
        Button okB = (Button)buttonR.getFirstChild().getFirstChild();
        Button cancelB = (Button) okB.getNextSibling();
   
        reset();

        okB.addEventListener("onClick", new EventListener<Event>() {
            @Override
            public void onEvent(final Event event) throws Exception {
                createProcess();
            }
        });
        cancelB.addEventListener("onClick", new EventListener<Event>() {
            @Override
            public void onEvent(final Event event) throws Exception {
                cancel();
            }
        });

        this.createProcessW.doModal();
    }

    protected void createProcess() throws Exception {
        editProcess(mainC.getService().createNewEmptyProcess(UserSessionManager.getCurrentUser().getUsername()));
    }

    protected void editProcess(final ProcessSummaryType process) throws Exception {
        VersionSummaryType version = process.getVersionSummaries().get(0);
        AnnotationsType annotations = version.getAnnotations().get(0);
        String annotationName = annotations.getAnnotationName().get(0);
        String readOnly = "false";
        this.mainC.editProcess2(process, version, annotations.getNativeType(), annotationName, readOnly, new HashSet<RequestParameterType<?>>(), true);
        cancel();
    }

    protected void cancel() throws Exception {
        closePopup();
    }

    private void closePopup() {
        this.createProcessW.detach();
    }

    protected void reset() {
        this.processNameT.setValue("");
        this.versionNumberT.setValue("1.0");
    }
}
