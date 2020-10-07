/*-
 * #%L
 * This file is part of "Apromore Core".
 * 
 * Copyright (C) 2016 - 2017 Queensland University of Technology.
 * %%
 * Copyright (C) 2018 - 2020 Apromore Pty Ltd.
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
package org.apromore.service.impl;

import org.apromore.apmlog.APMLog;
import org.apromore.common.ConfigBean;
import org.apromore.common.Constants;
import org.apromore.dao.FolderRepository;
import org.apromore.dao.GroupLogRepository;
import org.apromore.dao.GroupRepository;
import org.apromore.dao.LogRepository;
import org.apromore.dao.model.Group;
import org.apromore.dao.model.GroupLog;
import org.apromore.dao.model.Log;
import org.apromore.dao.model.User;
import org.apromore.exception.NotAuthorizedException;
import org.apromore.exception.UserNotFoundException;
import org.apromore.portal.model.ExportLogResultType;
import org.apromore.portal.model.PluginMessages;
import org.apromore.portal.model.SummariesType;
import org.apromore.service.EventLogService;
import org.apromore.service.UserMetadataService;
import org.apromore.service.UserService;
import org.apromore.service.helper.UserInterfaceHelper;
import org.deckfour.xes.extension.std.XConceptExtension;
import org.deckfour.xes.factory.XFactory;
import org.deckfour.xes.factory.XFactoryRegistry;
import org.deckfour.xes.in.*;
import org.deckfour.xes.model.XLog;
import org.deckfour.xes.out.XSerializer;
import org.deckfour.xes.out.XesXmlGZIPSerializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import javax.activation.DataHandler;
import javax.inject.Inject;
import javax.mail.util.ByteArrayDataSource;
import java.io.*;
import java.util.*;

//import javax.annotation.Resource;

/**
 * Implementation of the ProcessService Contract.
 *
 * @author <a href="mailto:cam.james@gmail.com">Cameron James</a>
 */
@Service
@Transactional(propagation = Propagation.REQUIRED, isolation = Isolation.DEFAULT, readOnly = true, rollbackFor =
        Exception.class)
//@EnableCaching
public class EventLogServiceImpl implements EventLogService {

    private static final Logger LOGGER = LoggerFactory.getLogger(EventLogServiceImpl.class);
    private LogRepository logRepo;
    private GroupRepository groupRepo;
    private GroupLogRepository groupLogRepo;
    private FolderRepository folderRepo;
    private UserService userSrv;
    private UserInterfaceHelper ui;
    private File logsDir;
    private UserMetadataService userMetadataService;
    private TemporaryCacheService tempCacheService;

//    @javax.annotation.Resource
//    private Set<EventLogPlugin> eventLogPlugins;

    /**
     * Default Constructor allowing Spring to Autowire for testing and normal use.
     *
     * @param logRepository Log repository.
     * @param ui            User Interface Helper.
     */
    @Inject
    public EventLogServiceImpl(final LogRepository logRepository, final GroupRepository groupRepository,
                               final GroupLogRepository groupLogRepository, final FolderRepository folderRepo,
                               final UserService userSrv, final UserInterfaceHelper ui,
                               final ConfigBean configBean,
                               final UserMetadataService userMetadataService,final TemporaryCacheService temporaryCacheService) {
        this.logRepo = logRepository;
        this.groupRepo = groupRepository;
        this.groupLogRepo = groupLogRepository;
        this.folderRepo = folderRepo;
        this.userSrv = userSrv;
        this.ui = ui;
        this.logsDir = new File(configBean.getLogsDir());
        this.userMetadataService = userMetadataService;
        this.tempCacheService=temporaryCacheService;
    }

