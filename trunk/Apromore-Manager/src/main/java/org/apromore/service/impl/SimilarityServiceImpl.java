package org.apromore.service.impl;

import java.util.List;
import javax.inject.Inject;

import org.apromore.dao.ProcessModelVersionRepository;
import org.apromore.dao.model.ProcessModelVersion;
import org.apromore.exception.ExceptionSearchForSimilar;
import org.apromore.graph.canonical.Canonical;
import org.apromore.model.ParameterType;
import org.apromore.model.ParametersType;
import org.apromore.model.ProcessSummariesType;
import org.apromore.model.ProcessVersionType;
import org.apromore.model.ProcessVersionsType;
import org.apromore.service.CanonicalConverter;
import org.apromore.service.ProcessService;
import org.apromore.service.SimilarityService;
import org.apromore.service.helper.UserInterfaceHelper;
import org.apromore.service.model.ToolboxData;
import org.apromore.toolbox.similaritySearch.tools.SearchForSimilarProcesses;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Implementation of the SimilarityService Contract.
 *
 * @author <a href="mailto:cam.james@gmail.com">Cameron James</a>
 */
@Service
@Transactional
public class SimilarityServiceImpl implements SimilarityService {

    private static final Logger LOGGER = LoggerFactory.getLogger(SimilarityServiceImpl.class);

    private ProcessModelVersionRepository processModelVersionRepo;
    private CanonicalConverter converter;
    private ProcessService processSrv;
    private UserInterfaceHelper ui;


    /**
     * Default Constructor allowing Spring to Autowire for testing and normal use.
     *
     * @param processModelVersionRepository Process Model Version Repository.
     * @param canonicalConverter Canonical Converter.
     * @param processService Repository Service.
     * @param uiHelper user interface helper
     */
    @Inject
    public SimilarityServiceImpl(final ProcessModelVersionRepository processModelVersionRepository,
            final CanonicalConverter canonicalConverter, final ProcessService processService, final UserInterfaceHelper uiHelper) {
        processModelVersionRepo = processModelVersionRepository;
        converter = canonicalConverter;
        processSrv = processService;
        ui = uiHelper;
    }


    /**
     * @see org.apromore.service.SimilarityService#SearchForSimilarProcesses(Integer, String, Boolean, String, org.apromore.model.ParametersType)
     *      {@inheritDoc}
     */
    public ProcessSummariesType SearchForSimilarProcesses(final Integer branchId, final String versionName, final Boolean latestVersions,
            final String method, final ParametersType params) throws ExceptionSearchForSimilar {
        LOGGER.debug("Starting Similarity Search...");

        ProcessVersionsType similarProcesses = null;
        ProcessModelVersion query = processModelVersionRepo.findProcessModelVersionByBranch(branchId, versionName);
        List<ProcessModelVersion> models = processModelVersionRepo.getLatestProcessModelVersions();
        try {
            ToolboxData data = getParametersForSearch(method, params);
            similarProcesses = performSearch(data, models, query);
            if (similarProcesses.getProcessVersion().size() == 0) {
                LOGGER.error("Process model " + query.getProcessBranch().getProcess().getId() + " version " +
                        query.getVersionNumber() + " probably faulty");
            }
        } catch (Exception se) {
            LOGGER.error("Failed to perform the similarity search.", se);
        }

        return ui.buildProcessSummaryList("", similarProcesses);
    }


    /* Loads the Parameters used for the Search */
    private ToolboxData getParametersForSearch(String method, ParametersType params) {
        ToolboxData data = new ToolboxData();
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
    private ProcessVersionsType performSearch(ToolboxData data, List<ProcessModelVersion> models, ProcessModelVersion query) {
        double similarity;
        ProcessVersionType processVersion;
        ProcessVersionsType similarProcesses = new ProcessVersionsType();
        Canonical original = converter.convert(processSrv.getCanonicalFormat(query));

        SearchForSimilarProcesses search = new SearchForSimilarProcesses();

        for (ProcessModelVersion pmv : models) {
            if (!query.equals(pmv)) {
                Canonical current = converter.convert(processSrv.getCanonicalFormat(pmv));

                similarity = search.findProcessesSimilarity(
                        original, current, data.getAlgorithm(), data.getLabelthreshold(), data.getContextthreshold(),
                        data.getSkipnweight(), data.getSubnweight(), data.getSkipeweight());

                if (similarity >= data.getModelthreshold()) {
                    processVersion = new ProcessVersionType();
                    processVersion.setProcessId(pmv.getProcessBranch().getProcess().getId());
                    processVersion.setVersionName(pmv.getProcessBranch().getBranchName());
                    processVersion.setScore(similarity);
                    similarProcesses.getProcessVersion().add(processVersion);
                }
            }
        }

        return similarProcesses;
    }

}
