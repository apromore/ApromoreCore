/*
 * This file is part of "Apromore".
 *
 * Copyright (C) 2016 - 2017 Queensland University of Technology.
 * Copyright (C) 2018, 2020 The University of Melbourne.
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

package org.apromore.plugin.portal;

import org.apromore.model.ClusterFilterType;
import org.apromore.model.FolderType;
import org.apromore.model.ProcessSummaryType;
import org.apromore.model.UserType;

/**
 * Communication interface that provides access to the Apromore portal
 */
public interface PortalContext {

    /**
     * @return a PortalSelection object with information on the currently selected objects (process models) in the portal
     */
    PortalSelection getSelection();

    //TODO expose the manager web-service client
    // getManagerClient();

    /**
     * @return a PortalUI object that MUST be used to generate new UI elements using the ZK library.
     */
    PortalUI getUI();

    /**
     * Request to add a <var>process</var> to the table.
     *
     * @param process
     */
    void displayNewProcess(ProcessSummaryType process);

    /**
     * @return the current folder displayed by the portal screen
     */
    FolderType getCurrentFolder();

    /**
     * @return the authenticated user
     */
    UserType getCurrentUser();
    
    /**
     * Get attributes stored in the user session
     * Created by Bruce 17.05.2019
     * @param attribute
     * @return
     */
    Object getAttribute(String attribute);

    void refreshContent();

    void displaySimilarityClusters(final ClusterFilterType filter);

    MainControllerInterface getMainController();
}
