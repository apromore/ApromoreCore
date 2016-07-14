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

/**
 *
 */
package org.apromore.dao.jpa;

import org.apromore.dao.LogRepositoryCustom;
import org.apromore.dao.model.Log;
import org.deckfour.xes.extension.std.XConceptExtension;
import org.deckfour.xes.factory.XFactory;
import org.deckfour.xes.in.*;
import org.deckfour.xes.model.XLog;
import org.deckfour.xes.out.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.JdbcTemplate;

import javax.annotation.Resource;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * implementation of the org.apromore.dao.ClusteringDao interface.
 * @author <a href="mailto:chathura.ekanayake@gmail.com">Chathura C. Ekanayake</a>
 */
public class LogRepositoryCustomImpl implements LogRepositoryCustom {

    private static final Logger LOGGER = LoggerFactory.getLogger(LogRepositoryCustomImpl.class);

    @PersistenceContext
    private EntityManager em;

    @Resource
    private JdbcTemplate jdbcTemplate;

    private final SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyyMMddHHmmssSSS");

    private static final String GET_ALL_LOGS_JPA = "SELECT l FROM Log l ";
    private static final String GET_ALL_LOGS_FOLDER_JPA = "SELECT l FROM Log l JOIN l.folder f ";
    private static final String GET_ALL_PUBLIC_JPA = "l.publicModel = true ";
    private static final String GET_ALL_FOLDER_JPA = "f.id = ";
    private static final String GET_ALL_SORT_JPA = " ORDER by l.id";


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
            strQry.append(" AND ").append(GET_ALL_PUBLIC_JPA);
        } else {
            strQry.append(" WHERE ").append(GET_ALL_PUBLIC_JPA);
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
        if (conditions != null && !conditions.isEmpty()) {
            strQry.append(" WHERE ").append(conditions);
            strQry.append(" AND ").append(GET_ALL_PUBLIC_JPA);
            whereAdded = true;
        } //else {
        //   strQry.append(" WHERE ").append(GET_ALL_PUBLIC_JPA);
        //}
        if (whereAdded) {
            strQry.append(" AND ");
        } else {
            strQry.append(" WHERE ");
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
    public String storeProcessLog(final Integer folderId, final String logName, XLog log, final Integer userID, final String domain, final String created, final boolean publicModel) {

        LOGGER.error("Storing Log " + log.size() + " " + logName);
        if (log != null && logName != null) {
            String logNameId = simpleDateFormat.format(new Date());

            try {
                final String name = logNameId + "_" + logName + ".xes.gz";
                exportToFile("processLogs/", name, log);
            } catch (Exception e) {
                LOGGER.error("Error " + e.getMessage());
            }
            return logNameId;
        }
        return null;
    }

    /**
//     * @see LogRepository#removeProcessLog(Integer)
     * {@inheritDoc}
     */

    @SuppressWarnings("unchecked")
    public void removeProcessLog(Integer processLogId) {
        if (processLogId != null) {
            String sql = "DELETE FROM Log WHERE id = ?";
            this.jdbcTemplate.update(sql, new Object[] {processLogId});
        }
    }


    /* ************************** Util Methods ******************************* */


    public XLog importFromFile(XFactory factory, String location) throws Exception {
        if(location.endsWith("mxml.gz")) {
            return importFromInputStream(new FileInputStream(location), new XMxmlGZIPParser(factory));
        }else if(location.endsWith("mxml")) {
            return importFromInputStream(new FileInputStream(location), new XMxmlParser(factory));
        }else if(location.endsWith("xes.gz")) {
            return importFromInputStream(new FileInputStream(location), new XesXmlGZIPParser(factory));
        }else if(location.endsWith("xes")) {
            return importFromInputStream(new FileInputStream(location), new XesXmlParser(factory));
        }
        return null;
    }

    public void exportToFile(String path, String name, XLog log) throws Exception {
        if(name.endsWith("mxml.gz")) {
            exportToInputStream(log, path + name, new XMxmlGZIPSerializer());
        }else if(name.endsWith("mxml")) {
            exportToInputStream(log, path + name, new XMxmlSerializer());
        }else if(name.endsWith("xes.gz")) {
            exportToInputStream(log, path + name, new XesXmlGZIPSerializer());
        }else if(name.endsWith("xes")) {
            exportToInputStream(log, path + name, new XesXmlSerializer());
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

    public void exportToInputStream(XLog log, String name, XSerializer serializer) {
        FileOutputStream outputStream;
        try {
            File f = new File(name);
            if(!f.exists()) f.createNewFile();
            outputStream = new FileOutputStream(f);
            serializer.serialize(log, outputStream);
            outputStream.close();
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Error");
        }
    }

}
