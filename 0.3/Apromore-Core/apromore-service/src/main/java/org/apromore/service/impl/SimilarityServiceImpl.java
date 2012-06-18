package org.apromore.service.impl;

import org.apromore.cpf.CanonicalProcessType;
import org.apromore.dao.ProcessModelVersionDao;
import org.apromore.dao.model.ProcessModelVersion;
import org.apromore.exception.ExceptionSearchForSimilar;
import org.apromore.exception.SerializationException;
import org.apromore.model.ParameterType;
import org.apromore.model.ParametersType;
import org.apromore.model.ProcessSummariesType;
import org.apromore.model.ProcessVersionType;
import org.apromore.model.ProcessVersionsType;
import org.apromore.service.CanoniserService;
import org.apromore.service.RepositoryService;
import org.apromore.service.SimilarityService;
import org.apromore.service.helper.UIHelper;
import org.apromore.service.model.ToolboxData;
import org.apromore.toolbox.similaritySearch.tools.SearchForSimilarProcesses;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map.Entry;

/**
 * Implementation of the SimilarityService Contract.
 *
 * @author <a href="mailto:cam.james@gmail.com">Cameron James</a>
 */
@Service("SimilarityService")
@Transactional(propagation = Propagation.REQUIRED)
public class SimilarityServiceImpl implements SimilarityService {

    private static final Logger LOGGER = LoggerFactory.getLogger(SimilarityServiceImpl.class);


    @Autowired @Qualifier("ProcessModelVersionDao")
    private ProcessModelVersionDao pmvDao;

    @Autowired @Qualifier("CanoniserService")
    private CanoniserService canSrv;
    @Autowired @Qualifier("RepositoryService")
    private RepositoryService rSrv;
    @Autowired @Qualifier("UIHelper")
    private UIHelper uiSrv;


    /**
     * @see org.apromore.service.SimilarityService#SearchForSimilarProcesses(Integer, String, Boolean, String, org.apromore.model.ParametersType)
     * {@inheritDoc}
     */
    public ProcessSummariesType SearchForSimilarProcesses(final Integer branchId, final String versionName, final Boolean latestVersions,
            final String method, final ParametersType params) throws ExceptionSearchForSimilar {
        ProcessVersionsType similarProcesses = null;
        ProcessModelVersion query = pmvDao.findProcessModelVersionByBranch(branchId, versionName);
        List<ProcessModelVersion> models = pmvDao.getAllProcessModelVersions(latestVersions);
        try {
            ToolboxData data = convertModelsToCPT(models, query);
            data = getParametersForSearch(data, method, params);
            similarProcesses = performSearch(data);
            if (similarProcesses.getProcessVersion().size() == 0) {
                //throw new ExceptionComputeSimilarity("Process model " + query.getProcessBranch().getProcess().getProcessId()
                //        + " version " + query.getVersionName() + " probably faulty");
                LOGGER.error("Process model " + query.getProcessBranch().getProcess().getProcessId() + " version " +
                        query.getVersionName() + " probably faulty");
            }
        } catch (SerializationException se) {
            LOGGER.error("Failed to convert the models into the Canonical Format.", se);
        }

        return uiSrv.buildProcessSummaryList("", similarProcesses);
    }



    /* Responsible for getting all the Models and converting them to CPT internal format */
    private ToolboxData convertModelsToCPT(List<ProcessModelVersion> models, ProcessModelVersion query) throws SerializationException {
        ToolboxData data = new ToolboxData();

        data.setOrigin(canSrv.serializeCPF(rSrv.getCanonicalFormat(query)));
        for (ProcessModelVersion pmv : models) {
            data.addModel(pmv, canSrv.serializeCPF(rSrv.getCanonicalFormat(pmv)));
        }

        return data;
    }

    /* Loads the Parameters used for the Search */
    private ToolboxData getParametersForSearch(ToolboxData data, String method, ParametersType params) {
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
            }
        }

        return data;
    }

    /* Does the similarity search. */
    private ProcessVersionsType performSearch(ToolboxData data) {
        double similarity;
        ProcessVersionType processVersion = null;
        ProcessVersionsType similarProcesses = new ProcessVersionsType();

        for (Entry<ProcessModelVersion, CanonicalProcessType> e : data.getModel().entrySet()) {
            similarity = SearchForSimilarProcesses.findProcessesSimilarity(
                    data.getOrigin(), e.getValue(), data.getAlgorithm(), data.getLabelthreshold(), data.getContextthreshold(),
                    data.getSkipnweight(), data.getSubnweight(), data.getSkipeweight());
            if (similarity >= data.getModelthreshold()) {
                processVersion = new ProcessVersionType();
                processVersion.setProcessId(e.getKey().getProcessBranch().getProcess().getProcessId());
                processVersion.setVersionName(e.getKey().getVersionName());
                processVersion.setScore(similarity);
                similarProcesses.getProcessVersion().add(processVersion);
            }
        }
        return similarProcesses;
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
