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

package org.apromore.plugin.portal.account;

import java.awt.image.BufferedImage;
import java.awt.image.RenderedImage;
import java.io.IOException;
import java.io.InputStream;
import java.util.Locale;
import javax.imageio.ImageIO;
import org.apromore.plugin.portal.DefaultPortalPlugin;
import org.apromore.plugin.portal.PortalContext;
import org.apromore.zk.label.LabelSupplier;
import org.springframework.stereotype.Component;
import org.zkoss.zk.ui.Sessions;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zk.ui.event.EventQueues;
import org.zkoss.zul.Messagebox;

@Component
public class SignOutPlugin extends DefaultPortalPlugin implements LabelSupplier {

  // PortalPlugin overrides

  @Override
  public String getBundleName() {
    return "account";
  }

  @Override
  public String getLabel(Locale locale) {
        return getLabel("signOut");
    }

  @Override
  public RenderedImage getIcon() {
    try (InputStream in = getClass().getClassLoader().getResourceAsStream("sign-out-icon.png")) {
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
    Messagebox.show(getLabel("warnLogout"), getLabel("titleLogout"), Messagebox.YES | Messagebox.NO,
        Messagebox.QUESTION, new EventListener<Event>() {
          public void onEvent(Event evt) throws Exception {
            switch ((Integer) evt.getData()) {
              case Messagebox.YES:
                EventQueues.lookup("signOutQueue", EventQueues.DESKTOP, true)
                    .publish(new Event("onSignout", null, Sessions.getCurrent()));
                break;
              case Messagebox.NO:
                break;
            }
          }
        });
  }
}
