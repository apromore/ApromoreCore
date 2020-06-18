/*-
 * #%L
 * This file is part of "Apromore Core".
 * 
 * Copyright (C) 2011 - 2017 Queensland University of Technology.
 * Copyright (C) 2012 Felix Mannhardt.
 * Copyright (C) 2015, 2016 Adriano Augusto.
 * %%
 * Copyright (C) 2018 - 2020 Apromore Pty Ltd.
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

import static org.springframework.ws.soap.SoapVersion.SOAP_11;

import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.activation.DataHandler;
import javax.mail.util.ByteArrayDataSource;
import javax.xml.bind.JAXBElement;
import javax.xml.soap.MessageFactory;
import javax.xml.soap.SOAPException;

import org.apromore.helper.PluginHelper;
import org.apromore.manager.client.helper.DeleteProcessVersionHelper;
import org.apromore.manager.client.helper.MergeProcessesHelper;
import org.apromore.manager.client.helper.SearchForSimilarProcessesHelper;
import org.apromore.manager.client.util.StreamUtil;
import org.apromore.model.*;
import org.apromore.plugin.property.RequestParameterType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.oxm.jaxb.Jaxb2Marshaller;
import org.springframework.ws.client.core.WebServiceTemplate;
import org.springframework.ws.soap.saaj.SaajSoapMessageFactory;
import org.springframework.ws.transport.http.HttpComponentsMessageSender;

/**
 * Performance Test for the Apromore Manager Client.
 */
public class ManagerServiceClient implements ManagerService {

    private static final Logger LOGGER = LoggerFactory.getLogger(ManagerServiceClient.class);
    private static final ObjectFactory WS_CLIENT_FACTORY = new ObjectFactory();

    private WebServiceTemplate webServiceTemplate;

    /**
     * Default Constructor.
     *
     * @param webServiceTemplate the webservice template
     */
    public ManagerServiceClient(WebServiceTemplate webServiceTemplate) {
        this.webServiceTemplate = webServiceTemplate;
    }

    /**
     * Constructor for use outside the Spring IoC container.
     *
     * @param siteHost
     * @param sitePort
     * @param siteManager
     */
    public ManagerServiceClient(String siteHost, int sitePort, String siteManager) throws SOAPException, URISyntaxException {
        URI uri = new URI("http", null, siteHost, sitePort, siteManager + "/services", null, null);
        this.webServiceTemplate = createWebServiceTemplate(new URI("http", null, siteHost, sitePort, siteManager + "/services", null, null));
    }

    public ManagerServiceClient(URI managerEndpointURI) throws SOAPException {
        this.webServiceTemplate = createWebServiceTemplate(managerEndpointURI);
    }


    /**
     * @param managerEndpointURI the externally reachable URL of the manager endpoint, e.g. "http://localhost:9000/manager/services"
     */
    private static WebServiceTemplate createWebServiceTemplate(URI managerEndpointURI) throws SOAPException {

        SaajSoapMessageFactory messageFactory = new SaajSoapMessageFactory(MessageFactory.newInstance());
        messageFactory.setSoapVersion(SOAP_11);

        HttpComponentsMessageSender httpSender = new HttpComponentsMessageSender();
        httpSender.setConnectionTimeout(1200000);
        httpSender.setReadTimeout(1200000);

        Jaxb2Marshaller serviceMarshaller = new Jaxb2Marshaller();
        serviceMarshaller.setContextPath("org.apromore.model");

        WebServiceTemplate webServiceTemplate = new WebServiceTemplate(messageFactory);
        webServiceTemplate.setMarshaller(serviceMarshaller);
        webServiceTemplate.setUnmarshaller(serviceMarshaller);
        webServiceTemplate.setMessageSender(httpSender);
        webServiceTemplate.setDefaultUri(managerEndpointURI.toString());

        return webServiceTemplate;
    }

