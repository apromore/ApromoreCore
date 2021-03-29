/*-
 * #%L
 * This file is part of "Apromore Core".
 * 
 * Copyright (C) 2011 - 2017 Queensland University of Technology.
 * Copyright (C) 2012 Felix Mannhardt.
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

package org.apromore.portal.dialogController;


import org.apromore.plugin.portal.PortalContext;
import org.apromore.plugin.portal.PortalPlugin;
import org.apromore.plugin.portal.SessionTab;
import org.apromore.plugin.property.RequestParameterType;
import org.apromore.portal.common.UserSessionManager;
import org.apromore.portal.context.PluginPortalContext;
import org.apromore.portal.context.PortalPluginResolver;
import org.apromore.portal.model.*;
import org.zkoss.zul.*;
import org.zkoss.util.resource.Labels;

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
        Div registerBtn = (Div) mainW.getFellow("registerBtn");
        Div agree = (Div) mainW.getFellow("agree");
        Div subscribe = (Div) mainW.getFellow("subscribe");
        Div role = (Div) mainW.getFellow("role");
        Div organization = (Div) mainW.getFellow("organization");
        Div country = (Div) mainW.getFellow("country");
        Div phone = (Div) mainW.getFellow("phone");
        Html ppAgree = (Html) mainW.getFellow("ppAgree");
        Html andAgree = (Html) mainW.getFellow("andAgree");
        Html tcAgree = (Html) mainW.getFellow("tcAgree");
        Html ppLink = (Html) mainW.getFellow("ppLink");
        Html tcLink = (Html) mainW.getFellow("tcLink");
        Image logoWithTag = (Image) mainW.getFellow("logoWithTag");
        String src = "~./themes/" + Labels.getLabel("theme") + "/common/img/brand/logo-colour-with-tag";

        boolean enableTC = config.isEnableTC();
        boolean enablePP = config.isEnablePP();
        boolean enableUserReg = config.isEnableUserReg();
        boolean enableFullUserReg = config.isEnableFullUserReg();
        boolean enableSubscription = config.isEnableSubscription();

        registerBtn.setVisible(enableUserReg);
        subscribe.setVisible(enableSubscription);
        tcLink.setVisible(enableTC);
        ppLink.setVisible(enablePP);
        tcAgree.setVisible(enableTC);
        ppAgree.setVisible(enablePP);
        agree.setVisible(enableTC || enablePP);
        andAgree.setVisible(enableTC && enablePP);

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
