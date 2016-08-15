/*
 * Copyright © 2009-2016 The Apromore Initiative.
 *
 * This file is part of "Apromore".
 *
 * "Apromore" is free software; you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 3 of the
 * License, or (at your option) any later version.
 *
 * "Apromore" is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this program.
 * If not, see <http://www.gnu.org/licenses/lgpl-3.0.html>.
 */

package org.apromore.service.pql.impl;

// Java 2 Standard
import java.io.IOException;
import java.sql.SQLException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

// Java 2 Enterprise
import javax.inject.Inject;

// Third party
import org.apache.commons.io.IOUtils;
import org.pql.api.IPQLAPI;
import org.pql.core.PQLTask;
import org.pql.index.IndexStatus;
import org.pql.query.PQLQueryResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

// First party
import org.apromore.dao.ProcessModelVersionRepository;
import org.apromore.dao.model.Process;
import org.apromore.dao.model.ProcessBranch;
import org.apromore.dao.model.ProcessModelVersion;
import org.apromore.exception.ExportFormatException;
import org.apromore.helper.Version;
import org.apromore.model.Detail;
import org.apromore.model.ExportFormatResultType;
import org.apromore.model.ProcessSummariesType;
import org.apromore.model.ProcessSummaryType;
import org.apromore.model.VersionSummaryType;
import org.apromore.plugin.DefaultPlugin;
import org.apromore.plugin.process.ProcessPlugin;
import org.apromore.plugin.property.RequestParameterType;
import org.apromore.service.pql.ExternalId;
import org.apromore.service.pql.PQLService;
import org.apromore.service.ProcessService;

/**
 * Created by corno on 22/07/2014.
 */
@Service
public class PQLServiceImpl extends DefaultPlugin implements PQLService, ProcessPlugin {

    private static final Logger LOGGER = LoggerFactory.getLogger(PQLServiceImpl.class);
    private final String PNML_NATIVE_TYPE = "PNML 1.3.2";

    @Inject private ProcessService processService;
    @Inject private ProcessModelVersionRepository processModelVersionRepository;
    @Inject private PqlBeanImpl pqlBean;

    private final List<ExternalId> queue = Collections.synchronizedList(new ArrayList<ExternalId>());

    /**
     * Store the text of a PNML model into the PQL database.
     *
     * This is a relatively quick, synchronous operation.  The model is not indexed immediately, but
     * becomes available for eventual (asynchronous) indexing by the PQL "bot" process.
     *
     * @param exportResult  a PNML model obtained from the {@link ProcessService#exportProcess} method
     * @param procId
     * @param version
     * @param branchName
     * @throws IOException  if the native PNML serialization couldn't be obtained from the <var>exportResult</var>
     * @throws SQLException  if the model couldn't be store into the PQL database
     */
    private void storeModel(ExportFormatResultType exportResult, Integer procId, Version version, String branchName) throws IOException, SQLException {

        byte[]     bytes      = IOUtils.toByteArray(exportResult.getNative().getInputStream());
        ExternalId externalId = new ExternalId(procId, branchName, version);
        int        internalId = pqlBean.getApi().storeModel(bytes, externalId.toString());

        LOGGER.info("SOUNDNESS: " + pqlBean.getApi().checkModel(internalId));
    }

