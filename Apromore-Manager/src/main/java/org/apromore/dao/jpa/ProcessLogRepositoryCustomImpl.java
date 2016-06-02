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

import org.apromore.dao.ClusterRepositoryCustom;
import org.apromore.dao.ProcessLogRepository;
import org.apromore.dao.ProcessLogRepositoryCustom;
import org.apromore.dao.model.Cluster;
import org.apromore.dao.model.Log;
import org.apromore.service.model.ClusterFilter;
import org.apromore.toolbox.clustering.algorithm.dbscan.FragmentPair;
import org.deckfour.xes.extension.std.XConceptExtension;
import org.deckfour.xes.factory.XFactory;
import org.deckfour.xes.factory.XFactoryNaiveImpl;
import org.deckfour.xes.in.*;
import org.deckfour.xes.model.XLog;
import org.deckfour.xes.out.*;
import org.springframework.data.domain.Page;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;

import javax.annotation.Resource;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

import static org.apache.axis.attachments.MimeUtils.filter;

/**
 * implementation of the org.apromore.dao.ClusteringDao interface.
 * @author <a href="mailto:chathura.ekanayake@gmail.com">Chathura C. Ekanayake</a>
 */
public class ProcessLogRepositoryCustomImpl implements ProcessLogRepositoryCustom {

    @Resource
    private JdbcTemplate jdbcTemplate;


    /* ************************** JPA Methods here ******************************* */

    /**
     * @see ProcessLogRepository#findUniqueByID(Integer)
     * {@inheritDoc}
     */
    @Override
    @SuppressWarnings("unchecked")
    public XLog findUniqueByID(Integer processLogId) {
        if (processLogId != null) {
            String sql = "SELECT file_path FROM process_log WHERE id = ?";
            List<String> path = this.jdbcTemplate.query(sql, new Object[] {processLogId},
                    new RowMapper<String>() {
                        public String mapRow(ResultSet rs, int rowNum) throws SQLException {
                            return rs.getString("file_path");
                        }
                    });
            if (path.isEmpty()) {
                return null;
            } else {
                try {
                    return importFromFile(new XFactoryNaiveImpl(), path.get(0));
                } catch (Exception e) {
                    return null;
                }
            }
        } else {
            return null;
        }
    }

    /**
     * @see ProcessLogRepository#findLogNameByID(Integer)
     * {@inheritDoc}
     */
    @Override
    @SuppressWarnings("unchecked")
    public String findLogNameByID(Integer processLogId) {
        if (processLogId != null) {
            String sql = "SELECT name FROM process_log WHERE id = ?";
            List<String> name = this.jdbcTemplate.query(sql, new Object[] {processLogId},
                    new RowMapper<String>() {
                        public String mapRow(ResultSet rs, int rowNum) throws SQLException {
                            return rs.getString("name");
                        }
                    });
            if (name.isEmpty()) {
                return null;
            } else {
                return name.get(0);
            }
        } else {
            return null;
        }
    }

    /**
     * @see ProcessLogRepository#findFolderId(Integer)
     * {@inheritDoc}
     */
    @Override
    @SuppressWarnings("unchecked")
    public Page<Log> findFolderId(Integer folderId) {
        if (folderId != null) {
            String sql = "SELECT * FROM process_log WHERE folderId = ?";
            List<Integer> path = this.jdbcTemplate.query(sql, new Object[] {folderId},
                    new RowMapper<Integer>() {
                        public Integer mapRow(ResultSet rs, int rowNum) throws SQLException {
                            return rs.getInt("id");
                        }
                    });
            return path;
        } else {
            return null;
        }
    }

    /**
     * @see ProcessLogRepository#storeProcessLog(Integer, String, XLog)
     * {@inheritDoc}
     */
    @Override
    @SuppressWarnings("unchecked")
    public void storeProcessLog(Integer folderId, String logName, XLog log) {
        if (log != null && logName != null) {
            String sql = "SELECT max(id) FROM process_log";
            List<Integer> max = this.jdbcTemplate.query(sql,
                    new RowMapper<Integer>() {
                        public Integer mapRow(ResultSet rs, int rowNum) throws SQLException {
                            return rs.getInt("id");
                        }
                    });

            try {
                String name = max + "_" + logName + ".xes.gz";
                exportToFile("processLogs/", name, log);

                sql = "INSERT INTO process_log WHERE (folderId, name, path_file) values (?, ?, ?)";
                this.jdbcTemplate.update(sql, new Object[] {folderId, logName, "processLogs/" + name});
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * @see ProcessLogRepository#removeProcessLog(Integer)
     * {@inheritDoc}
     */
    @Override
    @SuppressWarnings("unchecked")
    public void removeProcessLog(Integer processLogId) {
        if (processLogId != null) {
            String sql = "DELETE FROM process_log WHERE id = ?";
            this.jdbcTemplate.update(sql, new Object[] {processLogId});
        }
    }

    @Override
    public long count() {
        return 0;
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
