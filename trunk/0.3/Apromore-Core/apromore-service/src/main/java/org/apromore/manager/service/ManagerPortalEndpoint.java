package org.apromore.manager.service;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;

import de.epml.TypeEPML;
import org.apromore.common.Constants;
import org.apromore.dao.model.Cluster;
import org.apromore.dao.model.ClusteringSummary;
import org.apromore.exception.ExceptionCanoniseVersion;
import org.apromore.exception.ExceptionVersion;
import org.apromore.exception.ExportFormatException;
import org.apromore.exception.RepositoryException;
import org.apromore.manager.canoniser.ManagerCanoniserClient;
import org.apromore.manager.da.ManagerDataAccessClient;
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
import org.apromore.model.DomainsType;
import org.apromore.model.EditProcessDataInputMsgType;
import org.apromore.model.EditProcessDataOutputMsgType;
import org.apromore.model.EditSessionType;
import org.apromore.model.ExportFormatInputMsgType;
import org.apromore.model.ExportFormatOutputMsgType;
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
import org.apromore.model.MergeProcessesInputMsgType;
import org.apromore.model.MergeProcessesOutputMsgType;
import org.apromore.model.NativeTypesType;
import org.apromore.model.ObjectFactory;
import org.apromore.model.PairDistancesType;
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
import org.apromore.service.ClusterService;
import org.apromore.service.DomainService;
import org.apromore.service.FormatService;
import org.apromore.service.FragmentService;
import org.apromore.service.MergeService;
import org.apromore.service.ProcessService;
import org.apromore.service.RepositoryService;
import org.apromore.service.SessionService;
import org.apromore.service.SimilarityService;
import org.apromore.service.UserService;
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
import org.wfmc._2008.xpdl2.Author;
import org.wfmc._2008.xpdl2.Created;
import org.wfmc._2008.xpdl2.Documentation;
import org.wfmc._2008.xpdl2.ModificationDate;
import org.wfmc._2008.xpdl2.PackageHeader;
import org.wfmc._2008.xpdl2.PackageType;
import org.wfmc._2008.xpdl2.RedefinableHeader;
import org.wfmc._2008.xpdl2.Version;

/**
 * The WebService Endpoint Used by the Portal.
 * <p/>
 * This is the only web service available in this application.
 */
@Endpoint
public class ManagerPortalEndpoint {

    private static final Logger LOGGER = LoggerFactory.getLogger(ManagerPortalEndpoint.class.getName());

    private static final String NAMESPACE = "urn:qut-edu-au:schema:apromore:manager";


    @Autowired @Qualifier("FragmentService")
    private FragmentService fragmentSrv;
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

