/*
 * Copyright © 2009-2018 The Apromore Initiative.
 *
 * This file is part of "Apromore".
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
package org.apromore.service.impl;

import org.apromore.common.Constants;
import org.apromore.dao.FolderRepository;
import org.apromore.dao.GroupRepository;
import org.apromore.dao.LogRepository;
import org.apromore.dao.StatisticRepository;
import org.apromore.dao.model.*;
import org.apromore.model.ExportLogResultType;
import org.apromore.model.PluginMessages;
import org.apromore.model.SummariesType;
import org.apromore.service.EventLogService;
import org.apromore.service.UserService;
import org.apromore.service.helper.UserInterfaceHelper;
import org.apromore.util.StatType;
import org.apromore.util.UuidAdapter;
import org.deckfour.xes.extension.std.XConceptExtension;
import org.deckfour.xes.factory.XFactory;
import org.deckfour.xes.factory.XFactoryNaiveImpl;
import org.deckfour.xes.factory.XFactoryRegistry;
import org.deckfour.xes.in.*;
import org.deckfour.xes.model.XAttribute;
import org.deckfour.xes.model.XAttributeMap;
import org.deckfour.xes.model.XLog;
import org.deckfour.xes.out.XSerializer;
import org.deckfour.xes.out.XesXmlGZIPSerializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.concurrent.ConcurrentMapCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import javax.activation.DataHandler;
import javax.inject.Inject;
import javax.mail.util.ByteArrayDataSource;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.*;

//import javax.annotation.Resource;

/**
 * Implementation of the ProcessService Contract.
 * @author <a href="mailto:cam.james@gmail.com">Cameron James</a>
 */
@Service
@Transactional(propagation = Propagation.REQUIRED, isolation = Isolation.DEFAULT, readOnly = true, rollbackFor = Exception.class)
//@EnableCaching
public class EventLogServiceImpl implements EventLogService {

    private static final Logger LOGGER = LoggerFactory.getLogger(EventLogServiceImpl.class);

    public static final String PARENT_NODE_FLAG = "0";
    public static final String STAT_NODE_NAME = "apromore:stat";

    private LogRepository logRepo;
    private GroupRepository groupRepo;
    private FolderRepository folderRepo;
    private UserService userSrv;
    private UserInterfaceHelper ui;
    private StatisticRepository statisticRepository;

    /**
     * Default Constructor allowing Spring to Autowire for testing and normal use.
     * @param logRepository Log repository.
     * @param ui User Interface Helper.
     */
    @Inject
    public EventLogServiceImpl(final LogRepository logRepository, final GroupRepository groupRepository, final FolderRepository folderRepo, final UserService userSrv, final UserInterfaceHelper ui, final StatisticRepository statisticRepository) {
        this.logRepo = logRepository;
        this.groupRepo = groupRepository;
        this.folderRepo = folderRepo;
        this.userSrv = userSrv;
        this.ui = ui;
        this.statisticRepository = statisticRepository;
    }


    @Override
    public SummariesType readLogSummaries(Integer folderId, String searchExpression) {
        return null;
    }

    @Override
    public Log importLog(String username, Integer folderId, String logName, InputStream inputStreamLog, String extension, String domain, String created, boolean publicModel) throws Exception {
        User user = userSrv.findUserByLogin(username);

        String path = logRepo.storeProcessLog(folderId, logName, importFromStream(new XFactoryNaiveImpl(), inputStreamLog, extension), user.getId(), domain, created);
        Log log = new Log();
        log.setFolder(folderRepo.findUniqueByID(folderId));
        log.setDomain(domain);
        log.setCreateDate(created);
        log.setFilePath(path);
        log.setName(logName);
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
        log.setName(logName);

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
        for (GroupLog groupLog: groupLogs) {
            if (publicGroup.equals(groupLog.getGroup())) {
                publicGroupLogs.add(groupLog);
            }
        }

        return publicGroupLogs;
    }

    @Override
    public ExportLogResultType exportLog(Integer logId) throws Exception {
        Log log = logRepo.findUniqueByID(logId);
        XLog xlog = logRepo.getProcessLog(log);
        final ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        exportToStream(outputStream, xlog);
        ExportLogResultType exportLogResultType = new ExportLogResultType();

        PluginMessages pluginMessages = new PluginMessages();
        exportLogResultType.setMessage(pluginMessages);
        exportLogResultType.setNative(new DataHandler(new ByteArrayDataSource(new ByteArrayInputStream(outputStream.toByteArray()), Constants.GZ_MIMETYPE)));
        return exportLogResultType;
    }

    @Override
    public XLog getXLog(Integer logId) {
        Log log = logRepo.findUniqueByID(logId);
        return logRepo.getProcessLog(log);
    }


