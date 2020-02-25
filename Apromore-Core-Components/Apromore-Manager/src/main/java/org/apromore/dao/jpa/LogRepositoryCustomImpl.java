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

/**
 *
 */
package org.apromore.dao.jpa;

import org.apromore.apmlog.APMLog;
import org.apromore.apmlog.APMLogService;
import org.apromore.apmlog.impl.APMLogServiceImpl;
import org.apromore.dao.CacheRepository;
import org.apromore.dao.LogRepositoryCustom;
import org.apromore.dao.model.Log;
import org.deckfour.xes.extension.std.XConceptExtension;
import org.deckfour.xes.factory.XFactory;
import org.deckfour.xes.factory.XFactoryRegistry;
import org.deckfour.xes.in.*;
import org.deckfour.xes.model.XLog;
import org.deckfour.xes.out.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Resource;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.lang.management.ManagementFactory;
import java.lang.management.MemoryMXBean;
import java.lang.management.MemoryUsage;
import java.text.SimpleDateFormat;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Objects;

/**
 * implementation of the org.apromore.dao.LogRepositoryCustom interface.
 * @author <a href="mailto:raffaele.conforti@unimelb.edu.au">Raffaele Conforti</a>
 */
public class LogRepositoryCustomImpl implements LogRepositoryCustom {

    private static final Logger LOGGER = LoggerFactory.getLogger(LogRepositoryCustomImpl.class);

    private static final String APMLOG_CACHE_KEY_SUFFIX = "APMLog";
    private static final String GET_ALL_LOGS_JPA = "SELECT l FROM Log l ";
    private static final String GET_ALL_LOGS_FOLDER_JPA = "SELECT l FROM Log l JOIN l.folder f ";
    private static final String GET_ALL_FOLDER_JPA = "f.id = ";
    private static final String GET_ALL_SORT_JPA = " ORDER by l.id";
    private final SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyyMMddHHmmssSSS");
    @PersistenceContext
    private EntityManager em;

    /**
     * Inject the CacheRepository instance
     */
    @Resource
    private CacheRepository cacheRepo;

    @Resource
    private APMLogService apmLogService;


    /* ************************** JPA Methods here ******************************* */

    /**
     * @see org.apromore.dao.LogRepositoryCustom#findAllLogs(String)
     * {@inheritDoc}
     */
    @Override
    @SuppressWarnings("unchecked")
    public List<Log> findAllLogs(final String conditions) {
        StringBuilder strQry = new StringBuilder(0);
        strQry.append(GET_ALL_LOGS_JPA);
        if (conditions != null && !conditions.isEmpty()) {
            strQry.append(" WHERE ").append(conditions);
        }
        strQry.append(GET_ALL_SORT_JPA);

        Query query = em.createQuery(strQry.toString());
        return query.getResultList();
    }

    /**
     * @see org.apromore.dao.LogRepositoryCustom#findAllLogsByFolder(Integer, String)
     * {@inheritDoc}
     */
    @Override
    @SuppressWarnings("unchecked")
    public List<Log> findAllLogsByFolder(final Integer folderId, final String conditions) {
        boolean whereAdded = false;
        StringBuilder strQry = new StringBuilder(0);
        strQry.append(GET_ALL_LOGS_FOLDER_JPA);
        strQry.append(" WHERE ");
        if (conditions != null && !conditions.isEmpty()) {
            strQry.append(conditions);
            strQry.append(" AND ");
        }
        strQry.append(GET_ALL_FOLDER_JPA).append(folderId);
        strQry.append(GET_ALL_SORT_JPA);

        Query query = em.createQuery(strQry.toString());
        return query.getResultList();
    }

    /**
     * ?@see LogRepository#storeProcessLog(Integer, String, XLog)
     * {@inheritDoc}
     */
    @Override
    public String storeProcessLog(final Integer folderId, final String logName, XLog log, final Integer userID,
                                  final String domain, final String created) {

        LOGGER.debug("Storing Log " + log.size() + " " + logName);
        if (log != null && logName != null) {
            String logNameId = simpleDateFormat.format(new Date());

            try {
                final String name = logNameId + "_" + logName + ".xes.gz";
                exportToFile("../Event-Logs-Repository/", name, log);

                LOGGER.info("Memory Used: " + getMemoryUsage().getUsed() / 1024 / 1024 + " MB ");

                // Store corresponding object into cache
                cacheRepo.put(logNameId, log);
                cacheRepo.put(logNameId + APMLOG_CACHE_KEY_SUFFIX, apmLogService.findAPMLogForXLog(log));
                LOGGER.info("Put XLog [hash: " + log.hashCode() + "] into Cache [" + cacheRepo.getCacheName() + "] " +
                        "using Key [" + logNameId + "]. ");
                LOGGER.info("Put APMLog [hash: " + log.hashCode() + "] into Cache [" + cacheRepo.getCacheName() + "] " +
                        "using Key [" + logNameId + "APMLog]. ");

                System.gc();
                try {
                    Thread.sleep(500);
                } catch (InterruptedException e) {
                }

                LOGGER.info("Memory Used: " + getMemoryUsage().getUsed() / 1024 / 1024 + " MB ");
                LOGGER.info("Memory Available: " + (getMemoryUsage().getMax() - getMemoryUsage().getUsed()) / 1024 / 1024 + " " +
                        "MB ");
                LOGGER.info("The number of elements in the memory store = " + cacheRepo.getMemoryStoreSize());
                return logNameId;
            } catch (Exception e) {
                LOGGER.error("Error " + e.getMessage(), e);
            }

        }
        return null;
    }

