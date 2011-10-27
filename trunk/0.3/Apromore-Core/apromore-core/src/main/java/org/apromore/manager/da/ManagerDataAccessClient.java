package org.apromore.manager.da;

import org.apromore.dao.DataAccessManagerManager;
import org.apromore.exception.ExceptionAllUsers;
import org.apromore.exception.ExceptionDeleteEditSession;
import org.apromore.exception.ExceptionDeleteProcessVersions;
import org.apromore.exception.ExceptionDomains;
import org.apromore.exception.ExceptionFormats;
import org.apromore.exception.ExceptionReadCanonicalAnf;
import org.apromore.exception.ExceptionReadEditSession;
import org.apromore.exception.ExceptionReadNative;
import org.apromore.exception.ExceptionReadProcessSummaries;
import org.apromore.exception.ExceptionReadUser;
import org.apromore.exception.ExceptionUpdateProcess;
import org.apromore.exception.ExceptionWriteEditSession;
import org.apromore.exception.ExceptionWriteUser;
import org.apromore.model.AnnotationsType;
import org.apromore.model.DeleteEditSessionInputMsgType;
import org.apromore.model.DeleteEditSessionOutputMsgType;
import org.apromore.model.DeleteProcessVersionsInputMsgType;
import org.apromore.model.DeleteProcessVersionsOutputMsgType;
import org.apromore.model.DomainsType;
import org.apromore.model.EditProcessDataInputMsgType;
import org.apromore.model.EditProcessDataOutputMsgType;
import org.apromore.model.EditSessionType;
import org.apromore.model.FormatType;
import org.apromore.model.NativeTypesType;
import org.apromore.model.ProcessSummariesType;
import org.apromore.model.ProcessSummaryType;
import org.apromore.model.ProcessVersionIdentifierType;
import org.apromore.model.ReadAllUsersInputMsgType;
import org.apromore.model.ReadAllUsersOutputMsgType;
import org.apromore.model.ReadCanonicalAnfInputMsgType;
import org.apromore.model.ReadCanonicalAnfOutputMsgType;
import org.apromore.model.ReadDomainsInputMsgType;
import org.apromore.model.ReadDomainsOutputMsgType;
import org.apromore.model.ReadEditSessionInputMsgType;
import org.apromore.model.ReadEditSessionOutputMsgType;
import org.apromore.model.ReadFormatInputMsgType;
import org.apromore.model.ReadFormatOutputMsgType;
import org.apromore.model.ReadNativeTypesInputMsgType;
import org.apromore.model.ReadNativeTypesOutputMsgType;
import org.apromore.model.ReadProcessSummariesInputMsgType;
import org.apromore.model.ReadProcessSummariesOutputMsgType;
import org.apromore.model.ReadUserInputMsgType;
import org.apromore.model.ReadUserOutputMsgType;
import org.apromore.model.ResultType;
import org.apromore.model.SearchHistoriesType;
import org.apromore.model.UserType;
import org.apromore.model.UsernamesType;
import org.apromore.model.VersionSummaryType;
import org.apromore.model.WriteEditSessionInputMsgType;
import org.apromore.model.WriteEditSessionOutputMsgType;
import org.apromore.model.WriteUserInputMsgType;
import org.apromore.model.WriteUserOutputMsgType;
import org.springframework.beans.factory.annotation.Autowired;

import javax.activation.DataHandler;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

public class ManagerDataAccessClient {

	private DataAccessManagerManager manager;

	private InputStream cpf;
	private InputStream anf;

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


	public InputStream ReadFormat(int processId, String version,
			String nativeType) throws ExceptionReadProcessSummaries, IOException, ExceptionReadNative {	
		ReadFormatInputMsgType payload0 = new ReadFormatInputMsgType();
		payload0.setProcessId(processId);
		payload0.setVersion(version);
		payload0.setFormat(nativeType);

		ReadFormatOutputMsgType res = manager.readFormat(payload0);
		ResultType result = res.getResult();
		if (result.getCode() == 0) {
			// if native found return it
			DataHandler handler = res.getNative();
			InputStream is = handler.getInputStream();
			return is;
		} else {
			throw new ExceptionReadNative(result.getMessage());
		}
	}

	public void ReadCanonicalAnf(int processId, String version, boolean withAnnotation, String annotationName) throws ExceptionReadCanonicalAnf, IOException {
		ReadCanonicalAnfInputMsgType payload = new ReadCanonicalAnfInputMsgType();
		payload.setProcessId(processId);
		payload.setVersion(version);
		payload.setAnnotationName(annotationName);
		payload.setWithAnnotation(withAnnotation);
		ReadCanonicalAnfOutputMsgType res = manager.readCanonicalAnf(payload);
		ResultType result = res.getResult();
		if (result.getCode() == 0) {
			DataHandler handler_cpf = res.getCpf();
			this.cpf = handler_cpf.getInputStream();
			DataHandler handler_anf = res.getAnf();
			if (handler_anf != null) {
				this.anf = handler_anf.getInputStream();
			}
		} else {
			throw new ExceptionReadCanonicalAnf(result.getMessage());
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

	public InputStream getCpf() {
		return cpf;
	}

	public InputStream getAnf() {
		return anf;
	}
}