    public static XLog importFromStream(XFactory factory, InputStream is, String extension) throws Exception {
        XParser parser;
        parser = null;
        if (extension.endsWith("mxml")) {
            parser = new XMxmlParser(factory);
        } else if (extension.endsWith("mxml.gz")) {
            parser = new XMxmlGZIPParser(factory);
        } else if (extension.endsWith("xes")) {
            parser = new XesXmlParser(factory);
        } else if (extension.endsWith("xes.gz")) {
            parser = new XesXmlGZIPParser(factory);
        }

        Collection<XLog> logs;
        try {
            logs = parser.parse(is);
        } catch (Exception e) {
            e.printStackTrace();
            logs = null;
        }
        if (logs == null) {
            // try any other parser
            for (XParser p : XParserRegistry.instance().getAvailable()) {
                if (p == parser) {
                    continue;
                }
                try {
                    logs = p.parse(is);
                    if (logs.size() > 0) {
                        break;
                    }
                } catch (Exception e1) {
                    // ignore and move on.
                    logs = null;
                }
            }
        }

        // log sanity checks;
        // notify user if the log is awkward / does miss crucial information
        if (logs == null || logs.size() == 0) {
            throw new Exception("No processes contained in log!");
        }

        XLog log = logs.iterator().next();
        if (XConceptExtension.instance().extractName(log) == null) {
            XConceptExtension.instance().assignName(log, "Anonymous log imported from ");
        }

        if (log.isEmpty()) {
            throw new Exception("No process instances contained in log!");
        }

        return log;

    }

    @Override
    public SummariesType readLogSummaries(Integer folderId, String searchExpression) {
        return null;
    }

    /**
     * Import serialisations into Apromore application.
     *
     * @param username       The user doing the importing.
     * @param folderId       The folder we are saving the process in.
     * @param logName        the name of the process being imported.
     * @param inputStreamLog
     * @param extension
     * @param domain         the domain of the model
     * @param created        the time created
     * @param publicModel    is this a public model?
     * @return
     * @throws Exception
     */
    @Override
    public Log importLog(String username, Integer folderId, String logName, InputStream inputStreamLog,
                         String extension, String domain, String created, boolean publicModel) throws Exception {
        User user = userSrv.findUserByLogin(username);

        XFactory factory = XFactoryRegistry.instance().currentDefault();
        LOGGER.info("Import XES log " + logName + " using " + factory.getClass());
        XLog xLog = importFromStream(factory, inputStreamLog, extension);
        String path = tempCacheService.storeProcessLog(folderId, logName, xLog, user.getId(), domain, created);
        Log log = new Log();
        log.setFolder(folderRepo.findUniqueByID(folderId));
        log.setDomain(domain);
        log.setCreateDate(created);
        log.setFilePath(path);
        updateLogName(log, logName);
        log.setRanking("");
        log.setUser(user);

        Set<GroupLog> groupLogs = log.getGroupLogs();

        // Add the user's personal group
        groupLogs.add(new GroupLog(user.getGroup(), log, true, true, true));

        // Add the public group
        if (publicModel) {
            Group publicGroup = groupRepo.findPublicGroup();
            if (publicGroup == null) {
                LOGGER.warn("No public group present in repository");
            } else {
                groupLogs.add(new GroupLog(publicGroup, log, true, true, false));
            }
        }

        log.setGroupLogs(groupLogs);

        // Perform the update
        logRepo.saveAndFlush(log);

        return log;
    }

    @Override
    public void updateLogMetaData(Integer logId, String logName, boolean isPublic) {
        Log log = logRepo.findUniqueByID(logId);
        updateLogName(log, logName);

        Set<GroupLog> groupLogs = log.getGroupLogs();
        Set<GroupLog> publicGroupLogs = filterPublicGroupLogs(groupLogs);

        if (publicGroupLogs.isEmpty() && isPublic) {
            groupLogs.add(new GroupLog(groupRepo.findPublicGroup(), log, true, true, false));
            log.setGroupLogs(groupLogs);

        } else if (!publicGroupLogs.isEmpty() && !isPublic) {
            groupLogs.removeAll(publicGroupLogs);
            log.setGroupLogs(groupLogs);
        }

        logRepo.saveAndFlush(log);
    }

    private void updateLogName(Log log, String newName) {
        String file_name = log.getFilePath() + "_" + log.getName() + ".xes.gz";
        File file = new File(logsDir, file_name);
        String new_file_name = log.getFilePath() + "_" + newName + ".xes.gz";
        file.renameTo(new File(logsDir, new_file_name));
        log.setName(newName);
    }

