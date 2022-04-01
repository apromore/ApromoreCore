/*-
 * #%L
 * This file is part of "Apromore Core".
 * 
 * Copyright (C) 2013 - 2016 Reina Uba.
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

package org.apromore.plugin.merge.logic.impl;

import java.io.ByteArrayInputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.inject.Inject;

import org.apromore.common.Constants;
import org.apromore.dao.ProcessModelVersionRepository;
import org.apromore.dao.model.ProcessModelVersion;
import org.apromore.exception.ExceptionMergeProcess;
import org.apromore.plugin.DefaultParameterAwarePlugin;
import org.apromore.plugin.merge.logic.MergeService;
import org.apromore.portal.helper.Version;
import org.apromore.portal.model.ParameterType;
import org.apromore.portal.model.ParametersType;
import org.apromore.portal.model.ProcessSummaryType;
import org.apromore.portal.model.ProcessVersionIdType;
import org.apromore.portal.model.ProcessVersionIdsType;
import org.apromore.processmining.models.graphbased.directed.bpmn.BPMNDiagram;
import org.apromore.processmining.models.graphbased.directed.bpmn.BPMNDiagramFactory;
import org.apromore.processmining.plugins.bpmn.plugins.BpmnLayoutPlugin;
import org.apromore.service.ProcessService;
import org.apromore.service.helper.UserInterfaceHelper;
import org.apromore.similaritysearch.tools.MergeProcesses;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(propagation = Propagation.REQUIRED, isolation = Isolation.DEFAULT, rollbackFor = Exception.class)
public class MergeServiceImpl extends DefaultParameterAwarePlugin implements MergeService {

    private static final Logger LOGGER = LoggerFactory.getLogger(MergeServiceImpl.class);

    private ProcessModelVersionRepository processModelVersionRepo;
//    private CanoniserService canoniserSrv;
    private ProcessService processSrv;
    private UserInterfaceHelper ui;


    /**
     * Default Constructor allowing Spring to Autowire for testing and normal use.
     *
     * @param processModelVersionRepository Annotation Repository.
     * @param processService                Native Type repository.
     */
    @Inject
    public MergeServiceImpl(final ProcessModelVersionRepository processModelVersionRepository, final ProcessService processService, final UserInterfaceHelper uiHelper) {
        processModelVersionRepo = processModelVersionRepository;
        processSrv = processService;
        ui = uiHelper;
    }


    @Override
    @Transactional(readOnly = false)
    public ProcessSummaryType mergeProcesses(String processName, String version, String domain, String username, String algo, Integer folderId,
                                             ParametersType parameters, ProcessVersionIdsType ids, final boolean makePublic) throws ExceptionMergeProcess {
        List<ProcessModelVersion> models = new ArrayList<>();
        for (ProcessVersionIdType cpf : ids.getProcessVersionId()) {
            models.add(processModelVersionRepo.getProcessModelVersion(cpf.getProcessId(), cpf.getBranchName(), cpf.getVersionNumber()));
        }

        try {
            ToolboxData data = convertModelsToMergeData(models);
            data = getParametersForMerge(data, algo, parameters);
            BPMNDiagram mergeResult = performMerge(data);
            String processText = BpmnLayoutPlugin.addLayout(mergeResult, "");
            
            SimpleDateFormat sf = new SimpleDateFormat(Constants.DATE_FORMAT);
            String created = sf.format(new Date());
            Version importVersion = new Version(1, 0);

            ProcessModelVersion pmv = processSrv.importProcess(username, folderId, processName, importVersion, 
                    Constants.NATIVE_TYPE, new ByteArrayInputStream(processText.getBytes()), 
                    "", "", created, created, makePublic);

            return ui.createProcessSummary(pmv.getProcessBranch().getProcess(), pmv.getProcessBranch(), pmv,
                    "", domain, pmv.getCreateDate(), pmv.getLastUpdateDate(), username, makePublic);

        } catch (Exception e) {
            LOGGER.error("Failed to merge process models", e);
        }

        return null;
    }


    private ToolboxData convertModelsToMergeData(List<ProcessModelVersion> models) throws Exception {
        ToolboxData data = new ToolboxData();
        for (ProcessModelVersion pmv : models) {
            data.addModel(pmv, BPMNDiagramFactory.newDiagramFromProcessText(processSrv.getBPMNRepresentation(
                pmv.getProcessBranch().getProcess().getName(),
                pmv.getProcessBranch().getProcess().getId(),
                pmv.getProcessBranch().getBranchName(),
                new Version(pmv.getVersionNumber())
            )));
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
    private BPMNDiagram performMerge(ToolboxData data) {
        return MergeProcesses.mergeProcesses(data.getModel().values(), data.isRemoveEntanglements(), data.getAlgorithm(),
                data.getModelthreshold(), data.getLabelthreshold(), data.getContextthreshold(), data.getSkipnweight(),
                data.getSubnweight(), data.getSkipeweight());
    }
}
