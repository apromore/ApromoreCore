package org.apromore.manager.canoniser;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.mail.util.ByteArrayDataSource;
import javax.xml.namespace.QName;

import org.apromore.manager.exception.ExceptionCanoniseVersion;
import org.apromore.manager.exception.ExceptionImport;
import org.apromore.manager.model_canoniser.CanoniseProcessInputMsgType;
import org.apromore.manager.model_canoniser.CanoniseVersionInputMsgType;
import org.apromore.manager.model_canoniser.CanoniseVersionOutputMsgType;
import org.apromore.manager.model_canoniser.DeCanoniseProcessInputMsgType;

public class RequestToCanoniser {

	private static final QName SERVICE_NAME = new QName("http://www.apromore.org/canoniser/service_manager", "CanoniserManagerService");
	private CanoniserManagerPortType port;

	public RequestToCanoniser() {

		URL wsdlURL = CanoniserManagerService.WSDL_LOCATION;

		CanoniserManagerService ss = new CanoniserManagerService(wsdlURL, SERVICE_NAME);
		this.port = ss.getCanoniserManager();  
	}

	public void ImportProcess(String username, String processName, String versionName, 
			String nativeType, InputStream process, String domain) 
	throws IOException, ExceptionImport {
		org.apromore.manager.model_canoniser.CanoniseProcessInputMsgType payload = new CanoniseProcessInputMsgType();
		DataSource source = new ByteArrayDataSource(process, "text/xml"); 
		payload.setUsername(username);
		payload.setNativeType(nativeType);
		payload.setProcessName(processName);
		payload.setVersionName(versionName);
		payload.setDomain(domain);
		payload.setProcessDescription(new DataHandler(source));
		org.apromore.manager.model_canoniser.CanoniseProcessOutputMsgType res = this.port.canoniseProcess(payload);
		if (res.getResult().getCode() == -1) {
			throw new ExceptionImport (res.getResult().getMessage());
		} 		
	}

	public InputStream DeCanonise(int processId, String version, String nativeType, InputStream canonical_xml) 
	throws IOException, ExceptionDeCanonise {
		org.apromore.manager.model_canoniser.DeCanoniseProcessInputMsgType payload = new DeCanoniseProcessInputMsgType();
		DataSource source = new ByteArrayDataSource(canonical_xml, "text/xml");
		payload.setProcessId(processId);
		payload.setVersion(version);
		payload.setNativeType(nativeType);
		payload.setCpf(new DataHandler(source));
		org.apromore.manager.model_canoniser.DeCanoniseProcessOutputMsgType res =
			this.port.deCanoniseProcess(payload);
		if (res.getResult().getCode() == -1) {
			throw new ExceptionDeCanonise (res.getResult().getMessage());
		} else {
			DataHandler handler = res.getNativeDescription();
			InputStream is = handler.getInputStream();
			return is;
		}
	}

	public void CanoniseVersion(Integer processId, String preVersion,
			String newVersion, String nativeType, String domain, String username,
			InputStream native_is) throws IOException, ExceptionCanoniseVersion {
		CanoniseVersionInputMsgType payload = new CanoniseVersionInputMsgType();
		DataSource source = new ByteArrayDataSource(native_is, "text/xml");
		payload.setDomain(domain);
		payload.setNative(new DataHandler(source));
		payload.setNativeType(nativeType);
		payload.setNewVersion(newVersion);
		payload.setPreVersion(preVersion);
		payload.setProcessId(processId);
		payload.setUsername(username);
		CanoniseVersionOutputMsgType res = this.port.canoniseVersion(payload);
		if (res.getResult().getCode() == -1) {
			throw new ExceptionCanoniseVersion (res.getResult().getMessage());
		} 
	}

}
