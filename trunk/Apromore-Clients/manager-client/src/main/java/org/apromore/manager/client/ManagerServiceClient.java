package org.apromore.manager.client;

import javax.activation.DataHandler;
import javax.mail.util.ByteArrayDataSource;
import javax.xml.bind.JAXBElement;
import java.io.InputStream;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apromore.helper.PluginHelper;
import org.apromore.manager.client.helper.DeleteProcessVersionHelper;
import org.apromore.manager.client.helper.MergeProcessesHelper;
import org.apromore.manager.client.helper.SearchForSimilarProcessesHelper;
import org.apromore.manager.client.util.StreamUtil;
import org.apromore.model.AddProcessToFolderInputMsgType;
import org.apromore.model.AddProcessToFolderOutputMsgType;
import org.apromore.model.ClusterFilterType;
import org.apromore.model.ClusterSettingsType;
import org.apromore.model.ClusterSummaryType;
import org.apromore.model.ClusterType;
import org.apromore.model.ClusteringSummaryType;
import org.apromore.model.CreateClustersInputMsgType;
import org.apromore.model.CreateFolderInputMsgType;
import org.apromore.model.CreateFolderOutputMsgType;
import org.apromore.model.CreateGEDMatrixInputMsgType;
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
import org.apromore.model.FolderType;
import org.apromore.model.FragmentIdsType;
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
import org.apromore.model.SaveFolderPermissionsInputMsgType;
import org.apromore.model.SaveFolderPermissionsOutputMsgType;
import org.apromore.model.SaveProcessPermissionsInputMsgType;
import org.apromore.model.SaveProcessPermissionsOutputMsgType;
import org.apromore.model.SearchForSimilarProcessesInputMsgType;
import org.apromore.model.SearchForSimilarProcessesOutputMsgType;
import org.apromore.model.SearchHistoriesType;
import org.apromore.model.SearchUserInputMsgType;
import org.apromore.model.SearchUserOutputMsgType;
import org.apromore.model.UpdateFolderInputMsgType;
import org.apromore.model.UpdateFolderOutputMsgType;
import org.apromore.model.UpdateProcessInputMsgType;
import org.apromore.model.UpdateProcessOutputMsgType;
import org.apromore.model.UpdateSearchHistoryInputMsgType;
import org.apromore.model.UpdateSearchHistoryOutputMsgType;
import org.apromore.model.UserFolderType;
import org.apromore.model.UserType;
import org.apromore.model.UsernamesType;
import org.apromore.model.VersionSummaryType;
import org.apromore.model.WriteAnnotationInputMsgType;
import org.apromore.model.WriteAnnotationOutputMsgType;
import org.apromore.model.WriteUserInputMsgType;
import org.apromore.model.WriteUserOutputMsgType;
import org.apromore.plugin.property.RequestParameterType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ws.client.core.WebServiceTemplate;

/**
 * Performance Test for the Apromore Manager Client.
 */
public class ManagerServiceClient implements ManagerService {

    private static final Logger LOGGER = LoggerFactory.getLogger(ManagerServiceClient.class);
    private static final ObjectFactory WS_CLIENT_FACTORY = new ObjectFactory();

    private WebServiceTemplate webServiceTemplate;

    /**
     * Default Constructor.
     * @param webServiceTemplate the webservice template
     */
    public ManagerServiceClient(WebServiceTemplate webServiceTemplate) {
        this.webServiceTemplate = webServiceTemplate;
    }

    /**
     * @see org.apromore.manager.client.ManagerService#readUser(String)
     *      {@inheritDoc}
     */
    @Override
    @SuppressWarnings("unchecked")
    public UserType readUser(String username) {
        LOGGER.debug("Preparing ReadUserRequest.....");

        ReadUserInputMsgType msg = new ReadUserInputMsgType();
        msg.setUsername(username);

        JAXBElement<ReadUserInputMsgType> request = WS_CLIENT_FACTORY.createReadUserRequest(msg);

        JAXBElement<ReadUserOutputMsgType> response = (JAXBElement<ReadUserOutputMsgType>) webServiceTemplate.marshalSendAndReceive(request);
        return response.getValue().getUser();
    }

