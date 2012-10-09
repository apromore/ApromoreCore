package org.apromore.manager.client;

import java.io.IOException;
import java.io.InputStream;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.activation.DataHandler;
import javax.mail.util.ByteArrayDataSource;
import javax.xml.bind.JAXBElement;

import org.apromore.manager.client.helper.DeleteProcessVersionHelper;
import org.apromore.manager.client.helper.MergeProcessesHelper;
import org.apromore.manager.client.helper.PluginHelper;
import org.apromore.manager.client.helper.SearchForSimilarProcessesHelper;
import org.apromore.manager.client.util.StreamUtil;
import org.apromore.model.ClusterFilterType;
import org.apromore.model.ClusterSettingsType;
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
import org.apromore.model.FragmentIdsType;
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
import org.apromore.model.PairDistanceType;
import org.apromore.model.PluginInfo;
import org.apromore.model.PluginInfoResult;
import org.apromore.model.PluginMessages;
import org.apromore.model.ProcessSummariesType;
import org.apromore.model.ProcessSummaryType;
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
import org.apromore.model.SearchForSimilarProcessesInputMsgType;
import org.apromore.model.SearchForSimilarProcessesOutputMsgType;
import org.apromore.model.UpdateProcessInputMsgType;
import org.apromore.model.UpdateProcessOutputMsgType;
import org.apromore.model.UserType;
import org.apromore.model.UsernamesType;
import org.apromore.model.VersionSummaryType;
import org.apromore.model.WriteAnnotationInputMsgType;
import org.apromore.model.WriteAnnotationOutputMsgType;
import org.apromore.model.WriteEditSessionInputMsgType;
import org.apromore.model.WriteEditSessionOutputMsgType;
import org.apromore.model.WriteUserInputMsgType;
import org.apromore.model.WriteUserOutputMsgType;
import org.apromore.plugin.property.RequestPropertyType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ws.client.core.WebServiceTemplate;

/**
 * Performance Test for the Apromore Manager Client.
 *
 * @author <a href="mailto:cam.james@gmail.com">Cameron James</a>
 */
public class ManagerServiceClient implements ManagerService {

    private static final Logger LOGGER = LoggerFactory.getLogger(ManagerServiceClient.class);

    private static final ObjectFactory WS_CLIENT_FACTORY = new ObjectFactory();

    private final WebServiceTemplate webServiceTemplate;

    /**
     * Default Constructor.
     * @param newWebServiceTemplate the webservice template
     */
    public ManagerServiceClient(final WebServiceTemplate newWebServiceTemplate) {
        this.webServiceTemplate = newWebServiceTemplate;
    }


    /**
     * @see ManagerService#readUser(String)
     * {@inheritDoc}
     */
    @Override
    @SuppressWarnings("unchecked")
    public UserType readUser(final String username) {
        LOGGER.debug("Preparing ReadUserRequest.....");

        ReadUserInputMsgType msg = new ReadUserInputMsgType();
        msg.setUsername(username);

        JAXBElement<ReadUserInputMsgType> request = WS_CLIENT_FACTORY.createReadUserRequest(msg);
        JAXBElement<ReadUserOutputMsgType> response = (JAXBElement<ReadUserOutputMsgType>) webServiceTemplate.marshalSendAndReceive(request);
        return response.getValue().getUser();
    }

    /**
     * @see ManagerService#readAllUsers()
     * {@inheritDoc}
     */
    @Override
    @SuppressWarnings("unchecked")
    public UsernamesType readAllUsers() {
        LOGGER.debug("Preparing ReadAllUsersRequest.....");

        ReadAllUsersInputMsgType msg = new ReadAllUsersInputMsgType();
        msg.setEmpty("");

        JAXBElement<ReadAllUsersInputMsgType> request = WS_CLIENT_FACTORY.createReadAllUsersRequest(msg);
        JAXBElement<ReadAllUsersOutputMsgType> response = (JAXBElement<ReadAllUsersOutputMsgType>) webServiceTemplate.marshalSendAndReceive(request);
        return response.getValue().getUsernames();
    }


    /**
     * @see ManagerService#readDomains()
     * {@inheritDoc}
     */
    @Override
    @SuppressWarnings("unchecked")
    public DomainsType readDomains() {
        LOGGER.debug("Preparing ReadDomainsRequest.....");

        ReadDomainsInputMsgType msg = new ReadDomainsInputMsgType();
        msg.setEmpty("");

        JAXBElement<ReadDomainsInputMsgType> request = WS_CLIENT_FACTORY.createReadDomainsRequest(msg);
        JAXBElement<ReadDomainsOutputMsgType> response = (JAXBElement<ReadDomainsOutputMsgType>) webServiceTemplate.marshalSendAndReceive(request);
        return response.getValue().getDomains();
    }

