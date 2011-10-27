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
import org.apromore.dao.dao.EditSessionDao;
import org.apromore.dao.dao.ProcessDao;
import org.apromore.model.DeleteEditSessionInputMsgType;
import org.apromore.model.DeleteEditSessionOutputMsgType;
import org.apromore.model.DeleteProcessVersionsInputMsgType;
import org.apromore.model.DeleteProcessVersionsOutputMsgType;
import org.apromore.model.EditProcessDataInputMsgType;
import org.apromore.model.EditProcessDataOutputMsgType;
import org.apromore.model.EditSessionType;
import org.apromore.model.ProcessVersionIdentifierType;
import org.apromore.model.ReadCanonicalAnfInputMsgType;
import org.apromore.model.ReadCanonicalAnfOutputMsgType;
import org.apromore.model.ReadEditSessionInputMsgType;
import org.apromore.model.ReadEditSessionOutputMsgType;
import org.apromore.model.ReadFormatInputMsgType;
import org.apromore.model.ReadFormatOutputMsgType;
import org.apromore.model.ResultType;
import org.apromore.model.WriteEditSessionInputMsgType;
import org.apromore.model.WriteEditSessionOutputMsgType;
import org.springframework.stereotype.Service;

/**
 *
 */
@Service
public class DataAccessManagerManagerImpl implements DataAccessManagerManager {

    private static final Logger LOG = Logger.getLogger(DataAccessManagerManagerImpl.class.getName());

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

}
