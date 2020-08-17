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
import org.apromore.dao.*;
import org.apromore.dao.model.*;
import org.apromore.exception.NotAuthorizedException;
import org.apromore.portal.model.ExportLogResultType;
import org.apromore.portal.model.PluginMessages;
import org.apromore.portal.model.SummariesType;
import org.apromore.service.EventLogService;
import org.apromore.service.UserMetadataService;
import org.apromore.service.UserService;
import org.apromore.service.helper.UserInterfaceHelper;
import org.apromore.util.StatType;
import org.apromore.util.UserMetadataTypeEnum;
import org.apromore.util.UuidAdapter;
import org.deckfour.xes.extension.std.XConceptExtension;
import org.deckfour.xes.factory.XFactory;
import org.deckfour.xes.factory.XFactoryRegistry;
import org.deckfour.xes.in.*;
import org.deckfour.xes.model.XAttribute;
import org.deckfour.xes.model.XAttributeMap;
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

    public static final String PARENT_NODE_FLAG = "0";
    public static final String STAT_NODE_NAME = "apromore:stat";
    private static final Logger LOGGER = LoggerFactory.getLogger(EventLogServiceImpl.class);
    private LogRepository logRepo;
    private GroupRepository groupRepo;
    private GroupLogRepository groupLogRepo;
    private FolderRepository folderRepo;
    private UserService userSrv;
    private UserInterfaceHelper ui;
    private StatisticRepository statisticRepository;
    private File logsDir;
    private DashboardLayoutRepository dashboardLayoutRepository;
    private UserMetadataService userMetadataService;

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
                               final StatisticRepository statisticRepository, final ConfigBean configBean,
                               final DashboardLayoutRepository dashboardLayoutRepository, final UserMetadataService userMetadataService) {
        this.logRepo = logRepository;
        this.groupRepo = groupRepository;
        this.groupLogRepo = groupLogRepository;
        this.folderRepo = folderRepo;
        this.userSrv = userSrv;
        this.ui = ui;
        this.statisticRepository = statisticRepository;
        this.logsDir = new File(configBean.getLogsDir());
        this.dashboardLayoutRepository = dashboardLayoutRepository;
        this.userMetadataService = userMetadataService;
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
        String path = logRepo.storeProcessLog(folderId, logName, importFromStream(factory, inputStreamLog, extension)
                , user.getId(), domain, created);
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

        // TODO: Remove test code

//        List<Integer> logIdlist = new ArrayList<>();
//        logIdlist.add(138);
//        logIdlist.add(139);
//        userMetadataService.saveUserMetadata("test metadata content", UserMetadataTypeEnum.DASHBOARD, username, logIdlist);
//        userMetadataService.saveUserMetadataLinkedToOneLog("test metadata content", UserMetadataTypeEnum.DASHBOARD, username, log.getId());
//        userMetadataService.updateUserMetadata(16, username, "new content");
//        userMetadataService.deleteUserMetadata(17, username);
//        for (Usermetadata usermetadata : userMetadataService.getUserMetadata(username, 166, UserMetadataTypeEnum.DASHBOARD)) {
//            LOGGER.info( "RESULT :" + usermetadata.getId() + usermetadata.getContent());
//        }
//        LOGGER.info("Result: " + userMetadataService.canUserEditMetadata(username, 18));
//        LOGGER.info("Result: " + userMetadataService.canUserEditMetadata(username, 10));


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
        File file = new File("../Event-Logs-Repository/" + file_name);
        String new_file_name = log.getFilePath() + "_" + newName + ".xes.gz";
        file.renameTo(new File("../Event-Logs-Repository/" + new_file_name));
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

    /**
     * @param user  a user
     * @param logId identifier for a log
     * @return whether the <var>user</var> should be allowed to update the log identified by <var>logId</var>
     */
    private boolean canUserWriteLog(User user, Integer logId) {
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
        XLog xlog = logRepo.getProcessLog(log, null);
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
        XLog xlog = logRepo.getProcessLog(log, null);
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
        XLog xLog = logRepo.getProcessLog(log, factoryName);
        LOGGER.info("[--IMPORTANT--] Plugin take over control ");
        return xLog;
    }

    @Override
    public XLog getXLogWithStats(Integer logId) {

        XFactory factory = XFactoryRegistry.instance().currentDefault();
        XAttribute parent;

        XLog log = getXLog(logId);

        // TODO: The value of containerAttribute can be used to store the availability of different of statistics by
        //  bitwise.
        XAttribute containerAttribute = factory.createAttributeLiteral(STAT_NODE_NAME, "", null);
        log.getAttributes().put(STAT_NODE_NAME, containerAttribute);

        List<Statistic> stats = getStats(logId);

        if (stats != null && !stats.isEmpty()) { // if there is cache, then append it to XES log as metadata
//            for (Statistic stat : stats) {
//                if (Arrays.equals(stat.getPid(), PARENT_NODE_FLAG.getBytes())) {
//                    parent = factory.createAttributeLiteral(stat.getStat_key(), stat.getStat_value(), null);
//                    parent.setAttributes(getChildNodes(stat.getId(), stats, factory));
//                    // Since parent share the same stat_key, so add Statistic.count as key when put stat into
//                    XAttributeMap
//                    log.getAttributes().get(STAT_NODE_NAME).getAttributes().put(stat.getCount().toString(), parent);
//                }
//            }

            // Append stats into Log in one loop
            for (int i = 0; i < stats.size(); i++) {

                Statistic pStat = stats.get(i);
                byte[] parentId = pStat.getId();

                if (Arrays.equals(pStat.getPid(), PARENT_NODE_FLAG.getBytes())) {

                    parent = factory.createAttributeLiteral(pStat.getStat_key(), pStat.getStat_value(), null);

                    XAttributeMap attributeMap = factory.createAttributeMap();

                    for (int j = 1; j < stats.size(); j++) {
                        if (i + j < stats.size()) {
                            if (Arrays.equals(stats.get(i + j).getPid(), parentId)) {
                                XAttribute attribute = factory.createAttributeLiteral(stats.get(i + j).getStat_key(),
                                        stats.get(i + j).getStat_value(), null);
                                attributeMap.put(stats.get(i + j).getStat_key(), attribute);
                            } else {
                                i = i + j - 1;
                                break;
                            }
                        }

                    }
                    parent.setAttributes(attributeMap);
                    log.getAttributes().get(STAT_NODE_NAME).getAttributes().put(pStat.getCount().toString(), parent);
                }
            }
        }
        return log;
    }

    /**
     * @param parentId parent ID
     * @param stats    list of statistic entities
     * @return XAttributeMap
     */
    private XAttributeMap getChildNodes(byte[] parentId, List<Statistic> stats, XFactory factory) {
        XAttributeMap attributeMap = factory.createAttributeMap();
        for (Statistic stat : stats) {
            if (Arrays.equals(stat.getPid(), parentId)) {
                XAttribute attribute = factory.createAttributeLiteral(stat.getStat_key(), stat.getStat_value(), null);
                attributeMap.put(stat.getStat_key(), attribute);
            }
        }
        return attributeMap;
    }

    /**
     * Get statistics by LogID
     *
     * @param logId logID
     * @return list of statistic entities
     */
    public List<Statistic> getStats(Integer logId) {
        LOGGER.info("Get statistics by LogID  " + logId);
        return statisticRepository.findByLogid(logId);
    }

    /**
     * @param logId
     * @param statType
     * @return
     */
    public List<?> getStatsByType(Integer logId, StatType statType) {
        // if flag = pd, if flag = db
        List<?> stats;

        switch (statType) {

            case FILTER:
                stats = statisticRepository.findByLogid(logId);
                break;
            case CASE:
            case ACTIVITY:
            case RESOURCE:
                stats = statisticRepository.findByLogid(logId);
                break;
            default:
                stats = null;
                break;
        }
        return stats;
    }

//    public Boolean isStatsExists(Integer logId, StatType statType) {
//        List<Statistic> stats = statisticRepository.findByLogid(logId);
//        return (null == stats || stats.size() == 0);
//    }

    // just for test, delete when finish
//    private static EntityManagerFactory emf = null;
//    public EntityManagerFactory getEntityManagerFactory() {
//        if (emf == null) {
//            emf = Persistence.createEntityManagerFactory("Apromore");
//        }
//        return emf;
//    }

    /**
     * @param logId
     * @param statType
     * @return
     */
    @Override
    public boolean isStatsExists(Integer logId, StatType statType) {
        return statisticRepository.existsByLogidAndStatType(logId, statType);
    }

    @Override
    public void storeStats(Map<String, Map<String, Integer>> map, Integer logId) {

        List<Statistic> stats = getStats(logId);
        if (null == stats || stats.size() == 0) {

            statisticRepository.storeAllStats(flattenNestedMap(map, logId));

//            statisticRepository.save(flattenNestedMap(map, logId));
            LOGGER.info("Stored statistics of Log: " + logId);
        }
        LOGGER.info("statistics already exist in Log: " + logId);
    }

    public void storeStatsByType(Map<String, Map<String, String>> map, Integer logId, StatType statType) {

        if (!isStatsExists(logId, statType)) {
            statisticRepository.storeAllStats(flattenNestedStringMap(map, logId, statType));

            LOGGER.info("Stored statistics of " + statType.toString() + " in Log [" + logId + "]");
        }
    }

    /**
     * flatten nested map into list of {@link org.apromore.dao.model.Statistic } entities
     *
     * @param map   nested map generated by Process Discover generateStatistic() method
     *              <caseId, <key, value>>
     *              <activityId, <key, value>>
     *              <resourceId, <key, value>>
     *              <p>
     *              <caseId, <caseID, 173640>>, <caseId, <Events, 20>>, <caseId, <Variant, 2>>
     * @param logId logID
     * @return list of statistic entities
     * @throws IllegalArgumentException
     */

    public List<Statistic> flattenNestedStringMap(Map<String, Map<String, String>> map, Integer logId,
                                                  StatType statType) {

        if (map == null || logId == null || statType == null) {
            throw new IllegalArgumentException();
        }

        List<Statistic> statList = new ArrayList<>();

        for (Map.Entry<String, Map<String, String>> option : map.entrySet()) {
            Statistic parent = new Statistic();
            if (option.getKey() != null && option.getValue() != null) {
                parent.setId(UuidAdapter.getBytesFromUUID(UUID.randomUUID()));
                parent.setStat_key(statType.toString()); //assign statType to the key, align with XAttributable object
                parent.setStat_value(option.getKey());
                parent.setLogid(logId);
                parent.setPid(PARENT_NODE_FLAG.getBytes());
                statList.add(parent);
            }
            HashMap<String, String> options_frequency = (HashMap<String, String>) option.getValue();
            if (options_frequency != null) {
                for (Map.Entry<String, String> entry : options_frequency.entrySet()) {
                    Statistic child = new Statistic();
                    if (entry.getKey() != null && entry.getValue() != null) {
                        // child.setId(option.getKey().getBytes());
                        child.setId(UuidAdapter.getBytesFromUUID(UUID.randomUUID()));
                        child.setStat_key(entry.getKey());
                        child.setStat_value(entry.getValue());
                        child.setLogid(logId);
                        child.setPid(parent.getId());
                        statList.add(child);
                    }
                }
            }
        }
        return statList;
    }

    /**
     * flatten nested map into list of Statistic entities
     *
     * @param map   nested map generated by Process Discover generateStatistic() method
     *              <caseId, <key, value>>
     *              <activityId, <key, value>>
     *              <resourceId, <key, value>>
     *              <p>
     *              <caseId, <caseID, 173640>>, <caseId, <Events, 20>>, <caseId, <Variant, 2>>
     * @param logId logID
     * @return list of statistic entities
     */
    public List<Statistic> flattenNestedMap(Map<String, Map<String, Integer>> map, Integer logId) {

        List<Statistic> statList = new ArrayList<>();

        for (Map.Entry<String, Map<String, Integer>> option : map.entrySet()) {
            Statistic parent = new Statistic();
            if (option.getKey() != null && option.getValue() != null) {
                parent.setId(UuidAdapter.getBytesFromUUID(UUID.randomUUID()));
                parent.setStat_key(option.getKey());
                parent.setStat_value("");
                parent.setLogid(logId);
                parent.setPid(PARENT_NODE_FLAG.getBytes());
                statList.add(parent);
            }
            HashMap<String, Integer> options_frequency = (HashMap<String, Integer>) option.getValue();
            if (options_frequency != null) {
                for (Map.Entry<String, Integer> entry : options_frequency.entrySet()) {
                    Statistic child = new Statistic();
                    if (entry.getKey() != null && entry.getValue() != null) {
                        // child.setId(option.getKey().getBytes());
                        child.setId(UuidAdapter.getBytesFromUUID(UUID.randomUUID()));
                        child.setStat_key(entry.getKey());
                        child.setStat_value(entry.getValue().toString());
                        child.setLogid(logId);
                        child.setPid(parent.getId());
                        statList.add(child);
                    }
                }
            }
        }
        return statList;
    }

    @Override
    public void deleteLogs(List<Log> logs, User user) throws Exception {
        for (Log log : logs) {
            if (!canUserWriteLog(user, log.getId())) {
                throw new NotAuthorizedException("Log with id " + log.getId() + " may not be deleted by " + user.getUsername());
            }
            Log realLog = logRepo.findUniqueByID(log.getId());
            logRepo.delete(realLog);
            logRepo.deleteProcessLog(realLog);
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
        return logRepo.getAggregatedLog(log);
    }

    public String getLayoutByLogId(Integer userId, Integer logId) {
        //TODO
        String layout = dashboardLayoutRepository.findByUserIdAndLogId(userId, logId);
        return layout;
    }

    public void saveLayoutByLogId(Integer logId, Integer userId, String layout){
        dashboardLayoutRepository.saveLayoutByLogId(userId, logId, layout);
    }



}
