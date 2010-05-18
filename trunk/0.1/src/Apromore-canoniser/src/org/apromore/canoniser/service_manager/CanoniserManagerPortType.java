package org.apromore.canoniser.service_manager;

import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebResult;
import javax.jws.WebService;
import javax.jws.soap.SOAPBinding;
import javax.xml.bind.annotation.XmlSeeAlso;

/**
 * This class was generated by Apache CXF 2.2.7
 * Tue May 18 17:44:02 EST 2010
 * Generated source version: 2.2.7
 * 
 */
 
@WebService(targetNamespace = "http://www.apromore.org/canoniser/service_manager", name = "CanoniserManagerPortType")
@XmlSeeAlso({org.apromore.canoniser.model_manager.ObjectFactory.class})
@SOAPBinding(parameterStyle = SOAPBinding.ParameterStyle.BARE)
public interface CanoniserManagerPortType {

    @WebResult(name = "CanoniseProcessOutputMsg", targetNamespace = "http://www.apromore.org/canoniser/model_manager", partName = "payload")
    @WebMethod(operationName = "CanoniseProcess")
    public org.apromore.canoniser.model_manager.CanoniseProcessOutputMsgType canoniseProcess(
        @WebParam(partName = "payload", name = "CanoniseProcessInputMsg", targetNamespace = "http://www.apromore.org/canoniser/model_manager")
        org.apromore.canoniser.model_manager.CanoniseProcessInputMsgType payload
    );

	@WebResult(name = "DeCanoniseProcessOutputMsg", targetNamespace = "http://www.apromore.org/canoniser/model_manager", partName = "payload")
    @WebMethod(operationName = "DeCanoniseProcess")
    public org.apromore.canoniser.model_manager.DeCanoniseProcessOutputMsgType deCanoniseProcess(
        @WebParam(partName = "payload", name = "DeCanoniseProcessInputMsg", targetNamespace = "http://www.apromore.org/canoniser/model_manager")
        org.apromore.canoniser.model_manager.DeCanoniseProcessInputMsgType payload
    );

	@WebResult(name = "CanoniseVersionOutputMsg", targetNamespace = "http://www.apromore.org/canoniser/model_manager", partName = "payload")
    @WebMethod(operationName = "CanoniseVersion")
    public org.apromore.canoniser.model_manager.CanoniseVersionOutputMsgType canoniseVersion(
        @WebParam(partName = "payload", name = "CanoniseVersionInputMsg", targetNamespace = "http://www.apromore.org/canoniser/model_manager")
        org.apromore.canoniser.model_manager.CanoniseVersionInputMsgType payload
    );
}
