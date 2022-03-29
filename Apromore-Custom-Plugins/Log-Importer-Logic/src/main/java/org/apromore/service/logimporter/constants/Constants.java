/*-
 * #%L
 * This file is part of "Apromore Core".
 *
 * Copyright (C) 2020 University of Tartu
 * %%
 * Copyright (C) 2018 - 2022 Apromore Pty Ltd.
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

package org.apromore.service.logimporter.constants;

public interface Constants {

    char[] supportedSeparators = {',', '|', ';', '\t'};

    String POSSIBLE_CASE_ID = "^((case)|(.*(case|service|event)(\\s|-|_)?(id)).*)$";
    String POSSIBLE_ACTIVITY = "^((activity|operation)(.*)|event)$";
    String POSSIBLE_RESOURCE = "^(resource|agent|employee|group)$";
    String POSSIBLE_ROLE = "^role$";
    String POSSIBLE_END_TIMESTAMP = "^((.*(end|complete|completion).*)|(time:)?timestamp|date & time)$";
    String POSSIBLE_START_TIMESTAMP = "^(.*start.*)$";
    String POSSIBLE_OTHER_TIMESTAMP = "^(.*(date|time).*)$";
    String TIMESTAMP_PATTERN = "^(\\d{1,2}|\\d{4})([/\\-.])\\d{1,2}([/\\-.])(\\d{1,2}.*)$";

    // File extension will be converted to lowercase before comparisons, so only use lowercase characters
    String CSV_FILE_EXTENSION = "csv";
    String PARQUET_FILE_EXTENSION = "parquet";
    String PARQ_FILE_EXTENSION = "parq";
    String XLS_FILE_EXTENSION = "xls";
    String XLSX_FILE_EXTENSION = "xlsx";
    String XES_EXTENSION = "xes.gz";
}
