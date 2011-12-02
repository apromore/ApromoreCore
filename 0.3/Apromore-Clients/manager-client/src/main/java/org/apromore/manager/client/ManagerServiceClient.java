package org.apromore.manager.client;

import org.apromore.manager.client.helper.DeleteProcessVersionHelper;
import org.apromore.manager.client.helper.MergeProcessesHelper;
import org.apromore.manager.client.helper.SearchForSimilarProcessesHelper;
import org.apromore.manager.client.util.StreamUtil;
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
import org.apromore.model.ImportProcessInputMsgType;
import org.apromore.model.ImportProcessOutputMsgType;
import org.apromore.model.MergeProcessesInputMsgType;
import org.apromore.model.MergeProcessesOutputMsgType;
import org.apromore.model.NativeTypesType;
import org.apromore.model.ObjectFactory;
import org.apromore.model.ProcessSummaryType;
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
import org.apromore.model.SearchForSimilarProcessesInputMsgType;
import org.apromore.model.SearchForSimilarProcessesOutputMsgType;
import org.apromore.model.UpdateProcessInputMsgType;
import org.apromore.model.UpdateProcessOutputMsgType;
import org.apromore.model.UserType;
import org.apromore.model.UsernamesType;
import org.apromore.model.ProcessSummariesType;
import org.apromore.model.VersionSummaryType;
import org.apromore.model.WriteAnnotationInputMsgType;
import org.apromore.model.WriteAnnotationOutputMsgType;
import org.apromore.model.WriteEditSessionInputMsgType;
import org.apromore.model.WriteEditSessionOutputMsgType;
import org.apromore.model.WriteUserInputMsgType;
import org.apromore.model.WriteUserOutputMsgType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ws.client.core.WebServiceTemplate;

