package org.apromore.manager.service;

import de.epml.TypeEPML;
import org.apromore.common.Constants;
import org.apromore.exception.ExceptionCanoniseVersion;
import org.apromore.exception.ExceptionDeCanonise;
import org.apromore.exception.ExceptionReadCanonicalAnf;
import org.apromore.exception.ExceptionReadNative;
import org.apromore.exception.ExceptionVersion;
import org.apromore.manager.canoniser.ManagerCanoniserClient;
import org.apromore.manager.da.ManagerDataAccessClient;
import org.apromore.manager.toolbox.ManagerToolboxClient;
import org.apromore.model.DeleteEditSessionInputMsgType;
import org.apromore.model.DeleteEditSessionOutputMsgType;
import org.apromore.model.DeleteProcessVersionsInputMsgType;
import org.apromore.model.DeleteProcessVersionsOutputMsgType;
import org.apromore.model.DomainsType;
import org.apromore.model.EditProcessDataInputMsgType;
import org.apromore.model.EditProcessDataOutputMsgType;
import org.apromore.model.EditSessionType;
import org.apromore.model.ExportFormatInputMsgType;
import org.apromore.model.ExportFormatOutputMsgType;
import org.apromore.model.ImportProcessInputMsgType;
import org.apromore.model.ImportProcessOutputMsgType;
import org.apromore.model.MergeProcessesInputMsgType;
import org.apromore.model.MergeProcessesOutputMsgType;
import org.apromore.model.NativeTypesType;
import org.apromore.model.ObjectFactory;
import org.apromore.model.ParameterType;
import org.apromore.model.ParametersType;
import org.apromore.model.ProcessSummariesType;
import org.apromore.model.ProcessSummaryType;
import org.apromore.model.ProcessVersionIdType;
import org.apromore.model.ProcessVersionIdentifierType;
import org.apromore.model.ProcessVersionIdsType;
import org.apromore.model.ReadAllUsersInputMsgType;
import org.apromore.model.ReadAllUsersOutputMsgType;
import org.apromore.model.ReadDomainsInputMsgType;
import org.apromore.model.ReadDomainsOutputMsgType;
import org.apromore.model.ReadEditSessionInputMsgType;
import org.apromore.model.ReadEditSessionOutputMsgType;
import org.apromore.model.ReadNativeTypesInputMsgType;
import org.apromore.model.ReadNativeTypesOutputMsgType;
import org.apromore.model.ReadProcessSummariesInputMsgType;
import org.apromore.model.ReadProcessSummariesOutputMsgType;
import org.apromore.model.ReadUserInputMsgType;
import org.apromore.model.ReadUserOutputMsgType;
import org.apromore.model.ResultType;
import org.apromore.model.SearchForSimilarProcessesInputMsgType;
import org.apromore.model.SearchForSimilarProcessesOutputMsgType;
import org.apromore.model.UpdateProcessInputMsgType;
import org.apromore.model.UpdateProcessOutputMsgType;
import org.apromore.model.UserType;
import org.apromore.model.UsernamesType;
import org.apromore.model.WriteAnnotationInputMsgType;
import org.apromore.model.WriteAnnotationOutputMsgType;
import org.apromore.model.WriteEditSessionInputMsgType;
import org.apromore.model.WriteEditSessionOutputMsgType;
import org.apromore.model.WriteUserInputMsgType;
import org.apromore.model.WriteUserOutputMsgType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ws.server.endpoint.annotation.Endpoint;
import org.springframework.ws.server.endpoint.annotation.PayloadRoot;
import org.springframework.ws.server.endpoint.annotation.RequestPayload;
import org.springframework.ws.server.endpoint.annotation.ResponsePayload;
import org.wfmc._2008.xpdl2.Author;
import org.wfmc._2008.xpdl2.Created;
import org.wfmc._2008.xpdl2.Documentation;
import org.wfmc._2008.xpdl2.ModificationDate;
import org.wfmc._2008.xpdl2.PackageHeader;
import org.wfmc._2008.xpdl2.PackageType;
import org.wfmc._2008.xpdl2.RedefinableHeader;
import org.wfmc._2008.xpdl2.Version;

