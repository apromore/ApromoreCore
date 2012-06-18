package org.apromore.service.impl;

import org.apromore.common.Constants;
import org.apromore.cpf.CanonicalProcessType;
import org.apromore.dao.ProcessModelVersionDao;
import org.apromore.dao.model.ProcessModelVersion;
import org.apromore.exception.ExceptionMergeProcess;
import org.apromore.exception.ImportException;
import org.apromore.exception.SerializationException;
import org.apromore.graph.JBPT.CPF;
import org.apromore.model.ParameterType;
import org.apromore.model.ParametersType;
import org.apromore.model.ProcessSummaryType;
import org.apromore.model.ProcessVersionIdType;
import org.apromore.model.ProcessVersionIdsType;
import org.apromore.service.CanoniserService;
import org.apromore.service.MergeService;
import org.apromore.service.RepositoryService;
import org.apromore.service.helper.UIHelper;
import org.apromore.service.model.ToolboxData;
import org.apromore.toolbox.similaritySearch.tools.MergeProcesses;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Implementation of the MergeService Contract.
 *
 * @author <a href="mailto:cam.james@gmail.com">Cameron James</a>
 */
@Service("MergeService")
@Transactional(propagation = Propagation.REQUIRED)
public class MergeServiceImpl implements MergeService {

    private static final Logger LOGGER = LoggerFactory.getLogger(MergeServiceImpl.class);

    @Autowired @Qualifier("ProcessModelVersionDao")
    private ProcessModelVersionDao pmvDao;

    @Autowired @Qualifier("CanoniserService")
    private CanoniserService canSrv;
    @Autowired @Qualifier("RepositoryService")
    private RepositoryService rSrv;
    @Autowired @Qualifier("UIHelper")
    private UIHelper uiSrv;



    /**
     * @see org.apromore.service.MergeService#mergeProcesses(String, String, String, String, String, org.apromore.model.ParametersType, org.apromore.model.ProcessVersionIdsType)
     * {@inheritDoc}
     */
    @Override
    public ProcessSummaryType mergeProcesses(String processName, String version, String domain, String username, String algo,
            ParametersType parameters, ProcessVersionIdsType ids) throws ExceptionMergeProcess {
        List<ProcessModelVersion> models = new ArrayList<ProcessModelVersion>(0);
        for (ProcessVersionIdType cpf : ids.getProcessVersionId()) {
            models.add(pmvDao.findProcessModelVersionByBranch(cpf.getProcessId(), cpf.getVersionName()));
        }

        ProcessSummaryType pst = null;
        try {
            ToolboxData data = convertModelsToCPT(models);
            data = getParametersForMerge(data, algo, parameters);
            CPF pg = canSrv.deserializeCPF(performMerge(data));

            SimpleDateFormat sf = new SimpleDateFormat(Constants.DATE_FORMAT);
            String created = sf.format(new Date());
            ProcessModelVersion pmv = rSrv.addProcessModel(processName, version, username, null, null, domain, "", created, created, pg);
            pst = uiSrv.createProcessSummary(processName, pmv.getProcessModelVersionId(), version, null, domain, created, created, username);
        } catch (SerializationException se) {
            LOGGER.error("Failed to convert the models into the Canonical Format.", se);
        } catch (ImportException ie) {
            LOGGER.error("Failed Import the newly merged model.", ie);
        }

        return pst;
    }


    /* Responsible for getting all the Models and converting them to CPT internal format */
    private ToolboxData convertModelsToCPT(List<ProcessModelVersion> models) throws SerializationException {
        ToolboxData data = new ToolboxData();

        for (ProcessModelVersion pmv : models) {
            data.addModel(pmv, canSrv.serializeCPF(rSrv.getCanonicalFormat(pmv)));
        }

        return data;
    }


    /* Loads the Parameters used for the Merge */
    private ToolboxData getParametersForMerge(ToolboxData data, String method, ParametersType params) {
        data.setAlgorithm(method);

        for (ParameterType p : params.getParameter()) {
            if (ToolboxData.MODEL_THRESHOLD.equals(p.getName())) {
                data.setModelthreshold(p.getValue());
            } else if (ToolboxData.LABEL_THRESHOLD.equals(p.getName())) {
                data.setLabelthreshold(p.getValue());
            } else if (ToolboxData.CONTEXT_THRESHOLD.equals(p.getName())) {
                data.setContextthreshold(p.getValue());
            } else if (ToolboxData.SKIP_N_WEIGHT.equals(p.getName())) {
                data.setSkipnweight(p.getValue());
            } else if (ToolboxData.SUB_N_WEIGHT.equals(p.getName())) {
                data.setSubnweight(p.getValue());
            } else if (ToolboxData.SKIP_E_WEIGHT.equals(p.getName())) {
                data.setSkipeweight(p.getValue());
            } else if (ToolboxData.REMOVE_ENT.equals(p.getName())) {
                data.setRemoveEntanglements(p.getValue() == 1);
            }
        }

        return data;
    }


    /* Does the merge. */
    private CanonicalProcessType performMerge(ToolboxData data) {
        ArrayList<CanonicalProcessType> models = new ArrayList<CanonicalProcessType>(data.getModel().values());
        return MergeProcesses.mergeProcesses(models, data.isRemoveEntanglements(), data.getAlgorithm(),
                data.getModelthreshold(), data.getLabelthreshold(), data.getContextthreshold(), data.getSkipnweight(),
                data.getSubnweight(), data.getSkipeweight());
    }






    /**
     * Set the Process Model Version DAO object for this class. Mainly for spring tests.
     * @param pmvDAOJpa the Process Model Version Dao.
     */
    public void setProcessModelVersionDao(ProcessModelVersionDao pmvDAOJpa) {
        pmvDao = pmvDAOJpa;
    }

    /**
     * Set the Canoniser Service for this class. Mainly for spring tests.
     * @param newCanSrv the service
     */
    public void setCanoniserService(CanoniserService newCanSrv) {
        this.canSrv = newCanSrv;
    }

    /**
     * Set the Repository Service for this class. Mainly for spring tests.
     * @param newRSrv the service
     */
    public void setRepositoryService(RepositoryService newRSrv) {
        this.rSrv = newRSrv;
    }

    /**
     * Set the Repository Service for this class. Mainly for spring tests.
     * @param newUISrv the service
     */
    public void setUIHelperService(UIHelper newUISrv) {
        this.uiSrv = newUISrv;
    }
}