import javax.activation.DataHandler;
import javax.mail.util.ByteArrayDataSource;
import javax.xml.bind.JAXBElement;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Map;

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
     * @see ManagerService#readUser(String)
     * {@inheritDoc}
     */
    @Override
    public UserType readUser(String username) {
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
    public NativeTypesType readNativeTypes() {
        LOGGER.debug("Preparing ReadNativeTypesRequest.....");

        ReadNativeTypesInputMsgType msg = new ReadNativeTypesInputMsgType();
        msg.setEmpty("");

        JAXBElement<ReadNativeTypesInputMsgType> request = WS_CLIENT_FACTORY.createReadNativeTypesRequest(msg);

        JAXBElement<ReadNativeTypesOutputMsgType> response = (JAXBElement<ReadNativeTypesOutputMsgType>) webServiceTemplate.marshalSendAndReceive(request);
        return response.getValue().getNativeTypes();
    }

    /**
     * @see ManagerService#readEditSession(int)
     * {@inheritDoc}
     */
    @Override
    public EditSessionType readEditSession(int code) {
        LOGGER.debug("Preparing ReadEditSessionRequest.....");

        ReadEditSessionInputMsgType msg = new ReadEditSessionInputMsgType();
        msg.setEditSessionCode(code);

        JAXBElement<ReadEditSessionInputMsgType> request = WS_CLIENT_FACTORY.createReadEditSessionRequest(msg);

        JAXBElement<ReadEditSessionOutputMsgType> response = (JAXBElement<ReadEditSessionOutputMsgType>) webServiceTemplate.marshalSendAndReceive(request);
        return response.getValue().getEditSession();
    }

    /**
     * @see ManagerService#readProcessSummaries(String)
     * {@inheritDoc}
     */
    @Override
    public ProcessSummariesType readProcessSummaries(String searchCriteria) {
        LOGGER.debug("Preparing ReadProcessSummariesRequest.....");

        ReadProcessSummariesInputMsgType msg = new ReadProcessSummariesInputMsgType();
        msg.setSearchExpression(searchCriteria);

        JAXBElement<ReadProcessSummariesInputMsgType> request = WS_CLIENT_FACTORY.createReadProcessSummariesRequest(msg);

        JAXBElement<ReadProcessSummariesOutputMsgType> response = (JAXBElement<ReadProcessSummariesOutputMsgType>) webServiceTemplate.marshalSendAndReceive(request);
        return response.getValue().getProcessSummaries();
    }




    /**
     * @see ManagerService#searchForSimilarProcesses(int, String, String, Boolean, double, double, double, double, double, double)
     * {@inheritDoc}
     */
    @Override
    public ProcessSummariesType searchForSimilarProcesses(int processId, String versionName, String method, Boolean latestVersions,
            double modelThreshold, double labelThreshold, double contextThreshold, double skipnWeight, double subnWeight,
            double skipeWeight) {
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
     * @see ManagerService#mergeProcesses(java.util.Map, String, String, String, String, String, boolean, double, double, double, double, double, double)
     * {@inheritDoc}
     */
    @Override
    public ProcessSummaryType mergeProcesses(Map<ProcessSummaryType, List<VersionSummaryType>> selectedProcessVersions,
            String mergedProcessName, String mergedVersionName, String mergedDomain, String mergedUsername, String method,
            boolean removeEntanglements, double mergeThreshold, double labelThreshold, double contextThreshold, double skipnWeight,
            double subnWeight, double skipeWeight) {
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

        JAXBElement<MergeProcessesOutputMsgType> response = (JAXBElement<MergeProcessesOutputMsgType>) webServiceTemplate.marshalSendAndReceive(request);
        return response.getValue().getProcessSummary();
    }

    /**
     * @see ManagerService#exportFormat(int, String, String, String, String, Boolean, String)
     * {@inheritDoc}
     */
    @Override
    public DataHandler exportFormat(int processId, String processName, String versionName, String nativeType, String annotationName,
            Boolean withAnnotations, String owner) throws IOException, Exception {
        LOGGER.debug("Preparing ExportFormatRequest.....");

        ExportFormatInputMsgType msg = new ExportFormatInputMsgType();
        msg.setProcessId(processId);
        msg.setVersionName(versionName);
        msg.setFormat(nativeType);
        msg.setAnnotationName(annotationName);
        msg.setWithAnnotations(withAnnotations);
        msg.setProcessName(processName);
        msg.setOwner(owner);

        JAXBElement<ExportFormatInputMsgType> request = WS_CLIENT_FACTORY.createExportFormatRequest(msg);

        JAXBElement<ExportFormatOutputMsgType> response = (JAXBElement<ExportFormatOutputMsgType>) webServiceTemplate.marshalSendAndReceive(request);
        if (response.getValue().getResult().getCode() == -1) {
            throw new Exception(response.getValue().getResult().getMessage());
        } else {
            LOGGER.info(StreamUtil.convertStreamToString(response.getValue().getNative()));
            return response.getValue().getNative();
        }
    }

    /**
     * @see ManagerService#importProcess(String, String, String, String, java.io.InputStream, String, String, String, String, Boolean)
     * {@inheritDoc}
     */
    @Override
    public ProcessSummaryType importProcess(String username, String nativeType, String processName, String versionName,
            InputStream xml_process, String domain, String documentation, String created, String lastUpdate,
            Boolean addFakeEvents) throws IOException, Exception {
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
        msg.setAddFakeEvents(addFakeEvents);
        msg.setProcessDescription(new DataHandler(new ByteArrayDataSource(xml_process, "text/xml")));
        msg.setEditSession(editSession);

        JAXBElement<ImportProcessInputMsgType> request = WS_CLIENT_FACTORY.createImportProcessRequest(msg);

        JAXBElement<ImportProcessOutputMsgType> response = (JAXBElement<ImportProcessOutputMsgType>) webServiceTemplate.marshalSendAndReceive(request);
        if (response.getValue().getResult().getCode() == -1) {
            throw new Exception(response.getValue().getResult().getMessage());
        } else {
            return response.getValue().getProcessSummary();
        }
    }

    /**
     * @see ManagerService#updateProcess(int, String, String, int, String, String, String, String, java.io.InputStream)
     * {@inheritDoc}
     */
    @Override
    public void updateProcess(int sessionCode, String username, String nativeType, int processId, String domain, String processName,
            String new_versionName, String preVersion, InputStream native_is) throws IOException, Exception {
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

        JAXBElement<UpdateProcessOutputMsgType> response = (JAXBElement<UpdateProcessOutputMsgType>) webServiceTemplate.marshalSendAndReceive(request);
        if (response.getValue().getResult().getCode() == -1) {
            throw new Exception(response.getValue().getResult().getMessage());
        }
    }




    /**
     * @see ManagerService#editProcessData(Integer, String, String, String, String, String, String)
     * {@inheritDoc}
     */
    @Override
    public void editProcessData(Integer processId, String processName, String domain, String username, String preVersion, String newVersion, String ranking) throws Exception {
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
    public void writeUser(UserType user) throws Exception {
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
    public void writeAnnotation(Integer editSessionCode, String annName, boolean isNew, Integer processId, String version, String nat_type,
            InputStream native_is) throws IOException, Exception {
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
     * @see ManagerService#writeEditSession(org.apromore.model.EditSessionType)
     * {@inheritDoc}
     */
    @Override
    public int writeEditSession(EditSessionType editSession) throws Exception {
        LOGGER.debug("Preparing WriteEditSessionRequest.....");

        WriteEditSessionInputMsgType msg = new WriteEditSessionInputMsgType();
        msg.setEditSession(editSession);

        JAXBElement<WriteEditSessionInputMsgType> request = WS_CLIENT_FACTORY.createWriteEditSessionRequest(msg);

        JAXBElement<WriteEditSessionOutputMsgType> response = (JAXBElement<WriteEditSessionOutputMsgType>) webServiceTemplate.marshalSendAndReceive(request);
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
    public void deleteEditSession(int code) throws Exception {
        LOGGER.debug("Preparing DeleteEditionSessionRequest.....");

        DeleteEditSessionInputMsgType msg = new DeleteEditSessionInputMsgType();
        msg.setEditSessionCode(code);

        JAXBElement<DeleteEditSessionInputMsgType> request = WS_CLIENT_FACTORY.createDeleteEditSessionRequest(msg);

        JAXBElement<DeleteEditSessionOutputMsgType> response = (JAXBElement<DeleteEditSessionOutputMsgType>) webServiceTemplate.marshalSendAndReceive(request);
        if (response.getValue().getResult().getCode() == -1) {
            throw new Exception(response.getValue().getResult().getMessage());
        }
    }

    /**
     * @see ManagerService#deleteProcessVersions(java.util.Map)
     * {@inheritDoc}
     */
    @Override
    public void deleteProcessVersions(Map<ProcessSummaryType, List<VersionSummaryType>> processVersions) throws Exception {
        LOGGER.debug("Preparing DeleteProcessVersions.....");

        DeleteProcessVersionsInputMsgType msg = new DeleteProcessVersionsInputMsgType();
        msg.getProcessVersionIdentifier().addAll(DeleteProcessVersionHelper.setProcessModels(processVersions));

        JAXBElement<DeleteProcessVersionsInputMsgType> request = WS_CLIENT_FACTORY.createDeleteProcessVersionsRequest(msg);

        JAXBElement<DeleteProcessVersionsOutputMsgType> response = (JAXBElement<DeleteProcessVersionsOutputMsgType>) webServiceTemplate.marshalSendAndReceive(request);
        if (response.getValue().getResult().getCode() == -1) {
            throw new Exception(response.getValue().getResult().getMessage());
        }
    }

}