    /**
     * @see ManagerService#readNativeTypes()
     * {@inheritDoc}
     */
    @Override
    @SuppressWarnings("unchecked")
    public NativeTypesType readNativeTypes() {
        LOGGER.debug("Preparing ReadNativeTypesRequest.....");

        ReadNativeTypesInputMsgType msg = new ReadNativeTypesInputMsgType();
        msg.setEmpty("");

        JAXBElement<ReadNativeTypesInputMsgType> request = WS_CLIENT_FACTORY.createReadNativeTypesRequest(msg);
        JAXBElement<ReadNativeTypesOutputMsgType> response = (JAXBElement<ReadNativeTypesOutputMsgType>)
                webServiceTemplate.marshalSendAndReceive(request);
        return response.getValue().getNativeTypes();
    }

    /**
     * @see ManagerService#readEditSession(int)
     * {@inheritDoc}
     */
    @Override
    @SuppressWarnings("unchecked")
    public EditSessionType readEditSession(final int code) {
        LOGGER.debug("Preparing ReadEditSessionRequest.....");

        ReadEditSessionInputMsgType msg = new ReadEditSessionInputMsgType();
        msg.setEditSessionCode(code);

        JAXBElement<ReadEditSessionInputMsgType> request = WS_CLIENT_FACTORY.createReadEditSessionRequest(msg);
        JAXBElement<ReadEditSessionOutputMsgType> response = (JAXBElement<ReadEditSessionOutputMsgType>)
                webServiceTemplate.marshalSendAndReceive(request);
        return response.getValue().getEditSession();
    }


    /**
     * @see ManagerService#getFragment(Integer)
     * {@inheritDoc}
     */
    @Override
    @SuppressWarnings("unchecked")
    public FragmentType getFragment(final Integer fragmentId) {
        LOGGER.debug("Invoking getFragment method to retreive fragment content...");

        GetFragmentRequestType msg = new GetFragmentRequestType();
        msg.setFragmentId(fragmentId);

        JAXBElement<GetFragmentRequestType> request = WS_CLIENT_FACTORY.createGetFragmentRequest(msg);
        JAXBElement<FragmentResponseType> response = (JAXBElement<FragmentResponseType>) webServiceTemplate.marshalSendAndReceive(request);
        return response.getValue().getFragment();
    }

    /**
     * @see ManagerService#createClusters(org.apromore.model.ClusterSettingsType)
     * {@inheritDoc}
     */
    @Override
    @SuppressWarnings("unchecked")
    public void createClusters(final ClusterSettingsType settings) {
        LOGGER.debug("Invoking create clusters method ...");

        CreateClustersInputMsgType msg = new CreateClustersInputMsgType();
        msg.setClusterSettings(settings);

        JAXBElement<CreateClustersInputMsgType> request = WS_CLIENT_FACTORY.createCreateClustersRequest(msg);
        JAXBElement<CreateClustersOutputMsgType> response =
                (JAXBElement<CreateClustersOutputMsgType>) webServiceTemplate.marshalSendAndReceive(request);
    }

    /**
     * @see ManagerService#getPairwiseDistances(java.util.List)
     * {@inheritDoc}
     */
    @Override
    @SuppressWarnings("unchecked")
    public List<PairDistanceType> getPairwiseDistances(final List<Integer> fragmentIds) {
        LOGGER.debug("Invoking get pairwise distances method ...");

        GetPairwiseDistancesInputMsgType msg = new GetPairwiseDistancesInputMsgType();
        msg.setFragmentIds(new FragmentIdsType());
        for (Integer fid : fragmentIds) {
            msg.getFragmentIds().getFragmentId().add(fid);
        }
        JAXBElement<GetPairwiseDistancesInputMsgType> request = WS_CLIENT_FACTORY.createPairwiseDistancesRequest(msg);
        JAXBElement<GetPairwiseDistancesOutputMsgType> response =
                (JAXBElement<GetPairwiseDistancesOutputMsgType>) webServiceTemplate.marshalSendAndReceive(request);

        return response.getValue().getPairDistances().getPiarDistance();
    }

