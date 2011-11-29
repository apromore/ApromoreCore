package org.apromore.manager.da;

import org.apromore.dao.DataAccessManagerManager;
import org.apromore.exception.ExceptionDeleteEditSession;
import org.apromore.exception.ExceptionDeleteProcessVersions;
import org.apromore.exception.ExceptionReadEditSession;
import org.apromore.exception.ExceptionUpdateProcess;
import org.apromore.exception.ExceptionWriteEditSession;
import org.apromore.model.DeleteEditSessionInputMsgType;
import org.apromore.model.DeleteEditSessionOutputMsgType;
import org.apromore.model.DeleteProcessVersionsInputMsgType;
import org.apromore.model.DeleteProcessVersionsOutputMsgType;
import org.apromore.model.EditProcessDataInputMsgType;
import org.apromore.model.EditProcessDataOutputMsgType;
import org.apromore.model.EditSessionType;
import org.apromore.model.ProcessVersionIdentifierType;
import org.apromore.model.ReadEditSessionInputMsgType;
import org.apromore.model.ReadEditSessionOutputMsgType;
import org.apromore.model.ResultType;
import org.apromore.model.WriteEditSessionInputMsgType;
import org.apromore.model.WriteEditSessionOutputMsgType;

import java.util.List;

public class ManagerDataAccessClient {

	private DataAccessManagerManager manager;

	/**
	 * Build the payload for the request UpdateProcesses to be sent to the DA. For each
	 * process summary in processes (according to model_portal) match its counter part
	 * according to model_da.
	 */
	public void EditProcessData (Integer processId, String processName, String domain, String username,
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
			throw new ExceptionUpdateProcess (result.getMessage()); 
		}
	}

	public EditSessionType ReadEditSession (int code) throws ExceptionReadEditSession {
		EditSessionType editSession;
		ReadEditSessionInputMsgType payload = new ReadEditSessionInputMsgType();
		payload.setEditSessionCode(code);
		ReadEditSessionOutputMsgType res = manager.readEditSession(payload);
		ResultType result = res.getResult();
		if (result.getCode() == 0) {
			editSession = res.getEditSession();
			return editSession;
		} else {
			throw new ExceptionReadEditSession(result.getMessage());
		}

	}

	public int WriteEditSession (EditSessionType editSession) throws ExceptionWriteEditSession {
		WriteEditSessionInputMsgType payload = new WriteEditSessionInputMsgType();
		payload.setEditSession(editSession);
		WriteEditSessionOutputMsgType res = manager.writeEditSession(payload);
		ResultType result = res.getResult();
		if (result.getCode() != 0) {
			throw new ExceptionWriteEditSession(result.getMessage());
		} else {
			return res.getEditSessionCode();
		}
	}	

	public void DeleteEditSession (int code) throws ExceptionDeleteEditSession {
		DeleteEditSessionInputMsgType payload = new DeleteEditSessionInputMsgType();
		payload.setEditSessionCode(code);
		DeleteEditSessionOutputMsgType res = manager.deleteEditSession(payload);
		ResultType result = res.getResult();
		if (result.getCode() != 0) {
			throw new ExceptionDeleteEditSession(result.getMessage());
		}
	}


	public void DeleteProcessVersion (List<ProcessVersionIdentifierType> processVersions) throws ExceptionDeleteProcessVersions {
		DeleteProcessVersionsInputMsgType payload = new DeleteProcessVersionsInputMsgType();
		payload.getProcessVersionIdentifier().clear();
		payload.getProcessVersionIdentifier().addAll(processVersions);
		DeleteProcessVersionsOutputMsgType res = manager.deleteProcessVersions(payload);
		ResultType result = res.getResult();
		if (result.getCode() != 0) {
			throw new ExceptionDeleteProcessVersions(result.getMessage());
		}
	}



    public void setManager(DataAccessManagerManager manager) {
        this.manager = manager;
    }

}







