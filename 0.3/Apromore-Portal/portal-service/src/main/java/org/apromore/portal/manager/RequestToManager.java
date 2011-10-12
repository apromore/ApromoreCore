package org.apromore.portal.manager;

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
import org.apromore.model.VersionSummaryType;
import org.apromore.model.WriteAnnotationInputMsgType;
import org.apromore.model.WriteAnnotationOutputMsgType;
import org.apromore.model.WriteEditSessionInputMsgType;
import org.apromore.model.WriteEditSessionOutputMsgType;
import org.apromore.model.WriteUserInputMsgType;
import org.apromore.model.WriteUserOutputMsgType;
import org.apromore.portal.exception.ExceptionAllUsers;
import org.apromore.portal.exception.ExceptionDeleteEditSession;
import org.apromore.portal.exception.ExceptionDeleteProcess;
import org.apromore.portal.exception.ExceptionDomains;
import org.apromore.portal.exception.ExceptionEditDataProcess;
import org.apromore.portal.exception.ExceptionExport;
import org.apromore.portal.exception.ExceptionFormats;
import org.apromore.portal.exception.ExceptionImport;
import org.apromore.portal.exception.ExceptionProcess;
import org.apromore.portal.exception.ExceptionReadEditSession;
import org.apromore.portal.exception.ExceptionUpdateProcess;
import org.apromore.portal.exception.ExceptionUser;
import org.apromore.portal.exception.ExceptionVersion;
import org.apromore.portal.exception.ExceptionWriteEditSession;

import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.mail.util.ByteArrayDataSource;
import javax.xml.namespace.QName;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

public class RequestToManager {

    private static final QName SERVICE_NAME = new QName("http://www.apromore.org/manager/service_portal", "ManagerService");

    private Manager port;

    public RequestToManager() {
        URL wsdlURL = ManagerService.WSDL_LOCATION;
        ManagerService ss = new ManagerService(wsdlURL, SERVICE_NAME);
        this.port = ss.getManagerSoap11();
    }

    public DomainsType ReadDomains() throws ExceptionDomains {
        // payload empty
        ReadDomainsInputMsgType payload = new ReadDomainsInputMsgType();
        ReadDomainsOutputMsgType res = this.port.readDomains(payload);

        ResultType result = res.getResult();
        if (result.getCode() == -1) {
            throw new ExceptionDomains(result.getMessage());
        } else {
            return res.getDomains();
        }
    }

    public UsernamesType ReadAllUsers() throws ExceptionAllUsers {
        ReadAllUsersInputMsgType payload = new ReadAllUsersInputMsgType();
        ReadAllUsersOutputMsgType res = this.port.readAllUsers(payload);
        ResultType result = res.getResult();
        if (result.getCode() == -1) {
            throw new ExceptionAllUsers(result.getMessage());
        } else {
            return res.getUsernames();
        }

    }

    public NativeTypesType ReadNativeTypes() throws ExceptionFormats {
        // payload empty
        ReadNativeTypesInputMsgType payload = new ReadNativeTypesInputMsgType();
        ReadNativeTypesOutputMsgType res = this.port.readNativeTypes(payload);

        ResultType result = res.getResult();
        if (result.getCode() == -1) {
            throw new ExceptionFormats(result.getMessage());
        } else {
            return res.getNativeTypes();
        }
    }

    public UserType ReadUser(String username) throws ExceptionUser {
        ReadUserInputMsgType payload = new ReadUserInputMsgType();
        payload.setUsername(username);
        ReadUserOutputMsgType res = this.port.readUser(payload);
        ResultType result = res.getResult();
        if (result.getCode() == -1) {
            throw new ExceptionUser(result.getMessage());
        } else {
            return res.getUser();
        }
    }

    public void WriteUser(UserType user) throws ExceptionUser {
        WriteUserInputMsgType payload = new WriteUserInputMsgType();
        payload.setUser(user);
        WriteUserOutputMsgType res = this.port.writeUser(payload);
        ResultType result = res.getResult();
        if (result.getCode() == -1) {
            throw new ExceptionUser(result.getMessage());
        }
    }

