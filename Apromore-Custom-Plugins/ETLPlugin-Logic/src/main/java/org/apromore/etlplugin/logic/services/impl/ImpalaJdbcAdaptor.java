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
package org.apromore.etlplugin.logic.services.impl;

import org.springframework.stereotype.Component;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Impala JDBC Adaptor class to connect and execute queries.
 */
@Component
public class ImpalaJdbcAdaptor {
    // Impala connection info
    private final String connectionUrl = System.getenv("IMPALA_LINK");
    private final String jdbcDriverName = "com.cloudera.impala.jdbc41.Driver";

    /**
     * Execute the raw query commands in the Impala.
     *
     * @param query statement
     * @throws SQLException Sql failure
     */
    public void execute(String query) throws SQLException {
        try {
            Class.forName(jdbcDriverName);
            try (
                    Connection connection = DriverManager
                            .getConnection(connectionUrl);
                    Statement statement = connection.createStatement();
            ) {
                statement.execute(query);
            }
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    /**
     * Add the table in the impala.
     *
     * @param create    statement used to create the table
     * @param tableName Name of the table to add
     * @throws SQLException if unable to execute statement
     */
    public void createTable(String create, String tableName)
            throws SQLException {
        String drop = "DROP TABLE IF EXISTS " + tableName;

        try {
            Class.forName(jdbcDriverName);
            try (
                Connection connection = DriverManager
                    .getConnection(connectionUrl);
                Statement statement = connection.createStatement();
            ) {
                // Import table
                statement.execute(drop);
                statement.execute(create);
            }
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    /**
     * Execute SQL query on the impala tables.
     *
     * @param sqlStatement Sql string
     * @return List of the result rows
     * @throws SQLException if unable to execute query
     */
    public List<List<String>> executeQuery(String sqlStatement)
            throws SQLException {
        List<List<String>> resultList = new ArrayList<>();

        try {
            Class.forName(jdbcDriverName);
            try (
                Connection connection = DriverManager
                    .getConnection(connectionUrl);
                Statement statement = connection.createStatement();
            ) {
                ResultSet resultSet = statement.executeQuery(sqlStatement);
                ResultSetMetaData rsmd = resultSet.getMetaData();
                int columnsNumber = rsmd.getColumnCount();
                List<String> header = new ArrayList<>();

                // Header
                for (int i = 1; i <= columnsNumber; i++) {
                    header.add(rsmd.getColumnName(i));
                }

                // Add the header
                resultList.add(header);

                // Parsing the returned result
                while (resultSet.next()) {
                    List<String> rowList = new ArrayList<>();

                    for (int i = 1; i <= columnsNumber; i++) {
                        if (resultSet.getString(i) != null) {
                            rowList.add(resultSet.getString(i));
                        } else {
                            rowList.add("null");
                        }
                    }

                    resultList.add(rowList);
                }
            }
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }

        return resultList;
    }
}