    /**
     * @see org.apromore.manager.client.ManagerService#readUser(String)
     *      {@inheritDoc}
     */
    @Override
    @SuppressWarnings("unchecked")
    public List<UserType> searchUsers(String searchString) {
        LOGGER.debug("Preparing SearchUserRequest.....");

        SearchUserInputMsgType msg = new SearchUserInputMsgType();
        msg.setSearchString(searchString);

        JAXBElement<SearchUserInputMsgType> request = WS_CLIENT_FACTORY.createSearchUserRequest(msg);

        JAXBElement<SearchUserOutputMsgType> response = (JAXBElement<SearchUserOutputMsgType>) webServiceTemplate.marshalSendAndReceive(request);
        return response.getValue().getUsers();
    }

    /**
     * @see org.apromore.manager.client.ManagerService#readUser(String)
     *      {@inheritDoc}
     */
    @Override
    @SuppressWarnings("unchecked")
    public UserType login(String username, String password) {
        LOGGER.debug("Preparing LoginRequest.....");

        LoginInputMsgType msg = new LoginInputMsgType();
        msg.setUsername(username);
        msg.setPassword(password);

        JAXBElement<LoginInputMsgType> request = WS_CLIENT_FACTORY.createLoginRequest(msg);

        JAXBElement<LoginOutputMsgType> response = (JAXBElement<LoginOutputMsgType>) webServiceTemplate.marshalSendAndReceive(request);
        return response.getValue().getUser();
    }

