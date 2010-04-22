
/**
 * Please modify this class to meet your needs
 * This class is not complete
 */

package org.apromore.manager.service_portal;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.logging.Logger;

import javax.activation.DataHandler;

import org.apromore.manager.adapter.RequestToAdapter;
import org.apromore.manager.da.RequestToDA;
import org.apromore.manager.model_portal.DomainsType;
import org.apromore.manager.model_portal.FormatsType;
import org.apromore.manager.model_portal.ImportProcessInputMsgType;
import org.apromore.manager.model_portal.ImportProcessOutputMsgType;
import org.apromore.manager.model_portal.ProcessSummariesType;
import org.apromore.manager.model_portal.ReadDomainsInputMsgType;
import org.apromore.manager.model_portal.ReadDomainsOutputMsgType;
import org.apromore.manager.model_portal.ReadFormatsInputMsgType;
import org.apromore.manager.model_portal.ReadFormatsOutputMsgType;
import org.apromore.manager.model_portal.ReadProcessSummariesInputMsgType;
import org.apromore.manager.model_portal.ReadProcessSummariesOutputMsgType;
import org.apromore.manager.model_portal.ReadUserInputMsgType;
import org.apromore.manager.model_portal.ReadUserOutputMsgType;
import org.apromore.manager.model_portal.ResultType;
import org.apromore.manager.model_portal.UserType;
import org.apromore.manager.model_portal.WriteUserInputMsgType;
import org.apromore.manager.model_portal.WriteUserOutputMsgType;

/**
 * This class was generated by Apache CXF 2.2.7
 * Thu Apr 22 11:04:05 EST 2010
 * Generated source version: 2.2.7
 * 
 */

@javax.jws.WebService(
                      serviceName = "ManagerPortalService",
                      portName = "ManagerPortal",
                      targetNamespace = "http://www.apromore.org/manager/service_portal",
                      wsdlLocation = "http://localhost:8080/Apromore-manager/services/ManagerPortal?wsdl",
                      endpointInterface = "org.apromore.manager.service_portal.ManagerPortalPortType")
                      
public class ManagerPortalPortTypeImpl implements ManagerPortalPortType {

    private static final Logger LOG = Logger.getLogger(ManagerPortalPortTypeImpl.class.getName());

	public ImportProcessOutputMsgType importProcess(ImportProcessInputMsgType payload) { 
        LOG.info("Executing operation importProcess");
        System.out.println(payload);
        ImportProcessOutputMsgType res = new ImportProcessOutputMsgType();
        ResultType result = new ResultType();
        res.setResult(result);
        
        try {
        	DataHandler handler = payload.getProcessDescription();
        	InputStream is = handler.getInputStream();
        	RequestToAdapter request = new RequestToAdapter();
        	request.ImportProcess(payload.getProcessName(), payload.getNativeType(), is);
            result.setCode(0);
            result.setMessage("");
        	
        } catch (Exception ex) {
            ex.printStackTrace();
            ex.printStackTrace();
            result.setCode(0);
            result.setMessage("ManagerPortalPortTypeImpl(importProcess): " + ex.getMessage());
        }
        return res;
    }


	/* (non-Javadoc)
     * @see org.apromore.manager.service_portal.ManagerPortalPortType#writeUser(org.apromore.manager.model_portal.WriteUserInputMsgType  payload )*
     */
    public WriteUserOutputMsgType writeUser(WriteUserInputMsgType payload) { 
        LOG.info("Executing operation writeUser");
        System.out.println(payload);
        
        WriteUserOutputMsgType res = new WriteUserOutputMsgType();
        ResultType result = new ResultType();
        res.setResult(result);
        UserType user = payload.getUser();
        try {
        	RequestToDA request = new RequestToDA();
        	request.WriteUser(user);
            result.setCode(0);
            result.setMessage("");
        } catch (Exception ex) {
            ex.printStackTrace();
            result.setCode(0);
            result.setMessage("ManagerPortalPortTypeImpl(writeUser): " + ex.getMessage());
        }
		return res;
    }

