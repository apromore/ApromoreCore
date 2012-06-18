package org.apromore.dao.service;

import java.io.InputStream;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.activation.DataHandler;

import org.apromore.dao.DataAccessToolboxManager;
import org.apromore.dao.dao.ProcessDao;
import org.apromore.model.CanonicalType;
import org.apromore.model.CanonicalsType;
import org.apromore.model.MergedSource;
import org.apromore.model.ProcessSummariesType;
import org.apromore.model.ProcessSummaryType;
import org.apromore.model.ProcessVersionType;
import org.apromore.model.ReadCanonicalsInputMsgType;
import org.apromore.model.ReadCanonicalsOutputMsgType;
import org.apromore.model.ReadProcessSummariesOutputMsgType;
import org.apromore.model.ReadProcessSummaryInputMsgType;
import org.apromore.model.ResultType;
import org.apromore.model.StoreCpfInputMsgType;
import org.apromore.model.StoreCpfOutputMsgType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

/**
 * 
 */
@Service
public class DataAccessToolboxManagerImpl implements DataAccessToolboxManager {

    private static final Logger LOGGER = LoggerFactory.getLogger(DataAccessToolboxManagerImpl.class.getName());


	/* (non-Javadoc)
     * @see org.apromore.dao.DataAccessToolboxManager#readCanonicals(ReadCanonicalsInputMsgType  payload )*
     */
    public ReadCanonicalsOutputMsgType readCanonicals(ReadCanonicalsInputMsgType payload) {
        LOGGER.info("Executing operation readCanonicals");
        ReadCanonicalsOutputMsgType res = new ReadCanonicalsOutputMsgType();
        ResultType result = new ResultType();
        res.setResult(result);
        try {
        	List<ProcessVersionType> ids = payload.getProcessVersion();
        	Boolean latestVersions = payload.isLatestVersions();
        	List<CanonicalType> allCanonicals = ProcessDao.getInstance().getCanonicals(ids,latestVersions);
            CanonicalsType canonicals = new CanonicalsType();
            canonicals.getCanonical().addAll(allCanonicals);
            res.setCanonicals(canonicals);
            result.setCode(0);
            result.setMessage("");
        } catch (Exception ex) {
        	result.setCode(-1);
        	result.setMessage(ex.getMessage());
        }
        return res;
    }

    /* (non-Javadoc)
     * @see org.apromore.dao.DataAccessToolboxManager#storeCpf(StoreCpfInputMsgType  payload )*
     */
    public StoreCpfOutputMsgType storeCpf(StoreCpfInputMsgType payload) {
        LOGGER.info("Executing operation storeCpf");
        StoreCpfOutputMsgType res = new StoreCpfOutputMsgType();
        ResultType result = new ResultType();
        res.setResult(result);
        String processName = payload.getProcessName();
        String versionName = payload.getVersion();
        String domain = payload.getDomain();
        String username = payload.getUsername();
        Map<Integer,String> sources = new HashMap();
        for (MergedSource merged : payload.getSources().getMergedSource()) {
        	sources.put(merged.getProcessId(), merged.getVersionName());
        }    
        DataHandler handler = payload.getCpf();
        try {
            InputStream cpf_is = handler.getInputStream();
            ProcessSummaryType process =
            	ProcessDao.getInstance().storeCpf(processName, versionName, 
            			domain, username, cpf_is, newCpfURI(), sources);
            res.setProcessSummary(process);
            result.setCode(0);
            result.setMessage("");
        } catch (Exception ex) {
        	result.setCode(-1);
        	result.setMessage(ex.getMessage());
        }
        return res;
    }
    /**
	 * Generate a cpf uri for version of processId
	 */
	private String newCpfURI() {
		DateFormat dateFormat = new SimpleDateFormat("yyyyMMddHHmmsSSS");
		Date date = new Date();
		return dateFormat.format(date);
	}
}
