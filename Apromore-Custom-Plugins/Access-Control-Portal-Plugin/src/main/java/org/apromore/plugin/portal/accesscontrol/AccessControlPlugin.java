/*-
 * #%L
 * This file is part of "Apromore Core".
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

package org.apromore.plugin.portal.accesscontrol;

import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Locale;
import java.util.Map;
import java.util.ResourceBundle;
import javax.inject.Inject;

import org.apromore.commons.config.ConfigBean;
import org.apromore.dao.model.User;
import org.apromore.plugin.portal.DefaultPortalPlugin;
import org.apromore.plugin.portal.PortalContext;
import org.apromore.plugin.portal.PortalLoggerFactory;
import org.apromore.plugin.portal.accesscontrol.controllers.SecuritySetupController;
import org.apromore.portal.common.ItemHelpers;
import org.apromore.portal.common.UserSessionManager;
import org.apromore.portal.dialogController.MainController;
import org.apromore.service.SecurityService;
import org.apromore.zk.notification.Notification;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.zkoss.web.Attributes;
import org.zkoss.zk.ui.Execution;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.Sessions;
import org.zkoss.zk.ui.metainfo.PageDefinition;
import org.zkoss.zul.Messagebox;
import org.zkoss.zul.Window;

@Component("accessControlPlugin")
public class AccessControlPlugin extends DefaultPortalPlugin {

  private static final Logger LOGGER = PortalLoggerFactory.getLogger(AccessControlPlugin.class);

  private String label = "Manage access control";

  @Autowired
  ConfigBean configBean;

  @Inject
  private SecurityService securityService;

  @Override
  public String getLabel(final Locale locale) {
    return label;
  }

  @Override
  public String getIconPath() {
    return "/share-icon.svg";
  }

  @Override
  public Availability getAvailability() {
    return Availability.HIDDEN;
  }

  public ResourceBundle getLabels() {
    // Locale locale = Locales.getCurrent()
    Locale locale = (Locale) Sessions.getCurrent().getAttribute(Attributes.PREFERRED_LOCALE);
    return ResourceBundle.getBundle("access-control", locale,
        AccessControlPlugin.class.getClassLoader());
  }

  public String getLabel(String key) {
    return getLabels().getString(key);
  }

  @Override
  public void execute(final PortalContext portalContext) {
    LOGGER.info("execute");
    Map arg = getSimpleParams();
    Boolean enablePublish = (Boolean) arg.get("enablePublish");
    if (enablePublish == null) {
      arg.put("enablePublish", configBean.isEnablePublish());
    }
    Boolean enableUsersList = (Boolean) arg.get("enableUsersList");
    if (enableUsersList == null) {
      arg.put("enableUsersList", configBean.isEnableUsersList());
    }

    try {
      boolean canShare = false;
      Object selectedItem = arg.get("selectedItem");
      User currentUser = securityService.getUserById(portalContext.getCurrentUser().getId());
      if (selectedItem != null) {
        canShare = ItemHelpers.isOwner(currentUser, selectedItem);
      }
      boolean withFolderTree = (boolean) arg.getOrDefault("withFolderTree", false);
      if (withFolderTree) {
        new SecuritySetupController((MainController) portalContext.getMainController(),
            UserSessionManager.getCurrentUser(), selectedItem, canShare);
      } else {
        if (canShare) {

          PageDefinition pageDefinition = getPageDefination("accesscontrol/zul/share.zul");

          Window window =
              (Window) Executions.getCurrent().createComponents(pageDefinition, null, arg);

          window.doModal();
        } else {
          Notification.error(getLabel("onlyOwnerCanShare_message"));
        }
      }
    } catch (Exception e) {
      Messagebox.show(e.getMessage(), "Apromore", Messagebox.OK, Messagebox.ERROR);
    }
  }

  private PageDefinition getPageDefination(String uri) throws IOException {
    Execution current = Executions.getCurrent();
    PageDefinition pageDefinition = current.getPageDefinitionDirectly(
        new InputStreamReader(getClass().getClassLoader().getResourceAsStream(uri)), "zul");
    return pageDefinition;
  }
}