    /**
     * @see org.apromore.manager.client.ManagerService#readUserByUsername(String)
     * {@inheritDoc}
     */
    @Override
    @SuppressWarnings("unchecked")
    public UserType readUserByUsername(String username) {
        LOGGER.debug("Preparing ReadUserRequest.....");

        ReadUserByUsernameInputMsgType msg = new ReadUserByUsernameInputMsgType();
        msg.setUsername(username);

        JAXBElement<ReadUserByUsernameInputMsgType> request = WS_CLIENT_FACTORY.createReadUserByUsernameRequest(msg);

        JAXBElement<ReadUserByUsernameOutputMsgType> response = (JAXBElement<ReadUserByUsernameOutputMsgType>) webServiceTemplate.marshalSendAndReceive(request);
        return response.getValue().getUser();
    }

    /**
     * @see org.apromore.manager.client.ManagerService#resetUserPassword(String, String)
     * {@inheritDoc}
     */
    @Override
    @SuppressWarnings("unchecked")
    public boolean resetUserPassword(String username, String password) {
        LOGGER.debug("Preparing Reset the Users Password.....");

        ResetUserPasswordInputMsgType msg = new ResetUserPasswordInputMsgType();
        msg.setUsername(username);
        msg.setPassword(password);

        JAXBElement<ResetUserPasswordInputMsgType> request = WS_CLIENT_FACTORY.createResetUserPasswordRequest(msg);

        JAXBElement<ResetUserPasswordOutputMsgType> response = (JAXBElement<ResetUserPasswordOutputMsgType>) webServiceTemplate.marshalSendAndReceive(request);
        return response.getValue().isSuccess();
    }

    /**
     * @see org.apromore.manager.client.ManagerService#searchUsers(String)
     * {@inheritDoc}
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

    @Override
    @SuppressWarnings("unchecked")
    public List<GroupType> searchGroups(String searchString) {
        LOGGER.debug("Preparing SearchGroupsRequest.....");

        SearchGroupsInputMsgType msg = new SearchGroupsInputMsgType();
        msg.setSearchString(searchString);

        JAXBElement<SearchGroupsInputMsgType> request = WS_CLIENT_FACTORY.createSearchGroupsRequest(msg);

        JAXBElement<SearchGroupsOutputMsgType> response = (JAXBElement<SearchGroupsOutputMsgType>) webServiceTemplate.marshalSendAndReceive(request);
        return response.getValue().getGroups();
    }

    /**
     * @see org.apromore.manager.client.ManagerService#readUserByEmail(String)
     * {@inheritDoc}
     */
    @Override
    @SuppressWarnings("unchecked")
    public UserType readUserByEmail(String email) throws Exception {
        LOGGER.debug("Preparing ResetUserRequest.....");

        ReadUserByEmailInputMsgType msg = new ReadUserByEmailInputMsgType();
        msg.setEmail(email);

        JAXBElement<ReadUserByEmailInputMsgType> request = WS_CLIENT_FACTORY.createReadUserByEmailRequest(msg);

        JAXBElement<ReadUserByEmailOutputMsgType> response = (JAXBElement<ReadUserByEmailOutputMsgType>) webServiceTemplate.marshalSendAndReceive(request);
        if (response.getValue().getResult().getCode() == -1) {
            throw new Exception(response.getValue().getResult().getMessage());
        } else {
            return response.getValue().getUser();
        }
    }

