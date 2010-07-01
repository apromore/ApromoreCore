
/**
 * Please modify this class to meet your needs
 * This class is not complete
 */

package org.apromore.manager.service_portal;

import java.io.IOException;
import java.io.InputStream;
import java.sql.Date;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.logging.Logger;

import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebResult;
import javax.jws.WebService;
import javax.jws.soap.SOAPBinding;
import javax.xml.bind.annotation.XmlSeeAlso;
import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.mail.util.ByteArrayDataSource;

import org.apromore.manager.canoniser.RequestToCanoniser;
import org.apromore.manager.da.RequestToDA;
import org.apromore.manager.exception.ExceptionDeCanonise;
import org.apromore.manager.exception.ExceptionReadCanonicalAnf;
import org.apromore.manager.exception.ExceptionReadNative;
import org.apromore.manager.model_portal.DeleteProcessVersionsOutputMsgType;
import org.apromore.manager.model_portal.DomainsType;
import org.apromore.manager.model_portal.EditSessionType;
import org.apromore.manager.model_portal.ExportNativeInputMsgType;
import org.apromore.manager.model_portal.ExportNativeOutputMsgType;
import org.apromore.manager.model_portal.FormatsType;
import org.apromore.manager.model_portal.ImportProcessInputMsgType;
import org.apromore.manager.model_portal.ImportProcessOutputMsgType;
import org.apromore.manager.model_portal.ProcessSummariesType;
import org.apromore.manager.model_portal.ProcessVersionIdentifierType;
import org.apromore.manager.model_portal.ReadDomainsInputMsgType;
import org.apromore.manager.model_portal.ReadDomainsOutputMsgType;
import org.apromore.manager.model_portal.ReadEditSessionInputMsgType;
import org.apromore.manager.model_portal.ReadEditSessionOutputMsgType;
import org.apromore.manager.model_portal.ReadFormatsInputMsgType;
import org.apromore.manager.model_portal.ReadFormatsOutputMsgType;
import org.apromore.manager.model_portal.ReadProcessSummariesInputMsgType;
import org.apromore.manager.model_portal.ReadProcessSummariesOutputMsgType;
import org.apromore.manager.model_portal.ReadUserInputMsgType;
import org.apromore.manager.model_portal.ReadUserOutputMsgType;
import org.apromore.manager.model_portal.ResultType;
import org.apromore.manager.model_portal.UpdateProcessInputMsgType;
import org.apromore.manager.model_portal.UpdateProcessOutputMsgType;
import org.apromore.manager.model_portal.UpdateProcessSummariesType;
import org.apromore.manager.model_portal.UserType;
import org.apromore.manager.model_portal.WriteEditSessionInputMsgType;
import org.apromore.manager.model_portal.WriteEditSessionOutputMsgType;
import org.apromore.manager.model_portal.WriteUserInputMsgType;
import org.apromore.manager.model_portal.WriteUserOutputMsgType;

/**
 * This class was generated by Apache CXF 2.2.7
 * Thu Jul 01 18:04:38 EST 2010
 * Generated source version: 2.2.7
 * 
 */

