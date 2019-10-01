/*
 * Copyright © 2009-2018 The Apromore Initiative.
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

package org.apromore.plugin.portal.pql;

// Java 2 Standard Edition packages
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

// Java 2 Enterprise Edition packages
import javax.inject.Inject;

// Third party packages
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zul.Button;
import org.zkoss.zul.Messagebox;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Window;

// Local packages
import org.apromore.portal.custom.gui.plugin.PluginCustomGui;
import org.apromore.portal.custom.gui.tab.impl.TabRowValue;
import org.apromore.plugin.portal.PortalContext;
import org.apromore.service.pql.PQLService;

/**
 * A user interface to the PQL query service.
 */
@Component("plugin")
public class PQLPlugin extends PluginCustomGui {

    private static final Logger LOGGER = LoggerFactory.getLogger(PQLPlugin.class);

    private final PQLService pqlService;

    @Inject
    public PQLPlugin(final PQLService pqlService) {

        this.pqlService = pqlService;
    }

    @Override
    public String getLabel(Locale locale) {
        return "Query with PQL (simple UI)";
    }

    @Override
    public String getGroupLabel(Locale locale) {
        return "Analyze";
    }

    @Override
    public void execute(final PortalContext portalContext) {

        LOGGER.debug("Executed PQL query plug-in!");

        try {
            final Window window = (Window) portalContext.getUI().createComponent(getClass().getClassLoader(), "zul/pqlQuery.zul", null, null);
            final Textbox query = (Textbox) window.getFellow("query");

            Button cancelButton = (Button) window.getFellow("cancelButton");
            cancelButton.addEventListener("onClick", new EventListener<Event>() {
                public void onEvent(Event event) throws Exception {
                    window.detach();
                }
            });

            Button okButton = (Button) window.getFellow("okButton");
            okButton.addEventListener("onClick", new EventListener<Event>() {
                public void onEvent(Event event) throws Exception {
                    String queryPQL = query.getText();
                    try {
                        displayProcessSummaries("PQL result", pqlService.query(queryPQL), portalContext);

                    } catch (PQLService.QueryParsingException e) {
                        LOGGER.error("Unable to parse " + queryPQL, e);
                        Messagebox.show(e.getParseErrorMessages().get(0), "Attention", Messagebox.OK, Messagebox.ERROR);

                    } catch (Exception e) {
                        LOGGER.error("Unable to execute " + queryPQL, e);
                        Messagebox.show("Unable to execute " + queryPQL + " (" + e.getMessage() + ")", "Attention", Messagebox.OK, Messagebox.ERROR);
                    }
                }
            });

            window.doModal();

        } catch (IOException e) {
            LOGGER.error("PQL query portal plugin failed", e);
            Messagebox.show("Portal plugin failed (" + e.getMessage() + ")", "Attention", Messagebox.OK, Messagebox.ERROR);
        }
    }
}
