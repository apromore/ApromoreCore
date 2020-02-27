/*
 * Copyright © 2009-2018 The Apromore Initiative.
 *
 * This file is part of "Apromore".
 *
 * "Apromore" is free software; you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 3 of the
 * License, or (at your option) any later version.
 *
 * "Apromore" is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty
 * of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this program.
 * If not, see <http://www.gnu.org/licenses/lgpl-3.0.html>.
 */

package org.apromore.manager.client;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
//import java.util.stream.Collectors;
import javax.activation.DataHandler;
import javax.inject.Inject;
import javax.mail.util.ByteArrayDataSource;
import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;
import org.apromore.canoniser.Canoniser;
import org.apromore.canoniser.exception.CanoniserException;
import org.apromore.canoniser.result.CanoniserMetadataResult;
import org.apromore.common.Constants;
import org.apromore.cpf.CanonicalProcessType;
//import org.apromore.dao.model.Cluster;
import org.apromore.dao.model.Group;
import org.apromore.dao.model.HistoryEnum;
import org.apromore.dao.model.HistoryEvent;
import org.apromore.dao.model.Log;
import org.apromore.dao.model.NativeType;
import org.apromore.dao.model.ProcessModelVersion;
import org.apromore.dao.model.StatusEnum;
import org.apromore.dao.model.User;
import org.apromore.exception.NotAuthorizedException;
import org.apromore.exception.RepositoryException;
import org.apromore.helper.CanoniserHelper;
import org.apromore.helper.PluginHelper;
import org.apromore.helper.Version;
import org.apromore.manager.client.helper.DeleteProcessVersionHelper;
import org.apromore.manager.client.helper.MergeProcessesHelper;
import org.apromore.manager.client.helper.SearchForSimilarProcessesHelper;
import org.apromore.mapper.*;
import org.apromore.model.*;
import org.apromore.plugin.ParameterAwarePlugin;
import org.apromore.plugin.Plugin;
import org.apromore.plugin.PluginRequestImpl;
import org.apromore.plugin.message.PluginMessage;
import org.apromore.plugin.property.RequestParameterType;
//import org.apromore.plugin.merge.logic.MergeService;
//import org.apromore.plugin.similaritysearch.logic.SimilarityService;
import org.apromore.service.*;
import org.apromore.service.helper.UserInterfaceHelper;
import org.apromore.service.model.CanonisedProcess;
import org.apromore.service.model.Cluster;
import org.apromore.service.model.DecanonisedProcess;
import org.apromore.service.model.ProcessData;
import org.deckfour.xes.model.XLog;
import org.springframework.stereotype.Service;

/**
 * Implements {@link ManagerService} by delegating to OSGi services.
 */
@Service("managerClient")
public class ManagerServiceImpl implements ManagerService {

    @Inject private DeploymentService deploymentService;
    @Inject private PluginService pluginService;
    @Inject private FragmentService fragmentSrv;
    @Inject private CanoniserService canoniserService;
    @Inject private ProcessService procSrv;
    @Inject private EventLogService logSrv;
    @Inject private ClusterService clusterService;
    @Inject private FormatService frmSrv;
    @Inject private DomainService domSrv;
    @Inject private UserService userSrv;
    @Inject private SecurityService secSrv;
    @Inject private WorkspaceService workspaceSrv;
    @Inject private UserInterfaceHelper uiHelper;
    //@Inject private MergeService mergeSrv;
    //@Inject private SimilarityService similaritySrv;
    private boolean enableCPF = true;

    private boolean isGEDMatrixReady = true;


    // Implementation of ManagerService

    /**
     * the User record.
     * @param username the users login name
     * @return the UserType from the webservice
     */
    @Override
    public UserType readUserByUsername(String username) {
        return UserMapper.convertUserTypes(secSrv.getUserByName(username));
    }

    /**
     * the User record.
     * @param searchString the users username
     * @return the UserType from the webservice
     */
    @Override
    public List<UserType> searchUsers(String searchString) {
        //return secSrv.searchUsers(searchString)
        //             .stream()
        //             .map(UserMapper::convertUserTypes)
        //             .collect(Collectors.toList());

        List<UserType> users = new ArrayList<>();
        for (User user: secSrv.searchUsers(searchString)) {
            users.add(UserMapper.convertUserTypes(user));
        }

        return users;
    }