    @Autowired
    private ManagerDataAccessClient daClient;
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
            ex.printStackTrace();
            result.setCode(-1);
            result.setMessage(ex.getMessage());
        }
        return new ObjectFactory().createSearchForSimilarProcessesResponse(res);
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
        return new ObjectFactory().createReadAllUsersResponse(res);
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
            @RequestPayload final JAXBElement<DeleteProcessVersionsInputMsgType> req) {
        LOGGER.info("Executing operation deleteProcessVersions");
        DeleteProcessVersionsInputMsgType payload = req.getValue();
        DeleteProcessVersionsOutputMsgType res = new DeleteProcessVersionsOutputMsgType();
        ResultType result = new ResultType();
        res.setResult(result);
        try {
            List<NameValuePair> processVersions = new ArrayList<>(0);
            for (final ProcessVersionIdentifierType p : payload.getProcessVersionIdentifier()) {
                processVersions.add(new NameValuePair(p.getProcessName(), p.getBranchName()));
            }
            repSrv.deleteProcessModel(processVersions);

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
            caClient.CanoniseVersion(editSessionCode, editSessionC, newCpfURI(), native_is);
            result.setCode(0);
            result.setMessage("");
        } catch (ExceptionVersion ex) {
            result.setCode(-3);
            result.setMessage(ex.getMessage());
        } catch (IOException | ExceptionCanoniseVersion ex) {
            result.setCode(-1);
            result.setMessage(ex.getMessage());
        }
        return new ObjectFactory().createUpdateProcessResponse(res);
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
            ex.printStackTrace();
            result.setCode(-1);
            result.setMessage(ex.getMessage());
        }
        return new ObjectFactory().createWriteEditSessionResponse(res);
    }

    @PayloadRoot(localPart = "GetFragmentRequest", namespace = NAMESPACE)
    @ResponsePayload
    public JAXBElement<FragmentResponseType> getFragment(@RequestPayload final JAXBElement<GetFragmentRequestType> req) {
        LOGGER.info("Executing operation getFragment");

        String fragmentId = req.getValue().getFragmentId();
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
        return new ObjectFactory().createFragmentResponse(res);
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
            DataSource source;
            Integer processId = payload.getProcessId();
            String name = payload.getProcessName();
            String version = payload.getVersionName();
            String format = payload.getFormat();
            String annName = payload.getAnnotationName();
            boolean withAnn = payload.isWithAnnotations();

            source = procSrv.exportFormat(name, processId, version, format, annName, withAnn);

            res.setNative(new DataHandler(source));
            result.setCode(0);
            result.setMessage("");
        } catch (ExportFormatException efe) {
            LOGGER.error("ExportFormat failed: " + efe.toString());
            result.setCode(-1);
            result.setMessage(efe.getMessage());
        }

        res.setResult(result);
        return new ObjectFactory().createExportFormatResponse(res);
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

            //Boolean addFakeEvents = payload.isAddFakeEvents();
            DataHandler handler = payload.getProcessDescription();

            //ProcessSummaryType process = caClient.CanoniseProcess(username, processName, newCpfURI(),
            //        versionName, nativeType, is, domain, "", creationDate, lastUpdate, addFakeEvents);
            ProcessSummaryType process = procSrv.importProcess(username, processName, newCpfURI(), versionName,
                    nativeType, handler, domain, "", creationDate, lastUpdate);

            res.setProcessSummary(process);
            result.setCode(0);
            result.setMessage("");
        } catch (Exception ex) {
            ex.printStackTrace();
            result.setCode(-1);
            result.setMessage(ex.getMessage());
        }

        res.setResult(result);
        return new ObjectFactory().createImportProcessResponse(res);
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

        return new ObjectFactory().createCreateClustersResponse(res);
    }

    @PayloadRoot(localPart = "PairwiseDistancesRequest", namespace = NAMESPACE)
    @ResponsePayload
    public JAXBElement<GetPairwiseDistancesOutputMsgType> getPairwiseDistances(@RequestPayload final JAXBElement<GetPairwiseDistancesInputMsgType> req) {
        LOGGER.info("Executing operation getPairwiseDistances");
        GetPairwiseDistancesInputMsgType payload = req.getValue();
        GetPairwiseDistancesOutputMsgType res = new GetPairwiseDistancesOutputMsgType();

        List<String> fragmentIds = payload.getFragmentIds().getFragmentId();
        try {
            Map<FragmentPair, Double> pairDistances = clusterService.getPairDistances(fragmentIds);
            PairDistancesType pairDistancesType = ClusterMapper.convertPairDistancesToPairDistancesType(pairDistances);
            res.setPairDistances(pairDistancesType);
        } catch (RepositoryException e) {
            LOGGER.error("Failed to get pairwise distances. " + e.getMessage());
        }

        return new ObjectFactory().createPairwiseDistancesResponse(res);
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

        return new ObjectFactory().createGetClusteringSummaryResponse(res);
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
        return new ObjectFactory().createGetClusterSummariesResponse(res);
    }

    @PayloadRoot(localPart = "GetClusterRequest", namespace = NAMESPACE)
    @ResponsePayload
    public JAXBElement<GetClusterOutputMsgType> getCluster(@RequestPayload final JAXBElement<GetClusterInputMsgType> req) {
        LOGGER.info("Executing operation getCluster");
        GetClusterInputMsgType payload = req.getValue();
        GetClusterOutputMsgType res = new GetClusterOutputMsgType();

        String clusterId = payload.getClusterId();
        org.apromore.service.model.Cluster cluster = clusterService.getCluster(clusterId);
        ClusterType ctype = ClusterMapper.convertClusterToClusterType(cluster);
        res.setCluster(ctype);
        return new ObjectFactory().createGetClusterResponse(res);
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
        return new ObjectFactory().createGetClustersResponse(res);
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
        return new ObjectFactory().createWriteUserResponse(res);
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
        return new ObjectFactory().createReadNativeTypesResponse(res);
    }

    /* (non-Javadoc)
      * @see org.apromore.manager.service.ManagerPortalPortType#readDomains(ReadDomainsInputMsgType  payload )*
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
            ex.printStackTrace();
            result.setCode(-1);
            result.setMessage(ex.getMessage());
        }
        return new ObjectFactory().createReadDomainsResponse(res);
    }

    /* (non-Javadoc)
     * @see org.apromore.manager.service.ManagerPortalPortType#readUser(ReadUserInputMsgType  payload )
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
            UserType user = UserMapper.convertUserTypes(userSrv.findUser(payload.getUsername()));
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
            @RequestPayload final JAXBElement<ReadProcessSummariesInputMsgType> req) {
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
            ex.printStackTrace();
            result.setCode(-1);
            result.setMessage(ex.getMessage());
        }
        return new ObjectFactory().createReadProcessSummariesResponse(res);
    }

    /**
     * Generate a new npf which is the result of writing parameters in process_xml.
     *
     * @param process_xml the given npf to be synchronised
     * @param nativeType  npf native type
     * @param processName
     * @param version
     * @param username
     * @param lastUpdate
     * @return
     * @throws javax.xml.bind.JAXBException
     */
    private InputStream copyParam2NPF(InputStream process_xml, String nativeType, String processName,
            String version, String username, String lastUpdate, String documentation) throws JAXBException {
        InputStream res = null;
        if (nativeType.compareTo(Constants.XPDL_2_1) == 0) {
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

        } else if (nativeType.compareTo(Constants.EPML_2_0) == 0) {
            JAXBContext jc = JAXBContext.newInstance("de.epml");
            Unmarshaller u = jc.createUnmarshaller();
            JAXBElement<TypeEPML> rootElement = (JAXBElement<TypeEPML>) u.unmarshal(process_xml);
            TypeEPML epml = rootElement.getValue();

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
     *
     * @return the new cpf uri
     */
    private static String newCpfURI() {
        DateFormat dateFormat = new SimpleDateFormat("yyyyMMddHHmmsSSS");
        Date date = new Date();
        return dateFormat.format(date);
    }




    public void setDaClient(ManagerDataAccessClient daClient) {
        this.daClient = daClient;
    }

    public void setCaClient(ManagerCanoniserClient caClient) {
        this.caClient = caClient;
    }


    public void setUserSrv(UserService userService) {
        this.userSrv = userService;
    }

    public void setProcSrv(ProcessService procService) {
        this.procSrv = procService;
    }

    public void setFrmSrv(FormatService formatService) {
        this.frmSrv = formatService;
    }

    public void setDomSrv(DomainService domainService) {
        this.domSrv = domainService;
    }

    public void setSimSrv(SimilarityService similarityService) {
        this.simSrv = similarityService;
    }

    public void setMerSrv(MergeService mergeService) {
        this.merSrv = mergeService;
    }

    public void setSesSrv(SessionService sessionService) {
        this.sesSrv = sessionService;
    }
}
