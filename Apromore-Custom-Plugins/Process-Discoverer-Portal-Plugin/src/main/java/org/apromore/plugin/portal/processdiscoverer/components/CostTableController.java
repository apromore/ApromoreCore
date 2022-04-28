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

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.apromore.exception.UserNotFoundException;
import org.apromore.plugin.portal.processdiscoverer.PDController;
import org.apromore.plugin.portal.processdiscoverer.data.AttributeCost;
import org.apromore.plugin.portal.processdiscoverer.data.UserOptionsData;
import org.apromore.portal.util.CostTable;
import org.eclipse.collections.api.list.ImmutableList;
import org.zkoss.zk.ui.Execution;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.ForwardEvent;
import org.zkoss.zk.ui.event.InputEvent;
import org.zkoss.zk.ui.metainfo.PageDefinition;
import org.zkoss.zk.ui.util.Clients;
import org.zkoss.zul.Combobox;
import org.zkoss.zul.Doublebox;
import org.zkoss.zul.ListModelList;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.Messagebox;
import org.zkoss.zul.Window;

public class CostTableController extends DataListController {
    private Window costTableWindow;
    private Combobox currencyCombobox;
    private boolean disabled;
    private UserOptionsData userOptions;
    private Boolean viewOnly;

    public CostTableController(PDController controller) {
        super(controller);
        this.userOptions = controller.getUserOptions();
    }

    public void setViewOnly(Boolean viewOnly) {
        this.viewOnly = viewOnly;
    }

    private void generateData() {
        records = new ListModelList<AttributeCost>();
        rows = new ArrayList<>();
        for (Object role : parent.getProcessAnalyst().getRoleValues()) {
            String attrVal = (String) role;
            Double costValue = userOptions.getCostTable().getCost(attrVal);
            AttributeCost ac = AttributeCost.valueOf(attrVal, costValue);
            records.add(ac);
            rows.add(new String[]{ac.getAttributeId(), String.format("%6.2f", ac.getCostValue())});
        }
    }

    private void initializeData(Listbox listbox) {
        generateData();
        listbox.setModel(records);
        listbox.addEventListener("onUpdateCostValue", (ForwardEvent event) -> {
            InputEvent inputEvent = (InputEvent) event.getOrigin();
            Doublebox inputBox = (Doublebox) inputEvent.getTarget();
            Double costValue = inputBox.getValue();
            Listitem listItem = (Listitem) inputBox.getParent().getParent();
            AttributeCost ac = listItem.getValue();
            ac.setCostValue(costValue);
        });
        currencyCombobox.setValue(userOptions.getCostTable().getCurrency());
    }

    @Override
    public String[] getDataHeaders() {
        return new String[]{"Attribute", "Cost Value"};
    }

    private void persistCostTable() throws JsonProcessingException, UserNotFoundException {
        String currency = currencyCombobox.getValue();
        //TODO: Get perspective value from user input
        String perspective = "role";
        CostTable costTable = CostTable.builder()
                .perspective(perspective)
                .currency(currency)
                .costRates(this.getCostMapper())
                .build();
        List<CostTable> costTables = Stream.of(costTable)
                .collect(Collectors.toList());

        ObjectMapper objectMapper = new ObjectMapper();
        String arrayToJson = objectMapper.writeValueAsString(costTables);

        parent.getEventLogService().saveCostTablesByLog(arrayToJson, parent.getContextData().getLogId(),
                parent.getContextData().getUsername());
    }

    @Override
    public void onEvent(Event event) throws Exception {
        if (costTableWindow == null) {
            try {
                ImmutableList<Object> roleValues = parent.getProcessAnalyst().getRoleValues();
                if (roleValues.isEmpty()) {
                    Messagebox.show(parent.getLabel("costTableEmpty_message"));
                    return;
                }
            } catch (Exception e) {
                Messagebox.show(parent.getLabel("costTableEmpty_message"));
                return;
            }

            Map<String, Object> arg = new HashMap<>();
            arg.put("pdLabels", parent.getLabels());
            arg.put("viewOnly", viewOnly);
            costTableWindow = (Window) Executions
                    .createComponents(getPageDefinition("processdiscoverer/zul/costTable.zul"), null, arg);

            costTableWindow.addEventListener("onClose", e -> costTableWindow = null);

            costTableWindow.addEventListener("onChangeCurrency", e -> {
                userOptions.setCostTable(CostTable.builder()
                    .currency(currencyCombobox.getValue())
                    .costRates(this.getCostMapper()).build());
                persistCostTable();
            });

            costTableWindow.addEventListener("onApplyCost", e -> {
                userOptions.setCostTable(CostTable.builder()
                    .currency(currencyCombobox.getValue())
                    .costRates(this.getCostMapper()).build());
                persistCostTable();
                costTableWindow.detach();
                costTableWindow = null;
                Messagebox.show(
                    parent.getLabel("costTableUpdated_message"),
                    new Messagebox.Button[] {Messagebox.Button.OK, Messagebox.Button.CANCEL},
                    (ev) -> {
                        if (Messagebox.ON_OK.equals(ev.getName())) {
                            Clients.evalJavaScript("window.location.reload()");
                        }
                    }
                );
            });

            currencyCombobox = (Combobox) costTableWindow.getFellow("costTableCurrency");
            Listbox listbox = (Listbox) costTableWindow.getFellow("costTableListbox");
            initializeData(listbox);

            try {
                // @todo Incorrect coordinate returned by ZK 9
                // org.zkoss.json.JSONObject param = (org.zkoss.json.JSONObject) event.getData();
                costTableWindow.setPosition("center");
            } catch (Exception e) {
                // ignore the exception and proceed with default centered window
            }
            costTableWindow.doOverlapped();
        }
    }

    public Map<String, Double> getCostMapper() {
        Map<String, Double> attributeCostMapper = new HashMap<>();
        for (Object rec: records) {
            AttributeCost ac = (AttributeCost) rec;
            attributeCostMapper.put(ac.getAttributeId(), ac.getCostValue());
        }
        return attributeCostMapper;
    }

    public Window getWindow() {
        return costTableWindow;
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
