package org.apromore.portal.manager;

import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebResult;
import javax.jws.WebService;
import javax.jws.soap.SOAPBinding;
import javax.xml.bind.annotation.XmlSeeAlso;

/**
 * This class was generated by Apache CXF 2.2.7
 * Tue May 25 15:30:45 EST 2010
 * Generated source version: 2.2.7
 * 
 */
 
@WebService(targetNamespace = "http://www.apromore.org/manager/service_portal", name = "ManagerPortalPortType")
@XmlSeeAlso({org.apromore.portal.model_manager.ObjectFactory.class})
@SOAPBinding(parameterStyle = SOAPBinding.ParameterStyle.BARE)
public interface ManagerPortalPortType {

    @WebResult(name = "ExportNativeOutputMsg", targetNamespace = "http://www.apromore.org/manager/model_portal", partName = "payload")
    @WebMethod(operationName = "ExportNative")
    public org.apromore.portal.model_manager.ExportNativeOutputMsgType exportNative(
        @WebParam(partName = "payload", name = "ExportNativeInputMsg", targetNamespace = "http://www.apromore.org/manager/model_portal")
        org.apromore.portal.model_manager.ExportNativeInputMsgType payload
    );

    @WebResult(name = "WriteUserOutputMsg", targetNamespace = "http://www.apromore.org/manager/model_portal", partName = "payload")
    @WebMethod(operationName = "WriteUser")
    public org.apromore.portal.model_manager.WriteUserOutputMsgType writeUser(
        @WebParam(partName = "payload", name = "WriteUserInputMsg", targetNamespace = "http://www.apromore.org/manager/model_portal")
        org.apromore.portal.model_manager.WriteUserInputMsgType payload
    );

    @WebResult(name = "WriteEditSessionOutputMsg", targetNamespace = "http://www.apromore.org/manager/model_portal", partName = "payload")
    @WebMethod(operationName = "WriteEditSession")
    public org.apromore.portal.model_manager.WriteEditSessionOutputMsgType writeEditSession(
        @WebParam(partName = "payload", name = "WriteEditSessionInputMsg", targetNamespace = "http://www.apromore.org/manager/model_portal")
        org.apromore.portal.model_manager.WriteEditSessionInputMsgType payload
    );

	@WebResult(name = "ReadFormatsOutputMsg", targetNamespace = "http://www.apromore.org/manager/model_portal", partName = "payload")
    @WebMethod(operationName = "ReadFormats")
    public org.apromore.portal.model_manager.ReadFormatsOutputMsgType readFormats(
        @WebParam(partName = "payload", name = "ReadFormatsInputMsg", targetNamespace = "http://www.apromore.org/manager/model_portal")
        org.apromore.portal.model_manager.ReadFormatsInputMsgType payload
    );

    @WebResult(name = "ReadDomainsOutputMsg", targetNamespace = "http://www.apromore.org/manager/model_portal", partName = "payload")
    @WebMethod(operationName = "ReadDomains")
    public org.apromore.portal.model_manager.ReadDomainsOutputMsgType readDomains(
        @WebParam(partName = "payload", name = "ReadDomainsInputMsg", targetNamespace = "http://www.apromore.org/manager/model_portal")
        org.apromore.portal.model_manager.ReadDomainsInputMsgType payload
    );

    @WebResult(name = "ReadUserOutputMsg", targetNamespace = "http://www.apromore.org/manager/model_portal", partName = "payload")
    @WebMethod(operationName = "ReadUser")
    public org.apromore.portal.model_manager.ReadUserOutputMsgType readUser(
        @WebParam(partName = "payload", name = "ReadUserInputMsg", targetNamespace = "http://www.apromore.org/manager/model_portal")
        org.apromore.portal.model_manager.ReadUserInputMsgType payload
    );

    @WebResult(name = "DeleteEditSessionOutputMsg", targetNamespace = "http://www.apromore.org/manager/model_portal", partName = "payload")
    @WebMethod(operationName = "DeleteEditSession")
    public org.apromore.portal.model_manager.DeleteEditSessionOutputMsgType deleteEditSession(
        @WebParam(partName = "payload", name = "DeleteEditSessionInputMsg", targetNamespace = "http://www.apromore.org/manager/model_portal")
        org.apromore.portal.model_manager.DeleteEditSessionInputMsgType payload
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