    @Override
    public boolean isPublicLog(Integer logId) {
        return !filterPublicGroupLogs(logRepo.findUniqueByID(logId).getGroupLogs()).isEmpty();
    }

    private Set<GroupLog> filterPublicGroupLogs(Set<GroupLog> groupLogs) {
        Group publicGroup = groupRepo.findPublicGroup();
        if (publicGroup == null) {
            LOGGER.warn("No public group present in repository");
            return Collections.emptySet();
        }

        Set<GroupLog> publicGroupLogs = new HashSet<>(); /* groupLogs
                .stream()
                .filter(groupLog -> publicGroup.equals(groupLog.getGroup()))
                .collect(Collectors.toSet());*/
        for (GroupLog groupLog : groupLogs) {
            if (publicGroup.equals(groupLog.getGroup())) {
                publicGroupLogs.add(groupLog);
            }
        }

        return publicGroupLogs;
    }

    public boolean canUserWriteLog(String username, Integer logId) throws UserNotFoundException {
        User user = userSrv.findUserByLogin(username);
        for (GroupLog gl : groupLogRepo.findByLogAndUser(logId, user.getRowGuid())) {
            if (gl.getHasWrite()) {
                return true;
            }
        }
        return false;
    }

    @Override
    public ExportLogResultType exportLog(Integer logId) throws Exception {
        Log log = logRepo.findUniqueByID(logId);
        XLog xlog = tempCacheService.getProcessLog(log, null);
        final ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        exportToStream(outputStream, xlog);
        ExportLogResultType exportLogResultType = new ExportLogResultType();

        PluginMessages pluginMessages = new PluginMessages();
        exportLogResultType.setMessage(pluginMessages);
        exportLogResultType.setNative(new DataHandler(new ByteArrayDataSource(new ByteArrayInputStream(outputStream.toByteArray()), Constants.GZ_MIMETYPE)));
        return exportLogResultType;
    }

    @Override
    public void cloneLog(String username, Integer folderId, String logName, Integer sourceLogId,
                  String domain, String created, boolean publicModel)
            throws Exception {
        Log log = logRepo.findUniqueByID(sourceLogId);
        XLog xlog = tempCacheService.getProcessLog(log, null);
        final ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        exportToStream(outputStream, xlog);
        ByteArrayInputStream inputStreamLog = new ByteArrayInputStream(outputStream.toByteArray());
        importLog(username, folderId, logName, inputStreamLog, "xes.gz", domain, created, publicModel);
    }

    @Override
    public XLog getXLog(Integer logId) {
        return getXLog(logId, null);
    }

    @Override
    public XLog getXLog(Integer logId, String factoryName) {
        Log log = logRepo.findUniqueByID(logId);
        XLog xLog = tempCacheService.getProcessLog(log, factoryName);
        LOGGER.info("[--IMPORTANT--] Plugin take over control ");
        return xLog;
    }

    @Override
    public void deleteLogs(List<Log> logs, User user) throws Exception {
        for (Log log : logs) {
            if (!canUserWriteLog(user.getUsername(), log.getId())) {
                throw new NotAuthorizedException("Log with id " + log.getId() + " may not be deleted by " + user.getUsername());
            }
            Log realLog = logRepo.findUniqueByID(log.getId());
            userMetadataService.deleteUserMetadataByLog(realLog, user);
            logRepo.delete(realLog);
            tempCacheService.deleteProcessLog(realLog);
            LOGGER.info("Delete XES log " + log.getId() + " from repository.");
        }
    }

    @Override
    public void exportToStream(OutputStream outputStream, XLog log) throws Exception {
        XSerializer serializer = new XesXmlGZIPSerializer();
        serializer.serialize(log, outputStream);
    }

    @Override
    public APMLog getAggregatedLog(Integer logId) {
        Log log = logRepo.findUniqueByID(logId);
        return tempCacheService.getAggregatedLog(log);
    }

}