    public ProcessSummariesType ReadProcessSummariesType(String searchExpr) throws ExceptionProcess {
        try {
            ReadProcessSummariesInputMsgType payload = new ReadProcessSummariesInputMsgType();
            payload.setSearchExpression(searchExpr);
            ReadProcessSummariesOutputMsgType res = this.port.readProcessSummaries(payload);

            ResultType result = res.getResult();
            if (result.getCode() == -1) {
                throw new ExceptionProcess(result.getMessage());
            } else {
                return res.getProcessSummaries();
            }
        } catch (Exception e) {
            System.out.println(e);
        }
        return null;
    }

    public org.apromore.model.ProcessSummariesType searchForSimilarProcesses(
            int processId, String versionName,
            String method,
            Boolean latestVersions,
            double modelthreshold,
            double labelthreshold,
            double contextthreshold,
            double skipnweight,
            double subnweight,
            double skipeweight) throws ExceptionProcess {

        SearchForSimilarProcessesInputMsgType payload = new SearchForSimilarProcessesInputMsgType();
        payload.setAlgorithm(method);
        payload.setProcessId(processId);
        payload.setVersionName(versionName);
        payload.setLatestVersions(latestVersions);
        ParametersType params = new ParametersType();
        // modelthreshold
        ParameterType p = new ParameterType();
        p.setName("modelthreshold");
        p.setValue(modelthreshold);
        params.getParameter().add(p);

        // labelthreshold
        p = new ParameterType();
        p.setName("labelthreshold");
        p.setValue(labelthreshold);
        params.getParameter().add(p);

        // contextthreshold
        p = new ParameterType();
        p.setName("contextthreshold");
        p.setValue(contextthreshold);
        params.getParameter().add(p);

        if ("Greedy".equals(method)) {
            // skipnweight
            p = new ParameterType();
            p.setName("skipnweight");
            p.setValue(skipnweight);
            params.getParameter().add(p);

            // subnweight
            p = new ParameterType();
            p.setName("subnweight");
            p.setValue(subnweight);
            params.getParameter().add(p);

            // skipeweight
            p = new ParameterType();
            p.setName("skipeweight");
            p.setValue(skipeweight);
            params.getParameter().add(p);
        }
        payload.setParameters(params);

        SearchForSimilarProcessesOutputMsgType res = this.port.searchForSimilarProcesses(payload);

        ResultType result = res.getResult();
        if (result.getCode() == -1) {
            throw new ExceptionProcess(result.getMessage());
        } else {
            return res.getProcessSummaries();
        }
    }

    public ProcessSummaryType mergeProcesses(
            Map<ProcessSummaryType, List<VersionSummaryType>> selectedProcessVersions,
            String mergedProcessname,
            String mergedversionName,
            String mergedDomain,
            String mergedUsername,
            String method,
            boolean removeEntanglements,
            double mergethreshold,
            double labelthreshold,
            double contextthreshold,
            double skipnweight,
            double subnweight,
            double skipeweight) throws ExceptionProcess {

        MergeProcessesInputMsgType payload = new MergeProcessesInputMsgType();

        // merged process data
        payload.setProcessName(mergedProcessname);
        payload.setVersionName(mergedversionName);
        payload.setDomain(mergedDomain);
        payload.setUsername(mergedUsername);

        // process models
        ProcessVersionIdsType modelIdList = new ProcessVersionIdsType();
        for (Entry<ProcessSummaryType, List<VersionSummaryType>> i : selectedProcessVersions.entrySet()) {
            for (VersionSummaryType v : i.getValue()) {
                ProcessVersionIdType id = new ProcessVersionIdType();
                id.setProcessId(i.getKey().getId());
                id.setVersionName(v.getName());
                modelIdList.getProcessVersionId().add(id);
            }
        }
        payload.setProcessVersionIds(modelIdList);
        payload.setAlgorithm(method);

        // PARAMETERS
        ParametersType params = new ParametersType();
        // remove entanglements
        ParameterType p = new ParameterType();
        p.setName("removeent");
        p.setValue(removeEntanglements ? 1 : 0);
        params.getParameter().add(p);

        // modelthreshold
        p = new ParameterType();
        p.setName("modelthreshold");
        p.setValue(mergethreshold);
        params.getParameter().add(p);

        // labelthreshold
        p = new ParameterType();
        p.setName("labelthreshold");
        p.setValue(labelthreshold);
        params.getParameter().add(p);

        // contextthreshold
        p = new ParameterType();
        p.setName("contextthreshold");
        p.setValue(contextthreshold);
        params.getParameter().add(p);

        if ("Greedy".equals(method)) {
            // skipnweight
            p = new ParameterType();
            p.setName("skipnweight");
            p.setValue(skipnweight);
            params.getParameter().add(p);

            // subnweight
            p = new ParameterType();
            p.setName("subnweight");
            p.setValue(subnweight);
            params.getParameter().add(p);

            // skipeweight
            p = new ParameterType();
            p.setName("skipeweight");
            p.setValue(skipeweight);
            params.getParameter().add(p);
        }
        payload.setParameters(params);

        MergeProcessesOutputMsgType res = this.port.mergeProcesses(payload);

        ResultType result = res.getResult();
        if (result.getCode() == -1) {
            throw new ExceptionProcess(result.getMessage());
        } else {
            return res.getProcessSummary();
        }
    }


