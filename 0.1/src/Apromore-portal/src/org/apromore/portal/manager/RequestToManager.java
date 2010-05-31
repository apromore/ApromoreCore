package org.apromore.portal.manager;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.mail.util.ByteArrayDataSource;
import javax.xml.namespace.QName;

import org.apromore.portal.exception.ExceptionDeleteEditSession;
import org.apromore.portal.exception.ExceptionDeleteProcess;
import org.apromore.portal.exception.ExceptionDomains;
import org.apromore.portal.exception.ExceptionExport;
import org.apromore.portal.exception.ExceptionFormats;
import org.apromore.portal.exception.ExceptionImport;
import org.apromore.portal.exception.ExceptionProcess;
import org.apromore.portal.exception.ExceptionReadEditSession;
import org.apromore.portal.exception.ExceptionUpdateProcess;
import org.apromore.portal.exception.ExceptionUser;
import org.apromore.portal.exception.ExceptionWriteEditSession;
import org.apromore.portal.model_manager.DeleteEditSessionInputMsgType;
import org.apromore.portal.model_manager.DeleteEditSessionOutputMsgType;
import org.apromore.portal.model_manager.DeleteProcessVersionsInputMsgType;
import org.apromore.portal.model_manager.DeleteProcessVersionsOutputMsgType;
import org.apromore.portal.model_manager.DomainsType;
import org.apromore.portal.model_manager.EditSessionType;
import org.apromore.portal.model_manager.ExportNativeInputMsgType;
import org.apromore.portal.model_manager.ExportNativeOutputMsgType;
import org.apromore.portal.model_manager.FormatType;
import org.apromore.portal.model_manager.FormatsType;
import org.apromore.portal.model_manager.ImportProcessInputMsgType;
import org.apromore.portal.model_manager.ImportProcessOutputMsgType;
import org.apromore.portal.model_manager.ProcessSummariesType;
import org.apromore.portal.model_manager.ProcessSummaryType;
import org.apromore.portal.model_manager.ProcessVersionIdentifierType;
import org.apromore.portal.model_manager.ReadDomainsInputMsgType;
import org.apromore.portal.model_manager.ReadDomainsOutputMsgType;
import org.apromore.portal.model_manager.ReadEditSessionInputMsgType;
import org.apromore.portal.model_manager.ReadEditSessionOutputMsgType;
import org.apromore.portal.model_manager.ReadFormatsInputMsgType;
import org.apromore.portal.model_manager.ReadFormatsOutputMsgType;
import org.apromore.portal.model_manager.ReadProcessSummariesInputMsgType;
import org.apromore.portal.model_manager.ReadProcessSummariesOutputMsgType;
import org.apromore.portal.model_manager.ReadUserInputMsgType;
import org.apromore.portal.model_manager.ReadUserOutputMsgType;
import org.apromore.portal.model_manager.ResultType;
import org.apromore.portal.model_manager.UpdateProcessInputMsgType;
import org.apromore.portal.model_manager.UpdateProcessOutputMsgType;
import org.apromore.portal.model_manager.UserType;
import org.apromore.portal.model_manager.VersionSummaryType;
import org.apromore.portal.model_manager.WriteEditSessionInputMsgType;
import org.apromore.portal.model_manager.WriteEditSessionOutputMsgType;
import org.apromore.portal.model_manager.WriteUserInputMsgType;
import org.apromore.portal.model_manager.WriteUserOutputMsgType;


public class RequestToManager {
	private static final QName SERVICE_NAME = new QName("http://www.apromore.org/manager/service_portal", "ManagerPortalService");

	private ManagerPortalPortType port;
	
	public RequestToManager() {
        URL wsdlURL = ManagerPortalService.WSDL_LOCATION;
        ManagerPortalService ss = new ManagerPortalService(wsdlURL, SERVICE_NAME);
        this.port = ss.getManagerPortal();  
	}
	