    public void deleteProcessLog(Log log) {
        if (log != null) {
            try {
                String name = log.getFilePath() + "_" + log.getName() + ".xes.gz";
                File file = new File("../Event-Logs-Repository/" + name);
                file.delete();

                // Remove corresponding object from cache
                String key = log.getFilePath();
                cacheRepo.evict(key);
                cacheRepo.evict(key + APMLOG_CACHE_KEY_SUFFIX);
                LOGGER.info("Delete XLog [ KEY: " + key + "] from cache [" + cacheRepo.getCacheName() + "]");
                LOGGER.info("The number of elements in the memory store = " + cacheRepo.getMemoryStoreSize());

            } catch (Exception e) {
                LOGGER.error("Error " + e.getMessage());
            }
        }
    }

    /**
     * Load XES log file from cache, if not found then load from Event Logs Repository
     * @param log
     * @return
     */
    public XLog getProcessLog(Log log, String factoryName) {

        if (log != null) {

            // *******  profiling code start here ********
            long startTime = System.nanoTime();
            long elapsedNanos;
            // *******  profiling code end here ********

            String key = log.getFilePath();
            XLog element = (XLog) cacheRepo.get(key);

            if (element == null) {
                // If doesn't hit cache
                LOGGER.info("Cache for [KEY: " + key + "] is null.");

                try {
                    String name = "../Event-Logs-Repository/" + log.getFilePath() + "_" + log.getName() + ".xes.gz";
                    XFactory factory = getXFactory(factoryName);
                    XLog xlog = importFromFile(factory, name);


                    // *******  profiling code start here ********
                    elapsedNanos = System.nanoTime() - startTime;
                    LOGGER.info("Retrieved XES log " + name + " [" + xlog.hashCode() + "]. Elapsed time: " + elapsedNanos / 1000000 + " ms");
                    LOGGER.info("Memory Used: " + getMemoryUsage().getUsed() / 1024 / 1024 + " MB ");
                    LOGGER.info("Memory Available: " + (getMemoryUsage().getMax() - getMemoryUsage().getUsed()) / 1024 / 1024 + " " +
                            "MB ");
                    startTime = System.nanoTime();
                    // *******  profiling code end here ********

                    // Log POJO has one constraint that span 2 columns (@UniqueConstraint(columnNames = {"name",
                    // "folderId"}))
                    cacheRepo.put(key, xlog);
                    elapsedNanos = System.nanoTime() - startTime;
                    LOGGER.info("Cache XLog [KEY:" + key + "]. " + "Elapsed time: " + elapsedNanos / 1000000 +
                            " ms.");

                    startTime = System.nanoTime();
                    cacheRepo.put(key + APMLOG_CACHE_KEY_SUFFIX, apmLogService.findAPMLogForXLog(xlog));
                    elapsedNanos = System.nanoTime() - startTime;
                    LOGGER.info("Construct and cache APMLog [KEY:" + key + APMLOG_CACHE_KEY_SUFFIX + "]. Elapsed time: " + elapsedNanos / 1000000 + " ms.");

                    System.gc();
                    try {
                        Thread.sleep(500);
                    } catch (InterruptedException e) {
                    }

                    LOGGER.info("Memory Used: "  + getMemoryUsage().getUsed() / 1024 / 1024 + " MB ");
                    LOGGER.info("Memory Available: " + (getMemoryUsage().getMax() - getMemoryUsage().getUsed()) / 1024 / 1024 + " " +
                            "MB ");
                    LOGGER.info("The number of elements in the memory store = " + cacheRepo.getMemoryStoreSize());
//                    LOGGER.info("Current Memory Usage: " + cache.calculateInMemorySize() / 1024 / 1024 + " MB");
//                    LOGGER.info("Current Memory Store Size: " + cache.getMemoryStoreSize() / 1000 + " MB");
//                    LOGGER.info("Current Disk Store Size: " + cache.calculateOnDiskSize() / 1024 / 1024 + " MB");

                    return xlog;
                } catch (Exception e) {
                    LOGGER.error("Error " + e.getMessage());
                }

            } else {
                // If cache hit
                LOGGER.info("Got object [HASH: " + element.hashCode() + " KEY:" + key + "] from cache [" + cacheRepo.getCacheName() + "]");
                LOGGER.info("Memory Used: " + getMemoryUsage().getUsed() / 1024 / 1024 + " MB ");
                return element;
            }
        }
        return null;
    }




