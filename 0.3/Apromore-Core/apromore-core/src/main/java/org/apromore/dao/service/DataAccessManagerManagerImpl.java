package org.apromore.dao.service;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.logging.Logger;

import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.mail.util.ByteArrayDataSource;

import org.apromore.common.Constants;
import org.apromore.dao.DataAccessManagerManager;
import org.apromore.dao.dao.DomainDao;
import org.apromore.dao.dao.EditSessionDao;
import org.apromore.dao.dao.FormatDao;
import org.apromore.dao.dao.ProcessDao;
import org.apromore.dao.dao.UserDao;
import org.apromore.model.DeleteEditSessionInputMsgType;
import org.apromore.model.DeleteEditSessionOutputMsgType;
import org.apromore.model.DeleteProcessVersionsInputMsgType;
import org.apromore.model.DeleteProcessVersionsOutputMsgType;
import org.apromore.model.DomainsType;
import org.apromore.model.EditProcessDataInputMsgType;
import org.apromore.model.EditProcessDataOutputMsgType;
import org.apromore.model.EditSessionType;
import org.apromore.model.NativeTypesType;
import org.apromore.model.ProcessSummariesType;
import org.apromore.model.ProcessVersionIdentifierType;
import org.apromore.model.ReadAllUsersInputMsgType;
import org.apromore.model.ReadAllUsersOutputMsgType;
import org.apromore.model.ReadCanonicalAnfInputMsgType;
import org.apromore.model.ReadCanonicalAnfOutputMsgType;
import org.apromore.model.ReadDomainsInputMsgType;
import org.apromore.model.ReadDomainsOutputMsgType;
import org.apromore.model.ReadEditSessionInputMsgType;
import org.apromore.model.ReadEditSessionOutputMsgType;
import org.apromore.model.ReadFormatInputMsgType;
import org.apromore.model.ReadFormatOutputMsgType;
import org.apromore.model.ReadNativeTypesInputMsgType;
import org.apromore.model.ReadNativeTypesOutputMsgType;
import org.apromore.model.ReadProcessSummariesInputMsgType;
import org.apromore.model.ReadProcessSummariesOutputMsgType;
import org.apromore.model.ReadUserInputMsgType;
import org.apromore.model.ReadUserOutputMsgType;
import org.apromore.model.ResultType;
import org.apromore.model.UserType;
import org.apromore.model.UsernamesType;
import org.apromore.model.WriteEditSessionInputMsgType;
import org.apromore.model.WriteEditSessionOutputMsgType;
import org.apromore.model.WriteUserInputMsgType;
import org.apromore.model.WriteUserOutputMsgType;
import org.springframework.stereotype.Service;

/**
 *
 */
@Service
public class DataAccessManagerManagerImpl implements DataAccessManagerManager {

    private static final Logger LOG = Logger.getLogger(DataAccessManagerManagerImpl.class.getName());
//
//    public ReadAllUsersOutputMsgType readAllUsers(ReadAllUsersInputMsgType payload) {
//        LOG.info("Executing operation readAllUsers");
//        System.out.println(payload);
//        ReadAllUsersOutputMsgType res = new ReadAllUsersOutputMsgType();
//        ResultType result = new ResultType();
//        res.setResult(result);
//        UsernamesType allUsers;
//        try {
//            allUsers = UserDao.getInstance().getAllUsers();
//            res.setUsernames(allUsers);
//            result.setCode(0);
//            result.setMessage("");
//        } catch (Exception ex) {
//            ex.printStackTrace();
//            result.setCode(-1);
//            result.setMessage(ex.getMessage());
//        }
//        return res;
//    }