    @Override
    public XLog getXLogWithStats(Integer logId) {

        XFactory factory = XFactoryRegistry.instance().currentDefault();
        XAttribute parent;

        XLog log = getXLog(logId);

        // TODO: The value of containerAttribute can be used to store the availability of different of statistics by bitwise.
        XAttribute containerAttribute = factory.createAttributeLiteral(STAT_NODE_NAME, "", null);
        log.getAttributes().put(STAT_NODE_NAME, containerAttribute);

        List<Statistic> stats = getStats(logId);

        if (stats != null && !stats.isEmpty()) { // if there is cache, then append it to XES log as metadata
//            for (Statistic stat : stats) {
//                if (Arrays.equals(stat.getPid(), PARENT_NODE_FLAG.getBytes())) {
//                    parent = factory.createAttributeLiteral(stat.getStat_key(), stat.getStat_value(), null);
//                    parent.setAttributes(getChildNodes(stat.getId(), stats, factory));
//                    // Since parent share the same stat_key, so add Statistic.count as key when put stat into XAttributeMap
//                    log.getAttributes().get(STAT_NODE_NAME).getAttributes().put(stat.getCount().toString(), parent);
//                }
//            }

            for (int i = 0; i < stats.size(); i++) {

                Statistic pStat = stats.get(i);
                byte[] parentId = pStat.getId();

                if (Arrays.equals(pStat.getPid(), PARENT_NODE_FLAG.getBytes())){

                    parent = factory.createAttributeLiteral(pStat.getStat_key(), pStat.getStat_value(), null);

                    XAttributeMap attributeMap = factory.createAttributeMap();

                    for (int j = 1; j < stats.size(); j++) {
                        if( i + j < stats.size()) {
                            if(Arrays.equals(stats.get(i+j).getPid(), parentId)) {
                                XAttribute attribute = factory.createAttributeLiteral(stats.get(i+j).getStat_key(), stats.get(i+j).getStat_value(), null);
                                attributeMap.put(stats.get(i+j).getStat_key(), attribute);
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
     * @param stats list of statistic entities
     * @return XAttributeMap
     */
    private XAttributeMap getChildNodes (byte[] parentId, List<Statistic> stats, XFactory factory) {
        XAttributeMap attributeMap = factory.createAttributeMap();
        for(Statistic stat : stats) {
            if(Arrays.equals(stat.getPid(), parentId)){
                XAttribute attribute = factory.createAttributeLiteral(stat.getStat_key(), stat.getStat_value(), null);
                attributeMap.put(stat.getStat_key(), attribute);
            }
        }
        return attributeMap;
    }

    /**
     * Get statistics by LogID
     * @param logId logID
     * @return list of statistic entities
     */
//    @Cacheable("log")
    public List<Statistic> getStats(Integer logId) {
        return statisticRepository.findByLogid(logId);
    }

    /**
     * @param logId
     * @param statType
     * @return
     */
//    public List<?> getStatsByType(Integer logId, StatType statType) {
//        // if flag = pd, if flag = db
//        List<?> stats;
//
//        switch (statType) {
//
//            case FILTER:
//                stats = statisticRepository.findByLogid(logId);
//                break;
//            case CASE:
//            case ACTIVITY:
//            case RESOURCE:
//                stats = dashboardRepository.findByLogid(logId);
//                break;
//            default:
//                stats = null;
//                break;
//        }
//        return stats;
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

        if(!isStatsExists(logId, statType)) {
            statisticRepository.storeAllStats(flattenNestedStringMap(map, logId, statType));

            LOGGER.info("Stored statistics of " + statType.toString() + " in Log [" + logId + "]");
        }
    }


    /**
     * flatten nested map into list of {@link org.apromore.dao.model.Statistic } entities
     * @param map nested map generated by Process Discover generateStatistic() method
     *            <caseId, <key, value>>
     *            <activityId, <key, value>>
     *            <resourceId, <key, value>>
     *
     *            <caseId, <caseID, 173640>>, <caseId, <Events, 20>>, <caseId, <Variant, 2>>
     *
     * @param logId logID
     * @return list of statistic entities
     * @throws IllegalArgumentException
     */

    public List<Statistic> flattenNestedStringMap(Map<String, Map<String, String>> map, Integer logId, StatType statType) {

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
     * @param map nested map generated by Process Discover generateStatistic() method
     *            <caseId, <key, value>>
     *            <activityId, <key, value>>
     *            <resourceId, <key, value>>
     *
     *            <caseId, <caseID, 173640>>, <caseId, <Events, 20>>, <caseId, <Variant, 2>>
     *
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
    public void deleteLogs(List<Log> logs) throws Exception {
        for(Log log : logs) {
            Log realLog = logRepo.findUniqueByID(log.getId());
            logRepo.delete(realLog);
            logRepo.deleteProcessLog(realLog);
        }
    }

    public static XLog importFromStream(XFactory factory, InputStream is, String extension) throws Exception {
        XParser parser;
        parser = null;
        if(extension.endsWith("mxml")) {
            parser = new XMxmlParser(factory);
        }else if(extension.endsWith("mxml.gz")) {
            parser = new XMxmlGZIPParser(factory);
        }else if(extension.endsWith("xes")) {
            parser = new XesXmlParser(factory);
        }else if(extension.endsWith("xes.gz")) {
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
    public void exportToStream(OutputStream outputStream, XLog log) throws Exception {
        XSerializer serializer = new XesXmlGZIPSerializer();
        serializer.serialize(log, outputStream);
    }

}