    /**
     * @see ManagerService#getClusteringSummary()
     * {@inheritDoc}
     */
    @Override
    @SuppressWarnings("unchecked")
    public ClusteringSummaryType getClusteringSummary() {
        LOGGER.debug("Invoking get clustering summary method ...");

        GetClusteringSummaryInputMsgType msg = new GetClusteringSummaryInputMsgType();
        msg.setParam1("Not required");

        JAXBElement<GetClusteringSummaryInputMsgType> request = WS_CLIENT_FACTORY.createGetClusteringSummaryRequest(msg);
        JAXBElement<GetClusteringSummaryOutputMsgType> response =
                (JAXBElement<GetClusteringSummaryOutputMsgType>) webServiceTemplate.marshalSendAndReceive(request);
        return response.getValue().getClusteringSummary();
    }

    /**
     * @see ManagerService#getClusterSummaries(org.apromore.model.ClusterFilterType)
     * {@inheritDoc}
     */
    @Override
    @SuppressWarnings("unchecked")
    public List<ClusterSummaryType> getClusterSummaries(final ClusterFilterType filter) {
        LOGGER.debug("Invoking get cluster summaries method ...");

        GetClusterSummariesInputMsgType msg = new GetClusterSummariesInputMsgType();
        msg.setFilter(filter);

        JAXBElement<GetClusterSummariesInputMsgType> request = WS_CLIENT_FACTORY.createGetClusterSummariesRequest(msg);
        JAXBElement<GetClusterSummariesOutputMsgType> response =
                (JAXBElement<GetClusterSummariesOutputMsgType>) webServiceTemplate.marshalSendAndReceive(request);
        return response.getValue().getClusterSummaries();
    }

    /**
     * @see ManagerService#getCluster(Integer)
     * {@inheritDoc}
     */
    @Override
    @SuppressWarnings("unchecked")
    public ClusterType getCluster(final Integer clusterId) {
        LOGGER.debug("Invoking get cluster method ...");

        GetClusterInputMsgType msg = new GetClusterInputMsgType();
        msg.setClusterId(clusterId);

        JAXBElement<GetClusterInputMsgType> request = WS_CLIENT_FACTORY.createGetClusterRequest(msg);
        JAXBElement<GetClusterOutputMsgType> response = (JAXBElement<GetClusterOutputMsgType>) webServiceTemplate.marshalSendAndReceive(request);
        return response.getValue().getCluster();
    }

    /**
     * @see ManagerService#getClusters(org.apromore.model.ClusterFilterType)
     * {@inheritDoc}
     */
    @Override
    @SuppressWarnings("unchecked")
    public List<ClusterType> getClusters(final ClusterFilterType filter) {
        LOGGER.debug("Invoking get clusters method ...");

        GetClustersRequestType msg = new GetClustersRequestType();
        msg.setClusterFilter(filter);

        JAXBElement<GetClustersRequestType> request = WS_CLIENT_FACTORY.createGetClustersRequest(msg);
        JAXBElement<GetClustersResponseType> response = (JAXBElement<GetClustersResponseType>) webServiceTemplate.marshalSendAndReceive(request);
        return response.getValue().getClusters();
    }

    /**
     * @see ManagerService#readProcessSummaries(String)
     * {@inheritDoc}
     */
    @Override
    @SuppressWarnings("unchecked")
    public ProcessSummariesType readProcessSummaries(final String searchCriteria) {
        LOGGER.debug("Preparing ReadProcessSummariesRequest.....");

        ReadProcessSummariesInputMsgType msg = new ReadProcessSummariesInputMsgType();
        msg.setSearchExpression(searchCriteria);

        JAXBElement<ReadProcessSummariesInputMsgType> request = WS_CLIENT_FACTORY.createReadProcessSummariesRequest(msg);
        JAXBElement<ReadProcessSummariesOutputMsgType> response =
                (JAXBElement<ReadProcessSummariesOutputMsgType>) webServiceTemplate.marshalSendAndReceive(request);
        return response.getValue().getProcessSummaries();
    }