    /**
     * @see org.apromore.manager.client.ManagerService#readAllUsers()
     *      {@inheritDoc}
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

    @Override
    @SuppressWarnings("unchecked")
    public List<FolderType> getWorkspaceFolderTree(String userId) {
        LOGGER.debug("Preparing GetUserWorkspacesRequest.....");

        GetWorkspaceFolderTreeInputMsgType msg = new GetWorkspaceFolderTreeInputMsgType();
        msg.setUserId(userId);

        JAXBElement<GetWorkspaceFolderTreeInputMsgType> request = WS_CLIENT_FACTORY.createGetWorkspaceFolderTreeRequest(msg);

        JAXBElement<GetWorkspaceFolderTreeOutputMsgType> response = (JAXBElement<GetWorkspaceFolderTreeOutputMsgType>) webServiceTemplate.marshalSendAndReceive(request);
        return response.getValue().getFolders();
    }

    @Override
    @SuppressWarnings("unchecked")
    public List<FolderType> getSubFolders(String userId, int folderId) {
        LOGGER.debug("Preparing GetUserWorkspacesRequest.....");

        GetSubFoldersInputMsgType msg = new GetSubFoldersInputMsgType();
        msg.setUserId(userId);
        msg.setFolderId(folderId);

        JAXBElement<GetSubFoldersInputMsgType> request = WS_CLIENT_FACTORY.createGetSubFoldersRequest(msg);

        JAXBElement<GetSubFoldersOutputMsgType> response = (JAXBElement<GetSubFoldersOutputMsgType>) webServiceTemplate.marshalSendAndReceive(request);
        return response.getValue().getFolders();
    }

    @Override
    @SuppressWarnings("unchecked")
    public List<FolderType> getBreadcrumbs(String userId, int folderId) {
        LOGGER.debug("Preparing GetBreadcrumbsRequest.....");

        GetBreadcrumbsInputMsgType msg = new GetBreadcrumbsInputMsgType();
        msg.setUserId(userId);
        msg.setFolderId(folderId);

        JAXBElement<GetBreadcrumbsInputMsgType> request = WS_CLIENT_FACTORY.createGetBreadcrumbsRequest(msg);

        JAXBElement<GetBreadcrumbsOutputMsgType> response = (JAXBElement<GetBreadcrumbsOutputMsgType>) webServiceTemplate.marshalSendAndReceive(request);
        return response.getValue().getFolders();
    }

    @Override
    @SuppressWarnings("unchecked")
    public List<UserFolderType> getFolderUsers(int folderId) {
        LOGGER.debug("Preparing GetFolderUsersRequest.....");

        GetFolderUsersInputMsgType msg = new GetFolderUsersInputMsgType();
        msg.setFolderId(folderId);

        JAXBElement<GetFolderUsersInputMsgType> request = WS_CLIENT_FACTORY.createGetFolderUsersRequest(msg);

        JAXBElement<GetFolderUsersOutputMsgType> response = (JAXBElement<GetFolderUsersOutputMsgType>) webServiceTemplate.marshalSendAndReceive(request);
        return response.getValue().getUsers();
    }

    @Override
    @SuppressWarnings("unchecked")
    public String saveFolderPermissions(int folderId, String userId, boolean hasRead, boolean hasWrite, boolean hasOwnership) {
        LOGGER.debug("Preparing SaveFolderPermissionsRequest.....");

        SaveFolderPermissionsInputMsgType msg = new SaveFolderPermissionsInputMsgType();
        msg.setFolderId(folderId);
        msg.setUserId(userId);
        msg.setHasRead(hasRead);
        msg.setHasWrite(hasWrite);
        msg.setHasOwnership(hasOwnership);

        JAXBElement<SaveFolderPermissionsInputMsgType> request = WS_CLIENT_FACTORY.createSaveFolderPermissionsRequest(msg);

        JAXBElement<SaveFolderPermissionsOutputMsgType> response = (JAXBElement<SaveFolderPermissionsOutputMsgType>) webServiceTemplate.marshalSendAndReceive(request);
        return response.getValue().getMessage();
    }

    @Override
    @SuppressWarnings("unchecked")
    public String saveProcessPermissions(int processId, String userId, boolean hasRead, boolean hasWrite, boolean hasOwnership) {
        LOGGER.debug("Preparing SaveProcessPermissionsRequest.....");

        SaveProcessPermissionsInputMsgType msg = new SaveProcessPermissionsInputMsgType();
        msg.setProcessId(processId);
        msg.setUserId(userId);
        msg.setHasRead(hasRead);
        msg.setHasWrite(hasWrite);
        msg.setHasOwnership(hasOwnership);

        JAXBElement<SaveProcessPermissionsInputMsgType> request = WS_CLIENT_FACTORY.createSaveProcessPermissionsRequest(msg);

        JAXBElement<SaveProcessPermissionsOutputMsgType> response = (JAXBElement<SaveProcessPermissionsOutputMsgType>) webServiceTemplate.marshalSendAndReceive(request);
        return response.getValue().getMessage();
    }

    @Override
    @SuppressWarnings("unchecked")
    public String removeFolderPermissions(int folderId, String userId) {
        LOGGER.debug("Preparing RemoveFolderPermissionsRequest.....");

        RemoveFolderPermissionsInputMsgType msg = new RemoveFolderPermissionsInputMsgType();
        msg.setFolderId(folderId);
        msg.setUserId(userId);

        JAXBElement<RemoveFolderPermissionsInputMsgType> request = WS_CLIENT_FACTORY.createRemoveFolderPermissionsRequest(msg);

        JAXBElement<RemoveFolderPermissionsOutputMsgType> response = (JAXBElement<RemoveFolderPermissionsOutputMsgType>) webServiceTemplate.marshalSendAndReceive(request);
        return response.getValue().getMessage();
    }

    @Override
    @SuppressWarnings("unchecked")
    public String removeProcessPermissions(int processId, String userId) {
        LOGGER.debug("Preparing RemoveProcessPermissionsRequest.....");

        RemoveProcessPermissionsInputMsgType msg = new RemoveProcessPermissionsInputMsgType();
        msg.setProcessId(processId);
        msg.setUserId(userId);

        JAXBElement<RemoveProcessPermissionsInputMsgType> request = WS_CLIENT_FACTORY.createRemoveProcessPermissionsRequest(msg);

        JAXBElement<RemoveProcessPermissionsOutputMsgType> response = (JAXBElement<RemoveProcessPermissionsOutputMsgType>) webServiceTemplate.marshalSendAndReceive(request);
        return response.getValue().getMessage();
    }

    @Override
    @SuppressWarnings("unchecked")
    public List<UserFolderType> getProcessUsers(int processId) {
        LOGGER.debug("Preparing GetProcessUsersRequest.....");

        GetProcessUsersInputMsgType msg = new GetProcessUsersInputMsgType();
        msg.setProcessId(processId);

        JAXBElement<GetProcessUsersInputMsgType> request = WS_CLIENT_FACTORY.createGetProcessUsersRequest(msg);

        JAXBElement<GetProcessUsersOutputMsgType> response = (JAXBElement<GetProcessUsersOutputMsgType>) webServiceTemplate.marshalSendAndReceive(request);
        return response.getValue().getUsers();
    }

    @Override
    @SuppressWarnings("unchecked")
    public ProcessSummariesType getProcesses(String userId, int folderId) {
        LOGGER.debug("Preparing GetProcessesRequest.....");

        GetProcessesInputMsgType msg = new GetProcessesInputMsgType();
        msg.setUserId(userId);
        msg.setFolderId(folderId);

        JAXBElement<GetProcessesInputMsgType> request = WS_CLIENT_FACTORY.createGetProcessesRequest(msg);

        JAXBElement<GetProcessesOutputMsgType> response = (JAXBElement<GetProcessesOutputMsgType>) webServiceTemplate.marshalSendAndReceive(request);
        return response.getValue().getProcesses();
    }

    @Override
    @SuppressWarnings("unchecked")
    public void createFolder(String userId, String folderName, int parentFolderId) {
        LOGGER.debug("Preparing createFolderRequest.....");

        CreateFolderInputMsgType msg = new CreateFolderInputMsgType();
        msg.setUserId(userId);
        msg.setFolderName(folderName);
        msg.setParentFolderId(parentFolderId);

        JAXBElement<CreateFolderInputMsgType> request = WS_CLIENT_FACTORY.createCreateFolderRequest(msg);

        JAXBElement<CreateFolderOutputMsgType> response = (JAXBElement<CreateFolderOutputMsgType>) webServiceTemplate.marshalSendAndReceive(request);
        response.getValue().getResult();
    }

    @Override
    @SuppressWarnings("unchecked")
    public void addProcessToFolder(int processId, int folderId) {
        LOGGER.debug("Preparing addProcessToFolderRequest.....");

        AddProcessToFolderInputMsgType msg = new AddProcessToFolderInputMsgType();
        msg.setProcessId(processId);
        msg.setFolderId(folderId);

        JAXBElement<AddProcessToFolderInputMsgType> request = WS_CLIENT_FACTORY.createAddProcessToFolderRequest(msg);

        JAXBElement<AddProcessToFolderOutputMsgType> response = (JAXBElement<AddProcessToFolderOutputMsgType>) webServiceTemplate.marshalSendAndReceive(request);
        response.getValue().getResult();
    }

    @Override
    @SuppressWarnings("unchecked")
    public void updateFolder(int folderId, String folderName) {
        LOGGER.debug("Preparing createFolderRequest.....");

        UpdateFolderInputMsgType msg = new UpdateFolderInputMsgType();
        msg.setFolderId(folderId);
        msg.setFolderName(folderName);

        JAXBElement<UpdateFolderInputMsgType> request = WS_CLIENT_FACTORY.createUpdateFolderRequest(msg);

        JAXBElement<UpdateFolderOutputMsgType> response = (JAXBElement<UpdateFolderOutputMsgType>) webServiceTemplate.marshalSendAndReceive(request);
        response.getValue().getResult();
    }

    @Override
    @SuppressWarnings("unchecked")
    public void deleteFolder(int folderId) {
        LOGGER.debug("Preparing deleteFolderRequest.....");

        DeleteFolderInputMsgType msg = new DeleteFolderInputMsgType();
        msg.setFolderId(folderId);

        JAXBElement<DeleteFolderInputMsgType> request = WS_CLIENT_FACTORY.createDeleteFolderRequest(msg);

        JAXBElement<DeleteFolderOutputMsgType> response = (JAXBElement<DeleteFolderOutputMsgType>) webServiceTemplate.marshalSendAndReceive(request);
        response.getValue().getResult();
    }

    /**
     * @see org.apromore.manager.client.ManagerService#readDomains()
     *      {@inheritDoc}
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
     * @see org.apromore.manager.client.ManagerService#readNativeTypes()
     *      {@inheritDoc}
     */
    @Override
    @SuppressWarnings("unchecked")
    public NativeTypesType readNativeTypes() {
        LOGGER.debug("Preparing ReadNativeTypesRequest.....");

        ReadNativeTypesInputMsgType msg = new ReadNativeTypesInputMsgType();
        msg.setEmpty("");

        JAXBElement<ReadNativeTypesInputMsgType> request = WS_CLIENT_FACTORY.createReadNativeTypesRequest(msg);

        JAXBElement<ReadNativeTypesOutputMsgType> response = (JAXBElement<ReadNativeTypesOutputMsgType>) webServiceTemplate.marshalSendAndReceive(request);
        return response.getValue().getNativeTypes();
    }


