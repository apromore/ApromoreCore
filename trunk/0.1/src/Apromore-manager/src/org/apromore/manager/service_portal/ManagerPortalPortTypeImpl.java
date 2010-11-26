
/**
 * Please modify this class to meet your needs
 * This class is not complete
 */

package org.apromore.manager.service_portal;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
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
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;

import org.apromore.manager.canoniser.RequestToCanoniser;
import org.apromore.manager.commons.Constants;
import org.apromore.manager.da.RequestToDA;
import org.apromore.manager.exception.ExceptionCanoniseVersion;
import org.apromore.manager.exception.ExceptionDeCanonise;
import org.apromore.manager.exception.ExceptionReadCanonicalAnf;
import org.apromore.manager.exception.ExceptionReadNative;
import org.apromore.manager.exception.ExceptionVersion;
import org.apromore.manager.model_portal.DeleteProcessVersionsOutputMsgType;
import org.apromore.manager.model_portal.DomainsType;
import org.apromore.manager.model_portal.EditSessionType;
import org.apromore.manager.model_portal.ExportFormatInputMsgType;
import org.apromore.manager.model_portal.ExportFormatOutputMsgType;
import org.apromore.manager.model_portal.ImportProcessInputMsgType;
import org.apromore.manager.model_portal.ImportProcessOutputMsgType;
import org.apromore.manager.model_portal.MergeProcessesOutputMsgType;
import org.apromore.manager.model_portal.NativeTypesType;
import org.apromore.manager.model_portal.ParameterType;
import org.apromore.manager.model_portal.ProcessSummariesType;
import org.apromore.manager.model_portal.ProcessSummaryType;
import org.apromore.manager.model_portal.ProcessVersionIdType;
import org.apromore.manager.model_portal.ProcessVersionIdentifierType;
import org.apromore.manager.model_portal.ReadAllUsersOutputMsgType;
import org.apromore.manager.model_portal.ReadDomainsInputMsgType;
import org.apromore.manager.model_portal.ReadDomainsOutputMsgType;
import org.apromore.manager.model_portal.ReadEditSessionInputMsgType;
import org.apromore.manager.model_portal.ReadEditSessionOutputMsgType;
import org.apromore.manager.model_portal.ReadNativeTypesInputMsgType;
import org.apromore.manager.model_portal.ReadNativeTypesOutputMsgType;
import org.apromore.manager.model_portal.ReadProcessSummariesInputMsgType;
import org.apromore.manager.model_portal.ReadProcessSummariesOutputMsgType;
import org.apromore.manager.model_portal.ReadUserInputMsgType;
import org.apromore.manager.model_portal.ReadUserOutputMsgType;
import org.apromore.manager.model_portal.ResultType;
import org.apromore.manager.model_portal.SearchForSimilarProcessesOutputMsgType;
import org.apromore.manager.model_portal.UpdateProcessInputMsgType;
import org.apromore.manager.model_portal.UpdateProcessOutputMsgType;
import org.apromore.manager.model_portal.UserType;
import org.apromore.manager.model_portal.UsernamesType;
import org.apromore.manager.model_portal.WriteAnnotationOutputMsgType;
import org.apromore.manager.model_portal.WriteEditSessionInputMsgType;
import org.apromore.manager.model_portal.WriteEditSessionOutputMsgType;
import org.apromore.manager.model_portal.WriteUserInputMsgType;
import org.apromore.manager.model_portal.WriteUserOutputMsgType;
import org.apromore.manager.model_toolbox.ParametersType;
import org.apromore.manager.model_toolbox.ProcessVersionIdsType;
import org.apromore.manager.toolbox.RequestToToolbox;
import org.apromore.manager.toolbox.ToolboxManagerPortType;
import org.apromore.manager.toolbox.ToolboxManagerService;
import org.wfmc._2008.xpdl2.Author;
import org.wfmc._2008.xpdl2.Created;
import org.wfmc._2008.xpdl2.Documentation;
import org.wfmc._2008.xpdl2.ModificationDate;
import org.wfmc._2008.xpdl2.PackageHeader;
import org.wfmc._2008.xpdl2.PackageType;
import org.wfmc._2008.xpdl2.RedefinableHeader;
import org.wfmc._2008.xpdl2.Version;

