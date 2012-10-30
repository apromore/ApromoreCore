package org.apromore.manager.service;

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
import java.util.Map;
import java.util.Set;
import javax.activation.DataHandler;
import javax.mail.util.ByteArrayDataSource;
import javax.xml.bind.JAXBElement;

import org.apromore.canoniser.Canoniser;
import org.apromore.canoniser.result.CanoniserMetadataResult;
import org.apromore.cpf.CanonicalProcessType;
import org.apromore.dao.model.Cluster;
import org.apromore.dao.model.ClusteringSummary;
import org.apromore.dao.model.EditSession;
import org.apromore.exception.ExceptionCanoniseVersion;
import org.apromore.exception.ExceptionVersion;
import org.apromore.exception.ExportFormatException;
import org.apromore.exception.RepositoryException;
import org.apromore.service.CanonicalConverter;
import org.apromore.graph.canonical.Canonical;
import org.apromore.manager.canoniser.ManagerCanoniserClient;
import org.apromore.manager.client.helper.CanoniserHelper;
import org.apromore.manager.client.helper.PluginHelper;
import org.apromore.mapper.ClusterMapper;
import org.apromore.mapper.DomainMapper;
import org.apromore.mapper.NativeTypeMapper;
import org.apromore.mapper.UserMapper;
import org.apromore.model.ClusterSummaryType;
import org.apromore.model.ClusterType;
import org.apromore.model.ClusteringSummaryType;
import org.apromore.model.CreateClustersInputMsgType;
import org.apromore.model.CreateClustersOutputMsgType;
import org.apromore.model.DeleteEditSessionInputMsgType;
import org.apromore.model.DeleteEditSessionOutputMsgType;
import org.apromore.model.DeleteProcessVersionsInputMsgType;
import org.apromore.model.DeleteProcessVersionsOutputMsgType;
import org.apromore.model.DeployProcessInputMsgType;
import org.apromore.model.DeployProcessOutputMsgType;
import org.apromore.model.DomainsType;
import org.apromore.model.EditProcessDataInputMsgType;
import org.apromore.model.EditProcessDataOutputMsgType;
import org.apromore.model.EditSessionType;
import org.apromore.model.ExportFormatInputMsgType;
import org.apromore.model.ExportFormatOutputMsgType;
import org.apromore.model.ExportFormatResultType;
import org.apromore.model.FragmentResponseType;
import org.apromore.model.FragmentType;
import org.apromore.model.GetClusterInputMsgType;
import org.apromore.model.GetClusterOutputMsgType;
import org.apromore.model.GetClusterSummariesInputMsgType;
import org.apromore.model.GetClusterSummariesOutputMsgType;
import org.apromore.model.GetClusteringSummaryInputMsgType;
import org.apromore.model.GetClusteringSummaryOutputMsgType;
import org.apromore.model.GetClustersRequestType;
import org.apromore.model.GetClustersResponseType;
import org.apromore.model.GetFragmentRequestType;
import org.apromore.model.GetPairwiseDistancesInputMsgType;
import org.apromore.model.GetPairwiseDistancesOutputMsgType;
import org.apromore.model.ImportProcessInputMsgType;
import org.apromore.model.ImportProcessOutputMsgType;
import org.apromore.model.ImportProcessResultType;
import org.apromore.model.MergeProcessesInputMsgType;
import org.apromore.model.MergeProcessesOutputMsgType;
import org.apromore.model.NativeMetaData;
import org.apromore.model.NativeTypesType;
import org.apromore.model.ObjectFactory;
import org.apromore.model.PairDistancesType;
import org.apromore.model.ParameterType;
import org.apromore.model.ParametersType;
import org.apromore.model.PluginInfo;
import org.apromore.model.PluginInfoResult;
import org.apromore.model.PluginParameters;
import org.apromore.model.ProcessSummariesType;
import org.apromore.model.ProcessSummaryType;
import org.apromore.model.ProcessVersionIdType;
import org.apromore.model.ProcessVersionIdentifierType;
import org.apromore.model.ProcessVersionIdsType;
import org.apromore.model.ReadAllUsersInputMsgType;
import org.apromore.model.ReadAllUsersOutputMsgType;
import org.apromore.model.ReadCanoniserInfoInputMsgType;
import org.apromore.model.ReadCanoniserInfoOutputMsgType;
import org.apromore.model.ReadDeploymentPluginInfoInputMsgType;
import org.apromore.model.ReadDeploymentPluginInfoOutputMsgType;
import org.apromore.model.ReadDomainsInputMsgType;
import org.apromore.model.ReadDomainsOutputMsgType;
import org.apromore.model.ReadEditSessionInputMsgType;
import org.apromore.model.ReadEditSessionOutputMsgType;
import org.apromore.model.ReadInitialNativeFormatInputMsgType;
import org.apromore.model.ReadInitialNativeFormatOutputMsgType;
import org.apromore.model.ReadInstalledPluginsInputMsgType;
import org.apromore.model.ReadInstalledPluginsOutputMsgType;
import org.apromore.model.ReadNativeMetaDataInputMsgType;
import org.apromore.model.ReadNativeMetaDataOutputMsgType;
import org.apromore.model.ReadNativeTypesInputMsgType;
import org.apromore.model.ReadNativeTypesOutputMsgType;
import org.apromore.model.ReadPluginInfoInputMsgType;
import org.apromore.model.ReadPluginInfoOutputMsgType;
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
import org.apromore.plugin.ParameterAwarePlugin;
import org.apromore.plugin.Plugin;
import org.apromore.plugin.deployment.DeploymentPlugin;
import org.apromore.plugin.impl.PluginRequestImpl;
import org.apromore.plugin.message.PluginMessage;
import org.apromore.plugin.property.RequestParameterType;
import org.apromore.service.CanoniserService;
import org.apromore.service.ClusterService;
import org.apromore.service.DeploymentService;
import org.apromore.service.DomainService;
import org.apromore.service.FormatService;
import org.apromore.service.FragmentService;
import org.apromore.service.MergeService;
import org.apromore.service.PluginService;
import org.apromore.service.ProcessService;
import org.apromore.service.RepositoryService;
import org.apromore.service.SessionService;
import org.apromore.service.SimilarityService;
import org.apromore.service.UserService;
import org.apromore.service.model.CanonisedProcess;
import org.apromore.service.model.ClusterFilter;
import org.apromore.service.model.ClusterSettings;
import org.apromore.service.model.NameValuePair;
import org.apromore.toolbox.clustering.algorithms.dbscan.FragmentPair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.ws.server.endpoint.annotation.Endpoint;
import org.springframework.ws.server.endpoint.annotation.PayloadRoot;
import org.springframework.ws.server.endpoint.annotation.RequestPayload;
import org.springframework.ws.server.endpoint.annotation.ResponsePayload;

