package org.apromore.manager.client;

import java.io.IOException;
import java.io.InputStream;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.activation.DataHandler;

import org.apromore.model.ClusterFilterType;
import org.apromore.model.ClusterSettingsType;
import org.apromore.model.ClusterSummaryType;
import org.apromore.model.ClusterType;
import org.apromore.model.ClusteringSummaryType;
import org.apromore.model.DomainsType;
import org.apromore.model.EditSessionType;
import org.apromore.model.ExportFormatResultType;
import org.apromore.model.FolderType;
import org.apromore.model.FragmentType;
import org.apromore.model.ImportProcessResultType;
import org.apromore.model.NativeMetaData;
import org.apromore.model.NativeTypesType;
import org.apromore.model.PairDistanceType;
import org.apromore.model.PluginInfo;
import org.apromore.model.PluginInfoResult;
import org.apromore.model.PluginMessages;
import org.apromore.model.ProcessSummariesType;
import org.apromore.model.ProcessSummaryType;
import org.apromore.model.UserFolderType;
import org.apromore.model.UserType;
import org.apromore.model.UsernamesType;
import org.apromore.model.VersionSummaryType;
import org.apromore.plugin.property.RequestParameterType;

/**
 * Manager interface.
 *
 * @author <a href="mailto:cam.james@gmail.com">Cameron James</a>
 */
public interface ManagerService {

    /**
     * the User record.
     * @param username the users login name
     * @return the UserType from the webservice
     */
    UserType readUser(String username);

    /**
     * the User record.
     * @param searchString the users username
     * @return the UserType from the webservice
     */
    List<UserType> searchUsers(String searchString);

    /**
     * the User record.
     * @param username the users login name
     * @return the UserType from the webservice
     */
    UserType login(String username, String password);

    List<FolderType> getWorkspaceFolderTree(String userId);

    List<FolderType> getSubFolders(String userId, int folderId);

    List<FolderType> getBreadcrumbs(String userId, int folderId);

    List<UserFolderType> getFolderUsers(int folderId);

    List<UserFolderType> getProcessUsers(int processId);

    List<ProcessSummaryType> getProcesses(String userId, int folderId);

    void createFolder(String userId, String folderName, int parentFolderId);

    void addProcessToFolder(int processId, int folderId);

    void updateFolder(int folderId, String folderName);

    void deleteFolder(int folderId);

    String saveFolderPermissions(int folderId, String userId, boolean hasRead, boolean hasWrite, boolean hasOwnership);

    String saveProcessPermissions(int processId, String userId, boolean hasRead, boolean hasWrite, boolean hasOwnership);

    String removeFolderPermissions(int folderId, String userId);

    String removeProcessPermissions(int processId, String userId);

    /**
     * Read all the users from the apromore manager.
     * @return the UsernameType from the Webservice
     */
    UsernamesType readAllUsers();

    /**
     * Reads all the different domains from the apromore manager.
     * @return the DomainsType from the WebService
     */
    DomainsType readDomains();

    /**
     * Reads all the Native Types from the apromore manager.
     * @return the NativeTypesType from the WebService
     */
    NativeTypesType readNativeTypes();

    /**
     * Reads the Edit session values for a particular code.
     * @param code the code we are searching for
     * @return the EditSessionType from the WebService
     */
    EditSessionType readEditSession(int code);

    /**
     * Create a Cluster.
     * @param settings The settings
     */
    void createClusters(ClusterSettingsType settings);

    /**
     * Get the cluster Summaries.
     * @param filter the search filter
     * @return the list of cluster summaries
     */
    List<ClusterSummaryType> getClusterSummaries(ClusterFilterType filter);

    /**
     * Get a Cluster.
     * @param clusterId the Id of the Cluster we want
     * @return the found cluster
     */
    ClusterType getCluster(Integer clusterId);

    /**
     * Get a list of clusters.
     * @param filter the cluster Filter
     * @return the found list of clusters
     */
    List<ClusterType> getClusters(ClusterFilterType filter);

    /**
     * the cluster summary.
     * @return a summary of all clusters
     */
    ClusteringSummaryType getClusteringSummary();

    /**
     * get a Fragment.
     * @param fragmentId the id of the fragment we want
     * @return the found fragment
     */
    FragmentType getFragment(Integer fragmentId);

    /**
     * get the distance between two fragments.
     * @param fragmentIds the Id's of the fragments want to find the distances.
     * @return the list of distances
     */
    List<PairDistanceType> getPairwiseDistances(List<Integer> fragmentIds);

    /**
     * Get the Process Summaries from the Apromore Manager.
     * @param searchCriteria the search criteria to restrict the results
     * @return the ProcessSummaryType from the WebService
     */
    ProcessSummariesType readProcessSummaries(String searchCriteria);


