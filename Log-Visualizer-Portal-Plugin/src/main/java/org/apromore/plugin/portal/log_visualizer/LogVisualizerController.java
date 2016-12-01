/*
 * Copyright © 2009-2016 The Apromore Initiative.
 *
 * This file is part of "Apromore".
 *
 * "Apromore" is free software; you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 3 of the
 * License, or (at your option) any later version.
 *
 * "Apromore" is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this program.
 * If not, see <http://www.gnu.org/licenses/lgpl-3.0.html>.
 */

package org.apromore.plugin.portal.log_visualizer;

// Java 2 Standard Edition
import java.io.FileInputStream;
import java.util.UUID;

// Third party packages
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.util.Clients;
import org.zkoss.zk.ui.util.GenericForwardComposer;
import org.zkoss.zul.Messagebox;
import org.zkoss.zul.Slider;

// Local packages
import org.apromore.util.StreamUtil;

public class LogVisualizerController extends GenericForwardComposer {

    private static final Logger LOGGER = LoggerFactory.getLogger(LogVisualizerController.class.getCanonicalName());

    public void onClick$cancelButton(Event event) throws InterruptedException {
        event.getTarget().detach();
    }

    public void onClick$okButton(Event event) throws InterruptedException {
        Integer logId = (Integer) event.getTarget().getAttribute("logId");
        Slider slider1 = (Slider) event.getTarget().getFellow("slider1");
        Slider slider2 = (Slider) event.getTarget().getFellow("slider2");
        LOGGER.info("Invoking Log visualizer: logId=" + logId + " slider1=" + slider1.getCurpos() + " slider2=" + slider2.getCurpos());
        event.getTarget().detach();

        try {
            String id = UUID.randomUUID().toString();

            // Dummy implementation: should instead assign data1 by calling Log-Visualizer-Logic with the parameters logId, slider1, slider2
            String data1 = StreamUtil.convertStreamToString(new FileInputStream("/Users/raboczi/Project/ApromoreCode/SOAP/d.bpmn"));

            Executions.getCurrent().getSession().setAttribute("SIGNAVIO_SESSION" + id, data1);

            Clients.evalJavaScript("window.open('macros/openLogVisualizerInSignavio.zul?id=" + id + "');");

        } catch (Exception e) {
            Messagebox.show("Cannot visualize log with ID " + logId + " (" + e.getMessage() + ")", "Attention", Messagebox.OK, Messagebox.ERROR);
        }
    }
}
