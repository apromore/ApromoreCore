/*
 * This file is part of "Apromore".
 *
 * Copyright (C) 2017 Queensland University of Technology.
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

package org.apromore.plugin.portal.loganimation;

import org.apromore.plugin.portal.PortalContext;
import org.deckfour.xes.model.XLog;

/**
 * Created by Raffaele Conforti (conforti.raffaele@gmail.com) on 27/10/17.
 */
public interface LogAnimationPluginInterface {

//    void execute(PortalContext portalContext, ProcessSummaryType processSummaryType, VersionSummaryType versionSummaryType, LogSummaryType logSummaryType);
//    void execute(PortalContext portalContext, String JSONData, String layout, LogSummaryType logSummaryType);
    void execute(PortalContext portalContext, String JSONData, String layout, XLog eventlog, boolean maintain_gateways, String logName);

}