import de.epml.TypeEPML;

/**
 * This class was generated by Apache CXF 2.2.9
 * Sat Nov 20 17:14:27 CET 2010
 * Generated source version: 2.2.9
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






	public org.apromore.manager.model_portal.EditProcessDataOutputMsgType
	editProcessData(org.apromore.manager.model_portal.EditProcessDataInputMsgType payload) { 
		LOG.info("Executing operation editDataProcess");
		System.out.println(payload);
		org.apromore.manager.model_portal.EditProcessDataOutputMsgType res = 
			new org.apromore.manager.model_portal.EditProcessDataOutputMsgType();
		org.apromore.manager.model_portal.ResultType result = 
			new org.apromore.manager.model_portal.ResultType();
		res.setResult(result);
		try {			
			Integer processId = payload.getId();
			String processName = payload.getProcessName();
			String domain = payload.getDomain();
			String username = payload.getOwner();
			String preVersion = payload.getPreName();
			String newVersion = payload.getNewName();
			String ranking = payload.getRanking();
			RequestToDA request = new RequestToDA();
			request.EditProcessData (processId, processName, domain, username,
					preVersion, newVersion, ranking);
			result.setCode(0);
			result.setMessage("");
		} catch (Exception ex) {
			ex.printStackTrace();
			result.setCode(-1);
			result.setMessage(ex.getMessage());
		}
		return res;
	}




	public org.apromore.manager.model_portal.MergeProcessesOutputMsgType 
	mergeProcesses(org.apromore.manager.model_portal.MergeProcessesInputMsgType payload) { 
		LOG.info("Executing operation mergeProcesses");
		System.out.println(payload);
		org.apromore.manager.model_portal.MergeProcessesOutputMsgType res =
			new MergeProcessesOutputMsgType();
		ResultType result = new ResultType();
		res.setResult(result);		
		try {
			// Build data to send to toolbox
			String algo = payload.getAlgorithm();
			String processName = payload.getProcessName();
			String version = payload.getVersionName();
			Integer processId = payload.getProcessId();
			String username = payload.getUsername();
			org.apromore.manager.model_toolbox.ParametersType parameters = 
				new org.apromore.manager.model_toolbox.ParametersType();
			for (ParameterType p : payload.getParameters().getParameter()) {
				org.apromore.manager.model_toolbox.ParameterType param = 
					new org.apromore.manager.model_toolbox.ParameterType();
				param.setName(p.getName());
				param.setValue(p.getValue());
				parameters.getParameter().add(param);
			}
			// processes
			org.apromore.manager.model_toolbox.ProcessVersionIdsType ids = 
				new ProcessVersionIdsType();
			for (ProcessVersionIdType t : payload.getProcessVersionIds().getProcessVersionId()) {
				org.apromore.manager.model_toolbox.ProcessVersionIdType id = 
					new org.apromore.manager.model_toolbox.ProcessVersionIdType();
				id.setProcessId(t.getProcessId());
				id.setVersionName(t.getVersionName());
				ids.getProcessVersionId().add(id);
			}
			RequestToToolbox request = new RequestToToolbox();
			ProcessSummaryType respFromToolbox = 
				request.MergeProcesses(processName, version, username, algo, parameters, ids);
			res.setProcessSummary(respFromToolbox);
			result.setCode(0);
			result.setMessage("");
		} catch (Exception ex) {
			ex.printStackTrace();
			result.setCode(-1);
			result.setMessage(ex.getMessage());
		}
		return res;
	}





	/* (non-Javadoc)
	 * @see org.apromore.manager.service_portal1.ManagerPortalPortType#searchForSimilarProcesses(org.apromore.manager.model_portal.SearchForSimilarProcessesInputMsgType  payload )*
	 */
	public org.apromore.manager.model_portal.SearchForSimilarProcessesOutputMsgType 
	searchForSimilarProcesses(org.apromore.manager.model_portal.SearchForSimilarProcessesInputMsgType payload) { 
		LOG.info("Executing operation searchForSimilarProcesses");
		System.out.println(payload);
		org.apromore.manager.model_portal.SearchForSimilarProcessesOutputMsgType res = 
			new SearchForSimilarProcessesOutputMsgType();
		org.apromore.manager.model_portal.ResultType result = 
			new org.apromore.manager.model_portal.ResultType();
		res.setResult(result);
		try {
			String algo = payload.getAlgorithm();
			Integer processId = payload.getProcessId();
			String version = payload.getVersionName();
			org.apromore.manager.model_toolbox.ParametersType paramsT = new ParametersType();
			for (ParameterType p : payload.getParameters().getParameter()) {
				org.apromore.manager.model_toolbox.ParameterType paramT = 
					new org.apromore.manager.model_toolbox.ParameterType();
				paramsT.getParameter().add(paramT);
				paramT.setName(p.getName());
				paramT.setValue(p.getValue());
			}
			RequestToToolbox req = new RequestToToolbox();
			org.apromore.manager.model_portal.ProcessSummariesType processes = 
				req.SearchForSimilarProcesses(processId, algo, paramsT);
			res.setProcessSummaries(processes);
			result.setCode(0);
			result.setMessage("");			
		} catch (Exception ex) {
			ex.printStackTrace();
			result.setCode(-1);
			result.setMessage(ex.getMessage());
		}
		return res;
	}

	public org.apromore.manager.model_portal.WriteAnnotationOutputMsgType writeAnnotation 
	(org.apromore.manager.model_portal.WriteAnnotationInputMsgType payload) { 
		LOG.info("Executing operation writeAnnotation");
		System.out.println(payload);
		org.apromore.manager.model_portal.WriteAnnotationOutputMsgType res =
			new WriteAnnotationOutputMsgType();
		org.apromore.manager.model_portal.ResultType result = 
			new org.apromore.manager.model_portal.ResultType();
		res.setResult(result);
		try {
			Integer editSessionCode = payload.getEditSessionCode();
			String annotName = payload.getAnnotationName();
			Integer processId = payload.getProcessId();
			String version = payload.getVersion();
			String nat_type = payload.getNativeType();
			Boolean isNew = payload.isIsNew();
			DataHandler handler = payload.getNative();
			InputStream native_is = handler.getInputStream();
			RequestToCanoniser request = new RequestToCanoniser();
			request.GenerateAnnotation (annotName, editSessionCode, isNew, processId, version, nat_type, native_is);
			result.setCode(0);
			result.setMessage("");
		} catch (Exception ex) {
			ex.printStackTrace();
			result.setCode(-1);
			result.setMessage(ex.getMessage());
		}
		return res;
	}




	public org.apromore.manager.model_portal.ReadAllUsersOutputMsgType readAllUsers(org.apromore.manager.model_portal.ReadAllUsersInputMsgType payload) { 
		LOG.info("Executing operation readAllUsers");
		System.out.println(payload);
		org.apromore.manager.model_portal.ReadAllUsersOutputMsgType res =
			new ReadAllUsersOutputMsgType();
		org.apromore.manager.model_portal.ResultType result = 
			new org.apromore.manager.model_portal.ResultType();
		res.setResult(result);
		try {
			RequestToDA request = new RequestToDA();
			UsernamesType allUsers = request.ReadAllUsers();
			res.setUsernames(allUsers);
			result.setCode(0);
			result.setMessage("");
		} catch (Exception ex) {
			ex.printStackTrace();
			result.setCode(-1);
			result.setMessage(ex.getMessage());
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
			result.setMessage(ex.getMessage());
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
			result.setMessage(ex.getMessage());
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
			int editSessionCode = payload.getEditSessionCode();
			int processId = payload.getProcessId();
			String nativeType = payload.getNativeType();
			String preVersion = payload.getPreVersion();
			String new_uri = newCpfURI();
			InputStream modified_native_is = native_is;
			// update native_is with new uri if it's a new version regarding preVersion
			if ("XPDL 2.1".compareTo(nativeType)==0) {
				JAXBContext jc = JAXBContext.newInstance("org.wfmc._2008.xpdl2");
				Unmarshaller u = jc.createUnmarshaller();
				JAXBElement<PackageType> xpdl_root = (JAXBElement<PackageType>) u.unmarshal(native_is);
				PackageType xpdl = xpdl_root.getValue();
				if(xpdl.getRedefinableHeader().getVersion()!=null &&
						preVersion.compareTo(xpdl.getRedefinableHeader().getVersion().getValue())!=0) {
					xpdl.setId(new_uri);
					Marshaller m = jc.createMarshaller();
					m.setProperty(javax.xml.bind.Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE );
					JAXBElement<PackageType> rootxpdl = 
						new org.wfmc._2008.xpdl2.ObjectFactory().createPackage(xpdl);
					ByteArrayOutputStream xpdl_xml = new ByteArrayOutputStream();
					m.marshal(rootxpdl, xpdl_xml);
					modified_native_is = new ByteArrayInputStream(xpdl_xml.toByteArray());
				}
			} else if ("EPML 2.0".compareTo(nativeType)==0) {
				// TODO: where to put process version uri?
				JAXBContext jc = JAXBContext.newInstance("de.epml");
				Unmarshaller u = jc.createUnmarshaller();
				JAXBElement<TypeEPML> epml_root = (JAXBElement<TypeEPML>) u.unmarshal(native_is);
				TypeEPML epml = epml_root.getValue();

				// TODO: to be finished!

				Marshaller m = jc.createMarshaller();
				m.setProperty(javax.xml.bind.Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE );
				JAXBElement<TypeEPML> rootepml = new de.epml.ObjectFactory().createEpml(epml);
				ByteArrayOutputStream epml_xml = new ByteArrayOutputStream();
				m.marshal(rootepml, epml_xml);
				modified_native_is = new ByteArrayInputStream(epml_xml.toByteArray());
			}
			RequestToCanoniser request = new RequestToCanoniser();
			request.CanoniseVersion (editSessionCode, processId, preVersion, 
					nativeType, modified_native_is);
			result.setCode(0);
			result.setMessage("");
		} catch (ExceptionVersion ex) {
			result.setCode(-3);
			result.setMessage(ex.getMessage());
		} catch (IOException ex) {
			result.setCode(-1);
			result.setMessage(ex.getMessage());
		} catch (ExceptionCanoniseVersion ex) {
			result.setCode(-1);
			result.setMessage(ex.getMessage());
		} catch (JAXBException e) {
			result.setCode(-1);
			result.setMessage(e.getMessage());
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
			editSessionP.setWithAnnotation(editSessionDA.isWithAnnotation());
			editSessionP.setAnnotation(editSessionDA.getAnnotation());
			res.setEditSession(editSessionP);
			result.setCode(0);
			result.setMessage("");
		} catch (Exception ex) {
			ex.printStackTrace();
			result.setCode(-1);
			result.setMessage(ex.getMessage());
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
		editSessionDA.setWithAnnotation(editSessionP.isWithAnnotation());
		editSessionDA.setAnnotation(editSessionP.getAnnotation());
		try {
			RequestToDA request = new RequestToDA();
			int code = request.WriteEditSession(editSessionDA);

			res.setEditSessionCode(code);
			result.setCode(0);
			result.setMessage("");
		} catch (Exception ex) {
			ex.printStackTrace();
			result.setCode(-1);
			result.setMessage(ex.getMessage());
		}
		return res;
	}

	public ExportFormatOutputMsgType exportFormat(ExportFormatInputMsgType payload) { 
		LOG.info("Executing operation exportFormat");
		System.out.println(payload);
		ExportFormatOutputMsgType res = new ExportFormatOutputMsgType();
		ResultType result = new ResultType();
		res.setResult(result);
		int processId = payload.getProcessId();
		String processname = payload.getProcessName();
		String version = payload.getVersionName();
		String annotationName = payload.getAnnotationName();
		String format = payload.getFormat();
		Boolean withAnnotations = payload.isWithAnnotations();
		String owner = payload.getOwner();
		try {
			// Get native from the database, only if initial annotations are to be used
			// or if format is Constants.CANONICAL or Constants.ANNOTATION
			if ((withAnnotations && annotationName.compareTo(Constants.INITIAL_ANNOTATIONS)==0)
					|| Constants.CANONICAL.compareTo(format)==0 
					|| format.startsWith(Constants.ANNOTATIONS)) {
				RequestToDA request = new RequestToDA();
				InputStream native_xml = request.ReadFormat (processId, version, format);
				DataSource source = new ByteArrayDataSource(native_xml, "text/xml"); 
				res.setNative(new DataHandler(source));	
				result.setCode(0);
				result.setMessage("");
			} else {
				// native not found or native found but Initial annotations not to be used
				// or no annotations to be used
				throw new ExceptionReadNative("");
			}
		} catch (ExceptionReadNative ex) {
			try {
				// native not found, request canonical
				RequestToDA request1 = new RequestToDA();
				request1.ReadCanonicalAnf (processId, version, withAnnotations, annotationName);
				InputStream cpf_is = request1.getCpf();
				InputStream anf_is = request1.getAnf();
				// request canonical_xml de-canonisation
				RequestToCanoniser requestCa = new RequestToCanoniser();
				// TODO temporary to test de-canoniser with and without annotations
				// TODO: annotations might be unavailable!
				InputStream native_xml;
				if (withAnnotations) {
					native_xml = requestCa.DeCanonise (processId, version, format, cpf_is, anf_is);
				} else {
					native_xml = requestCa.DeCanonise (processId, version, format, cpf_is, null);
				}
				// record meta data in native_xml: process and version names
				InputStream native_xml_sync = 
					copyParam2NPF(native_xml, format, processname, version, owner, null, null);
				DataSource source = new ByteArrayDataSource(native_xml_sync, "text/xml"); 
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
			} catch (JAXBException e) {
				e.printStackTrace();
				result.setCode(-1);
				result.setMessage(e.getMessage());
			} 
		} catch (Exception e) {	
			e.printStackTrace();
			result.setCode(-1);
			result.setMessage(e.getMessage());
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
			String username = payload.getUsername();
			String processName = payload.getProcessName();
			String versionName = payload.getVersionName();
			String nativeType = payload.getNativeType();
			String domain = payload.getDomain();
			String documentation = payload.getDocumentation();
			String creationDate = payload.getCreationDate();
			String lastupdate = payload.getLastUpdate();
			DataHandler handler = payload.getProcessDescription();
			InputStream is =  handler.getInputStream();
			RequestToCanoniser request = new RequestToCanoniser();
			org.apromore.manager.model_portal.ProcessSummaryType process =
				request.CanoniseProcess(username, processName, versionName, 
						nativeType, is, domain, documentation, creationDate, lastupdate);
			res.setProcessSummary(process);
			result.setCode(0);
			result.setMessage("");

		} catch (Exception ex) {
			ex.printStackTrace();
			result.setCode(-1);
			result.setMessage(ex.getMessage());
		}
		return res;
	}


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
			result.setMessage(ex.getMessage());
		}
		return res;
	}

	public ReadNativeTypesOutputMsgType readNativeTypes(ReadNativeTypesInputMsgType payload) { 
		LOG.info("Executing operation readFormats");
		System.out.println(payload);
		ReadNativeTypesOutputMsgType res = new ReadNativeTypesOutputMsgType();
		ResultType result = new ResultType();
		res.setResult(result);
		try {
			RequestToDA request = new RequestToDA();
			NativeTypesType formats = request.ReadNativeTypes();
			result.setCode(0);
			result.setMessage("");
			res.setNativeTypes(formats);
		} catch (Exception ex) {
			ex.printStackTrace();
			result.setCode(-1);
			result.setMessage(ex.getMessage());
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
			result.setMessage(ex.getMessage());   
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
			result.setMessage(ex.getMessage());
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
			result.setMessage(ex.getMessage());
		}
		return res;
	}

	/**
	 * Generate a new npf which is the result of writing parameters in process_xml.
	 * @param process_xml the given npf to be synchronised
	 * @param nativeType npf native type
	 * @param processId id generated by database
	 * @param processName
	 * @param version
	 * @param username
	 * @param creationDate
	 * @param lastUpdate
	 * @return
	 * @throws JAXBException 
	 */
	private InputStream copyParam2NPF(InputStream process_xml,
			String nativeType, String processName,
			String version, String username,
			String lastUpdate, String documentation) throws JAXBException {

		InputStream res = null;
		if (nativeType.compareTo("XPDL 2.1")==0) {
			JAXBContext jc = JAXBContext.newInstance("org.wfmc._2008.xpdl2");
			Unmarshaller u = jc.createUnmarshaller();
			JAXBElement<PackageType> rootElement = (JAXBElement<PackageType>) u.unmarshal(process_xml);
			PackageType pkg = rootElement.getValue();
			copyParam2xpdl (pkg, processName, version, username, lastUpdate, documentation);

			Marshaller m = jc.createMarshaller();
			m.setProperty(javax.xml.bind.Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE );
			ByteArrayOutputStream xpdl_xml = new ByteArrayOutputStream();
			m.marshal(rootElement, xpdl_xml);
			res = new ByteArrayInputStream(xpdl_xml.toByteArray());

		} else if (nativeType.compareTo("EPML 2.0")==0) {
			JAXBContext jc = JAXBContext.newInstance("de.epml");
			Unmarshaller u = jc.createUnmarshaller();
			JAXBElement<TypeEPML> rootElement = (JAXBElement<TypeEPML>) u.unmarshal(process_xml);
			TypeEPML epml = rootElement.getValue();

			// TODO

			Marshaller m = jc.createMarshaller();
			m.setProperty(javax.xml.bind.Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE );
			ByteArrayOutputStream xpdl_xml = new ByteArrayOutputStream();
			m.marshal(rootElement, xpdl_xml);
			res = new ByteArrayInputStream(xpdl_xml.toByteArray());

		}
		return res;
	}

	/**
	 * Modify pkg (npf of type xpdl) with parameters values if not null.
	 * @param pkg
	 * @param processId
	 * @param processName
	 * @param version
	 * @param username
	 * @param creationDate
	 * @param lastUpdate
	 * @param documentation
	 * @return
	 */
	private void copyParam2xpdl(PackageType pkg, 
			String processName, String version, String username,
			String lastUpdate, String documentation) {

		DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH-mm-ss");
		Date date = new Date();
		String creationDate = dateFormat.format(date);
			
		if (pkg.getRedefinableHeader()==null) {
			RedefinableHeader header = new RedefinableHeader();
			pkg.setRedefinableHeader(header);
			Version v = new Version();
			header.setVersion(v);
			Author a = new Author();
			header.setAuthor(a);
		} else {
			if (pkg.getRedefinableHeader().getVersion()==null) {
				Version v = new Version();
				pkg.getRedefinableHeader().setVersion(v);
			}
			if (pkg.getRedefinableHeader().getAuthor()==null) {
				Author a = new Author();
				pkg.getRedefinableHeader().setAuthor(a);
			}
		}
		if (pkg.getPackageHeader()==null) {
			PackageHeader pkgHeader = new PackageHeader();
			pkg.setPackageHeader(pkgHeader);
			Created created = new Created();
			pkgHeader.setCreated(created);
			ModificationDate modifDate = new ModificationDate();
			pkgHeader.setModificationDate(modifDate);
			Documentation doc = new Documentation();
			pkgHeader.setDocumentation(doc);
		} else {
			if (pkg.getPackageHeader().getCreated()==null) {
				Created created = new Created();
				pkg.getPackageHeader().setCreated(created);
			}
			if (pkg.getPackageHeader().getModificationDate()==null) {
				ModificationDate modifDate = new ModificationDate();
				pkg.getPackageHeader().setModificationDate(modifDate);
			}
			if (pkg.getPackageHeader().getDocumentation()==null) {
				Documentation doc = new Documentation();
				pkg.getPackageHeader().setDocumentation(doc);
			}
		}
		if (processName!=null) pkg.setName(processName);
		if (version!=null) pkg.getRedefinableHeader().getVersion().setValue(version);
		if (username!=null) pkg.getRedefinableHeader().getAuthor().setValue(username);
		if (creationDate!=null)	pkg.getPackageHeader().getCreated().setValue(creationDate);
		if (lastUpdate!=null)pkg.getPackageHeader().getModificationDate().setValue(lastUpdate);
		if (documentation!=null)pkg.getPackageHeader().getDocumentation().setValue(documentation);
	}
	
	/**
	 * Generate a cpf uri for version of processId
	 * @param processId
	 * @param version
	 * @return
	 */
	private static String newCpfURI() {
		DateFormat dateFormat = new SimpleDateFormat("yyyyMMddHHmmsSSS");
		Date date = new Date();
		String time = dateFormat.format(date);
		return time;
	}
}