    /**
     * @see ManagerService#searchForSimilarProcesses(int, String, String, Boolean, double, double, double, double, double, double)
     * {@inheritDoc}
     */
    @Override
    @SuppressWarnings("unchecked")
    public ProcessSummariesType searchForSimilarProcesses(final int processId, final String versionName, final String method,
            final Boolean latestVersions, final double modelThreshold, final double labelThreshold, final double contextThreshold,
            final double skipnWeight, final double subnWeight, final double skipeWeight) {
        LOGGER.debug("Preparing SearchForSimilarProcessesRequest.....");

        SearchForSimilarProcessesInputMsgType msg = new SearchForSimilarProcessesInputMsgType();
        msg.setAlgorithm(method);
        msg.setProcessId(processId);
        msg.setVersionName(versionName);
        msg.setLatestVersions(latestVersions);
        msg.setParameters(SearchForSimilarProcessesHelper.setParams(method, modelThreshold, labelThreshold, contextThreshold, skipnWeight,
                skipeWeight, subnWeight));

        JAXBElement<SearchForSimilarProcessesInputMsgType> request = WS_CLIENT_FACTORY.createSearchForSimilarProcessesRequest(msg);
        JAXBElement<SearchForSimilarProcessesOutputMsgType> response =
                (JAXBElement<SearchForSimilarProcessesOutputMsgType>) webServiceTemplate.marshalSendAndReceive(request);
        return response.getValue().getProcessSummaries();
    }

    /**
     * @see ManagerService#mergeProcesses(java.util.Map, String, String, String, String, String, boolean, double, double, double, double, double, double)
     * {@inheritDoc}
     */
    @Override
    @SuppressWarnings("unchecked")
    public ProcessSummaryType mergeProcesses(final Map<ProcessSummaryType, List<VersionSummaryType>> selectedProcessVersions,
            final String mergedProcessName, final String mergedVersionName, final String mergedDomain, final String mergedUsername,
            final String method, final boolean removeEntanglements, final double mergeThreshold, final double labelThreshold,
            final double contextThreshold, final double skipnWeight, final double subnWeight, final double skipeWeight) {
        LOGGER.debug("Preparing MergeProcessesRequest.....");

        MergeProcessesInputMsgType msg = new MergeProcessesInputMsgType();
        msg.setAlgorithm(method);
        msg.setProcessName(mergedProcessName);
        msg.setVersionName(mergedVersionName);
        msg.setDomain(mergedDomain);
        msg.setUsername(mergedUsername);
        msg.setProcessVersionIds(MergeProcessesHelper.setProcessModels(selectedProcessVersions));
        msg.setParameters(MergeProcessesHelper.setParams(method, removeEntanglements, mergeThreshold, labelThreshold, contextThreshold,
                skipnWeight, skipeWeight, subnWeight));

        JAXBElement<MergeProcessesInputMsgType> request = WS_CLIENT_FACTORY.createMergeProcessesRequest(msg);
        JAXBElement<MergeProcessesOutputMsgType> response =
                (JAXBElement<MergeProcessesOutputMsgType>) webServiceTemplate.marshalSendAndReceive(request);
        return response.getValue().getProcessSummary();
    }

    /**
     * @see ManagerService#exportFormat(int, String, String, String, String, Boolean, String)
     * {@inheritDoc}
     */
    @Override
    @SuppressWarnings("unchecked")
    public ExportFormatResultType exportFormat(final int processId, final String processName, final String versionName, final String nativeType,
            final String annotationName, final Boolean withAnnotations, final String owner, final Set<RequestPropertyType<?>> canoniserProperties) throws Exception {
        LOGGER.debug("Preparing ExportFormatRequest.....");

        ExportFormatInputMsgType msg = new ExportFormatInputMsgType();
        msg.setProcessId(processId);
        msg.setVersionName(versionName);
        msg.setFormat(nativeType);
        msg.setAnnotationName(annotationName);
        msg.setWithAnnotations(withAnnotations);
        msg.setProcessName(processName);
        msg.setOwner(owner);

        msg.setCanoniserProperties(PluginHelper.convertFromPluginProperties(canoniserProperties));

        JAXBElement<ExportFormatInputMsgType> request = WS_CLIENT_FACTORY.createExportFormatRequest(msg);
        JAXBElement<ExportFormatOutputMsgType> response = (JAXBElement<ExportFormatOutputMsgType>) webServiceTemplate.marshalSendAndReceive(request);
        if (response.getValue().getResult().getCode() == -1) {
            throw new Exception(response.getValue().getResult().getMessage());
        } else {
            LOGGER.info(StreamUtil.convertStreamToString(response.getValue().getExportResult().getNative()));
            return response.getValue().getExportResult();
        }
    }

