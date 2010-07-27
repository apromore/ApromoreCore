package org.apromore.manager.da;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.List;

import javax.activation.DataHandler;
import javax.xml.namespace.QName;

import org.apromore.manager.commons.Constants;
import org.apromore.manager.exception.ExceptionAllUsers;
import org.apromore.manager.exception.ExceptionDeleteEditSession;
import org.apromore.manager.exception.ExceptionDeleteProcessVersions;
import org.apromore.manager.exception.ExceptionDomains;
import org.apromore.manager.exception.ExceptionFormats;
import org.apromore.manager.exception.ExceptionReadCanonicalAnf;
import org.apromore.manager.exception.ExceptionReadEditSession;
import org.apromore.manager.exception.ExceptionReadNative;
import org.apromore.manager.exception.ExceptionReadProcessSummaries;
import org.apromore.manager.exception.ExceptionReadUser;
import org.apromore.manager.exception.ExceptionUpdateProcess;
import org.apromore.manager.exception.ExceptionWriteEditSession;
import org.apromore.manager.exception.ExceptionWriteUser;
import org.apromore.manager.model_da.DeleteEditSessionInputMsgType;
import org.apromore.manager.model_da.DeleteProcessVersionsInputMsgType;
import org.apromore.manager.model_da.EditDataProcessInputMsgType;
import org.apromore.manager.model_da.ProcessVersionIdentifierType;
import org.apromore.manager.model_da.ReadAllUsersInputMsgType;
import org.apromore.manager.model_da.ReadCanonicalAnfInputMsgType;
import org.apromore.manager.model_da.ReadDomainsInputMsgType;
import org.apromore.manager.model_da.ReadDomainsOutputMsgType;
import org.apromore.manager.model_da.ReadEditSessionInputMsgType;
import org.apromore.manager.model_da.ReadFormatInputMsgType;
import org.apromore.manager.model_da.ReadNativeTypesInputMsgType;
import org.apromore.manager.model_da.ReadNativeTypesOutputMsgType;
import org.apromore.manager.model_da.ReadProcessSummariesInputMsgType;
import org.apromore.manager.model_da.ReadUserInputMsgType;
import org.apromore.manager.model_da.ResultType;
import org.apromore.manager.model_da.SearchHistoriesType;
import org.apromore.manager.model_da.UserType;
import org.apromore.manager.model_da.WriteEditSessionInputMsgType;
import org.apromore.manager.model_da.WriteUserInputMsgType;
import org.apromore.manager.model_da.WriteUserOutputMsgType;
import org.apromore.manager.model_portal.ProcessSummariesType;
import org.apromore.manager.model_portal.ProcessSummaryType;
import org.apromore.manager.model_portal.UsernamesType;
import org.apromore.manager.model_portal.VersionSummaryType;

public class RequestToDA {


	private static final QName SERVICE_NAME = new QName(Constants.DA_MANAGER_URI, Constants.DA_MANAGER_SERVICE);
	private DAManagerPortType port;

	private InputStream cpf;
	private InputStream anf;

	public RequestToDA() {
		URL wsdlURL = DAManagerService.WSDL_LOCATION;
		DAManagerService ss = new DAManagerService(wsdlURL, SERVICE_NAME);
		this.port = ss.getDAManager();
	}


	/**
	 * Build the payload for the request UpdateProcesses to be sent to the DA. For each
	 * process summary in processes (according to model_portal) match its counter part
	 * according to model_da. 
	 * @param process
	 * @throws ExceptionUpdateProcess
	 */
	public void EditDataProcess (Integer processId, String processName, String domain, String username,
			String preVersion, String newVersion, String ranking) throws ExceptionUpdateProcess {

		org.apromore.manager.model_da.EditDataProcessInputMsgType payload =
			new EditDataProcessInputMsgType();

		payload.setDomain(domain);
		payload.setProcessName(processName);
		payload.setOwner(username);
		payload.setId(processId);
		payload.setNewName(newVersion);
		payload.setPreName(preVersion);
		payload.setRanking(ranking);
		org.apromore.manager.model_da.EditDataProcessOutputMsgType res = this.port.editDataProcess(payload);
		ResultType result = res.getResult();
		if (result.getCode() == -1) {
			throw new ExceptionUpdateProcess (result.getMessage()); 
		}
	}

