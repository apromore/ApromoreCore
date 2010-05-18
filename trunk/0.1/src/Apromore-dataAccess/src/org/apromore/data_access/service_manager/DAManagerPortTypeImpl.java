
/**
 * Please modify this class to meet your needs
 * This class is not complete
 */

package org.apromore.data_access.service_manager;

import java.util.logging.Logger;

import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebResult;
import javax.jws.WebService;
import javax.jws.soap.SOAPBinding;
import javax.xml.bind.annotation.XmlSeeAlso;
import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.mail.util.ByteArrayDataSource;

import org.apromore.data_access.dao.DomainDao;
import org.apromore.data_access.dao.EditSessionDao;
import org.apromore.data_access.dao.FormatDao;
import org.apromore.data_access.dao.ProcessDao;
import org.apromore.data_access.dao.UserDao;
import org.apromore.data_access.model_manager.DeleteEditSessionInputMsgType;
import org.apromore.data_access.model_manager.DeleteEditSessionOutputMsgType;
import org.apromore.data_access.model_manager.DomainsType;
import org.apromore.data_access.model_manager.EditSessionType;
import org.apromore.data_access.model_manager.FormatsType;
import org.apromore.data_access.model_manager.ProcessSummariesType;
import org.apromore.data_access.model_manager.ReadCanonicalInputMsgType;
import org.apromore.data_access.model_manager.ReadCanonicalOutputMsgType;
import org.apromore.data_access.model_manager.ReadDomainsInputMsgType;
import org.apromore.data_access.model_manager.ReadDomainsOutputMsgType;
import org.apromore.data_access.model_manager.ReadEditSessionInputMsgType;
import org.apromore.data_access.model_manager.ReadEditSessionOutputMsgType;
import org.apromore.data_access.model_manager.ReadFormatsInputMsgType;
import org.apromore.data_access.model_manager.ReadFormatsOutputMsgType;
import org.apromore.data_access.model_manager.ReadNativeInputMsgType;
import org.apromore.data_access.model_manager.ReadNativeOutputMsgType;
import org.apromore.data_access.model_manager.ReadProcessSummariesInputMsgType;
import org.apromore.data_access.model_manager.ReadProcessSummariesOutputMsgType;
import org.apromore.data_access.model_manager.ReadUserInputMsgType;
import org.apromore.data_access.model_manager.ReadUserOutputMsgType;
import org.apromore.data_access.model_manager.ResultType;
import org.apromore.data_access.model_manager.UserType;
import org.apromore.data_access.model_manager.WriteEditSessionInputMsgType;
import org.apromore.data_access.model_manager.WriteEditSessionOutputMsgType;
import org.apromore.data_access.model_manager.WriteUserInputMsgType;
import org.apromore.data_access.model_manager.WriteUserOutputMsgType;

/**
 * This class was generated by Apache CXF 2.2.7
 * Tue May 18 12:12:06 EST 2010
 * Generated source version: 2.2.7
 * 
 */