    /**
     * @see ManagerService#importProcess(String, String, String, String, java.io.InputStream, String, String, String, String, Boolean)
     * {@inheritDoc}
     */
    @Override
    @SuppressWarnings("unchecked")
    public ImportProcessResultType importProcess(final String username, final String nativeType, final String processName, final String versionName,
            final InputStream xmlProcess, final String domain, final String documentation, final String created, final String lastUpdate,
            final Set<RequestPropertyType<?>> canoniserProperties) throws IOException, Exception {
        LOGGER.debug("Preparing ImportProcessRequest.....");

        EditSessionType editSession = new EditSessionType();
        editSession.setUsername(username);
        editSession.setNativeType(nativeType);
        editSession.setProcessName(processName);
        editSession.setVersionName(versionName);
        editSession.setDomain(domain);
        editSession.setCreationDate(created);
        editSession.setLastUpdate(lastUpdate);

        ImportProcessInputMsgType msg = new ImportProcessInputMsgType();
        msg.setCanoniserProperties(PluginHelper.convertFromPluginProperties(canoniserProperties));
        msg.setProcessDescription(new DataHandler(new ByteArrayDataSource(xmlProcess, "text/xml")));
        msg.setEditSession(editSession);

        JAXBElement<ImportProcessInputMsgType> request = WS_CLIENT_FACTORY.createImportProcessRequest(msg);
        JAXBElement<ImportProcessOutputMsgType> response = (JAXBElement<ImportProcessOutputMsgType>)
                webServiceTemplate.marshalSendAndReceive(request);
        if (response.getValue().getResult().getCode() == -1) {
            throw new Exception(response.getValue().getResult().getMessage());
        } else {
            return response.getValue().getImportProcessResult();
        }
    }

    /**
     * @see ManagerService#updateProcess(int, String, String, int, String, String, String, String, java.io.InputStream)
     * {@inheritDoc}
     */
    @Override
    @SuppressWarnings("unchecked")
    public void updateProcess(final int sessionCode, final String username, final String nativeType, final int processId, final String domain,
            final String processName, final String new_versionName, final String preVersion, final InputStream native_is)
            throws IOException, Exception {
        LOGGER.debug("Preparing UpdateProcessRequest.....");

        EditSessionType editSession = new EditSessionType();
        editSession.setUsername(username);
        editSession.setNativeType(nativeType);
        editSession.setProcessName(processName);
        editSession.setVersionName(new_versionName);
        editSession.setDomain(domain);
        editSession.setProcessId(processId);

        UpdateProcessInputMsgType msg = new UpdateProcessInputMsgType();
        msg.setEditSessionCode(sessionCode);
        msg.setPreVersion(preVersion);
        msg.setEditSession(editSession);
        msg.setNative(new DataHandler(new ByteArrayDataSource(native_is, "text/xml")));

        JAXBElement<UpdateProcessInputMsgType> request = WS_CLIENT_FACTORY.createUpdateProcessRequest(msg);
        JAXBElement<UpdateProcessOutputMsgType> response = (JAXBElement<UpdateProcessOutputMsgType>)
                webServiceTemplate.marshalSendAndReceive(request);
        if (response.getValue().getResult().getCode() == -1) {
            throw new Exception(response.getValue().getResult().getMessage());
        }
    }




    /**
     * @see ManagerService#editProcessData(Integer, String, String, String, String, String, String)
     * {@inheritDoc}
     */
    @Override
    @SuppressWarnings("unchecked")
    public void editProcessData(final Integer processId, final String processName, final String domain, final String username,
            final String preVersion, final String newVersion, final String ranking) throws Exception {
        LOGGER.debug("Preparing EditProcessDataRequest.....");

        EditProcessDataInputMsgType msg = new EditProcessDataInputMsgType();
        msg.setDomain(domain);
        msg.setProcessName(processName);
        msg.setOwner(username);
        msg.setId(processId);
        msg.setNewName(newVersion);
        msg.setPreName(preVersion);
        msg.setRanking(ranking);

        JAXBElement<EditProcessDataInputMsgType> request = WS_CLIENT_FACTORY.createEditProcessDataRequest(msg);
        JAXBElement<EditProcessDataOutputMsgType> response =
                (JAXBElement<EditProcessDataOutputMsgType>) webServiceTemplate.marshalSendAndReceive(request);
        if (response.getValue().getResult().getCode() == -1) {
            throw new Exception(response.getValue().getResult().getMessage());
        }
    }




