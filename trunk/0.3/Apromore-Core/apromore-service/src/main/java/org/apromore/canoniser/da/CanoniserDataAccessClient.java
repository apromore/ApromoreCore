package org.apromore.canoniser.da;

import org.apromore.dao.DataAccessCanoniserManager;
import org.apromore.exception.ExceptionAnnotation;
import org.apromore.exception.ExceptionCpfUri;
import org.apromore.exception.ExceptionStore;
import org.apromore.exception.ExceptionVersion;
import org.apromore.model.EditSessionType;
import org.apromore.model.GetCpfUriInputMsgType;
import org.apromore.model.GetCpfUriOutputMsgType;
import org.apromore.model.StoreNativeInputMsgType;
import org.apromore.model.StoreNativeOutputMsgType;
import org.apromore.model.StoreVersionInputMsgType;
import org.apromore.model.StoreVersionOutputMsgType;
import org.apromore.model.WriteAnnotationInputMsgType;
import org.apromore.model.WriteAnnotationOutputMsgType;

import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.mail.util.ByteArrayDataSource;
import java.io.IOException;
import java.io.InputStream;

public class CanoniserDataAccessClient {

	private DataAccessCanoniserManager manager;


//	public ProcessSummaryType storeNativeCpf (String username, String processName, String cpfURI,
//			String domain, String nativeType, String versionName, String documentation, String created, String lastupdate,
//			InputStream process_xml, InputStream cpf_xml, InputStream anf_xml) throws IOException, ExceptionStore {
//
//		ProcessSummaryType processM = new ProcessSummaryType();
//		StoreNativeCpfInputMsgType payload = new StoreNativeCpfInputMsgType();
//		payload.setCpfURI(cpfURI);
//		EditSessionType editSession = new EditSessionType();
//		editSession.setUsername(username);
//		editSession.setNativeType(nativeType);
//		editSession.setProcessName(processName);
//		editSession.setDomain(domain);
//		editSession.setVersionName(versionName);
//		editSession.setCreationDate(created);
//		editSession.setLastUpdate(lastupdate);
//		payload.setEditSession(editSession);
//		DataSource source_proc = new ByteArrayDataSource(process_xml, "text/xml");
//		payload.setNative(new DataHandler(source_proc));
//		DataSource source_cpf = new ByteArrayDataSource(cpf_xml, "text/xml");
//		payload.setCpf(new DataHandler(source_cpf));
//		DataSource source_anf = new ByteArrayDataSource(anf_xml, "text/xml");
//		payload.setAnf(new DataHandler(source_anf));
//		StoreNativeCpfOutputMsgType res = manager.storeNativeCpf(payload);
//		if (res.getResult().getCode() == -1) {
//			throw new ExceptionStore (res.getResult().getMessage());
//		} else {
//			processM.setDomain(res.getProcessSummary().getDomain());
//			processM.setId(res.getProcessSummary().getId());
//			processM.setLastVersion(res.getProcessSummary().getLastVersion());
//			processM.setName(res.getProcessSummary().getName());
//			processM.setOriginalNativeType(res.getProcessSummary().getOriginalNativeType());
//			processM.setRanking(res.getProcessSummary().getRanking());
//			processM.setOwner(res.getProcessSummary().getOwner());
//			processM.getVersionSummaries().clear();
//			Iterator<VersionSummaryType> it = res.getProcessSummary().getVersionSummaries().iterator();
//			// normally, only one... consider many for future needs
//			while (it.hasNext()) {
//				VersionSummaryType first_versionM = new VersionSummaryType();
//				VersionSummaryType versionDa = it.next();
//				first_versionM.setName(versionDa.getName());
//				first_versionM.setRanking(versionDa.getRanking());
//				first_versionM.setCreationDate(versionDa.getCreationDate());
//				first_versionM.setLastUpdate(versionDa.getLastUpdate());
//				for (int i=0; i<versionDa.getAnnotations().size(); i++) {
//					AnnotationsType annotationsM = new AnnotationsType();
//					annotationsM.setNativeType(versionDa.getAnnotations().get(i).getNativeType());
//					annotationsM.getAnnotationName().addAll(versionDa.getAnnotations().get(i).getAnnotationName());
//					first_versionM.getAnnotations().add(annotationsM);
//				}
//				processM.getVersionSummaries().add(first_versionM);
//			}
//			return processM;
//		}
//	}

	public void StoreNative (int processId, String version, String nativeType, InputStream process) 
	        throws IOException, ExceptionStore {
		StoreNativeInputMsgType payload = new StoreNativeInputMsgType();
		payload.setProcessId(processId);
		payload.setVersion(version);
		payload.setNativeType(nativeType);
		DataSource source_native = new ByteArrayDataSource(process, "text/xml");
		payload.setNative(new DataHandler(source_native));
		StoreNativeOutputMsgType res = manager.storeNative(payload);
		if (res.getResult().getCode() == -1) {
			throw new ExceptionStore (res.getResult().getMessage());
		}
	}

	public void StoreVersion(int editSessionCode, EditSessionType editSession, 
			String cpfURI, InputStream native_is, InputStream anf_xml_is,
			InputStream cpf_xml_is) throws IOException, ExceptionStore, ExceptionVersion {
		StoreVersionInputMsgType payload = new StoreVersionInputMsgType();
		payload.setCpfURI(cpfURI);
		payload.setEditSession(editSession);
		payload.setEditSessionCode(editSessionCode);
		DataSource source_proc = new ByteArrayDataSource(native_is, "text/xml"); 
		payload.setNative(new DataHandler(source_proc));
		DataSource source_cpf = new ByteArrayDataSource(cpf_xml_is, "text/xml"); 
		payload.setCpf(new DataHandler(source_cpf));
		DataSource source_anf = new ByteArrayDataSource(anf_xml_is, "text/xml"); 
		payload.setAnf(new DataHandler(source_anf));
		StoreVersionOutputMsgType res = manager.storeVersion(payload);
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
		WriteAnnotationOutputMsgType res = manager.writeAnnotation(payload);
		if (res.getResult().getCode() == -1) {
			throw new ExceptionAnnotation (res.getResult().getMessage());
		}
	}

	public String GetCpfUri(Integer processId, String version) throws ExceptionCpfUri {
		GetCpfUriInputMsgType payload = new GetCpfUriInputMsgType();
		payload.setProcessId(processId);
		payload.setVersion(version);
		GetCpfUriOutputMsgType res = manager.getCpfUri(payload);
		if (res.getResult().getCode() == -1) {
			throw new ExceptionCpfUri (res.getResult().getMessage());
		}
		return res.getCpfURI();
	}



    public void setManager(DataAccessCanoniserManager manager) {
        this.manager = manager;
    }
}
