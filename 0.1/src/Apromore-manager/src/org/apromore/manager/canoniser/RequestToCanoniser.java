package org.apromore.manager.canoniser;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Iterator;

import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.mail.util.ByteArrayDataSource;
import javax.xml.namespace.QName;

import org.apromore.manager.exception.ExceptionCanoniseVersion;
import org.apromore.manager.exception.ExceptionDeCanonise;
import org.apromore.manager.exception.ExceptionImport;
import org.apromore.manager.exception.ExceptionVersion;
import org.apromore.manager.model_canoniser.CanoniseProcessInputMsgType;
import org.apromore.manager.model_canoniser.CanoniseVersionInputMsgType;
import org.apromore.manager.model_canoniser.CanoniseVersionOutputMsgType;
import org.apromore.manager.model_canoniser.DeCanoniseProcessInputMsgType;
import org.apromore.manager.model_portal.VersionSummaryType;

public class RequestToCanoniser {

	private static final QName SERVICE_NAME = new QName("http://www.apromore.org/canoniser/service_manager", "CanoniserManagerService");
	private CanoniserManagerPortType port;

	public RequestToCanoniser() {

		URL wsdlURL = CanoniserManagerService.WSDL_LOCATION;

		CanoniserManagerService ss = new CanoniserManagerService(wsdlURL, SERVICE_NAME);
		this.port = ss.getCanoniserManager();  
	}

	/**
	 * 
	 * @param username
	 * @param processName
	 * @param versionName
	 * @param nativeType
	 * @param cpf
	 * @param domain
	 * @param documentation
	 * @param created
	 * @param lastupdate
	 * @return
	 * @throws IOException
	 * @throws ExceptionImport
	 */
	public org.apromore.manager.model_portal.ProcessSummaryType 
	CanoniseProcess(String username, String processName, String versionName, 
			String nativeType, InputStream cpf, String domain, String documentation, String created, String lastupdate) 
	throws IOException, ExceptionImport {
		org.apromore.manager.model_canoniser.CanoniseProcessInputMsgType payload = new CanoniseProcessInputMsgType();
		DataSource source = new ByteArrayDataSource(cpf, "text/xml"); 
		payload.setUsername(username);
		payload.setNativeType(nativeType);
		payload.setProcessName(processName);
		payload.setVersionName(versionName);
		payload.setDomain(domain);
		payload.setDocumentation(documentation);
		payload.setCreationDate(created);
		payload.setLastUpdate(lastupdate);
		payload.setProcessDescription(new DataHandler(source));
		org.apromore.manager.model_canoniser.CanoniseProcessOutputMsgType res = this.port.canoniseProcess(payload);
		if (res.getResult().getCode() == -1) {
			throw new ExceptionImport (res.getResult().getMessage());
		} else {
			org.apromore.manager.model_portal.ProcessSummaryType processP =
				new org.apromore.manager.model_portal.ProcessSummaryType();
			
			processP.setDomain(res.getProcessSummary().getDomain());
			processP.setId(res.getProcessSummary().getId());
			processP.setLastVersion(res.getProcessSummary().getLastVersion());
			processP.setName(res.getProcessSummary().getName());
			processP.setOriginalNativeType(res.getProcessSummary().getOriginalNativeType());
			processP.setRanking(res.getProcessSummary().getRanking());
			processP.getVersionSummaries().clear();
			processP.setOwner(res.getProcessSummary().getOwner());
			Iterator it = res.getProcessSummary().getVersionSummaries().iterator();
			// normally, only one... consider many for future needs
			while (it.hasNext()) {
				org.apromore.manager.model_portal.VersionSummaryType first_versionP =
					new VersionSummaryType();
				org.apromore.manager.model_canoniser.VersionSummaryType versionC = 
					(org.apromore.manager.model_canoniser.VersionSummaryType) it.next();
				first_versionP.setCreationDate(versionC.getCreationDate());
				first_versionP.setLastUpdate(versionC.getLastUpdate());
				first_versionP.setName(versionC.getName());
				first_versionP.setRanking(versionC.getRanking());
				first_versionP.setDocumentation(versionC.getDocumentation());
				first_versionP.setCreationDate(versionC.getCreationDate());
				first_versionP.setLastUpdate(versionC.getLastUpdate());
				first_versionP.getAnnotations().addAll(versionC.getAnnotations());
				processP.getVersionSummaries().add(first_versionP);
			}
			return processP;
			
		}
	}

	public InputStream 
	DeCanonise(int processId, String version, String nativeType, InputStream cpf_is, InputStream anf_is) 
	throws IOException, ExceptionDeCanonise {
		org.apromore.manager.model_canoniser.DeCanoniseProcessInputMsgType payload = new DeCanoniseProcessInputMsgType();
		DataSource source_cpf = new ByteArrayDataSource(cpf_is, "text/xml");
		payload.setProcessId(processId);
		payload.setVersion(version);
		payload.setNativeType(nativeType);
		payload.setCpf(new DataHandler(source_cpf));
		if (anf_is != null) {
			// given annotation must be used
			DataSource source_anf = new ByteArrayDataSource(anf_is, "text/xml");
			payload.setAnf(new DataHandler(source_anf));
		}
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

	public void 
	CanoniseVersion(Integer editSessionCode, Integer processId, String preVersion, String nativeType,
			InputStream native_is) 
	throws IOException, ExceptionCanoniseVersion, ExceptionVersion {
		CanoniseVersionInputMsgType payload = new CanoniseVersionInputMsgType();
		DataSource source = new ByteArrayDataSource(native_is, "text/xml");
		payload.setEditSessionCode(editSessionCode);
		payload.setNative(new DataHandler(source));
		payload.setNativeType(nativeType);
		payload.setProcessId(processId);
		payload.setPreVersion(preVersion);
		// send request to canoniser
		CanoniseVersionOutputMsgType res = this.port.canoniseVersion(payload);
		if (res.getResult().getCode() == -1) {
			throw new ExceptionCanoniseVersion (res.getResult().getMessage());
		} else if (res.getResult().getCode() == -3) {
			throw new ExceptionVersion(res.getResult().getMessage());
		}
	}

}
