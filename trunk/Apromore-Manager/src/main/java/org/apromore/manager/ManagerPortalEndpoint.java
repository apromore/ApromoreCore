/*
 * Copyright © 2009-2014 The Apromore Initiative.
 *
 * This file is part of "Apromore".
 *
 * "Apromore" is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 3 of the License, or
 * (at your option) any later version.
 *
 * "Apromore" is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.
 * If not, see <http://www.gnu.org/licenses/lgpl-3.0.html>.
 */

package org.apromore.manager;

import org.apromore.canoniser.Canoniser;
import org.apromore.canoniser.exception.CanoniserException;
import org.apromore.canoniser.result.CanoniserMetadataResult;
import org.apromore.common.Constants;
import org.apromore.cpf.CanonicalProcessType;
import org.apromore.helper.Version;
import org.apromore.dao.model.Cluster;
import org.apromore.dao.model.ClusteringSummary;
import org.apromore.dao.model.Group;
import org.apromore.dao.model.HistoryEnum;
import org.apromore.dao.model.HistoryEvent;
import org.apromore.dao.model.NativeType;
import org.apromore.dao.model.ProcessModelVersion;
import org.apromore.dao.model.StatusEnum;
import org.apromore.dao.model.User;
import org.apromore.exception.ExportFormatException;
import org.apromore.exception.RepositoryException;
import org.apromore.helper.CanoniserHelper;
import org.apromore.helper.PluginHelper;
import org.apromore.mapper.ClusterMapper;
import org.apromore.mapper.DomainMapper;
import org.apromore.mapper.GroupMapper;
import org.apromore.mapper.NativeTypeMapper;
import org.apromore.mapper.SearchHistoryMapper;
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
import org.apromore.model.GedMatrixSummaryType;
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
import org.apromore.model.GetFolderGroupsInputMsgType;
import org.apromore.model.GetFolderGroupsOutputMsgType;
import org.apromore.model.GetFragmentInputMsgType;
import org.apromore.model.GetFragmentOutputMsgType;
import org.apromore.model.GetGedMatrixSummaryInputMsgType;
import org.apromore.model.GetGedMatrixSummaryOutputMsgType;
import org.apromore.model.GetPairwiseDistancesInputMsgType;
import org.apromore.model.GetPairwiseDistancesOutputMsgType;
import org.apromore.model.GetProcessGroupsInputMsgType;
import org.apromore.model.GetProcessGroupsOutputMsgType;
import org.apromore.model.GetProcessesInputMsgType;
import org.apromore.model.GetProcessesOutputMsgType;
import org.apromore.model.GetSubFoldersInputMsgType;
import org.apromore.model.GetSubFoldersOutputMsgType;
import org.apromore.model.GetWorkspaceFolderTreeInputMsgType;
import org.apromore.model.GetWorkspaceFolderTreeOutputMsgType;
import org.apromore.model.GroupAccessType;
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
import org.apromore.model.ReadUserByEmailInputMsgType;
import org.apromore.model.ReadUserByEmailOutputMsgType;
import org.apromore.model.ReadUserByUsernameInputMsgType;
import org.apromore.model.ReadUserByUsernameOutputMsgType;
import org.apromore.model.RemoveFolderPermissionsInputMsgType;
import org.apromore.model.RemoveFolderPermissionsOutputMsgType;
import org.apromore.model.RemoveProcessPermissionsInputMsgType;
import org.apromore.model.RemoveProcessPermissionsOutputMsgType;
import org.apromore.model.ResetUserPasswordInputMsgType;
import org.apromore.model.ResetUserPasswordOutputMsgType;
import org.apromore.model.ResultType;
import org.apromore.model.RunAPQLInputMsgType;
import org.apromore.model.RunAPQLOutputMsgType;
import org.apromore.model.SaveFolderPermissionsInputMsgType;
import org.apromore.model.SaveFolderPermissionsOutputMsgType;
import org.apromore.model.SaveProcessPermissionsInputMsgType;
import org.apromore.model.SaveProcessPermissionsOutputMsgType;
import org.apromore.model.SearchForSimilarProcessesInputMsgType;
import org.apromore.model.SearchForSimilarProcessesOutputMsgType;
import org.apromore.model.SearchGroupsInputMsgType;
import org.apromore.model.SearchGroupsOutputMsgType;
import org.apromore.model.SearchUserInputMsgType;
import org.apromore.model.SearchUserOutputMsgType;
import org.apromore.model.UpdateFolderInputMsgType;
import org.apromore.model.UpdateFolderOutputMsgType;
import org.apromore.model.UpdateProcessInputMsgType;
import org.apromore.model.UpdateProcessOutputMsgType;
import org.apromore.model.UpdateSearchHistoryInputMsgType;
import org.apromore.model.UpdateSearchHistoryOutputMsgType;
import org.apromore.model.UserType;
import org.apromore.model.UsernamesType;
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
import org.apromore.service.SimilarityService;
import org.apromore.service.UserService;
import org.apromore.service.WorkspaceService;
import org.apromore.service.helper.UserInterfaceHelper;
import org.apromore.service.model.CanonisedProcess;
import org.apromore.service.model.ClusterFilter;
import org.apromore.service.model.ClusterSettings;
import org.apromore.service.model.DecanonisedProcess;
import org.apromore.service.model.ProcessData;
import org.apromore.toolbox.clustering.algorithm.dbscan.FragmentPair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ws.server.endpoint.annotation.Endpoint;
import org.springframework.ws.server.endpoint.annotation.PayloadRoot;
import org.springframework.ws.server.endpoint.annotation.RequestPayload;
import org.springframework.ws.server.endpoint.annotation.ResponsePayload;

