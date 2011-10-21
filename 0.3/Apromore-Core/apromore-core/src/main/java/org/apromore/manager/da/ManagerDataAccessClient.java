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

    @Autowired
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

	public NativeTypesType ReadNativeTypes() throws ExceptionFormats {
		// payload empty
		ReadNativeTypesInputMsgType payload = new ReadNativeTypesInputMsgType();
		ReadNativeTypesOutputMsgType res = manager.readNativeTypes(payload);
		ResultType result = res.getResult();
		if (result.getCode() == -1) {
			throw new ExceptionFormats (result.getMessage()); 
		} else {
			List<FormatType> formats_da = res.getNativeTypes().getNativeType();
			NativeTypesType resFormats_p = new NativeTypesType();
			for (int i=0;i<formats_da.size();i++){
				FormatType format_p = new FormatType();
				format_p.setFormat(formats_da.get(i).getFormat());
				format_p.setExtension(formats_da.get(i).getExtension());
				resFormats_p.getNativeType().add(format_p);
			}
			return resFormats_p;
		}
	}

	public DomainsType ReadDomains() throws ExceptionDomains{
		// payload empty
		ReadDomainsInputMsgType payload = new ReadDomainsInputMsgType();
		ReadDomainsOutputMsgType res = manager.readDomains(payload);
		ResultType result = res.getResult();
		if (result.getCode() == -1) {
			throw new ExceptionDomains (result.getMessage()); 
		} else {
			List<String> domains = res.getDomains().getDomain();
			DomainsType resDomains = new DomainsType();
			resDomains.getDomain().addAll(domains);
			return resDomains;
		}

	}

//	public UsernamesType ReadAllUsers() throws ExceptionAllUsers {
//		ReadAllUsersInputMsgType payload = new ReadAllUsersInputMsgType();
//		ReadAllUsersOutputMsgType res = manager.readAllUsers(payload);
//		ResultType result = res.getResult();
//		if (result.getCode() == -1) {
//			throw new ExceptionAllUsers (result.getMessage());
//		} else {
//			UsernamesType allUsersDA = res.getUsernames();
//			UsernamesType allUsersP = new UsernamesType();
//			allUsersP.getUsername().addAll(allUsersDA.getUsername());
//			return allUsersP;
//		}
//	}

	
//	public void WriteUser(UserType userP) throws ExceptionWriteUser {
//		WriteUserInputMsgType payload = new WriteUserInputMsgType();
//		UserType userM = new UserType();
//		userM.setEmail(userP.getEmail());
//		userM.setFirstname(userP.getFirstname());
//		userM.setLastname(userP.getLastname());
//		userM.setPasswd(userP.getPasswd());
//		userM.setUsername(userP.getUsername());
//		for (int i=0;i<userP.getSearchHistories().size();i++) {
//			SearchHistoriesType sht = new SearchHistoriesType();
//			sht.setNum(userP.getSearchHistories().get(i).getNum());
//			sht.setSearch(userP.getSearchHistories().get(i).getSearch());
//			userM.getSearchHistories().add(sht);
//		}
//		payload.setUser(userM);
//
//		WriteUserOutputMsgType res = manager.writeUser(payload);
//		ResultType result = res.getResult();
//		if (result.getCode() == -1) {
//			throw new ExceptionWriteUser (result.getMessage());
//		}
//	}

//	public UserType ReadUser(String username) throws ExceptionReadUser {
//		ReadUserInputMsgType payload = new ReadUserInputMsgType();
//		payload.setUsername(username);
//		ReadUserOutputMsgType res = manager.readUser(payload);
//		ResultType result = res.getResult();
//
//		if (result.getCode() == -1) {
//			throw new ExceptionReadUser (result.getMessage());
//		} else {
//			UserType user = new UserType();
//			user.setEmail(res.getUser().getEmail());
//			user.setFirstname(res.getUser().getFirstname());
//			user.setLastname(res.getUser().getLastname());
//			user.setPasswd(res.getUser().getPasswd());
//			user.setUsername(res.getUser().getUsername());
//			for (int i=0; i<res.getUser().getSearchHistories().size(); i++) {
//				SearchHistoriesType sht = new SearchHistoriesType();
//				sht.setNum(res.getUser().getSearchHistories().get(i).getNum());
//				sht.setSearch(res.getUser().getSearchHistories().get(i).getSearch());
//				user.getSearchHistories().add(sht);
//			}
//			return user;
//		}
//
//	}


	public ProcessSummariesType ReadProcessSummaries(String searchExpression) throws ExceptionReadProcessSummaries {
		ReadProcessSummariesInputMsgType payload = new ReadProcessSummariesInputMsgType();
		payload.setSearchExpression(searchExpression);
		ReadProcessSummariesOutputMsgType res = manager.readProcessSummaries(payload);
		ResultType result = res.getResult();
		if (result.getCode() == -1) {
			throw new ExceptionReadProcessSummaries(result.getMessage()); 
		} else {
			ProcessSummariesType processesP = new ProcessSummariesType();
			ProcessSummariesType processesM = res.getProcessSummaries(); 
			for (int i=0; i<processesM.getProcessSummary().size();i++){
				ProcessSummaryType processM = processesM.getProcessSummary().get(i);
				ProcessSummaryType processP = new ProcessSummaryType();
				processesP.getProcessSummary().add(processP);

				processP.setDomain(processM.getDomain());
				processP.setId(processM.getId());
				processP.setLastVersion(processM.getLastVersion());
				processP.setName(processM.getName());
				processP.setOriginalNativeType(processM.getOriginalNativeType());
				processP.setRanking(processM.getRanking());
				processP.setOwner(processM.getOwner());

				for (int j=0;j<processM.getVersionSummaries().size();j++){
					VersionSummaryType versionM = processM.getVersionSummaries().get(j);
					VersionSummaryType versionP = new VersionSummaryType();
					processP.getVersionSummaries().add(versionP);
					versionP.setCreationDate(versionM.getCreationDate());
					versionP.setLastUpdate(versionM.getLastUpdate());
					versionP.setName(versionM.getName());
					versionP.setRanking(versionM.getRanking());
					for (int k=0;k<versionM.getAnnotations().size();k++){
						AnnotationsType annotations = new AnnotationsType();
						annotations.setNativeType(versionM.getAnnotations().get(k).getNativeType());
						annotations.getAnnotationName().addAll(versionM.getAnnotations().get(k).getAnnotationName());
						versionP.getAnnotations().add(annotations);
					}
				}
			}
			return processesP;
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







