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

package org.apromore.service.csvimporter.model;

import org.apromore.service.csvimporter.services.legecy.CreateXLog;
import org.deckfour.xes.model.XLog;

import java.util.List;

public class LogModelImpl implements LogModel {

    private List<LogEventModel> rows;
    private List<LogErrorReport> logErrorReport;
    private boolean rowLimitExceeded = false;

    public LogModelImpl(List<LogEventModel> rows, List<LogErrorReport> logErrorReportImpl, boolean rowLimitExceeded) {
        this.rows = rows;
        this.logErrorReport = logErrorReportImpl;
        this.rowLimitExceeded = rowLimitExceeded;
    }

    @Override
    public List<LogErrorReport> getLogErrorReport() {
        return logErrorReport;
    }

    @Override
    public int getRowsCount() {
        return rows.size();
    }

    @Override
    public XLog getXLog() {
        return new CreateXLog().generateXLog(rows);
    }

    @Override
    public boolean isRowLimitExceeded() { return rowLimitExceeded; }
}
