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

package org.apromore.plugin.portal.processdiscoverer.components;

import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import lombok.Setter;
import org.apromore.apmlog.ATrace;
import org.apromore.logman.attribute.log.AttributeLog;
import org.apromore.logman.attribute.log.AttributeTrace;
import org.apromore.plugin.portal.processdiscoverer.InteractiveMode;
import org.apromore.plugin.portal.processdiscoverer.PDController;
import org.apromore.plugin.portal.processdiscoverer.data.CaseVariantDetails;
import org.apromore.plugin.portal.processdiscoverer.data.OutputData;
import org.apromore.plugin.portal.processdiscoverer.utils.AttributesStandardizer;
import org.apromore.processdiscoverer.bpmn.TraceVariantBPMNDiagram;
import org.apromore.processmining.models.graphbased.directed.bpmn.BPMNNode;
import org.json.JSONArray;
import org.json.JSONObject;
import org.zkoss.zk.ui.Execution;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zk.ui.metainfo.PageDefinition;
import org.zkoss.zk.ui.util.Clients;
import org.zkoss.zul.Button;
import org.zkoss.zul.ListModelList;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Listcell;
import org.zkoss.zul.Messagebox;
import org.zkoss.zul.Window;

public class CaseVariantDetailsController extends DataListController {
    private Window caseVariantDetailsWindow;
    @Setter
    private boolean disabledInspector = false;
    private boolean disabled = false;
    private Map<String, Map<String, String>> activityToAttributeMap = new HashMap<>();
    private AttributesStandardizer attStandardizer = new AttributesStandardizer();

    public CaseVariantDetailsController(PDController controller) {
        super(controller);
        attStandardizer = AttributesStandardizer.SIMPLE;
    }

    private void generateData() {
        List<CaseVariantDetails> caseVariantDetails = parent.getProcessAnalyst().getCaseVariantDetails();
        records = new ListModelList();
        rows = new ArrayList<>();
        for (CaseVariantDetails c : caseVariantDetails) {
            records.add(c);
            rows.add(new String[] {Integer.toString(c.getCaseVariantId()), Long.toString(c.getActivityInstances()),
                    c.getAvgDurationStr(), Long.toString(c.getNumCases()), c.getFreqStr()});
        }
    }

    private void populateCaseVariantTable(Listbox listbox) {
        generateData();
        listbox.setModel(records);
    }

    @Override
    public String[] getDataHeaders() {
        return new String[] {"Case variant ID", "Activity instances", "Average duration", "Cases", "Percentage (%)"};
    }

    @Override
    public String getExportFilename() {
        return parent.getContextData().getLogName() + ".csv";
    }

    /**
     * Update Activity <-> Attributes Map for quick lookup
     **/
    private void updateActivityToAttributeMap(int caseVariantId, TraceVariantBPMNDiagram diagram) {
        activityToAttributeMap.clear();
        AttributeLog attLog = parent.getProcessAnalyst().getAttributeLog();
        List<String> caseIds = parent.getProcessAnalyst().getCaseVariantGroupMap().get(caseVariantId)
                .stream().map(ATrace::getCaseId).collect(Collectors.toList());

        //Use first case to get number of activities (they should all have the same number of activities).
        AttributeTrace firstAttTrace = attLog.getTraceFromTraceId(caseIds.get(0));

        if (firstAttTrace != null) {
            BPMNNode node = diagram.getStartNode();
            for (int index = 0; index < firstAttTrace.getValueTrace().size(); index++) {

                //Get map with activity attribute averages of cases in the case variant.
                Map<String, String> nonStandardisedMap = parent.getProcessAnalyst()
                        .getActivityAttributeAverageMap(caseVariantId, index);
                activityToAttributeMap.put(node.getId().toString(),
                        attStandardizer.standardizedAttributeMap(nonStandardisedMap));
                if (!diagram.getOutEdges(node).isEmpty())
                    node = diagram.getOutEdges(node).iterator().next().getTarget();
            }
            updateActivityToAttributeMapClient();
        }
    }

