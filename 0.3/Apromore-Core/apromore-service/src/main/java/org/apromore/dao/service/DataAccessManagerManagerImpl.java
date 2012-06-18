package org.apromore.dao.service;

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
import org.apromore.model.ReadEditSessionInputMsgType;
import org.apromore.model.ReadEditSessionOutputMsgType;
import org.apromore.model.ResultType;
import org.apromore.model.WriteEditSessionInputMsgType;
import org.apromore.model.WriteEditSessionOutputMsgType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

/**
 *
 */
@Service
public class DataAccessManagerManagerImpl implements DataAccessManagerManager {

    private static final Logger LOGGER = LoggerFactory.getLogger(DataAccessManagerManagerImpl.class.getName());


    public EditProcessDataOutputMsgType editProcessData(EditProcessDataInputMsgType payload) {
        LOGGER.info("Executing operation EditDataProcesses");
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
        LOGGER.info("Executing operation deleteProcessVersions");
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
        LOGGER.info("Executing operation deleteEditSession");
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
        LOGGER.info("Executing operation readEditSession");
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
        LOGGER.info("Executing operation writeEditSession");
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

}
