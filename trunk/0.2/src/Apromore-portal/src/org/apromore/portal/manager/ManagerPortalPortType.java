package org.apromore.portal.manager;

import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebResult;
import javax.jws.WebService;
import javax.jws.soap.SOAPBinding;
import javax.xml.bind.annotation.XmlSeeAlso;

/**
 * This class was generated by Apache CXF 2.2.9
 * Thu Mar 03 14:08:48 CET 2011
 * Generated source version: 2.2.9
 * 
 */
 
@WebService(targetNamespace = "http://www.apromore.org/manager/service_portal", name = "ManagerPortalPortType")
@XmlSeeAlso({org.apromore.portal.model_manager.ObjectFactory.class})
@SOAPBinding(parameterStyle = SOAPBinding.ParameterStyle.BARE)
public interface ManagerPortalPortType {

    @WebResult(name = "WriteUserOutputMsg", targetNamespace = "http://www.apromore.org/manager/model_portal", partName = "payload")
    @WebMethod(operationName = "WriteUser")
    public org.apromore.portal.model_manager.WriteUserOutputMsgType writeUser(
        @WebParam(partName = "payload", name = "WriteUserInputMsg", targetNamespace = "http://www.apromore.org/manager/model_portal")
        org.apromore.portal.model_manager.WriteUserInputMsgType payload
    );

    @WebResult(name = "EditProcessDataOutputMsg", targetNamespace = "http://www.apromore.org/manager/model_portal", partName = "payload")
    @WebMethod(operationName = "EditProcessData")
    public org.apromore.portal.model_manager.EditProcessDataOutputMsgType editProcessData(
        @WebParam(partName = "payload", name = "EditProcessDataInputMsg", targetNamespace = "http://www.apromore.org/manager/model_portal")
        org.apromore.portal.model_manager.EditProcessDataInputMsgType payload
    );

    @WebResult(name = "WriteEditSessionOutputMsg", targetNamespace = "http://www.apromore.org/manager/model_portal", partName = "payload")
    @WebMethod(operationName = "WriteEditSession")
    public org.apromore.portal.model_manager.WriteEditSessionOutputMsgType writeEditSession(
        @WebParam(partName = "payload", name = "WriteEditSessionInputMsg", targetNamespace = "http://www.apromore.org/manager/model_portal")
        org.apromore.portal.model_manager.WriteEditSessionInputMsgType payload
    );

    @WebResult(name = "ExportFormatOutputMsg", targetNamespace = "http://www.apromore.org/manager/model_portal", partName = "payload")
    @WebMethod(operationName = "ExportFormat")
    public org.apromore.portal.model_manager.ExportFormatOutputMsgType exportFormat(
        @WebParam(partName = "payload", name = "ExportFormatInputMsg", targetNamespace = "http://www.apromore.org/manager/model_portal")
        org.apromore.portal.model_manager.ExportFormatInputMsgType payload
    );

    @WebResult(name = "ReadNativeTypesOutputMsg", targetNamespace = "http://www.apromore.org/manager/model_portal", partName = "payload")
    @WebMethod(operationName = "ReadNativeTypes")
    public org.apromore.portal.model_manager.ReadNativeTypesOutputMsgType readNativeTypes(
        @WebParam(partName = "payload", name = "ReadNativeTypesInputMsg", targetNamespace = "http://www.apromore.org/manager/model_portal")
        org.apromore.portal.model_manager.ReadNativeTypesInputMsgType payload
    );

    @WebResult(name = "ReadDomainsOutputMsg", targetNamespace = "http://www.apromore.org/manager/model_portal", partName = "payload")
    @WebMethod(operationName = "ReadDomains")
    public org.apromore.portal.model_manager.ReadDomainsOutputMsgType readDomains(
        @WebParam(partName = "payload", name = "ReadDomainsInputMsg", targetNamespace = "http://www.apromore.org/manager/model_portal")
        org.apromore.portal.model_manager.ReadDomainsInputMsgType payload
    );

    @WebResult(name = "MergeProcessesOutputMsg", targetNamespace = "http://www.apromore.org/manager/model_portal", partName = "payload")
    @WebMethod(operationName = "MergeProcesses")
    public org.apromore.portal.model_manager.MergeProcessesOutputMsgType mergeProcesses(
        @WebParam(partName = "payload", name = "MergeProcessesInputMsg", targetNamespace = "http://www.apromore.org/manager/model_portal")
        org.apromore.portal.model_manager.MergeProcessesInputMsgType payload
    );

    @WebResult(name = "ReadUserOutputMsg", targetNamespace = "http://www.apromore.org/manager/model_portal", partName = "payload")
    @WebMethod(operationName = "ReadUser")
    public org.apromore.portal.model_manager.ReadUserOutputMsgType readUser(
        @WebParam(partName = "payload", name = "ReadUserInputMsg", targetNamespace = "http://www.apromore.org/manager/model_portal")
        org.apromore.portal.model_manager.ReadUserInputMsgType payload
    );

