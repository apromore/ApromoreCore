package org.apromore.dao.service;

import org.apromore.dao.DataAccessCanoniserManager;
import org.apromore.dao.dao.ProcessDao;
import org.apromore.exception.ExceptionAnntotationName;
import org.apromore.exception.ExceptionDao;
import org.apromore.exception.ExceptionStoreVersion;
import org.apromore.exception.ExceptionSyncNPF;
import org.apromore.model.EditSessionType;
import org.apromore.model.GetCpfUriOutputMsgType;
import org.apromore.model.ResultType;
import org.apromore.model.StoreNativeInputMsgType;
import org.apromore.model.StoreNativeOutputMsgType;
import org.apromore.model.StoreVersionInputMsgType;
import org.apromore.model.StoreVersionOutputMsgType;
import org.apromore.model.WriteAnnotationInputMsgType;
import org.apromore.model.WriteAnnotationOutputMsgType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.activation.DataHandler;
import java.io.IOException;
import java.io.InputStream;
import java.sql.SQLException;

/**
 *
 */
@Service
public class DataAccessCanoniserManagerImpl implements DataAccessCanoniserManager {

    private static final Logger LOGGER = LoggerFactory.getLogger(DataAccessCanoniserManagerImpl.class.getName());


    public GetCpfUriOutputMsgType getCpfUri(org.apromore.model.GetCpfUriInputMsgType payload) {
        LOGGER.info("Executing operation getCpfUri");
        GetCpfUriOutputMsgType res = new GetCpfUriOutputMsgType();
        ResultType result = new ResultType();
        res.setResult(result);
        try {
            String version = payload.getVersion();
            Integer processId = payload.getProcessId();
            String cpf_uri = ProcessDao.getInstance().getCpfUri(processId, version);
            res.setCpfURI(cpf_uri);
            result.setCode(0);
            result.setMessage("");
        } catch (Exception ex) {
            result.setCode(-1);
            result.setMessage(ex.getMessage());
        }
        return res;
    }


    public WriteAnnotationOutputMsgType writeAnnotation(WriteAnnotationInputMsgType payload) {
        LOGGER.info("Executing operation writeAnnotation");
        WriteAnnotationOutputMsgType res = new WriteAnnotationOutputMsgType();
        ResultType result = new ResultType();
        res.setResult(result);
        try {
            Integer editSessionCode = payload.getEditSessionCode();
            String annotationName = payload.getAnnotationName();
            Boolean isNew = payload.isIsNew();
            DataHandler handler = payload.getAnf();
            InputStream anf_is = handler.getInputStream();
            Integer processId = payload.getProcessId();
            String version = payload.getVersion();
            String nat_type = payload.getNativeType();
            String cpf_uri = payload.getCpfURI();
            ProcessDao.getInstance().storeAnnotation(annotationName, processId, version, cpf_uri, nat_type, anf_is, isNew);
            result.setCode(0);
            result.setMessage("");
        } catch (IOException e) {
            result.setCode(-1);
            result.setMessage(e.getMessage());
        } catch (SQLException e) {
            result.setCode(-1);
            result.setMessage(e.getMessage());
        } catch (ExceptionDao e) {
            result.setCode(-1);
            result.setMessage(e.getMessage());
        } catch (ExceptionAnntotationName e) {
            result.setCode(-3);
            result.setMessage(e.getMessage());
        }
        return res;
    }


    public StoreVersionOutputMsgType storeVersion(StoreVersionInputMsgType payload) {
        LOGGER.info("Executing operation storeVersion");
        StoreVersionOutputMsgType res = new StoreVersionOutputMsgType();
        ResultType result = new ResultType();
        res.setResult(result);
        EditSessionType editSession = new EditSessionType();
        editSession.setProcessId(payload.getEditSession().getProcessId());
        editSession.setCreationDate(payload.getEditSession().getCreationDate());
        editSession.setAnnotation(payload.getEditSession().getAnnotation());
        editSession.setDomain(payload.getEditSession().getDomain());
        editSession.setLastUpdate(payload.getEditSession().getLastUpdate());
        editSession.setNativeType(payload.getEditSession().getNativeType());
        editSession.setProcessName(payload.getEditSession().getProcessName());
        editSession.setUsername(payload.getEditSession().getUsername());
        editSession.setVersionName(payload.getEditSession().getVersionName());
        int editSessionCode = payload.getEditSessionCode();
        String cpf_uri = payload.getCpfURI();
        try {
            DataHandler handlernat = payload.getNative();
            InputStream native_is = handlernat.getInputStream();

            DataHandler handlercpf = payload.getCpf();
            InputStream cpf_is = handlercpf.getInputStream();

            DataHandler handleranf = payload.getAnf();
            InputStream anf_is = handleranf.getInputStream();
            ProcessDao.getInstance().storeVersion(editSessionCode, editSession, cpf_uri, native_is, cpf_is, anf_is);
            result.setCode(0);
            result.setMessage("");
        } catch (ExceptionDao ex) {
            result.setCode(-1);
            result.setMessage(ex.getMessage());
        } catch (SQLException ex) {
            result.setCode(-1);
            result.setMessage(ex.getMessage());
        } catch (IOException ex) {
            result.setCode(-1);
            result.setMessage(ex.getMessage());
        } catch (ExceptionStoreVersion ex) {
            result.setCode(-3);
            result.setMessage(ex.getMessage());
        } catch (ExceptionSyncNPF ex) {
            result.setCode(-1);
            result.setMessage(ex.getMessage());
        }
        return res;
    }


    public StoreNativeOutputMsgType storeNative(StoreNativeInputMsgType payload) {
        LOGGER.info("Executing operation storeNative");
        StoreNativeOutputMsgType res = new StoreNativeOutputMsgType();
        ResultType result = new ResultType();
        res.setResult(result);
        int processId = payload.getProcessId();
        String version = payload.getVersion();
        String nativeType = payload.getNativeType();

        try {
            DataHandler handler = payload.getNative();
            InputStream native_xml = handler.getInputStream();
            ProcessDao.getInstance().storeNative(nativeType, processId, version, native_xml);
            result.setCode(0);
            result.setMessage("");
        } catch (ExceptionDao ex) {
            result.setCode(-1);
            result.setMessage(ex.getMessage());
        } catch (SQLException ex) {
            result.setCode(-1);
            result.setMessage(ex.getMessage());
        } catch (IOException ex) {
            result.setCode(-1);
            result.setMessage(ex.getMessage());
        }
        return res;
    }

}