    /**
     * @see ManagerService#getFragment(Integer)
     *      {@inheritDoc}
     */
    @Override
    @SuppressWarnings("unchecked")
    public GetFragmentOutputMsgType getFragment(final Integer fragmentId) {
        LOGGER.debug("Invoking getFragment method to retreive fragment content...");

        GetFragmentInputMsgType msg = new GetFragmentInputMsgType();
        msg.setFragmentId(fragmentId);

        JAXBElement<GetFragmentInputMsgType> request = WS_CLIENT_FACTORY.createGetFragmentRequest(msg);

        JAXBElement<GetFragmentOutputMsgType> response = (JAXBElement<GetFragmentOutputMsgType>) webServiceTemplate.marshalSendAndReceive(request);
        return response.getValue();
    }

    /**
     * @see ManagerService#createGedMatrix()
     *      {@inheritDoc}
     */
    @Override
    @SuppressWarnings("unchecked")
    public void createGedMatrix() {
        LOGGER.debug("Invoking create GED Matrix method ...");

        CreateGEDMatrixInputMsgType msg = new CreateGEDMatrixInputMsgType();
        JAXBElement<CreateGEDMatrixInputMsgType> request = WS_CLIENT_FACTORY.createCreateGEDMatrixRequest(msg);
        webServiceTemplate.marshalSendAndReceive(request);
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

        webServiceTemplate.marshalSendAndReceive(request);
    }

