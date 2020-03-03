/*
 * This file is part of "Apromore".
 *
 * Copyright (C) 2019 - 2020 The University of Melbourne.
 *
 * "Apromore" is free software; you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 3 of the
 * License, or (at your option) any later version.
 *
 * "Apromore" is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty
 * of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this program.
 * If not, see <http://www.gnu.org/licenses/lgpl-3.0.html>.
 */
package org.apromore.apmlog.impl;

import org.apromore.apmlog.APMLog;
import org.apromore.apmlog.APMLogService;
//import org.apromore.service.EventLogService;
import org.deckfour.xes.model.XLog;

/**
 * Frank Ma (16/11/2019)
 */
public class APMLogServiceImpl implements APMLogService {

    //private EventLogService eventLogService;

    //APMLogServiceImpl(final EventLogService newEventLogService) {
    //    this.eventLogService = newEventLogService;
    //}

    @Override
    public APMLog findAPMLogForXLog(XLog xLog) {
        return new APMLog(xLog);
    }
}