import javax.activation.DataHandler;
import javax.inject.Inject;
import javax.mail.util.ByteArrayDataSource;
import javax.xml.bind.JAXBElement;
import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

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
     * @param secSrv security Service.
     * @param wrkSrv workspace service.
     * @param uiHelper UI Helper.
     */
    @Inject
    public ManagerPortalEndpoint(final DeploymentService deploymentService, final PluginService pluginService,
            final FragmentService fragmentSrv, final CanoniserService canoniserService, final ProcessService procSrv,
            final ClusterService clusterService, final FormatService frmSrv, final DomainService domSrv,
            final UserService userSrv, final SimilarityService simSrv, final MergeService merSrv,
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
        this.secSrv = secSrv;
        this.workspaceSrv = wrkSrv;
        this.uiHelper = uiHelper;
    }



    @PayloadRoot(namespace = NAMESPACE, localPart = "EditProcessDataRequest")
    @ResponsePayload
    public JAXBElement<EditProcessDataOutputMsgType> editProcessData(@RequestPayload final JAXBElement<EditProcessDataInputMsgType> req) {
        LOGGER.trace("Executing operation editDataProcess");
        EditProcessDataInputMsgType payload = req.getValue();
        EditProcessDataOutputMsgType res = new EditProcessDataOutputMsgType();
        ResultType result = new ResultType();
        res.setResult(result);
        try {
            Integer processId = payload.getId();
            String processName = payload.getProcessName();
            String domain = payload.getDomain();
            String username = payload.getOwner();
            Version preVersion = new Version(payload.getPreVersion());
            Version newVersion = new Version(payload.getNewVersion());
            String ranking = payload.getRanking();
            boolean isPublic = payload.isMakePublic();

            procSrv.updateProcessMetaData(processId, processName, domain, username, preVersion, newVersion, ranking, isPublic);

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
        LOGGER.trace("Executing operation mergeProcesses");
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
            boolean makePublic = payload.isMakePublic();
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
            ProcessModelVersion pmv = merSrv.mergeProcesses(processName, version, domain, username, algo, folderId, parameters, ids, makePublic);
            ProcessSummaryType process = uiHelper.createProcessSummary(pmv.getProcessBranch().getProcess(), pmv.getProcessBranch(), pmv,
                    "", domain, pmv.getCreateDate(), pmv.getLastUpdateDate(), username, makePublic);

            res.setProcessSummary(process);
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
        LOGGER.trace("Executing operation searchForSimilarProcesses");
        SearchForSimilarProcessesInputMsgType payload = req.getValue();
        SearchForSimilarProcessesOutputMsgType res = new SearchForSimilarProcessesOutputMsgType();
        ResultType result = new ResultType();
        res.setResult(result);
        try {
            String algo = payload.getAlgorithm();
            Integer processId = payload.getProcessId();
            String versionName = payload.getVersionName();
            Boolean latestVersions = payload.isLatestVersions();
            Integer folderId = payload.getFolderId();
            String userId = payload.getUserId();
            ParametersType paramsT = new ParametersType();
            for (ParameterType p : payload.getParameters().getParameter()) {
                ParameterType paramT = new ParameterType();
                paramsT.getParameter().add(paramT);
                paramT.setName(p.getName());
                paramT.setValue(p.getValue());
            }

            ProcessSummariesType processes = simSrv.SearchForSimilarProcesses(processId, versionName, latestVersions, folderId, userId,
                    algo, paramsT);

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

    @PayloadRoot(namespace = NAMESPACE, localPart = "GetAllClustersRequest")
    public void getAllClusters(@RequestPayload final JAXBElement<String> message) {
        LOGGER.trace("Retrieving all clusters in the repository ...");
        String payload = message.getValue();
        //String res = "received: " + payload;
    }

    @PayloadRoot(namespace = NAMESPACE, localPart = "ReadAllUsersRequest")
    @ResponsePayload
    public JAXBElement<ReadAllUsersOutputMsgType> readAllUsers(@RequestPayload final JAXBElement<ReadAllUsersInputMsgType> message) {
        LOGGER.trace("Executing operation readAllUsers");
        ReadAllUsersOutputMsgType res = new ReadAllUsersOutputMsgType();
        ResultType result = new ResultType();
        res.setResult(result);
        UsernamesType allUsers = UserMapper.convertUsernameTypes(userSrv.findAllUsers());
        res.setUsernames(allUsers);
        result.setCode(0);
        result.setMessage("");
        return WS_OBJECT_FACTORY.createReadAllUsersResponse(res);
    }


    @PayloadRoot(localPart = "DeleteProcessVersionsRequest", namespace = NAMESPACE)
    @ResponsePayload
    public JAXBElement<DeleteProcessVersionsOutputMsgType> deleteProcessVersions(
            @RequestPayload final JAXBElement<DeleteProcessVersionsInputMsgType> req) {
        LOGGER.trace("Executing operation deleteProcessVersions");
        DeleteProcessVersionsInputMsgType payload = req.getValue();
        DeleteProcessVersionsOutputMsgType res = new DeleteProcessVersionsOutputMsgType();
        ResultType result = new ResultType();
        res.setResult(result);
        try {
            List<ProcessData> processVersions = new ArrayList<>();
            for (final ProcessVersionIdentifierType p : payload.getProcessVersionIdentifier()) {
                processVersions.add(new ProcessData(p.getProcessId(), new Version(p.getVersionNumber())));
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
        LOGGER.trace("Executing operation updateProcess");
        UpdateProcessInputMsgType payload = req.getValue();
        UpdateProcessOutputMsgType res = new UpdateProcessOutputMsgType();
        ResultType result = new ResultType();
        res.setResult(result);
        try {

            EditSessionType editType = payload.getEditSession();
            InputStream native_is = payload.getNative().getInputStream();
            NativeType natType = frmSrv.findNativeType(editType.getNativeType());
            Version orignialVersion = new Version(editType.getOriginalVersionNumber());
            Version currentVersion = new Version(editType.getCurrentVersionNumber());

            Set<RequestParameterType<?>> canoniserProperties = new HashSet<>();
            CanonisedProcess canonisedProcess = canoniserService.canonise(editType.getNativeType(), native_is, canoniserProperties);

            procSrv.updateProcess(editType.getProcessId(), editType.getProcessName(), editType.getOriginalBranchName(), editType.getNewBranchName(),
                    currentVersion, orignialVersion, secSrv.getUserByName(editType.getUsername()), Constants.LOCKED, natType, canonisedProcess);

            result.setCode(0);
            result.setMessage("");
        } catch (Exception ex) {
            LOGGER.error("", ex);
            result.setCode(-1);
            result.setMessage(ex.getMessage());
        }
        return WS_OBJECT_FACTORY.createUpdateProcessResponse(res);
    }


    @PayloadRoot(localPart = "GetFragmentRequest", namespace = NAMESPACE)
    @ResponsePayload
    public JAXBElement<GetFragmentOutputMsgType> getFragment(@RequestPayload final JAXBElement<GetFragmentInputMsgType> req) {
        LOGGER.trace("Executing operation getFragment");

        String defaultFormat = "EPML 2.0";

        ResultType result = new ResultType();
        GetFragmentInputMsgType payload = req.getValue();
        GetFragmentOutputMsgType res = new GetFragmentOutputMsgType();

        Integer fragmentId = payload.getFragmentId();

        try {
            CanonicalProcessType cpt = fragmentSrv.getFragmentToCanonicalProcessType(fragmentId);
            DecanonisedProcess dp = canoniserService.deCanonise(defaultFormat, cpt, null, new HashSet<RequestParameterType<?>>());

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
        LOGGER.trace("Executing operation exportFormat");

        ResultType result = new ResultType();
        ExportFormatInputMsgType payload = req.getValue();
        ExportFormatOutputMsgType res = new ExportFormatOutputMsgType();

        // Search for Native
        try {
            Integer processId = payload.getProcessId();
            String name = payload.getProcessName();
            String branch = payload.getBranchName();
            Version version = new Version(payload.getVersionNumber());
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
        LOGGER.trace("Executing operation importProcess");

        ResultType result = new ResultType();
        ImportProcessInputMsgType payload = req.getValue();
        ImportProcessOutputMsgType res = new ImportProcessOutputMsgType();

        try {
            EditSessionType editSession = payload.getEditSession();
            Integer folderId = editSession.getFolderId();
            String username = editSession.getUsername();
            String processName = editSession.getProcessName();
            Version version = new Version(editSession.getCurrentVersionNumber());
            String nativeType = editSession.getNativeType();
            String domain = editSession.getDomain();
            String creationDate = editSession.getCreationDate();
            String lastUpdate = editSession.getLastUpdate();
            boolean publicModel = editSession.isPublicModel();

            DataHandler handler = payload.getProcessDescription();
            PluginParameters xmlCanoniserProperties = payload.getCanoniserParameters();

            LOGGER.info("Importing process: " + processName);

            Set<RequestParameterType<?>> canoniserProperties = PluginHelper.convertToRequestParameters(xmlCanoniserProperties);
            CanonisedProcess canonisedProcess = canoniserService.canonise(nativeType, handler.getInputStream(), canoniserProperties);
            ProcessModelVersion pmv = procSrv.importProcess(username, folderId, processName, version, nativeType, canonisedProcess,
                    domain, "", creationDate, lastUpdate, publicModel);
            ProcessSummaryType process = uiHelper.createProcessSummary(pmv.getProcessBranch().getProcess(), pmv.getProcessBranch(), pmv,
                    nativeType, domain, creationDate, lastUpdate, username, publicModel);

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
        LOGGER.trace("Executing operation createGedMatrix");
        CreateGEDMatrixOutputMsgType res = new CreateGEDMatrixOutputMsgType();

        try {
            clusterService.computeGEDMatrix();
        } catch (RepositoryException e) {
            LOGGER.error("Failed to create GED Matrix - " + e.getMessage());
        }

        return WS_OBJECT_FACTORY.createCreateGEDMatrixResponse(res);
    }

    @PayloadRoot(localPart = "GetGedMatrixSummaryRequest", namespace = NAMESPACE)
    @ResponsePayload
    public JAXBElement<GetGedMatrixSummaryOutputMsgType> getGedMatrixSummary(@RequestPayload final JAXBElement<GetGedMatrixSummaryInputMsgType> req) {
        LOGGER.trace("Executing operation getGedMatrixSummary");
        GetGedMatrixSummaryOutputMsgType res = new GetGedMatrixSummaryOutputMsgType();
        GedMatrixSummaryType gedMatrixSummary = new GedMatrixSummaryType();
        ResultType result = new ResultType();

        try {
            HistoryEvent gedMatrix = clusterService.getGedMatrixLastExecutionTime();
            GregorianCalendar calendar = new GregorianCalendar();
            if (gedMatrix == null) {
                gedMatrixSummary.setBuilt(false);
                gedMatrixSummary.setBuildDate(null);
            } else if (gedMatrix.getStatus() == StatusEnum.FINISHED && gedMatrix.getType() == HistoryEnum.GED_MATRIX_COMPUTATION) {
                gedMatrixSummary.setBuilt(true);
                calendar.setTime(gedMatrix.getOccurDate());
                gedMatrixSummary.setBuildDate(DatatypeFactory.newInstance().newXMLGregorianCalendar(calendar));
            } else if (gedMatrix.getStatus() == StatusEnum.START && gedMatrix.getType() == HistoryEnum.GED_MATRIX_COMPUTATION) {
                gedMatrixSummary.setBuilt(false);
                calendar.setTime(gedMatrix.getOccurDate());
                gedMatrixSummary.setBuildDate(DatatypeFactory.newInstance().newXMLGregorianCalendar(calendar));
            }

            result.setCode(0);
            result.setMessage("");
        } catch (DatatypeConfigurationException e) {
            LOGGER.error("Returing the GED Matrix Summary failed!", e);
            result.setCode(-1);
            result.setMessage(e.getMessage());
        }

        gedMatrixSummary.setResult(result);
        res.setGedMatrixSummary(gedMatrixSummary);

        return WS_OBJECT_FACTORY.createGetGedMatrixSummaryResponse(res);
    }


    @PayloadRoot(localPart = "CreateClustersRequest", namespace = NAMESPACE)
    @ResponsePayload
    public JAXBElement<CreateClustersOutputMsgType> createClusters(@RequestPayload final JAXBElement<CreateClustersInputMsgType> req) {
        LOGGER.trace("Executing operation createClusters");
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
        LOGGER.trace("Executing operation getPairwiseDistances");
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
        LOGGER.trace("Executing operation getClusteringSummary");
        GetClusteringSummaryOutputMsgType res = new GetClusteringSummaryOutputMsgType();

        ClusteringSummary summary = clusterService.getClusteringSummary();
        ClusteringSummaryType summaryType = ClusterMapper.convertClusteringSummaryToClusteringSummaryType(summary);
        res.setClusteringSummary(summaryType);

        return WS_OBJECT_FACTORY.createGetClusteringSummaryResponse(res);
    }

    @PayloadRoot(localPart = "GetClusterSummariesRequest", namespace = NAMESPACE)
    @ResponsePayload
    public JAXBElement<GetClusterSummariesOutputMsgType> getClusterSummaries(@RequestPayload final JAXBElement<GetClusterSummariesInputMsgType> req) {
        LOGGER.trace("Executing operation getClusterSummaries");
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
        LOGGER.debug("Executing operation getCluster");
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
        LOGGER.trace("Executing operation getClusters");
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
        LOGGER.trace("Executing operation writeUser");
        WriteUserInputMsgType payload = req.getValue();
        WriteUserOutputMsgType res = new WriteUserOutputMsgType();
        ResultType result = new ResultType();
        res.setResult(result);
        try {
            UserType newUser = UserMapper.convertUserTypes(
                    secSrv.createUser(UserMapper.convertFromUserType(payload.getUser())));
            result.setCode(0);
            result.setMessage("");
            res.setUser(newUser);
        } catch (Exception ex) {
            LOGGER.warn("Failed to write user", ex);
            result.setCode(-1);
            result.setMessage(ex.getMessage());
        }
        return WS_OBJECT_FACTORY.createWriteUserResponse(res);
    }


    @PayloadRoot(localPart = "UpdateSearchHistoryRequest", namespace = NAMESPACE)
    @ResponsePayload
    public JAXBElement<UpdateSearchHistoryOutputMsgType> updateSearchHistories(@RequestPayload final JAXBElement<UpdateSearchHistoryInputMsgType> req) {
        LOGGER.trace("Executing operation updateSearchHistories");
        UpdateSearchHistoryInputMsgType payload = req.getValue();
        UpdateSearchHistoryOutputMsgType res = new UpdateSearchHistoryOutputMsgType();
        ResultType result = new ResultType();
        res.setResult(result);
        try {
            userSrv.updateUserSearchHistory(UserMapper.convertFromUserType(payload.getUser()),
                    SearchHistoryMapper.convertFromSearchHistoriesType(payload.getSearchHistory()));
            result.setCode(0);
            result.setMessage("");
        } catch (Exception ex) {
            ex.printStackTrace();
            result.setCode(1);
            result.setMessage(ex.getMessage());
        }
        return WS_OBJECT_FACTORY.createUpdateSearchHistoryResponse(res);
    }

    @PayloadRoot(localPart = "ReadNativeTypesRequest", namespace = NAMESPACE)
    @ResponsePayload
    public JAXBElement<ReadNativeTypesOutputMsgType> readNativeTypes(@RequestPayload final JAXBElement<ReadNativeTypesInputMsgType> req) {
        LOGGER.trace("Executing operation readFormats");
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
        LOGGER.trace("Executing operation readDomains");
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
     * @see org.apromore.manager.service.ManagerPortalPortType#ReadUserByUsername(ReadUserInputMsgType payload )
     */
    @PayloadRoot(localPart = "ReadUserByUsernameRequest", namespace = NAMESPACE)
    @ResponsePayload
    public JAXBElement<ReadUserByUsernameOutputMsgType> readUserByUsername(@RequestPayload final JAXBElement<ReadUserByUsernameInputMsgType> req) {
        LOGGER.trace("Executing operation readUser");
        ReadUserByUsernameInputMsgType payload = req.getValue();
        ReadUserByUsernameOutputMsgType res = new ReadUserByUsernameOutputMsgType();
        ResultType result = new ResultType();
        res.setResult(result);
        try {
            UserType user = UserMapper.convertUserTypes(secSrv.getUserByName(payload.getUsername()));
            result.setCode(0);
            result.setMessage("");
            res.setUser(user);
        } catch (Exception ex) {
            LOGGER.error("Get User by there username failed for " + payload.getUsername(), ex);
            result.setCode(-1);
            result.setMessage(ex.getMessage());
        }
        return WS_OBJECT_FACTORY.createReadUserByUsernameResponse(res);
    }

    /*
     * (non-Javadoc)
     * @see org.apromore.manager.service.ManagerPortalPortType#ReadUserByUsername(ReadUserInputMsgType payload )
     */
    @PayloadRoot(localPart = "ReadUserByEmailRequest", namespace = NAMESPACE)
    @ResponsePayload
    public JAXBElement<ReadUserByEmailOutputMsgType> readUserByEmail(@RequestPayload final JAXBElement<ReadUserByEmailInputMsgType> req) {
        LOGGER.trace("Executing operation login");
        ReadUserByEmailInputMsgType payload = req.getValue();
        ReadUserByEmailOutputMsgType res = new ReadUserByEmailOutputMsgType();
        ResultType result = new ResultType();
        res.setResult(result);
        try {
            UserType user = UserMapper.convertUserTypes(secSrv.getUserByEmail(payload.getEmail()));
            res.setUser(user);
            result.setCode(0);
            result.setMessage("");
        } catch (Exception ex) {
            LOGGER.error("Get User by there email address failed for " + payload.getEmail(), ex);
            result.setCode(-1);
            result.setMessage(ex.getMessage());
        }
        return new ObjectFactory().createReadUserByEmailResponse(res);
    }

    @PayloadRoot(localPart = "ResetUserPasswordRequest", namespace = NAMESPACE)
    @ResponsePayload
    public JAXBElement<ResetUserPasswordOutputMsgType> resetUserPassword(@RequestPayload final JAXBElement<ResetUserPasswordInputMsgType> req) {
        LOGGER.trace("Executing operation login");
        ResetUserPasswordInputMsgType payload = req.getValue();
        ResetUserPasswordOutputMsgType res = new ResetUserPasswordOutputMsgType();
        ResultType result = new ResultType();
        res.setResult(result);
        try {
            res.setSuccess(secSrv.resetUserPassword(payload.getUsername(), payload.getPassword()));
            result.setCode(0);
            result.setMessage("");
        } catch (Exception ex) {
            LOGGER.error("Resetting a users password failed for " + payload.getUsername(), ex);
            result.setCode(-1);
            result.setMessage(ex.getMessage());
        }
        return new ObjectFactory().createResetUserPasswordResponse(res);
    }


    @PayloadRoot(localPart = "SearchGroupsRequest", namespace = NAMESPACE)
    @ResponsePayload
    public JAXBElement<SearchGroupsOutputMsgType> searchGroups(@RequestPayload final JAXBElement<SearchGroupsInputMsgType> req) {
        LOGGER.trace("Executing operation searchGroups");
        SearchGroupsInputMsgType payload = req.getValue();
        SearchGroupsOutputMsgType res = new SearchGroupsOutputMsgType();
        ResultType result = new ResultType();
        res.setResult(result);
        try {
            List<Group> groups = secSrv.searchGroups(payload.getSearchString());
            for (Group group : groups) {
                res.getGroups().add(GroupMapper.toGroupType(group));
            }
            result.setCode(0);
            result.setMessage("");
        } catch (Exception ex) {
            ex.printStackTrace();
            result.setCode(-1);
            result.setMessage(ex.getMessage());
        }
        return new ObjectFactory().createSearchGroupsResponse(res);
    }


    /* (non-Javadoc)
    * @see org.apromore.manager.service.ManagerPortalPortType#readUser(ReadUserInputMsgType  payload )
    */
    @PayloadRoot(localPart = "SearchUserRequest", namespace = NAMESPACE)
    @ResponsePayload
    public JAXBElement<SearchUserOutputMsgType> searchUsers(@RequestPayload final JAXBElement<SearchUserInputMsgType> req) {
        LOGGER.trace("Executing operation searchUser");
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
        LOGGER.trace("Executing operation readProcessSummaries");
        ReadProcessSummariesInputMsgType payload = req.getValue();
        ReadProcessSummariesOutputMsgType res = new ReadProcessSummariesOutputMsgType();
        ResultType result = new ResultType();
        res.setResult(result);

        try {
            ProcessSummariesType processes = procSrv.readProcessSummaries(payload.getFolderId(), payload.getSearchExpression());
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

    /*
     * (non-Javadoc)
     *
     * @see org.apromore.manager.service.ManagerPortalPortType#runAPQLExpression(RunAPQLInputMsgType payload )*
     */
    @PayloadRoot(localPart = "RunAPQLRequest", namespace = NAMESPACE)
    @ResponsePayload
    public JAXBElement<RunAPQLOutputMsgType> runAPQLExpression(@RequestPayload final JAXBElement<RunAPQLInputMsgType> req) {
        LOGGER.trace("Executing operation runAPQLExpression");
        RunAPQLInputMsgType payload = req.getValue();
        RunAPQLOutputMsgType res = new RunAPQLOutputMsgType();
        ResultType result = new ResultType();
        res.setResult(result);

        try {
            result.setCode(-1);
            result.setMessage("Currently Not Implemented");
        } catch (Exception ex) {
            LOGGER.error("runAPQLExpression", ex);
            result.setCode(-1);
            result.setMessage(ex.getMessage());
        }
        return WS_OBJECT_FACTORY.createRunAPQLResponse(res);
    }


    @PayloadRoot(localPart = "ReadInstalledPluginsRequest", namespace = NAMESPACE)
    @ResponsePayload
    public JAXBElement<ReadInstalledPluginsOutputMsgType> readInstalledPlugins(@RequestPayload final JAXBElement<ReadInstalledPluginsInputMsgType> req) {
        LOGGER.trace("Executing operation 'ReadInstalledPlugins'");
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
        LOGGER.trace("Executing operation 'ReadPluginInfo'");
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
        LOGGER.trace("Executing operation 'ReadCanoniserInfo'");
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
        LOGGER.trace("Executing operation 'ReadNativeMetaData'");
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
            if (metaData != null) {
                res.setNativeMetaData(CanoniserHelper.convertFromCanoniserMetaData(metaData));
            } else {
                throw new Exception("Couldn't read meta data!");
            }

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
        LOGGER.trace("Executing operation 'ReadInitialNativeFormat'");
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
        LOGGER.trace("Executing operation 'ReadDeploymentPluginInfo'");
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
        LOGGER.trace("Executing operation 'DeployProcess'");
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

    @PayloadRoot(localPart = "GetWorkspaceFolderTreeRequest", namespace = NAMESPACE)
    @ResponsePayload
    public JAXBElement<GetWorkspaceFolderTreeOutputMsgType> getWorkspaceFolderTree(@RequestPayload final JAXBElement<GetWorkspaceFolderTreeInputMsgType> req) {
        LOGGER.trace("Executing operation getWorkspaceFolderTree");
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
        LOGGER.trace("Executing operation getSubFolders");
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
        LOGGER.trace("Executing operation getBreadcrumbs");
        GetBreadcrumbsInputMsgType payload = req.getValue();
        GetBreadcrumbsOutputMsgType res = new GetBreadcrumbsOutputMsgType();
        ResultType result = new ResultType();
        res.setResult(result);

        List<FolderType> folderTypes = WorkspaceMapper.convertFolderListToFolderTypes(workspaceSrv.getBreadcrumbs(payload.getFolderId()));
        for (FolderType ft : folderTypes) {
            res.getFolders().add(ft);
        }
        return new ObjectFactory().createGetBreadcrumbsResponse(res);
    }

    @PayloadRoot(localPart = "GetFolderGroupsRequest", namespace = NAMESPACE)
    @ResponsePayload
    public JAXBElement<GetFolderGroupsOutputMsgType> getFolderUsers(@RequestPayload final JAXBElement<GetFolderGroupsInputMsgType> req) {
        LOGGER.trace("Executing operation getFolderGroups");
        GetFolderGroupsInputMsgType payload = req.getValue();
        GetFolderGroupsOutputMsgType res = new GetFolderGroupsOutputMsgType();
        ResultType result = new ResultType();
        res.setResult(result);

        List<GroupAccessType> groupAccessTypes = WorkspaceMapper.convertGroupFoldersToGroupAccessTypes(workspaceSrv.getGroupFolders(payload.getFolderId()));
        for (GroupAccessType ft : groupAccessTypes) {
            res.getGroups().add(ft);
        }
        return new ObjectFactory().createGetFolderGroupsResponse(res);
    }

    @PayloadRoot(localPart = "SaveFolderPermissionsRequest", namespace = NAMESPACE)
    @ResponsePayload
    public JAXBElement<SaveFolderPermissionsOutputMsgType> saveFolderPermissions(@RequestPayload final JAXBElement<SaveFolderPermissionsInputMsgType> req) {
        LOGGER.trace("Executing operation getFolderUsers");
        SaveFolderPermissionsInputMsgType payload = req.getValue();
        SaveFolderPermissionsOutputMsgType res = new SaveFolderPermissionsOutputMsgType();
        ResultType result = new ResultType();
        res.setResult(result);

        String message = workspaceSrv.saveFolderPermissions(payload.getFolderId(), payload.getUserId(), payload.isHasRead(), payload.isHasWrite(), payload.isHasOwnership());
        res.setMessage(message);
        return new ObjectFactory().createSaveFolderPermissionsResponse(res);
    }

    @PayloadRoot(localPart = "SaveProcessPermissionsRequest", namespace = NAMESPACE)
    @ResponsePayload
    public JAXBElement<SaveProcessPermissionsOutputMsgType> saveProcessPermissions(@RequestPayload final JAXBElement<SaveProcessPermissionsInputMsgType> req) {
        LOGGER.trace("Executing operation getFolderUsers");
        SaveProcessPermissionsInputMsgType payload = req.getValue();
        SaveProcessPermissionsOutputMsgType res = new SaveProcessPermissionsOutputMsgType();
        ResultType result = new ResultType();
        res.setResult(result);

        String message = workspaceSrv.saveProcessPermissions(payload.getProcessId(), payload.getUserId(), payload.isHasRead(), payload.isHasWrite(), payload.isHasOwnership());
        res.setMessage(message);
        return new ObjectFactory().createSaveProcessPermissionsResponse(res);
    }

    @PayloadRoot(localPart = "RemoveFolderPermissionsRequest", namespace = NAMESPACE)
    @ResponsePayload
    public JAXBElement<RemoveFolderPermissionsOutputMsgType> removeFolderPermissions(@RequestPayload final JAXBElement<RemoveFolderPermissionsInputMsgType> req) {
        LOGGER.trace("Executing operation removeFolderPermissions");
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
        LOGGER.trace("Executing operation removeProcessPermissions");
        RemoveProcessPermissionsInputMsgType payload = req.getValue();
        RemoveProcessPermissionsOutputMsgType res = new RemoveProcessPermissionsOutputMsgType();
        ResultType result = new ResultType();
        res.setResult(result);

        String message = workspaceSrv.removeProcessPermissions(payload.getProcessId(), payload.getUserId());
        res.setMessage(message);
        return new ObjectFactory().createRemoveProcessPermissionsResponse(res);
    }

    @PayloadRoot(localPart = "GetProcessGroupsRequest", namespace = NAMESPACE)
    @ResponsePayload
    public JAXBElement<GetProcessGroupsOutputMsgType> getProcessGroups(@RequestPayload final JAXBElement<GetProcessGroupsInputMsgType> req) {
        LOGGER.trace("Executing operation getProcessGroups");
        GetProcessGroupsInputMsgType payload = req.getValue();
        GetProcessGroupsOutputMsgType res = new GetProcessGroupsOutputMsgType();
        ResultType result = new ResultType();
        res.setResult(result);

        List<GroupAccessType> groupAccessTypes = WorkspaceMapper.convertGroupProcessesToGroupAccessTypes(workspaceSrv.getGroupProcesses(payload.getProcessId()));
        for (GroupAccessType ft : groupAccessTypes) {
            res.getGroups().add(ft);
        }
        return new ObjectFactory().createGetProcessGroupsResponse(res);
    }

    @PayloadRoot(localPart = "GetProcessesRequest", namespace = NAMESPACE)
    @ResponsePayload
    public JAXBElement<GetProcessesOutputMsgType> getProcesses(@RequestPayload final JAXBElement<GetProcessesInputMsgType> req) {
        LOGGER.trace("Executing operation getProcesses");
        GetProcessesInputMsgType payload = req.getValue();
        GetProcessesOutputMsgType res = new GetProcessesOutputMsgType();
        ResultType result = new ResultType();
        res.setResult(result);

        res.setProcesses(uiHelper.buildProcessSummaryList(payload.getUserId(), payload.getFolderId(), null));

        return new ObjectFactory().createGetProcessesResponse(res);
    }

    @PayloadRoot(localPart = "CreateFolderRequest", namespace = NAMESPACE)
    @ResponsePayload
    public JAXBElement<CreateFolderOutputMsgType> createFolder(@RequestPayload final JAXBElement<CreateFolderInputMsgType> req) {
        LOGGER.trace("Executing operation createFolder");
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
        LOGGER.trace("Executing operation addProcessToFolder");
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
        LOGGER.trace("Executing operation updateFolder");
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
        LOGGER.trace("Executing operation deleteFolder");
        DeleteFolderInputMsgType payload = req.getValue();
        DeleteFolderOutputMsgType res = new DeleteFolderOutputMsgType();
        ResultType result = new ResultType();
        res.setResult(result);

        workspaceSrv.deleteFolder(payload.getFolderId());

        return new ObjectFactory().createDeleteFolderResponse(res);
    }

}
