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

import org.apache.commons.io.IOUtils;
import org.apromore.canoniser.Canoniser;
import org.apromore.canoniser.provider.CanoniserProvider;
import org.apromore.dao.ProcessModelVersionRepository;
import org.apromore.dao.dataObject.FolderTreeNode;
import org.apromore.dao.model.*;
import org.apromore.dao.model.Process;
import org.apromore.exception.ExportFormatException;
import org.apromore.helper.Version;
import org.apromore.model.Detail;
import org.apromore.model.ExportFormatResultType;
import org.apromore.model.ProcessSummaryType;
import org.apromore.model.VersionSummaryType;
import org.apromore.plugin.DefaultPlugin;
import org.apromore.plugin.process.ProcessPlugin;
import org.apromore.plugin.property.RequestParameterType;
import org.apromore.service.CanoniserService;
import org.apromore.service.PluginService;
import org.apromore.service.pql.PQLService;
import org.apromore.service.ProcessService;
import org.apromore.service.UserService;
import org.apromore.service.WorkspaceService;
import org.apromore.service.helper.UserInterfaceHelper;
import org.jbpt.petri.Flow;
import org.jbpt.petri.INetSystem;
import org.jbpt.petri.Marking;
import org.jbpt.petri.Node;
import org.jbpt.petri.Place;
import org.jbpt.petri.Transition;
import org.jbpt.petri.io.PNMLSerializer;
import org.pql.api.IPQLAPI;
import org.pql.core.PQLTask;
import org.pql.index.IndexStatus;
import org.pql.query.PQLQueryResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import javax.activation.DataHandler;
import javax.inject.Inject;
import java.io.InputStream;
import java.io.IOException;
import java.sql.SQLException;
import java.util.*;

/**
 * Created by corno on 22/07/2014.
 */
@Service
public class PQLServiceImpl extends DefaultPlugin implements PQLService, ProcessPlugin {
    private static final Logger LOGGER = LoggerFactory.getLogger(PQLServiceImpl.class);
    private final String PNML_NATIVE_TYPE = "PNML 1.3.2";
    private final String admin = "admin";

    private String pnmlCanoniser;
    private final List<ExternalId> queue = Collections.synchronizedList(new ArrayList<ExternalId>());

    @Inject private WorkspaceService workspaceService;
    @Inject private ProcessService processService;
    @Inject private PluginService pluginService;
    @Inject private CanoniserService canoniserService;
    @Inject private UserService userService;
    @Inject private UserInterfaceHelper helperService;
    @Inject @Qualifier("canoniserProvider") private CanoniserProvider canoniserProviderService;
    @Inject private ProcessModelVersionRepository processModelVersionRepository;

    @Inject private LolaDirImpl lolaDir;
    @Inject private MySqlBeanImpl mySqlBean;
    @Inject private PGBeanImpl pgBean;

    @Inject private PqlBeanImpl pqlBean;

    @Override
    public void indexAllModels() {
        LinkedList<FolderTreeNode> root = null;
        LinkedList<GroupProcess> processes = new LinkedList<>();

        try {
            String userID = userService.findUserByLogin(admin).getRowGuid();
            root = new LinkedList<>(workspaceService.getWorkspaceFolderTree(userID));

            FolderTreeNode head;
            Integer folderId = 0;

            processes.addAll(workspaceService.getGroupProcesses(userID, folderId));
            indexModels(processes, folderId, userID);
            processes.clear();

            while (!root.isEmpty()) {
                head = root.removeFirst();
                folderId = head.getId();
                processes.addAll(workspaceService.getGroupProcesses(userID, folderId));
                indexModels(processes, folderId, userID);
                root.addAll(head.getSubFolders());
                processes.clear();
            }

        } catch (Exception e) {
            for (StackTraceElement ste : e.getStackTrace())
                LOGGER.info("ERRORE1: " + e.getMessage() + ste.getClassName() + " " + ste.getMethodName() + " " + ste.getLineNumber() + " " + ste.getFileName());
        }
    }

