package org.apromore.manager.da;

import org.apromore.dao.DataAccessManagerManager;
import org.apromore.exception.ExceptionDeleteEditSession;
import org.apromore.exception.ExceptionReadEditSession;
import org.apromore.exception.ExceptionUpdateProcess;
import org.apromore.model.DeleteEditSessionInputMsgType;
import org.apromore.model.DeleteEditSessionOutputMsgType;
import org.apromore.model.EditProcessDataInputMsgType;
import org.apromore.model.EditProcessDataOutputMsgType;
import org.apromore.model.EditSessionType;
import org.apromore.model.ReadEditSessionInputMsgType;
import org.apromore.model.ReadEditSessionOutputMsgType;
import org.apromore.model.ResultType;

public class ManagerDataAccessClient {

    private DataAccessManagerManager manager;

    /**
     * Build the payload for the request UpdateProcesses to be sent to the DA. For each
     * process summary in processes (according to model_portal) match its counter part
     * according to model_da.
     */
    public void EditProcessData(Integer processId, String processName, String domain, String username,
                                String preVersion, String newVersion, String ranking) throws ExceptionUpdateProcess {
        EditProcessDataInputMsgType payload = new EditProcessDataInputMsgType();

        payload.setDomain(domain);
        payload.setProcessName(processName);
        payload.setOwner(username);
        payload.setId(processId);
        payload.setNewName(newVersion);
        payload.setPreName(preVersion);
        payload.setRanking(ranking);
        EditProcessDataOutputMsgType res = manager.editProcessData(payload);
        ResultType result = res.getResult();
        if (result.getCode() == -1) {
            throw new ExceptionUpdateProcess(result.getMessage());
        }
    }

    public EditSessionType ReadEditSession(int code) throws ExceptionReadEditSession {
        EditSessionType editSession;
        ReadEditSessionInputMsgType payload = new ReadEditSessionInputMsgType();
        payload.setEditSessionCode(code);
        ReadEditSessionOutputMsgType res = manager.readEditSession(payload);
        ResultType result = res.getResult();
        if (result.getCode() == 0) {
            editSession = res.getEditSession();
            return editSession;
        }
        else {
            throw new ExceptionReadEditSession(result.getMessage());
        }

    }

    public void DeleteEditSession(int code) throws ExceptionDeleteEditSession {
        DeleteEditSessionInputMsgType payload = new DeleteEditSessionInputMsgType();
        payload.setEditSessionCode(code);
        DeleteEditSessionOutputMsgType res = manager.deleteEditSession(payload);
        ResultType result = res.getResult();
        if (result.getCode() != 0) {
            throw new ExceptionDeleteEditSession(result.getMessage());
        }
    }


    public void setManager(DataAccessManagerManager manager) {
        this.manager = manager;
    }

}







