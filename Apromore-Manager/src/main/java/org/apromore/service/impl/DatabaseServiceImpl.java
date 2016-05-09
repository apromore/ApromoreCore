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

import org.apromore.service.DatabaseService;
import org.apromore.service.MySqlBean;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.inject.Inject;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by corno on 28/08/2014.
 */
@Service
public class DatabaseServiceImpl implements DatabaseService {
    private MySqlBean mySqlBean;
    private static final String tableLabels = "jbpt_labels";
    private static final String tableNets = "jbpt_petri_nets";
    private static final String columnLabel = "label";
    private static final String columIdentifier = "identifier";
    private static final Logger LOGGER = LoggerFactory.getLogger(DatabaseServiceImpl.class);

    public DatabaseServiceImpl(){

    }

    @Inject
    public DatabaseServiceImpl(MySqlBean mySqlBean){
        this.mySqlBean=mySqlBean;
    }

    @Override
    public List<String> getLabels(String table, String columnName) {
        HashSet<String> labels = new HashSet<String>();
        try {
            Class.forName("com.mysql.jdbc.Driver");
            Connection conn = DriverManager.getConnection(mySqlBean.getURL(), mySqlBean.getUser(), mySqlBean.getPassword());
            Statement stmt = conn.createStatement();
            String query = "SELECT "+columnName+" FROM "+table+";";
            ResultSet rs = stmt.executeQuery(query);

            while (rs.next())
                labels.add(rs.getString(columnName));
        }catch(Exception ex){
            LOGGER.error("-----------ERRORRE: " + ex.toString());
            for (StackTraceElement ste : ex.getStackTrace())
                LOGGER.info("ERRORE: " + ste.getClassName() + " " + ste.getMethodName() + " " + ste.getLineNumber() + " " + ste.getFileName());
        }
        return new LinkedList<String>(labels);
    }
}
