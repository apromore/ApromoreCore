package org.apromore.canoniser.da;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Iterator;

import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.mail.util.ByteArrayDataSource;
import javax.xml.namespace.QName;

import org.apromore.canoniser.exception.ExceptionAnnotation;
import org.apromore.canoniser.exception.ExceptionCpfUri;
import org.apromore.canoniser.exception.ExceptionStore;
import org.apromore.canoniser.exception.ExceptionVersion;
import org.apromore.canoniser.model_da.EditSessionType;
import org.apromore.canoniser.model_da.GetCpfUriInputMsgType;
import org.apromore.canoniser.model_da.GetCpfUriOutputMsgType;
import org.apromore.canoniser.model_da.StoreNativeCpfInputMsgType;
import org.apromore.canoniser.model_da.StoreNativeCpfOutputMsgType;
import org.apromore.canoniser.model_da.StoreNativeInputMsgType;
import org.apromore.canoniser.model_da.StoreNativeOutputMsgType;
import org.apromore.canoniser.model_da.StoreVersionInputMsgType;
import org.apromore.canoniser.model_da.StoreVersionOutputMsgType;
import org.apromore.canoniser.model_da.WriteAnnotationInputMsgType;
import org.apromore.canoniser.model_da.WriteAnnotationOutputMsgType;
import org.apromore.canoniser.model_manager.AnnotationsType;
import org.apromore.canoniser.model_manager.ProcessSummaryType;
import org.apromore.canoniser.model_manager.VersionSummaryType;

public class RequestToDA {

	private static final QName SERVICE_NAME = new QName("http://www.apromore.org/data_access/service_canoniser", "DACanoniserService");
	private DACanoniserPortType port;

	public RequestToDA() {
		URL wsdlURL = DACanoniserService.WSDL_LOCATION;
		DACanoniserService ss = new DACanoniserService(wsdlURL, SERVICE_NAME);
		this.port = ss.getDACanoniser(); 
	}