    /**
     * @see org.apromore.manager.client.ManagerService#readAllUsers()
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
    public List<GroupAccessType> getFolderGroups(int folderId) {
        LOGGER.debug("Preparing GetFolderGroupsRequest.....");

        GetFolderGroupsInputMsgType msg = new GetFolderGroupsInputMsgType();
        msg.setFolderId(folderId);

        JAXBElement<GetFolderGroupsInputMsgType> request = WS_CLIENT_FACTORY.createGetFolderGroupsRequest(msg);

        JAXBElement<GetFolderGroupsOutputMsgType> response = (JAXBElement<GetFolderGroupsOutputMsgType>) webServiceTemplate.marshalSendAndReceive(request);
        return response.getValue().getGroups();
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
    public String saveLogPermissions(int logId, String userId, boolean hasRead, boolean hasWrite, boolean hasOwnership) {
        LOGGER.debug("Preparing SaveLogPermissionsRequest.....");

        SaveLogPermissionsInputMsgType msg = new SaveLogPermissionsInputMsgType();
        msg.setLogId(logId);
        msg.setUserId(userId);
        msg.setHasRead(hasRead);
        msg.setHasWrite(hasWrite);
        msg.setHasOwnership(hasOwnership);

        JAXBElement<SaveLogPermissionsInputMsgType> request = WS_CLIENT_FACTORY.createSaveLogPermissionsRequest(msg);

        JAXBElement<SaveLogPermissionsOutputMsgType> response = (JAXBElement<SaveLogPermissionsOutputMsgType>) webServiceTemplate.marshalSendAndReceive(request);
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
    public String removeLogPermissions(int logId, String userId) {
        LOGGER.debug("Preparing RemoveLogPermissionsRequest.....");

        RemoveLogPermissionsInputMsgType msg = new RemoveLogPermissionsInputMsgType();
        msg.setLogId(logId);
        msg.setUserId(userId);

        JAXBElement<RemoveLogPermissionsInputMsgType> request = WS_CLIENT_FACTORY.createRemoveLogPermissionsRequest(msg);

        JAXBElement<RemoveLogPermissionsOutputMsgType> response = (JAXBElement<RemoveLogPermissionsOutputMsgType>) webServiceTemplate.marshalSendAndReceive(request);
        return response.getValue().getMessage();
    }

    @Override
    @SuppressWarnings("unchecked")
    public List<GroupAccessType> getProcessGroups(int processId) {
        LOGGER.debug("Preparing GetProcessGroupsRequest.....");

        GetProcessGroupsInputMsgType msg = new GetProcessGroupsInputMsgType();
        msg.setProcessId(processId);

        JAXBElement<GetProcessGroupsInputMsgType> request = WS_CLIENT_FACTORY.createGetProcessGroupsRequest(msg);

        JAXBElement<GetProcessGroupsOutputMsgType> response = (JAXBElement<GetProcessGroupsOutputMsgType>) webServiceTemplate.marshalSendAndReceive(request);
        return response.getValue().getGroups();
    }

    @Override
    @SuppressWarnings("unchecked")
    public List<GroupAccessType> getLogGroups(int logId) {
        LOGGER.debug("Preparing GetLogGroupsRequest.....");

        GetLogGroupsInputMsgType msg = new GetLogGroupsInputMsgType();
        msg.setLogId(logId);

        JAXBElement<GetLogGroupsInputMsgType> request = WS_CLIENT_FACTORY.createGetLogGroupsRequest(msg);

        JAXBElement<GetLogGroupsOutputMsgType> response = (JAXBElement<GetLogGroupsOutputMsgType>) webServiceTemplate.marshalSendAndReceive(request);
        return response.getValue().getGroups();
    }

    @Override
    @SuppressWarnings("unchecked")
    public SummariesType getProcessSummaries(String userId, int folderId, int pageIndex, int pageSize) {
        LOGGER.debug("Preparing GetProcessRequest.....");

        GetProcessesInputMsgType msg = new GetProcessesInputMsgType();
        msg.setUserId(userId);
        msg.setFolderId(folderId);
        msg.setPageIndex(pageIndex);
        msg.setPageSize(pageSize);

        JAXBElement<GetProcessesInputMsgType> request = WS_CLIENT_FACTORY.createGetProcessesRequest(msg);

        JAXBElement<GetProcessesOutputMsgType> response = (JAXBElement<GetProcessesOutputMsgType>) webServiceTemplate.marshalSendAndReceive(request);
        return response.getValue().getProcesses();
    }

    @Override
    @SuppressWarnings("unchecked")
    public SummariesType getLogSummaries(String userId, int folderId, int pageIndex, int pageSize) {
        LOGGER.debug("Preparing GetLogRequest.....");

        GetLogsInputMsgType msg = new GetLogsInputMsgType();
        msg.setUserId(userId);
        msg.setFolderId(folderId);
        msg.setPageIndex(pageIndex);
        msg.setPageSize(pageSize);

        JAXBElement<GetLogsInputMsgType> request = WS_CLIENT_FACTORY.createGetLogsRequest(msg);

        JAXBElement<GetLogsOutputMsgType> response = (JAXBElement<GetLogsOutputMsgType>) webServiceTemplate.marshalSendAndReceive(request);
        return response.getValue().getLogs();
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
    public boolean isGEDReadyFolder(int folderId) {
        LOGGER.debug("Preparing addProcessToFolderRequest.....");

        IsGEDReadyInputMsgType msg = new IsGEDReadyInputMsgType();
        msg.setFolderId(folderId);

        JAXBElement<IsGEDReadyInputMsgType> request = WS_CLIENT_FACTORY.createIsGEDReadyRequest(msg);

        JAXBElement<IsGEDReadyOutputMsgType> response = (JAXBElement<IsGEDReadyOutputMsgType>) webServiceTemplate.marshalSendAndReceive(request);
        return response.getValue().isGEDReady();
    }

    @Override
    @SuppressWarnings("unchecked")
    public void updateFolder(int folderId, String folderName, String username) {
        LOGGER.debug("Preparing createFolderRequest.....");

        UpdateFolderInputMsgType msg = new UpdateFolderInputMsgType();
        msg.setFolderId(folderId);
        msg.setFolderName(folderName);
        msg.setUsername(username);

        JAXBElement<UpdateFolderInputMsgType> request = WS_CLIENT_FACTORY.createUpdateFolderRequest(msg);

        JAXBElement<UpdateFolderOutputMsgType> response = (JAXBElement<UpdateFolderOutputMsgType>) webServiceTemplate.marshalSendAndReceive(request);
        response.getValue().getResult();
    }

    @Override
    @SuppressWarnings("unchecked")
    public void deleteFolder(int folderId, String username) {
        LOGGER.debug("Preparing deleteFolderRequest.....");

        DeleteFolderInputMsgType msg = new DeleteFolderInputMsgType();
        msg.setFolderId(folderId);
        msg.setUsername(username);

        JAXBElement<DeleteFolderInputMsgType> request = WS_CLIENT_FACTORY.createDeleteFolderRequest(msg);

        JAXBElement<DeleteFolderOutputMsgType> response = (JAXBElement<DeleteFolderOutputMsgType>) webServiceTemplate.marshalSendAndReceive(request);
        response.getValue().getResult();
    }

    /**
     * @see org.apromore.manager.client.ManagerService#readDomains()
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
     * @see org.apromore.manager.client.ManagerService#readNativeTypes()
     * {@inheritDoc}
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


//    /**
//     * @see ManagerService#getFragment(Integer)
//     * {@inheritDoc}
//     */
//    @Override
//    @SuppressWarnings("unchecked")
//    public GetFragmentOutputMsgType getFragment(final Integer fragmentId) {
//        LOGGER.debug("Invoking getFragment method to retreive fragment content...");
//
//        GetFragmentInputMsgType msg = new GetFragmentInputMsgType();
//        msg.setFragmentId(fragmentId);
//
//        JAXBElement<GetFragmentInputMsgType> request = WS_CLIENT_FACTORY.createGetFragmentRequest(msg);
//
//        JAXBElement<GetFragmentOutputMsgType> response = (JAXBElement<GetFragmentOutputMsgType>) webServiceTemplate.marshalSendAndReceive(request);
//        return response.getValue();
//    }

