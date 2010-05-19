package org.apromore.canoniser.da;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.mail.util.ByteArrayDataSource;
import javax.xml.namespace.QName;

import org.apromore.canoniser.exception.ExceptionStore;
import org.apromore.canoniser.model_da.StoreNativeCpfInputMsgType;
import org.apromore.canoniser.model_da.StoreNativeCpfOutputMsgType;
import org.apromore.canoniser.model_da.StoreNativeInputMsgType;
import org.apromore.canoniser.model_da.StoreNativeOutputMsgType;
import org.apromore.canoniser.model_da.StoreVersionInputMsgType;
import org.apromore.canoniser.model_da.StoreVersionOutputMsgType;

public class RequestToDA {

	private static final QName SERVICE_NAME = new QName("http://www.apromore.org/data_access/service_canoniser", "DACanoniserService");
	private DACanoniserPortType port;

	public RequestToDA() {
		URL wsdlURL = DACanoniserService.WSDL_LOCATION;
		DACanoniserService ss = new DACanoniserService(wsdlURL, SERVICE_NAME);
		this.port = ss.getDACanoniser(); 
	}

	public void StoreProcess (String username, String processName, String domain, String nativeType, String versionName,
			InputStream process_xml, InputStream cpf_xml, InputStream anf_xml) throws IOException, ExceptionStore {

		StoreNativeCpfInputMsgType payload = new StoreNativeCpfInputMsgType();
		payload.setUsername(username);
		payload.setNativeType(nativeType);
		payload.setProcessName(processName);
		payload.setDomain(domain);
		payload.setVersionName(versionName);
		DataSource source_proc = new ByteArrayDataSource(process_xml, "text/xml"); 
		payload.setNative(new DataHandler(source_proc));
		DataSource source_cpf = new ByteArrayDataSource(cpf_xml, "text/xml"); 
		payload.setCpf(new DataHandler(source_cpf));
		DataSource source_anf = new ByteArrayDataSource(anf_xml, "text/xml"); 
		payload.setAnf(new DataHandler(source_anf));
		StoreNativeCpfOutputMsgType res = this.port.storeNativeCpf(payload);
		if (res.getResult().getCode() == -1) {
			throw new ExceptionStore (res.getResult().getMessage());
		}
	}
	
	public void StoreNative (int processId, String version, String nativeType, InputStream process) 
	throws IOException, ExceptionStore {
		
		StoreNativeInputMsgType payload = new StoreNativeInputMsgType();
		payload.setProcessId(processId);
		payload.setVersion(version);
		payload.setNativeType(nativeType);
		DataSource source_native = new ByteArrayDataSource(process, "text/xml");
		payload.setNative(new DataHandler(source_native));
		StoreNativeOutputMsgType res = this.port.storeNative(payload);
		if (res.getResult().getCode() == -1) {
			throw new ExceptionStore (res.getResult().getMessage());
		}
		
	}

	public void StoreVersion(int processId, String preVersion,
			String newVersion, String nativeType, String domain,
			String username, InputStream native_is, InputStream anf_xml_is,
			InputStream cpf_xml_is) throws IOException, ExceptionStore {
		
		StoreVersionInputMsgType payload = new StoreVersionInputMsgType();
		payload.setDomain(domain);
		payload.setNativeType(nativeType);
		payload.setNewVersion(newVersion);
		payload.setPreVersion(preVersion);
		payload.setProcessId(processId);

		DataSource source_proc = new ByteArrayDataSource(native_is, "text/xml"); 
		payload.setNative(new DataHandler(source_proc));
		DataSource source_cpf = new ByteArrayDataSource(cpf_xml_is, "text/xml"); 
		payload.setCpf(new DataHandler(source_cpf));
		DataSource source_anf = new ByteArrayDataSource(anf_xml_is, "text/xml"); 
		payload.setAnf(new DataHandler(source_anf));
		StoreVersionOutputMsgType res = this.port.storeVersion(payload);
		if (res.getResult().getCode() == -1) {
			throw new ExceptionStore (res.getResult().getMessage());
		}		
	}
}