    /* (non-Javadoc)
     * @see org.apromore.manager.service_portal.ManagerPortalPortType#readFormats(org.apromore.manager.model_portal.ReadFormatsInputMsgType  payload )*
     */
    public ReadFormatsOutputMsgType readFormats(ReadFormatsInputMsgType payload) { 
        LOG.info("Executing operation readFormats");
        System.out.println(payload);
        ReadFormatsOutputMsgType res = new ReadFormatsOutputMsgType();
        ResultType result = new ResultType();
        res.setResult(result);
        try {
            RequestToDA request = new RequestToDA();
            FormatsType formats = request.ReadFormats();
            result.setCode(0);
            result.setMessage("");
            res.setFormats(formats);
        } catch (Exception ex) {
            ex.printStackTrace();
            result.setCode(-1);
            result.setMessage("ManagerPortalPortTypeImpl(ReadFormats) " + ex.getMessage());
        }
        return res;
    }

    /* (non-Javadoc)
     * @see org.apromore.manager.service_portal.ManagerPortalPortType#readDomains(org.apromore.manager.model_portal.ReadDomainsInputMsgType  payload )*
     */
    public ReadDomainsOutputMsgType readDomains(ReadDomainsInputMsgType payload) { 
        LOG.info("Executing operation readDomains");
        System.out.println(payload);

        ReadDomainsOutputMsgType res = new ReadDomainsOutputMsgType();
        ResultType result = new ResultType();
        res.setResult(result);
        try {
        	RequestToDA request = new RequestToDA();
            DomainsType domains = request.ReadDomains();
            result.setCode(0);
            result.setMessage("");
            res.setDomains(domains);
        } catch (Exception ex) {
            ex.printStackTrace();
            result.setCode(-1);
            result.setMessage("ManagerPortalPortTypeImpl(ReadDomains) " + ex.getMessage());   
        }
        return res;
    }

    /* (non-Javadoc)
     * @see org.apromore.manager.service_portal.ManagerPortalPortType#readUser(org.apromore.manager.model_portal.ReadUserInputMsgType  payload )*
     */
    public ReadUserOutputMsgType readUser(ReadUserInputMsgType payload) { 
        LOG.info("Executing operation readUser");
        System.out.println(payload);
        
        ReadUserOutputMsgType res = new ReadUserOutputMsgType();
        ResultType result = new ResultType();
        res.setResult(result);
        
        try {
        	RequestToDA request = new RequestToDA();
        	UserType user = request.ReadUser(payload.getUsername());
            result.setCode(0);
            result.setMessage("");
            res.setUser(user);
        } catch (Exception ex) {
            ex.printStackTrace();
            result.setCode(-1);
            result.setMessage("ManagerPortalPortTypeImpl(ReadUser) " + ex.getMessage());
        }
        return res;
    }

    /* (non-Javadoc)
     * @see org.apromore.manager.service_portal.ManagerPortalPortType#readProcessSummaries(org.apromore.manager.model_portal.ReadProcessSummariesInputMsgType  payload )*
     */
    public ReadProcessSummariesOutputMsgType readProcessSummaries(ReadProcessSummariesInputMsgType payload) { 
        LOG.info("Executing operation readProcessSummaries");
        System.out.println(payload);
        
        ReadProcessSummariesOutputMsgType res = new ReadProcessSummariesOutputMsgType();
        ResultType result = new ResultType();
        res.setResult(result);
        
        try {
        	RequestToDA request = new RequestToDA();
        	ProcessSummariesType processes = request.ReadProcessSummaries (payload.getSearchExpression());
            result.setCode(0);
            result.setMessage("");
            res.setProcessSummaries(processes);
        } catch (Exception ex) {
            ex.printStackTrace();
            result.setCode(-1);
            result.setMessage("ManagerPortalPortTypeImpl(ReadProcessSummaries) " + ex.getMessage());
        }
        return res;
    }

}