    /**
     * @see ManagerService#readProcessSummaries(Integer, String)
     * {@inheritDoc}
     */
    @Override
    @SuppressWarnings("unchecked")
    public SummariesType readProcessSummaries(final Integer folderId, final String userId, final String searchCriteria) {
        LOGGER.debug("Preparing ReadProcessSummariesRequest.....");

        ReadProcessSummariesInputMsgType msg = new ReadProcessSummariesInputMsgType();
        msg.setFolderId(folderId);
        msg.setUserId(userId);
        msg.setSearchExpression(searchCriteria);

        JAXBElement<ReadProcessSummariesInputMsgType> request = WS_CLIENT_FACTORY.createReadProcessSummariesRequest(msg);

        JAXBElement<ReadProcessSummariesOutputMsgType> response = (JAXBElement<ReadProcessSummariesOutputMsgType>) webServiceTemplate.marshalSendAndReceive(request);
        return response.getValue().getProcessSummaries();
    }

    /**
     * @see ManagerService#searchForSimilarProcesses(int, String, String, Boolean, int, String, double, double, double, double, double, double)
     * {@inheritDoc}
     */
    @Override
    @SuppressWarnings("unchecked")
    public SummariesType searchForSimilarProcesses(final int processId, final String versionName, final String method,
            final Boolean latestVersions, final int folderId, final String userId, final double modelThreshold, final double labelThreshold,
            final double contextThreshold, final double skipnWeight, final double subnWeight, final double skipeWeight) {
        LOGGER.debug("Preparing SearchForSimilarProcessesRequest.....");

        SearchForSimilarProcessesInputMsgType msg = new SearchForSimilarProcessesInputMsgType();
        msg.setAlgorithm(method);
        msg.setProcessId(processId);
        msg.setVersionName(versionName);
        msg.setLatestVersions(latestVersions);
        msg.setFolderId(folderId);
        msg.setUserId(userId);
        msg.setParameters(SearchForSimilarProcessesHelper.setParams(method, modelThreshold, labelThreshold, contextThreshold, skipnWeight,
                skipeWeight, subnWeight));

        JAXBElement<SearchForSimilarProcessesInputMsgType> request = WS_CLIENT_FACTORY.createSearchForSimilarProcessesRequest(msg);

        JAXBElement<SearchForSimilarProcessesOutputMsgType> response = (JAXBElement<SearchForSimilarProcessesOutputMsgType>) webServiceTemplate.marshalSendAndReceive(request);
        return response.getValue().getProcessSummaries();
    }

