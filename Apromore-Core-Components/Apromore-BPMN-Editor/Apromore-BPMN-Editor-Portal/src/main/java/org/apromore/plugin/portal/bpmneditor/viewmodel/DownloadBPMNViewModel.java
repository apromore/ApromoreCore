/*-
 * #%L
 * This file is part of "Apromore Core".
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

package org.apromore.plugin.portal.bpmneditor.viewmodel;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import javax.xml.parsers.ParserConfigurationException;
import lombok.Getter;
import lombok.Setter;
import org.apromore.exception.CircularReferenceException;
import org.apromore.exception.ExportFormatException;
import org.apromore.exception.RepositoryException;
import org.apromore.portal.common.UserSessionManager;
import org.apromore.portal.helper.Version;
import org.apromore.portal.model.ProcessSummaryType;
import org.apromore.portal.model.UserType;
import org.apromore.portal.model.VersionSummaryType;
import org.apromore.service.ProcessService;
import org.apromore.zk.notification.Notification;
import org.zkoss.bind.annotation.BindingParam;
import org.zkoss.bind.annotation.Command;
import org.zkoss.bind.annotation.ExecutionArgParam;
import org.zkoss.bind.annotation.Init;
import org.zkoss.util.resource.Labels;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.select.annotation.VariableResolver;
import org.zkoss.zk.ui.select.annotation.WireVariable;
import org.zkoss.zul.Filedownload;
import org.zkoss.zul.Messagebox;

@VariableResolver(org.zkoss.zkplus.spring.DelegatingVariableResolver.class)
public class DownloadBPMNViewModel {
    private static final String WINDOW_PARAM = "window";

    @WireVariable
    private ProcessService processService;

    @Getter @Setter
    private boolean includeLinkedSubprocesses;

    private ProcessSummaryType process;
    private VersionSummaryType version;

    @Init
    public void init(@ExecutionArgParam("process") final ProcessSummaryType proc,
                     @ExecutionArgParam("version") final VersionSummaryType vst) {
        process = proc;
        version = vst;
    }

    @Command
    public void download(@BindingParam(WINDOW_PARAM) final Component window)
        throws RepositoryException, ParserConfigurationException, ExportFormatException {
        UserType currentUser = UserSessionManager.getCurrentUser();

        if (currentUser == null) {
            Notification.error("Unable to fetch the current user");
            return;
        }

        try {
            String bpmnXML = processService.getBPMNRepresentation(process.getName(), process.getId(), "MAIN",
                new Version(version.getVersionNumber()), currentUser.getUsername(), includeLinkedSubprocesses);
            InputStream is = new ByteArrayInputStream(bpmnXML.getBytes());
            Filedownload.save(is, "text/xml", process.getName() + ".bpmn");
            window.detach();
        } catch (CircularReferenceException e) {
            Messagebox.show(Labels.getLabel("bpmnEditor_exportCircularReference_message",
                "You cannot export this model. The linked models form a loop. Please review the linked subprocesses."),
                Labels.getLabel("common_unknown_title", "Error"), Messagebox.OK, Messagebox.ERROR);
        } catch (ExportFormatException e) {
            Messagebox.show("Unable to export model with linked subprocesses. Reason: " + e.getMessage(),
                Labels.getLabel("common_unknown_title", "Error"), Messagebox.OK, Messagebox.ERROR);
        }
    }

}
