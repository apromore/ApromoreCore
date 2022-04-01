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
package org.apromore.plugin.portal;

// Java 2 Standard
import java.util.Locale;

// Third party
import org.zkoss.zul.Listcell;
import org.zkoss.zul.Listheader;
import org.apromore.plugin.ParameterAwarePlugin;
import org.apromore.portal.model.FolderType;
import org.apromore.portal.model.LogSummaryType;
import org.apromore.portal.model.ProcessSummaryType;

/**
 * Plug-in interface for an extra column in the Portal's process summary list.
 */
public interface PortalProcessAttributePlugin extends ParameterAwarePlugin {

    /**
     * Present the column header.
     */
    Listheader getListheader();

    /**
     * Present the column value for a process model within a process summary row
     *
     * Note that it's ineffective to set the width on the listcell; you must set it on the listheader instead.
     *
     * @param process 
     */
    Listcell getListcell(ProcessSummaryType process);

    /**
     * Present the column value for a log within a process summary row
     *
     * @param log
     */
    Listcell getListcell(LogSummaryType log);

    /**
     * Present the column value for a folder within a process summary row
     *
     * @param folder
     */
    Listcell getListcell(FolderType folder);
}