    /**
     * @see ManagerService#mergeProcesses(java.util.Map, String, String, String, String, Integer, boolean, String, boolean, double, double, double, double, double, double)
     * {@inheritDoc}
     */
    @Override
    @SuppressWarnings("unchecked")
    public ProcessSummaryType mergeProcesses(final Map<ProcessSummaryType, List<VersionSummaryType>> selectedProcessVersions,
                                             final String mergedProcessName, final String mergedVersionName, final String mergedDomain, final String mergedUsername,
                                             final Integer folderId, final boolean makePublic, final String method, final boolean removeEntanglements,
                                             final double mergeThreshold, final double labelThreshold, final double contextThreshold, final double skipnWeight,
                                             final double subnWeight, final double skipeWeight) {
        LOGGER.debug("Preparing MergeProcessesRequest.....");

        MergeProcessesInputMsgType msg = new MergeProcessesInputMsgType();
        msg.setAlgorithm(method);
        msg.setProcessName(mergedProcessName);
        msg.setVersionName(mergedVersionName);
        msg.setDomain(mergedDomain);
        msg.setUsername(mergedUsername);
        msg.setFolderId(folderId);
        msg.setMakePublic(makePublic);
        msg.setProcessVersionIds(MergeProcessesHelper.setProcessModels(selectedProcessVersions));
        msg.setParameters(MergeProcessesHelper.setParams(method, removeEntanglements, mergeThreshold, labelThreshold, contextThreshold,
                skipnWeight, skipeWeight, subnWeight));

        JAXBElement<MergeProcessesInputMsgType> request = WS_CLIENT_FACTORY.createMergeProcessesRequest(msg);

        JAXBElement<MergeProcessesOutputMsgType> response = (JAXBElement<MergeProcessesOutputMsgType>) webServiceTemplate.marshalSendAndReceive(request);
        return response.getValue().getProcessSummary();
    }

    /**
     * @see ManagerService#exportFormat(int, String, String, String, String, String, Boolean, String, java.util.Set)
     * {@inheritDoc}
     */
    @Override
    @SuppressWarnings("unchecked")
    public ExportFormatResultType exportFormat(final int processId, final String processName, final String branchName, final String versionNumber,
            final String nativeType, final String owner) throws Exception {
        LOGGER.debug("Preparing ExportFormatRequest.....");

        ExportFormatInputMsgType msg = new ExportFormatInputMsgType();
        msg.setProcessId(processId);
        msg.setBranchName(branchName);
        msg.setFormat(nativeType);
        msg.setProcessName(processName);
        msg.setVersionNumber(versionNumber);
        msg.setOwner(owner);

        JAXBElement<ExportFormatInputMsgType> request = WS_CLIENT_FACTORY.createExportFormatRequest(msg);

        JAXBElement<ExportFormatOutputMsgType> response = (JAXBElement<ExportFormatOutputMsgType>) webServiceTemplate.marshalSendAndReceive(request);
        if (response.getValue().getResult().getCode() == -1) {
            throw new Exception(response.getValue().getResult().getMessage());
        } else {
            LOGGER.info(StreamUtil.convertStreamToString(response.getValue().getExportResult().getNative()));
            return response.getValue().getExportResult();
        }
    }