import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.mail.util.ByteArrayDataSource;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
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

/**
 * The WebService Endpoint Used by the Portal.
 *
 * This is the only web service available in this application.
 */
@Endpoint
public class ManagerPortalEndpoint {

    private static final Logger LOG = Logger.getLogger(ManagerPortalEndpoint.class.getName());

    private static final String NAMESPACE = "urn:qut-edu-au:schema:apromore:manager";

    @Autowired
    private ManagerDataAccessClient daClient;
    @Autowired
    private ManagerCanoniserClient caClient;
    @Autowired
    private ManagerToolboxClient tbClient;

    @PayloadRoot(namespace = NAMESPACE, localPart = "EditProcessDataRequest")
    @ResponsePayload
    public JAXBElement<EditProcessDataOutputMsgType> editProcessData(@RequestPayload JAXBElement<EditProcessDataInputMsgType> req) {
        LOG.info("Executing operation editDataProcess");
        EditProcessDataInputMsgType payload = req.getValue();
        System.out.println(payload);
        EditProcessDataOutputMsgType res = new EditProcessDataOutputMsgType();
        ResultType result = new ResultType();
        res.setResult(result);
        try {
            Integer processId = payload.getId();
            String processName = payload.getProcessName();
            String domain = payload.getDomain();
            String username = payload.getOwner();
            String preVersion = payload.getPreName();
            String newVersion = payload.getNewName();
            String ranking = payload.getRanking();
            daClient.EditProcessData(processId, processName, domain, username, preVersion, newVersion, ranking);
            result.setCode(0);
            result.setMessage("");
        } catch (Exception ex) {
            ex.printStackTrace();
            result.setCode(-1);
            result.setMessage(ex.getMessage());
        }
        return new ObjectFactory().createEditProcessDataResponse(res);
    }

    @PayloadRoot(localPart = "MergeProcessesRequest", namespace = NAMESPACE)
    @ResponsePayload
    public JAXBElement<MergeProcessesOutputMsgType> mergeProcesses(@RequestPayload JAXBElement<MergeProcessesInputMsgType> req) {
        LOG.info("Executing operation mergeProcesses");
        MergeProcessesInputMsgType payload = req.getValue();
        System.out.println(payload);
        MergeProcessesOutputMsgType res = new MergeProcessesOutputMsgType();
        ResultType result = new ResultType();
        res.setResult(result);
        try {
            // Build data to send to toolbox
            String algo = payload.getAlgorithm();
            String processName = payload.getProcessName();
            String version = payload.getVersionName();
            String domain = payload.getDomain();
            Integer processId = payload.getProcessId();
            String username = payload.getUsername();
            ParametersType parameters = new ParametersType();
            for (ParameterType p : payload.getParameters().getParameter()) {
                ParameterType param = new ParameterType();
                param.setName(p.getName());
                param.setValue(p.getValue());
                parameters.getParameter().add(param);
            }
            // processes
            ProcessVersionIdsType ids = new ProcessVersionIdsType();
            for (ProcessVersionIdType t : payload.getProcessVersionIds().getProcessVersionId()) {
                ProcessVersionIdType id = new ProcessVersionIdType();
                id.setProcessId(t.getProcessId());
                id.setVersionName(t.getVersionName());
                ids.getProcessVersionId().add(id);
            }
            ProcessSummaryType respFromToolbox = tbClient.MergeProcesses(processName, version, domain, username, algo, parameters, ids);
            res.setProcessSummary(respFromToolbox);
            result.setCode(0);
            result.setMessage("");
        } catch (Exception ex) {
            ex.printStackTrace();
            result.setCode(-1);
            result.setMessage(ex.getMessage());
        }
        return new ObjectFactory().createMergeProcessesResponse(res);
    }


