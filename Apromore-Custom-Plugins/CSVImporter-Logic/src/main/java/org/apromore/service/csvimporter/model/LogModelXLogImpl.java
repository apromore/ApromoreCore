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

import org.deckfour.xes.model.XLog;

import java.util.List;

//Upon migrating to parquet, xlog need to be removed and LogModelXLogImpl need to be renamed
public class LogModelXLogImpl implements LogModel {
    private XLog xLog;
    private List<LogErrorReport> logErrorReport;
    private boolean rowLimitExceeded = false;
    private int numOfEvents;

    public LogModelXLogImpl(XLog xLog, List<LogErrorReport> logErrorReportImpl, boolean rowLimitExceeded, int numOfEvents) {
        this.xLog = xLog;
        this.logErrorReport = logErrorReportImpl;
        this.rowLimitExceeded = rowLimitExceeded;
        this.numOfEvents = numOfEvents;
    }

    @Override
    public int getRowsCount() {
        return numOfEvents;
    }

    @Override
    public List<LogErrorReport> getLogErrorReport() {
        return logErrorReport;
    }

    @Override
    public XLog getXLog() {
        return xLog;
    }

    @Override
    public boolean isRowLimitExceeded() {
        return rowLimitExceeded;
    }

}