    @Override
    @SuppressWarnings("unchecked")
    public ExportLogResultType exportLog(final int logId, final String logName) throws Exception {
        LOGGER.debug("Preparing ExportLogRequest.....");

        ExportLogInputMsgType msg = new ExportLogInputMsgType();
        msg.setLogId(logId);
        msg.setLogName(logName);

        JAXBElement<ExportLogInputMsgType> request = WS_CLIENT_FACTORY.createExportLogRequest(msg);

        JAXBElement<ExportLogOutputMsgType> response = (JAXBElement<ExportLogOutputMsgType>) webServiceTemplate.marshalSendAndReceive(request);
        if (response.getValue().getResult().getCode() == -1) {
            throw new Exception(response.getValue().getResult().getMessage());
        } else {
            LOGGER.info(StreamUtil.convertStreamToString(response.getValue().getExportResult().getNative()));
            return response.getValue().getExportResult();
        }
    }

    @Override
    public ImportLogResultType importLog(String username, Integer folderId, String logName, InputStream log, String extension, String domain, String created, boolean makePublic) throws Exception {
        LOGGER.debug("Preparing ImportLogRequest.....");

        EditSessionType editSession = new EditSessionType();
        editSession.setUsername(username);
        editSession.setFolderId(folderId);
//        editSession.setNativeType(nativeType);
        editSession.setProcessName(logName);
//        editSession.setCurrentVersionNumber(versionNumber);
        editSession.setDomain(domain);
        editSession.setCreationDate(created);
//        editSession.setLastUpdate(lastUpdate);
        editSession.setPublicModel(makePublic);

        ImportLogInputMsgType msg = new ImportLogInputMsgType();
        msg.setExtension(extension);
        msg.setLog(new DataHandler(new ByteArrayDataSource(log, "text/xml")));
        msg.setEditSession(editSession);

        JAXBElement<ImportLogInputMsgType> request = WS_CLIENT_FACTORY.createImportLogRequest(msg);
        JAXBElement<ImportLogOutputMsgType> response = (JAXBElement<ImportLogOutputMsgType>)
                webServiceTemplate.marshalSendAndReceive(request);
        if (response.getValue().getResult().getCode() == -1) {
            throw new Exception(response.getValue().getResult().getMessage());
        } else {
            return response.getValue().getImportLogResult();
        }
    }
    
    @Override
    public void editLogData(Integer logId, String logName, String username, boolean isPublic) throws Exception {
        LOGGER.debug("Preparing EditLogDataRequest.....");

        EditLogDataInputMsgType msg = new EditLogDataInputMsgType();
        msg.setId(logId);
        msg.setLogName(logName);
        msg.setMakePublic(isPublic);

        JAXBElement<EditLogDataInputMsgType> request = WS_CLIENT_FACTORY.createEditLogDataRequest(msg);

        JAXBElement<EditLogDataOutputMsgType> response = (JAXBElement<EditLogDataOutputMsgType>) webServiceTemplate.marshalSendAndReceive(request);
        if (response.getValue().getResult().getCode() == -1) {
            throw new Exception(response.getValue().getResult().getMessage());
        }

    }