	public org.apromore.manager.model_portal.NativeTypesType
	ReadNativeTypes() throws ExceptionFormats {
		// payload empty
		ReadNativeTypesInputMsgType payload = new ReadNativeTypesInputMsgType();
		ReadNativeTypesOutputMsgType res = this.port.readNativeTypes(payload);
		ResultType result = res.getResult();
		if (result.getCode() == -1) {
			throw new ExceptionFormats (result.getMessage()); 
		} else {
			List<org.apromore.manager.model_da.FormatType> formats_da = res.getNativeTypes().getNativeType();
			org.apromore.manager.model_portal.NativeTypesType resFormats_p = 
				new org.apromore.manager.model_portal.NativeTypesType();
			for (int i=0;i<formats_da.size();i++){
				org.apromore.manager.model_portal.FormatType format_p = 
					new org.apromore.manager.model_portal.FormatType();
				format_p.setFormat(formats_da.get(i).getFormat());
				format_p.setExtension(formats_da.get(i).getExtension());
				resFormats_p.getNativeType().add(format_p);
			}
			return resFormats_p;
		}
	}

	public org.apromore.manager.model_portal.DomainsType 
	ReadDomains() throws ExceptionDomains{
		// payload empty
		ReadDomainsInputMsgType payload = new ReadDomainsInputMsgType();
		ReadDomainsOutputMsgType res = this.port.readDomains(payload);
		ResultType result = res.getResult();
		if (result.getCode() == -1) {
			throw new ExceptionDomains (result.getMessage()); 
		} else {
			List<String> domains = res.getDomains().getDomain();
			org.apromore.manager.model_portal.DomainsType resDomains = new org.apromore.manager.model_portal.DomainsType();
			resDomains.getDomain().addAll(domains);
			return resDomains;
		}

	}

	public org.apromore.manager.model_portal.UsernamesType ReadAllUsers() throws ExceptionAllUsers {
		org.apromore.manager.model_da.ReadAllUsersInputMsgType payload =
			new ReadAllUsersInputMsgType();
		org.apromore.manager.model_da.ReadAllUsersOutputMsgType res = this.port.readAllUsers(payload);
		ResultType result = res.getResult();
		if (result.getCode() == -1) {
			throw new ExceptionAllUsers (result.getMessage()); 
		} else {
			org.apromore.manager.model_da.UsernamesType allUsersDA = res.getUsernames();
			org.apromore.manager.model_portal.UsernamesType allUsersP = new UsernamesType();
			allUsersP.getUsername().addAll(allUsersDA.getUsername());
			return allUsersP;
		}
	}

	
	public void WriteUser(org.apromore.manager.model_portal.UserType userP) throws ExceptionWriteUser {

		WriteUserInputMsgType payload = new WriteUserInputMsgType();
		UserType userM = new UserType();
		userM.setEmail(userP.getEmail());
		userM.setFirstname(userP.getFirstname());
		userM.setLastname(userP.getLastname());
		userM.setPasswd(userP.getPasswd());
		userM.setUsername(userP.getUsername());
		for (int i=0;i<userP.getSearchHistories().size();i++) {
			SearchHistoriesType sht = new SearchHistoriesType();
			sht.setNum(userP.getSearchHistories().get(i).getNum());
			sht.setSearch(userP.getSearchHistories().get(i).getSearch());
			userM.getSearchHistories().add(sht);
		}
		payload.setUser(userM);

		WriteUserOutputMsgType res = this.port.writeUser(payload);
		ResultType result = res.getResult();
		if (result.getCode() == -1) {
			throw new ExceptionWriteUser (result.getMessage()); 
		}
	}