    public ReadFormatOutputMsgType readFormat(ReadFormatInputMsgType payload) {
        LOG.info("Executing operation readNative");
        System.out.println(payload);
        ReadFormatOutputMsgType res = new ReadFormatOutputMsgType();
        ResultType result = new ResultType();
        res.setResult(result);
        try {
            Integer processId = payload.getProcessId();
            String version = payload.getVersion();
            String read = null;
            String format = payload.getFormat();

            if (Constants.CANONICAL.compareTo(format) == 0) {
                read = ProcessDao.getInstance().getCanonical(processId, version);
            } else if (format.startsWith(Constants.ANNOTATIONS)) {
                // format starts with Constants.ANNOTATIONS + " - "
                format = format.substring(Constants.ANNOTATIONS.length() + 3, format.length());
                read = ProcessDao.getInstance().getAnnotation(processId, version, format);
            } else {
                read = ProcessDao.getInstance().getNative(processId, version, format);
            }
            DataSource source = new ByteArrayDataSource(read, "text/xml");
            res.setNative(new DataHandler(source));
            result.setCode(0);
            result.setMessage("");
        } catch (Exception ex) {
            ex.printStackTrace();
            result.setCode(-1);
            result.setMessage(ex.getMessage());
        }
        return res;
    }


    public EditProcessDataOutputMsgType editProcessData(EditProcessDataInputMsgType payload) {
        LOG.info("Executing operation EditDataProcesses");
        System.out.println(payload);
        EditProcessDataOutputMsgType res = new EditProcessDataOutputMsgType();
        ResultType result = new ResultType();
        res.setResult(result);
        try {
            Integer processId = payload.getId();
            String processName = payload.getProcessName();
            String domain = payload.getDomain();
            String username = payload.getOwner();
            String preVersion = payload.getPreName();
            String newVersion = payload.getNewName();
            String ranking = payload.getRanking();
            ProcessDao.getInstance().editDataProcesses(processId, processName, domain, username, preVersion, newVersion, ranking);
            result.setCode(0);
            result.setMessage("");
        } catch (Exception ex) {
            ex.printStackTrace();
            result.setCode(-1);
            result.setMessage(ex.getMessage());
        }
        return res;
    }

    public DeleteProcessVersionsOutputMsgType deleteProcessVersions(DeleteProcessVersionsInputMsgType payload) {
        LOG.info("Executing operation deleteProcessVersions");
        System.out.println(payload);
        DeleteProcessVersionsOutputMsgType res = new DeleteProcessVersionsOutputMsgType();
        ResultType result = new ResultType();
        res.setResult(result);
        try {
            HashMap<Integer, List<String>> processVersions = new HashMap<Integer, List<String>>();
            List<ProcessVersionIdentifierType> processes = payload.getProcessVersionIdentifier();
            Iterator<ProcessVersionIdentifierType> it = processes.iterator();
            while (it.hasNext()) {
                ProcessVersionIdentifierType process = it.next();
                Iterator<String> itV = process.getVersionName().iterator();
                processVersions.put(process.getProcessid(), process.getVersionName());
            }
            ProcessDao.getInstance().deleteProcessVersions(processVersions);
            result.setCode(0);
            result.setMessage("");
        } catch (Exception ex) {
            ex.printStackTrace();
            result.setCode(-1);
            result.setMessage(ex.getMessage());
        }
        return res;
    }


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

    public ReadCanonicalAnfOutputMsgType readCanonicalAnf(ReadCanonicalAnfInputMsgType payload) {
        LOG.info("Executing operation readCanonicalAnf");
        System.out.println(payload);
        ReadCanonicalAnfOutputMsgType res = new ReadCanonicalAnfOutputMsgType();
        ResultType result = new ResultType();
        res.setResult(result);
        try {
            String canonical = ProcessDao.getInstance().getCanonical(payload.getProcessId(), payload.getVersion());
            DataSource sourceCpf = new ByteArrayDataSource(canonical, "text/xml");
            res.setCpf(new DataHandler(sourceCpf));
            Boolean withAnnotation = payload.isWithAnnotation();
            String anf = null;
            if (withAnnotation) {
                anf = ProcessDao.getInstance().getAnnotation(payload.getProcessId(), payload.getVersion(), payload.getAnnotationName());
                DataSource sourceAnf = new ByteArrayDataSource(anf, "text/xml");
                res.setAnf(new DataHandler(sourceAnf));
            }
            result.setCode(0);
            result.setMessage("");
        } catch (Exception ex) {
            ex.printStackTrace();
            result.setCode(-1);
            result.setMessage(ex.getMessage());
        }
        return res;
    }

