/*-
 * #%L
 * This file is part of "Apromore Core".
 * %%
 * Copyright (C) 2018 - 2021 Apromore Pty Ltd.
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
package org.apromore.plugin.portal.processdiscoverer.impl.factory;

import org.apromore.plugin.portal.processdiscoverer.PDController;
import org.apromore.plugin.portal.processdiscoverer.actionlisteners.AnimationController;
import org.apromore.plugin.portal.processdiscoverer.actionlisteners.BPMNExportController;
import org.apromore.plugin.portal.processdiscoverer.actionlisteners.LogExportController;
import org.apromore.plugin.portal.processdiscoverer.actionlisteners.LogFilterController;
import org.apromore.plugin.portal.processdiscoverer.actions.UndoRedoController;
import org.apromore.plugin.portal.processdiscoverer.components.CaseDetailsController;
import org.apromore.plugin.portal.processdiscoverer.components.GraphSettingsController;
import org.apromore.plugin.portal.processdiscoverer.components.GraphVisController;
import org.apromore.plugin.portal.processdiscoverer.components.LogStatsController;
import org.apromore.plugin.portal.processdiscoverer.components.PerspectiveDetailsController;
import org.apromore.plugin.portal.processdiscoverer.components.TimeStatsController;
import org.apromore.plugin.portal.processdiscoverer.components.ToolbarController;
import org.apromore.plugin.portal.processdiscoverer.components.ViewSettingsController;

public interface PDFactory {
    GraphVisController createGraphVisController(PDController pdController) throws Exception;
    GraphSettingsController createGraphSettingsController(PDController pdController) throws Exception;
    ViewSettingsController createViewSettingsController(PDController pdController) throws Exception;
    LogStatsController createLogStatsController(PDController pdController) throws Exception;
    TimeStatsController createTimeStatsController(PDController pdController) throws Exception;
    CaseDetailsController createCaseDetailsController(PDController pdController) throws Exception;
    PerspectiveDetailsController createPerspectiveDetailsController(PDController pdController) throws Exception;
    LogFilterController createLogFilterController(PDController pdController) throws Exception;
    AnimationController createAnimationController(PDController pdController) throws Exception;
    BPMNExportController createBPMNExportController(PDController pdController) throws Exception;
    LogExportController createLogExportController(PDController pdController) throws Exception;
    ToolbarController createToolbarController(PDController controller) throws Exception;
    UndoRedoController createUndoRedoController(PDController pdController) throws Exception;
}
