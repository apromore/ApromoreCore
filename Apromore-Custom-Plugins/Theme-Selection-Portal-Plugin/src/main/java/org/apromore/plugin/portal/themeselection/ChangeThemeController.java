/*
 * This file is part of "Apromore".
 *
 * Copyright (C) 2019 - 2020 The University of Melbourne.
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

package org.apromore.plugin.portal.themeselection;

import java.io.IOException;
import org.apromore.plugin.portal.PortalContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zk.ui.event.SelectEvent;
import org.zkoss.zul.Button;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.ListitemRenderer;
import org.zkoss.zul.ListModelArray;
import org.zkoss.zul.Messagebox;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Window;
import org.zkoss.zul.theme.Themes;

public class ChangeThemeController {

    private static Logger LOGGER = LoggerFactory.getLogger(ChangeThemeController.class);
    private static String THEME_ATTRIBUTE = "theme.name";

    public ChangeThemeController(PortalContext portalContext) {
        try {
            Window window = (Window) portalContext.getUI().createComponent(getClass().getClassLoader(), "zul/changeTheme.zul", null, null);

            ListModelArray<String> model = new ListModelArray<>(Themes.getThemes());
            model.addToSelection(Themes.getCurrentTheme());
            model.sort((a, b) -> Themes.getDisplayName(a).compareTo(Themes.getDisplayName(b)), true);

            Listbox themeListbox = (Listbox) window.getFellow("themeListbox");
            themeListbox.setModel(model);
            themeListbox.setItemRenderer(new ListitemRenderer<String>() {
                public void render(final Listitem listitem,
                                   final String   theme,
                                   final int      index) {

                    listitem.setAttribute(THEME_ATTRIBUTE, theme);
                    listitem.setLabel(Themes.getDisplayName(theme));
            }
            });
            themeListbox.addEventListener("onSelect", new EventListener<SelectEvent<Listitem, String>>() {
                public void onEvent(SelectEvent<Listitem, String> selectEvent) {
                    String newTheme = (String) selectEvent.getReference()
                                              .getAttribute(THEME_ATTRIBUTE);
                    LOGGER.info(String.format("Changed ZK theme: %s", newTheme));
                    Themes.setTheme(Executions.getCurrent(), newTheme);
                    Executions.sendRedirect("");
                }
            });  

            ((Button) window.getFellow("okButton")).addEventListener("onClick", new EventListener<Event>() {
                public void onEvent(Event event) throws Exception {
                    window.detach();
                }
            });

            window.doModal();

        } catch (IOException e) {
            LOGGER.warn("Unable to read changeTheme.zul", e);
            Messagebox.show("Unable to create dialog", "Attention", Messagebox.OK, Messagebox.ERROR);
        }
    }
}