    /**
     * @see ManagerService#writeUser(org.apromore.model.UserType)
     * {@inheritDoc}
     */
    @Override
    @SuppressWarnings("unchecked")
    public void writeUser(final UserType user) throws Exception {
        LOGGER.debug("Preparing WriteUserRequest.....");

        WriteUserInputMsgType msg = new WriteUserInputMsgType();
        msg.setUser(user);

        JAXBElement<WriteUserInputMsgType> request = WS_CLIENT_FACTORY.createWriteUserRequest(msg);
        JAXBElement<WriteUserOutputMsgType> response = (JAXBElement<WriteUserOutputMsgType>) webServiceTemplate.marshalSendAndReceive(request);
        if (response.getValue().getResult().getCode() == -1) {
            throw new Exception(response.getValue().getResult().getMessage());
        }
    }

    /**
     * @see ManagerService#writeAnnotation(Integer, String, boolean, Integer, String, String, java.io.InputStream)
     * {@inheritDoc}
     */
    @Override
    @SuppressWarnings("unchecked")
    public void writeAnnotation(final Integer editSessionCode, final String annName, final boolean isNew, final Integer processId,
            final String version, final String nat_type, final InputStream native_is) throws IOException, Exception {
        LOGGER.debug("Preparing WriteAnnotationRequest.....");

        WriteAnnotationInputMsgType msg = new WriteAnnotationInputMsgType();
        msg.setEditSessionCode(editSessionCode);
        msg.setAnnotationName(annName);
        msg.setIsNew(isNew);
        msg.setProcessId(processId);
        msg.setVersion(version);
        msg.setNativeType(nat_type);
        msg.setNative(new DataHandler(new ByteArrayDataSource(native_is, "text/xml")));

        JAXBElement<WriteAnnotationInputMsgType> request = WS_CLIENT_FACTORY.createWriteAnnotationRequest(msg);
        JAXBElement<WriteAnnotationOutputMsgType> response =
                (JAXBElement<WriteAnnotationOutputMsgType>) webServiceTemplate.marshalSendAndReceive(request);

        if (response.getValue().getResult().getCode() == -1) {
            throw new Exception(response.getValue().getResult().getMessage());
        }
    }

    /**
     * @see ManagerService#writeEditSession(org.apromore.model.EditSessionType)
     * {@inheritDoc}
     */
    @Override
    @SuppressWarnings("unchecked")
    public int writeEditSession(final EditSessionType editSession) throws Exception {
        LOGGER.debug("Preparing WriteEditSessionRequest.....");

        WriteEditSessionInputMsgType msg = new WriteEditSessionInputMsgType();
        msg.setEditSession(editSession);

        JAXBElement<WriteEditSessionInputMsgType> request = WS_CLIENT_FACTORY.createWriteEditSessionRequest(msg);
        JAXBElement<WriteEditSessionOutputMsgType> response =
                (JAXBElement<WriteEditSessionOutputMsgType>) webServiceTemplate.marshalSendAndReceive(request);

        if (response.getValue().getResult().getCode() == -1) {
            throw new Exception(response.getValue().getResult().getMessage());
        } else {
            return response.getValue().getEditSessionCode();
        }
    }


    /**
     * @see ManagerService#deleteEditSession(int)
     * {@inheritDoc}
     */
    @Override
    @SuppressWarnings("unchecked")
    public void deleteEditSession(final int code) throws Exception {
        LOGGER.debug("Preparing DeleteEditionSessionRequest.....");

        DeleteEditSessionInputMsgType msg = new DeleteEditSessionInputMsgType();
        msg.setEditSessionCode(code);

        JAXBElement<DeleteEditSessionInputMsgType> request = WS_CLIENT_FACTORY.createDeleteEditSessionRequest(msg);
        JAXBElement<DeleteEditSessionOutputMsgType> response =
                (JAXBElement<DeleteEditSessionOutputMsgType>) webServiceTemplate.marshalSendAndReceive(request);

        if (response.getValue().getResult().getCode() == -1) {
            throw new Exception(response.getValue().getResult().getMessage());
        }
    }

    /**
     * @see ManagerService#deleteProcessVersions(java.util.Map)
     * {@inheritDoc}
     */
    @Override
    @SuppressWarnings("unchecked")
    public void deleteProcessVersions(final Map<ProcessSummaryType, List<VersionSummaryType>> processVersions) throws Exception {
        LOGGER.debug("Preparing DeleteProcessVersions.....");

        DeleteProcessVersionsInputMsgType msg = new DeleteProcessVersionsInputMsgType();
        msg.getProcessVersionIdentifier().addAll(DeleteProcessVersionHelper.setProcessModels(processVersions));

        JAXBElement<DeleteProcessVersionsInputMsgType> request = WS_CLIENT_FACTORY.createDeleteProcessVersionsRequest(msg);
        JAXBElement<DeleteProcessVersionsOutputMsgType> response =
                (JAXBElement<DeleteProcessVersionsOutputMsgType>) webServiceTemplate.marshalSendAndReceive(request);

        if (response.getValue().getResult().getCode() == -1) {
            throw new Exception(response.getValue().getResult().getMessage());
        }
    }