    /**
     * Serialize Activity <-> Attributes Map and sent it to client-side JS for fast tooltip
     */
    private void updateActivityToAttributeMapClient() {
        JSONObject json = new JSONObject();
        for (Map.Entry<String, Map<String, String>> entry : activityToAttributeMap.entrySet()) {
            json.put(entry.getKey(), makeJSONArray(entry.getValue()));
        }
        Clients.evalJavaScript("Ap.pd.updateActivityToAttributeMap(" + json.toString() + ")");
    }

    private JSONArray makeJSONArray(Map<String, String> attributeMap) {
        JSONArray array = new JSONArray();
        for (Map.Entry<String, String> entry : attributeMap.entrySet()) {
            array.put((new JSONObject()).put("name", entry.getKey()).put("value", entry.getValue()));
        }
        return array;
    }

    @Override
    public void onEvent(Event event) throws Exception {
        if (caseVariantDetailsWindow == null && !disabledInspector) {
            Map<String, Object> arg = new HashMap<>();
            arg.put("pdLabels", parent.getLabels());
            parent.disableGraphEditButtons();
            caseVariantDetailsWindow = (Window) Executions
                    .createComponents(getPageDefinition("processdiscoverer/zul/caseVariantDetails.zul"), null, arg);
            caseVariantDetailsWindow.setTitle(getLabel("caseVariantInspector_text", "Case variant Inspector"));
            caseVariantDetailsWindow.getFellow("lblClickACase").setVisible(!this.disabled);
            caseVariantDetailsWindow.addEventListener("onClose", new EventListener<Event>() {
                @Override
                public void onEvent(Event event) throws Exception {
                    caseVariantDetailsWindow = null;
                    if (parent.getInteractiveMode() == InteractiveMode.TRACE_MODE) {
                        parent.restoreModelView();
                        Clients.evalJavaScript("Ap.pd.updateActivityToAttributeMap()"); // reset
                    }
                }
            });

            Listbox listbox = (Listbox) caseVariantDetailsWindow.getFellow("caseVariantDetailsList");
            populateCaseVariantTable(listbox);

            listbox.addEventListener("onSelect", new EventListener<Event>() {
                @Override
                public void onEvent(Event event) throws Exception {
                    if (disabled || disabledInspector)
                        return;
                    try {
                        String caseVariantIDLabel = ((Listcell) (listbox.getSelectedItem()).getChildren().get(0)).getLabel();
                        int caseVariantID = Integer.parseInt(caseVariantIDLabel);
                        OutputData result = parent.getProcessAnalyst().discoverTraceVariant(caseVariantID, parent.getUserOptions());
                        //Update map used for node tooltips
                        updateActivityToAttributeMap(caseVariantID, (TraceVariantBPMNDiagram) result.getAbstraction().getDiagram());
                        //Disables buttons in PD and updates canvas
                        parent.showTrace(result.getVisualizedText());
                    } catch (Exception e) {
                        Messagebox.show("Fail to show trace variant details for the selected case variant");
                    }
                }
            });

            Button save = (Button) caseVariantDetailsWindow.getFellow("downloadCSV");
            save.addEventListener("onClick", new EventListener<Event>() {
                @Override
                public void onEvent(Event event) throws Exception {
                    exportData();
                }
            });
            try {
                // @todo Incorrect coordinate returned by ZK 9
                // org.zkoss.json.JSONObject param = (org.zkoss.json.JSONObject) event.getData();
                // caseVariantDetailsWindow.setLeft((String) param.get("left"));
                // caseVariantDetailsWindow.setTop((String) param.get("top"));
                caseVariantDetailsWindow.setPosition("nocenter");
                caseVariantDetailsWindow.setLeft("10px");
                caseVariantDetailsWindow.setTop("76px");
            } catch (Exception e) {
                // ignore the exception and proceed with default centered window
            }
            caseVariantDetailsWindow.doOverlapped();
        }
    }

    public Window getWindow() {
        return caseVariantDetailsWindow;
    }

    @Override
    public void setDisabled(boolean disabled) {
        this.disabled = disabled;
    }

    private PageDefinition getPageDefinition(String uri) throws IOException {
        String url = "static/" + uri;
        Execution current = Executions.getCurrent();
        return current.getPageDefinitionDirectly(
                new InputStreamReader(getClass().getClassLoader().getResourceAsStream(url)), "zul");
    }

}
