package org.apromore.canoniser.da;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.mail.util.ByteArrayDataSource;
import javax.xml.namespace.QName;

import org.apromore.anf.AnnotationsType;
import org.apromore.canoniser.exception.ExceptionStore;
import org.apromore.canoniser.model_da.StoreNativeCpfInputMsgType;
import org.apromore.canoniser.model_da.StoreNativeCpfOutputMsgType;
import org.apromore.cpf.CanonicalProcessType;

public class RequestToDA {

	private static final QName SERVICE_NAME = new QName("http://www.apromore.org/data_access/service_canoniser", "DACanoniserService");
	private DACanoniserPortType port;

	public RequestToDA() {
		URL wsdlURL = DACanoniserService.WSDL_LOCATION;
		DACanoniserService ss = new DACanoniserService(wsdlURL, SERVICE_NAME);
		this.port = ss.getDACanoniser(); 
	}

	public void StoreProcess(String username, String processName, String domain, String nativeType, String versionName,
			InputStream process_xml, CanonicalProcessType cpf, AnnotationsType anf) throws IOException, ExceptionStore {

		StoreNativeCpfInputMsgType payload = new StoreNativeCpfInputMsgType();
		payload.setUsername(username);
		payload.setAnf(anf);
		payload.setCpf(cpf);
		payload.setNativeType(nativeType);
		payload.setProcessName(processName);
		payload.setDomain(domain);
		payload.setVersionName(versionName);
		
		DataSource source = new ByteArrayDataSource(process_xml, "text/xml"); 
		payload.setNative(new DataHandler(source));
		StoreNativeCpfOutputMsgType res = this.port.storeNativeCpf(payload);
		if (res.getResult().getCode() == -1) {
			throw new ExceptionStore (res.getResult().getMessage());
		}
	}
}