    /* (non-Javadoc)
     * @see org.apromore.manager.client.ManagerService#readPluginInfo(java.lang.String, java.lang.String)
     */
    @Override
    public PluginInfoResult readPluginInfo(final String name, final String version) throws Exception {

        ReadPluginInfoInputMsgType msg = new ReadPluginInfoInputMsgType();
        msg.setPluginName(name);
        msg.setPluginVersion(version);

        JAXBElement<ReadPluginInfoInputMsgType> request = WS_CLIENT_FACTORY.createReadPluginInfoRequest(msg);
        @SuppressWarnings("unchecked")
        JAXBElement<ReadPluginInfoOutputMsgType> response = (JAXBElement<ReadPluginInfoOutputMsgType>)
                webServiceTemplate.marshalSendAndReceive(request);
        if (response.getValue().getResult().getCode() == -1) {
            throw new Exception(response.getValue().getResult().getMessage());
        } else {
            return response.getValue().getPluginInfoResult();
        }
    }


    /* (non-Javadoc)
     * @see org.apromore.manager.client.ManagerService#readInstalledPlugins(java.lang.String)
     */
    @Override
    public Collection<PluginInfo> readInstalledPlugins(final String typeFilter) throws Exception {

        ReadInstalledPluginsInputMsgType msg = new ReadInstalledPluginsInputMsgType();
        msg.setTypeFilter(typeFilter);

        JAXBElement<ReadInstalledPluginsInputMsgType> request = WS_CLIENT_FACTORY.createReadInstalledPluginsRequest(msg);
        @SuppressWarnings("unchecked")
        JAXBElement<ReadInstalledPluginsOutputMsgType> response = (JAXBElement<ReadInstalledPluginsOutputMsgType>)
                webServiceTemplate.marshalSendAndReceive(request);
        if (response.getValue().getResult().getCode() == -1) {
            throw new Exception(response.getValue().getResult().getMessage());
        } else {
            return response.getValue().getPluginInfo();
        }

    }

    /* (non-Javadoc)
     * @see org.apromore.manager.client.ManagerService#readCanoniserInfo(java.lang.String)
     */
    @Override
    public Set<PluginInfo> readCanoniserInfo(final String nativeType) throws Exception {
        LOGGER.debug("Preparing readCanoniserInfo ...");

        ReadCanoniserInfoInputMsgType msg = new ReadCanoniserInfoInputMsgType();
        msg.setNativeType(nativeType);

        JAXBElement<ReadCanoniserInfoInputMsgType> request = WS_CLIENT_FACTORY.createReadCanoniserInfoRequest(msg);
        @SuppressWarnings("unchecked")
        JAXBElement<ReadCanoniserInfoOutputMsgType> response = (JAXBElement<ReadCanoniserInfoOutputMsgType>)
                webServiceTemplate.marshalSendAndReceive(request);
        if (response.getValue().getResult().getCode() == -1) {
            throw new Exception(response.getValue().getResult().getMessage());
        } else {
            Set<PluginInfo> infoSet = new HashSet<PluginInfo>();
            for (PluginInfo pluginInfo: response.getValue().getPluginInfo()) {
                infoSet.add(pluginInfo);
            }
            return infoSet;
        }
    }

    /* (non-Javadoc)
     * @see org.apromore.manager.client.ManagerService#readNativeMetaData(java.lang.String, java.lang.String, java.lang.String, java.io.InputStream)
     */
    @Override
    public NativeMetaData readNativeMetaData(final String nativeType, final String canoniserName, final String canoniserVersion, final InputStream nativeProcess)
            throws Exception {

        ReadNativeMetaDataInputMsgType msg = new ReadNativeMetaDataInputMsgType();
        msg.setCanoniserName(canoniserName);
        msg.setCanoniserVersion(canoniserVersion);
        msg.setNativeType(nativeType);
        msg.setNativeFormat(new DataHandler(new ByteArrayDataSource(nativeProcess, "text/xml")));

        JAXBElement<ReadNativeMetaDataInputMsgType> request = WS_CLIENT_FACTORY.createReadNativeMetaDataRequest(msg);
        @SuppressWarnings("unchecked")
        JAXBElement<ReadNativeMetaDataOutputMsgType> response = (JAXBElement<ReadNativeMetaDataOutputMsgType>)
                webServiceTemplate.marshalSendAndReceive(request);
        if (response.getValue().getResult().getCode() == -1) {
            throw new Exception(response.getValue().getResult().getMessage());
        } else {
            return response.getValue().getNativeMetaData();
        }

    }