    public ProcessSummaryType importProcess(String username, String nativeType, String processName,
                                            String versionName, InputStream xml_process, String domain,
                                            String documentation, String created, String lastUpdate, Boolean addFakeEvents)
            throws IOException, ExceptionImport {
        ImportProcessInputMsgType payload = new ImportProcessInputMsgType();
        DataSource source = new ByteArrayDataSource(xml_process, "text/xml");
        payload.setProcessDescription(new DataHandler(source));
        EditSessionType editSession = new EditSessionType();
        payload.setEditSession(editSession);
        editSession.setUsername(username);
        editSession.setNativeType(nativeType);
        editSession.setProcessName(processName);
        editSession.setVersionName(versionName);
        editSession.setDomain(domain);
        editSession.setCreationDate(created);
        editSession.setLastUpdate(lastUpdate);
        payload.setAddFakeEvents(addFakeEvents);
        ImportProcessOutputMsgType res = this.port.importProcess(payload);
        ResultType result = res.getResult();
        if (result.getCode() == -1) {
            throw new ExceptionImport(result.getMessage());
        } else {
            return res.getProcessSummary();
        }

    }

    public InputStream ExportFormat(int processId, String processName, String versionName, String nativeType, String annotationName,
                                    Boolean withAnnotations, String owner)
            throws ExceptionExport, IOException {
        ExportFormatInputMsgType payload = new ExportFormatInputMsgType();
        payload.setProcessId(processId);
        payload.setVersionName(versionName);
        payload.setFormat(nativeType);
        payload.setAnnotationName(annotationName);
        payload.setWithAnnotations(withAnnotations);
        payload.setProcessName(processName);
        payload.setOwner(owner);
        ExportFormatOutputMsgType res = this.port.exportFormat(payload);
        ResultType result = res.getResult();
        if (result.getCode() == -1) {
            throw new ExceptionExport(result.getMessage());
        } else {
            DataHandler handler = res.getNative();
            InputStream is = handler.getInputStream();
            return is;
        }
    }

    public int WriteEditSession(EditSessionType editSession) throws ExceptionWriteEditSession {
        WriteEditSessionInputMsgType payload = new WriteEditSessionInputMsgType();
        payload.setEditSession(editSession);
        WriteEditSessionOutputMsgType res = this.port.writeEditSession(payload);
        ResultType result = res.getResult();
        if (result.getCode() == -1) {
            throw new ExceptionWriteEditSession(result.getMessage());
        } else {
            return res.getEditSessionCode();
        }
    }

    public void DeleteEditionSession(int code) throws ExceptionDeleteEditSession {
        DeleteEditSessionInputMsgType payload = new DeleteEditSessionInputMsgType();
        payload.setEditSessionCode(code);
        DeleteEditSessionOutputMsgType res = this.port.deleteEditSession(payload);
        ResultType result = res.getResult();
        if (result.getCode() == -1) {
            throw new ExceptionDeleteEditSession(result.getMessage());
        }
    }

    public EditSessionType ReadEditSession(int code) throws ExceptionReadEditSession {
        ReadEditSessionInputMsgType payload = new ReadEditSessionInputMsgType();
        payload.setEditSessionCode(code);
        ReadEditSessionOutputMsgType res = this.port.readEditSession(payload);
        ResultType result = res.getResult();
        if (result.getCode() == -1) {
            throw new ExceptionReadEditSession(result.getMessage());
        } else {
            return res.getEditSession();
        }
    }

