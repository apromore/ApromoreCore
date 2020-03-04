/*
 * This file is part of "Apromore".
 *
 * Copyright (C) 2016 - 2017 Queensland University of Technology.
 * Copyright (C) 2018, 2020 The University of Melbourne.
 *
 * "Apromore" is free software; you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 3 of the
 * License, or (at your option) any later version.
 *
 * "Apromore" is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty
 * of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this program.
 * If not, see <http://www.gnu.org/licenses/lgpl-3.0.html>.
 */

package org.apromore.plugin.merge.logic.impl;

import org.apromore.common.Constants;
import org.apromore.cpf.CanonicalProcessType;
import org.apromore.dao.ProcessModelVersionRepository;
import org.apromore.dao.model.ProcessModelVersion;
import org.apromore.exception.ExceptionMergeProcess;
import org.apromore.exception.ImportException;
import org.apromore.exception.SerializationException;
import org.apromore.helper.Version;
import org.apromore.model.*;
import org.apromore.plugin.DefaultParameterAwarePlugin;
import org.apromore.plugin.merge.logic.MergeService;
import org.apromore.service.CanoniserService;
import org.apromore.service.ProcessService;
import org.apromore.service.helper.UserInterfaceHelper;
import org.apromore.service.model.CanonisedProcess;
import org.apromore.service.model.ToolboxData;
import org.apromore.similaritysearch.tools.MergeProcesses;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import javax.inject.Inject;
import javax.xml.bind.JAXBException;
import java.io.ByteArrayInputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Implementation of the SimilarityService Contract.
 *
 * @author <a href="mailto:raffaele.conforti@unimelb.edu.au">Raffaele Conforti</a>
 */
@Service
@Transactional(propagation = Propagation.REQUIRED, isolation = Isolation.DEFAULT, readOnly = true, rollbackFor = Exception.class)
public class MergeServiceImpl extends DefaultParameterAwarePlugin implements MergeService {

    private static final Logger LOGGER = LoggerFactory.getLogger(MergeServiceImpl.class);

    private ProcessModelVersionRepository processModelVersionRepo;
    private CanoniserService canoniserSrv;
    private ProcessService processSrv;
    private UserInterfaceHelper ui;


    /**
     * Default Constructor allowing Spring to Autowire for testing and normal use.
     *
     * @param processModelVersionRepository Annotation Repository.
     * @param canoniserService              Canoniser Service.
     * @param processService                Native Type repository.
     */
    @Inject
    public MergeServiceImpl(final ProcessModelVersionRepository processModelVersionRepository, final CanoniserService canoniserService,
                            final ProcessService processService, final UserInterfaceHelper uiHelper) {
        processModelVersionRepo = processModelVersionRepository;
        canoniserSrv = canoniserService;
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
            ToolboxData data = convertModelsToCPT(models);
            data = getParametersForMerge(data, algo, parameters);

            CanonisedProcess cp = new CanonisedProcess();

            cp.setCpt(performMerge(data));
            cp.setCpf(new ByteArrayInputStream(canoniserSrv.CPFtoString(cp.getCpt()).getBytes()));

            SimpleDateFormat sf = new SimpleDateFormat(Constants.DATE_FORMAT);
            String created = sf.format(new Date());

            // This fails as we need to specify a native type and pass in the model.
            Version importVersion = new Version(1, 0);

            ProcessModelVersion pmv = processSrv.importProcess(username, folderId, processName, importVersion, null, cp, domain, "", created, created, makePublic);

            return ui.createProcessSummary(pmv.getProcessBranch().getProcess(), pmv.getProcessBranch(), pmv,
                    "", domain, pmv.getCreateDate(), pmv.getLastUpdateDate(), username, makePublic);

        } catch (SerializationException se) {
            LOGGER.error("Failed to convert the models into the Canonical Format.", se);
        } catch (ImportException | JAXBException ie) {
            LOGGER.error("Failed Import the newly merged model.", ie);
        }

        return null;
    }


    /* Responsible for getting all the Models and converting them to CPT internal format */
    private ToolboxData convertModelsToCPT(List<ProcessModelVersion> models) throws SerializationException {
        ToolboxData data = new ToolboxData();

        for (ProcessModelVersion pmv : models) {
            data.addModel(pmv, processSrv.getCanonicalFormat(pmv));
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
        ArrayList<CanonicalProcessType> models = new ArrayList<>(data.getModel().values());
        return MergeProcesses.mergeProcesses(models, data.isRemoveEntanglements(), data.getAlgorithm(),
                data.getModelthreshold(), data.getLabelthreshold(), data.getContextthreshold(), data.getSkipnweight(),
                data.getSubnweight(), data.getSkipeweight());
    }
}