    @WebResult(name = "WriteAnnotationOutputMsg", targetNamespace = "http://www.apromore.org/manager/model_portal", partName = "payload")
    @WebMethod(operationName = "WriteAnnotation")
    public org.apromore.portal.model_manager.WriteAnnotationOutputMsgType writeAnnotation(
        @WebParam(partName = "payload", name = "WriteAnnotationInputMsg", targetNamespace = "http://www.apromore.org/manager/model_portal")
        org.apromore.portal.model_manager.WriteAnnotationInputMsgType payload
    );

    @WebResult(name = "ReadAllUsersOutputMsg", targetNamespace = "http://www.apromore.org/manager/model_portal", partName = "payload")
    @WebMethod(operationName = "ReadAllUsers")
    public org.apromore.portal.model_manager.ReadAllUsersOutputMsgType readAllUsers(
        @WebParam(partName = "payload", name = "ReadAllUsersInputMsg", targetNamespace = "http://www.apromore.org/manager/model_portal")
        org.apromore.portal.model_manager.ReadAllUsersInputMsgType payload
    );

    @WebResult(name = "DeleteEditSessionOutputMsg", targetNamespace = "http://www.apromore.org/manager/model_portal", partName = "payload")
    @WebMethod(operationName = "DeleteEditSession")
    public org.apromore.portal.model_manager.DeleteEditSessionOutputMsgType deleteEditSession(
        @WebParam(partName = "payload", name = "DeleteEditSessionInputMsg", targetNamespace = "http://www.apromore.org/manager/model_portal")
        org.apromore.portal.model_manager.DeleteEditSessionInputMsgType payload
    );

    @WebResult(name = "SearchForSimilarProcessesOutputMsg", targetNamespace = "http://www.apromore.org/manager/model_portal", partName = "payload")
    @WebMethod(operationName = "SearchForSimilarProcesses")
    public org.apromore.portal.model_manager.SearchForSimilarProcessesOutputMsgType searchForSimilarProcesses(
        @WebParam(partName = "payload", name = "SearchForSimilarProcessesInputMsg", targetNamespace = "http://www.apromore.org/manager/model_portal")
        org.apromore.portal.model_manager.SearchForSimilarProcessesInputMsgType payload
    );

    @WebResult(name = "DeleteProcessVersionsOutputMsg", targetNamespace = "http://www.apromore.org/manager/model_portal", partName = "payload")
    @WebMethod(operationName = "DeleteProcessVersions")
    public org.apromore.portal.model_manager.DeleteProcessVersionsOutputMsgType deleteProcessVersions(
        @WebParam(partName = "payload", name = "DeleteProcessVersionsInputMsg", targetNamespace = "http://www.apromore.org/manager/model_portal")
        org.apromore.portal.model_manager.DeleteProcessVersionsInputMsgType payload
    );

    @WebResult(name = "ImportProcessOutputMsg", targetNamespace = "http://www.apromore.org/manager/model_portal", partName = "payload")
    @WebMethod(operationName = "ImportProcess")
    public org.apromore.portal.model_manager.ImportProcessOutputMsgType importProcess(
        @WebParam(partName = "payload", name = "ImportProcessInputMsg", targetNamespace = "http://www.apromore.org/manager/model_portal")
        org.apromore.portal.model_manager.ImportProcessInputMsgType payload
    );

    @WebResult(name = "ReadProcessSummariesOutputMsg", targetNamespace = "http://www.apromore.org/manager/model_portal", partName = "payload")
    @WebMethod(operationName = "ReadProcessSummaries")
    public org.apromore.portal.model_manager.ReadProcessSummariesOutputMsgType readProcessSummaries(
        @WebParam(partName = "payload", name = "ReadProcessSummariesInputMsg", targetNamespace = "http://www.apromore.org/manager/model_portal")
        org.apromore.portal.model_manager.ReadProcessSummariesInputMsgType payload
    );

    @WebResult(name = "ReadEditSessionOutputMsg", targetNamespace = "http://www.apromore.org/manager/model_portal", partName = "payload")
    @WebMethod(operationName = "ReadEditSession")
    public org.apromore.portal.model_manager.ReadEditSessionOutputMsgType readEditSession(
        @WebParam(partName = "payload", name = "ReadEditSessionInputMsg", targetNamespace = "http://www.apromore.org/manager/model_portal")
        org.apromore.portal.model_manager.ReadEditSessionInputMsgType payload
    );

    @WebResult(name = "UpdateProcessOutputMsg", targetNamespace = "http://www.apromore.org/manager/model_portal", partName = "payload")
    @WebMethod(operationName = "UpdateProcess")
    public org.apromore.portal.model_manager.UpdateProcessOutputMsgType updateProcess(
        @WebParam(partName = "payload", name = "UpdateProcessInputMsg", targetNamespace = "http://www.apromore.org/manager/model_portal")
        org.apromore.portal.model_manager.UpdateProcessInputMsgType payload
    );
}
