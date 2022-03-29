/*-
 * #%L
 * This file is part of "Apromore Core".
 * 
 * Copyright (C) 2014 - 2017 Queensland University of Technology.
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

package org.apromore.portal.dialogController.dto;

import java.util.HashMap;
import java.util.Set;

import org.apromore.plugin.property.RequestParameterType;
import org.apromore.portal.dialogController.MainController;
import org.apromore.portal.model.EditSessionType;
import org.apromore.portal.model.ProcessSummaryType;
import org.apromore.portal.model.VersionSummaryType;
import org.deckfour.xes.model.XLog;

/**
 * Stores the Signavio Session information for an edit session.
 *
 * @author Cameron James
 */
public class ApromoreSession extends HashMap {

    private EditSessionType editSession, editSession2;
    private MainController mainC;
    private ProcessSummaryType process, process2;
    private VersionSummaryType version, version2;
    private Set<RequestParameterType<?>> params;
    private XLog log;

    /**
     * Public Default Constructor.
     */
    public ApromoreSession() { }

    /**
     * Constructor that builds the object.
     * @param editSession the edit session.
     * @param editSession2 another edit session for the second process model in the case of a BP-diff comparison, <code>null</code> otherwise
     * @param mainC the main controller
     * @param process the process model
     * @param version the version of that process model.
     * @param process2 the second process model in the case of a BP-diff comparison, <code>null</code> otherwise
     * @param version2 the version of that second process model, <code>null</code> if no second model
     * @param params the canoniser params.
     */
    public ApromoreSession(EditSessionType editSession, EditSessionType editSession2, MainController mainC, ProcessSummaryType process, VersionSummaryType version, ProcessSummaryType process2, VersionSummaryType version2,
            Set<RequestParameterType<?>> params) {
        this.editSession = editSession;
        this.editSession2 = editSession2;
        this.mainC = mainC;
        this.process = process;
        this.version = version;
        this.process2 = process2;
        this.version2 = version2;
        this.params = params;
    }

    public EditSessionType getEditSession() {
        return editSession;
    }

    public void setEditSession(EditSessionType editSession) {
        this.editSession = editSession;
    }

    public EditSessionType getEditSession2() {
        return editSession2;
    }

    public void setEditSession2(EditSessionType editSession2) {
        this.editSession2 = editSession2;
    }

    public MainController getMainC() {
        return mainC;
    }

    public void setMainC(MainController mainC) {
        this.mainC = mainC;
    }

    public ProcessSummaryType getProcess() {
        return process;
    }

    public void setProcess(ProcessSummaryType process) {
        this.process = process;
    }

    public XLog getLog() {
        return log;
    }

    public void setLog(XLog log) {
        this.log = log;
    }

    public VersionSummaryType getVersion() {
        return version;
    }

    public void setVersion(VersionSummaryType version) {
        this.version = version;
    }

    public ProcessSummaryType getProcess2() {
        return process2;
    }

    public void setProcess2(ProcessSummaryType process2) {
        this.process2 = process2;
    }

    public VersionSummaryType getVersion2() {
        return version2;
    }

    public void setVersion2(VersionSummaryType version2) {
        this.version2 = version2;
    }

    public Set<RequestParameterType<?>> getParams() {
        return params;
    }

    public void setParams(Set<RequestParameterType<?>> params) {
        this.params = params;
    }
    
    public boolean containVersion(String versionNumber) {
        for (VersionSummaryType version : this.process.getVersionSummaries()) {
            if (version.getVersionNumber().equalsIgnoreCase(versionNumber)) {
                return true;
            }
        }
        return false;
    }
}
