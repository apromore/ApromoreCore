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

package org.apromore.plugin.portal.account;

import java.util.Locale;
import org.apromore.commons.config.ConfigBean;
import org.apromore.plugin.portal.DefaultPortalPlugin;
import org.apromore.plugin.portal.PortalContext;
import org.apromore.plugin.portal.PortalLoggerFactory;
import org.apromore.service.SecurityService;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;


@Component
public class ChangePasswordPlugin extends DefaultPortalPlugin {

  private static Logger LOGGER = PortalLoggerFactory.getLogger(ChangePasswordPlugin.class);

  private String label = "Change password";

  private SecurityService securityService;

  @Autowired
  ConfigBean configBean;

  ChangePasswordPlugin(SecurityService newSecurityService) {
    this.securityService = newSecurityService;
  }

  // PortalPlugin overrides

  @Override
  public Availability getAvailability() {

    boolean isUseKeycloakSso = configBean.getKeycloak().isEnabled();
    isUseKeycloakSso = false; // always show change password for now

    return isUseKeycloakSso ? Availability.UNAVAILABLE : Availability.AVAILABLE;
  }

  @Override
  public String getLabel(Locale locale) {
    return label;
  }

  @Override
  public String getIconPath() {
    return "change-password-icon.svg";
  }

  @Override
  public void execute(PortalContext portalContext) {
    new ChangePasswordController(portalContext, securityService);
  }
}
