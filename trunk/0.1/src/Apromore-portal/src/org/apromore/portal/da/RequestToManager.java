package org.apromore.portal.da;

import java.io.InputStream;
import java.net.URL;
import java.util.List;

import javax.xml.namespace.QName;

import org.apromore.portal.exception.ExceptionDomains;
import org.apromore.portal.exception.ExceptionFormats;
import org.apromore.portal.exception.ExceptionProcess;
import org.apromore.portal.exception.ExceptionUser;
import org.apromore.portal.model_portal.DomainsType;
import org.apromore.portal.model_portal.FormatsType;
import org.apromore.portal.model_portal.ProcessSummariesType;
import org.apromore.portal.model_portal.ReadDomainsInputMsgType;
import org.apromore.portal.model_portal.ReadDomainsOutputMsgType;
import org.apromore.portal.model_portal.ReadFormatsInputMsgType;
import org.apromore.portal.model_portal.ReadFormatsOutputMsgType;
import org.apromore.portal.model_portal.ReadProcessSummariesInputMsgType;
import org.apromore.portal.model_portal.ReadProcessSummariesOutputMsgType;
import org.apromore.portal.model_portal.ReadUserInputMsgType;
import org.apromore.portal.model_portal.ReadUserOutputMsgType;
import org.apromore.portal.model_portal.ResultType;
import org.apromore.portal.model_portal.UserType;
import org.apromore.portal.model_portal.WriteUserInputMsgType;
import org.apromore.portal.model_portal.WriteUserOutputMsgType;

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

	public ResultType ImportModel(InputStream nativeProcessFormat,
			String string, UserType currentUser) {
		// TODO Auto-generated method stub
		return null;
	}
}