    public ReadNativeTypesOutputMsgType readNativeTypes(ReadNativeTypesInputMsgType payload) {
        LOG.info("(DA)Executing operation readFormats");
        System.out.println(payload);
        ReadNativeTypesOutputMsgType res = new ReadNativeTypesOutputMsgType();
        ResultType result = new ResultType();
        NativeTypesType formats;
        res.setResult(result);
        try {
            formats = ((FormatDao) FormatDao.getInstance()).getFormats();
            res.setNativeTypes(formats);
            result.setCode(0);
            result.setMessage("");
        } catch (Exception ex) {
            ex.printStackTrace();
            result.setCode(-1);
            result.setMessage(ex.getMessage());
        }
        return res;
    }

//    /* (non-Javadoc)
//      * @see org.apromore.dataaccess.service.DataAccessManager#writeUser(org.apromore.dataaccess.model_manager.WriteUserInputMsgType  payload )*
//      */
//    public WriteUserOutputMsgType writeUser(WriteUserInputMsgType payload) {
//        LOG.info("Executing operation writeUser");
//        System.out.println(payload);
//
//        WriteUserOutputMsgType res = new WriteUserOutputMsgType();
//        ResultType result = new ResultType();
//        res.setResult(result);
//        UserType user = payload.getUser();
//        try {
//            UserDao.getInstance().writeUser(user);
//            result.setCode(0);
//            result.setMessage("");
//        } catch (Exception ex) {
//            ex.printStackTrace();
//            result.setCode(-1);
//            result.setMessage(ex.getMessage());
//        }
//        return res;
//    }

    /* (non-Javadoc)
      * @see org.apromore.dataaccess.service.DataAccessManager#readProcessSummaries(org.apromore.dataaccess.model_manager.ReadProcessSummariesInputMsgType  payload )*
      */
    public ReadProcessSummariesOutputMsgType readProcessSummaries(ReadProcessSummariesInputMsgType payload) {
        LOG.info("Executing operation readProcessSummaries");
        System.out.println(payload);
        ReadProcessSummariesOutputMsgType res = new ReadProcessSummariesOutputMsgType();
        ResultType result = new ResultType();
        ProcessSummariesType processSummaries;
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
            result.setMessage(ex.getMessage());
        }
        return res;
    }

    /* (non-Javadoc)
      * @see org.apromore.dataaccess.service.DataAccessManager#readDomains(org.apromore.dataaccess.model_manager.ReadDomainsInputMsgType  payload )*
      */
    public ReadDomainsOutputMsgType readDomains(ReadDomainsInputMsgType payload) {
        LOG.info("Executing operation readDomains");
        System.out.println(payload);

        ReadDomainsOutputMsgType res = new ReadDomainsOutputMsgType();
        ResultType result = new ResultType();
        DomainsType domains;
        res.setResult(result);
        try {
            domains = ((DomainDao) DomainDao.getInstance()).getDomains();
            res.setDomains(domains);
            result.setCode(0);
            result.setMessage("");
        } catch (Exception ex) {
            ex.printStackTrace();
            result.setCode(-1);
            result.setMessage(ex.getMessage());
        }
        return res;
    }

//    /* (non-Javadoc)
//      * @see org.apromore.dataaccess.service.DataAccessManager#readUser(org.apromore.dataaccess.model_manager.ReadUserInputMsgType  payload )*
//      */
//    public ReadUserOutputMsgType readUser(ReadUserInputMsgType payload) {
//        LOG.info("Executing operation readUser");
//        System.out.println(payload);
//
//        String username = payload.getUsername();
//        ReadUserOutputMsgType res = new ReadUserOutputMsgType();
//        ResultType result = new ResultType();
//        UserType user;
//        res.setResult(result);
//
//        try {
//            user = ((UserDao) UserDao.getInstance()).readUser(username);
//            res.setUser(user);
//            result.setCode(0);
//            result.setMessage("");
//        } catch (Exception ex) {
//            ex.printStackTrace();
//            result.setCode(-1);
//            result.setMessage(ex.getMessage());
//        }
//        return res;
//    }

}