    /**
     * Access group records.
     * @param searchString the users username
     * @return the GroupType from the webservice
     */
    @Override
    public List<GroupType> searchGroups(String searchString) {
        //return secSrv.searchGroups(searchString)
        //             .stream()
        //             .map(GroupMapper::toGroupType)
        //             .collect(Collectors.toList());

        List<GroupType> groups = new ArrayList<>();
        for (Group group: secSrv.searchGroups(searchString)) {
            groups.add(GroupMapper.toGroupType(group));
        }

        return groups;
    }

    /**
     * USed as apart of the reset user's password.
     * @param email the users email address
     * @return the UserType from the webservice
     */
    @Override
    public UserType readUserByEmail(String email) throws Exception {
        return UserMapper.convertUserTypes(secSrv.getUserByEmail(email));
    }

    /**
     * Reset the USers password for them.
     * @param username the users username
     * @param password the new password
     * @return if we succeeded or not
     */
    @Override
    public boolean resetUserPassword(String username, String password) {
        return secSrv.resetUserPassword(username, password);
    }

    @Override
    public List<FolderType> getWorkspaceFolderTree(String userId) {
        return WorkspaceMapper.convertFolderTreeNodesToFolderTypes(workspaceSrv.getWorkspaceFolderTree(userId));
    }

    @Override
    public List<FolderType> getSubFolders(String userId, int folderId) {
        return WorkspaceMapper.convertFoldersToFolderTypes(workspaceSrv.getSubFolders(userId, folderId));
    }

    @Override
    public List<FolderType> getBreadcrumbs(String userId, int folderId) {
        return WorkspaceMapper.convertFolderListToFolderTypes(workspaceSrv.getBreadcrumbs(folderId));
    }

    @Override
    public List<GroupAccessType> getFolderGroups(int folderId) {
        return WorkspaceMapper.convertGroupFoldersToGroupAccessTypes(workspaceSrv.getGroupFolders(folderId));
    }

    @Override
    public List<GroupAccessType> getProcessGroups(int processId) {
        return WorkspaceMapper.convertGroupProcessesToGroupAccessTypes(workspaceSrv.getGroupProcesses(processId));
    }

    @Override
    public List<GroupAccessType> getLogGroups(int logId) {
        return WorkspaceMapper.convertGroupLogsToGroupAccessTypes(workspaceSrv.getGroupLogs(logId));
    }

    @Override
    public SummariesType getProcessSummaries(String userId, int folderId, int pageIndex, int pageSize) {
        return uiHelper.buildProcessSummaryList(userId, folderId, pageIndex, pageSize);
    }

    @Override
    public SummariesType getLogSummaries(String userId, int folderId, int pageIndex, int pageSize) {
        return uiHelper.buildLogSummaryList(userId, folderId, pageIndex, pageSize);
    }

    @Override
    public ImportLogResultType importLog(String username, Integer folderId, String logName, InputStream log, String extension, String domain, String created, boolean makePublic) throws Exception {
        LogSummaryType logSummary = (LogSummaryType) uiHelper.buildLogSummary(logSrv.importLog(username, folderId, logName, log, extension, domain, created, makePublic));
        ImportLogResultType importResult = new ImportLogResultType();
        importResult.setLogSummary(logSummary);

        return importResult;
    }

    @Override
    public void editLogData(Integer logId, String logName, String username, boolean isPublic) throws Exception {
        logSrv.updateLogMetaData(logId, logName, isPublic);
    }

    @Override
    public void createFolder(String userId, String folderName, int parentFolderId) {
        workspaceSrv.createFolder(userId, folderName, parentFolderId, isGEDMatrixReady);
    }

    @Override
    public void addProcessToFolder(int processId, int folderId) {
         workspaceSrv.addProcessToFolder(processId, folderId);
    }

    @Override
    public boolean isGEDReadyFolder(int folderId) {
        return workspaceSrv.isGEDReadyFolder(folderId);
    }