	public org.apromore.canoniser.model_manager.ProcessSummaryType 
	storeNativeCpf (String username, String processName, String cpfURI,
			String domain, String nativeType, String versionName, String documentation, String created, String lastupdate,
			InputStream process_xml, InputStream cpf_xml, InputStream anf_xml) throws IOException, ExceptionStore {

		org.apromore.canoniser.model_manager.ProcessSummaryType processM =
			new ProcessSummaryType();
		StoreNativeCpfInputMsgType payload = new StoreNativeCpfInputMsgType();
		payload.setCpfURI(cpfURI);
		EditSessionType editSession = new EditSessionType();
		editSession.setUsername(username);
		editSession.setNativeType(nativeType);
		editSession.setProcessName(processName);
		editSession.setDomain(domain);
		editSession.setVersionName(versionName);
		editSession.setCreationDate(created);
		editSession.setLastUpdate(lastupdate);
		payload.setEditSession(editSession);
		DataSource source_proc = new ByteArrayDataSource(process_xml, "text/xml"); 
		payload.setNative(new DataHandler(source_proc));
		DataSource source_cpf = new ByteArrayDataSource(cpf_xml, "text/xml"); 
		payload.setCpf(new DataHandler(source_cpf));
		DataSource source_anf = new ByteArrayDataSource(anf_xml, "text/xml"); 
		payload.setAnf(new DataHandler(source_anf));
		StoreNativeCpfOutputMsgType res = this.port.storeNativeCpf(payload);
		if (res.getResult().getCode() == -1) {
			throw new ExceptionStore (res.getResult().getMessage());
		} else {
			processM.setDomain(res.getProcessSummary().getDomain());
			processM.setId(res.getProcessSummary().getId());
			processM.setLastVersion(res.getProcessSummary().getLastVersion());
			processM.setName(res.getProcessSummary().getName());
			processM.setOriginalNativeType(res.getProcessSummary().getOriginalNativeType());
			processM.setRanking(res.getProcessSummary().getRanking());
			processM.setOwner(res.getProcessSummary().getOwner());
			processM.getVersionSummaries().clear();
			Iterator<org.apromore.canoniser.model_da.VersionSummaryType> it = 
				res.getProcessSummary().getVersionSummaries().iterator();
			// normally, only one... consider many for future needs
			while (it.hasNext()) {
				org.apromore.canoniser.model_manager.VersionSummaryType first_versionM =
					new VersionSummaryType();
				org.apromore.canoniser.model_da.VersionSummaryType versionDa = 
					(org.apromore.canoniser.model_da.VersionSummaryType) it.next();
				first_versionM.setName(versionDa.getName());
				first_versionM.setRanking(versionDa.getRanking());
				first_versionM.setCreationDate(versionDa.getCreationDate());
				first_versionM.setLastUpdate(versionDa.getLastUpdate());
				for (int i=0; i<versionDa.getAnnotations().size(); i++) {
					org.apromore.canoniser.model_manager.AnnotationsType annotationsM = new AnnotationsType();
					annotationsM.setNativeType(versionDa.getAnnotations().get(i).getNativeType());
					annotationsM.getAnnotationName().addAll(versionDa.getAnnotations().get(i).getAnnotationName());
					first_versionM.getAnnotations().add(annotationsM);
				}
				processM.getVersionSummaries().add(first_versionM);
			}
			return processM;
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

	public void StoreVersion(int editSessionCode, int processId, 
			String cpfURI, String nativeType, 
			InputStream native_is, InputStream anf_xml_is,
			InputStream cpf_xml_is) throws IOException, ExceptionStore, ExceptionVersion {

		StoreVersionInputMsgType payload = new StoreVersionInputMsgType();
		payload.setCpfURI(cpfURI);
		EditSessionType editSession = new EditSessionType();
		editSession.setNativeType(nativeType);
		editSession.setProcessId(processId);
		payload.setEditSession(editSession);
		payload.setEditSessionCode(editSessionCode);
		DataSource source_proc = new ByteArrayDataSource(native_is, "text/xml"); 
		payload.setNative(new DataHandler(source_proc));
		DataSource source_cpf = new ByteArrayDataSource(cpf_xml_is, "text/xml"); 
		payload.setCpf(new DataHandler(source_cpf));
		DataSource source_anf = new ByteArrayDataSource(anf_xml_is, "text/xml"); 
		payload.setAnf(new DataHandler(source_anf));
		StoreVersionOutputMsgType res = this.port.storeVersion(payload);
		if (res.getResult().getCode() == -1) {
			throw new ExceptionStore (res.getResult().getMessage());
		} else if (res.getResult().getCode() == -3) {
			throw new ExceptionVersion (res.getResult().getMessage());
		}
	}

	public void WriteAnnotation(Integer editSessionCode, String annotationName,
			Boolean isNew, Integer processId, String version, String cpfUri,
			String nativeType, InputStream inputStream, InputStream anf_is) throws IOException, ExceptionAnnotation {
		WriteAnnotationInputMsgType payload = new WriteAnnotationInputMsgType();
		payload.setAnnotationName(annotationName);
		payload.setEditSessionCode(editSessionCode);
		payload.setProcessId(processId);
		payload.setVersion(version);
		payload.setIsNew(isNew);
		payload.setNativeType(nativeType);
		payload.setCpfURI(cpfUri);
		DataSource source_anf = new ByteArrayDataSource(anf_is, "text/xml"); 
		payload.setAnf(new DataHandler(source_anf));
		WriteAnnotationOutputMsgType res = this.port.writeAnnotation(payload);
		if (res.getResult().getCode() == -1) {
			throw new ExceptionAnnotation (res.getResult().getMessage());
		}
	}

	public String GetCpfUri(Integer processId, String version) throws ExceptionCpfUri {
		GetCpfUriInputMsgType payload = new GetCpfUriInputMsgType();
		payload.setProcessId(processId);
		payload.setVersion(version);
		GetCpfUriOutputMsgType res = this.port.getCpfUri(payload);
		if (res.getResult().getCode() == -1) {
			throw new ExceptionCpfUri (res.getResult().getMessage());
		}
		return res.getCpfURI();
		
	}
}
