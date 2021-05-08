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

package org.apromore.plugin.portal.processdiscoverer.components;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apromore.logman.attribute.graph.MeasureAggregation;
import org.apromore.logman.attribute.graph.MeasureRelation;
import org.apromore.logman.attribute.graph.MeasureType;
import org.apromore.logman.attribute.log.AttributeLog;
import org.apromore.logman.attribute.log.AttributeTrace;
import org.apromore.plugin.portal.processdiscoverer.InteractiveMode;
import org.apromore.plugin.portal.processdiscoverer.PDController;
import org.apromore.plugin.portal.processdiscoverer.data.CaseDetails;
import org.apromore.processdiscoverer.Abstraction;
import org.apromore.processdiscoverer.AbstractionParams;
import org.apromore.processdiscoverer.bpmn.TraceBPMNDiagram;
import org.apromore.processmining.models.graphbased.directed.bpmn.BPMNNode;
import org.json.JSONObject;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zk.ui.util.Clients;
import org.zkoss.zul.Button;
import org.zkoss.zul.ListModelList;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Listcell;
import org.zkoss.zul.Messagebox;
import org.zkoss.zul.Window;

public class CaseDetailsController extends DataListController {
	private Window caseDetailsWindow;
	private boolean disabled = false;
	private Map<String,Map<String,String>> activityToAttributeMap = new HashMap<>();

	public CaseDetailsController(PDController controller) {
		super(controller);
	}

	private void generateData() {
		List<CaseDetails> caseDetails = parent.getLogData().getCaseDetails();
		records = new ListModelList();
		rows = new ArrayList<String[]>();
		for (CaseDetails c : caseDetails) {
			records.add(c);
			rows.add(new String[] { c.getCaseId(), Integer.toString(c.getCaseEvents()),
					Integer.toString(c.getCaseVariantId()), c.getCaseVariantFreqStr() });
		}
	}

	private void populateCasesBasedOnActivities(Listbox listbox) {
		generateData();
		listbox.setModel(records);
	}

	@Override
	public String[] getDataHeaders() {
		return new String[] { "Case ID", "Events", "Case Variant", "Percentage (%)" };
	}

	@Override
	public String getExportFilename() {
		return parent.getContextData().getLogName() + ".csv";
	}

	/**
	 * Update Activity <-> Attributes Map for quick lookup
	 **/
	private void updateActivityToAttributeMap(String caseId, TraceBPMNDiagram diagram) {
		activityToAttributeMap.clear();
		AttributeLog attLog = parent.getLogData().getAttributeLog();
		AttributeTrace attTrace = attLog.getTraceFromTraceId(caseId);
		if (attTrace != null) {
		    BPMNNode node = diagram.getStartNode();
		    for (int index=0; index<attTrace.getValueTrace().size(); index++) {
		        activityToAttributeMap.put(node.getId().toString(), attTrace.getAttributeMapAtIndex(index));
		        if (!diagram.getOutEdges(node).isEmpty()) node = diagram.getOutEdges(node).iterator().next().getTarget();
		    }
		    updateActivityToAttributeMapClient();
		}
	}

	/**
	 * Serialize Activity <-> Attributes Map and sent it to client-side JS
	 * for fast tooltip
 	 */
	private void updateActivityToAttributeMapClient() {
		JSONObject json = new JSONObject();
		for (String nodeId : activityToAttributeMap.keySet()) {
			json.put(nodeId, new JSONObject(activityToAttributeMap.get(nodeId)));
		}
		Clients.evalJavaScript("Ap.pd.updateActivityToAttributeMap(" + json.toString() + ")");
	}

	@Override
	public void onEvent(Event event) throws Exception {
		if (caseDetailsWindow == null) {
			caseDetailsWindow = (Window) Executions.createComponents("caseDetails.zul", null, null);
			caseDetailsWindow.setTitle("Cases");
			caseDetailsWindow.getFellow("lblClickACase").setVisible(!this.disabled);

			caseDetailsWindow.addEventListener("onClose", new EventListener<Event>() {
				@Override
				public void onEvent(Event event) throws Exception {
					caseDetailsWindow = null;
					if (parent.getInteractiveMode() == InteractiveMode.TRACE_MODE) {
					    parent.restoreModelView();
						Clients.evalJavaScript("Ap.pd.updateActivityToAttributeMap()"); // reset
					}
				}
			});

			Listbox listbox = (Listbox) caseDetailsWindow.getFellow("caseDetailsList");
			populateCasesBasedOnActivities(listbox);

			listbox.addEventListener("onSelect", new EventListener<Event>() {
				@Override
				public void onEvent(Event event) throws Exception {
				    if (disabled) return;
					try {
						String traceID = ((Listcell) (listbox.getSelectedItem()).getChildren().get(0)).getLabel();
						AbstractionParams params = parent.genAbstractionParamsSimple(false, true, false,
								MeasureType.DURATION, MeasureAggregation.CASES, MeasureRelation.ABSOLUTE,
								MeasureType.FREQUENCY, MeasureAggregation.CASES, MeasureRelation.ABSOLUTE);
						Abstraction traceAbs = parent.getProcessDiscoverer().generateTraceAbstraction(traceID, params);
						String visualizedText = parent.getProcessVisualizer().generateVisualizationText(traceAbs);
						updateActivityToAttributeMap(traceID, (TraceBPMNDiagram)traceAbs.getDiagram());
						parent.showTrace(visualizedText);
					} catch (Exception e) {
						Messagebox.show(e.getMessage());
					}
				}
			});

			Button save = (Button) caseDetailsWindow.getFellow("downloadCSV");
			save.addEventListener("onClick", new EventListener<Event>() {
				@Override
				public void onEvent(Event event) throws Exception {
					exportData();
				}
			});
			try {
				JSONObject param = (JSONObject) event.getData();
				caseDetailsWindow.setPosition("nocenter");
				caseDetailsWindow.setLeft((String) param.get("left"));
				caseDetailsWindow.setTop((String) param.get("top"));
			} catch (Exception e) {
				// ignore the exception and proceed with default centered window
			}
			caseDetailsWindow.doOverlapped();
		}
	}

	public Window getWindow() {
		return caseDetailsWindow;
	}
	
    @Override
    public void setDisabled(boolean disabled) {
        this.disabled = disabled;
    }

}