@javax.jws.WebService(
                      serviceName = "DAManagerService",
                      portName = "DAManager",
                      targetNamespace = "http://www.apromore.org/data_access/service_manager",
                      wsdlLocation = "http://localhost:8080/Apromore-dataAccess/services/DAManager?wsdl",
                      endpointInterface = "org.apromore.data_access.service_manager.DAManagerPortType")


		public class DAManagerPortTypeImpl implements DAManagerPortType {
	private static final Logger LOG = Logger.getLogger(DAManagerPortTypeImpl.class.getName());

	public DeleteEditSessionOutputMsgType deleteEditSession(DeleteEditSessionInputMsgType payload) { 
		LOG.info("Executing operation deleteEditSession");
		System.out.println(payload);
		DeleteEditSessionOutputMsgType res = new DeleteEditSessionOutputMsgType();
		ResultType result = new ResultType();
		res.setResult(result);
		try {
			int code = payload.getEditSessionCode();
			EditSessionDao.getInstance().deleteEditSession(code);
			result.setCode(0);
			result.setMessage("");
		} catch (Exception ex) {
			ex.printStackTrace();
			result.setCode(-1);
			result.setMessage(ex.getMessage());
		}
		return res;
	}

	public ReadEditSessionOutputMsgType readEditSession(ReadEditSessionInputMsgType payload) { 
		LOG.info("Executing operation readEditSession");
		System.out.println(payload);
		ReadEditSessionOutputMsgType res = new ReadEditSessionOutputMsgType();
		ResultType result = new ResultType();
		res.setResult(result);
		try {
			int code = payload.getEditSessionCode();
			EditSessionType editSession = EditSessionDao.getInstance().getEditSession(code);
			res.setEditSession(editSession);
			result.setCode(0);
			result.setMessage("");
		} catch (Exception ex) {
			ex.printStackTrace();
			result.setCode(-1);
			result.setMessage(ex.getMessage());
		}
		return res;
	}


	public WriteEditSessionOutputMsgType writeEditSession(WriteEditSessionInputMsgType payload) { 
		LOG.info("Executing operation writeEditSession");
		System.out.println(payload);
		WriteEditSessionOutputMsgType res = new WriteEditSessionOutputMsgType();
		ResultType result = new ResultType();
		res.setResult(result);
		try {
			EditSessionType editSession = payload.getEditSession();
			int code = EditSessionDao.getInstance().writeEditSession(editSession);
			res.setEditSessionCode(code);
			result.setCode(0);
			result.setMessage("");
		} catch (Exception ex) {
			ex.printStackTrace();
			result.setCode(-1);
			result.setMessage(ex.getMessage());
		}
		return res;
	}

	public ReadCanonicalOutputMsgType readCanonical(ReadCanonicalInputMsgType payload) { 
		LOG.info("Executing operation readCanonical");
		System.out.println(payload);
		ReadCanonicalOutputMsgType res = new  ReadCanonicalOutputMsgType();
		ResultType result = new ResultType();
		res.setResult(result);
		try {
			String canonical = ProcessDao.getInstance().getCanonical(payload.getProcessId(), 
					payload.getVersion());
			DataSource sourceCpf = new ByteArrayDataSource(canonical, "text/xml"); 
			res.setCpf(new DataHandler(sourceCpf));
			result.setCode(0);
			result.setMessage("");
		} catch (Exception ex) {
			ex.printStackTrace();
			result.setCode(-1);
			result.setMessage("DAManagerPortImplem(ReadCanonical): " + ex.getMessage());
		}
		return res;
	}

	public ReadNativeOutputMsgType readNative(ReadNativeInputMsgType payload) { 
		LOG.info("Executing operation readNative");
		System.out.println(payload);
		ReadNativeOutputMsgType res = new ReadNativeOutputMsgType();
		ResultType result = new ResultType();
		res.setResult(result);
		try {
			String natives = ProcessDao.getInstance().getNative(payload.getProcessId(), 
					payload.getVersion(), payload.getNativeType());
			DataSource source = new ByteArrayDataSource(natives, "text/xml"); 
			res.setNative(new DataHandler(source));
			result.setCode(0);
			result.setMessage("");
		} catch (Exception ex) {
			ex.printStackTrace();
			result.setCode(-1);
			result.setMessage("DAManagerPortImplem(ReadNative): " + ex.getMessage());
		}
		return res;
	}

	/* (non-Javadoc)
	 * @see org.apromore.dataaccess.service_manager.DAManagerPortType#readFormats(org.apromore.dataaccess.model_manager.ReadFormatsInputMsgType  payload )*
	 */
	/**
	 * payload is empty.
	 */
	public ReadFormatsOutputMsgType readFormats(ReadFormatsInputMsgType payload) { 
		LOG.info("(DA)Executing operation readFormats");
		System.out.println(payload);
		ReadFormatsOutputMsgType res = new ReadFormatsOutputMsgType();
		ResultType result = new ResultType();
		FormatsType formats ;
		res.setResult(result);

		try {
			formats = ((FormatDao) FormatDao.getInstance()).getFormats();
			res.setFormats(formats);
			result.setCode(0);
			result.setMessage("");
		} catch (Exception ex) {
			ex.printStackTrace();
			result.setCode(-1);
			result.setMessage("DAManagerPortImplem(ReadFormats): " + ex.getMessage());
		}
		return res;
	}

	/* (non-Javadoc)
	 * @see org.apromore.dataaccess.service_manager.DAManagerPortType#writeUser(org.apromore.dataaccess.model_manager.WriteUserInputMsgType  payload )*
	 */
	public WriteUserOutputMsgType writeUser(WriteUserInputMsgType payload) { 
		LOG.info("Executing operation writeUser");
		System.out.println(payload);

		WriteUserOutputMsgType res = new WriteUserOutputMsgType();
		ResultType result = new ResultType();
		res.setResult(result);
		UserType user = payload.getUser();
		try {			
			UserDao.getInstance().writeUser(user);
			result.setCode(0);
			result.setMessage("");
		} catch (Exception ex) {
			ex.printStackTrace();
			result.setCode(-1);
			result.setMessage("DAManagerPortImplem(WriteUser): " + ex.getMessage());
		}
		return res;
	}

	/* (non-Javadoc)
	 * @see org.apromore.dataaccess.service_manager.DAManagerPortType#readProcessSummaries(org.apromore.dataaccess.model_manager.ReadProcessSummariesInputMsgType  payload )*
	 */
	public ReadProcessSummariesOutputMsgType readProcessSummaries(ReadProcessSummariesInputMsgType payload) { 
		LOG.info("Executing operation readProcessSummaries");
		System.out.println(payload);
		ReadProcessSummariesOutputMsgType res = new ReadProcessSummariesOutputMsgType();
		ResultType result = new ResultType();
		ProcessSummariesType processSummaries ;
		res.setResult(result);
		String searchExp = payload.getSearchExpression();

		try {
			processSummaries = ((ProcessDao) ProcessDao.getInstance()).getProcessSummaries(searchExp);
			res.setProcessSummaries(processSummaries);
			result.setCode(0);
			result.setMessage("");
		} catch (Exception ex) {
			ex.printStackTrace();
			result.setCode(-1);
			result.setMessage("DAManagerPortImplem(ReadSummaries): " + ex.getMessage());
		}
		return res;
	}

	/* (non-Javadoc)
	 * @see org.apromore.dataaccess.service_manager.DAManagerPortType#readDomains(org.apromore.dataaccess.model_manager.ReadDomainsInputMsgType  payload )*
	 */
	public ReadDomainsOutputMsgType readDomains(ReadDomainsInputMsgType payload) { 
		LOG.info("Executing operation readDomains");
		System.out.println(payload);

		ReadDomainsOutputMsgType res = new ReadDomainsOutputMsgType();
		ResultType result = new ResultType();
		DomainsType domains ;
		res.setResult(result);
		try {
			domains = ((DomainDao) DomainDao.getInstance()).getDomains();
			res.setDomains(domains);
			result.setCode(0);
			result.setMessage("");
		} catch (Exception ex) {
			ex.printStackTrace();
			result.setCode(-1);
			result.setMessage("DAManagerPortImplem(ReadDomains): " + ex.getMessage());
		}
		return res;
	}

	/* (non-Javadoc)
	 * @see org.apromore.dataaccess.service_manager.DAManagerPortType#readUser(org.apromore.dataaccess.model_manager.ReadUserInputMsgType  payload )*
	 */
	public ReadUserOutputMsgType readUser(ReadUserInputMsgType payload) { 
		LOG.info("Executing operation readUser");
		System.out.println(payload);

		String username = payload.getUsername();
		ReadUserOutputMsgType res = new ReadUserOutputMsgType();
		ResultType result = new ResultType();
		UserType user ;
		res.setResult(result);

		try {
			user = ((UserDao) UserDao.getInstance()).readUser(username);
			res.setUser(user);
			result.setCode(0);
			result.setMessage("");
		} catch (Exception ex) {
			ex.printStackTrace();
			result.setCode(-1);
			result.setMessage("DAManagerPortImplem(ReadUser): " + ex.getMessage());
		}
		return res;
	}

}