    @Override
    public void updateFolder(int folderId, String folderName, String username) {
        try {
            workspaceSrv.updateFolder(folderId, folderName, isGEDReadyFolder(folderId), secSrv.getUserByName(username));

        } catch (NotAuthorizedException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void deleteFolder(int folderId, String username) {
        try {
            workspaceSrv.deleteFolder(folderId, secSrv.getUserByName(username));

        } catch (NotAuthorizedException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public String saveFolderPermissions(int folderId, String userId, boolean hasRead, boolean hasWrite, boolean hasOwnership) {
        return workspaceSrv.saveFolderPermissions(folderId, userId, hasRead, hasWrite, hasOwnership);
    }

    @Override
    public String saveProcessPermissions(int processId, String userId, boolean hasRead, boolean hasWrite, boolean hasOwnership) {
        return workspaceSrv.saveProcessPermissions(processId, userId, hasRead, hasWrite, hasOwnership);
    }

    @Override
    public String saveLogPermissions(int logId, String userId, boolean hasRead, boolean hasWrite, boolean hasOwnership) {
        return workspaceSrv.saveLogPermissions(logId, userId, hasRead, hasWrite, hasOwnership);
    }

    @Override
    public String removeFolderPermissions(int folderId, String userId) {
        return workspaceSrv.removeFolderPermissions(folderId, userId);
    }

    @Override
    public String removeProcessPermissions(int processId, String userId) {
        return workspaceSrv.removeProcessPermissions(processId, userId);
    }

    @Override
    public String removeLogPermissions(int logId, String userId) {
        return workspaceSrv.removeLogPermissions(logId, userId);
    }

    /**
     * Read all the users from the apromore manager.
     * @return the UsernameType from the Webservice
     */
    @Override
    public UsernamesType readAllUsers() {
        return UserMapper.convertUsernameTypes(userSrv.findAllUsers());
    }

    /**
     * Reads all the different domains from the apromore manager.
     * @return the DomainsType from the WebService
     */
    @Override
    public DomainsType readDomains() {
        return DomainMapper.convertFromDomains(domSrv.findAllDomains());
    }

    /**
     * Reads all the Native Types from the apromore manager.
     * @return the NativeTypesType from the WebService
     */
    @Override
    public NativeTypesType readNativeTypes() {
        return NativeTypeMapper.convertFromNativeType(frmSrv.findAllFormats());
    }


    /**
     * Create a GED Matrix used in the Cluster Creation.
     */
    @Override
    public void createGedMatrix() {
        try {
            clusterService.computeGEDMatrix();

        } catch (RepositoryException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Used to get some basic details about the GED matrix.
     */
    @Override
    public GedMatrixSummaryType getGedMatrixSummary() {
        GedMatrixSummaryType gedMatrixSummary = new GedMatrixSummaryType();
        HistoryEvent gedMatrix = clusterService.getGedMatrixLastExecutionTime();
        GregorianCalendar calendar = new GregorianCalendar();
        try {
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

            return gedMatrixSummary;

        } catch (DatatypeConfigurationException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Create a Cluster.
     * @param settings The settings
     */
    @Override
    public void createClusters(ClusterSettingsType settings) {
        try {
            clusterService.cluster(ClusterMapper.convertClusterSettingsTypeToClusterSettings(settings));

        } catch (RepositoryException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Get the cluster Summaries.
     * @param filter the search filter
     * @return the list of cluster summaries
     */
    @Override
    public List<ClusterSummaryType> getClusterSummaries(ClusterFilterType filter) {
        //return clusterService.getClusterSummaries(ClusterMapper.convertClusterFilterTypeToClusterFilter(filter))
        //                     .stream()
        //                     .map(ClusterMapper::convertClusterInfoToClusterSummaryType)
        //                     .collect(Collectors.toList());

        List<ClusterSummaryType> clusterSummaries = new ArrayList<>();
        for (org.apromore.dao.model.Cluster clusterInfo: clusterService.getClusterSummaries(ClusterMapper.convertClusterFilterTypeToClusterFilter(filter))) {
            clusterSummaries.add(ClusterMapper.convertClusterInfoToClusterSummaryType(clusterInfo));
        }

        return clusterSummaries;
    }

    /**
     * Get a Cluster.
     * @param clusterId the Id of the Cluster we want
     * @return the found cluster
     */
    @Override
    public ClusterType getCluster(Integer clusterId) {
        return ClusterMapper.convertClusterToClusterType(clusterService.getCluster(clusterId));
    }

    /**
     * Get a list of clusters.
     * @param filter the cluster Filter
     * @return the found list of clusters
     */
    @Override
    public List<ClusterType> getClusters(ClusterFilterType filter) {
        //return clusterService.getClusters(ClusterMapper.convertClusterFilterTypeToClusterFilter(filter))
        //                     .stream()
        //                     .map(ClusterMapper::convertClusterToClusterType)
        //                     .collect(Collectors.toList());

        List<ClusterType> clusters = new ArrayList<>();
        for (Cluster cluster: clusterService.getClusters(ClusterMapper.convertClusterFilterTypeToClusterFilter(filter))) {
            clusters.add(ClusterMapper.convertClusterToClusterType(cluster));
        }

        return clusters;
    }

    /**
     * the cluster summary.
     * @return a summary of all clusters
     */
    @Override
    public ClusteringSummaryType getClusteringSummary() {
        return ClusterMapper.convertClusteringSummaryToClusteringSummaryType(clusterService.getClusteringSummary());
    }

    /**
     * get a Fragment.
     * @param fragmentId the id of the fragment we want
     * @return the found fragment
     */
    @Override
    public GetFragmentOutputMsgType getFragment(Integer fragmentId) {

        String defaultFormat = "EPML 2.0";

        try {
            if (!enableCPF) {
                throw new CanoniserException("Fragments not supported because manager.enableCPF is false");
            }

            CanonicalProcessType cpt = fragmentSrv.getFragmentToCanonicalProcessType(fragmentId);
            DecanonisedProcess dp = canoniserService.deCanonise(defaultFormat, cpt, null, new HashSet<RequestParameterType<?>>());

            ExportFragmentResultType exportResult = new ExportFragmentResultType();
            exportResult.setMessage(PluginHelper.convertFromPluginMessages(dp.getMessages()));
            exportResult.setNative(new DataHandler(new ByteArrayDataSource(dp.getNativeFormat(), Constants.XML_MIMETYPE)));

            GetFragmentOutputMsgType res = new GetFragmentOutputMsgType();
            res.setFragmentResult(exportResult);
            res.setNativeType(defaultFormat);
       
            return res;

        } catch (CanoniserException | IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * get the distance between two fragments.
     * @param fragmentIds the Id's of the fragments want to find the distances.
     * @return the list of distances
     */
    @Override
    public List<PairDistanceType> getPairwiseDistances(List<Integer> fragmentIds) {
        try {
            return ClusterMapper.convertPairDistancesToPairDistancesType(clusterService.getPairDistances(fragmentIds))
                                .getPairDistance();

        } catch (RepositoryException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Get the Process Summaries from the Apromore Manager.
     * @param folderId the folder we are currently asking for the process Ids.
     * @param searchCriteria the search criteria to restrict the results
     * @return the ProcessSummaryType from the WebService
     */
    @Override
    public SummariesType readProcessSummaries(Integer folderId, String searchCriteria) {
        return procSrv.readProcessSummaries(folderId, searchCriteria);
    }

    /**
     * Run a search for similar processes models.
     * @param processId the search criteria being a process model
     * @param versionName the version name of the process model search criteria
     * @param method the method of search algorithm
     * @param latestVersions only search the latest version.
     * @param folderId from which folder to search under.
     * @param userId which user is running this query.
     * @param modelThreshold the Model Threshold
     * @param labelThreshold the Label Threshold
     * @param contextThreshold the Context Threshold
     * @param skipnWeight the Skip weight
     * @param subnWeight the Sub N weight
     * @param skipeWeight the Skip E weight
     * @return processSummary type for the Web Service
     */
    @Override
    public SummariesType searchForSimilarProcesses(int processId, String versionName, String method, Boolean latestVersions, int folderId,
            String userId, double modelThreshold, double labelThreshold, double contextThreshold, double skipnWeight, double subnWeight,
            double skipeWeight) {

        /*
        ParametersType params = SearchForSimilarProcessesHelper.setParams(method, modelThreshold, labelThreshold, contextThreshold, skipnWeight, skipeWeight, subnWeight);
        return similaritySrv.searchForSimilarProcesses(processId, versionName, latestVersions, folderId, userId, method, params);
        */

        throw new Error("Not implemented");
    }

    /**
     * Merge two or more processes.
     * @param selectedProcessVersions the select process models versions
     * @param mergedProcessName the new process model name
     * @param mergedVersionName the new process version name
     * @param mergedDomain the new process model domain name
     * @param mergedUsername the new process model username who modified/merged
     * @param folderId the folder we are going to save the document in.
     * @param makePublic do we make this new model public?
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
    @Override
    public ProcessSummaryType mergeProcesses(Map<ProcessSummaryType, List<VersionSummaryType>> selectedProcessVersions, String mergedProcessName,
            String mergedVersionName, String mergedDomain, String mergedUsername, Integer folderId, boolean makePublic, String method,
            boolean removeEntanglements, double mergeThreshold, double labelThreshold, double contextThreshold, double skipnWeight,
            double subnWeight, double skipeWeight) {

        /*
        ParameterType parameters = MergeProcessesHelper.setParams(method, removeEntanglements, mergeThreshold, labelThreshold, contextThreshold, skipnWeight, skipeWeight, subnWeight);
        return mergeSrv.mergeProcesses(String processName, String version, String domain, String username, String algo, Integer folderId,
                                       ParametersType parameters, ProcessVersionIdsType ids, boolean makePublic)
        */

        throw new Error("Not implemented");
    }

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
    @Override
    public ExportFormatResultType exportFormat(int processId, String processName, String branch, String versionNumber, String nativeType,
            String annotationName, Boolean withAnnotations, String owner, Set<RequestParameterType<?>> canoniserProperties)
            throws Exception {

        return procSrv.exportProcess(processName, processId, branch, new Version(versionNumber), nativeType, annotationName, withAnnotations, canoniserProperties);
    }

    @Override
    public ExportLogResultType exportLog(int logId, String logName) throws Exception {
        return logSrv.exportLog(logId);
    }

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
     * @param makePublic is this process public?
     * @param canoniserProperties canoniser properties to use
     * @return ProcessSummary List of processes after the import.
     * @throws java.io.IOException if the streams cause issues
     * @throws Exception ... change to be something more relevant TODO: Fix Exception
     */
    @Override
    public ImportProcessResultType importProcess(String username, Integer folderId, String nativeType, String processName, String versionNumber,
            InputStream xml_process, String domain, String documentation, String created, String lastUpdate, boolean makePublic,
            Set<RequestParameterType<?>> canoniserProperties) throws Exception {

        CanonisedProcess canonisedProcess;
        if (enableCPF) {
            canonisedProcess = canoniserService.canonise(nativeType, xml_process, canoniserProperties);
        } else {
            canonisedProcess = new CanonisedProcess();
            canonisedProcess.setOriginal(xml_process);
        }
        assert canonisedProcess != null;
        ProcessModelVersion pmv = procSrv.importProcess(username, folderId, processName, new Version(versionNumber), nativeType, canonisedProcess,
                domain, "", created, lastUpdate, makePublic);
        ProcessSummaryType process = uiHelper.createProcessSummary(pmv.getProcessBranch().getProcess(), pmv.getProcessBranch(), pmv,
                nativeType, domain, created, lastUpdate, username, makePublic);

        ImportProcessResultType importResult = new ImportProcessResultType();
        if (canonisedProcess.getMessages() != null) {
            importResult.setMessage(PluginHelper.convertFromPluginMessages(canonisedProcess.getMessages()));
        } else {
            importResult.setMessage(PluginHelper.convertFromPluginMessages(Collections.emptyList()));
        }
        importResult.setProcessSummary(process);

        return importResult;
    }

    /**
     * Get list of all currently installed Plugins.
     * @param typeFilter so filter by type of Plugin, shows all if NULL
     * @return PluginInfo for Plugin that is found
     * @throws Exception if communication failed
     */
    @Override
    public Collection<PluginInfo> readInstalledPlugins(String typeFilter) throws Exception {
        Set<PluginInfo> results = new HashSet<>();
        Set<Plugin> plugins;
        if (typeFilter != null) {
            plugins = pluginService.listByType(typeFilter);
        } else {
            plugins = pluginService.listAll();
        }
        for (Plugin p : plugins) {
            results.add(PluginHelper.convertPluginInfo(p));
        }

        return results;
    }

    /**
     * Get information about the Plugin with given name and version.
     * @param name of Plugin
     * @param version of Plugin
     * @return PluginInfo for Plugin that is found
     * @throws Exception if Plugin is not found
     */
    @Override
    public PluginInfoResult readPluginInfo(String name, String version) throws Exception {
        Plugin plugin = pluginService.findByNameAndVersion(name, version);
        PluginInfo pluginInfo = PluginHelper.convertPluginInfo(plugin);
        PluginInfoResult infoResult = new PluginInfoResult();
        infoResult.setPluginInfo(pluginInfo);
        if (plugin instanceof ParameterAwarePlugin) {
            ParameterAwarePlugin propertyAwarePlugin = (ParameterAwarePlugin) plugin;
            infoResult.setMandatoryParameters(PluginHelper.convertFromPluginParameters(propertyAwarePlugin.getMandatoryParameters()));
            infoResult.setOptionalParameters(PluginHelper.convertFromPluginParameters(propertyAwarePlugin.getOptionalParameters()));
        }

        return infoResult;
    }

    /**
     * Get information about all installed Canoniser's for the specified native type.
     * @param nativeType of the process
     * @return Set of PluginInfo
     * @throws Exception TODO: Fix Exception
     */
    @Override
    public Set<PluginInfo> readCanoniserInfo(String nativeType) throws Exception {
        Set<PluginInfo> results = new HashSet<>();
        Set<Canoniser> cList = canoniserService.listByNativeType(nativeType);
        for (Canoniser c : cList) {
            results.add(PluginHelper.convertPluginInfo(c));
        }

        return results;
    }

    /**
     * Read some meta data form the native process XML, optionally using a special Canoniser
     * @param nativeType of the process
     * @param canoniserName use a special Canoniser (optional, may be NULL)
     * @param canoniserVersion use a special Canoniser (optional, may be NULL)
     * @param nativeProcess the process as an XML Stream.
     * @return Meta data of Process
     * @throws Exception in case of any error
     */
    @Override
    public NativeMetaData readNativeMetaData(String nativeType, String canoniserName, String canoniserVersion, InputStream nativeProcess) throws Exception {
        Canoniser c;
        if (canoniserName != null && canoniserVersion != null) {
            c = canoniserService.findByNativeTypeAndNameAndVersion(nativeType, canoniserName, canoniserName);
        } else {
            c = canoniserService.findByNativeType(nativeType);
        }

        CanoniserMetadataResult metaData = c.readMetaData(nativeProcess, new PluginRequestImpl());
        if (metaData != null) {
            return CanoniserHelper.convertFromCanoniserMetaData(metaData);
        } else {
            throw new Exception("Couldn't read meta data!");
        }
    }

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
    @Override
    public DataHandler readInitialNativeFormat(String nativeType, String canoniserName, String canoniserVersion, String owner, String processName,
            String versionName, String creationDate) throws Exception {

        Canoniser c;
        if (canoniserName != null && canoniserVersion != null) {
            c = canoniserService.findByNativeTypeAndNameAndVersion(nativeType, canoniserName, canoniserName);
        } else {
            c = canoniserService.findByNativeType(nativeType);
        }

        ByteArrayOutputStream nativeXml = new ByteArrayOutputStream();

        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH-mm-ss");
        Date processCreated = (creationDate == null) ? null : dateFormat.parse(creationDate);

        c.createInitialNativeFormat(nativeXml, processName, versionName, owner, processCreated, new PluginRequestImpl());
        InputStream nativeXmlInputStream = new ByteArrayInputStream(nativeXml.toByteArray());

        return new DataHandler(new ByteArrayDataSource(nativeXmlInputStream, "text/xml"));
    }

    /**
     * Get information about all installed Deployment Plugins for the specified native type.
     * @param nativeType of the process
     * @return Set of PluginInfo about installed Deployment PLugins supporting the native type
     * @throws Exception in case of any error
     */
    @Override
    public Set<PluginInfo> readDeploymentPluginInfo(String nativeType) throws Exception {
        //return deploymentService.listDeploymentPlugin(nativeType)
        //                        .stream()
        //                        .map(PluginHelper::convertPluginInfo)
        //                        .collect(Collectors.toSet());

        Set<PluginInfo> pluginInfos = new HashSet<>();
        for (Plugin plugin: deploymentService.listDeploymentPlugin(nativeType)) {
            pluginInfos.add(PluginHelper.convertPluginInfo(plugin));
        }

        return pluginInfos;
    }

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
    @Override
    public PluginMessages deployProcess(String branchName, String processName, String versionName, String nativeType, String pluginName,
            String pluginVersion, Set<RequestParameterType<?>> deploymentProperties) throws Exception {

        throw new Error("Not implemented");
    }

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
     * @param preVersion the process current version.
     * @param native_is the actual input stream of the model.
     * @throws java.io.IOException if the streams cause issues
     * @throws Exception ... change to be something more relevant TODO: Fix Exception
     */
    @Override
    public void updateProcess(Integer sessionCode, String username, String nativeType, Integer processId, String domain, String processName,
            String originalBranchName, String newBranchName, String versionNumber, String originalVersionNumber,
            String preVersion, InputStream native_is) throws Exception {

        NativeType natType = frmSrv.findNativeType(nativeType);

        CanonisedProcess canonisedProcess;
        if (enableCPF) {
            Set<RequestParameterType<?>> canoniserProperties = new HashSet<>();
            canonisedProcess = canoniserService.canonise(nativeType, native_is, canoniserProperties);
        } else {
            canonisedProcess = new CanonisedProcess();
            canonisedProcess.setOriginal(native_is);
        }
        assert canonisedProcess != null;

        procSrv.updateProcess(processId, processName, originalBranchName, newBranchName,
                new Version(versionNumber), new Version(originalVersionNumber), secSrv.getUserByName(username), Constants.LOCKED, natType, canonisedProcess);

    }

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
     * @param isPublic is the model public.
     * @throws Exception ... change to be something more relevant TODO: Fix Exception
     */
    @Override
    public void editProcessData(Integer processId, String processName, String domain, String username, String preVersion, String newVersion,
            String ranking, boolean isPublic) throws Exception {

        procSrv.updateProcessMetaData(processId, processName, domain, username, new Version(preVersion), new Version(newVersion), ranking, isPublic);
    }

    /**
     * Send the users details to be persisted.
     * @param user the user to send to the WebService
     * @throws Exception ... change to be something more relevant
     * TODO: Fix Exception
     */
    @Override
    public UserType writeUser(UserType user) throws Exception {
        return  UserMapper.convertUserTypes(secSrv.createUser(UserMapper.convertFromUserType(user)));
    }

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
    @Override
    public void writeAnnotation(Integer editSessionCode, String annName, boolean isNew, Integer processId, String version, String nat_type,
                         InputStream native_is) throws Exception {
        throw new Error("Not implemented");
    }

    /**
     * Delete process models / versions from the repository.
     * @param elements the list of process models
     * @param username the user on whose authority the deletion will be performed
     * @throws Exception ... change to be something more relevant
     * TODO: Fix Exception
     */
    @Override
    public void deleteElements(Map<SummaryType, List<VersionSummaryType>> elements, String username) throws Exception {
        for (Map.Entry<SummaryType, List<VersionSummaryType>> entry: elements.entrySet()) {
            if (entry.getKey() instanceof ProcessSummaryType) {
                //procSrv.deleteProcessModel(elements.get(key)
                //                                   .stream()
                //                                   .map(versionSummary -> new ProcessData(entry.getKey().getId(), new Version(versionSummary.getVersionNumber())))
                //                                   .collect(Collectors.toList()),
                //                           secSrv.getUserByName(username));

                List<ProcessData> processDatas = new ArrayList<>();
                for (VersionSummaryType versionSummary: entry.getValue()) {
                    processDatas.add(new ProcessData(entry.getKey().getId(), new Version(versionSummary.getVersionNumber())));
                }
                procSrv.deleteProcessModel(processDatas, secSrv.getUserByName(username));

            } else if (entry.getKey() instanceof LogSummaryType) {
                logSrv.deleteLogs(Collections.singletonList(new Log(((LogSummaryType) entry.getKey()).getId())), secSrv.getUserByName(username));

            } else {
                throw new Exception("Deletion not supported for " + entry.getKey());
            }
        }
    }

    /**
     * Update the search history records for a User.
     * @param currentUser the Current User to save the serches against.
     * @param searchHist the list of searches we need to save.
     * @throws Exception ... change to be something more relevant
     */
    @Override
    public void updateSearchHistories(UserType currentUser, List<SearchHistoriesType> searchHist) throws Exception {
        userSrv.updateUserSearchHistory(UserMapper.convertFromUserType(currentUser),
                                        SearchHistoryMapper.convertFromSearchHistoriesType(searchHist));
    }
}
