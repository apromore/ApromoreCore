package org.apromore.manager;

import javax.activation.DataHandler;
import javax.inject.Inject;
import javax.mail.util.ByteArrayDataSource;
import javax.xml.bind.JAXBElement;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apromore.anf.AnnotationsType;
import org.apromore.canoniser.Canoniser;
import org.apromore.canoniser.exception.CanoniserException;
import org.apromore.canoniser.result.CanoniserMetadataResult;
import org.apromore.common.Constants;
import org.apromore.cpf.CanonicalProcessType;
import org.apromore.dao.model.Cluster;
import org.apromore.dao.model.ClusteringSummary;
import org.apromore.dao.model.EditSession;
import org.apromore.dao.model.NativeType;
import org.apromore.dao.model.ProcessModelVersion;
import org.apromore.dao.model.User;
import org.apromore.exception.ExportFormatException;
import org.apromore.exception.RepositoryException;
import org.apromore.manager.client.helper.CanoniserHelper;
import org.apromore.manager.client.helper.PluginHelper;
import org.apromore.mapper.ClusterMapper;
import org.apromore.mapper.DomainMapper;
import org.apromore.mapper.NativeTypeMapper;
import org.apromore.mapper.UserMapper;
import org.apromore.mapper.WorkspaceMapper;
import org.apromore.model.AddProcessToFolderInputMsgType;
import org.apromore.model.AddProcessToFolderOutputMsgType;
import org.apromore.model.ClusterSummaryType;
import org.apromore.model.ClusterType;
import org.apromore.model.ClusteringSummaryType;
import org.apromore.model.CreateClustersInputMsgType;
import org.apromore.model.CreateClustersOutputMsgType;
import org.apromore.model.CreateFolderInputMsgType;
import org.apromore.model.CreateFolderOutputMsgType;
import org.apromore.model.CreateGEDMatrixInputMsgType;
import org.apromore.model.CreateGEDMatrixOutputMsgType;
import org.apromore.model.DeleteEditSessionInputMsgType;
import org.apromore.model.DeleteEditSessionOutputMsgType;
import org.apromore.model.DeleteFolderInputMsgType;
import org.apromore.model.DeleteFolderOutputMsgType;
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
import org.apromore.model.ExportFragmentResultType;
import org.apromore.model.FolderType;
import org.apromore.model.GetBreadcrumbsInputMsgType;
import org.apromore.model.GetBreadcrumbsOutputMsgType;
import org.apromore.model.GetClusterInputMsgType;
import org.apromore.model.GetClusterOutputMsgType;
import org.apromore.model.GetClusterSummariesInputMsgType;
import org.apromore.model.GetClusterSummariesOutputMsgType;
import org.apromore.model.GetClusteringSummaryInputMsgType;
import org.apromore.model.GetClusteringSummaryOutputMsgType;
import org.apromore.model.GetClustersRequestType;
import org.apromore.model.GetClustersResponseType;
import org.apromore.model.GetFolderUsersInputMsgType;
import org.apromore.model.GetFolderUsersOutputMsgType;
import org.apromore.model.GetFragmentInputMsgType;
import org.apromore.model.GetFragmentOutputMsgType;
import org.apromore.model.GetPairwiseDistancesInputMsgType;
import org.apromore.model.GetPairwiseDistancesOutputMsgType;
import org.apromore.model.GetProcessUsersInputMsgType;
import org.apromore.model.GetProcessUsersOutputMsgType;
import org.apromore.model.GetProcessesInputMsgType;
import org.apromore.model.GetProcessesOutputMsgType;
import org.apromore.model.GetSubFoldersInputMsgType;
import org.apromore.model.GetSubFoldersOutputMsgType;
import org.apromore.model.GetWorkspaceFolderTreeInputMsgType;
import org.apromore.model.GetWorkspaceFolderTreeOutputMsgType;
import org.apromore.model.ImportProcessInputMsgType;
import org.apromore.model.ImportProcessOutputMsgType;
import org.apromore.model.ImportProcessResultType;
import org.apromore.model.LoginInputMsgType;
import org.apromore.model.LoginOutputMsgType;
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
import org.apromore.model.RemoveFolderPermissionsInputMsgType;
import org.apromore.model.RemoveFolderPermissionsOutputMsgType;
import org.apromore.model.RemoveProcessPermissionsInputMsgType;
import org.apromore.model.RemoveProcessPermissionsOutputMsgType;
import org.apromore.model.ResultType;
import org.apromore.model.SaveFolderPermissionsInputMsgType;
import org.apromore.model.SaveFolderPermissionsOutputMsgType;
import org.apromore.model.SaveProcessPermissionsInputMsgType;
import org.apromore.model.SaveProcessPermissionsOutputMsgType;
import org.apromore.model.SearchForSimilarProcessesInputMsgType;
import org.apromore.model.SearchForSimilarProcessesOutputMsgType;
import org.apromore.model.SearchUserInputMsgType;
import org.apromore.model.SearchUserOutputMsgType;
import org.apromore.model.UpdateFolderInputMsgType;
import org.apromore.model.UpdateFolderOutputMsgType;
import org.apromore.model.UpdateProcessInputMsgType;
import org.apromore.model.UpdateProcessOutputMsgType;
import org.apromore.model.UserFolderType;
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
import org.apromore.plugin.PluginRequestImpl;
import org.apromore.plugin.deployment.DeploymentPlugin;
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
import org.apromore.service.SecurityService;
import org.apromore.service.SessionService;
import org.apromore.service.SimilarityService;
import org.apromore.service.UserService;
import org.apromore.service.WorkspaceService;
import org.apromore.service.helper.UserInterfaceHelper;
import org.apromore.service.model.CanonisedProcess;
import org.apromore.service.model.ClusterFilter;
import org.apromore.service.model.ClusterSettings;
import org.apromore.service.model.DecanonisedProcess;
import org.apromore.service.model.NameValuePair;
import org.apromore.toolbox.clustering.algorithm.dbscan.FragmentPair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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

    private DeploymentService deploymentService;
    private PluginService pluginService;
    private FragmentService fragmentSrv;
    private CanoniserService canoniserService;
    private ProcessService procSrv;
    private ClusterService clusterService;
    private FormatService frmSrv;
    private DomainService domSrv;
    private UserService userSrv;
    private SimilarityService simSrv;
    private MergeService merSrv;
    private SessionService sesSrv;
    private SecurityService secSrv;
    private WorkspaceService workspaceSrv;
    private UserInterfaceHelper uiHelper;


    /**
     * Default Constructor for use with CGLib.
     */
    public ManagerPortalEndpoint() { }

    /**
     * Default Constructor allowing Spring to Autowire for testing and normal use.
     * @param deploymentService Deployment Service.
     * @param pluginService plugin Service.
     * @param fragmentSrv Fragment Service.
     * @param canoniserService Canoniser Service.
     * @param procSrv process Service.
     * @param clusterService Cluster Service.
     * @param frmSrv Format Service.
     * @param domSrv domain Service.
     * @param userSrv User Service.
     * @param simSrv Similarity Service.
     * @param merSrv Merge Service.
     * @param sesSrv Session Service.
     * @param secSrv security Service.
     * @param wrkSrv workspace service.
     * @param uiHelper UI Helper.
     */
    @Inject
    public ManagerPortalEndpoint(final DeploymentService deploymentService, final PluginService pluginService,
            final FragmentService fragmentSrv, final CanoniserService canoniserService, final ProcessService procSrv,
            final ClusterService clusterService, final FormatService frmSrv, final DomainService domSrv,
            final UserService userSrv, final SimilarityService simSrv, final MergeService merSrv, final SessionService sesSrv,
            final SecurityService secSrv, final WorkspaceService wrkSrv, final UserInterfaceHelper uiHelper) {
        this.deploymentService = deploymentService;
        this.pluginService = pluginService;
        this.fragmentSrv = fragmentSrv;
        this.canoniserService = canoniserService;
        this.procSrv = procSrv;
        this.clusterService = clusterService;
        this.frmSrv = frmSrv;
        this.domSrv = domSrv;
        this.userSrv = userSrv;
        this.simSrv = simSrv;
        this.merSrv = merSrv;
        this.sesSrv = sesSrv;
        this.secSrv = secSrv;
        this.workspaceSrv = wrkSrv;
        this.uiHelper = uiHelper;
    }



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
            Double preVersion = payload.getPreVersion();
            Double newVersion = payload.getNewVersion();
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
            Integer folderId = payload.getFolderId();
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
                id.setBranchName(t.getBranchName());
                id.setVersionNumber(t.getVersionNumber());
                ids.getProcessVersionId().add(id);
            }
            ProcessSummaryType respFromToolbox = merSrv.mergeProcesses(processName, version, domain, username, algo, folderId, parameters, ids);
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
        //WriteAnnotationInputMsgType payload = req.getValue();
        WriteAnnotationOutputMsgType res = new WriteAnnotationOutputMsgType();
        ResultType result = new ResultType();
        res.setResult(result);
        try {
//            Integer editSessionCode = payload.getEditSessionCode();
//            String annotName = payload.getAnnotationName();
//            Integer processId = payload.getProcessId();
//            String version = payload.getVersion();
//            String nat_type = payload.getNativeType();
//            Boolean isNew = payload.isIsNew();
//            DataHandler handler = payload.getNative();
//            InputStream native_is = handler.getInputStream();
//            // TODO use CanoniserService instead
//            caClient.GenerateAnnotation(annotName, editSessionCode, isNew, processId, version, nat_type, native_is);
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
        //String res = "received: " + payload;
    }

    @PayloadRoot(namespace = NAMESPACE, localPart = "ReadAllUsersRequest")
    @ResponsePayload
    public JAXBElement<ReadAllUsersOutputMsgType> readAllUsers(@RequestPayload final JAXBElement<ReadAllUsersInputMsgType> message) {
        LOGGER.info("Executing operation readAllUsers");
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
            List<NameValuePair> processVersions = new ArrayList<>(0);
            for (final ProcessVersionIdentifierType p : payload.getProcessVersionIdentifier()) {
                processVersions.add(new NameValuePair(p.getProcessName(), p.getBranchName(), p.getVersionNumber()));
            }
            procSrv.deleteProcessModel(processVersions);

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

            EditSessionType editType = payload.getEditSession();
            InputStream native_is = payload.getNative().getInputStream();
            NativeType natType = frmSrv.findNativeType(editType.getNativeType());

            Set<RequestParameterType<?>> canoniserProperties = new HashSet<RequestParameterType<?>>(0);
            CanonisedProcess canonisedProcess = canoniserService.canonise(editType.getNativeType(), native_is, canoniserProperties);

            procSrv.updateProcess(editType.getProcessId(), editType.getProcessName(), editType.getOriginalBranchName(), editType.getNewBranchName(),
                    editType.getVersionNumber(), editType.getOriginalVersionNumber(), editType.isCreateNewBranch(),
                    secSrv.getUserByName(editType.getUsername()), Constants.LOCKED, natType, canonisedProcess);

            result.setCode(0);
            result.setMessage("");
        } catch (Exception ex) {
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
            editSessionP.setVersionNumber(session.getVersionNumber());
            editSessionP.setProcessName(session.getProcess().getName());
            editSessionP.setOriginalBranchName(session.getOriginalBranchName());
            editSessionP.setNewBranchName(session.getNewBranchName());
            editSessionP.setCreateNewBranch(session.getCreateNewBranch());
            editSessionP.setDomain(session.getProcess().getDomain());
            editSessionP.setCreationDate(session.getCreateDate());
            editSessionP.setLastUpdate(session.getLastUpdateDate());
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

        try {
            EditSession session = sesSrv.createSession(payload.getEditSession());
            res.setEditSessionCode(session.getId());
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
    public JAXBElement<GetFragmentOutputMsgType> getFragment(@RequestPayload final JAXBElement<GetFragmentInputMsgType> req) {
        LOGGER.info("Executing operation getFragment");

        String defaultFormat = "EPML 2.0";

        ResultType result = new ResultType();
        GetFragmentInputMsgType payload = req.getValue();
        GetFragmentOutputMsgType res = new GetFragmentOutputMsgType();

        Integer fragmentId = payload.getFragmentId();

        try {
            AnnotationsType anf = null;
            CanonicalProcessType cpt = fragmentSrv.getFragmentToCanonicalProcessType(fragmentId);
            DecanonisedProcess dp = canoniserService.deCanonise(defaultFormat, cpt, anf, new HashSet<RequestParameterType<?>>());

            ExportFragmentResultType exportResult = new ExportFragmentResultType();
            exportResult.setMessage(PluginHelper.convertFromPluginMessages(dp.getMessages()));
            exportResult.setNative(new DataHandler(new ByteArrayDataSource(dp.getNativeFormat(), Constants.XML_MIMETYPE)));

            res.setFragmentResult(exportResult);
            res.setNativeType(defaultFormat);

            result.setCode(0);
            result.setMessage("");
        } catch (CanoniserException | IOException e) {
            LOGGER.error("Failed to load Fragment with Id: " + fragmentId);
            result.setCode(-1);
            result.setMessage(e.getMessage());
        }

        res.setResult(result);
        return WS_OBJECT_FACTORY.createGetFragmentResponse(res);
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
            String branch = payload.getBranchName();
            Double version = payload.getVersionNumber();
            String format = payload.getFormat();
            String annName = payload.getAnnotationName();
            boolean withAnn = payload.isWithAnnotations();

            Set<RequestParameterType<?>> requestProperties = PluginHelper.convertToRequestParameters(payload.getCanoniserParameters());
            ExportFormatResultType exportResult = procSrv.exportProcess(name, processId, branch, version, format, annName, withAnn, requestProperties);
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
            Integer folderId = editSession.getFolderId();
            String username = editSession.getUsername();
            String processName = editSession.getProcessName();
            Double versionNumber = editSession.getVersionNumber();
            String nativeType = editSession.getNativeType();
            String domain = editSession.getDomain();
            String creationDate = editSession.getCreationDate();
            String lastUpdate = editSession.getLastUpdate();

            DataHandler handler = payload.getProcessDescription();
            PluginParameters xmlCanoniserProperties = payload.getCanoniserParameters();

            Set<RequestParameterType<?>> canoniserProperties = PluginHelper.convertToRequestParameters(xmlCanoniserProperties);
            CanonisedProcess canonisedProcess = canoniserService.canonise(nativeType, handler.getInputStream(), canoniserProperties);
            ProcessModelVersion pmv = procSrv.importProcess(username, folderId, processName, versionNumber, nativeType, canonisedProcess,
                    domain, "", creationDate, lastUpdate);
            ProcessSummaryType process = uiHelper.createProcessSummary(pmv.getProcessBranch().getProcess(), pmv.getProcessBranch(), pmv,
                    nativeType, domain, creationDate, lastUpdate, username);

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

    @PayloadRoot(localPart = "CreateGEDMatrixRequest", namespace = NAMESPACE)
    @ResponsePayload
    public JAXBElement<CreateGEDMatrixOutputMsgType> createGedMatrix(@RequestPayload final JAXBElement<CreateGEDMatrixInputMsgType> req) {
        LOGGER.info("Executing operation createGedMatrix");
        CreateGEDMatrixInputMsgType payload = req.getValue();
        CreateGEDMatrixOutputMsgType res = new CreateGEDMatrixOutputMsgType();

        clusterService.computeGEDMatrix();

        return WS_OBJECT_FACTORY.createCreateGEDMatrixResponse(res);
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
        //GetClusteringSummaryInputMsgType payload = req.getValue();
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
            UserType newUser = UserMapper.convertUserTypes(secSrv.createUser(UserMapper.convertFromUserType(payload.getUser())));
            result.setCode(0);
            result.setMessage("");
            res.setUser(newUser);
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
            UserType user = UserMapper.convertUserTypes(secSrv.getUserByName(payload.getUsername()));
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

    /* (non-Javadoc)
    * @see org.apromore.manager.service.ManagerPortalPortType#readUser(ReadUserInputMsgType  payload )
    */
    @PayloadRoot(localPart = "SearchUserRequest", namespace = NAMESPACE)
    @ResponsePayload
    public JAXBElement<SearchUserOutputMsgType> searchUsers(@RequestPayload final JAXBElement<SearchUserInputMsgType> req) {
        LOGGER.info("Executing operation searchUser");
        SearchUserInputMsgType payload = req.getValue();
        SearchUserOutputMsgType res = new SearchUserOutputMsgType();
        ResultType result = new ResultType();
        res.setResult(result);
        try {
            List<User> users = secSrv.searchUsers(payload.getSearchString());
            for (User user : users){
                res.getUsers().add(UserMapper.convertUserTypes(user));
            }
            result.setCode(0);
            result.setMessage("");
        } catch (Exception ex) {
            ex.printStackTrace();
            result.setCode(-1);
            result.setMessage(ex.getMessage());
        }
        return new ObjectFactory().createSearchUserResponse(res);
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
            for (Canoniser c : cList) {
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
            Canoniser c;
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
            Canoniser c;
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
            for (DeploymentPlugin d : dList) {
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
        return WS_OBJECT_FACTORY.createReadDeploymentPluginInfoResponse(res);
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
            String branchName = req.getValue().getBranchName();
            PluginParameters deploymentProperties = req.getValue().getDeploymentParameters();
            Set<RequestParameterType<?>> requestProperties = PluginHelper.convertToRequestParameters(deploymentProperties);
            String deploymentPluginName = req.getValue().getDeploymentPluginName();
            String deploymentPluginVersion = req.getValue().getDeploymentPluginVersion();

            CanonicalProcessType canonialProcess = procSrv.getCurrentProcessModel(processName, branchName, false);

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

    @PayloadRoot(localPart = "LoginRequest", namespace = NAMESPACE)
    @ResponsePayload
    public JAXBElement<LoginOutputMsgType> login(@RequestPayload final JAXBElement<LoginInputMsgType> req) {
        LOGGER.info("Executing operation login");
        LoginInputMsgType payload = req.getValue();
        LoginOutputMsgType res = new LoginOutputMsgType();
        ResultType result = new ResultType();
        res.setResult(result);
        try {
            UserType user = UserMapper.convertUserTypes(secSrv.login(payload.getUsername(), payload.getPassword()));
            res.setUser(user);
            result.setCode(0);
            result.setMessage("");
        } catch (Exception ex) {
            ex.printStackTrace();
            result.setCode(0);
            result.setMessage(ex.getMessage());
        }
        return new ObjectFactory().createLoginResponse(res);
    }

    @PayloadRoot(localPart = "GetWorkspaceFolderTreeRequest", namespace = NAMESPACE)
    @ResponsePayload
    public JAXBElement<GetWorkspaceFolderTreeOutputMsgType> getWorkspaceFolderTree(@RequestPayload final JAXBElement<GetWorkspaceFolderTreeInputMsgType> req) {
        LOGGER.info("Executing operation getWorkspaceFolderTree");
        GetWorkspaceFolderTreeInputMsgType payload = req.getValue();
        GetWorkspaceFolderTreeOutputMsgType res = new GetWorkspaceFolderTreeOutputMsgType();
        ResultType result = new ResultType();
        res.setResult(result);

        List<FolderType> folderTypes = WorkspaceMapper.convertFolderTreeNodesToFolderTypes(workspaceSrv.getWorkspaceFolderTree(payload.getUserId()));
        for (FolderType ft : folderTypes) {
            res.getFolders().add(ft);
        }
        return new ObjectFactory().createGetWorkspaceFolderTreeResponse(res);
    }

    @PayloadRoot(localPart = "GetSubFoldersRequest", namespace = NAMESPACE)
    @ResponsePayload
    public JAXBElement<GetSubFoldersOutputMsgType> getSubFolders(@RequestPayload final JAXBElement<GetSubFoldersInputMsgType> req) {
        LOGGER.info("Executing operation getSubFolders");
        GetSubFoldersInputMsgType payload = req.getValue();
        GetSubFoldersOutputMsgType res = new GetSubFoldersOutputMsgType();
        ResultType result = new ResultType();
        res.setResult(result);

        List<FolderType> folderTypes = WorkspaceMapper.convertFoldersToFolderTypes(workspaceSrv.getSubFolders(payload.getUserId(), payload.getFolderId()));
        for (FolderType ft : folderTypes) {
            res.getFolders().add(ft);
        }
        return new ObjectFactory().createGetSubFoldersResponse(res);
    }

    @PayloadRoot(localPart = "GetBreadcrumbsRequest", namespace = NAMESPACE)
    @ResponsePayload
    public JAXBElement<GetBreadcrumbsOutputMsgType> getBreadcrumbs(@RequestPayload final JAXBElement<GetBreadcrumbsInputMsgType> req) {
        LOGGER.info("Executing operation getBreadcrumbs");
        GetBreadcrumbsInputMsgType payload = req.getValue();
        GetBreadcrumbsOutputMsgType res = new GetBreadcrumbsOutputMsgType();
        ResultType result = new ResultType();
        res.setResult(result);

        List<FolderType> folderTypes = WorkspaceMapper.convertFolderListToFolderTypes(workspaceSrv.getBreadcrumbs(payload.getUserId(), payload.getFolderId()));
        for (FolderType ft : folderTypes) {
            res.getFolders().add(ft);
        }
        return new ObjectFactory().createGetBreadcrumbsResponse(res);
    }

    @PayloadRoot(localPart = "GetFolderUsersRequest", namespace = NAMESPACE)
    @ResponsePayload
    public JAXBElement<GetFolderUsersOutputMsgType> getFolderUsers(@RequestPayload final JAXBElement<GetFolderUsersInputMsgType> req) {
        LOGGER.info("Executing operation getFolderUsers");
        GetFolderUsersInputMsgType payload = req.getValue();
        GetFolderUsersOutputMsgType res = new GetFolderUsersOutputMsgType();
        ResultType result = new ResultType();
        res.setResult(result);

        List<UserFolderType> userFolderTypes = WorkspaceMapper.convertFolderUsersToFolderUserTypes(workspaceSrv.getFolderUsers(payload.getFolderId()));
        for (UserFolderType ft : userFolderTypes) {
            res.getUsers().add(ft);
        }
        return new ObjectFactory().createGetFolderUsersResponse(res);
    }

    @PayloadRoot(localPart = "SaveFolderPermissionsRequest", namespace = NAMESPACE)
    @ResponsePayload
    public JAXBElement<SaveFolderPermissionsOutputMsgType> saveFolderPermissions(@RequestPayload final JAXBElement<SaveFolderPermissionsInputMsgType> req) {
        LOGGER.info("Executing operation getFolderUsers");
        SaveFolderPermissionsInputMsgType payload = req.getValue();
        SaveFolderPermissionsOutputMsgType res = new SaveFolderPermissionsOutputMsgType();
        ResultType result = new ResultType();
        res.setResult(result);

        String message = workspaceSrv.saveFolderPermissions(payload.getFolderId(), payload.getUserId(), payload.isHasRead(), payload.isHasRead(), payload.isHasOwnership());
        res.setMessage(message);
        return new ObjectFactory().createSaveFolderPermissionsResponse(res);
    }

    @PayloadRoot(localPart = "SaveProcessPermissionsRequest", namespace = NAMESPACE)
    @ResponsePayload
    public JAXBElement<SaveProcessPermissionsOutputMsgType> saveProcessPermissions(@RequestPayload final JAXBElement<SaveProcessPermissionsInputMsgType> req) {
        LOGGER.info("Executing operation getFolderUsers");
        SaveProcessPermissionsInputMsgType payload = req.getValue();
        SaveProcessPermissionsOutputMsgType res = new SaveProcessPermissionsOutputMsgType();
        ResultType result = new ResultType();
        res.setResult(result);

        String message = workspaceSrv.saveProcessPermissions(payload.getProcessId(), payload.getUserId(), payload.isHasRead(), payload.isHasRead(), payload.isHasOwnership());
        res.setMessage(message);
        return new ObjectFactory().createSaveProcessPermissionsResponse(res);
    }

    @PayloadRoot(localPart = "RemoveFolderPermissionsRequest", namespace = NAMESPACE)
    @ResponsePayload
    public JAXBElement<RemoveFolderPermissionsOutputMsgType> removeFolderPermissions(@RequestPayload final JAXBElement<RemoveFolderPermissionsInputMsgType> req) {
        LOGGER.info("Executing operation removeFolderPermissions");
        RemoveFolderPermissionsInputMsgType payload = req.getValue();
        RemoveFolderPermissionsOutputMsgType res = new RemoveFolderPermissionsOutputMsgType();
        ResultType result = new ResultType();
        res.setResult(result);

        String message = workspaceSrv.removeFolderPermissions(payload.getFolderId(), payload.getUserId());
        res.setMessage(message);
        return new ObjectFactory().createRemoveFolderPermissionsResponse(res);
    }

    @PayloadRoot(localPart = "RemoveProcessPermissionsRequest", namespace = NAMESPACE)
    @ResponsePayload
    public JAXBElement<RemoveProcessPermissionsOutputMsgType> removeProcessPermissions(@RequestPayload final JAXBElement<RemoveProcessPermissionsInputMsgType> req) {
        LOGGER.info("Executing operation removeProcessPermissions");
        RemoveProcessPermissionsInputMsgType payload = req.getValue();
        RemoveProcessPermissionsOutputMsgType res = new RemoveProcessPermissionsOutputMsgType();
        ResultType result = new ResultType();
        res.setResult(result);

        String message = workspaceSrv.removeProcessPermissions(payload.getProcessId(), payload.getUserId());
        res.setMessage(message);
        return new ObjectFactory().createRemoveProcessPermissionsResponse(res);
    }

    @PayloadRoot(localPart = "GetProcessUsersRequest", namespace = NAMESPACE)
    @ResponsePayload
    public JAXBElement<GetProcessUsersOutputMsgType> getProcessUsers(@RequestPayload final JAXBElement<GetProcessUsersInputMsgType> req) {
        LOGGER.info("Executing operation getProcessUsers");
        GetProcessUsersInputMsgType payload = req.getValue();
        GetProcessUsersOutputMsgType res = new GetProcessUsersOutputMsgType();
        ResultType result = new ResultType();
        res.setResult(result);

        List<UserFolderType> userFolderTypes = WorkspaceMapper.convertProcessUsersToFolderUserTypes(workspaceSrv.getProcessUsers(payload.getProcessId()));
        for (UserFolderType ft : userFolderTypes) {
            res.getUsers().add(ft);
        }
        return new ObjectFactory().createGetProcessUsersResponse(res);
    }

    @PayloadRoot(localPart = "GetProcessesRequest", namespace = NAMESPACE)
    @ResponsePayload
    public JAXBElement<GetProcessesOutputMsgType> getProcesses(@RequestPayload final JAXBElement<GetProcessesInputMsgType> req) {
        LOGGER.info("Executing operation getProcesses");
        GetProcessesInputMsgType payload = req.getValue();
        GetProcessesOutputMsgType res = new GetProcessesOutputMsgType();
        ResultType result = new ResultType();
        res.setResult(result);

        ProcessSummariesType processTypes = uiHelper.buildProcessSummaryList(payload.getUserId(), payload.getFolderId(), null); //WorkspaceMapper.convertProcessUsersToProcessSummaryTypes(workspaceSrv.getUserProcesses(payload.getUserId(), payload.getFolderId()));
        for (ProcessSummaryType pt : processTypes.getProcessSummary()) {
            res.getProcesses().add(pt);
        }
        return new ObjectFactory().createGetProcessesResponse(res);
    }

    @PayloadRoot(localPart = "CreateFolderRequest", namespace = NAMESPACE)
    @ResponsePayload
    public JAXBElement<CreateFolderOutputMsgType> createFolder(@RequestPayload final JAXBElement<CreateFolderInputMsgType> req) {
        LOGGER.info("Executing operation createFolder");
        CreateFolderInputMsgType payload = req.getValue();
        CreateFolderOutputMsgType res = new CreateFolderOutputMsgType();
        ResultType result = new ResultType();
        res.setResult(result);

        workspaceSrv.createFolder(payload.getUserId(), payload.getFolderName(), payload.getParentFolderId());

        return new ObjectFactory().createCreateFolderResponse(res);
    }

    @PayloadRoot(localPart = "AddProcessToFolderRequest", namespace = NAMESPACE)
    @ResponsePayload
    public JAXBElement<AddProcessToFolderOutputMsgType> addProcessToFolder(@RequestPayload final JAXBElement<AddProcessToFolderInputMsgType> req) {
        LOGGER.info("Executing operation addProcessToFolder");
        AddProcessToFolderInputMsgType payload = req.getValue();
        AddProcessToFolderOutputMsgType res = new AddProcessToFolderOutputMsgType();
        ResultType result = new ResultType();
        res.setResult(result);

        workspaceSrv.addProcessToFolder(payload.getProcessId(), payload.getFolderId());

        return new ObjectFactory().createAddProcessToFolderResponse(res);
    }

    @PayloadRoot(localPart = "UpdateFolderRequest", namespace = NAMESPACE)
    @ResponsePayload
    public JAXBElement<UpdateFolderOutputMsgType> updateFolder(@RequestPayload final JAXBElement<UpdateFolderInputMsgType> req) {
        LOGGER.info("Executing operation updateFolder");
        UpdateFolderInputMsgType payload = req.getValue();
        UpdateFolderOutputMsgType res = new UpdateFolderOutputMsgType();
        ResultType result = new ResultType();
        res.setResult(result);

        workspaceSrv.updateFolder(payload.getFolderId(), payload.getFolderName());

        return new ObjectFactory().createUpdateFolderResponse(res);
    }

    @PayloadRoot(localPart = "DeleteFolderRequest", namespace = NAMESPACE)
    @ResponsePayload
    public JAXBElement<DeleteFolderOutputMsgType> deleteFolder(@RequestPayload final JAXBElement<DeleteFolderInputMsgType> req) {
        LOGGER.info("Executing operation deleteFolder");
        DeleteFolderInputMsgType payload = req.getValue();
        DeleteFolderOutputMsgType res = new DeleteFolderOutputMsgType();
        ResultType result = new ResultType();
        res.setResult(result);

        workspaceSrv.deleteFolder(payload.getFolderId());

        return new ObjectFactory().createDeleteFolderResponse(res);
    }

}
