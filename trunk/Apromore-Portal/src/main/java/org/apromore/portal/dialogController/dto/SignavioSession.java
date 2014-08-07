/*
 * Copyright © 2009-2014 The Apromore Initiative.
 *
 * This file is part of “Apromore”.
 *
 * “Apromore” is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 3 of the License, or
 * (at your option) any later version.
 *
 * “Apromore” is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.
 * If not, see <http://www.gnu.org/licenses/lgpl-3.0.html>.
 */

package org.apromore.portal.dialogController.dto;

import org.apromore.model.EditSessionType;
import org.apromore.model.ProcessSummaryType;
import org.apromore.model.VersionSummaryType;
import org.apromore.plugin.property.RequestParameterType;
import org.apromore.portal.dialogController.MainController;

import java.util.Set;

/**
 * Stores the Signavio Session information for an edit session.
 *
 * @author Cameron James
 */
public class SignavioSession {

    private EditSessionType editSession;
    private MainController mainC;
    private ProcessSummaryType process;
    private VersionSummaryType version;
    private Set<RequestParameterType<?>> params;

    /**
     * Public Default Constructor.
     */
    public SignavioSession() { }

    /**
     * Constructor that builds the object.
     * @param editSession the edit session.
     * @param mainC the main controller
     * @param process the process model
     * @param version the version of that process model.
     * @param params the canoniser params.
     */
    public SignavioSession(EditSessionType editSession, MainController mainC, ProcessSummaryType process, VersionSummaryType version,
            Set<RequestParameterType<?>> params) {
        this.editSession = editSession;
        this.mainC = mainC;
        this.process = process;
        this.version = version;
        this.params = params;
    }


    public EditSessionType getEditSession() {
        return editSession;
    }

    public void setEditSession(EditSessionType editSession) {
        this.editSession = editSession;
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

    public VersionSummaryType getVersion() {
        return version;
    }

    public void setVersion(VersionSummaryType version) {
        this.version = version;
    }

    public Set<RequestParameterType<?>> getParams() {
        return params;
    }

    public void setParams(Set<RequestParameterType<?>> params) {
        this.params = params;
    }
}
