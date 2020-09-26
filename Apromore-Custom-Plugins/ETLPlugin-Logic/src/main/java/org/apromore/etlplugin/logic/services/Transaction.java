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
package org.apromore.etlplugin.logic.services;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

/**
 * Transaction Methods Interface for impala.
 */
public interface Transaction {
    /**
     * Separate add table method to add tables in Impala.
     *
     * @param fileName File to add
     * @throws SQLException if unable to execute statement
     * @throws IOException  if unable to read file
     */
    void addTable(String fileName) throws IOException, SQLException;

    /**
     * Join tables and export.
     *
     * @param query is the string query to execute.
     * @throws SQLException If unable to execute sql query
     */
    void exportQuery(String query) throws SQLException;

    /**
     * Executes the query in Impala.
     *
     * @param query is the query to be executed.
     * @param removeHeader whether the header should be removed.
     * @return 2D list of output rows
     * @throws SQLException if unable to execute sql query.
     */
    List<List<String>> executeQuery(String query,
                                    boolean removeHeader) throws SQLException;
}
