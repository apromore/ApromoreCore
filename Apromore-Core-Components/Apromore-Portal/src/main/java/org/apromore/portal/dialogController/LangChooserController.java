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
package org.apromore.portal.dialogController;

import org.apromore.plugin.portal.PortalLoggerFactory;
import org.apromore.portal.common.i18n.I18nConfig;
import org.apromore.portal.common.i18n.I18nSession;
import org.slf4j.Logger;
import org.zkoss.util.resource.Labels;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zk.ui.util.Clients;
import org.zkoss.zul.Combobox;
import org.zkoss.zul.Comboitem;
import org.zkoss.zul.Messagebox;

import java.util.Locale;
import java.util.Map;

public class LangChooserController {

    private static final Logger LOGGER = PortalLoggerFactory.getLogger(LangChooserController.class);

    private Map<String, Object> argMap;
    private I18nSession i18nSession;
    private I18nConfig i18nConfig;
    private Combobox combobox;
    private MainController mainController;

    public LangChooserController(Combobox combobox, MainController mainController) throws Exception {
        argMap = (Map<String, Object>) Executions.getCurrent().getArg();
        this.combobox = combobox;
        this.mainController = mainController;
        i18nSession = mainController.getI18nSession();
        i18nConfig = i18nSession.getConfig();
        combobox.addEventListener("onSelect", new EventListener<Event>() {
            @Override
            public void onEvent(Event event) throws Exception {
                String langTag = combobox.getSelectedItem().getValue();
                if (langTag.equals("auto")) {
                    i18nSession.resetClientPreferredLocale();
                    i18nSession.applyLocaleFromClient();
                } else {
                    i18nSession.applyLocale(langTag);
                    i18nSession.pushClientPreferredLocale();
                }
                Messagebox.show(Labels.getLabel("portal_langChanged_message"),
                    Labels.getLabel("portal_langChanged_title"),
                    new Messagebox.Button[] {Messagebox.Button.OK, Messagebox.Button.CANCEL},
                    Messagebox.QUESTION,
                    new org.zkoss.zk.ui.event.EventListener() {
                        public void onEvent(Event e) {
                            String buttonName = e.getName();
                            if (Messagebox.ON_CANCEL.equals(buttonName)) {
                                return;
                            } else if (Messagebox.ON_OK.equals(buttonName)) {
                                Clients.evalJavaScript("window.location.reload()");
                            }
                        }
                    }
                );
            }
        });
    }

    public void populate() {
        combobox.getItems().clear();
        int selIndex = 0, i = 1;
        Map<String, String> supportedLocales = i18nConfig.getSelectionSet();
        String persistedLangTag = i18nSession.getPersistedLangTag();
        Comboitem comboitem = combobox.appendItem("Auto");
        comboitem.setValue("auto");
        for (Map.Entry<String, String> entry : supportedLocales.entrySet()) {
            String langTag = entry.getKey();
            String label = entry.getValue();
            comboitem = combobox.appendItem(label);
            comboitem.setValue(langTag);
            if (langTag.equals(persistedLangTag)) {
                selIndex = i;
            }
            i++;
        }
        combobox.setSelectedIndex(selIndex);
    }
}
