package org.apromore.manager.canoniser;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.mail.util.ByteArrayDataSource;
import javax.xml.namespace.QName;

import org.apromore.manager.commons.Constants;
import org.apromore.manager.da.DAManagerService;
import org.apromore.manager.exception.ExceptionImport;
import org.apromore.manager.model_canoniser.CanoniseProcessInputMsgType;

public class RequestToCanoniser {

	private static final QName SERVICE_NAME = new QName("http://www.apromore.org/canoniser/service_manager", "CanoniserManagerService");
	private CanoniserManagerPortType port;

	public RequestToCanoniser() {

        URL wsdlURL = CanoniserManagerService.WSDL_LOCATION;

        CanoniserManagerService ss = new CanoniserManagerService(wsdlURL, SERVICE_NAME);
        this.port = ss.getCanoniserManager();  
	}

	public void ImportProcess(String username, String processName, String nativeType, InputStream process) 
	throws IOException, ExceptionImport {
		org.apromore.manager.model_canoniser.CanoniseProcessInputMsgType payload = new CanoniseProcessInputMsgType();
		DataSource source = new ByteArrayDataSource(process, "text/xml"); 
		payload.setUsername(username);
		payload.setNativeType(nativeType);
		payload.setProcessName(processName);
		payload.setProcessDescription(new DataHandler(source));
		org.apromore.manager.model_canoniser.CanoniseProcessOutputMsgType res = this.port.canoniseProcess(payload);
		if (res.getResult().getCode() == -1) {
			throw new ExceptionImport (res.getResult().getMessage());
		} 		
	}

}
