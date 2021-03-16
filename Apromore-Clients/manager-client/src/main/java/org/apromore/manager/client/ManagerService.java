/*-
 * #%L
 * This file is part of "Apromore Core".
 * 
 * Copyright (C) 2011 - 2017 Queensland University of Technology.
 * Copyright (C) 2012 Felix Mannhardt.
 * Copyright (C) 2015, 2016 Adriano Augusto.
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
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apromore.dao.model.ProcessModelVersion;
import org.apromore.exception.UserNotFoundException;
import org.apromore.plugin.property.RequestParameterType;
import org.apromore.portal.model.DomainsType;
import org.apromore.portal.model.ExportFormatResultType;
import org.apromore.portal.model.ExportLogResultType;
import org.apromore.portal.model.FolderType;
import org.apromore.portal.model.GroupAccessType;
import org.apromore.portal.model.GroupType;
import org.apromore.portal.model.ImportLogResultType;
import org.apromore.portal.model.ImportProcessResultType;
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
    UserType readUserByUsername(String username);

    /**
     * the User record.
     * @param searchString the users username
     * @return the UserType from the webservice
     */
    List<UserType> searchUsers(String searchString);

    /**
     * Access group records.
     * @param searchString the users username
     * @return the GroupType from the webservice
     */
    List<GroupType> searchGroups(String searchString);

    /**
     * USed as apart of the reset user's password.
     * @param email the users email address
     * @return the UserType from the webservice
     */
    UserType readUserByEmail(String email) throws Exception;

    /**
     * Reset the USers password for them.
     * @param username the users username
     * @param password the new password
     * @return if we succeeded or not
     */
    boolean resetUserPassword(String username, String password);

    List<FolderType> getWorkspaceFolderTree(String userId);

    List<FolderType> getSubFolders(String userId, int folderId);

    List<FolderType> getBreadcrumbs(String userId, int folderId);

    List<GroupAccessType> getFolderGroups(int folderId);

    List<GroupAccessType> getProcessGroups(int processId);

    List<GroupAccessType> getLogGroups(int logId);

    SummariesType getProcessSummaries(String userId, int folderId, int pageIndex, int pageSize);

    SummariesType getLogSummaries(String userId, int folderId, int pageIndex, int pageSize);

    ImportLogResultType importLog(String username, Integer folderId, String logName, InputStream log, String extension, String domain, String created, boolean makePublic) throws Exception;

    void editLogData(Integer logId, String logName, String username, boolean isPublic) throws Exception;

    void createFolder(String userId, String folderName, int parentFolderId);

    boolean isGEDReadyFolder(int folderId);

    void updateFolder(int folderId, String folderName, String username);

    void deleteFolder(int folderId, String username) throws Exception;

    String saveFolderPermissions(int folderId, String userId, boolean hasRead, boolean hasWrite, boolean hasOwnership);

    String saveProcessPermissions(int processId, String userId, boolean hasRead, boolean hasWrite, boolean hasOwnership);

    String saveLogPermissions(int logId, String userId, boolean hasRead, boolean hasWrite, boolean hasOwnership);

    String removeFolderPermissions(int folderId, String userId);

    String removeProcessPermissions(int processId, String userId);

    String removeLogPermissions(int logId, String userId, String username) throws UserNotFoundException;

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
    SummariesType searchForSimilarProcesses(int processId, String versionName, String method, Boolean latestVersions, int folderId,
            String userId, double modelThreshold, double labelThreshold, double contextThreshold, double skipnWeight, double subnWeight,
            double skipeWeight);

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
    ProcessSummaryType mergeProcesses(Map<ProcessSummaryType, List<VersionSummaryType>> selectedProcessVersions, String mergedProcessName,
            String mergedVersionName, String mergedDomain, String mergedUsername, Integer folderId, boolean makePublic, String method,
            boolean removeEntanglements, double mergeThreshold, double labelThreshold, double contextThreshold, double skipnWeight,
            double subnWeight, double skipeWeight);

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
    ExportFormatResultType exportFormat(int processId, String processName, String branch, String versionNumber, String nativeType,
            String owner)
            throws Exception;

    ExportLogResultType exportLog(int logId, String logName) throws Exception;

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
    ImportProcessResultType importProcess(String username, Integer folderId, String nativeType, String processName, String versionNumber,
            InputStream nativeStream, String domain, String documentation, String created, String lastUpdate, boolean makePublic) throws Exception;
    
    
    ProcessSummaryType createNewEmptyProcess(String username);
        

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
    PluginMessages deployProcess(String branchName, String processName, String versionName, String nativeType, String pluginName,
            String pluginVersion, Set<RequestParameterType<?>> deploymentProperties) throws Exception;

    /**
     * Create a process model version in the repository.
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
     * @param nativeStream the actual input stream of the model.
     * @throws java.io.IOException if the streams cause issues
     * @throws Exception ... change to be something more relevant TODO: Fix Exception
     */
    ProcessModelVersion createProcessModelVersion(Integer sessionCode, String username, String nativeType, Integer processId, String branchName, String versionNumber, String originalVersionNumber,
            String preVersion, InputStream nativeStream) throws Exception;
    
    
    /**
     * Update a process model version in the repository.
     * @param processId the process Identifier.
     * @param nativeType the process Native type.
     * @param processName the process name.
     * @param branchName the BranchName.
     * @param versionNumber the versionNumber.
     * @param username the Username.
     * @param lockStatus the lock status of this version
     * @param nativeType the native type
     * @param nativeStream the actual input stream of the model.
     * @throws Exception 
     */
    ProcessModelVersion updateProcessModelVersion(final Integer processId, final String branchName, final String versionNumber, 
            final String username, final String lockStatus,
            final String nativeType, final InputStream nativeStream) throws Exception;

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
    void editProcessData(Integer processId, String processName, String domain, String username, String preVersion, String newVersion,
            String ranking, boolean isPublic) throws Exception;

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
    void writeAnnotation(Integer editSessionCode, String annName, boolean isNew, Integer processId, String version, String nat_type,
                         InputStream native_is) throws Exception;

    /**
     * Delete process models / versions from the repository.
     * @param elements the list of process models
     * @param username the user on whose authority the deletion will be performed
     * @throws Exception ... change to be something more relevant
     * TODO: Fix Exception
     */
    void deleteElements(Map<SummaryType, List<VersionSummaryType>> elements, String username) throws Exception;
}