    /**
     * Run a search for similar processes models.
     * @param processId the search criteria being a process model
     * @param versionName the version name of the process model search criteria
     * @param method the method of search algorithm
     * @param latestVersions only search the latest version.
     * @param modelThreshold the Model Threshold
     * @param labelThreshold the Label Threshold
     * @param contextThreshold the Context Threshold
     * @param skipnWeight the Skip weight
     * @param subnWeight the Sub N weight
     * @param skipeWeight the Skip E weight
     * @return processSummary type for the Web Service
     */
    ProcessSummariesType searchForSimilarProcesses(int processId, String versionName, String method, Boolean latestVersions, double modelThreshold, double labelThreshold, double contextThreshold, double skipnWeight, double subnWeight, double skipeWeight);

    /**
     * Merge two or more processes.
     * @param selectedProcessVersions the select process models versions
     * @param mergedProcessName the new process model name
     * @param mergedVersionName the new process version name
     * @param mergedDomain the new process model domain name
     * @param mergedUsername the new process model username who modified/merged
     * @param folderId the folder we are going to save the document in.
     * @param method the method of search algorithm
     * @param removeEntanglements remove the entanglements
     * @param mergeThreshold the Model Threshold
     * @param labelThreshold the Label Threshold
     * @param contextThreshold the Context Threshold
     * @param skipnWeight the Skip weight
     * @param subnWeight the Sub N weight
     * @param skipeWeight the Skip E weight
     * @return the processSummaryType from the WebService
     */
    ProcessSummaryType mergeProcesses(Map<ProcessSummaryType, List<VersionSummaryType>> selectedProcessVersions, String mergedProcessName,
            String mergedVersionName, String mergedDomain, String mergedUsername, Integer folderId, String method, boolean removeEntanglements,
            double mergeThreshold, double labelThreshold, double contextThreshold, double skipnWeight, double subnWeight, double skipeWeight);

    /**
     * Export the process model in a particular format.
     * @param processId the process to export
     * @param processName the process name
     * @param branch the process branch name
     * @param nativeType the native type of the process model
     * @param annotationName the annotations name
     * @param withAnnotations with ot without annotations
     * @param owner the owner of the model
     * @return the request process model as a Stream
     * @throws Exception ... change to be something more relevant TODO: Fix Exception
     */
    ExportFormatResultType exportFormat(int processId, String processName, String branch, Double versionNumber, String nativeType,
            String annotationName, Boolean withAnnotations, String owner, Set<RequestParameterType<?>> canoniserProperties)
            throws Exception;

    /**
     * Import a process into the Apromore Repository.
     * @param username The username of the user importing the process
     * @param nativeType the native type of the process
     * @param processName the processes name
     * @param versionNumber the version number of this model.
     * @param xml_process the process as an XML Stream. The Actual Data
     * @param domain the domain this process model belongs
     * @param documentation any documentation that is needed with this process
     * @param created the date and time created
     * @param lastUpdate the date and time last updated
     * @param canoniserProperties canoniser properties to use
     * @return ProcessSummary List of processes after the import.
     * @throws java.io.IOException if the streams cause issues
     * @throws Exception ... change to be something more relevant TODO: Fix Exception
     */
    ImportProcessResultType importProcess(String username, Integer folderId, String nativeType, String processName, Double versionNumber,
            InputStream xml_process, String domain, String documentation, String created, String lastUpdate,
            Set<RequestParameterType<?>> canoniserProperties) throws IOException, Exception;

    /**
     * Get list of all currently installed Plugins.
     * @param typeFilter so filter by type of Plugin, shows all if NULL
     * @return PluginInfo for Plugin that is found
     * @throws Exception if communication failed
     */
    Collection<PluginInfo> readInstalledPlugins(String typeFilter) throws Exception;

    /**
     * Get information about the Plugin with given name and version.
     * @param name of Plugin
     * @param version of Plugin
     * @return PluginInfo for Plugin that is found
     * @throws Exception if Plugin is not found
     */
    PluginInfoResult readPluginInfo(String name, String version) throws Exception;

    /**
     * Get information about all installed Canoniser's for the specified native type.
     * @param nativeType of the process
     * @return Set of PluginInfo
     * @throws Exception TODO: Fix Exception
     */
    Set<PluginInfo> readCanoniserInfo(String nativeType) throws Exception;

    /**
     * Read some meta data form the native process XML, optionally using a special Canoniser
     * @param nativeType of the process
     * @param canoniserName use a special Canoniser (optional, may be NULL)
     * @param canoniserVersion use a special Canoniser (optional, may be NULL)
     * @param nativeProcess the process as an XML Stream.
     * @return Meta data of Process
     * @throws Exception in case of any error
     */
    NativeMetaData readNativeMetaData(String nativeType, String canoniserName, String canoniserVersion, InputStream nativeProcess) throws Exception;

