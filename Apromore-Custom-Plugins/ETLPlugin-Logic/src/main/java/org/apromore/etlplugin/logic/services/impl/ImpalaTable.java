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

import org.apache.commons.io.FilenameUtils;
import org.apromore.etlplugin.logic.utils.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.List;

/**
 * Add Table to Impala Handler.
 */
@Component
public class ImpalaTable {
    @Autowired
    ImpalaJdbcAdaptor impalaJdbcAdaptor;

    // Impala connection info
    private final String dataPath = System.getProperty("java.io.tmpdir") +
        System.getenv("DATA_STORE");

    private String getColumnsFrom(File file) throws IOException {
        try (
            FileInputStream fileInputStream = new FileInputStream(file);
            InputStreamReader inputStreamReader = new InputStreamReader(
                fileInputStream,
                StandardCharsets.UTF_8);
            BufferedReader br = new BufferedReader(inputStreamReader);
        ) {
            String columns = "";

            List<String> headers = Arrays.asList(br.readLine().split(","));
            List<String> firstRow = Arrays.asList(br.readLine().split(","));

            for (int i = 0; i < headers.size(); i++) {
                columns += String.format(
                    "`%s` %s, ",
                    headers.get(i),
                    StringUtils.getColumnType(firstRow.get(i)));
            }

            return columns;
        }
    }

    /**
     * Create a table from a parquet file.
     *
     * @param tableName name of the table to create
     * @param fileName  name of the file
     * @throws SQLException if unable to execute statement
     */
    public void createParquetTable(String tableName, String fileName)
            throws SQLException {
        String dir = dataPath + "/" + FilenameUtils.removeExtension(fileName);

        String create = "CREATE EXTERNAL TABLE `%s` " +
            "LIKE PARQUET '%s' " +
            "STORED AS PARQUET " +
            "LOCATION '%s'";

        create = String.format(create, tableName, dir + "/" + fileName, dir);

        impalaJdbcAdaptor.createTable(create, tableName);
    }

    /**
     * Create a table from a csv file.
     *
     * @param tableName name of the table to create
     * @param fileName  name of the file
     * @throws IOException  if unable to read file
     * @throws SQLException if unable to execute statement
     */
    public void createCsvTable(String tableName, String fileName)
            throws IOException, SQLException {
        String dir = dataPath + "/" + FilenameUtils.removeExtension(fileName) +
            "_csv";
        File file = new File(dir + "/" + fileName);

        String columns = getColumnsFrom(file);

        // Create Table in CSV/Textfile format
        String create = "CREATE EXTERNAL TABLE `%s` (%s) " +
            "ROW FORMAT DELIMITED FIELDS TERMINATED BY ',' " +
            "LINES TERMINATED BY '\n' " +
            "STORED AS TEXTFILE " +
            "LOCATION '%s' " +
            "TBLPROPERTIES('skip.header.line.count'='1')";

        create = String.format(
            create,
            tableName + "_csv",
            columns.substring(0, columns.length() - 2),
            dir);

        impalaJdbcAdaptor.createTable(create, tableName + "_csv");

        // Create File in Parquet format
        String query = "CREATE EXTERNAL TABLE `%s` " +
            "LIKE `%s` " +
            "STORED AS PARQUET " +
            "LOCATION '%s'";

        query = String.format(
            query,
            tableName,
            tableName + "_csv",
            dataPath + "/" + tableName);

        impalaJdbcAdaptor.createTable(query, tableName);

        impalaJdbcAdaptor.execute(
            String.format(
                "INSERT OVERWRITE TABLE `%s` SELECT * FROM `%s`",
                tableName,
                tableName + "_csv"));

        impalaJdbcAdaptor.execute(
            String.format("DROP TABLE IF EXISTS `%s`", tableName + "_csv"));
    }

    /**
     * Create a parquet table from result of a query.
     *
     * @param tableName name of created table
     * @param query query string
     * @throws SQLException throw SQLException
     */
    public void createTableFromQuery(String tableName, String query)
        throws SQLException {

        String dir = dataPath + "/" + tableName;

        String create = "CREATE EXTERNAL TABLE `%s` " +
            "STORED AS PARQUET " +
            "LOCATION '%s' " +
            "AS %s";

        create = String.format(create, tableName, dir, query);

        impalaJdbcAdaptor.createTable(create, tableName);
    }
}
