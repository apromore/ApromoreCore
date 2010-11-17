package org.apromore.manager.toolbox;

import java.net.URL;

import javax.xml.namespace.QName;

import org.apromore.manager.commons.Constants;
import org.apromore.manager.exception.ExceptionSearchForSimilar;
import org.apromore.manager.model_portal.CanonicalType;
import org.apromore.manager.model_portal.CanonicalsType;
import org.apromore.manager.model_toolbox.ParameterType;
import org.apromore.manager.model_toolbox.ParametersType;
import org.apromore.manager.model_toolbox.ResultType;
import org.apromore.manager.model_toolbox.SearchForSimilarProcessesInputMsgType;

public class RequestToToolbox {

	private static final QName SERVICE_NAME = new QName(Constants.TOOLBOX_MANAGER_URI, Constants.TOOLBOX_MANAGER_SERVICE);
	private ToolboxManagerPortType port;

	public RequestToToolbox() {
		URL wsdlURL = ToolboxManagerService.WSDL_LOCATION;
		ToolboxManagerService ss = new ToolboxManagerService(wsdlURL, SERVICE_NAME);
		this.port = ss.getToolboxManager();
	}

	
	public org.apromore.manager.model_portal.CanonicalsType SearchForSimilarProcesses(
			int selectedModelId, 
			String method, 
			double modelthreshold, 
			double labelthreshold, 
			double contextthreshold, 
			double skipnweight, 
			double subnweight, 
			double skipeweight) throws ExceptionSearchForSimilar {
		org.apromore.manager.model_toolbox.SearchForSimilarProcessesInputMsgType payload =
			new SearchForSimilarProcessesInputMsgType();
		payload.setAlgorithm(method);
		payload.setProcessId(selectedModelId);
		org.apromore.manager.model_toolbox.ParametersType params = new ParametersType();
		// modelthreshold
		org.apromore.manager.model_toolbox.ParameterType p = new ParameterType();
		p.setName("modelthreshold");
		p.setValue(modelthreshold);
		params.getParameter().add(p);

		// labelthreshold
		p = new ParameterType();
		p.setName("labelthreshold");
		p.setValue(labelthreshold);
		params.getParameter().add(p);

		// contextthreshold
		p = new ParameterType();
		p.setName("contextthreshold");
		p.setValue(contextthreshold);
		params.getParameter().add(p);

		if ("Greedy".equals(method)) {
			// skipnweight
			p = new ParameterType();
			p.setName("skipnweight");
			p.setValue(skipnweight);
			params.getParameter().add(p);

			// subnweight
			p = new ParameterType();
			p.setName("subnweight");
			p.setValue(subnweight);
			params.getParameter().add(p);

			// skipeweight
			p = new ParameterType();
			p.setName("skipeweight");
			p.setValue(skipeweight);
			params.getParameter().add(p);
		}
		payload.setParameters(params);
		org.apromore.manager.model_toolbox.SearchForSimilarProcessesOutputMsgType res =
			this.port.searchForSimilarProcesses(payload);
		ResultType result = res.getResult();
		if (result.getCode()==-1) {
			throw new ExceptionSearchForSimilar (result.getMessage());
		} else {
			org.apromore.manager.model_portal.CanonicalsType canonicalsP = new CanonicalsType();
			for (org.apromore.manager.model_toolbox.CanonicalType canonicalT: res.getCanonicals().getCanonicalType()) {
				org.apromore.manager.model_portal.CanonicalType canonicalP = new CanonicalType();
				canonicalP.setCpf(canonicalT.getCpf());
				canonicalP.setProcessId(canonicalT.getProcessId());
				canonicalP.setVersionName(canonicalT.getVersionName());
			}
			return canonicalsP;
		}
	}
}