    /**
     * @see ManagerService#getPairwiseDistances(java.util.List)
     *      {@inheritDoc}
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

        JAXBElement<GetPairwiseDistancesOutputMsgType> response = (JAXBElement<GetPairwiseDistancesOutputMsgType>) webServiceTemplate.marshalSendAndReceive(request);
        return response.getValue().getPairDistances().getPairDistance();
    }

    /**
     * @see ManagerService#getClusteringSummary()
     *      {@inheritDoc}
     */
    @Override
    @SuppressWarnings("unchecked")
    public ClusteringSummaryType getClusteringSummary() {
        LOGGER.debug("Invoking get clustering summary method ...");

        GetClusteringSummaryInputMsgType msg = new GetClusteringSummaryInputMsgType();
        msg.setParam1("Not required");

        JAXBElement<GetClusteringSummaryInputMsgType> request = WS_CLIENT_FACTORY.createGetClusteringSummaryRequest(msg);

        JAXBElement<GetClusteringSummaryOutputMsgType> response = (JAXBElement<GetClusteringSummaryOutputMsgType>) webServiceTemplate.marshalSendAndReceive(request);
        return response.getValue().getClusteringSummary();
    }

    /**
     * @see ManagerService#getClusterSummaries(org.apromore.model.ClusterFilterType)
     *      {@inheritDoc}
     */
    @Override
    @SuppressWarnings("unchecked")
    public List<ClusterSummaryType> getClusterSummaries(final ClusterFilterType filter) {
        LOGGER.debug("Invoking get cluster summaries method ...");

        GetClusterSummariesInputMsgType msg = new GetClusterSummariesInputMsgType();
        msg.setFilter(filter);

        JAXBElement<GetClusterSummariesInputMsgType> request = WS_CLIENT_FACTORY.createGetClusterSummariesRequest(msg);

        JAXBElement<GetClusterSummariesOutputMsgType> response = (JAXBElement<GetClusterSummariesOutputMsgType>) webServiceTemplate.marshalSendAndReceive(request);
        return response.getValue().getClusterSummaries();
    }

    /**
     * @see ManagerService#getCluster(Integer)
     *      {@inheritDoc}
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
     *      {@inheritDoc}
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
     *      {@inheritDoc}
     */
    @Override
    @SuppressWarnings("unchecked")
    public ProcessSummariesType readProcessSummaries(final String searchCriteria) {
        LOGGER.debug("Preparing ReadProcessSummariesRequest.....");

        ReadProcessSummariesInputMsgType msg = new ReadProcessSummariesInputMsgType();
        msg.setSearchExpression(searchCriteria);

        JAXBElement<ReadProcessSummariesInputMsgType> request = WS_CLIENT_FACTORY.createReadProcessSummariesRequest(msg);

        JAXBElement<ReadProcessSummariesOutputMsgType> response = (JAXBElement<ReadProcessSummariesOutputMsgType>) webServiceTemplate.marshalSendAndReceive(request);
        return response.getValue().getProcessSummaries();
    }