/**
 * The WebService Endpoint Used by the Portal.
 * <p/>
 * This is the only web service available in this application.
 */
@Endpoint
public class ManagerPortalEndpoint {

    private static final Logger LOGGER = LoggerFactory.getLogger(ManagerPortalEndpoint.class.getName());

    private static final ObjectFactory WS_OBJECT_FACTORY = new ObjectFactory();

    private static final String NAMESPACE = "urn:qut-edu-au:schema:apromore:manager";

    @Autowired @Qualifier("DeploymentService")
    private DeploymentService deploymentService;
    @Autowired @Qualifier("PluginService")
    private PluginService pluginService;
    @Autowired @Qualifier("FragmentService")
    private FragmentService fragmentSrv;
    @Autowired @Qualifier("CanoniserService")
    private CanoniserService canoniserService;
    @Autowired @Qualifier("ProcessService")
    private ProcessService procSrv;
    @Autowired @Qualifier("RepositoryService")
    private RepositoryService repSrv;
    @Autowired @Qualifier("ClusterService")
    private ClusterService clusterService;
    @Autowired @Qualifier("FormatService")
    private FormatService frmSrv;
    @Autowired @Qualifier("DomainService")
    private DomainService domSrv;
    @Autowired @Qualifier("UserService")
    private UserService userSrv;
    @Autowired @Qualifier("SimilarityService")
    private SimilarityService simSrv;
    @Autowired @Qualifier("MergeService")
    private MergeService merSrv;
    @Autowired @Qualifier("SessionService")
    private SessionService sesSrv;
    @Autowired @Qualifier("CanonicalConverter")
    private CanonicalConverter convertor;

    @Autowired
    private ManagerCanoniserClient caClient;

