/*-
 * #%L
 * This file is part of "Apromore Core".
 * 
 * Copyright (C) 2019 The University of Tartu.
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

package org.apromore.service.csvexporter;

import org.apromore.apmlog.APMLog;
import org.deckfour.xes.model.XLog;

import java.io.File;
import java.nio.file.Path;

public interface CSVExporterLogic {

    /**
     * Take XLog as input and generate a CSV File
     *
     * @param myLog XLog
     * @return CSV File
     */
    Path exportCSV(APMLog myLog, String encoding);

    Path generateCSV(APMLog apmLog);
}
