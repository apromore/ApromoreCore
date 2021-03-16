/*-
 * #%L
 * This file is part of "Apromore Core".
 * %%
 * Copyright (C) 2018 - 2021 Apromore Pty Ltd.
 * %%
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Lesser Public License for more details.
 * 
 * You should have received a copy of the GNU General Lesser Public
 * License along with this program.  If not, see
 * <http://www.gnu.org/licenses/lgpl-3.0.html>.
 * #L%
 */

package org.apromore.manager.client;

import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.inject.Inject;

import org.apromore.common.Constants;
//import org.apromore.dao.model.Cluster;
import org.apromore.dao.model.Group;
import org.apromore.dao.model.Log;
import org.apromore.dao.model.NativeType;
import org.apromore.dao.model.ProcessModelVersion;
import org.apromore.dao.model.User;
import org.apromore.exception.NotAuthorizedException;
import org.apromore.exception.UserNotFoundException;
import org.apromore.mapper.DomainMapper;
import org.apromore.mapper.GroupMapper;
import org.apromore.mapper.NativeTypeMapper;
import org.apromore.mapper.SearchHistoryMapper;
import org.apromore.mapper.UserMapper;
import org.apromore.mapper.WorkspaceMapper;
import org.apromore.plugin.ParameterAwarePlugin;
import org.apromore.plugin.Plugin;
import org.apromore.plugin.property.RequestParameterType;
import org.apromore.portal.helper.Version;
import org.apromore.portal.model.DomainsType;
import org.apromore.portal.model.ExportFormatResultType;
import org.apromore.portal.model.ExportLogResultType;
import org.apromore.portal.model.FolderType;
import org.apromore.portal.model.GroupAccessType;
import org.apromore.portal.model.GroupType;
import org.apromore.portal.model.ImportLogResultType;
import org.apromore.portal.model.ImportProcessResultType;
import org.apromore.portal.model.LogSummaryType;
import org.apromore.portal.model.NativeTypesType;
import org.apromore.portal.model.PluginInfo;
import org.apromore.portal.model.PluginInfoResult;
import org.apromore.portal.model.PluginMessages;
import org.apromore.portal.model.ProcessSummaryType;
import org.apromore.portal.model.SearchHistoriesType;
import org.apromore.portal.model.SummariesType;
import org.apromore.portal.model.SummaryType;
import org.apromore.portal.model.UserType;
import org.apromore.portal.model.UsernamesType;
import org.apromore.portal.model.VersionSummaryType;
import org.apromore.service.DomainService;
import org.apromore.service.EventLogService;
import org.apromore.service.FormatService;
import org.apromore.service.PluginService;
import org.apromore.service.ProcessService;
import org.apromore.service.SecurityService;
import org.apromore.service.UserService;
import org.apromore.service.WorkspaceService;
import org.apromore.service.helper.UserInterfaceHelper;
import org.apromore.service.model.ProcessData;
import org.apromore.service.search.SearchExpressionBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

/**
 * Implements {@link ManagerService} by delegating to OSGi services.
 */
@Service("managerClient")
public class ManagerServiceImpl implements ManagerService {

    @Inject private PluginService pluginService;
    @Inject private ProcessService procSrv;
    @Inject private EventLogService logSrv;
    @Inject private FormatService frmSrv;
    @Inject private DomainService domSrv;
    @Inject private UserService userSrv;
    @Inject private SecurityService secSrv;
    @Inject private WorkspaceService workspaceSrv;
    @Inject private UserInterfaceHelper uiHelper;

    private boolean isGEDMatrixReady = true;
    
    private static final Logger LOGGER = LoggerFactory.getLogger(ManagerServiceImpl.class);


    // Implementation of ManagerService

    /**
     * the User record.
     * @param username the users login name
     * @return the UserType from the webservice
     */
    @Override
    public UserType readUserByUsername(String username) {
        return UserMapper.convertUserTypes(secSrv.getUserByName(username), secSrv);
    }

