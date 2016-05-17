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
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

// Java 2 Enterprise
import javax.inject.Inject;

// Third party
import org.apache.commons.io.IOUtils;
import org.pql.api.IPQLAPI;
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
import org.apromore.model.ExportFormatResultType;
import org.apromore.model.ProcessSummariesType;
import org.apromore.model.ProcessSummaryType;
import org.apromore.model.VersionSummaryType;
import org.apromore.plugin.DefaultPlugin;
import org.apromore.plugin.process.ProcessPlugin;
import org.apromore.plugin.property.RequestParameterType;
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
                String[] fields = result.split("/", 3);

                ProcessModelVersion pmv = processModelVersionRepository.getProcessModelVersion(Integer.parseInt(fields[0]), fields[1], fields[2]);
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

    /**
     * @param id  a process ID in the Apromore database (the "external ID" from PQL's point of view)
     * @return the indexing status of the identified process in the PQL index
     */
    @Override
    public IndexStatus getIndexStatus(String id) throws SQLException {
        IPQLAPI api = pqlBean.getApi();
        int internalId = api.getInternalID(id);
        return api.getIndexStatus(internalId);
    }

    // Implementation of the ProcessPlugin interface

    /** {@inheritDoc} */
    public void bindToProcessService() {
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
        try {
            Set<RequestParameterType<?>> requestProperties = new HashSet<>();
            requestProperties.add(new RequestParameterType<>("isCpfTaskPnmlTransition",true));
            requestProperties.add(new RequestParameterType<>("isCpfTaskPnmlTrans",false));

            ExportFormatResultType exportResult = processService.exportProcess("dummy process name",
                externalId.getProcessId(), externalId.getBranch(), externalId.getVersion(),
                PNML_NATIVE_TYPE, null, false, requestProperties);

            byte[] bytes = IOUtils.toByteArray(exportResult.getNative().getInputStream());
            IPQLAPI api = pqlBean.getApi();
            int internalId = api.storeModel(bytes, externalId.toString());
            LOGGER.info("Stored " + (api.checkModel(internalId) ? "sound" : "unsound") + " process " + externalId + " for PQL indexing as id " + internalId);

        } catch (ExportFormatException | IOException | SQLException e) {
            LOGGER.warn("Unable to index " + externalId + " for PQL indexing", e);
        }
    }

    /**
     * Hold the composite key of a process model version
     */
    private static class ExternalId {
        private int processId;
        private String branch;
        private Version version;

        ExternalId(int processId, String branch, Version version) {
            this.processId = processId;
            this.branch    = branch;
            this.version   = version;
        }

        int     getProcessId() { return processId; }
        String  getBranch()    { return branch; }
        Version getVersion()   { return version; }

        @Override
        public String toString() {
            return processId + "/" + branch + "/" + version;
        }
    }
}