    /* (non-Javadoc)
      * @see org.apromore.manager.service_portal1.ManagerPortalPortType#searchForSimilarProcesses(SearchForSimilarProcessesInputMsgType  payload )*
      */
    @PayloadRoot(localPart = "SearchForSimilarProcessesRequest", namespace = NAMESPACE)
    @ResponsePayload
    public JAXBElement<SearchForSimilarProcessesOutputMsgType> searchForSimilarProcesses(
            @RequestPayload JAXBElement<SearchForSimilarProcessesInputMsgType> req) {
        LOG.info("Executing operation searchForSimilarProcesses");
        SearchForSimilarProcessesInputMsgType payload = req.getValue();
        System.out.println(payload);
        SearchForSimilarProcessesOutputMsgType res = new SearchForSimilarProcessesOutputMsgType();
        ResultType result = new ResultType();
        res.setResult(result);
        try {
            String algo = payload.getAlgorithm();
            Integer processId = payload.getProcessId();
            String versionName = payload.getVersionName();
            Boolean latestVersions = payload.isLatestVersions();
            ParametersType paramsT = new ParametersType();
            for (ParameterType p : payload.getParameters().getParameter()) {
                ParameterType paramT = new ParameterType();
                paramsT.getParameter().add(paramT);
                paramT.setName(p.getName());
                paramT.setValue(p.getValue());
            }
            ProcessSummariesType processes = tbClient.SearchForSimilarProcesses(processId, versionName, latestVersions, algo, paramsT);
            res.setProcessSummaries(processes);
            result.setCode(0);
            result.setMessage("");
        } catch (Exception ex) {
            ex.printStackTrace();
            result.setCode(-1);
            result.setMessage(ex.getMessage());
        }
        return new ObjectFactory().createSearchForSimilarProcessesResponse(res);
    }

