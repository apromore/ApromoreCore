package org.apromore.portal.manager;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.List;

import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.mail.util.ByteArrayDataSource;
import javax.xml.namespace.QName;

import org.apromore.portal.exception.ExceptionDomains;
import org.apromore.portal.exception.ExceptionFormats;
import org.apromore.portal.exception.ExceptionImport;
import org.apromore.portal.exception.ExceptionProcess;
import org.apromore.portal.exception.ExceptionUser;
import org.apromore.portal.model_manager.DomainsType;
import org.apromore.portal.model_manager.FormatsType;
import org.apromore.portal.model_manager.ImportProcessInputMsgType;
import org.apromore.portal.model_manager.ImportProcessOutputMsgType;
import org.apromore.portal.model_manager.ProcessSummariesType;
import org.apromore.portal.model_manager.ReadDomainsInputMsgType;
import org.apromore.portal.model_manager.ReadDomainsOutputMsgType;
import org.apromore.portal.model_manager.ReadFormatsInputMsgType;
import org.apromore.portal.model_manager.ReadFormatsOutputMsgType;
import org.apromore.portal.model_manager.ReadProcessSummariesInputMsgType;
import org.apromore.portal.model_manager.ReadProcessSummariesOutputMsgType;
import org.apromore.portal.model_manager.ReadUserInputMsgType;
import org.apromore.portal.model_manager.ReadUserOutputMsgType;
import org.apromore.portal.model_manager.ResultType;
import org.apromore.portal.model_manager.UserType;
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
			List<String> formats = res.getFormats().getFormat();
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

	public void ImportModel(String username, String nativeType, String processName, InputStream process) 
	throws IOException, ExceptionImport {
		
		ImportProcessInputMsgType payload = new ImportProcessInputMsgType();
		DataSource source = new ByteArrayDataSource(process, "text/xml"); 
		payload.setUsername(username);
		payload.setNativeType(nativeType);
		payload.setProcessName(processName);
		payload.setProcessDescription(new DataHandler(source));
		ImportProcessOutputMsgType res = this.port.importProcess(payload);
		ResultType result = res.getResult();
		if (result.getCode() == -1) {
			throw new ExceptionImport (result.getMessage()); 
		} 
		
	}
}
