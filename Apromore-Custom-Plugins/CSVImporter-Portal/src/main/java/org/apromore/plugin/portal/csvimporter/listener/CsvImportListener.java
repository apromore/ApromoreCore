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
package org.apromore.plugin.portal.csvimporter.listener;

import java.io.IOException;
import java.util.Map;

import org.apromore.plugin.portal.PortalContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.zkoss.json.JSONObject;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.Sessions;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zul.Window;

/**
 * @author nolantellis
 *
 */
public class CsvImportListener implements EventListener<Event> {

    private static Logger logger = LoggerFactory.getLogger(CsvImportListener.class);
    private static final String MODAL = "modal";
    private static final String PAGE = "page";
    Map args;
    String target;
    Component componentToDetach;
    JSONObject json;

    /**
     * @param args              A map that is passed to the zul component.
     * @param target            Suplied from external call, as Page or Modal.
     * @param componentToDetach Component to detached.
     * @param json              : json mapping which is value from metadata
     * 
     *                          NOTE : This listener is just used a s refactored
     *                          code to avoid lots of duplicate code from the
     *                          calling class.
     */
    public CsvImportListener(Map args, String target, Component componentToDetach, JSONObject json) {
	super();
	this.args = args;
	args.put("mappingJSON", json);
	this.target = target;
	this.componentToDetach = componentToDetach;
	this.json = json;
    }


    @Override
    public void onEvent(Event event) throws Exception {
	if (componentToDetach != null) {
	    componentToDetach.detach();
	}
	switch (target) {
	case PAGE:
	    Executions.getCurrent().sendRedirect("import-csv/csvimporter.zul", "_blank");
	    break;

	case MODAL:
	default:

	    try {
		Window window = (Window) ((PortalContext) Sessions.getCurrent().getAttribute("portalContext")).getUI()
			.createComponent(getClass().getClassLoader(), "import-csv/csvimporter.zul", null, args);
		window.doModal();

	    } catch (IOException e) {
		logger.error("Unable to create window", e);
	    }

	    break;

	}

    }

}
