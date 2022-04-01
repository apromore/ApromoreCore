/*-
 * #%L
 * This file is part of "Apromore Core".
 *
 * Copyright (C) 2011 - 2017 Queensland University of Technology.
 * Copyright (C) 2012 Felix Mannhardt.
 * Copyright (C) 2015 Adriano Augusto.
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

package org.apromore.portal.controller;

import java.util.Map;
import org.apromore.plugin.portal.PortalContext;
import org.apromore.plugin.portal.PortalContexts;
import org.apromore.plugin.portal.PortalPlugin;
import org.apromore.portal.controller.helper.UserMetaDataUtilService;
import org.apromore.portal.context.PortalPluginResolver;
import org.apromore.portal.dialogController.MainController;
import org.apromore.portal.dialogController.PopupMenuController;
import org.apromore.portal.model.LogSummaryType;
import org.zkoss.zul.Menupopup;

abstract class PopupLogSubMenuController {
    protected PopupMenuController popupMenuController;
    protected MainController mainController;
    protected Menupopup popupMenu;
    protected final LogSummaryType logSummaryType;
    protected Map<String, PortalPlugin> portalPluginMap;
    protected UserMetaDataUtilService userMetaDataUtilService;
    protected static final String ON_CLICK = "onClick";
    protected static final String CENTRE_ALIGN = "vertical-align: middle; text-align:left;color: var(--ap-c-widget)";
    protected static final int SUBMENU_SIZE = 5;
    protected static final String USER_META_DATA = "USER_META_DATA";
    protected static final String CALENDAR_DATA = "CALENDAR_DATA";


    protected PopupLogSubMenuController(PopupMenuController popupMenuController, MainController mainController,
                                     Menupopup popupMenu,
                                     LogSummaryType logSummaryType) {
        this.popupMenuController = popupMenuController;
        this.mainController = mainController;
        this.popupMenu = popupMenu;
        this.logSummaryType = logSummaryType;
        portalPluginMap = PortalPluginResolver.getPortalPluginMap();
        userMetaDataUtilService = new UserMetaDataUtilService();
    }

    protected PortalContext getPortalContext() {
        return PortalContexts.getActivePortalContext();
    }

}
