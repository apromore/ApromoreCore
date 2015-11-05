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

import ee.ut.eventstr.model.ProDriftDetectionResult;
import au.edu.qut.util.ImportEventLog;
import org.apromore.canoniser.Canoniser;
import org.apromore.canoniser.exception.CanoniserException;
import org.apromore.canoniser.result.CanoniserMetadataResult;
import org.apromore.common.Constants;
import org.apromore.cpf.CanonicalProcessType;
import org.apromore.dao.model.Cluster;
import org.apromore.dao.model.*;
import org.apromore.exception.ExportFormatException;
import org.apromore.exception.RepositoryException;
import org.apromore.helper.CanoniserHelper;
import org.apromore.helper.PluginHelper;
import org.apromore.helper.Version;
import org.apromore.mapper.*;
import org.apromore.model.*;
import org.apromore.plugin.ParameterAwarePlugin;
import org.apromore.plugin.Plugin;
import org.apromore.plugin.PluginRequestImpl;
import org.apromore.plugin.deployment.DeploymentPlugin;
import org.apromore.plugin.message.PluginMessage;
import org.apromore.plugin.property.RequestParameterType;
import org.apromore.service.*;
import org.apromore.service.helper.UserInterfaceHelper;
import org.apromore.service.model.*;
import org.apromore.toolbox.clustering.algorithm.dbscan.FragmentPair;
import org.deckfour.xes.factory.XFactoryNaiveImpl;
import org.deckfour.xes.model.XLog;
import org.deckfour.xes.out.XesXmlSerializer;
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
import java.io.*;
import java.util.*;
import java.util.Map;
import java.util.Set;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

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
    private ProDriftDetectionService proDriftSrv;
    private UserInterfaceHelper uiHelper;
    private PQLService pqlService;
    private DatabaseService dbService;
    private BPMNMinerService bpmnMinerService;
    private StructuringService structuringService;


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
     * @param proDriftSrv Process Drift Detection Service.
     * @param uiHelper UI Helper.
     */
    @Inject
    public ManagerPortalEndpoint(final DeploymentService deploymentService, final PluginService pluginService,
            final FragmentService fragmentSrv, final CanoniserService canoniserService, final ProcessService procSrv,
            final ClusterService clusterService, final FormatService frmSrv, final DomainService domSrv,
            final UserService userSrv, final SimilarityService simSrv, final MergeService merSrv,
            final SecurityService secSrv, final WorkspaceService wrkSrv, final UserInterfaceHelper uiHelper,
            final PQLService pqlService,  final DatabaseService dbService, final BPMNMinerService bpmnMinerService,
			final ProDriftDetectionService proDriftSrv, final StructuringService structuringService
    ) {
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
        this.proDriftSrv = proDriftSrv;
        this.uiHelper = uiHelper;
        this.pqlService = pqlService;
        this.dbService = dbService;
        this.bpmnMinerService = bpmnMinerService;
        this.structuringService = structuringService;

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
            LOGGER.error("Get User by username failed for " + payload.getUsername(), ex);
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

        RunAPQLInputMsgType input=req.getValue();
        RunAPQLOutputMsgType res = new RunAPQLOutputMsgType();

        ResultType resultType = new ResultType();

        try {
//            pqlService.indexAllModels();
            LOGGER.error("PRIMA RUNAPQL: "+input.getAPQLExpression()+" "+input.getIds()+" "+input.getUserID());
//            pqlService.indexAllModels();
            List<String> results=pqlService.runAPQLQuery(input.getAPQLExpression(), input.getIds(), input.getUserID());
//            List<Detail> details=pqlService.getDetails();

            if(!results.isEmpty() && !results.get(0).matches("([0-9]+[/]([0-9]+([.][0-9]+){1,2})[/][a-zA-Z0-9]+[;]?)+")) {
                LOGGER.error("Results Contains Errors: ");
                resultType.setMessage("ERRORS");
                resultType.setCode(0);

            }else {
                resultType.setMessage("RESULTS");
                resultType.setCode(1);
            }
            res.getProcessResult().addAll(results);
            res.setResult(resultType);
        } catch (Exception ex) {
            LOGGER.error("runAPQLExpression", ex);
            resultType.setCode(-1);
            resultType.setMessage(ex.getMessage()+" runAPQLExpression");
        }

        return WS_OBJECT_FACTORY.createRunAPQLResponse(res);
    }

    @PayloadRoot(localPart = "DBRequest", namespace = NAMESPACE)
    @ResponsePayload
    public JAXBElement<DBOutputMsgType> getProcessesLabels(@RequestPayload final JAXBElement<DBInputMsgType> req) {
        LOGGER.trace("Executing operation getProcessesLabels");

        DBInputMsgType input=req.getValue();
        DBOutputMsgType res = new DBOutputMsgType();

        try {
            List<String> labels=dbService.getLabels(input.getTable(),input.getColumnName());
            res.getLabels().clear();
            res.getLabels().addAll(labels);
        } catch (Exception ex) {
            LOGGER.error("getProcessesLabels", ex);
        }

        return WS_OBJECT_FACTORY.createDBResponse(res);
    }

    @PayloadRoot(localPart = "DetailRequest", namespace = NAMESPACE)
    @ResponsePayload
    public JAXBElement<DetailOutputMsgType> getDetails(@RequestPayload final JAXBElement<DetailInputMsgType> req) {
        LOGGER.trace("Executing operation runAPQLExpression");

        DetailInputMsgType input=req.getValue();
        DetailOutputMsgType res = new DetailOutputMsgType();

        ResultType resultType = new ResultType();

        try {
//            pqlService.indexAllModels();
//            LOGGER.error("PRIMA RUNAPQL: "+input.getAPQLExpression()+" "+input.getIds()+" "+input.getUserID());
//            pqlService.indexAllModels();
//            List<String> results=pqlService.runAPQLQuery(input.getAPQLExpression(),input.getIds(),input.getUserID());
            List<Detail> details=pqlService.getDetails();

            res.getDetail().addAll(details);

        } catch (Exception ex) {
            LOGGER.error("runAPQLExpression", ex);
            resultType.setCode(-1);
            resultType.setMessage(ex.getMessage() + " runAPQLExpression");
        }

        return WS_OBJECT_FACTORY.createDetailResponse(res);
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

        res.setProcesses(uiHelper.buildProcessSummaryList(payload.getUserId(), payload.getFolderId(), payload.getPageIndex(), payload.getPageSize()));

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

    @PayloadRoot(localPart = "DiscoverBPMNModelRequest", namespace = NAMESPACE)
    @ResponsePayload
    public JAXBElement<DiscoverBPMNModelOutputMsgType> discoverBPMNModel(@RequestPayload final JAXBElement<DiscoverBPMNModelInputMsgType> req) {
        LOGGER.trace("Executing operation discoverBPMNModel");
        DiscoverBPMNModelInputMsgType payload = req.getValue();

        DiscoverBPMNModelOutputMsgType res = new DiscoverBPMNModelOutputMsgType();
        ResultType result = new ResultType();
        res.setResult(result);
        try {

            ByteArrayInputStream b = new ByteArrayInputStream(payload.getXESGZLog());

            XLog log = ImportEventLog.importFromStream(new XFactoryNaiveImpl(), b);

            int miningAlgorithm = payload.getMiningAlgorithm();
            int dependencyAlgorithm = payload.getDependencyAlgorithm();
            boolean sortLog = payload.isSortLog();
            boolean structProcess = payload.isStructProcess();
            double interruptingEventTolerance = payload.getInterruptingEventTolerance();
            double multiInstancePercentage = payload.getMultiInstancePercentage();
            double multiInstanceTolerance = payload.getMultiInstanceTolerance();
            double timerEventPercentage = payload.getTimerEventPercentage();
            double timerEventTolerance = payload.getTimerEventTolerance();
            double noiseThreshold = payload.getNoiseThreshold();
            List<String> listCandidates = payload.getCanditateEntities().getElements();
            Map<Set<String>, Set<String>> primaryKeySelections = new HashMap<>();
            for(BPMNMinerEntry entry : payload.getPrimaryKeySelections().getEntries()) {
                primaryKeySelections.put(new HashSet<String>(entry.getKey().getElements()), new HashSet<String>(entry.getValue().getElements()));
            }

            String model = bpmnMinerService.discoverBPMNModel(log, sortLog, structProcess, miningAlgorithm, dependencyAlgorithm, interruptingEventTolerance,
                    timerEventPercentage, timerEventTolerance, multiInstancePercentage, multiInstanceTolerance, noiseThreshold,
                    listCandidates, primaryKeySelections);


            result.setCode(0);
            result.setMessage(model);
        } catch (Exception ex) {
            ex.printStackTrace();
            result.setCode(1);
            result.setMessage(ex.getMessage());
        }
        return WS_OBJECT_FACTORY.createDiscoverBPMNModelResponse(res);
    }

    @PayloadRoot(localPart = "StructureBPMNModelRequest", namespace = NAMESPACE)
    @ResponsePayload
    public JAXBElement<StructureBPMNModelOutputMsgType> structureBPMNModel(@RequestPayload final JAXBElement<StructureBPMNModelInputMsgType> req) {
        LOGGER.trace("Executing operation structureBPMNModel");

        StructureBPMNModelInputMsgType payload = req.getValue();
        StructureBPMNModelOutputMsgType res = new StructureBPMNModelOutputMsgType();
        ResultType result = new ResultType();
        res.setResult(result);

        String process = payload.getProcess();

        try {
            result.setMessage( structuringService.structureBPMNModel(process) );
            result.setCode(0);
        } catch (Exception e) {
            e.printStackTrace();
            result.setMessage(e.getMessage());
            result.setCode(1);
        }
        return WS_OBJECT_FACTORY.createStructureBPMNModelResponse(res);
    }

	@PayloadRoot(localPart = "ProDriftDetectorRequest", namespace = NAMESPACE)
    @ResponsePayload
    public JAXBElement<ProDriftDetectorOutputMsgType> proDriftDetector(@RequestPayload final JAXBElement<ProDriftDetectorInputMsgType> req) {
        LOGGER.trace("Executing operation proDriftDetector");

        ProDriftDetectorInputMsgType reqValue = req.getValue();
        ProDriftDetectorOutputMsgType res = new ProDriftDetectorOutputMsgType();


        try {

            byte[] logByteArray = reqValue.getLogByteArray();
            int winSize = reqValue.getWinSize();
            String fWinorAwin = reqValue.getFWinorAwin();
            String logFileName = reqValue.getLogFileName();

            ProDriftDetectionResult pddres = proDriftSrv.proDriftDetector(logByteArray, winSize, fWinorAwin, logFileName);

            res.setPValuesDiagram(pddres.getpValuesDiagram());

            res.getDriftPoints().addAll(pddres.getDriftPoints());
            res.getLastReadTrace().addAll(pddres.getLastReadTrace());
            res.getStartOfTransitionPoints().addAll(pddres.getStartOfTransitionPoints());
            res.getEndOfTransitionPoints().addAll(pddres.getEndOfTransitionPoints());



        } catch (Exception ex) {
            LOGGER.error("", ex);
        }


        return WS_OBJECT_FACTORY.createProDriftDetectorResponse(res);
    }

}