    /**
     * Get the initial process XML for specified native type, optionally using a specific Canoniser
     * @param nativeType of the process
     * @param canoniserName use a special Canoniser (optional, may be NULL)
     * @param canoniserVersion use a special Canoniser (optional, may be NULL)
     * @param owner create with this meta data (optional, may be NULL)
     * @param processName create with this meta data (optional, may be NULL)
     * @param versionName create with this meta data (optional, may be NULL)
     * @param creationDate create with this meta data (optional, may be NULL)
     * @return XML in native format
     * @throws Exception in case of any error
     */
    DataHandler readInitialNativeFormat(String nativeType, String canoniserName, String canoniserVersion, String owner, String processName,
            String versionName, String creationDate) throws Exception;

    /**
     * Get information about all installed Deployment Plugins for the specified native type.
     * @param nativeType of the process
     * @return Set of PluginInfo about installed Deployment PLugins supporting the native type
     * @throws Exception in case of any error
     */
    Set<PluginInfo> readDeploymentPluginInfo(String nativeType) throws Exception;

    /**
     * Deploy process to a running process engine
     * @param branchName of the process to be deployed
     * @param processName of the process to be deployed
     * @param versionName of the process to be deployed
     * @param nativeType of the process to be deployed
     * @param pluginName of the deployment plugin to use
     * @param pluginVersion of the deployment plugin to use
     * @param deploymentProperties to be used
     * @return any messages that the Deployment Plugin produced
     * @throws Exception in case of any error
     */
    PluginMessages deployProcess(String branchName, String processName, Double versionName, String nativeType, String pluginName,
            String pluginVersion, Set<RequestParameterType<?>> deploymentProperties) throws Exception;

    /**
     * Update a process in the Apromore repository.
     * @param sessionCode The Session Code.
     * @param username the Username.
     * @param nativeType the process Native type.
     * @param processId the process Identifier.
     * @param domain the process domain.
     * @param processName the process name.
     * @param originalBranchName the originalBranchName.
     * @param newBranchName the originalBranchName.
     * @param versionNumber the versionNumber.
     * @param originalVersionNumber the original version number of the model.
     * @param createNewBranch are we forcing a new branch.
     * @param preVersion the process current version.
     * @param native_is the actual input stream of the model.
     * @throws java.io.IOException if the streams cause issues
     * @throws Exception ... change to be something more relevant TODO: Fix Exception
     */
    void updateProcess(Integer sessionCode, String username, String nativeType, Integer processId, String domain, String processName,
            String originalBranchName, String newBranchName, Double versionNumber, Double originalVersionNumber, Boolean createNewBranch,
            String preVersion, InputStream native_is) throws IOException, Exception;

    /**
     * Write the modified processes which are in processVersions. For each of which, preNewVersion gives the mapping between its previous and new
     * names.
     * @param processId the process Identifier
     * @param processName the process name
     * @param domain the domain of the process
     * @param username the user doing the modification
     * @param preVersion the before version
     * @param newVersion the after version
     * @param ranking the ranking of the new model
     * @throws Exception ... change to be something more relevant TODO: Fix Exception
     */
    void editProcessData(Integer processId, String processName, String domain, String username, Double preVersion, Double newVersion, String ranking)
            throws Exception;

    /**
     * Send the users details to be persisted.
     * @param user the user to send to the WebService
     * @throws Exception ... change to be something more relevant
     * TODO: Fix Exception
     */
    UserType writeUser(UserType user) throws Exception;

    /**
     * Store the annotations for a model.
     * @param editSessionCode the edit session code
     * @param annName the annotation name
     * @param isNew new or not?
     * @param processId the process models identifier
     * @param version the version of the model
     * @param nat_type the native type of the model.
     * @param native_is the input stream, which is the annotations
     * @throws java.io.IOException if the input stream has issues.
     * @throws Exception ... change to be something more relevant
     * TODO: Fix Exception
     */
    void writeAnnotation(Integer editSessionCode, String annName, boolean isNew, Integer processId, String version, String nat_type, InputStream native_is) throws IOException, Exception;

    /**
     * set an edit session for a model.
     * @param editSession the edit session to persist.
     * @return the code of the new session.
     * @throws Exception ... change to be something more relevant
     * TODO: Fix Exception
     */
    int writeEditSession(EditSessionType editSession) throws Exception;


    /**
     * Delete an edit session for a model.
     * @param code the edit session code
     * @throws Exception ... change to be something more relevant
     * TODO: Fix Exception
     */
    void deleteEditSession(int code) throws Exception;

    /**
     * Delete process models / versions from the repository.
     * @param processVersions the list of process models
     * @throws Exception ... change to be something more relevant
     * TODO: Fix Exception
     */
    void deleteProcessVersions(Map<ProcessSummaryType, List<VersionSummaryType>> processVersions) throws Exception;


}