    /**
     * @see ManagerService#searchForSimilarProcesses(int, String, String, Boolean, double, double, double, double, double, double)
     *      {@inheritDoc}
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

        JAXBElement<SearchForSimilarProcessesOutputMsgType> response = (JAXBElement<SearchForSimilarProcessesOutputMsgType>) webServiceTemplate.marshalSendAndReceive(request);
        return response.getValue().getProcessSummaries();
    }

    /**
     * @see ManagerService#mergeProcesses(java.util.Map, String, String, String, String, Integer, String, boolean, double, double, double, double, double, double)
     *      {@inheritDoc}
     */
    @Override
    @SuppressWarnings("unchecked")
    public ProcessSummaryType mergeProcesses(final Map<ProcessSummaryType, List<VersionSummaryType>> selectedProcessVersions,
                                             final String mergedProcessName, final String mergedVersionName, final String mergedDomain, final String mergedUsername,
                                             final Integer folderId, final String method, final boolean removeEntanglements, final double mergeThreshold, final double labelThreshold,
                                             final double contextThreshold, final double skipnWeight, final double subnWeight, final double skipeWeight) {
        LOGGER.debug("Preparing MergeProcessesRequest.....");

        MergeProcessesInputMsgType msg = new MergeProcessesInputMsgType();
        msg.setAlgorithm(method);
        msg.setProcessName(mergedProcessName);
        msg.setVersionName(mergedVersionName);
        msg.setDomain(mergedDomain);
        msg.setUsername(mergedUsername);
        msg.setFolderId(folderId);
        msg.setProcessVersionIds(MergeProcessesHelper.setProcessModels(selectedProcessVersions));
        msg.setParameters(MergeProcessesHelper.setParams(method, removeEntanglements, mergeThreshold, labelThreshold, contextThreshold,
                skipnWeight, skipeWeight, subnWeight));

        JAXBElement<MergeProcessesInputMsgType> request = WS_CLIENT_FACTORY.createMergeProcessesRequest(msg);

        JAXBElement<MergeProcessesOutputMsgType> response = (JAXBElement<MergeProcessesOutputMsgType>) webServiceTemplate.marshalSendAndReceive(request);
        return response.getValue().getProcessSummary();
    }