    protected MemoryUsage getMemoryUsage() {
        MemoryMXBean memoryMXBean = ManagementFactory.getMemoryMXBean();
        return memoryMXBean.getHeapMemoryUsage();
    }

    /**
     * Load aggregated log
     * @param log
     * @return
     */
    public APMLog getAggregatedLog(Log log) {
        if (log != null) {

            // *******  profiling code start here ********
            long startTime = System.nanoTime();
            long elapsedNanos;
            // *******  profiling code end here ********

            String key = log.getFilePath() + APMLOG_CACHE_KEY_SUFFIX;
            APMLog element = (APMLog) cacheRepo.get(key);

            if (element == null) {
                // If doesn't hit cache
                LOGGER.info("Cache for [KEY: " + key + "] is null.");

                try {
                    APMLog apmLog = apmLogService.findAPMLogForXLog(getProcessLog(log, null));

                    cacheRepo.put(key, apmLog);
                    elapsedNanos = System.nanoTime() - startTime;
                    LOGGER.info("Put APMLog [KEY:" + key + "] into Cache. Elapsed time: " + elapsedNanos / 1000000 +
                            " ms.");
//                    LOGGER.info("The size that EhCache is using in memory   = " + cacheRepo.getMemoryUsage() / 1024 / 1024 + " MB ");
                    LOGGER.info("The number of elements in the memory store = " + cacheRepo.getMemoryStoreSize());
//                    LOGGER.info("Current Memory Usage: " + cache.calculateInMemorySize() / 1024 / 1024 + " MB");
//                    LOGGER.info("Current Memory Store Size: " + cache.getMemoryStoreSize() / 1000 + " MB");
//                    LOGGER.info("Current Disk Store Size: " + cache.calculateOnDiskSize() / 1024 / 1024 + " MB");

                    return apmLog;
                } catch (Exception e) {
                    LOGGER.error("Error " + e.getMessage());
                }

            } else {
                // If cache hit
                LOGGER.info("Get object [HASH: " + element.hashCode() + " KEY:" + key + "] from cache [" + cacheRepo.getCacheName() + "]");
                return element;
            }
        }
        return null;
    }

    private XFactory getXFactory(String factoryName) {

        if (factoryName != null) {
            // Look for a registered XFactory with the specified name
            for (XFactory factory : XFactoryRegistry.instance().getAvailable()) {
                if (Objects.equals(factory.getName(), factoryName)) {
                    return factory;
                }
            }
        }

        // If the named factory couldn't be found, fall back to the default
        return XFactoryRegistry.instance().currentDefault();
    }

    /* ************************** Util Methods ******************************* */


    public XLog importFromFile(XFactory factory, String location) throws Exception {
        if (location.endsWith("mxml.gz")) {
            return importFromInputStream(new FileInputStream(location), new XMxmlGZIPParser(factory));
        } else if (location.endsWith("mxml")) {
            return importFromInputStream(new FileInputStream(location), new XMxmlParser(factory));
        } else if (location.endsWith("xes.gz")) {
            return importFromInputStream(new FileInputStream(location), new XesXmlGZIPParser(factory));
        } else if (location.endsWith("xes")) {
            return importFromInputStream(new FileInputStream(location), new XesXmlParser(factory));
        }
        return null;
    }

    public void exportToFile(String path, String name, XLog log) throws Exception {
        if (name.endsWith("mxml.gz")) {
            exportToInputStream(log, path, name, new XMxmlGZIPSerializer());
        } else if (name.endsWith("mxml")) {
            exportToInputStream(log, path, name, new XMxmlSerializer());
        } else if (name.endsWith("xes.gz")) {
            exportToInputStream(log, path, name, new XesXmlGZIPSerializer());
        } else if (name.endsWith("xes")) {
            exportToInputStream(log, path, name, new XesXmlSerializer());
        }
    }

    public XLog importFromInputStream(InputStream inputStream, XParser parser) throws Exception {
        Collection<XLog> logs;
        try {
            logs = parser.parse(inputStream);
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
                    logs = p.parse(inputStream);
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
            throw new Exception("No logs contained in log!");
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

    public void exportToInputStream(XLog log, String path, String name, XSerializer serializer) {
        FileOutputStream outputStream;
        try {
            File directory = new File(path);
            if (!directory.exists()) directory.mkdirs();
            File file = new File(path + name);
            if (!file.exists()) file.createNewFile();
            outputStream = new FileOutputStream(file);
            serializer.serialize(log, outputStream);
            outputStream.close();
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Error");
        }
    }

}
