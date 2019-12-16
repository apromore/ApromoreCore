/*
 * Copyright © 2009-2019 The Apromore Initiative.
 *
 * This file is part of "Apromore".
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

package org.apromore.plugin.portal.account;

import java.awt.image.BufferedImage;
import java.awt.image.RenderedImage;
import java.io.InputStream;
import java.io.IOException;
import java.util.Locale;
import javax.imageio.ImageIO;
import org.apromore.plugin.portal.DefaultPortalPlugin;
import org.apromore.plugin.portal.PortalContext;
import org.apromore.portal.common.UserSessionManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zul.Messagebox;

public class SignOutPlugin extends DefaultPortalPlugin {

    private static Logger LOGGER = LoggerFactory.getLogger(SignOutPlugin.class);

    private String label = "Sign Out";
    private String groupLabel = "Account";

    // PortalPlugin overrides

    @Override
    public String getLabel(Locale locale) {
        return label;
    }

    @Override
    public String getGroupLabel(Locale locale) {
        return groupLabel;
    }

    @Override
    public RenderedImage getIcon() {
        try (InputStream in = getClass().getClassLoader().getResourceAsStream("/sign-out-icon.png")) {
            BufferedImage icon = ImageIO.read(in);
            return icon;

        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public String getIconPath() {
        return "sign-out-icon.svg";
    }

    @Override
    public void execute(PortalContext portalContext) {
        Messagebox.show("Are you sure you want to logout?", "Logout", Messagebox.YES | Messagebox.NO, Messagebox.QUESTION,
                new EventListener<Event>() {
                    public void onEvent(Event evt) throws Exception {
                        switch ((Integer) evt.getData()) {
                            case Messagebox.YES:
                                UserSessionManager.setCurrentFolder(null);
                                UserSessionManager.setCurrentSecurityItem(0);
                                UserSessionManager.setMainController(null);
                                UserSessionManager.setPreviousFolder(null);
                                UserSessionManager.setSelectedFolderIds(null);
                                UserSessionManager.setTree(null);
                                //getService().writeUser(UserSessionManager.getCurrentUser());
                                Executions.sendRedirect("/j_spring_security_logout");
                                break;
                            case Messagebox.NO:
                                break;
                        }
                    }
                }
        );
    }
}