    /* (non-Javadoc)
     * @see org.apromore.manager.client.ManagerService#readInitialNativeFormat(java.lang.String, java.lang.String, java.lang.String)
     */
    @Override
    public DataHandler readInitialNativeFormat(final String nativeType, final String canoniserName, final String canoniserVersion, final String owner, final String processName, final String versionName, final String creationDate) throws Exception {

        ReadInitialNativeFormatInputMsgType msg = new ReadInitialNativeFormatInputMsgType();
        msg.setCanoniserName(canoniserName);
        msg.setCanoniserVersion(canoniserVersion);
        msg.setNativeType(nativeType);

        NativeMetaData metaData = new NativeMetaData();
        metaData.setProcessAuthor(owner);
        metaData.setProcessVersion(versionName);
        metaData.setProcessName(processName);
        msg.setNativeMetaData(metaData);

        JAXBElement<ReadInitialNativeFormatInputMsgType> request = WS_CLIENT_FACTORY.createReadInitialNativeFormatRequest(msg);
        @SuppressWarnings("unchecked")
        JAXBElement<ReadInitialNativeFormatOutputMsgType> response = (JAXBElement<ReadInitialNativeFormatOutputMsgType>)
                webServiceTemplate.marshalSendAndReceive(request);
        if (response.getValue().getResult().getCode() == -1) {
            throw new Exception(response.getValue().getResult().getMessage());
        } else {
            return response.getValue().getNativeFormat();
        }

    }


    /* (non-Javadoc)
     * @see org.apromore.manager.client.ManagerService#readCanoniserInfo(java.lang.String)
     */
    @Override
    public Set<PluginInfo> readDeploymentPluginInfo(final String nativeType) throws Exception {
        LOGGER.debug("Preparing readDeploymentPlugin ...");

        ReadDeploymentPluginInfoInputMsgType msg = new ReadDeploymentPluginInfoInputMsgType();
        msg.setNativeType(nativeType);

        JAXBElement<ReadDeploymentPluginInfoInputMsgType> request = WS_CLIENT_FACTORY.createReadDeploymentPluginInfoRequest(msg);
        @SuppressWarnings("unchecked")
        JAXBElement<ReadDeploymentPluginInfoOutputMsgType> response = (JAXBElement<ReadDeploymentPluginInfoOutputMsgType>)
                webServiceTemplate.marshalSendAndReceive(request);
        if (response.getValue().getResult().getCode() == -1) {
            throw new Exception(response.getValue().getResult().getMessage());
        } else {
            Set<PluginInfo> infoSet = new HashSet<PluginInfo>();
            for (PluginInfo pluginInfo: response.getValue().getPluginInfo()) {
                infoSet.add(pluginInfo);
            }
            return infoSet;
        }
    }

    @Override
    public PluginMessages deployProcess(final String branchName, final String processName, final String versionName, final String nativeType, final String pluginName, final String pluginVersion, final Set<RequestPropertyType<?>> deploymentProperties) throws Exception {
        LOGGER.debug("Preparing deployProcess ...");

        DeployProcessInputMsgType msg = new DeployProcessInputMsgType();
        msg.setBranchName(branchName);
        msg.setProcessName(processName);
        msg.setVersionName(versionName);
        msg.setNativeType(nativeType);

        msg.setDeploymentPluginName(pluginName);
        msg.setDeploymentPluginVersion(pluginVersion);

        msg.setDeploymentProperties(PluginHelper.convertFromPluginProperties(deploymentProperties));

        JAXBElement<DeployProcessInputMsgType> request = WS_CLIENT_FACTORY.createDeployProcessRequest(msg);
        @SuppressWarnings("unchecked")
        JAXBElement<DeployProcessOutputMsgType> response = (JAXBElement<DeployProcessOutputMsgType>)
                webServiceTemplate.marshalSendAndReceive(request);
        if (response.getValue().getResult().getCode() == -1) {
            throw new Exception(response.getValue().getResult().getMessage());
        } else {
            return response.getValue().getMessage();
        }
    }


}