    /** @inheritDoc */
    @Override public ProcessSummariesType query(String pql) throws QueryException {

        PQLQueryResult pqlQueryResult;
        try {
            pqlQueryResult = pqlBean.getApi().query(pql);

            // If the pql wasn't well-formed, throw the parse errors
            if (pqlQueryResult.getNumberOfParseErrors() != 0) {
                throw new QueryParsingException(pqlQueryResult.getParseErrorMessages());
            }
            assert pqlQueryResult.getNumberOfParseErrors() == 0;

            // Compose process summaries for the query results
            ProcessSummariesType processSummaries = new ProcessSummariesType();
            for (String result: pqlQueryResult.getSearchResults()) {
                ExternalId id = new ExternalId(result);

                ProcessModelVersion pmv = processModelVersionRepository.getProcessModelVersion(id.getProcessId(), id.getBranch(), id.getVersion().toString());
                ProcessBranch pb = pmv.getProcessBranch();
                Process p = pb.getProcess();

                VersionSummaryType versionSummary = new VersionSummaryType();
                versionSummary.setVersionNumber(pmv.getVersionNumber());

                ProcessSummaryType processSummary = new ProcessSummaryType();
                processSummary.setName(p.getName());
                processSummary.setId(p.getId());
                processSummary.setOriginalNativeType("BPMN 2.0");
                processSummary.setDomain(p.getDomain());
                processSummary.setRanking(p.getRanking());
                processSummary.setLastVersion(pmv.getVersionNumber());
                processSummary.setOwner(p.getUser().getUsername());
                processSummary.getVersionSummaries().add(versionSummary);

                processSummaries.getProcessSummary().add(processSummary);
            }
            return processSummaries;

        } catch (QueryParsingException e) {
            throw e;

        } catch (Exception e) {
            throw new QueryException("Unable to execute PQL query: " + pql, e);
        }
    }

    public List<String> runAPQLQuery(String queryPQL, List<String> IDs, String userID) {
        //Set<String> idNets=new HashSet<>();
        List<String> results = Collections.emptyList();
        IPQLAPI api=pqlBean.getApi();
        LOGGER.error("-----------PQLAPI: " + api);
        LOGGER.error("----------- query: " + queryPQL);
        LOGGER.error("-----------   IDs: " + IDs);
        LOGGER.error("-----------  user: " + userID);
        try {
            PQLQueryResult pqlQueryResult = api.query(queryPQL, new HashSet<>(IDs));
            if (pqlQueryResult.getNumberOfParseErrors() != 0) {
                results = pqlQueryResult.getParseErrorMessages();
            } else {//risultati
                LOGGER.error("-----------IDS PQLServiceImpl" + IDs);
                map=pqlQueryResult.getTaskMap();
                LinkedList<PQLTask> tasks=new LinkedList<>(map.values());
/*
                idNets=new HashSet<>(IDs);
                idNets=api.checkLastQuery(idNets);
                results.addAll(idNets);
*/
                results = new LinkedList<>(pqlQueryResult.getSearchResults());
                LOGGER.error("-----------QUERYAPQL ESATTA "+results);
            }
        } catch (Exception e) {
            LOGGER.error("-----------ERRORRE: " + e.toString());
            for (StackTraceElement ste : e.getStackTrace())
                LOGGER.info("ERRORE6: " + ste.getClassName() + " " + ste.getMethodName() + " " + ste.getLineNumber() + " " + ste.getFileName());
        }
        return results;
    }

    private Map<PQLTask,PQLTask> map = new HashMap<>();

    public List<Detail> getDetails() {
        List<Detail> details = new LinkedList<>();
        Detail detail;
        for(PQLTask task : map.keySet()){
            PQLTask taskTwo = map.get(task);

            detail= new Detail();
            detail.setLabelOne(task.getLabel());
            detail.setSimilarityLabelOne(""+task.getSimilarity());
            if (taskTwo.getSimilarLabels() != null) {
                detail.getDetail().addAll(taskTwo.getSimilarLabels());
            }
            details.add(detail);
        }
        return details;
    }

    /**
     * @param id  a process ID in the Apromore database (the "external ID" from PQL's point of view)
     * @return the indexing status of the identified process in the PQL index
     */
    @Override
    public IndexStatus getIndexStatus(ExternalId id) throws SQLException {
        IPQLAPI api = pqlBean.getApi();
        int internalId = api.getInternalID(id.toString());
        return api.getIndexStatus(internalId);
    }

    // Implementation of the ProcessPlugin interface

