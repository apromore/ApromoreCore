/*-
 * #%L
 * This file is part of "Apromore Core".
 * 
 * Copyright (C) 2016 - 2017 Queensland University of Technology.
 * %%
 * Copyright (C) 2018 - 2022 Apromore Pty Ltd.
 * %%
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Lesser Public License for more details.
 * 
 * You should have received a copy of the GNU General Lesser Public
 * License along with this program.  If not, see
 * <http://www.gnu.org/licenses/lgpl-3.0.html>.
 * #L%
 */

package org.apromore.plugin.similaritysearch.logic.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;

import org.apromore.dao.FolderRepository;
import org.apromore.dao.ProcessModelVersionRepository;
import org.apromore.dao.model.ProcessModelVersion;
import org.apromore.exception.ExceptionSearchForSimilar;
import org.apromore.plugin.DefaultParameterAwarePlugin;
import org.apromore.plugin.similaritysearch.logic.SimilarityService;
import org.apromore.portal.helper.Version;
import org.apromore.portal.model.ParameterType;
import org.apromore.portal.model.ParametersType;
import org.apromore.portal.model.ProcessVersionType;
import org.apromore.portal.model.ProcessVersionsType;
import org.apromore.portal.model.SummariesType;
import org.apromore.processmining.models.graphbased.directed.bpmn.BPMNDiagram;
import org.apromore.processmining.models.graphbased.directed.bpmn.BPMNDiagramFactory;
import org.apromore.service.FolderService;
import org.apromore.service.ProcessService;
import org.apromore.service.helper.UserInterfaceHelper;
import org.apromore.similaritysearch.tools.SearchForSimilarProcesses;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class SimilarityServiceImpl extends DefaultParameterAwarePlugin implements SimilarityService {

    private static final Logger LOGGER = LoggerFactory.getLogger(SimilarityServiceImpl.class);

    private ProcessModelVersionRepository processModelVersionRepo;
    private FolderService folderService;
    private ProcessService processService;
    private UserInterfaceHelper ui;

    /**
     * Default Constructor allowing Spring to Autowire for testing and normal use.
     *
     * @param processModelVersionRepository Process Model Version Repository.
     * @param folderRepository he folder repository.
     * @param uiHelper user interface helper
     */
    @Inject
    public SimilarityServiceImpl(final ProcessModelVersionRepository processModelVersionRepository, final FolderService folderService,
                                 final ProcessService processService, final UserInterfaceHelper uiHelper) {
        processModelVersionRepo = processModelVersionRepository;
        this.folderService = folderService;
        this.processService = processService;
        ui = uiHelper;
    }


    /**
     * @see
     *      {@inheritDoc}
     */
    @Override
    public SummariesType searchForSimilarProcesses(final Integer processId, final String branchName, final Boolean latestVersions,
            final Integer folderId, final String userId, final String method, final ParametersType params) throws ExceptionSearchForSimilar {
        LOGGER.debug("Starting Similarity Search...");

        ProcessVersionsType similarProcesses = null;
        ProcessModelVersion query = processModelVersionRepo.getLatestProcessModelVersion(processId, branchName);
        List<ProcessModelVersion> models = getProcessModelVersionsToSearchAgainst(folderId, userId, latestVersions);

        try {
            ToolboxData data = convertModelsToSearchData(models, query);
            data = getParametersForSearch(data, method, params);
            similarProcesses = performSearch(data);
            if (similarProcesses.getProcessVersion().size() == 0) {
                LOGGER.info("Process model " + query.getProcessBranch().getProcess().getId() + " version " +
                        query.getVersionNumber() + " probably faulty");
            }
        } catch (Exception se) {
            LOGGER.error("Failed to perform the similarity search.", se);
        }

        return ui.buildProcessSummaryList(userId, folderId, similarProcesses);
    }

    private List<ProcessModelVersion> getProcessModelVersionsToSearchAgainst(int folderId, String userGuid, Boolean latestVersions) {
        List<ProcessModelVersion> models;
        if (folderId == 0) {
            if (latestVersions) {
                models = processModelVersionRepo.getLatestProcessModelVersionsByUser(userGuid);
            } else {
                models = processModelVersionRepo.findAll();
            }
        } else {
            // We have a folder, Get all processes in the folder and in all folders underneath this one.
            models = new ArrayList<>();
            models.addAll(folderService.getProcessModelVersionByFolderUserRecursive(folderId, userGuid));
        }
        return models;
    }


    /* Responsible for getting all the Models and converting them to CPT internal format */
    private ToolboxData convertModelsToSearchData(List<ProcessModelVersion> models, ProcessModelVersion query) throws Exception {
        LOGGER.debug("Loading Data for search!");
        ToolboxData data = new ToolboxData();
        data.setOrigin(bpmnDiagram(query));
        for (ProcessModelVersion pmv : models) {
            data.addModel(pmv, bpmnDiagram(pmv));
        }

        LOGGER.debug("Data Loaded for all models!");
        return data;
    }

    private BPMNDiagram bpmnDiagram(ProcessModelVersion pmv) throws Exception {
        return BPMNDiagramFactory.newDiagramFromProcessText(processService.getBPMNRepresentation(
            pmv.getProcessBranch().getProcess().getName(),
            pmv.getProcessBranch().getProcess().getId(),
            pmv.getProcessBranch().getBranchName(),
            new Version(pmv.getVersionNumber())
        ));
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
        ProcessVersionType processVersion;
        ProcessVersionsType similarProcesses = new ProcessVersionsType();

        for (Map.Entry<ProcessModelVersion, BPMNDiagram> e : data.getModel().entrySet()) {
            similarity = SearchForSimilarProcesses.findProcessesSimilarity(
                    data.getOrigin(), e.getValue(), data.getAlgorithm(), data.getLabelthreshold(), data.getContextthreshold(),
                    data.getSkipnweight(), data.getSubnweight(), data.getSkipeweight());
            if (similarity >= data.getModelthreshold()) {
                processVersion = new ProcessVersionType();
                processVersion.setProcessId(e.getKey().getProcessBranch().getProcess().getId());
                processVersion.setVersionName(e.getKey().getProcessBranch().getBranchName());
                processVersion.setScore(similarity);
                similarProcesses.getProcessVersion().add(processVersion);
            }
        }
        return similarProcesses;
    }
}