@javax.jws.WebService(
                      serviceName = "ManagerPortalService",
                      portName = "ManagerPortal",
                      targetNamespace = "http://www.apromore.org/manager/service_portal",
                      wsdlLocation = "http://localhost:8080/Apromore-manager/services/ManagerPortal?wsdl",
                      endpointInterface = "org.apromore.manager.service_portal.ManagerPortalPortType")

		public class ManagerPortalPortTypeImpl implements ManagerPortalPortType {

	private static final Logger LOG = Logger.getLogger(ManagerPortalPortTypeImpl.class.getName());

	public org.apromore.manager.model_portal.UpdateProcessSummariesOutputMsgType 
	updateProcessSummaries(org.apromore.manager.model_portal.UpdateProcessSummariesInputMsgType payload) { 
        LOG.info("Executing operation updateProcessSummaries");
        System.out.println(payload);
        org.apromore.manager.model_portal.UpdateProcessSummariesOutputMsgType res = 
        	new org.apromore.manager.model_portal.UpdateProcessSummariesOutputMsgType();
		org.apromore.manager.model_portal.ResultType result = 
			new org.apromore.manager.model_portal.ResultType();
		res.setResult(result);
		UpdateProcessSummariesType processes = payload.getUpdateProcessSummaries();
        try {
			RequestToDA request = new RequestToDA();
			request.UpdateProcesses (processes);
			result.setCode(0);
			result.setMessage("");
		} catch (Exception ex) {
			ex.printStackTrace();
			result.setCode(-1);
			result.setMessage("ManagerPortalPortTypeImpl(deleteEditSession): " + ex.getMessage());
		}
		return res;
    }

	public org.apromore.manager.model_portal.DeleteEditSessionOutputMsgType 
	deleteEditSession(org.apromore.manager.model_portal.DeleteEditSessionInputMsgType payload) { 
		LOG.info("Executing operation deleteEditSession");
		System.out.println(payload);
		org.apromore.manager.model_portal.DeleteEditSessionOutputMsgType res = 
			new org.apromore.manager.model_portal.DeleteEditSessionOutputMsgType();
		org.apromore.manager.model_portal.ResultType result = 
			new org.apromore.manager.model_portal.ResultType();
		res.setResult(result);
		int code = payload.getEditSessionCode();
		try {
			RequestToDA request = new RequestToDA();
			request.DeleteEditSession(code);
			result.setCode(0);
			result.setMessage("");
		} catch (Exception ex) {
			ex.printStackTrace();
			result.setCode(-1);
			result.setMessage("ManagerPortalPortTypeImpl(deleteEditSession): " + ex.getMessage());
		}
		return res;
	}


	public org.apromore.manager.model_portal.DeleteProcessVersionsOutputMsgType 
	deleteProcessVersions(org.apromore.manager.model_portal.DeleteProcessVersionsInputMsgType payload) { 
		LOG.info("Executing operation deleteProcessVersions");
		System.out.println(payload);
		org.apromore.manager.model_portal.DeleteProcessVersionsOutputMsgType res =
			new DeleteProcessVersionsOutputMsgType();
		org.apromore.manager.model_portal.ResultType result = 
			new org.apromore.manager.model_portal.ResultType();
		res.setResult(result);
		List<ProcessVersionIdentifierType> processesP = payload.getProcessVersionIdentifier();
		try {
			List<org.apromore.manager.model_da.ProcessVersionIdentifierType> processesDa = 
				new ArrayList<org.apromore.manager.model_da.ProcessVersionIdentifierType>();
			Iterator<ProcessVersionIdentifierType> it = processesP.iterator();
			while (it.hasNext()) {
				org.apromore.manager.model_portal.ProcessVersionIdentifierType processP =
					it.next();
				org.apromore.manager.model_da.ProcessVersionIdentifierType processDa =
					new org.apromore.manager.model_da.ProcessVersionIdentifierType();
				processDa.setProcessid(processP.getProcessid());
				processDa.getVersionName().addAll(processP.getVersionName());
				processesDa.add(processDa);
			}
			RequestToDA request = new RequestToDA();
			request.DeleteProcessVersion(processesDa);
			result.setCode(0);
			result.setMessage("");
		} catch (Exception ex) {
			ex.printStackTrace();
			result.setCode(-1);
			result.setMessage("ManagerPortalPortTypeImpl(deleteProcessVersions): " + ex.getMessage());
		}
		return res;
	}






	public UpdateProcessOutputMsgType updateProcess(UpdateProcessInputMsgType payload) { 
		LOG.info("Executing operation updateProcess");
		System.out.println(payload);
		UpdateProcessOutputMsgType res = new UpdateProcessOutputMsgType();
		ResultType result = new ResultType();
		res.setResult(result);

		try {
			DataHandler handler = payload.getNative();
			InputStream native_is = handler.getInputStream();
			int processId = payload.getProcessId();
			String nativeType = payload.getNativeType();
			String domain = payload.getDomain();
			RequestToCanoniser request = new RequestToCanoniser();
			request.CanoniseVersion (processId, nativeType, domain, native_is);
			result.setCode(0);
			result.setMessage("");
		} catch (Exception ex) {
			ex.printStackTrace();
			result.setCode(-1);
			result.setMessage(ex.getMessage());
		}
		return res;
	}


	public ReadEditSessionOutputMsgType readEditSession(ReadEditSessionInputMsgType payload) { 
		LOG.info("Executing operation readEditSession");
		System.out.println(payload);
		ReadEditSessionOutputMsgType res = new ReadEditSessionOutputMsgType();
		ResultType result = new ResultType();
		res.setResult(result);
		int code = payload.getEditSessionCode();
		try {
			RequestToDA request = new RequestToDA();
			org.apromore.manager.model_da.EditSessionType editSessionDA = request.ReadEditSession(code);
			org.apromore.manager.model_portal.EditSessionType editSessionP = new EditSessionType();
			editSessionP.setNativeType(editSessionDA.getNativeType());
			editSessionP.setProcessId(editSessionDA.getProcessId());
			editSessionP.setUsername(editSessionDA.getUsername());
			editSessionP.setVersionName(editSessionDA.getVersionName());
			editSessionP.setProcessName(editSessionDA.getProcessName());
			editSessionP.setDomain(editSessionDA.getDomain());
			res.setEditSession(editSessionP);
			result.setCode(0);
			result.setMessage("");
		} catch (Exception ex) {
			ex.printStackTrace();
			result.setCode(-1);
			result.setMessage("ManagerPortalPortTypeImpl(readEditSession): " + ex.getMessage());
		}
		return res;
	}

	public WriteEditSessionOutputMsgType writeEditSession (WriteEditSessionInputMsgType payload) { 
		LOG.info("Executing operation writeEditSession");
		System.out.println(payload);
		WriteEditSessionOutputMsgType res = new WriteEditSessionOutputMsgType();
		ResultType result = new ResultType();
		res.setResult(result);
		org.apromore.manager.model_portal.EditSessionType editSessionP = payload.getEditSession();
		org.apromore.manager.model_da.EditSessionType editSessionDA = new org.apromore.manager.model_da.EditSessionType();
		editSessionDA.setNativeType(editSessionP.getNativeType());
		editSessionDA.setProcessId(editSessionP.getProcessId());
		editSessionDA.setUsername(editSessionP.getUsername());
		editSessionDA.setVersionName(editSessionP.getVersionName());
		editSessionDA.setProcessName(editSessionP.getProcessName());
		try {
			RequestToDA request = new RequestToDA();
			int code = request.WriteEditSession(editSessionDA);

			res.setEditSessionCode(code);
			result.setCode(0);
			result.setMessage("");
		} catch (Exception ex) {
			ex.printStackTrace();
			result.setCode(-1);
			result.setMessage("ManagerPortalPortTypeImpl(writeEditSession): " + ex.getMessage());
		}
		return res;
	}


	public ExportNativeOutputMsgType exportNative(ExportNativeInputMsgType payload) { 
		LOG.info("Executing operation exportNative");
		System.out.println(payload);
		ExportNativeOutputMsgType res = new ExportNativeOutputMsgType();
		ResultType result = new ResultType();
		res.setResult(result);
		int processId = payload.getProcessId();
		String version = payload.getVersionName();
		String nativeType = payload.getNativeType();
		Boolean withAnnotations = payload.isWithAnnotations();

		try {
			RequestToDA request = new RequestToDA();
			InputStream native_xml = request.ReadNative (processId, version, nativeType);

			DataSource source = new ByteArrayDataSource(native_xml, "text/xml"); 
			res.setNative(new DataHandler(source));	

			result.setCode(0);
			result.setMessage("");
			//throw new ExceptionReadNative("temporary...");
		} catch (ExceptionReadNative ex) {
			try {
				// native not found, request canonical
				RequestToDA request1 = new RequestToDA();
				request1.ReadCanonicalAnf (processId, version);
				InputStream cpf_is = request1.getCpf();
				InputStream anf_is = request1.getAnf();
				// request canonical_xml de-canonisation
				RequestToCanoniser requestCa = new RequestToCanoniser();
				// TODO temporary to test de-canoniser with and without annotations
				// TODO: annotations might be unavailable!
				InputStream native_xml;
				if (withAnnotations) {
					native_xml = requestCa.DeCanonise (processId, version, nativeType, cpf_is, anf_is);
				} else {
					native_xml = requestCa.DeCanonise (processId, version, nativeType, cpf_is, null);
				}
				DataSource source = new ByteArrayDataSource(native_xml, "text/xml"); 
				res.setNative(new DataHandler(source));	

				result.setCode(0);
				result.setMessage("");
			} catch (ExceptionDeCanonise e) {
				e.printStackTrace();
				result.setCode(-1);
				result.setMessage(e.getMessage());
			} catch (ExceptionReadCanonicalAnf e) {
				e.printStackTrace();
				result.setCode(-1);
				result.setMessage(e.getMessage());
			} catch (IOException e) {
				e.printStackTrace();
				result.setCode(-1);
				result.setMessage(e.getMessage());
			} 
		} catch (Exception e) {	
			e.printStackTrace();
			result.setCode(-1);
			result.setMessage("ManagerPortalPortTypeImpl(exportNative): " + e.getMessage());
		}
		return res;
	}

	public ImportProcessOutputMsgType importProcess(ImportProcessInputMsgType payload) { 
		LOG.info("Executing operation importProcess");
		System.out.println(payload);
		ImportProcessOutputMsgType res = new ImportProcessOutputMsgType();
		ResultType result = new ResultType();
		res.setResult(result);

		try {
			DataHandler handler = payload.getProcessDescription();
			InputStream is = handler.getInputStream();
			String username = payload.getUsername();
			String processName = payload.getProcessName();
			String versionName = payload.getVersionName();
			String nativeType = payload.getNativeType();
			String domain = payload.getDomain();
			String documentation = payload.getDocumentation();
			String created = payload.getCreationDate();
			String lastupdate = payload.getLastUpdate();
			RequestToCanoniser request = new RequestToCanoniser();
			org.apromore.manager.model_portal.ProcessSummaryType process =
				request.CanoniseProcess(username, processName, versionName, 
						nativeType,is, domain, documentation, created, lastupdate);
			res.setProcessSummary(process);
			result.setCode(0);
			result.setMessage("");

		} catch (Exception ex) {
			ex.printStackTrace();
			result.setCode(-1);
			result.setMessage("ManagerPortalPortTypeImpl(importProcess): " + ex.getMessage());
		}
		return res;
	}


	/* (non-Javadoc)
	 * @see org.apromore.manager.service_portal.ManagerPortalPortType#writeUser(org.apromore.manager.model_portal.WriteUserInputMsgType  payload )*
	 */
	public WriteUserOutputMsgType writeUser(WriteUserInputMsgType payload) { 
		LOG.info("Executing operation writeUser");
		System.out.println(payload);

		WriteUserOutputMsgType res = new WriteUserOutputMsgType();
		ResultType result = new ResultType();
		res.setResult(result);
		UserType user = payload.getUser();
		try {
			RequestToDA request = new RequestToDA();
			request.WriteUser(user);
			result.setCode(0);
			result.setMessage("");
		} catch (Exception ex) {
			ex.printStackTrace();
			result.setCode(0);
			result.setMessage("ManagerPortalPortTypeImpl(writeUser): " + ex.getMessage());
		}
		return res;
	}

	/* (non-Javadoc)
	 * @see org.apromore.manager.service_portal.ManagerPortalPortType#readFormats(org.apromore.manager.model_portal.ReadFormatsInputMsgType  payload )*
	 */
	public ReadFormatsOutputMsgType readFormats(ReadFormatsInputMsgType payload) { 
		LOG.info("Executing operation readFormats");
		System.out.println(payload);
		ReadFormatsOutputMsgType res = new ReadFormatsOutputMsgType();
		ResultType result = new ResultType();
		res.setResult(result);
		try {
			RequestToDA request = new RequestToDA();
			FormatsType formats = request.ReadFormats();
			result.setCode(0);
			result.setMessage("");
			res.setFormats(formats);
		} catch (Exception ex) {
			ex.printStackTrace();
			result.setCode(-1);
			result.setMessage("ManagerPortalPortTypeImpl(ReadFormats) " + ex.getMessage());
		}
		return res;
	}

	/* (non-Javadoc)
	 * @see org.apromore.manager.service_portal.ManagerPortalPortType#readDomains(org.apromore.manager.model_portal.ReadDomainsInputMsgType  payload )*
	 */
	public ReadDomainsOutputMsgType readDomains(ReadDomainsInputMsgType payload) { 
		LOG.info("Executing operation readDomains");
		System.out.println(payload);

		ReadDomainsOutputMsgType res = new ReadDomainsOutputMsgType();
		ResultType result = new ResultType();
		res.setResult(result);
		try {
			RequestToDA request = new RequestToDA();
			DomainsType domains = request.ReadDomains();
			result.setCode(0);
			result.setMessage("");
			res.setDomains(domains);
		} catch (Exception ex) {
			ex.printStackTrace();
			result.setCode(-1);
			result.setMessage("ManagerPortalPortTypeImpl(ReadDomains) " + ex.getMessage());   
		}
		return res;
	}

	/* (non-Javadoc)
	 * @see org.apromore.manager.service_portal.ManagerPortalPortType#readUser(org.apromore.manager.model_portal.ReadUserInputMsgType  payload )*
	 */
	public ReadUserOutputMsgType readUser(ReadUserInputMsgType payload) { 
		LOG.info("Executing operation readUser");
		System.out.println(payload);

		ReadUserOutputMsgType res = new ReadUserOutputMsgType();
		ResultType result = new ResultType();
		res.setResult(result);

		try {
			RequestToDA request = new RequestToDA();
			UserType user = request.ReadUser(payload.getUsername());
			result.setCode(0);
			result.setMessage("");
			res.setUser(user);
		} catch (Exception ex) {
			ex.printStackTrace();
			result.setCode(-1);
			result.setMessage("ManagerPortalPortTypeImpl(ReadUser) " + ex.getMessage());
		}
		return res;
	}

	/* (non-Javadoc)
	 * @see org.apromore.manager.service_portal.ManagerPortalPortType#readProcessSummaries(org.apromore.manager.model_portal.ReadProcessSummariesInputMsgType  payload )*
	 */
	public ReadProcessSummariesOutputMsgType readProcessSummaries(ReadProcessSummariesInputMsgType payload) { 
		LOG.info("Executing operation readProcessSummaries");
		System.out.println(payload);

		ReadProcessSummariesOutputMsgType res = new ReadProcessSummariesOutputMsgType();
		ResultType result = new ResultType();
		res.setResult(result);

		try {
			RequestToDA request = new RequestToDA();
			ProcessSummariesType processes = request.ReadProcessSummaries (payload.getSearchExpression());
			result.setCode(0);
			result.setMessage("");
			res.setProcessSummaries(processes);
		} catch (Exception ex) {
			ex.printStackTrace();
			result.setCode(-1);
			result.setMessage("ManagerPortalPortTypeImpl(ReadProcessSummaries) " + ex.getMessage());
		}
		return res;
	}

}
