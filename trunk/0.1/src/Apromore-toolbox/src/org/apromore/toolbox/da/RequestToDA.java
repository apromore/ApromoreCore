package org.apromore.toolbox.da;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import javax.xml.namespace.QName;

import org.apromore.toolbox.common.Constants;
import org.apromore.toolbox.exception.ExceptionReadAllCanonicals;
import org.apromore.toolbox.model_da.CanonicalType;
import org.apromore.toolbox.model_da.ReadCanonicalsInputMsgType;
import org.apromore.toolbox.model_da.ResultType;

public class RequestToDA {
	private static final QName SERVICE_NAME = new QName(Constants.DA_TOOLBOX_URI, Constants.DA_TOOLBOX_SERVICE);
	private DAToolboxPortType port;

	public RequestToDA() {
		URL wsdlURL = DAToolboxService.WSDL_LOCATION;
		DAToolboxService ss = new DAToolboxService(wsdlURL, SERVICE_NAME);
		this.port = ss.getDAToolbox();
	}

	public List<CanonicalType> ReadAllCanonicals() throws ExceptionReadAllCanonicals {
		org.apromore.toolbox.model_da.ReadCanonicalsInputMsgType payload =
			new ReadCanonicalsInputMsgType();
		org.apromore.toolbox.model_da.ReadCanonicalsOutputMsgType res =
			this.port.readCanonicals(payload);
		ResultType result = res.getResult();
		List<CanonicalType> canonicals = new ArrayList<CanonicalType>();
		if (result.getCode()==-1) {
			throw new ExceptionReadAllCanonicals (result.getMessage());
		} else {
			org.apromore.toolbox.model_da.CanonicalsType allCanonicalsDA = res.getCanonicals();
			canonicals = allCanonicalsDA.getCanonical();		}
		return canonicals;
	}

	
}