    public void UpdateProcess(int sessionCode, String username, String nativeType,
                              int processId, String domain, String processName, String new_versionName, String preVersion, InputStream native_is)
            throws IOException, ExceptionUpdateProcess, ExceptionVersion {

        UpdateProcessInputMsgType payload = new UpdateProcessInputMsgType();
        payload.setEditSessionCode(sessionCode);

        EditSessionType editSession = new EditSessionType();
        payload.setEditSession(editSession);
        editSession.setUsername(username);
        editSession.setNativeType(nativeType);
        editSession.setProcessName(processName);
        editSession.setVersionName(new_versionName);
        editSession.setDomain(domain);
        editSession.setProcessId(processId);
        payload.setPreVersion(preVersion);
        DataSource sourceNat = new ByteArrayDataSource(native_is, "text/xml");
        payload.setNative(new DataHandler(sourceNat));
        // send request to manager
        UpdateProcessOutputMsgType res = this.port.updateProcess(payload);
        ResultType result = res.getResult();
        if (result.getCode() == -1) {
            throw new ExceptionUpdateProcess(result.getMessage());
        } else if (result.getCode() == -3) {
            throw new ExceptionVersion(result.getMessage());
        }

    }

    public void DeleteProcessVersions(
            Map<ProcessSummaryType, List<VersionSummaryType>> processVersions) throws ExceptionDeleteProcess {

        DeleteProcessVersionsInputMsgType payload = new DeleteProcessVersionsInputMsgType();
        Set<ProcessSummaryType> keys = processVersions.keySet();
        Iterator<ProcessSummaryType> it = keys.iterator();
        while (it.hasNext()) {
            ProcessSummaryType processSummary = it.next();
            List<VersionSummaryType> versionSummaries = processVersions.get(processSummary);

            ProcessVersionIdentifierType processVersionId = new ProcessVersionIdentifierType();
            payload.getProcessVersionIdentifier().add(processVersionId);
            processVersionId.setProcessid(processSummary.getId());
            for (int i = 0; i < versionSummaries.size(); i++) {
                processVersionId.getVersionName().add(versionSummaries.get(i).getName());
            }
        }
        DeleteProcessVersionsOutputMsgType res = this.port.deleteProcessVersions(payload);
        ResultType result = res.getResult();
        if (result.getCode() == -1) {
            throw new ExceptionDeleteProcess(result.getMessage());
        }
    }

    /**
     * Write the modified processes which are in processVersions. For each of which,
     * preNewVersion gives the mapping between its previous and new names.
     */
    public void EditProcessesData(Integer processId, String processName, String domain, String username,
                                  String preVersion, String newVersion, String ranking) throws ExceptionEditDataProcess {
        EditProcessDataInputMsgType payload = new EditProcessDataInputMsgType();
        payload.setDomain(domain);
        payload.setProcessName(processName);
        payload.setOwner(username);
        payload.setId(processId);
        payload.setNewName(newVersion);
        payload.setPreName(preVersion);
        payload.setRanking(ranking);
        EditProcessDataOutputMsgType res = this.port.editProcessData(payload);
        ResultType result = res.getResult();
        if (result.getCode() == -1) {
            throw new ExceptionEditDataProcess(result.getMessage());
        }
    }

    /**
     * write annotation contained in native_is. If isNew, then the annotation is new
     * and its name is annotName, otherwise it exist already and its name is
     * annotationName in editSessionMapping identified by editSessionCode
     *
     * @param editSessionCode
     * @param annotName
     * @param isNew
     * @param native_is
     * @throws org.apromore.portal.exception.ExceptionEditDataProcess
     *
     * @throws java.io.IOException
     */
    public void WriteAnnotation(Integer editSessionCode, String annotName,
                                boolean isNew, Integer processId, String version, String nat_type,
                                InputStream native_is) throws ExceptionEditDataProcess, IOException {
        WriteAnnotationInputMsgType payload = new WriteAnnotationInputMsgType();
        payload.setEditSessionCode(editSessionCode);
        payload.setAnnotationName(annotName);
        payload.setIsNew(isNew);
        payload.setProcessId(processId);
        payload.setVersion(version);
        payload.setNativeType(nat_type);
        DataSource sourceNat = new ByteArrayDataSource(native_is, "text/xml");
        payload.setNative(new DataHandler(sourceNat));
        WriteAnnotationOutputMsgType res = this.port.writeAnnotation(payload);
        ResultType result = res.getResult();
        if (result.getCode() == -1) {
            throw new ExceptionEditDataProcess(result.getMessage());
        }
    }
}