    /**
     * @see ManagerService#importProcess(String, java.lang.Integer, String, String, String, java.io.InputStream, String, String, String, String, boolean, java.util.Set)
     * {@inheritDoc}
     */
    @Override
    @SuppressWarnings("unchecked")
    public ImportProcessResultType importProcess(final String username, final Integer folderId, final String nativeType, final String processName,
            final String versionNumber, final InputStream xmlProcess, final String domain, final String documentation, final String created,
            final String lastUpdate, final boolean makePublic) throws Exception {
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
        editSession.setPublicModel(makePublic);

        ImportProcessInputMsgType msg = new ImportProcessInputMsgType();
        //msg.setCanoniserParameters(PluginHelper.convertFromPluginParameters(canoniserProperties));
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
    
    @Override
    public ProcessSummaryType createEmptyProcess() {
       return null; 
    }

    /**
     * @see ManagerService#updateProcess(Integer, String, String, Integer, String, String, String, String, String, String, String, java.io.InputStream)
     * {@inheritDoc}
     */
    @Override
    @SuppressWarnings("unchecked")
    public void updateProcessModelVersion(final Integer sessionCode, final String username, final String nativeType, final Integer processId,
            final String branchName, final String versionNumber, final String originalVersionNumber, final String preVersion, final InputStream native_is)
            throws Exception {
        LOGGER.debug("Preparing UpdateProcessRequest.....");

        EditSessionType editSession = new EditSessionType();
        editSession.setUsername(username);
        editSession.setNativeType(nativeType);
        editSession.setProcessName("");
        editSession.setOriginalBranchName(branchName);
        editSession.setNewBranchName(branchName);
        editSession.setOriginalVersionNumber(originalVersionNumber);
        editSession.setCurrentVersionNumber(versionNumber);
        editSession.setDomain("DOMAIN");
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
     * @see ManagerService#editProcessData(Integer, String, String, String, String, String, String, boolean)
     * {@inheritDoc}
     */
    @Override
    @SuppressWarnings("unchecked")
    public void editProcessData(final Integer processId, final String processName, final String domain, final String username,
            final String preVersion, final String newVersion, final String ranking, final boolean isPublic) throws Exception {
        LOGGER.debug("Preparing EditProcessDataRequest.....");

        EditProcessDataInputMsgType msg = new EditProcessDataInputMsgType();
        msg.setDomain(domain);
        msg.setProcessName(processName);
        msg.setOwner(username);
        msg.setId(processId);
        msg.setNewVersion(newVersion);
        msg.setPreVersion(preVersion);
        msg.setRanking(ranking);
        msg.setMakePublic(isPublic);

        JAXBElement<EditProcessDataInputMsgType> request = WS_CLIENT_FACTORY.createEditProcessDataRequest(msg);

        JAXBElement<EditProcessDataOutputMsgType> response = (JAXBElement<EditProcessDataOutputMsgType>) webServiceTemplate.marshalSendAndReceive(request);
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
     * {@inheritDoc}
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
     * @see ManagerService#deleteElements(java.util.Map)
     * {@inheritDoc}
     */
    @Override
    @SuppressWarnings("unchecked")
    public void deleteElements(final Map<SummaryType, List<VersionSummaryType>> elements, String username) throws Exception {
        LOGGER.debug("Preparing DeleteProcessVersions.....");

        for(SummaryType key: elements.keySet()) {
            if (key instanceof ProcessSummaryType) {
                DeleteProcessVersionsInputMsgType msg = new DeleteProcessVersionsInputMsgType();
                msg.setUsername(username);
                msg.getProcessVersionIdentifier().addAll(DeleteProcessVersionHelper.setElements(elements));

                JAXBElement<DeleteProcessVersionsInputMsgType> request = WS_CLIENT_FACTORY.createDeleteProcessVersionsRequest(msg);

                JAXBElement<DeleteProcessVersionsOutputMsgType> response = (JAXBElement<DeleteProcessVersionsOutputMsgType>) webServiceTemplate.marshalSendAndReceive(request);
                if (response.getValue().getResult().getCode() == -1) {
                    throw new Exception(response.getValue().getResult().getMessage());
                }
            } else if (key instanceof LogSummaryType) {
                DeleteLogInputMsgType msg = new DeleteLogInputMsgType();
                msg.setUsername(username);
                msg.getLogSummaryType().add((LogSummaryType) key);

                JAXBElement<DeleteLogInputMsgType> request = WS_CLIENT_FACTORY.createDeleteLogRequest(msg);

                JAXBElement<DeleteLogOutputMsgType> response = (JAXBElement<DeleteLogOutputMsgType>) webServiceTemplate.marshalSendAndReceive(request);
                if (response.getValue().getResult().getCode() == -1) {
                    throw new Exception(response.getValue().getResult().getMessage());
                }
            } else {
                throw new Exception("Deletion not supported for " + key);
            }
        }
    }

    /** (non-Javadoc)
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


    /** (non-Javadoc)
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

    /**
     * @see org.apromore.manager.client.ManagerService#deployProcess
     * {@inheritDoc}
     */
    @Override
    public PluginMessages deployProcess(final String branchName, final String processName, final String versionName, final String nativeType,
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

    /**
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