    private String getPNMLCanoniser() {
        if (pnmlCanoniser != null) {
            LOGGER.info("RECALLED " + pnmlCanoniser);
            return pnmlCanoniser;
        }

        for(Canoniser canoniser : canoniserProviderService.listAll()){
            if(canoniser.getNativeType().startsWith("PNML")) {
                pnmlCanoniser=canoniser.getNativeType();
                LOGGER.info("INITIALIZED " + pnmlCanoniser);
                return pnmlCanoniser;
            }
        }

        throw new RuntimeException("Unable to find a canoniser for PNML");
    }

    private Set<RequestParameterType<?>> readPluginProperties(String parameterCategory) {
        Set<RequestParameterType<?>> requestProperties = new HashSet<>();
        requestProperties.add(new RequestParameterType<>("isCpfTaskPnmlTransition",true));
        requestProperties.add(new RequestParameterType<>("isCpfTaskPnmlTrans",false));
        try {
        } catch (Exception e) {
            LOGGER.error("-----------ERRORRE PluginProperties: " + e.toString());
            for (StackTraceElement ste : e.getStackTrace())
                LOGGER.info("ERRORE4: " + ste.getClassName() + " " + ste.getMethodName() + " " + ste.getLineNumber() + " " + ste.getFileName());
        }

        return requestProperties;
    }

    private void indexModels(LinkedList<GroupProcess> processes, Integer folderId, String userID) {

        try {
            for (GroupProcess process : processes) {
                Process currentProc = process.getProcess();
                String procName = currentProc.getName();
                Integer procId = currentProc.getId();
                Version version = null;
                String nativeType = currentProc.getNativeType().getNatType();
                String annotationName = null;
                boolean withAnnotation = false;
                Set<RequestParameterType<?>> canoniserProperties = readPluginProperties(Canoniser.DECANONISE_PARAMETER);

                for (ProcessSummaryType pst : helperService.buildProcessSummaryList(userID, folderId, null).getProcessSummary()) {
                    if (pst.getName().equals(procName)) {
                    for (VersionSummaryType vst : pst.getVersionSummaries()) {

                            version = new Version(vst.getVersionNumber());
                            if (version != null && canoniserProperties != null) {
                                LOGGER.info("PROCESS: " + procName + " " + procId + " " + vst.getName() + " " + version.toString() + " " + nativeType);
                                ExportFormatResultType exportResult = this.processService.exportProcess(procName, procId, vst.getName(), version, getPNMLCanoniser(), annotationName, withAnnotation, canoniserProperties);
				storeModel(exportResult, procId, version, vst.getName());
                            }
                        }
                    }
                }
            }
        } catch (Exception e) {
            LOGGER.error("Unable to index models in folder " + folderId, e);
        }
    }

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

        byte[] bytes = IOUtils.toByteArray(exportResult.getNative().getInputStream());

        PNMLSerializer pnmlSerializer = new PNMLSerializer();

        // External ids are strings, e.g. "17/1.0/MAIN"
        String externalId = procId.toString() + "/" + version.toString() + "/" + branchName;

        IPQLAPI api = pqlBean.getApi();
        int internalId = api.storeModel(bytes, externalId);

        LOGGER.info("SOUNDNESS: " + api.checkModel(internalId));
    }

    @Override
    public List<String> runAPQLQuery(String queryPQL, List<String> IDs, String userID) {
        Set<String> idNets=new HashSet<>();
        List<String> results = Collections.emptyList();
        IPQLAPI api=pqlBean.getApi();
        LOGGER.error("-----------PQLAPI: " + api);
        LOGGER.error("----------- query: " + queryPQL);
        LOGGER.error("-----------   IDs: " + IDs);
        LOGGER.error("-----------  user: " + userID);
        try {
            PQLQueryResult pqlQueryResult = api.query(queryPQL /*, new HashSet<>(IDs) */);
            if (pqlQueryResult.getNumberOfParseErrors() != 0) {
                results = pqlQueryResult.getParseErrorMessages();
            } else {//risultati
                LOGGER.error("-----------IDS PQLServiceImpl" + IDs);
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

    /** {@inheritDoc} */
    @Override
    public boolean isIndexingEnabled() {
        return pqlBean.isIndexingEnabled();
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
            ExportFormatResultType exportResult = processService.exportProcess("dummy process name",
                externalId.getProcessId(), externalId.getBranch(), externalId.getVersion(),
                PNML_NATIVE_TYPE, null, false, readPluginProperties(Canoniser.DECANONISE_PARAMETER));

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