    /** {@inheritDoc} */
    public void bindToProcessService() {
        LOGGER.info("Binding to process service " + processService);
        IPQLAPI api = pqlBean.getApi();
        for (ProcessModelVersion pmv: processModelVersionRepository.findAll()) {
            int        processId  = pmv.getProcessBranch().getProcess().getId();
            String     branch     = pmv.getProcessBranch().getBranchName();
            Version    version    = new Version(pmv.getVersionNumber());
            ExternalId externalId = new ExternalId(processId, branch, version);

            try {
                int internalId = api.getInternalID(externalId.toString());
                IndexStatus status = (internalId == 0) ? IndexStatus.UNINDEXED : api.getIndexStatus(internalId);

                // If the external ID doesn't occur in the PQL index, queue it to be asynchronously indexed
                switch (status) {
                case UNINDEXED:
                    LOGGER.info("PQL index needs to be initialized for process with external ID " + externalId);
                    queue.add(externalId);
                    break;
                case INDEXING:    LOGGER.info("PQL index " + internalId + " already being indexed for process with external ID " + externalId);           break;
                case INDEXED:     LOGGER.info("PQL index " + internalId + " already present for process with external ID " + externalId);                 break;
                case CANNOTINDEX: LOGGER.info("PQL index " + internalId + " couldn't previously be indexed for process with external ID " + externalId);  break;
                default:          LOGGER.warn("PQL index " + internalId + " for process with external ID " + externalId + " has unknown status " + status);
                }
            } catch (SQLException e) {
                LOGGER.error("Unable to look up process with external ID " + externalId + " in the PQL index", e);
            }
        }
        LOGGER.info("Bound to process service " + processService);
    }

    /** {@inheritDoc} */
    public void unbindFromProcessService() {
        LOGGER.info("Unbound from process service " + processService);
    }

    /** {@inheritDoc} */
    public void processChanged(int processId, String branch, Version version) throws ProcessChangedException {
        ExternalId externalId = new ExternalId(processId, branch, version);
        LOGGER.info("PQL index needs to be updated for process with external ID " + externalId + " in process service " + processService);
        queue.add(externalId);
    }

    // Asynchronous indexing

    @Scheduled(cron="*/${pql.defaultBotSleepTime} * * * * ?")
    public void indexQueuedProcesses() {
        //LOGGER.info("Indexing queued processes");
        try {
            while(!queue.isEmpty()) {
                ExternalId externalId = queue.remove(0);
                indexProcess(externalId);
            }
        } catch (IndexOutOfBoundsException e) {
            // if there wasn't a 0th element of the queue, we're done
        }
        //LOGGER.info("Indexing queue empty");
    }

    private void indexProcess(ExternalId externalId) {
        //LOGGER.info("Indexing queued process " + externalId);
        IPQLAPI api = pqlBean.getApi();
        try {
            Set<RequestParameterType<?>> requestProperties = new HashSet<>();
            requestProperties.add(new RequestParameterType<>("isCpfTaskPnmlTransition",true));
            requestProperties.add(new RequestParameterType<>("isCpfTaskPnmlTrans",false));

            ExportFormatResultType exportResult = processService.exportProcess("dummy process name",
                externalId.getProcessId(), externalId.getBranch(), externalId.getVersion(),
                PNML_NATIVE_TYPE, null, false, requestProperties);

            byte[] bytes = IOUtils.toByteArray(exportResult.getNative().getInputStream());
            int internalId = api.storeModel(bytes, externalId.toString());
            LOGGER.info("Stored " + (api.checkModel(internalId) ? "sound" : "unsound") + " process " + externalId + " for PQL indexing as id " + internalId);

        } catch (ExportFormatException | IOException | SQLException e) {
            LOGGER.warn("Unable to index " + externalId + " for PQL indexing", e);

            // This is USUALLY because the model has been deleted.  In other cases, it's still relatively safe to just delete the associated index.
            try {
                LOGGER.info("Removing model with external ID " + externalId + " from PQL index");
                int internalId = api.getInternalID(externalId.toString());
                if(!api.deleteModel(internalId)) {
                    LOGGER.error("Failed to remove model with external ID " + externalId + " from PQL index");
                } else {
                    LOGGER.error("Removed model with external ID " + externalId + " and internal ID " + internalId + " from PQL index");
                }

            } catch (SQLException e2) {
                LOGGER.error("Exception while removing model " + externalId + " from PQL index", e2);
            }
        }
    }
}
