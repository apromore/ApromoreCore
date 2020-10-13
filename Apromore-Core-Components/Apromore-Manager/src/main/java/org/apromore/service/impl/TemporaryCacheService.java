/*-
 * #%L
 * This file is part of "Apromore Core".
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
import java.util.Objects;
import javax.annotation.Resource;
import org.apromore.apmlog.APMLog;
import org.apromore.apmlog.APMLogService;
import org.apromore.cache.ehcache.CacheRepository;
import org.apromore.common.ConfigBean;
import org.apromore.dao.jpa.LogRepositoryCustomImpl;
import org.apromore.dao.model.Log;
import org.deckfour.xes.extension.std.XConceptExtension;
import org.deckfour.xes.factory.XFactory;
import org.deckfour.xes.factory.XFactoryRegistry;
import org.deckfour.xes.in.XMxmlGZIPParser;
import org.deckfour.xes.in.XMxmlParser;
import org.deckfour.xes.in.XParser;
import org.deckfour.xes.in.XParserRegistry;
import org.deckfour.xes.in.XesXmlGZIPParser;
import org.deckfour.xes.in.XesXmlParser;
import org.deckfour.xes.model.XEvent;
import org.deckfour.xes.model.XLog;
import org.deckfour.xes.model.XTrace;
import org.deckfour.xes.out.XMxmlGZIPSerializer;
import org.deckfour.xes.out.XMxmlSerializer;
import org.deckfour.xes.out.XSerializer;
import org.deckfour.xes.out.XesXmlGZIPSerializer;
import org.deckfour.xes.out.XesXmlSerializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TemporaryCacheService {

  private static final Logger LOGGER = LoggerFactory.getLogger(LogRepositoryCustomImpl.class);

  private final SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyyMMddHHmmssSSS");

  @Resource
  private CacheRepository cacheRepo;

  @Resource
  private APMLogService apmLogService;

  @Resource
  private ConfigBean config;
  
  
  

  public CacheRepository getCacheRepo() {
    return cacheRepo;
  }

  public void setCacheRepo(CacheRepository cacheRepo) {
    this.cacheRepo = cacheRepo;
  }

  public APMLogService getApmLogService() {
    return apmLogService;
  }

  public void setApmLogService(APMLogService apmLogService) {
    this.apmLogService = apmLogService;
  }

  public ConfigBean getConfig() {
    return config;
  }

  public void setConfig(ConfigBean config) {
    this.config = config;
  }

  private static final String APMLOG_CACHE_KEY_SUFFIX = "APMLog";

  public String storeProcessLog(final Integer folderId, final String logName, XLog log,
      final Integer userID,
      final String domain, final String created) {

    LOGGER.debug("Storing Log " + log.size() + " " + logName);
    if (log != null && logName != null) {
      String logNameId = simpleDateFormat.format(new Date());

      try {
        final String name = logNameId + "_" + logName + ".xes.gz";
        exportToFile(config.getLogsDir() + "/", name, log);

        LOGGER.info("Memory Used: " + getMemoryUsage().getUsed() / 1024 / 1024 + " MB ");

        if (shouldCache(log)) {
          // Store corresponding object into cache
          cacheRepo.put(logNameId, log);
          cacheRepo.put(logNameId + APMLOG_CACHE_KEY_SUFFIX, apmLogService.findAPMLogForXLog(log));
          LOGGER.info(
              "Put XLog [hash: " + log.hashCode() + "] into Cache [" + cacheRepo.getCacheName() +
                  "] using Key [" + logNameId + "]. ");
          LOGGER.info("Put APMLog [hash: " + log.hashCode() + "] into Cache ["
              + cacheRepo.getCacheName() + "] " +
              "using Key [" + logNameId + "APMLog]. ");
          LOGGER.info("Memory Used: " + getMemoryUsage().getUsed() / 1024 / 1024 + " MB ");
          LOGGER.info("Memory Available: "
              + (getMemoryUsage().getMax() - getMemoryUsage().getUsed()) / 1024 / 1024 + " " +
              "MB ");
          LOGGER.info(
              "The number of elements in the memory store = " + cacheRepo.getMemoryStoreSize());
        } else {
          LOGGER.info("The total number of events in this log exceed cache threshold");
        }

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
        File file = new File(config.getLogsDir() + "/" + name);
        file.delete();

        // Remove corresponding object from cache
        String key = log.getFilePath();
        cacheRepo.evict(key);
        cacheRepo.evict(key + APMLOG_CACHE_KEY_SUFFIX);
        System.gc(); // Force GC after cache eviction
        LOGGER
            .info("Delete XLog [ KEY: " + key + "] from cache [" + cacheRepo.getCacheName() + "]");
        LOGGER
            .info("The number of elements in the memory store = " + cacheRepo.getMemoryStoreSize());

      } catch (Exception e) {
        LOGGER.error("Error " + e.getMessage());
      }
    }
  }

  /**
   * Load XES log file from cache, if not found then load from Event Logs Repository
   * 
   * @param log
   * @return
   */
  public XLog getProcessLog(Log log, String factoryName) {

    if (log != null) {

      // ******* profiling code start here ********
      long startTime = System.nanoTime();
      long elapsedNanos;
      // ******* profiling code end here ********

      String key = log.getFilePath();
      XLog element = (XLog) cacheRepo.get(key);

      if (element == null) {
        // If doesn't hit cache
        LOGGER.info("Cache for [KEY: " + key + "] is null.");

        try {
          String name =
              config.getLogsDir() + "/" + log.getFilePath() + "_" + log.getName() + ".xes.gz";
          XFactory factory = getXFactory(factoryName);
          XLog xlog = importFromFile(factory, name);


          // ******* profiling code start here ********
          elapsedNanos = System.nanoTime() - startTime;
          LOGGER.info("Retrieved XES log " + name + " [" + xlog.hashCode() + "]. Elapsed time: "
              + elapsedNanos / 1000000 + " ms");
          LOGGER.info("Memory Used: " + getMemoryUsage().getUsed() / 1024 / 1024 + " MB ");
          LOGGER.info("Memory Available: "
              + (getMemoryUsage().getMax() - getMemoryUsage().getUsed()) / 1024 / 1024 + " " +
              "MB ");
          startTime = System.nanoTime();
          // ******* profiling code end here ********

          if (shouldCache(xlog)) {
            cacheRepo.put(key, xlog);
            elapsedNanos = System.nanoTime() - startTime;
            LOGGER
                .info("Cache XLog [KEY:" + key + "]. " + "Elapsed time: " + elapsedNanos / 1000000 +
                    " ms.");

            startTime = System.nanoTime();
            cacheRepo.put(key + APMLOG_CACHE_KEY_SUFFIX, apmLogService.findAPMLogForXLog(xlog));
            elapsedNanos = System.nanoTime() - startTime;
            LOGGER.info(
                "Construct and cache APMLog [KEY:" + key + APMLOG_CACHE_KEY_SUFFIX + "]. Elapsed " +
                    "time: " + elapsedNanos / 1000000 + " ms.");

            LOGGER.info("Memory Used: " + getMemoryUsage().getUsed() / 1024 / 1024 + " MB ");
            LOGGER.info("Memory Available: "
                + (getMemoryUsage().getMax() - getMemoryUsage().getUsed()) / 1024 / 1024 + " " +
                "MB ");
            LOGGER.info(
                "The number of elements in the memory store = " + cacheRepo.getMemoryStoreSize());
          } else {
            LOGGER.info("The total number of events in this log exceed cache threshold");
          }

          return xlog;
        } catch (Exception e) {
          LOGGER.error("Error " + e.getMessage());
        }

      } else {
        // If cache hit
        LOGGER.info("Got object [HASH: " + element.hashCode() + " KEY:" + key + "] from cache ["
            + cacheRepo.getCacheName() + "]");
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
   * 
   * @param log
   * @return
   */
  public APMLog getAggregatedLog(Log log) {
    if (log != null) {

      // ******* profiling code start here ********
      long startTime = System.nanoTime();
      long elapsedNanos;
      // ******* profiling code end here ********

      String key = log.getFilePath() + APMLOG_CACHE_KEY_SUFFIX;
      APMLog element = (APMLog) cacheRepo.get(key);

      if (element == null) {
        // If doesn't hit cache
        LOGGER.info("Cache for [KEY: " + key + "] is null.");

        try {
          XLog xLog = getProcessLog(log, null);
          APMLog apmLog = apmLogService.findAPMLogForXLog(xLog);

          if (shouldCache(xLog)) {
            cacheRepo.put(key, apmLog);
            elapsedNanos = System.nanoTime() - startTime;
            LOGGER.info(
                "Put APMLog [KEY:" + key + "] into Cache. Elapsed time: " + elapsedNanos / 1000000 +
                    " ms.");
            LOGGER.info(
                "The number of elements in the memory store = " + cacheRepo.getMemoryStoreSize());
          }

          return apmLog;

        } catch (Exception e) {
          LOGGER.error("Error " + e.getMessage());
        }

      } else {
        // If cache hit
        LOGGER.info("Get object [HASH: " + element.hashCode() + " KEY:" + key + "] from cache ["
            + cacheRepo.getCacheName() + "]");
        return element;
      }
    }
    return null;
  }

  private boolean shouldCache(XLog xLog) {

    /**
     * The total number of events in this log.
     */
    int numberOfEvents = 0;
    /**
     * The number of traces in this log.
     */
    int numberOfTraces = 0;

    int numOfEventsLimit = 0;
    int numOfTracesLimit = 0;

    for (XTrace trace : xLog) {
      numberOfTraces++;
      for (XEvent event : trace) {
        numberOfEvents++;
      }
    }

    try {
      numOfEventsLimit = Integer.parseInt(config.getNumOfEvent().replaceAll(",", ""));
      numOfTracesLimit = Integer.parseInt(config.getNumOfTrace().replaceAll(",", ""));

    } catch (NumberFormatException e) {
      LOGGER.error("Cache threshold value is wrong, please check the setting in config file "
          + e.getMessage());
    }

    if ((numOfEventsLimit != 0 && numberOfEvents > numOfEventsLimit)
        || (numOfTracesLimit != 0 && numberOfTraces > numOfTracesLimit)) {
      return false;
    }

    return true;
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
      if (!directory.exists())
        directory.mkdirs();
      File file = new File(path + name);
      if (!file.exists())
        file.createNewFile();
      outputStream = new FileOutputStream(file);
      serializer.serialize(log, outputStream);
      outputStream.close();

      if (file.exists() && file.isFile()) {
        LOGGER.info(String.valueOf(file.length()));
      } else {
        LOGGER.info("file doesn't exist or is not a file");
      }

    } catch (Exception e) {
      e.printStackTrace();
      System.out.println("Error");
    }
  }


}