	public org.apromore.manager.model_portal.UserType 
	ReadUser(String username) throws ExceptionReadUser {

		org.apromore.manager.model_da.ReadUserInputMsgType payload = new ReadUserInputMsgType();
		payload.setUsername(username);
		org.apromore.manager.model_da.ReadUserOutputMsgType res = this.port.readUser(payload);
		org.apromore.manager.model_da.ResultType result = res.getResult();

		if (result.getCode() == -1) {
			throw new ExceptionReadUser (result.getMessage()); 
		} else {
			org.apromore.manager.model_portal.UserType user = new org.apromore.manager.model_portal.UserType();
			user.setEmail(res.getUser().getEmail());
			user.setFirstname(res.getUser().getFirstname());
			user.setLastname(res.getUser().getLastname());
			user.setPasswd(res.getUser().getPasswd());
			user.setUsername(res.getUser().getUsername());
			for (int i=0; i<res.getUser().getSearchHistories().size(); i++) {
				org.apromore.manager.model_portal.SearchHistoriesType sht = new org.apromore.manager.model_portal.SearchHistoriesType();
				sht.setNum(res.getUser().getSearchHistories().get(i).getNum());
				sht.setSearch(res.getUser().getSearchHistories().get(i).getSearch());
				user.getSearchHistories().add(sht);
			}
			return user;
		}

	}


	public org.apromore.manager.model_portal.ProcessSummariesType 
	ReadProcessSummaries(String searchExpression) 
	throws ExceptionReadProcessSummaries {
		org.apromore.manager.model_da.ReadProcessSummariesInputMsgType payload = new ReadProcessSummariesInputMsgType();
		payload.setSearchExpression(searchExpression);
		org.apromore.manager.model_da.ReadProcessSummariesOutputMsgType res = this.port.readProcessSummaries(payload);
		org.apromore.manager.model_da.ResultType result = res.getResult();
		if (result.getCode() == -1) {
			throw new ExceptionReadProcessSummaries(result.getMessage()); 
		} else {
			org.apromore.manager.model_portal.ProcessSummariesType processesP = new ProcessSummariesType();
			org.apromore.manager.model_da.ProcessSummariesType processesM = res.getProcessSummaries(); 
			for (int i=0; i<processesM.getProcessSummary().size();i++){
				org.apromore.manager.model_da.ProcessSummaryType processM = processesM.getProcessSummary().get(i);
				org.apromore.manager.model_portal.ProcessSummaryType processP = new ProcessSummaryType();
				processesP.getProcessSummary().add(processP);

				processP.setDomain(processM.getDomain());
				processP.setId(processM.getId());
				processP.setLastVersion(processM.getLastVersion());
				processP.setName(processM.getName());
				processP.setOriginalNativeType(processM.getOriginalNativeType());
				processP.setRanking(processM.getRanking());
				processP.setOwner(processM.getOwner());

				for (int j=0;j<processM.getVersionSummaries().size();j++){
					org.apromore.manager.model_da.VersionSummaryType versionM = processM.getVersionSummaries().get(j);
					org.apromore.manager.model_portal.VersionSummaryType versionP = new VersionSummaryType();
					processP.getVersionSummaries().add(versionP);
					versionP.setCreationDate(versionM.getCreationDate());
					versionP.setLastUpdate(versionM.getLastUpdate());
					versionP.setName(versionM.getName());
					versionP.setRanking(versionM.getRanking());					
					versionP.setDocumentation(versionM.getDocumentation());
					versionP.getAnnotations().addAll(versionM.getAnnotations());
				}
			}
			return processesP;
		}
	}


