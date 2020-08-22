/*-
 * #%L
 * This file is part of "Apromore Core".
 * %%
 * Copyright (C) 2018 - 2020 Apromore Pty Ltd.
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

package org.apromore.plugin.portal.processdiscoverer.controllers;

import java.util.ArrayList;

import org.apromore.logman.attribute.log.AttributeInfo;
import org.apromore.plugin.portal.processdiscoverer.PDController;
import org.apromore.plugin.portal.processdiscoverer.data.PerspectiveDetails;
import org.zkoss.json.JSONObject;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zul.Button;
import org.zkoss.zul.ListModelList;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Window;

public class PerspectiveDetailsController extends DataListController {
    Window perspectiveDetailsWindow;

    public PerspectiveDetailsController(PDController controller) {
        super(controller);
    }

    private void generateData() {
        records = new ListModelList();
        rows = new ArrayList<String []>();

        for (AttributeInfo info : parent.getLogData().getAttributeInfoList()) {
            ArrayList<String> cells = new ArrayList<>();

            String value = info.getAttributeValue();
            long occurrences = info.getAttributeOccurrenceCount();
            double freq = info.getAttributeOccurrenceFrequency();

            PerspectiveDetails perspectiveDetails = new PerspectiveDetails(value, occurrences, freq);
            records.add(perspectiveDetails);
            rows.add(new String []{value, Long.toString(occurrences), perspectiveDetails.getFreqStr()});
        }
    }

    @Override
    public String[] getDataHeaders () {
        return new String[]{"Activity", "Frequency", "Frequency (%)"};
    }

    @Override
    public String getExportFilename () {
        return parent.getLogName() + "-" + parent.getPerspective() + ".csv";
    }

    @Override
    public void onEvent(Event event) throws Exception {
        perspectiveDetailsWindow = (Window) Executions.createComponents("perspectiveDetails.zul", null, null);
        perspectiveDetailsWindow.setTitle(parent.getPerspective());
        Listbox listbox = (Listbox) perspectiveDetailsWindow.getFellow("perspectiveDetailsList");

        generateData();
        listbox.setModel(records);

        Button save = (Button) perspectiveDetailsWindow.getFellow("downloadCSV");
        save.addEventListener("onClick", new EventListener<Event>() {
            @Override
            public void onEvent(Event event) throws Exception {
                exportData();
            }
        });

        try {
            JSONObject param = (JSONObject) event.getData();
            perspectiveDetailsWindow.setPosition("nocenter");
            perspectiveDetailsWindow.setLeft((String)param.get("left"));
            perspectiveDetailsWindow.setTop((String)param.get("top"));
        } catch (Exception e) {
            // ignore the exception and proceed with default centered window
        }
        perspectiveDetailsWindow.doOverlapped();
    }

    public Window getWindow() {
        return perspectiveDetailsWindow;
    }
}
