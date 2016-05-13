/*
 * Copyright © 2009-2016 The Apromore Initiative.
 *
 * This file is part of "Apromore".
 *
 * "Apromore" is free software; you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 3 of the
 * License, or (at your option) any later version.
 *
 * "Apromore" is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this program.
 * If not, see <http://www.gnu.org/licenses/lgpl-3.0.html>.
 */
package org.apromore.plugin.portal;

// Java 2 Standard
import java.util.Locale;

// Third party
import org.zkoss.zul.Listcell;

// First party
import org.apromore.model.ProcessSummaryType;
import org.apromore.plugin.ParameterAwarePlugin;

/**
 * Plug-in interface for an extra column in the Portal's process summary list.
 */
public interface PortalProcessAttributePlugin extends ParameterAwarePlugin {

    /**
     * Attribute name, used as the column heading.
     *
     * @param locale (optional locale)
     * @return
     */
    String getLabel(Locale locale);

    /**
     * Present the column value for a particular process summary row
     *
     * @param process 
     */
    Listcell getListcell(ProcessSummaryType process);
}
