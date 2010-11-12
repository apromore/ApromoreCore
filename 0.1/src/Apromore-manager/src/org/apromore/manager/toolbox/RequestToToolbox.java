package org.apromore.manager.toolbox;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import javax.xml.namespace.QName;

import org.apromore.manager.commons.Constants;
import org.apromore.manager.exception.ExceptionReadAllCanonicals;
import org.apromore.manager.model_toolbox.CanonicalType;
import org.apromore.manager.model_toolbox.ResultType;
import org.apromore.manager.model_toolbox.SearchForSimilarProcessesInputMsgType;

public class RequestToToolbox {
	private static final QName SERVICE_NAME = new QName(Constants.TOOLBOX_MANAGER_URI, Constants.TOOLBOX_MANAGER_SERVICE);
	private ToolboxManagerPortType port;

	private RequestToToolbox() {
		URL wsdlURL = ToolboxManagerService.WSDL_LOCATION;
		ToolboxManagerService ss = new ToolboxManagerService(wsdlURL, SERVICE_NAME);
		this.port = ss.getToolboxManager();
	}

	public List<CanonicalType> ReadAllCanonicals() throws ExceptionReadAllCanonicals {
		org.apromore.manager.model_toolbox.SearchForSimilarProcessesInputMsgType payload =
			new SearchForSimilarProcessesInputMsgType();
		org.apromore.manager.model_toolbox.SearchForSimilarProcessesOutputMsgType res =
			this.port.searchForSimilarProcesses(payload);
		ResultType result = res.getResult();
		List<CanonicalType> canonicals = new ArrayList<CanonicalType>();
		if (result.getCode()==-1) {
			throw new ExceptionReadAllCanonicals (result.getMessage());
		} else {
			org.apromore.manager.model_toolbox.CanonicalsType allCanonicalsDA = res.getCanonicals();
			canonicals = allCanonicalsDA.getCanonicalType();    	
			return canonicals;
		}
	}
}