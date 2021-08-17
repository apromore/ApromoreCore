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
import org.apromore.plugin.portal.processdiscoverer.components.CaseDetailsController;
import org.apromore.plugin.portal.processdiscoverer.components.CaseVariantDetailsController;
import org.apromore.plugin.portal.processdiscoverer.components.GraphSettingsController;
import org.apromore.plugin.portal.processdiscoverer.components.GraphVisController;
import org.apromore.plugin.portal.processdiscoverer.components.LogStatsController;
import org.apromore.plugin.portal.processdiscoverer.components.PerspectiveDetailsController;
import org.apromore.plugin.portal.processdiscoverer.components.TimeStatsController;
import org.apromore.plugin.portal.processdiscoverer.components.ToolbarController;
import org.apromore.plugin.portal.processdiscoverer.components.ViewSettingsController;
import org.apromore.plugin.portal.processdiscoverer.eventlisteners.AnimationController;
import org.apromore.plugin.portal.processdiscoverer.eventlisteners.BPMNExportController;
import org.apromore.plugin.portal.processdiscoverer.eventlisteners.LogExportController;
import org.apromore.plugin.portal.processdiscoverer.eventlisteners.LogFilterController;

public class PDStandardFactory implements PDFactory {
    @Override
    public GraphVisController createGraphVisController(PDController pdController)  throws Exception {
        return new GraphVisController(pdController);
    }

    @Override
    public GraphSettingsController createGraphSettingsController(PDController pdController)  throws Exception {
        return new GraphSettingsController(pdController);
    }

    @Override
    public ViewSettingsController createViewSettingsController(PDController pdController) throws Exception {
        return new ViewSettingsController(pdController);
    }

    @Override
    public LogStatsController createLogStatsController(PDController pdController) throws Exception {
        return new LogStatsController(pdController);
    }

    @Override
    public TimeStatsController createTimeStatsController(PDController pdController) throws Exception {
        return new TimeStatsController(pdController);
    }

    @Override
    public CaseDetailsController createCaseDetailsController(PDController pdController) throws Exception {
        return new CaseDetailsController(pdController);
    }

    @Override
    public CaseVariantDetailsController createCaseVariantDetailsController(PDController pdController) throws Exception {
        return new CaseVariantDetailsController(pdController);
    }

    @Override
    public PerspectiveDetailsController createPerspectiveDetailsController(PDController pdController) throws Exception {
        return new PerspectiveDetailsController(pdController);
    }

    @Override
    public LogFilterController createLogFilterController(PDController pdController) throws Exception {
        return new LogFilterController(pdController);
    }

    @Override
    public AnimationController createAnimationController(PDController pdController) throws Exception {
        return new AnimationController(pdController);
    }

    @Override
    public BPMNExportController createBPMNExportController(PDController pdController) throws Exception {
        return new BPMNExportController(pdController, false);
    }

    @Override
    public LogExportController createLogExportController(PDController pdController) throws Exception {
        return new LogExportController(pdController);
    }

    @Override
    public ToolbarController createToolbarController(PDController controller) throws Exception {
        return new ToolbarController(controller);
    }
}
