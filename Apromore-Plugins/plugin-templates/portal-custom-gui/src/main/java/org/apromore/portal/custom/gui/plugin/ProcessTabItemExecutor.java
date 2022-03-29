/*-
 * #%L
 * This file is part of "Apromore Core".
 * 
 * Copyright (C) 2016 - 2017 Queensland University of Technology.
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

package org.apromore.portal.custom.gui.plugin;

import java.util.HashSet;

import org.apromore.plugin.portal.MainControllerInterface;
import org.apromore.plugin.portal.PortalLoggerFactory;
import org.apromore.plugin.property.RequestParameterType;
import org.apromore.portal.custom.gui.tab.TabItemExecutor;
import org.apromore.portal.custom.gui.tab.impl.ProcessSummaryRowValue;
import org.apromore.portal.custom.gui.tab.impl.TabItem;
import org.apromore.portal.model.ProcessSummaryType;
import org.apromore.portal.model.VersionSummaryType;
import org.slf4j.Logger;

/**
 * Created by Raffaele Conforti (conforti.raffaele@gmail.com) on 2/05/2016.
 */
public class ProcessTabItemExecutor implements TabItemExecutor {

    private static final Logger LOGGER = PortalLoggerFactory.getLogger(ProcessTabItemExecutor.class);

    private MainControllerInterface mainControllerInterface;

    public ProcessTabItemExecutor(MainControllerInterface mainControllerInterface) {
        this.mainControllerInterface = mainControllerInterface;
    }

    @Override
    public void execute(TabItem listItem) {
        ProcessSummaryType pst = createProcessSummaryType((ProcessSummaryRowValue) listItem.getTabRowValue());
        VersionSummaryType vst = createVersionSummaryType((ProcessSummaryRowValue) listItem.getTabRowValue());
        try {
            mainControllerInterface.editProcess2(pst, vst, pst.getOriginalNativeType(), new HashSet<RequestParameterType<?>>(), false);
        } catch (InterruptedException e) {
            LOGGER.error("Unable to execute tab item for process model " + pst + " (version " + vst + ")", e);
        }
    }

    protected ProcessSummaryType createProcessSummaryType(ProcessSummaryRowValue rowValue) {
        return rowValue.getProcessSummaryType();
    }

    protected VersionSummaryType createVersionSummaryType(ProcessSummaryRowValue rowValue) {
        return rowValue.getVersionSummaryType();
    }
}
