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
package org.apromore.service.impl;

import org.apromore.common.Constants;
import org.apromore.dao.*;
import org.apromore.dao.model.*;
import org.apromore.model.ExportLogResultType;
import org.apromore.model.PluginMessages;
import org.apromore.model.SummariesType;
import org.apromore.service.*;
import org.apromore.service.helper.UserInterfaceHelper;
import org.deckfour.xes.extension.std.XConceptExtension;
import org.deckfour.xes.factory.XFactory;
import org.deckfour.xes.factory.XFactoryNaiveImpl;
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
 * @author <a href="mailto:cam.james@gmail.com">Cameron James</a>
 */
@Service
@Transactional(propagation = Propagation.REQUIRED, isolation = Isolation.DEFAULT, readOnly = true, rollbackFor = Exception.class)
public class EventLogServiceImpl implements EventLogService {

    private static final Logger LOGGER = LoggerFactory.getLogger(EventLogServiceImpl.class);

    private UserService userSrv;
    private FolderRepository folderRepo;
    private LogRepository logRepo;
    private UserInterfaceHelper ui;

    /**
     * Default Constructor allowing Spring to Autowire for testing and normal use.
     * @param logRepository Log repository.
     * @param ui User Interface Helper.
     */
    @Inject
    public EventLogServiceImpl(final LogRepository logRepository, final FolderRepository folderRepo, final UserService userSrv, final UserInterfaceHelper ui) {
        this.logRepo = logRepository;
        this.folderRepo = folderRepo;
        this.userSrv = userSrv;
        this.ui = ui;
    }


    @Override
    public SummariesType readLogSummaries(Integer folderId, String searchExpression) {
        return null;
    }

    @Override
    public Log importLog(String username, Integer folderId, String logName, InputStream inputStreamLog, String extension, String domain, String created, boolean publicModel) throws Exception {
        String path = logRepo.storeProcessLog(folderId, logName, importFromStream(new XFactoryNaiveImpl(), inputStreamLog, extension), userSrv.findUserByLogin(username).getId(), domain, created, publicModel);
        Log log = new Log();
        log.setFolder(folderRepo.findUniqueByID(folderId));
        log.setDomain(domain);
        log.setCreateDate(created);
        log.setFilePath(path);
        log.setName(logName);
        log.setPublicLog(publicModel);
        log.setRanking("");
        log.setUser(userSrv.findUserByLogin(username));
        logRepo.saveAndFlush(log);
        return log;
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