    @PayloadRoot(namespace = NAMESPACE, localPart = "EditProcessDataRequest")
    @ResponsePayload
    public JAXBElement<EditProcessDataOutputMsgType> editProcessData(@RequestPayload final JAXBElement<EditProcessDataInputMsgType> req) {
        LOGGER.info("Executing operation editDataProcess");
        EditProcessDataInputMsgType payload = req.getValue();
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

            procSrv.updateProcessMetaData(processId, processName, domain, username, preVersion, newVersion, ranking);

            result.setCode(0);
            result.setMessage("");
        } catch (Exception ex) {
            LOGGER.error("", ex);
            result.setCode(-1);
            result.setMessage(ex.getMessage());
        }
        return WS_OBJECT_FACTORY.createEditProcessDataResponse(res);
    }

    @PayloadRoot(localPart = "MergeProcessesRequest", namespace = NAMESPACE)
    @ResponsePayload
    public JAXBElement<MergeProcessesOutputMsgType> mergeProcesses(@RequestPayload final JAXBElement<MergeProcessesInputMsgType> req) {
        LOGGER.info("Executing operation mergeProcesses");
        MergeProcessesInputMsgType payload = req.getValue();
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
            ProcessSummaryType respFromToolbox = merSrv.mergeProcesses(processName, version, domain, username, algo, parameters, ids);
            res.setProcessSummary(respFromToolbox);
            result.setCode(0);
            result.setMessage("");
        } catch (Exception ex) {
            LOGGER.error("", ex);
            result.setCode(-1);
            result.setMessage(ex.getMessage());
        }
        return WS_OBJECT_FACTORY.createMergeProcessesResponse(res);
    }

    /*
     * (non-Javadoc)
     *
     * @see org.apromore.manager.service_portal1.ManagerPortalPortType#searchForSimilarProcesses(SearchForSimilarProcessesInputMsgType payload )*
     */
    @PayloadRoot(localPart = "SearchForSimilarProcessesRequest", namespace = NAMESPACE)
    @ResponsePayload
    public JAXBElement<SearchForSimilarProcessesOutputMsgType> searchForSimilarProcesses(
            @RequestPayload final JAXBElement<SearchForSimilarProcessesInputMsgType> req) {
        LOGGER.info("Executing operation searchForSimilarProcesses");
        SearchForSimilarProcessesInputMsgType payload = req.getValue();
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
            ProcessSummariesType processes = simSrv.SearchForSimilarProcesses(processId, versionName, latestVersions, algo, paramsT);
            res.setProcessSummaries(processes);
            result.setCode(0);
            result.setMessage("");
        } catch (Exception ex) {
            LOGGER.error("", ex);
            result.setCode(-1);
            result.setMessage(ex.getMessage());
        }
        return WS_OBJECT_FACTORY.createSearchForSimilarProcessesResponse(res);
    }

    @PayloadRoot(localPart = "WriteAnnotationRequest", namespace = NAMESPACE)
    @ResponsePayload
    public JAXBElement<WriteAnnotationOutputMsgType> writeAnnotation(@RequestPayload final JAXBElement<WriteAnnotationInputMsgType> req) {
        LOGGER.info("Executing operation writeAnnotation");
        WriteAnnotationInputMsgType payload = req.getValue();
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
            // TODO use CanoniserService instead
            caClient.GenerateAnnotation(annotName, editSessionCode, isNew, processId, version, nat_type, native_is);
            result.setCode(0);
            result.setMessage("");
        } catch (Exception ex) {
            LOGGER.error("", ex);
            result.setCode(-1);
            result.setMessage(ex.getMessage());
        }
        return WS_OBJECT_FACTORY.createWriteAnnotationResponse(res);
    }

    @PayloadRoot(namespace = NAMESPACE, localPart = "GetAllClustersRequest")
    public void getAllClusters(@RequestPayload final JAXBElement<String> message) {
        LOGGER.info("Retrieving all clusters in the repository ...");
        String payload = message.getValue();
        String res = "received: " + payload;
        System.out.println(res);
    }

    @PayloadRoot(namespace = NAMESPACE, localPart = "ReadAllUsersRequest")
    @ResponsePayload
    public JAXBElement<ReadAllUsersOutputMsgType> readAllUsers(@RequestPayload final JAXBElement<ReadAllUsersInputMsgType> message) {
        LOGGER.info("Executing operation readAllUsers");
        ReadAllUsersInputMsgType payload = message.getValue();
        ReadAllUsersOutputMsgType res = new ReadAllUsersOutputMsgType();
        ResultType result = new ResultType();
        res.setResult(result);
        UsernamesType allUsers = UserMapper.convertUsernameTypes(userSrv.findAllUsers());
        res.setUsernames(allUsers);
        result.setCode(0);
        result.setMessage("");
        return WS_OBJECT_FACTORY.createReadAllUsersResponse(res);
    }

    @PayloadRoot(localPart = "DeleteEditSessionRequest", namespace = NAMESPACE)
    @ResponsePayload
    public JAXBElement<DeleteEditSessionOutputMsgType> deleteEditSession(@RequestPayload final JAXBElement<DeleteEditSessionInputMsgType> req) {
        LOGGER.info("Executing operation deleteEditSession");
        DeleteEditSessionInputMsgType payload = req.getValue();
        DeleteEditSessionOutputMsgType res = new DeleteEditSessionOutputMsgType();
        ResultType result = new ResultType();
        res.setResult(result);
        int code = payload.getEditSessionCode();
        try {
            sesSrv.deleteSession(code);
            result.setCode(0);
            result.setMessage("");
        } catch (Exception ex) {
            LOGGER.error("", ex);
            result.setCode(-1);
            result.setMessage(ex.getMessage());
        }
        return WS_OBJECT_FACTORY.createDeleteEditSessionResponse(res);
    }

    @PayloadRoot(localPart = "DeleteProcessVersionsRequest", namespace = NAMESPACE)
    @ResponsePayload
    public JAXBElement<DeleteProcessVersionsOutputMsgType> deleteProcessVersions(
            @RequestPayload final JAXBElement<DeleteProcessVersionsInputMsgType> req) {
        LOGGER.info("Executing operation deleteProcessVersions");
        DeleteProcessVersionsInputMsgType payload = req.getValue();
        DeleteProcessVersionsOutputMsgType res = new DeleteProcessVersionsOutputMsgType();
        ResultType result = new ResultType();
        res.setResult(result);
        try {
            List<NameValuePair> processVersions = new ArrayList<NameValuePair>(0);
            for (final ProcessVersionIdentifierType p : payload.getProcessVersionIdentifier()) {
                processVersions.add(new NameValuePair(p.getProcessName(), p.getBranchName()));
            }
            repSrv.deleteProcessModel(processVersions);

            result.setCode(0);
            result.setMessage("");
        } catch (Exception ex) {
            LOGGER.error("", ex);
            result.setCode(-1);
            result.setMessage(ex.getMessage());
        }
        return WS_OBJECT_FACTORY.createDeleteProcessVersionsResponse(res);
    }

    @PayloadRoot(localPart = "UpdateProcessRequest", namespace = NAMESPACE)
    @ResponsePayload
    public JAXBElement<UpdateProcessOutputMsgType> updateProcess(@RequestPayload final JAXBElement<UpdateProcessInputMsgType> req) {
        LOGGER.info("Executing operation updateProcess");
        UpdateProcessInputMsgType payload = req.getValue();
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
            // TODO use CanoniserService instead
            caClient.CanoniseVersion(editSessionCode, editSessionC, newCpfURI(), native_is);
            result.setCode(0);
            result.setMessage("");
        } catch (ExceptionVersion ex) {
            LOGGER.error("", ex);
            result.setCode(-3);
            result.setMessage(ex.getMessage());
        } catch (IOException ex) {
            LOGGER.error("", ex);
            result.setCode(-1);
            result.setMessage(ex.getMessage());
        } catch (ExceptionCanoniseVersion ex) {
            LOGGER.error("", ex);
            result.setCode(-1);
            result.setMessage(ex.getMessage());
        }
        return WS_OBJECT_FACTORY.createUpdateProcessResponse(res);
    }

    @PayloadRoot(localPart = "ReadEditSessionRequest", namespace = NAMESPACE)
    @ResponsePayload
    public JAXBElement<ReadEditSessionOutputMsgType> readEditSession(@RequestPayload final JAXBElement<ReadEditSessionInputMsgType> req) {
        LOGGER.info("Executing operation readEditSession");
        ReadEditSessionInputMsgType payload = req.getValue();
        ReadEditSessionOutputMsgType res = new ReadEditSessionOutputMsgType();
        int code = payload.getEditSessionCode();
        ResultType result = new ResultType();
        res.setResult(result);
        try {
            EditSession session = sesSrv.readSession(code);

            EditSessionType editSessionP = new EditSessionType();
            editSessionP.setNativeType(session.getNatType());
            editSessionP.setProcessId(session.getProcess().getId());
            editSessionP.setUsername(session.getUser().getUsername());
            editSessionP.setVersionName(session.getVersionName());
            editSessionP.setProcessName(session.getProcess().getName());
            editSessionP.setDomain(session.getProcess().getDomain());
            editSessionP.setCreationDate(session.getCreationDate());
            editSessionP.setLastUpdate(session.getLastUpdate());
            if (session.getAnnotation() == null) {
                editSessionP.setWithAnnotation(false);
            } else {
                editSessionP.setWithAnnotation(true);
                editSessionP.setAnnotation(session.getAnnotation());
            }

            res.setEditSession(editSessionP);
            result.setCode(0);
            result.setMessage("");
        } catch (Exception ex) {
            LOGGER.error("", ex);
            result.setCode(-1);
            result.setMessage(ex.getMessage());
        }
        return WS_OBJECT_FACTORY.createReadEditSessionResponse(res);
    }

    @PayloadRoot(localPart = "WriteEditSessionRequest", namespace = NAMESPACE)
    @ResponsePayload
    public JAXBElement<WriteEditSessionOutputMsgType> writeEditSession(@RequestPayload final JAXBElement<WriteEditSessionInputMsgType> req) {
        LOGGER.info("Executing operation writeEditSession");
        WriteEditSessionInputMsgType payload = req.getValue();
        WriteEditSessionOutputMsgType res = new WriteEditSessionOutputMsgType();
        ResultType result = new ResultType();
        res.setResult(result);

        EditSessionType editSessionP = payload.getEditSession();
        EditSessionType editSessionType = new EditSessionType();
        editSessionType.setNativeType(editSessionP.getNativeType());
        editSessionType.setProcessId(editSessionP.getProcessId());
        editSessionType.setUsername(editSessionP.getUsername());
        editSessionType.setVersionName(editSessionP.getVersionName());
        editSessionType.setProcessName(editSessionP.getProcessName());
        editSessionType.setDomain(editSessionP.getDomain());
        editSessionType.setCreationDate(editSessionP.getCreationDate());
        editSessionType.setLastUpdate(editSessionP.getLastUpdate());
        editSessionType.setWithAnnotation(editSessionP.isWithAnnotation());
        editSessionType.setAnnotation(editSessionP.getAnnotation());

        try {
            int code = sesSrv.createSession(editSessionType);
            res.setEditSessionCode(code);
            result.setCode(0);
            result.setMessage("");
        } catch (Exception ex) {
            LOGGER.error("", ex);
            result.setCode(-1);
            result.setMessage(ex.getMessage());
        }
        return WS_OBJECT_FACTORY.createWriteEditSessionResponse(res);
    }

    @PayloadRoot(localPart = "GetFragmentRequest", namespace = NAMESPACE)
    @ResponsePayload
    public JAXBElement<FragmentResponseType> getFragment(@RequestPayload final JAXBElement<GetFragmentRequestType> req) {
        LOGGER.info("Executing operation getFragment");

        Integer fragmentId = req.getValue().getFragmentId();
        FragmentResponseType res = new FragmentResponseType();
        FragmentType fragment = new FragmentType();
        fragment.setFragmentId(fragmentId);

        try {
            String epmlString = fragmentSrv.getFragmentAsEPML(fragmentId);
            fragment.setContent(epmlString);
        } catch (RepositoryException e) {
            LOGGER.error("getFragment failed.");
        }

        res.setFragment(fragment);
        return WS_OBJECT_FACTORY.createFragmentResponse(res);
    }

    @PayloadRoot(localPart = "ExportFormatRequest", namespace = NAMESPACE)
    @ResponsePayload
    public JAXBElement<ExportFormatOutputMsgType> exportFormat(@RequestPayload final JAXBElement<ExportFormatInputMsgType> req) {
        LOGGER.info("Executing operation exportFormat");

        ResultType result = new ResultType();
        ExportFormatInputMsgType payload = req.getValue();
        ExportFormatOutputMsgType res = new ExportFormatOutputMsgType();

        // Search for Native
        try {
            Integer processId = payload.getProcessId();
            String name = payload.getProcessName();
            String version = payload.getVersionName();
            String format = payload.getFormat();
            String annName = payload.getAnnotationName();
            boolean withAnn = payload.isWithAnnotations();

            Set<RequestParameterType<?>> requestProperties = PluginHelper.convertToRequestProperties(payload.getCanoniserParameters());
            ExportFormatResultType exportResult = procSrv.exportProcess(name, processId, version, format, annName, withAnn, requestProperties);
            res.setExportResult(exportResult);

            result.setCode(0);
            result.setMessage("");
        } catch (ExportFormatException efe) {
            LOGGER.error("ExportFormat failed: " + efe.toString());
            result.setCode(-1);
            result.setMessage(efe.getMessage());
        }

        res.setResult(result);
        return WS_OBJECT_FACTORY.createExportFormatResponse(res);
    }

    @PayloadRoot(localPart = "ImportProcessRequest", namespace = NAMESPACE)
    @ResponsePayload
    public JAXBElement<ImportProcessOutputMsgType> importProcess(@RequestPayload final JAXBElement<ImportProcessInputMsgType> req) {
        LOGGER.info("Executing operation importProcess");

        ResultType result = new ResultType();
        ImportProcessInputMsgType payload = req.getValue();
        ImportProcessOutputMsgType res = new ImportProcessOutputMsgType();

        try {
            EditSessionType editSession = payload.getEditSession();
            String username = editSession.getUsername();
            String processName = editSession.getProcessName();
            String versionName = editSession.getVersionName();
            String nativeType = editSession.getNativeType();
            String domain = editSession.getDomain();
            String creationDate = editSession.getCreationDate();
            String lastUpdate = editSession.getLastUpdate();

            DataHandler handler = payload.getProcessDescription();
            PluginParameters xmlCanoniserProperties = payload.getCanoniserParameters();

            Set<RequestParameterType<?>> canoniserProperties = PluginHelper.convertToRequestProperties(xmlCanoniserProperties);
            CanonisedProcess canonisedProcess = canoniserService.canonise(nativeType, handler.getInputStream(), canoniserProperties);

            ProcessSummaryType process = procSrv.importProcess(username, processName, newCpfURI(), versionName, nativeType, canonisedProcess,
                    handler.getInputStream(), domain, "", creationDate, lastUpdate);

            ImportProcessResultType importResult = new ImportProcessResultType();
            if (canonisedProcess.getMessages() != null) {
                importResult.setMessage(PluginHelper.convertFromPluginMessages(canonisedProcess.getMessages()));
            }
            importResult.setProcessSummary(process);
            res.setImportProcessResult(importResult);

            result.setCode(0);
            result.setMessage("");
        } catch (Exception ex) {
            LOGGER.error("", ex);
            result.setCode(-1);
            result.setMessage(ex.getMessage());
        }

        res.setResult(result);
        return WS_OBJECT_FACTORY.createImportProcessResponse(res);
    }

    @PayloadRoot(localPart = "CreateClustersRequest", namespace = NAMESPACE)
    @ResponsePayload
    public JAXBElement<CreateClustersOutputMsgType> createClusters(@RequestPayload final JAXBElement<CreateClustersInputMsgType> req) {
        LOGGER.info("Executing operation createClusters");
        CreateClustersInputMsgType payload = req.getValue();
        CreateClustersOutputMsgType res = new CreateClustersOutputMsgType();

        ClusterSettings settings = ClusterMapper.convertClusterSettingsTypeToClusterSettings(payload.getClusterSettings());

        try {
            clusterService.cluster(settings);
        } catch (RepositoryException e) {
            LOGGER.error("Failed to create clusters - " + e.getMessage());
        }

        int numClusters = -1;
        ClusteringSummaryType clusterSummaries = new ClusteringSummaryType();
        clusterSummaries.setNumClusters(numClusters);
        res.setClusterSummaries(clusterSummaries);

        return WS_OBJECT_FACTORY.createCreateClustersResponse(res);
    }

    @PayloadRoot(localPart = "PairwiseDistancesRequest", namespace = NAMESPACE)
    @ResponsePayload
    public JAXBElement<GetPairwiseDistancesOutputMsgType> getPairwiseDistances(@RequestPayload final JAXBElement<GetPairwiseDistancesInputMsgType> req) {
        LOGGER.info("Executing operation getPairwiseDistances");
        GetPairwiseDistancesInputMsgType payload = req.getValue();
        GetPairwiseDistancesOutputMsgType res = new GetPairwiseDistancesOutputMsgType();

        List<Integer> fragmentIds = payload.getFragmentIds().getFragmentId();
        try {
            Map<FragmentPair, Double> pairDistances = clusterService.getPairDistances(fragmentIds);
            PairDistancesType pairDistancesType = ClusterMapper.convertPairDistancesToPairDistancesType(pairDistances);
            res.setPairDistances(pairDistancesType);
        } catch (RepositoryException e) {
            LOGGER.error("Failed to get pairwise distances. " + e.getMessage());
        }

        return WS_OBJECT_FACTORY.createPairwiseDistancesResponse(res);
    }

    @PayloadRoot(localPart = "GetClusteringSummaryRequest", namespace = NAMESPACE)
    @ResponsePayload
    public JAXBElement<GetClusteringSummaryOutputMsgType> getClusteringSummary(@RequestPayload final JAXBElement<GetClusteringSummaryInputMsgType> req) {
        LOGGER.info("Executing operation getClusteringSummary");
        GetClusteringSummaryInputMsgType payload = req.getValue();
        GetClusteringSummaryOutputMsgType res = new GetClusteringSummaryOutputMsgType();

        ClusteringSummary summary = clusterService.getClusteringSummary();
        ClusteringSummaryType summaryType = ClusterMapper.convertClusteringSummaryToClusteringSummaryType(summary);
        res.setClusteringSummary(summaryType);

        return WS_OBJECT_FACTORY.createGetClusteringSummaryResponse(res);
    }

    @PayloadRoot(localPart = "GetClusterSummariesRequest", namespace = NAMESPACE)
    @ResponsePayload
    public JAXBElement<GetClusterSummariesOutputMsgType> getClusterSummaries(@RequestPayload final JAXBElement<GetClusterSummariesInputMsgType> req) {
        LOGGER.info("Executing operation getClusterSummaries");
        GetClusterSummariesInputMsgType payload = req.getValue();
        GetClusterSummariesOutputMsgType res = new GetClusterSummariesOutputMsgType();

        ClusterFilter filter = ClusterMapper.convertClusterFilterTypeToClusterFilter(payload.getFilter());
        List<Cluster> clusters = clusterService.getClusterSummaries(filter);
        for (Cluster c : clusters) {
            ClusterSummaryType ctype = ClusterMapper.convertClusterInfoToClusterSummaryType(c);
            res.getClusterSummaries().add(ctype);
        }
        return WS_OBJECT_FACTORY.createGetClusterSummariesResponse(res);
    }

    @PayloadRoot(localPart = "GetClusterRequest", namespace = NAMESPACE)
    @ResponsePayload
    public JAXBElement<GetClusterOutputMsgType> getCluster(@RequestPayload final JAXBElement<GetClusterInputMsgType> req) {
        LOGGER.info("Executing operation getCluster");
        GetClusterInputMsgType payload = req.getValue();
        GetClusterOutputMsgType res = new GetClusterOutputMsgType();

        Integer clusterId = payload.getClusterId();
        org.apromore.service.model.Cluster cluster = clusterService.getCluster(clusterId);
        ClusterType ctype = ClusterMapper.convertClusterToClusterType(cluster);
        res.setCluster(ctype);
        return WS_OBJECT_FACTORY.createGetClusterResponse(res);
    }

    @PayloadRoot(localPart = "GetClustersRequest", namespace = NAMESPACE)
    @ResponsePayload
    public JAXBElement<GetClustersResponseType> getClusters(@RequestPayload final JAXBElement<GetClustersRequestType> req) {
        LOGGER.info("Executing operation getClusters");
        GetClustersRequestType payload = req.getValue();
        GetClustersResponseType res = new GetClustersResponseType();

        ClusterFilter filter = ClusterMapper.convertClusterFilterTypeToClusterFilter(payload.getClusterFilter());
        List<org.apromore.service.model.Cluster> clusters = clusterService.getClusters(filter);
        for (org.apromore.service.model.Cluster c : clusters) {
            ClusterType ctype = ClusterMapper.convertClusterToClusterType(c);
            res.getClusters().add(ctype);
        }
        return WS_OBJECT_FACTORY.createGetClustersResponse(res);
    }

    @PayloadRoot(localPart = "WriteUserRequest", namespace = NAMESPACE)
    @ResponsePayload
    public JAXBElement<WriteUserOutputMsgType> writeUser(@RequestPayload final JAXBElement<WriteUserInputMsgType> req) {
        LOGGER.info("Executing operation writeUser");
        WriteUserInputMsgType payload = req.getValue();
        WriteUserOutputMsgType res = new WriteUserOutputMsgType();
        ResultType result = new ResultType();
        res.setResult(result);
        try {
            userSrv.writeUser(UserMapper.convertFromUserType(payload.getUser()));
            result.setCode(0);
            result.setMessage("");
        } catch (Exception ex) {
            ex.printStackTrace();
            result.setCode(0);
            result.setMessage(ex.getMessage());
        }
        return WS_OBJECT_FACTORY.createWriteUserResponse(res);
    }

    @PayloadRoot(localPart = "ReadNativeTypesRequest", namespace = NAMESPACE)
    @ResponsePayload
    public JAXBElement<ReadNativeTypesOutputMsgType> readNativeTypes(@RequestPayload final JAXBElement<ReadNativeTypesInputMsgType> req) {
        LOGGER.info("Executing operation readFormats");
        ReadNativeTypesOutputMsgType res = new ReadNativeTypesOutputMsgType();
        ResultType result = new ResultType();
        res.setResult(result);
        try {
            NativeTypesType formats = NativeTypeMapper.convertFromNativeType(frmSrv.findAllFormats());
            result.setCode(0);
            result.setMessage("");
            res.setNativeTypes(formats);
        } catch (Exception ex) {
            ex.printStackTrace();
            result.setCode(-1);
            result.setMessage(ex.getMessage());
        }
        return WS_OBJECT_FACTORY.createReadNativeTypesResponse(res);
    }

    /*
     * (non-Javadoc)
     *
     * @see org.apromore.manager.service.ManagerPortalPortType#readDomains(ReadDomainsInputMsgType payload )*
     */
    @PayloadRoot(localPart = "ReadDomainsRequest", namespace = NAMESPACE)
    @ResponsePayload
    public JAXBElement<ReadDomainsOutputMsgType> readDomains(@RequestPayload final JAXBElement<ReadDomainsInputMsgType> req) {
        LOGGER.info("Executing operation readDomains");
        ReadDomainsOutputMsgType res = new ReadDomainsOutputMsgType();
        ResultType result = new ResultType();
        res.setResult(result);
        try {
            DomainsType domains = DomainMapper.convertFromDomains(domSrv.findAllDomains());
            result.setCode(0);
            result.setMessage("");
            res.setDomains(domains);
        } catch (Exception ex) {
            LOGGER.error("", ex);
            result.setCode(-1);
            result.setMessage(ex.getMessage());
        }
        return WS_OBJECT_FACTORY.createReadDomainsResponse(res);
    }

    /*
     * (non-Javadoc)
     *
     * @see org.apromore.manager.service.ManagerPortalPortType#readUser(ReadUserInputMsgType payload )
     */
    @PayloadRoot(localPart = "ReadUserRequest", namespace = NAMESPACE)
    @ResponsePayload
    public JAXBElement<ReadUserOutputMsgType> readUser(@RequestPayload final JAXBElement<ReadUserInputMsgType> req) {
        LOGGER.info("Executing operation readUser");
        ReadUserInputMsgType payload = req.getValue();
        ReadUserOutputMsgType res = new ReadUserOutputMsgType();
        ResultType result = new ResultType();
        res.setResult(result);
        try {
            UserType user = UserMapper.convertUserTypes(userSrv.findUserByLogin(payload.getUsername()));
            result.setCode(0);
            result.setMessage("");
            res.setUser(user);
        } catch (Exception ex) {
            LOGGER.error("", ex);
            result.setCode(-1);
            result.setMessage(ex.getMessage());
        }
        return WS_OBJECT_FACTORY.createReadUserResponse(res);
    }

    /*
     * (non-Javadoc)
     *
     * @see org.apromore.manager.service.ManagerPortalPortType#readProcessSummaries(ReadProcessSummariesInputMsgType payload )*
     */
    @PayloadRoot(localPart = "ReadProcessSummariesRequest", namespace = NAMESPACE)
    @ResponsePayload
    public JAXBElement<ReadProcessSummariesOutputMsgType> readProcessSummaries(@RequestPayload final JAXBElement<ReadProcessSummariesInputMsgType> req) {
        LOGGER.info("Executing operation readProcessSummaries");
        ReadProcessSummariesInputMsgType payload = req.getValue();
        ReadProcessSummariesOutputMsgType res = new ReadProcessSummariesOutputMsgType();
        ResultType result = new ResultType();
        res.setResult(result);

        try {
            ProcessSummariesType processes = procSrv.readProcessSummaries(payload.getSearchExpression());
            result.setCode(0);
            result.setMessage("");
            res.setProcessSummaries(processes);
        } catch (Exception ex) {
            LOGGER.error("ReadProcessSummaries", ex);
            result.setCode(-1);
            result.setMessage(ex.getMessage());
        }
        return WS_OBJECT_FACTORY.createReadProcessSummariesResponse(res);
    }

    @PayloadRoot(localPart = "ReadInstalledPluginsRequest", namespace = NAMESPACE)
    @ResponsePayload
    public JAXBElement<ReadInstalledPluginsOutputMsgType> readInstalledPlugins(@RequestPayload final JAXBElement<ReadInstalledPluginsInputMsgType> req) {
        LOGGER.info("Executing operation 'ReadInstalledPlugins'");
        ReadInstalledPluginsOutputMsgType res = new ReadInstalledPluginsOutputMsgType();
        ResultType result = new ResultType();
        try {
            Set<Plugin> plugins;

            String pluginTypeFilter = req.getValue().getTypeFilter();
            if (pluginTypeFilter != null) {
                plugins = pluginService.listByType(pluginTypeFilter);
            } else {
                plugins = pluginService.listAll();
            }

            for (Plugin p : plugins) {
                res.getPluginInfo().add(PluginHelper.convertPluginInfo(p));
            }

            result.setCode(0);
            result.setMessage("Successfully read plugin info!");
        } catch (Exception ex) {
            LOGGER.error("ReadInstalledPlugins", ex);
            result.setCode(-1);
            result.setMessage(ex.getMessage());
        }

        res.setResult(result);
        return WS_OBJECT_FACTORY.createReadInstalledPluginsResponse(res);
    }

    @PayloadRoot(localPart = "ReadPluginInfoRequest", namespace = NAMESPACE)
    @ResponsePayload
    public JAXBElement<ReadPluginInfoOutputMsgType> readPluginInfo(@RequestPayload final JAXBElement<ReadPluginInfoInputMsgType> req) {
        LOGGER.info("Executing operation 'ReadPluginInfo'");
        ReadPluginInfoOutputMsgType res = new ReadPluginInfoOutputMsgType();
        ResultType result = new ResultType();
        try {
            String pluginName = req.getValue().getPluginName();
            String pluginVersion = req.getValue().getPluginVersion();
            Plugin plugin = pluginService.findByNameAndVersion(pluginName, pluginVersion);

            PluginInfo pluginInfo = PluginHelper.convertPluginInfo(plugin);
            PluginInfoResult infoResult = new PluginInfoResult();

            infoResult.setPluginInfo(pluginInfo);
            if (plugin instanceof ParameterAwarePlugin) {
                ParameterAwarePlugin propertyAwarePlugin = (ParameterAwarePlugin) plugin;
                infoResult.setMandatoryParameters(PluginHelper.convertFromPluginParameters(propertyAwarePlugin.getMandatoryParameters()));
                infoResult.setOptionalParameters(PluginHelper.convertFromPluginParameters(propertyAwarePlugin.getOptionalParameters()));
            }

            res.setPluginInfoResult(infoResult);
            result.setCode(0);
            result.setMessage("Successfully read plugin info!");
        } catch (Exception ex) {
            LOGGER.error("ReadPluginInfo", ex);
            result.setCode(-1);
            result.setMessage(ex.getMessage());
        }

        res.setResult(result);
        return WS_OBJECT_FACTORY.createReadPluginInfoResponse(res);
    }

    @PayloadRoot(localPart = "ReadCanoniserInfoRequest", namespace = NAMESPACE)
    @ResponsePayload
    public JAXBElement<ReadCanoniserInfoOutputMsgType> readCanoniserInfo(@RequestPayload final JAXBElement<ReadCanoniserInfoInputMsgType> req) {
        LOGGER.info("Executing operation 'ReadCanoniserInfo'");
        ReadCanoniserInfoOutputMsgType res = new ReadCanoniserInfoOutputMsgType();
        ResultType result = new ResultType();
        try {
            String nativeType = req.getValue().getNativeType();
            Set<Canoniser> cList = canoniserService.listByNativeType(nativeType);
            Iterator<Canoniser> iter = cList.iterator();
            while (iter.hasNext()) {
                Canoniser c = iter.next();
                res.getPluginInfo().add(PluginHelper.convertPluginInfo(c));
            }
            result.setCode(0);
            result.setMessage("Successfully read plugin info!");
        } catch (Exception ex) {
            LOGGER.error("ReadCanoniserInfo", ex);
            result.setCode(-1);
            result.setMessage(ex.getMessage());
        }

        res.setResult(result);
        return WS_OBJECT_FACTORY.createReadCanoniserInfoResponse(res);
    }

    @PayloadRoot(localPart = "ReadNativeMetaDataRequest", namespace = NAMESPACE)
    @ResponsePayload
    public JAXBElement<ReadNativeMetaDataOutputMsgType> readNativeMetaData(@RequestPayload final JAXBElement<ReadNativeMetaDataInputMsgType> req) {
        LOGGER.info("Executing operation 'ReadNativeMetaData'");
        ReadNativeMetaDataOutputMsgType res = new ReadNativeMetaDataOutputMsgType();
        ResultType result = new ResultType();
        try {
            Canoniser c = null;
            String nativeType = req.getValue().getNativeType();
            String canoniserName = req.getValue().getCanoniserName();
            String canoniserVersion = req.getValue().getCanoniserVersion();
            if (canoniserName != null && canoniserVersion != null) {
                c = canoniserService.findByNativeTypeAndNameAndVersion(nativeType, canoniserName, canoniserName);
            } else {
                c = canoniserService.findByNativeType(nativeType);
            }

            CanoniserMetadataResult metaData = c.readMetaData(req.getValue().getNativeFormat().getInputStream(), new PluginRequestImpl());
            res.setNativeMetaData(CanoniserHelper.convertFromCanoniserMetaData(metaData));

            result.setCode(0);
            result.setMessage("Success 'ReadNativeMetaData'!");
        } catch (Exception ex) {
            LOGGER.error("ReadNativeMetaData", ex);
            result.setCode(-1);
            result.setMessage(ex.getMessage());
        }

        res.setResult(result);
        return WS_OBJECT_FACTORY.createReadNativeMetaDataResponse(res);
    }

    @PayloadRoot(localPart = "ReadInitialNativeFormatRequest", namespace = NAMESPACE)
    @ResponsePayload
    public JAXBElement<ReadInitialNativeFormatOutputMsgType> readInitialNativeFormat(
            @RequestPayload final JAXBElement<ReadInitialNativeFormatInputMsgType> req) {
        LOGGER.info("Executing operation 'ReadInitialNativeFormat'");
        ReadInitialNativeFormatOutputMsgType res = new ReadInitialNativeFormatOutputMsgType();
        ResultType result = new ResultType();
        try {

            Canoniser c = null;
            String nativeType = req.getValue().getNativeType();
            String canoniserName = req.getValue().getCanoniserName();
            String canoniserVersion = req.getValue().getCanoniserVersion();
            if (canoniserName != null && canoniserVersion != null) {
                c = canoniserService.findByNativeTypeAndNameAndVersion(nativeType, canoniserName, canoniserName);
            } else {
                c = canoniserService.findByNativeType(nativeType);
            }

            ByteArrayOutputStream nativeXml = new ByteArrayOutputStream();

            String processAuthor = null;
            String processVersion = null;
            String processName = null;
            Date processCreated = null;

            NativeMetaData nativeMetaData = req.getValue().getNativeMetaData();
            if (nativeMetaData != null) {
                processName = nativeMetaData.getProcessName();
                processVersion = nativeMetaData.getProcessVersion();
                processAuthor = nativeMetaData.getProcessAuthor();
                if (nativeMetaData.getProcessCreated() != null) {
                    processCreated = nativeMetaData.getProcessCreated().toGregorianCalendar().getTime();
                }
            }

            c.createInitialNativeFormat(nativeXml, processName, processVersion, processAuthor, processCreated, new PluginRequestImpl());
            InputStream nativeXmlInputStream = new ByteArrayInputStream(nativeXml.toByteArray());

            res.setNativeFormat(new DataHandler(new ByteArrayDataSource(nativeXmlInputStream, "text/xml")));

            result.setCode(0);
            result.setMessage("Success 'ReadInitialNativeFormat'!");
        } catch (Exception ex) {
            LOGGER.error("ReadInitialNativeFormat", ex);
            result.setCode(-1);
            result.setMessage(ex.getMessage());
        }

        res.setResult(result);
        return WS_OBJECT_FACTORY.createReadInitialNativeFormatResponse(res);
    }

    @PayloadRoot(localPart = "ReadDeploymentPluginInfoRequest", namespace = NAMESPACE)
    @ResponsePayload
    public JAXBElement<ReadDeploymentPluginInfoOutputMsgType> readDeploymentPluginInfo(
            @RequestPayload final JAXBElement<ReadDeploymentPluginInfoInputMsgType> req) {
        LOGGER.info("Executing operation 'ReadDeploymentPluginInfo'");
        ReadDeploymentPluginInfoOutputMsgType res = new ReadDeploymentPluginInfoOutputMsgType();
        ResultType result = new ResultType();
        try {
            String nativeType = req.getValue().getNativeType();
            Set<DeploymentPlugin> dList = deploymentService.listDeploymentPlugin(nativeType);
            Iterator<DeploymentPlugin> iter = dList.iterator();
            while (iter.hasNext()) {
                DeploymentPlugin d = iter.next();
                res.getPluginInfo().add(PluginHelper.convertPluginInfo(d));
            }
            result.setCode(0);
            result.setMessage("Successfully read deployment plugin info!");
        } catch (Exception ex) {
            LOGGER.error("ReadDeploymentPluginInfo", ex);
            result.setCode(-1);
            result.setMessage(ex.getMessage());
        }

        res.setResult(result);
        return WS_OBJECT_FACTORY.createReadDeploymentPluginResponse(res);
    }

    @PayloadRoot(localPart = "DeployProcessRequest", namespace = NAMESPACE)
    @ResponsePayload
    public JAXBElement<DeployProcessOutputMsgType> deployProcess(@RequestPayload final JAXBElement<DeployProcessInputMsgType> req) {
        LOGGER.info("Executing operation 'DeployProcess'");
        DeployProcessOutputMsgType res = new DeployProcessOutputMsgType();
        ResultType result = new ResultType();
        try {
            String nativeType = req.getValue().getNativeType();
            String processName = req.getValue().getProcessName();
            String versionName = req.getValue().getVersionName();
            String branchName = req.getValue().getBranchName();
            PluginParameters deploymentProperties = req.getValue().getDeploymentParameters();
            Set<RequestParameterType<?>> requestProperties = PluginHelper.convertToRequestProperties(deploymentProperties);
            String deploymentPluginName = req.getValue().getDeploymentPluginName();
            String deploymentPluginVersion = req.getValue().getDeploymentPluginVersion();

            Canonical cpf = repSrv.getCurrentProcessModel(processName, branchName, false);
            CanonicalProcessType canonialProcess = convertor.convert(cpf);

            List<PluginMessage> deployProcess;
            // TODO Add ANF
            if (deploymentPluginName != null && deploymentPluginVersion != null) {
                deployProcess = deploymentService.deployProcess(nativeType, deploymentPluginName, deploymentPluginVersion, canonialProcess, null,
                        requestProperties);
            } else {
                deployProcess = deploymentService.deployProcess(nativeType, canonialProcess, null, requestProperties);
            }

            res.setMessage(PluginHelper.convertFromPluginMessages(deployProcess));
            result.setCode(0);
            result.setMessage("Successfully read plugin info!");
        } catch (Exception ex) {
            LOGGER.error("DeployProcess", ex);
            result.setCode(-1);
            result.setMessage(ex.getMessage());
        }

        res.setResult(result);
        return WS_OBJECT_FACTORY.createDeployProcessResponse(res);
    }

    /**
     * Generate a cpf uri for version of processId
     *
     * @return the new cpf uri
     */
    @Deprecated
    // TODO should CPF uri really be determined by date???
    private static String newCpfURI() {
        DateFormat dateFormat = new SimpleDateFormat("yyyyMMddHHmmsSSS");
        Date date = new Date();
        return dateFormat.format(date);
    }

    public void setCaClient(final ManagerCanoniserClient caClient) {
        this.caClient = caClient;
    }

    public void setUserSrv(final UserService userService) {
        this.userSrv = userService;
    }

    public void setProcSrv(final ProcessService procService) {
        this.procSrv = procService;
    }

    public void setFrmSrv(final FormatService formatService) {
        this.frmSrv = formatService;
    }

    public void setDomSrv(final DomainService domainService) {
        this.domSrv = domainService;
    }

    public void setSimSrv(final SimilarityService similarityService) {
        this.simSrv = similarityService;
    }

    public void setMerSrv(final MergeService mergeService) {
        this.merSrv = mergeService;
    }

    public void setSesSrv(final SessionService sessionService) {
        this.sesSrv = sessionService;
    }

    public DeploymentService getDeploymentService() {
        return deploymentService;
    }

    public void setDeploymentService(final DeploymentService deploymentService) {
        this.deploymentService = deploymentService;
    }

    public PluginService getPluginService() {
        return pluginService;
    }

    public void setPluginService(final PluginService pluginService) {
        this.pluginService = pluginService;
    }

    public CanoniserService getCanoniserService() {
        return canoniserService;
    }

    public void setCanoniserService(final CanoniserService canoniserService) {
        this.canoniserService = canoniserService;
    }

    public void setRepSrv(final RepositoryService repositoryService) {
        this.repSrv = repositoryService;
    }

    public void setConvertorAdpater(CanonicalConverter convertorService) {
        this.convertor = convertorService;
    }
}