    /**
     * the User record.
     * @param searchString the users username
     * @return the UserType from the webservice
     */
    @Override
    public List<UserType> searchUsers(String searchString) {
        List<UserType> users = new ArrayList<>();
        for (User user: secSrv.searchUsers(searchString)) {
            users.add(UserMapper.convertUserTypes(user, secSrv));
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
        return UserMapper.convertUserTypes(secSrv.getUserByEmail(email), secSrv);
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
    public void deleteFolder(int folderId, String username) throws Exception {
        workspaceSrv.deleteFolder(folderId, secSrv.getUserByName(username));
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
    public String removeLogPermissions(int logId, String userId, String username) throws UserNotFoundException {
        return workspaceSrv.removeLogPermissions(logId, userId, username);
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
     * @param owner the owner of the model
     * @return the request process model as a Stream
     * @throws Exception ... change to be something more relevant TODO: Fix Exception
     */
    @Override
    public ExportFormatResultType exportFormat(int processId, String processName, String branch, String versionNumber, String nativeType, String owner)
            throws Exception {
        return procSrv.exportProcess(processName, processId, branch, new Version(versionNumber), nativeType);
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
     * @return ProcessSummary List of processes after the import.
     * @throws java.io.IOException if the streams cause issues
     * @throws Exception ... change to be something more relevant TODO: Fix Exception
     */
    @Override
    public ImportProcessResultType importProcess(String username, Integer folderId, String nativeType, String processName, String versionNumber,
            InputStream nativeStream, String domain, String documentation, String created, String lastUpdate, boolean makePublic) throws Exception {

        ProcessModelVersion pmv = procSrv.importProcess(username, folderId, processName, new Version(versionNumber), nativeType, nativeStream,
                domain, "", created, lastUpdate, makePublic);
        ProcessSummaryType process = uiHelper.createProcessSummary(pmv.getProcessBranch().getProcess(), pmv.getProcessBranch(), pmv,
                nativeType, domain, created, lastUpdate, username, makePublic);

        ImportProcessResultType importResult = new ImportProcessResultType();
        importResult.setMessage(PluginHelper.convertFromPluginMessages(Collections.emptyList()));
        importResult.setProcessSummary(process);

        return importResult;
    }
    
    @Override
    public ProcessSummaryType createNewEmptyProcess(String username) {
        ProcessSummaryType proType = new ProcessSummaryType();
        VersionSummaryType verType = new VersionSummaryType();

        proType.setId(0);
        proType.setName("Untitled");
        proType.setDomain("");
        proType.setRanking("");
        proType.setLastVersion("1.0");
        proType.setOriginalNativeType("BPMN 2.0");
        proType.setOwner(username);
        proType.setMakePublic(false);

        DateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
        String now = dateFormat.format(new Date());
        
        verType.setName(Constants.TRUNK_NAME);
        verType.setCreationDate(now);
        verType.setLastUpdate(now);
        verType.setVersionNumber("1.0");
        verType.setRanking("");
        verType.setEmpty(false);

        proType.getVersionSummaries().clear();
        proType.getVersionSummaries().add(verType);
        
        return proType;

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
     * Update a process in the repository.
     * @param sessionCode The Session Code.
     * @param username the Username.
     * @param nativeType the process Native type.
     * @param processId the process Identifier.
     * @param processName the process name.
     * @param branchName the BranchName.
     * @param versionNumber the versionNumber.
     * @param originalVersionNumber the original version number of the model.
     * @param preVersion the process current version.
     * @param nativeStream the actual input stream of the model.
     * @throws Exception
     */
    @Override
    public ProcessModelVersion createProcessModelVersion(Integer sessionCode, String username, String nativeType, 
            Integer processId, String branchName, String versionNumber, String originalVersionNumber,
            String preVersion, InputStream nativeStream) throws Exception {

        NativeType natType = frmSrv.findNativeType(nativeType);
        return procSrv.createProcessModelVersion(processId, branchName, new Version(versionNumber), new Version(originalVersionNumber), secSrv.getUserByName(username), Constants.LOCKED, natType, nativeStream);

    }
    
    @Override
    public ProcessModelVersion updateProcessModelVersion(final Integer processId, final String branchName, final String versionNumber, 
            final String username, final String lockStatus,
            final String nativeType, final InputStream nativeStream) throws Exception {
        
        NativeType natType = frmSrv.findNativeType(nativeType);
        return procSrv.updateProcessModelVersion(processId, branchName, new Version(versionNumber), secSrv.getUserByName(username), lockStatus, natType, nativeStream);
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
        return UserMapper.convertUserTypes(secSrv.createUser(UserMapper.convertFromUserType(user, secSrv)), secSrv);
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
}
