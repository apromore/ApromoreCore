/*-
 * #%L
 * This file is part of "Apromore Core".
 *
 * Copyright (C) 2011 - 2017 Queensland University of Technology.
 * Copyright (C) 2012 Felix Mannhardt.
 * %%
 * Copyright (C) 2018 - 2020 The University of Melbourne.
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

package org.apromore.portal.dialogController;

import org.apromore.model.*;
import org.apromore.model.Detail;
import org.apromore.plugin.portal.PortalContext;
import org.apromore.plugin.portal.PortalPlugin;
import org.apromore.plugin.portal.SessionTab;
import org.apromore.plugin.property.RequestParameterType;
import org.apromore.portal.ConfigBean;
import org.apromore.portal.common.UserSessionManager;
import org.apromore.portal.context.PluginPortalContext;
import org.apromore.portal.context.PortalPluginResolver;
import org.zkoss.zul.*;
import org.zkoss.util.resource.Labels;

import java.io.IOException;
import java.text.ParseException;
import java.util.*;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LoginController extends BaseController {

    private static final Logger LOGGER = LoggerFactory.getLogger(LoginController.class);
    public LoginController() {
        super();
    }

    /**
     * onCreate is executed after the main window has been created it is
     * responsible for instantiating all necessary controllers (one for each
     * window defined in the interface) see description in index.zul
     * @throws InterruptedException
     */
    public void onCreate() throws InterruptedException {
        Window mainW = (Window) this.getFellow("login-main");
        Div agree = (Div) mainW.getFellow("agree");
        Div role = (Div) mainW.getFellow("role");
        Div organization = (Div) mainW.getFellow("organization");
        Div country = (Div) mainW.getFellow("country");
        Div phone = (Div) mainW.getFellow("phone");
        Image logoWithTag = (Image) mainW.getFellow("logoWithTag");
        String src = "/themes/" + Labels.getLabel("theme") + "/common/img/brand/logo-colour-with-tag";

        boolean enableTC = config.getEnableTC();
        boolean enableFullUserReg = config.getEnableFullUserReg();
        agree.setVisible(enableTC);
        if (enableFullUserReg) {
            role.setVisible(true);
            organization.setVisible(true);
            country.setVisible(true);
            phone.setVisible(true);
        }

        if (config.isCommunity()) {
            src += "-" + "community";
        }
        logoWithTag.setSrc(src + ".svg");
    }

}