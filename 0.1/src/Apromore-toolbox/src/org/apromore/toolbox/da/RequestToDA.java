package org.apromore.toolbox.da;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.mail.util.ByteArrayDataSource;
import javax.xml.namespace.QName;

import org.apromore.toolbox.common.Constants;
import org.apromore.toolbox.exception.ExceptionReadCanonicals;
import org.apromore.toolbox.exception.ExceptionReadProcessSummaries;
import org.apromore.toolbox.exception.ExceptionStoreCpf;
import org.apromore.toolbox.model_da.CanonicalType;
import org.apromore.toolbox.model_da.ProcessSummariesType;
import org.apromore.toolbox.model_da.ProcessSummaryType;
import org.apromore.toolbox.model_da.ProcessVersionsType;
import org.apromore.toolbox.model_da.ReadCanonicalsInputMsgType;
import org.apromore.toolbox.model_da.ReadProcessSummariesInputMsgType;
import org.apromore.toolbox.model_da.ReadProcessSummariesOutputMsgType;
import org.apromore.toolbox.model_da.ResultType;
import org.apromore.toolbox.model_da.StoreCpfInputMsgType;

public class RequestToDA {
	private static final QName SERVICE_NAME = new QName(Constants.DA_TOOLBOX_URI, Constants.DA_TOOLBOX_SERVICE);
	private DAToolboxPortType port;

	public RequestToDA() {
		URL wsdlURL = DAToolboxService.WSDL_LOCATION;
		DAToolboxService ss = new DAToolboxService(wsdlURL, SERVICE_NAME);
		this.port = ss.getDAToolbox();
	}

	public List<CanonicalType> ReadCanonicals(ProcessVersionsType ids) throws ExceptionReadCanonicals {
		org.apromore.toolbox.model_da.ReadCanonicalsInputMsgType payload =
			new ReadCanonicalsInputMsgType();
		payload.getProcessVersion().addAll(ids.getProcessVersion());
		org.apromore.toolbox.model_da.ReadCanonicalsOutputMsgType res =
			this.port.readCanonicals(payload);
		ResultType result = res.getResult();
		List<CanonicalType> canonicals = new ArrayList<CanonicalType>();
		if (result.getCode()==-1) {
			throw new ExceptionReadCanonicals (result.getMessage());
		} else {
			org.apromore.toolbox.model_da.CanonicalsType canonicalsDA = res.getCanonicals();
			canonicals = canonicalsDA.getCanonical();		}
		return canonicals;
	}
	
	public ProcessSummariesType ReadProcessSummaries (ProcessVersionsType processes) 
	throws ExceptionReadProcessSummaries {
		org.apromore.toolbox.model_da.ReadProcessSummariesInputMsgType payload =
			new ReadProcessSummariesInputMsgType();
		payload.setProcessVersions(processes);
		ProcessSummariesType toReturn = null;
		ReadProcessSummariesOutputMsgType res = this.port.readProcessSummaries(payload);
		ResultType result = res.getResult();
		if (result.getCode()==-1) {
			throw new ExceptionReadProcessSummaries (result.getMessage());
		} else {
			toReturn = res.getProcessSummaries();
		}
		return toReturn;
	}
	
	public ProcessSummaryType StoreCpf (String processName, String versionName, 
			String domain, String userName,
			InputStream cpf_is) throws IOException, ExceptionStoreCpf {
		ProcessSummaryType process = null;
		org.apromore.toolbox.model_da.StoreCpfInputMsgType payload =
			new StoreCpfInputMsgType();
		payload.setProcessName(processName);
		payload.setUsername(userName);
		payload.setVersion(versionName);
		payload.setDomain(domain);
		DataSource sourceCpf = new ByteArrayDataSource(cpf_is, "text/xml"); 
		payload.setCpf(new DataHandler(sourceCpf));	
		org.apromore.toolbox.model_da.StoreCpfOutputMsgType res =
			this.port.storeCpf(payload);
		if (res.getResult().getCode()==-1) {
			throw new ExceptionStoreCpf(res.getResult().getMessage());
		} else {
			process = res.getProcessSummary();
		}
		return process;
		
	}

}