    /**
     * @see ManagerService#exportFormat(int, String, String, Double, String, String, Boolean, String, java.util.Set)
     *      {@inheritDoc}
     */
    @Override
    @SuppressWarnings("unchecked")
    public ExportFormatResultType exportFormat(final int processId, final String processName, final String branchName, final Double versionNumber,
                                               final String nativeType, final String annotationName, final Boolean withAnnotations, final String owner,
                                               final Set<RequestParameterType<?>> canoniserProperties) throws Exception {
        LOGGER.debug("Preparing ExportFormatRequest.....");

        ExportFormatInputMsgType msg = new ExportFormatInputMsgType();
        msg.setProcessId(processId);
        msg.setBranchName(branchName);
        msg.setFormat(nativeType);
        msg.setAnnotationName(annotationName);
        msg.setWithAnnotations(withAnnotations);
        msg.setProcessName(processName);
        msg.setVersionNumber(versionNumber);
        msg.setOwner(owner);

        msg.setCanoniserParameters(PluginHelper.convertFromPluginParameters(canoniserProperties));

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
     * @see ManagerService#importProcess(String, java.lang.Integer, String, String, java.lang.Double, java.io.InputStream, String, String, String, String, java.util.Set)
     *      {@inheritDoc}
     */
    @Override
    @SuppressWarnings("unchecked")
    public ImportProcessResultType importProcess(final String username, final Integer folderId, final String nativeType, final String processName,
            final Double versionNumber, final InputStream xmlProcess, final String domain, final String documentation, final String created,
            final String lastUpdate, final Set<RequestParameterType<?>> canoniserProperties) throws Exception {
        LOGGER.debug("Preparing ImportProcessRequest.....");

        EditSessionType editSession = new EditSessionType();
        editSession.setUsername(username);
        editSession.setFolderId(folderId);
        editSession.setNativeType(nativeType);
        editSession.setProcessName(processName);
        editSession.setCurrentVersionNumber(versionNumber);
        editSession.setDomain(domain);
        editSession.setCreationDate(created);
        editSession.setLastUpdate(lastUpdate);

        ImportProcessInputMsgType msg = new ImportProcessInputMsgType();
        msg.setCanoniserParameters(PluginHelper.convertFromPluginParameters(canoniserProperties));
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
     * @see ManagerService#updateProcess(Integer, String, String, Integer, String, String, String, String, Double, Double, String, java.io.InputStream)
     *      {@inheritDoc}
     */
    @Override
    @SuppressWarnings("unchecked")
    public void updateProcess(final Integer sessionCode, final String username, final String nativeType, final Integer processId,
            final String domain, final String processName, final String originalBranchName, final String newBranchName, final Double versionNumber,
            final Double originalVersionNumber, final String preVersion, final InputStream native_is)
            throws Exception {
        LOGGER.debug("Preparing UpdateProcessRequest.....");

        EditSessionType editSession = new EditSessionType();
        editSession.setUsername(username);
        editSession.setNativeType(nativeType);
        editSession.setProcessName(processName);
        editSession.setOriginalBranchName(originalBranchName);
        editSession.setNewBranchName(newBranchName);
        editSession.setOriginalVersionNumber(originalVersionNumber);
        editSession.setCurrentVersionNumber(versionNumber);
        editSession.setDomain(domain);
        editSession.setProcessId(processId);

        UpdateProcessInputMsgType msg = new UpdateProcessInputMsgType();
        msg.setEditSessionCode(sessionCode);
        msg.setPreVersion(preVersion);
        msg.setEditSession(editSession);
        msg.setNative(new DataHandler(new ByteArrayDataSource(native_is, "text/xml")));

        JAXBElement<UpdateProcessInputMsgType> request = WS_CLIENT_FACTORY.createUpdateProcessRequest(msg);

        JAXBElement<UpdateProcessOutputMsgType> response = (JAXBElement<UpdateProcessOutputMsgType>) webServiceTemplate.marshalSendAndReceive(request);
        if (response.getValue().getResult().getCode() == -1) {
            throw new Exception(response.getValue().getResult().getMessage());
        }
    }


    /**
     * @see ManagerService#editProcessData(Integer, String, String, String, Double, Double, String)
     *      {@inheritDoc}
     */
    @Override
    @SuppressWarnings("unchecked")
    public void editProcessData(final Integer processId, final String processName, final String domain, final String username,
                                final Double preVersion, final Double newVersion, final String ranking) throws Exception {
        LOGGER.debug("Preparing EditProcessDataRequest.....");

        EditProcessDataInputMsgType msg = new EditProcessDataInputMsgType();
        msg.setDomain(domain);
        msg.setProcessName(processName);
        msg.setOwner(username);
        msg.setId(processId);
        msg.setNewVersion(newVersion);
        msg.setPreVersion(preVersion);
        msg.setRanking(ranking);

        JAXBElement<EditProcessDataInputMsgType> request = WS_CLIENT_FACTORY.createEditProcessDataRequest(msg);

        JAXBElement<EditProcessDataOutputMsgType> response = (JAXBElement<EditProcessDataOutputMsgType>) webServiceTemplate.marshalSendAndReceive(request);
        if (response.getValue().getResult().getCode() == -1) {
            throw new Exception(response.getValue().getResult().getMessage());
        }
    }


    /**
     * @see ManagerService#writeUser(org.apromore.model.UserType)
     *      {@inheritDoc}
     */
    @Override
    @SuppressWarnings("unchecked")
    public UserType writeUser(UserType user) throws Exception {
        LOGGER.debug("Preparing WriteUserRequest.....");

        WriteUserInputMsgType msg = new WriteUserInputMsgType();
        msg.setUser(user);

        JAXBElement<WriteUserInputMsgType> request = WS_CLIENT_FACTORY.createWriteUserRequest(msg);

        JAXBElement<WriteUserOutputMsgType> response = (JAXBElement<WriteUserOutputMsgType>) webServiceTemplate.marshalSendAndReceive(request);
        if (response.getValue().getResult().getCode() == -1) {
            throw new Exception(response.getValue().getResult().getMessage());
        }

        return response.getValue().getUser();
    }

    /**
     * @see org.apromore.manager.client.ManagerService#writeAnnotation(Integer, String, boolean, Integer, String, String, java.io.InputStream)
     *      {@inheritDoc}
     */
    @Override
    @SuppressWarnings("unchecked")
    public void writeAnnotation(final Integer editSessionCode, final String annName, final boolean isNew, final Integer processId,
            final String version, final String nat_type, final InputStream native_is) throws Exception {
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

        JAXBElement<WriteAnnotationOutputMsgType> response = (JAXBElement<WriteAnnotationOutputMsgType>) webServiceTemplate.marshalSendAndReceive(request);

        if (response.getValue().getResult().getCode() == -1) {
            throw new Exception(response.getValue().getResult().getMessage());
        }
    }

    /**
     * @see ManagerService#deleteProcessVersions(java.util.Map)
     *      {@inheritDoc}
     */
    @Override
    @SuppressWarnings("unchecked")
    public void deleteProcessVersions(final Map<ProcessSummaryType, List<VersionSummaryType>> processVersions) throws Exception {
        LOGGER.debug("Preparing DeleteProcessVersions.....");

        DeleteProcessVersionsInputMsgType msg = new DeleteProcessVersionsInputMsgType();
        msg.getProcessVersionIdentifier().addAll(DeleteProcessVersionHelper.setProcessModels(processVersions));

        JAXBElement<DeleteProcessVersionsInputMsgType> request = WS_CLIENT_FACTORY.createDeleteProcessVersionsRequest(msg);

        JAXBElement<DeleteProcessVersionsOutputMsgType> response = (JAXBElement<DeleteProcessVersionsOutputMsgType>) webServiceTemplate.marshalSendAndReceive(request);
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
            Set<PluginInfo> infoSet = new HashSet<>();
            for (PluginInfo pluginInfo : response.getValue().getPluginInfo()) {
                infoSet.add(pluginInfo);
            }
            return infoSet;
        }
    }

    /* (non-Javadoc)
     * @see org.apromore.manager.client.ManagerService#readNativeMetaData(java.lang.String, java.lang.String, java.lang.String, java.io.InputStream)
     */
    @Override
    public NativeMetaData readNativeMetaData(final String nativeType, final String canoniserName, final String canoniserVersion,
            final InputStream nativeProcess) throws Exception {
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
    public DataHandler readInitialNativeFormat(final String nativeType, final String canoniserName, final String canoniserVersion, final String owner,
            final String processName, final String versionName, final String creationDate) throws Exception {
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


    /*
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
            Set<PluginInfo> infoSet = new HashSet<>();
            for (PluginInfo pluginInfo : response.getValue().getPluginInfo()) {
                infoSet.add(pluginInfo);
            }
            return infoSet;
        }
    }

    /*
     * @see org.apromore.manager.client.ManagerService#deployProcess
     * {@inheritDoc}
     */
    @Override
    public PluginMessages deployProcess(final String branchName, final String processName, final Double versionName, final String nativeType,
            final String pluginName, final String pluginVersion, final Set<RequestParameterType<?>> deploymentProperties) throws Exception {
        LOGGER.debug("Preparing deployProcess ...");

        DeployProcessInputMsgType msg = new DeployProcessInputMsgType();
        msg.setBranchName(branchName);
        msg.setProcessName(processName);
        msg.setVersionName(versionName);
        msg.setNativeType(nativeType);

        msg.setDeploymentPluginName(pluginName);
        msg.setDeploymentPluginVersion(pluginVersion);

        msg.setDeploymentParameters(PluginHelper.convertFromPluginParameters(deploymentProperties));

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

    /*
     * @see ManagerService#updateSearchHistories
     * {@inheritDoc}
     */
    @Override
    public void updateSearchHistories(UserType user, List<SearchHistoriesType> searchHist) throws Exception {
        LOGGER.debug("Preparing UpdateSearchHistories Request...");

        UpdateSearchHistoryInputMsgType msg = new UpdateSearchHistoryInputMsgType();
        msg.setUser(user);
        msg.getSearchHistory().addAll(searchHist);

        JAXBElement<UpdateSearchHistoryInputMsgType> request = WS_CLIENT_FACTORY.createUpdateSearchHistoryRequest(msg);

        @SuppressWarnings("unchecked")
        JAXBElement<UpdateSearchHistoryOutputMsgType> response = (JAXBElement<UpdateSearchHistoryOutputMsgType>)
                webServiceTemplate.marshalSendAndReceive(request);
        if (response.getValue().getResult().getCode() == -1) {
            throw new Exception(response.getValue().getResult().getMessage());
        }
    }
}