	public DomainsType ReadDomains() throws ExceptionDomains {
		// payload empty
		ReadDomainsInputMsgType payload = new ReadDomainsInputMsgType();
		ReadDomainsOutputMsgType res = this.port.readDomains(payload);

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
	
	public FormatsType ReadFormats() throws ExceptionFormats {
		// payload empty
		ReadFormatsInputMsgType payload = new ReadFormatsInputMsgType();
		ReadFormatsOutputMsgType res = this.port.readFormats(payload);

		ResultType result = res.getResult();
		if (result.getCode() == -1) {
			throw new ExceptionFormats (result.getMessage()); 
		} else {
			List<FormatType> formats = res.getFormats().getFormat();
			FormatsType resFormats = new FormatsType();
			resFormats.getFormat().addAll(formats);
			return resFormats;
		}
	}
	
	public UserType ReadUser(String username) throws ExceptionUser{
		ReadUserInputMsgType payload = new ReadUserInputMsgType();
		payload.setUsername(username);
		ReadUserOutputMsgType res = this.port.readUser(payload);
		ResultType result = res.getResult();
		if (result.getCode() == -1) {
			throw new ExceptionUser (result.getMessage()); 
		} else {
			return res.getUser();
		}
	}
	
	public void WriteUser(UserType user) throws ExceptionUser{
		WriteUserInputMsgType payload = new WriteUserInputMsgType();
		payload.setUser(user);
		WriteUserOutputMsgType res = this.port.writeUser(payload);
		ResultType result = res.getResult();
		if (result.getCode() == -1) {
			throw new ExceptionUser (result.getMessage()); 
		}
	}
	
	public ProcessSummariesType ReadProcessSummariesType (String searchExpr) throws ExceptionProcess {
		ReadProcessSummariesInputMsgType payload = new ReadProcessSummariesInputMsgType();
		payload.setSearchExpression(searchExpr);
		ReadProcessSummariesOutputMsgType res = this.port.readProcessSummaries(payload);

		ResultType result = res.getResult();
		if (result.getCode() == -1) {
			throw new ExceptionProcess (result.getMessage()); 
		} else {
			return res.getProcessSummaries();
		}
	}

	public ProcessSummaryType ImportModel(String username, String nativeType, String processName, 
			String versionName, InputStream process, String domain) 
	throws IOException, ExceptionImport {
		
		ImportProcessInputMsgType payload = new ImportProcessInputMsgType();
		DataSource source = new ByteArrayDataSource(process, "text/xml"); 
		payload.setUsername(username);
		payload.setNativeType(nativeType);
		payload.setProcessName(processName);
		payload.setVersionName(versionName);
		payload.setProcessDescription(new DataHandler(source));
		payload.setDomain(domain);
		ImportProcessOutputMsgType res = this.port.importProcess(payload);
		ResultType result = res.getResult();
		if (result.getCode() == -1) {
			throw new ExceptionImport (result.getMessage()); 
		} else {
			return res.getProcessSummary();
		}
		
	}

	public InputStream ExportNative(int processId, String versionName, String nativeType) 
	throws ExceptionExport, IOException {
		
		ExportNativeInputMsgType payload = new ExportNativeInputMsgType();
		payload.setProcessId(processId);
		payload.setVersionName(versionName);
		payload.setNativeType(nativeType);
		ExportNativeOutputMsgType res = this.port.exportNative(payload);
		ResultType result = res.getResult();
		
		if (result.getCode() == -1) {
			throw new ExceptionExport (result.getMessage()); 
		} else {
			DataHandler handler = res.getNative();
			InputStream is = handler.getInputStream();
			return is;
		}
	}
	
	public int WriteEditSession (EditSessionType editSession) throws ExceptionWriteEditSession {
		WriteEditSessionInputMsgType payload = new WriteEditSessionInputMsgType();
		payload.setEditSession(editSession);
		WriteEditSessionOutputMsgType res = this.port.writeEditSession(payload);
		ResultType result = res.getResult();
		if (result.getCode() == -1) {
			throw new ExceptionWriteEditSession (result.getMessage()); 
		} else {
			return res.getEditSessionCode();
		}
	}
	
	public void DeleteEditionSession (int code) throws ExceptionDeleteEditSession {
		DeleteEditSessionInputMsgType payload = new DeleteEditSessionInputMsgType();
		payload.setEditSessionCode(code);
		DeleteEditSessionOutputMsgType res = this.port.deleteEditSession(payload);
		ResultType result = res.getResult();
		if (result.getCode() == -1) {
			throw new ExceptionDeleteEditSession (result.getMessage()); 
		}
	}
	
	public EditSessionType ReadEditSession (int code) throws ExceptionReadEditSession {
		ReadEditSessionInputMsgType payload = new ReadEditSessionInputMsgType();
		payload.setEditSessionCode(code);
		ReadEditSessionOutputMsgType res = this.port.readEditSession(payload);
		ResultType result = res.getResult();
		if (result.getCode() == -1) {
			throw new ExceptionReadEditSession (result.getMessage()); 
		} else {
			return res.getEditSession();
		}
	}

	public void UpdateProcess(String username, String nativeType,
			int processId, String versionName, String new_versionName,
			InputStream native_is, String domain) throws IOException, ExceptionUpdateProcess {
		
		UpdateProcessInputMsgType payload = new UpdateProcessInputMsgType();
		payload.setDomain(domain);
		payload.setNativeType(nativeType);
		payload.setNewVersion(new_versionName);
		payload.setPreVersion(versionName);
		payload.setProcessId(processId);
		payload.setUsername(username);
		DataSource sourceNat = new ByteArrayDataSource(native_is, "text/xml"); 
		payload.setNative(new DataHandler(sourceNat));
		UpdateProcessOutputMsgType res = this.port.updateProcess(payload);
		ResultType result = res.getResult();
		if (result.getCode() == -1) {
			throw new ExceptionUpdateProcess (result.getMessage()); 
		}
		
	}

	public void DeleteProcessVersions(
			HashMap<ProcessSummaryType, List<VersionSummaryType>> processVersions) throws ExceptionDeleteProcess {
		
		DeleteProcessVersionsInputMsgType payload = new DeleteProcessVersionsInputMsgType();
		Set<ProcessSummaryType> keys = processVersions.keySet();
		Iterator<ProcessSummaryType> it = keys.iterator();
		while (it.hasNext()){
			ProcessSummaryType processSummary = it.next();
			List<VersionSummaryType> versionSummaries = processVersions.get(processSummary);
			
			ProcessVersionIdentifierType processVersionId = new ProcessVersionIdentifierType();
			payload.getProcessVersionIdentifier().add(processVersionId);			
			processVersionId.setProcessid(processSummary.getId());
			for (int i=0;i<versionSummaries.size();i++){
				processVersionId.getVersionName().add(versionSummaries.get(i).getName());
			}
		}
		DeleteProcessVersionsOutputMsgType res = this.port.deleteProcessVersions(payload);
		ResultType result = res.getResult();
		if (result.getCode() == -1) {
			throw new ExceptionDeleteProcess (result.getMessage()); 
		}
	}
	
}
