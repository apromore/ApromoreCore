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