    @PayloadRoot(localPart = "WriteAnnotationRequest", namespace = NAMESPACE)
    @ResponsePayload
    public JAXBElement<WriteAnnotationOutputMsgType> writeAnnotation(@RequestPayload JAXBElement<WriteAnnotationInputMsgType> req) {
        LOG.info("Executing operation writeAnnotation");
        WriteAnnotationInputMsgType payload = req.getValue();
        System.out.println(payload);
        WriteAnnotationOutputMsgType res = new WriteAnnotationOutputMsgType();
        ResultType result = new ResultType();
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
            caClient.GenerateAnnotation(annotName, editSessionCode, isNew, processId, version, nat_type, native_is);
            result.setCode(0);
            result.setMessage("");
        } catch (Exception ex) {
            ex.printStackTrace();
            result.setCode(-1);
            result.setMessage(ex.getMessage());
        }
        return new ObjectFactory().createWriteAnnotationResponse(res);
    }

    @PayloadRoot(namespace = NAMESPACE, localPart = "ReadAllUsersRequest")
    @ResponsePayload
    public JAXBElement<ReadAllUsersOutputMsgType> readAllUsers(@RequestPayload final JAXBElement<ReadAllUsersInputMsgType> message) {
        LOG.info("Executing operation readAllUsers");
        ReadAllUsersInputMsgType payload = message.getValue();
        System.out.println(payload);
        ReadAllUsersOutputMsgType res = new ReadAllUsersOutputMsgType();
        ResultType result = new ResultType();
        res.setResult(result);
        try {
            UsernamesType allUsers = daClient.ReadAllUsers();
            res.setUsernames(allUsers);
            result.setCode(0);
            result.setMessage("");
        } catch (Exception ex) {
            ex.printStackTrace();
            result.setCode(-1);
            result.setMessage(ex.getMessage());
        }
        return new ObjectFactory().createReadAllUsersResponse(res);
    }

    @PayloadRoot(localPart = "DeleteEditSessionRequest", namespace = NAMESPACE)
    @ResponsePayload
    public JAXBElement<DeleteEditSessionOutputMsgType> deleteEditSession(@RequestPayload JAXBElement<DeleteEditSessionInputMsgType> req) {
        LOG.info("Executing operation deleteEditSession");
        DeleteEditSessionInputMsgType payload = req.getValue();
        System.out.println(payload);
        DeleteEditSessionOutputMsgType res = new DeleteEditSessionOutputMsgType();
        ResultType result = new ResultType();
        res.setResult(result);
        int code = payload.getEditSessionCode();
        try {
            daClient.DeleteEditSession(code);
            result.setCode(0);
            result.setMessage("");
        } catch (Exception ex) {
            ex.printStackTrace();
            result.setCode(-1);
            result.setMessage(ex.getMessage());
        }
        return new ObjectFactory().createDeleteEditSessionResponse(res);
    }

    @PayloadRoot(localPart = "DeleteProcessVersionsRequest", namespace = NAMESPACE)
    @ResponsePayload
    public JAXBElement<DeleteProcessVersionsOutputMsgType> deleteProcessVersions(
            @RequestPayload JAXBElement<DeleteProcessVersionsInputMsgType> req) {
        LOG.info("Executing operation deleteProcessVersions");
        DeleteProcessVersionsInputMsgType payload = req.getValue();
        System.out.println(payload);
        DeleteProcessVersionsOutputMsgType res = new DeleteProcessVersionsOutputMsgType();
        ResultType result = new ResultType();
        res.setResult(result);
        List<ProcessVersionIdentifierType> processesP = payload.getProcessVersionIdentifier();
        try {
            List<ProcessVersionIdentifierType> processesDa = new ArrayList<ProcessVersionIdentifierType>();
            Iterator<ProcessVersionIdentifierType> it = processesP.iterator();
            while (it.hasNext()) {
                ProcessVersionIdentifierType processP = it.next();
                ProcessVersionIdentifierType processDa = new ProcessVersionIdentifierType();
                processDa.setProcessid(processP.getProcessid());
                processDa.getVersionName().addAll(processP.getVersionName());
                processesDa.add(processDa);
            }
            daClient.DeleteProcessVersion(processesDa);
            result.setCode(0);
            result.setMessage("");
        } catch (Exception ex) {
            ex.printStackTrace();
            result.setCode(-1);
            result.setMessage(ex.getMessage());
        }
        return new ObjectFactory().createDeleteProcessVersionsResponse(res);
    }


    @PayloadRoot(localPart = "UpdateProcessRequest", namespace = NAMESPACE)
    @ResponsePayload
    public JAXBElement<UpdateProcessOutputMsgType> updateProcess(@RequestPayload JAXBElement<UpdateProcessInputMsgType> req) {
        LOG.info("Executing operation updateProcess");
        UpdateProcessInputMsgType payload = req.getValue();
        System.out.println(payload);
        UpdateProcessOutputMsgType res = new UpdateProcessOutputMsgType();
        ResultType result = new ResultType();
        res.setResult(result);
        try {
            DataHandler handler = payload.getNative();
            InputStream native_is = handler.getInputStream();
            int editSessionCode = payload.getEditSessionCode();
            EditSessionType editSessionP = payload.getEditSession();
            EditSessionType editSessionC = new EditSessionType();
            editSessionC.setProcessId(editSessionP.getProcessId());
            editSessionC.setNativeType(editSessionP.getNativeType());
            editSessionC.setAnnotation(editSessionP.getAnnotation());
            editSessionC.setCreationDate(editSessionP.getCreationDate());
            editSessionC.setLastUpdate(editSessionP.getLastUpdate());
            editSessionC.setProcessName(editSessionP.getProcessName());
            editSessionC.setUsername(editSessionP.getUsername());
            editSessionC.setVersionName(editSessionP.getVersionName());
            caClient.CanoniseVersion(editSessionCode, editSessionC, newCpfURI(), native_is);
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
        }
        return new ObjectFactory().createUpdateProcessResponse(res);
    }


    @PayloadRoot(localPart = "ReadEditSessionRequest", namespace = NAMESPACE)
    @ResponsePayload
    public JAXBElement<ReadEditSessionOutputMsgType> readEditSession(@RequestPayload JAXBElement<ReadEditSessionInputMsgType> req) {
        LOG.info("Executing operation readEditSession");
        ReadEditSessionInputMsgType payload = req.getValue();
        System.out.println(payload);
        ReadEditSessionOutputMsgType res = new ReadEditSessionOutputMsgType();
        ResultType result = new ResultType();
        res.setResult(result);
        int code = payload.getEditSessionCode();
        try {
            EditSessionType editSessionDA = daClient.ReadEditSession(code);
            EditSessionType editSessionP = new EditSessionType();
            editSessionP.setNativeType(editSessionDA.getNativeType());
            editSessionP.setProcessId(editSessionDA.getProcessId());
            editSessionP.setUsername(editSessionDA.getUsername());
            editSessionP.setVersionName(editSessionDA.getVersionName());
            editSessionP.setProcessName(editSessionDA.getProcessName());
            editSessionP.setDomain(editSessionDA.getDomain());
            editSessionP.setCreationDate(editSessionDA.getCreationDate());
            editSessionP.setLastUpdate(editSessionDA.getLastUpdate());
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
        return new ObjectFactory().createReadEditSessionResponse(res);
    }

    @PayloadRoot(localPart = "WriteEditSessionRequest", namespace = NAMESPACE)
    @ResponsePayload
    public JAXBElement<WriteEditSessionOutputMsgType> writeEditSession(@RequestPayload JAXBElement<WriteEditSessionInputMsgType> req) {
        LOG.info("Executing operation writeEditSession");
        WriteEditSessionInputMsgType payload = req.getValue();
        System.out.println(payload);
        WriteEditSessionOutputMsgType res = new WriteEditSessionOutputMsgType();
        ResultType result = new ResultType();
        res.setResult(result);
        EditSessionType editSessionP = payload.getEditSession();
        EditSessionType editSessionDA = new EditSessionType();
        editSessionDA.setNativeType(editSessionP.getNativeType());
        editSessionDA.setProcessId(editSessionP.getProcessId());
        editSessionDA.setUsername(editSessionP.getUsername());
        editSessionDA.setVersionName(editSessionP.getVersionName());
        editSessionDA.setProcessName(editSessionP.getProcessName());
        editSessionDA.setDomain(editSessionP.getDomain());
        editSessionDA.setCreationDate(editSessionP.getCreationDate());
        editSessionDA.setLastUpdate(editSessionP.getLastUpdate());
        editSessionDA.setWithAnnotation(editSessionP.isWithAnnotation());
        editSessionDA.setAnnotation(editSessionP.getAnnotation());
        try {
            int code = daClient.WriteEditSession(editSessionDA);

            res.setEditSessionCode(code);
            result.setCode(0);
            result.setMessage("");
        } catch (Exception ex) {
            ex.printStackTrace();
            result.setCode(-1);
            result.setMessage(ex.getMessage());
        }
        return new ObjectFactory().createWriteEditSessionResponse(res);
    }

    @PayloadRoot(localPart = "ExportFormatRequest", namespace = NAMESPACE)
    @ResponsePayload
    public JAXBElement<ExportFormatOutputMsgType> exportFormat(@RequestPayload JAXBElement<ExportFormatInputMsgType> req) {
        LOG.info("Executing operation exportFormat");
        ExportFormatInputMsgType payload = req.getValue();
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
            if ((withAnnotations && annotationName.compareTo(Constants.INITIAL_ANNOTATIONS) == 0)
                    || Constants.CANONICAL.compareTo(format) == 0
                    || format.startsWith(Constants.ANNOTATIONS)) {
                InputStream native_xml = daClient.ReadFormat(processId, version, format);
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
                daClient.ReadCanonicalAnf(processId, version, withAnnotations, annotationName);
                InputStream cpf_is = daClient.getCpf();
                InputStream anf_is = daClient.getAnf();
                // TODO temporary to test de-canoniser with and without annotations
                // TODO: annotations might be unavailable!
                InputStream native_xml;
                if (withAnnotations) {
                    native_xml = caClient.DeCanonise(processId, version, format, cpf_is, anf_is);
                } else {
                    native_xml = caClient.DeCanonise(processId, version, format, cpf_is, null);
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
        return new ObjectFactory().createExportFormatResponse(res);
    }

    @PayloadRoot(localPart = "ImportProcessRequest", namespace = NAMESPACE)
    @ResponsePayload
    public JAXBElement<ImportProcessOutputMsgType> importProcess(@RequestPayload JAXBElement<ImportProcessInputMsgType> req) {
        LOG.info("Executing operation importProcess");
        ImportProcessInputMsgType payload = req.getValue();
        System.out.println(payload);
        ImportProcessOutputMsgType res = new ImportProcessOutputMsgType();
        ResultType result = new ResultType();
        res.setResult(result);

        try {
            EditSessionType editSession = payload.getEditSession();
            String username = editSession.getUsername();
            String processName = editSession.getProcessName();
            String versionName = editSession.getVersionName();
            String nativeType = editSession.getNativeType();
            String domain = editSession.getDomain();
            String creationDate = editSession.getCreationDate();
            String lastupdate = editSession.getLastUpdate();
            Boolean addFakeEvents = payload.isAddFakeEvents();
            DataHandler handler = payload.getProcessDescription();
            InputStream is = handler.getInputStream();
            ProcessSummaryType process =
                    caClient.CanoniseProcess(username, processName, newCpfURI(),
                            versionName, nativeType, is, domain, "", creationDate, lastupdate, addFakeEvents);
            res.setProcessSummary(process);
            result.setCode(0);
            result.setMessage("");

        } catch (Exception ex) {
            ex.printStackTrace();
            result.setCode(-1);
            result.setMessage(ex.getMessage());
        }
        return new ObjectFactory().createImportProcessResponse(res);
    }


    @PayloadRoot(localPart = "WriteUserRequest", namespace = NAMESPACE)
    @ResponsePayload
    public JAXBElement<WriteUserOutputMsgType> writeUser(@RequestPayload JAXBElement<WriteUserInputMsgType> req) {
        LOG.info("Executing operation writeUser");
        WriteUserInputMsgType payload = req.getValue();
        System.out.println(payload);

        WriteUserOutputMsgType res = new WriteUserOutputMsgType();
        ResultType result = new ResultType();
        res.setResult(result);
        UserType user = payload.getUser();
        try {
            daClient.WriteUser(user);
            result.setCode(0);
            result.setMessage("");
        } catch (Exception ex) {
            ex.printStackTrace();
            result.setCode(0);
            result.setMessage(ex.getMessage());
        }
        return new ObjectFactory().createWriteUserResponse(res);
    }

    @PayloadRoot(localPart = "ReadNativeTypesRequest", namespace = NAMESPACE)
    @ResponsePayload
    public JAXBElement<ReadNativeTypesOutputMsgType> readNativeTypes(@RequestPayload JAXBElement<ReadNativeTypesInputMsgType> req) {
        LOG.info("Executing operation readFormats");
        ReadNativeTypesInputMsgType payload = req.getValue();
        System.out.println(payload);
        ReadNativeTypesOutputMsgType res = new ReadNativeTypesOutputMsgType();
        ResultType result = new ResultType();
        res.setResult(result);
        try {
            NativeTypesType formats = daClient.ReadNativeTypes();
            result.setCode(0);
            result.setMessage("");
            res.setNativeTypes(formats);
        } catch (Exception ex) {
            ex.printStackTrace();
            result.setCode(-1);
            result.setMessage(ex.getMessage());
        }
        return new ObjectFactory().createReadNativeTypesResponse(res);
    }

    /* (non-Javadoc)
      * @see org.apromore.manager.service.ManagerPortalPortType#readDomains(ReadDomainsInputMsgType  payload )*
      */
    @PayloadRoot(localPart = "ReadDomainsRequest", namespace = NAMESPACE)
    @ResponsePayload
    public JAXBElement<ReadDomainsOutputMsgType> readDomains(@RequestPayload JAXBElement<ReadDomainsInputMsgType> req) {
        LOG.info("Executing operation readDomains");
        ReadDomainsInputMsgType payload = req.getValue();
        System.out.println(payload);

        ReadDomainsOutputMsgType res = new ReadDomainsOutputMsgType();
        ResultType result = new ResultType();
        res.setResult(result);
        try {
            DomainsType domains = daClient.ReadDomains();
            result.setCode(0);
            result.setMessage("");
            res.setDomains(domains);
        } catch (Exception ex) {
            ex.printStackTrace();
            result.setCode(-1);
            result.setMessage(ex.getMessage());
        }
        return new ObjectFactory().createReadDomainsResponse(res);
    }

    /* (non-Javadoc)
      * @see org.apromore.manager.service.ManagerPortalPortType#readUser(ReadUserInputMsgType  payload )*
      */
    @PayloadRoot(localPart = "ReadUserRequest", namespace = NAMESPACE)
    @ResponsePayload
    public JAXBElement<ReadUserOutputMsgType> readUser(@RequestPayload ReadUserInputMsgType payload) {
        LOG.info("Executing operation readUser");
        System.out.println(payload);
        ReadUserOutputMsgType res = new ReadUserOutputMsgType();
        ResultType result = new ResultType();
        res.setResult(result);
        try {
            UserType user = daClient.ReadUser(payload.getUsername());
            result.setCode(0);
            result.setMessage("");
            res.setUser(user);
        } catch (Exception ex) {
            ex.printStackTrace();
            result.setCode(-1);
            result.setMessage(ex.getMessage());
        }
        return new ObjectFactory().createReadUserResponse(res);
    }

    /* (non-Javadoc)
      * @see org.apromore.manager.service.ManagerPortalPortType#readProcessSummaries(ReadProcessSummariesInputMsgType  payload )*
      */
    @PayloadRoot(localPart = "ReadProcessSummariesRequest", namespace = NAMESPACE)
    @ResponsePayload
    public JAXBElement<ReadProcessSummariesOutputMsgType> readProcessSummaries(
            @RequestPayload JAXBElement<ReadProcessSummariesInputMsgType> req) {
        LOG.info("Executing operation readProcessSummaries");
        ReadProcessSummariesInputMsgType payload = req.getValue();
        System.out.println(payload);

        ReadProcessSummariesOutputMsgType res = new ReadProcessSummariesOutputMsgType();
        ResultType result = new ResultType();
        res.setResult(result);

        try {
            ProcessSummariesType processes = daClient.ReadProcessSummaries(payload.getSearchExpression());
            result.setCode(0);
            result.setMessage("");
            res.setProcessSummaries(processes);
        } catch (Exception ex) {
            ex.printStackTrace();
            result.setCode(-1);
            result.setMessage(ex.getMessage());
        }
        return new ObjectFactory().createReadProcessSummariesResponse(res);
    }

    /**
     * Generate a new npf which is the result of writing parameters in process_xml.
     *
     * @param process_xml  the given npf to be synchronised
     * @param nativeType   npf native type
     * @param processName
     * @param version
     * @param username
     * @param lastUpdate
     * @return
     * @throws javax.xml.bind.JAXBException
     */
    private InputStream copyParam2NPF(InputStream process_xml,
                                      String nativeType, String processName,
                                      String version, String username,
                                      String lastUpdate, String documentation) throws JAXBException {

        InputStream res = null;
        if (nativeType.compareTo("XPDL 2.1") == 0) {
            JAXBContext jc = JAXBContext.newInstance("org.wfmc._2008.xpdl2");
            Unmarshaller u = jc.createUnmarshaller();
            JAXBElement<PackageType> rootElement = (JAXBElement<PackageType>) u.unmarshal(process_xml);
            PackageType pkg = rootElement.getValue();
            copyParam2xpdl(pkg, processName, version, username, lastUpdate, documentation);

            Marshaller m = jc.createMarshaller();
            m.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
            ByteArrayOutputStream xpdl_xml = new ByteArrayOutputStream();
            m.marshal(rootElement, xpdl_xml);
            res = new ByteArrayInputStream(xpdl_xml.toByteArray());

        } else if (nativeType.compareTo("EPML 2.0") == 0) {
            JAXBContext jc = JAXBContext.newInstance("de.epml");
            Unmarshaller u = jc.createUnmarshaller();
            JAXBElement<TypeEPML> rootElement = (JAXBElement<TypeEPML>) u.unmarshal(process_xml);
            TypeEPML epml = rootElement.getValue();

            // TODO

            Marshaller m = jc.createMarshaller();
            m.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
            ByteArrayOutputStream xpdl_xml = new ByteArrayOutputStream();
            m.marshal(rootElement, xpdl_xml);
            res = new ByteArrayInputStream(xpdl_xml.toByteArray());

        }
        return res;
    }

    /**
     * Modify pkg (npf of type xpdl) with parameters values if not null.
     *
     * @param pkg
     * @param processName
     * @param version
     * @param username
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

        if (pkg.getRedefinableHeader() == null) {
            RedefinableHeader header = new RedefinableHeader();
            pkg.setRedefinableHeader(header);
            Version v = new Version();
            header.setVersion(v);
            Author a = new Author();
            header.setAuthor(a);
        } else {
            if (pkg.getRedefinableHeader().getVersion() == null) {
                Version v = new Version();
                pkg.getRedefinableHeader().setVersion(v);
            }
            if (pkg.getRedefinableHeader().getAuthor() == null) {
                Author a = new Author();
                pkg.getRedefinableHeader().setAuthor(a);
            }
        }
        if (pkg.getPackageHeader() == null) {
            PackageHeader pkgHeader = new PackageHeader();
            pkg.setPackageHeader(pkgHeader);
            Created created = new Created();
            pkgHeader.setCreated(created);
            ModificationDate modifDate = new ModificationDate();
            pkgHeader.setModificationDate(modifDate);
            Documentation doc = new Documentation();
            pkgHeader.setDocumentation(doc);
        } else {
            if (pkg.getPackageHeader().getCreated() == null) {
                Created created = new Created();
                pkg.getPackageHeader().setCreated(created);
            }
            if (pkg.getPackageHeader().getModificationDate() == null) {
                ModificationDate modifDate = new ModificationDate();
                pkg.getPackageHeader().setModificationDate(modifDate);
            }
            if (pkg.getPackageHeader().getDocumentation() == null) {
                Documentation doc = new Documentation();
                pkg.getPackageHeader().setDocumentation(doc);
            }
        }
        if (processName != null) pkg.setName(processName);
        if (version != null) pkg.getRedefinableHeader().getVersion().setValue(version);
        if (username != null) pkg.getRedefinableHeader().getAuthor().setValue(username);
        if (creationDate != null) pkg.getPackageHeader().getCreated().setValue(creationDate);
        if (lastUpdate != null) pkg.getPackageHeader().getModificationDate().setValue(lastUpdate);
        if (documentation != null) pkg.getPackageHeader().getDocumentation().setValue(documentation);
    }

    /**
     * Generate a cpf uri for version of processId
     */
    private static String newCpfURI() {
        DateFormat dateFormat = new SimpleDateFormat("yyyyMMddHHmmsSSS");
        Date date = new Date();
        String time = dateFormat.format(date);
        return time;
    }



    public void setDaClient(ManagerDataAccessClient daClient) {
        this.daClient = daClient;
    }

    public void setTbClient(ManagerToolboxClient tbClient) {
        this.tbClient = tbClient;
    }

    public void setCaClient(ManagerCanoniserClient caClient) {
        this.caClient = caClient;
    }
}