	public InputStream ReadFormat(int processId, String version,
			String nativeType) throws ExceptionReadProcessSummaries, IOException, ExceptionReadNative {	
		org.apromore.manager.model_da.ReadFormatInputMsgType payload0 = new ReadFormatInputMsgType();
		payload0.setProcessId(processId);
		payload0.setVersion(version);
		payload0.setFormat(nativeType);

		org.apromore.manager.model_da.ReadFormatOutputMsgType res = this.port.readFormat(payload0);
		org.apromore.manager.model_da.ResultType result = res.getResult();
		if (result.getCode() == 0) {
			// if native found return it
			DataHandler handler = res.getNative();
			InputStream is = handler.getInputStream();
			return is;
		} else {
			throw new ExceptionReadNative(result.getMessage());
		}
	}

	public void ReadCanonicalAnf(int processId, String version, boolean withAnnotation, String annotationName) 
	throws ExceptionReadCanonicalAnf, IOException {
		org.apromore.manager.model_da.ReadCanonicalAnfInputMsgType payload = new ReadCanonicalAnfInputMsgType();
		payload.setProcessId(processId);
		payload.setVersion(version);
		payload.setAnnotationName(annotationName);
		payload.setWithAnnotation(withAnnotation);
		org.apromore.manager.model_da.ReadCanonicalAnfOutputMsgType res = this.port.readCanonicalAnf(payload);
		org.apromore.manager.model_da.ResultType result = res.getResult();
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

	public org.apromore.manager.model_da.EditSessionType 
	ReadEditSession (int code) throws ExceptionReadEditSession {

		org.apromore.manager.model_da.EditSessionType editSession;
		org.apromore.manager.model_da.ReadEditSessionInputMsgType payload = 
			new ReadEditSessionInputMsgType();
		payload.setEditSessionCode(code);
		org.apromore.manager.model_da.ReadEditSessionOutputMsgType res = this.port.readEditSession(payload);
		org.apromore.manager.model_da.ResultType result = res.getResult();
		if (result.getCode() == 0) {
			editSession = res.getEditSession();
			return editSession;
		} else {
			throw new ExceptionReadEditSession(result.getMessage());
		}

	}

	public int WriteEditSession (org.apromore.manager.model_da.EditSessionType editSession) throws ExceptionWriteEditSession {
		org.apromore.manager.model_da.WriteEditSessionInputMsgType payload =
			new WriteEditSessionInputMsgType();
		payload.setEditSession(editSession);
		org.apromore.manager.model_da.WriteEditSessionOutputMsgType res = this.port.writeEditSession(payload);
		org.apromore.manager.model_da.ResultType result = res.getResult();
		if (result.getCode() != 0) {
			throw new ExceptionWriteEditSession(result.getMessage());
		} else {
			return res.getEditSessionCode();
		}
	}	

	public void DeleteEditSession (int code) throws ExceptionDeleteEditSession {
		org.apromore.manager.model_da.DeleteEditSessionInputMsgType payload =
			new DeleteEditSessionInputMsgType();
		payload.setEditSessionCode(code);
		org.apromore.manager.model_da.DeleteEditSessionOutputMsgType res = this.port.deleteEditSession(payload);
		org.apromore.manager.model_da.ResultType result = res.getResult();
		if (result.getCode() != 0) {
			throw new ExceptionDeleteEditSession(result.getMessage());
		}
	}


	public void DeleteProcessVersion (List<ProcessVersionIdentifierType> processVersions) 
	throws ExceptionDeleteProcessVersions {
		org.apromore.manager.model_da.DeleteProcessVersionsInputMsgType payload =
			new DeleteProcessVersionsInputMsgType();
		payload.getProcessVersionIdentifier().clear();
		payload.getProcessVersionIdentifier().addAll(processVersions);
		org.apromore.manager.model_da.DeleteProcessVersionsOutputMsgType res =
			this.port.deleteProcessVersions(payload);
		org.apromore.manager.model_da.ResultType result = res.getResult();
		if (result.getCode() != 0) {
			throw new ExceptionDeleteProcessVersions(result.getMessage());
		}
	}
	public DAManagerPortType getPort() {
		return port;
	}

	public InputStream getCpf() {
		return cpf;
	}

	public InputStream getAnf() {
		return anf;
	}
}







