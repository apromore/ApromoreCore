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

import static org.apromore.logman.attribute.graph.MeasureType.DURATION;
import static org.apromore.logman.attribute.graph.MeasureType.FREQUENCY;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apromore.logman.attribute.AbstractAttribute;
import org.apromore.logman.attribute.graph.MeasureAggregation;
import org.apromore.logman.attribute.graph.MeasureRelation;
import org.apromore.logman.attribute.graph.MeasureType;
import org.apromore.plugin.portal.PortalLoggerFactory;
import org.apromore.plugin.portal.processdiscoverer.PDAnalyst;
import org.apromore.plugin.portal.processdiscoverer.PDController;
import org.apromore.plugin.portal.processdiscoverer.data.UserOptionsData;
import org.slf4j.Logger;
import org.zkoss.util.resource.Labels;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zul.Checkbox;
import org.zkoss.zul.Combobox;
import org.zkoss.zul.Comboitem;
import org.zkoss.zul.Div;
import org.zkoss.zul.Label;
import org.zkoss.zul.Span;

/**
 * @author Ivo Widjaja
 * Modified: Ivo Widjaja
 */
public class ViewSettingsController extends VisualController {
    private static final Logger LOGGER = PortalLoggerFactory.getLogger(ViewSettingsController.class);

    private final List<String> BLACKLISTED_PERSPECTIVES = Arrays.asList(
            "lifecycle:transition"
    );

    private final List<String> CANONICAL_PERSPECTIVES = Arrays.asList(
            "concept:name",
            "lifecycle:transition",
            "org:group",
            "org:resource",
            "org:role"
    );
    
    private String[] measures = {
            "case_count",
            "case_relative",
            "total",
            "mean",
            "median",
            "max",
            "min"
    };

    private Map<String, MeasureAggregation> measureAggMap = new HashMap<String, MeasureAggregation>() {
        {
            put(measures[0], MeasureAggregation.CASES);
            put(measures[1], MeasureAggregation.CASES);
            put(measures[2], MeasureAggregation.TOTAL);
            put(measures[3], MeasureAggregation.MEAN);
            put(measures[4], MeasureAggregation.MEDIAN);
            put(measures[5], MeasureAggregation.MAX);
            put(measures[6], MeasureAggregation.MIN);
        }
    };
    
    private Map<String, MeasureRelation> measureRelationMap = new HashMap<String, MeasureRelation>() {
        {
            put(measures[0], MeasureRelation.ABSOLUTE);
            put(measures[1], MeasureRelation.RELATIVE);
            put(measures[2], MeasureRelation.ABSOLUTE);
            put(measures[3], MeasureRelation.ABSOLUTE);
            put(measures[4], MeasureRelation.ABSOLUTE);
            put(measures[5], MeasureRelation.ABSOLUTE);
            put(measures[6], MeasureRelation.ABSOLUTE);
        }
    };

    private Checkbox bpmnMode;
    private Checkbox includeSecondary;
    private Combobox perspectiveSelector;


    private Combobox frequencyAggSelector;
    private Combobox durationAggSelector;

    private Div defaultPerspective;
    private Div defaultFrequency;
    private Div defaultDuration;

    private Label freqRank;
    private Label durationRank;
    private Span freqShow;
    private Span durationShow;
    private Div freqOption;
    private Div durationOption;

    private UserOptionsData userOptions;
    
    private String primaryTypeLabel;
    private String primaryAggregateCode;
    private final String FREQ_LABEL = "frequency";
    private final String DURATION_LABEL = "duration";
    
    private boolean disabled = false;

    public ViewSettingsController(PDController parent) {
        super(parent);
    }

    @Override
    public void initializeControls(Object data) {
        if (this.parent == null) return;

        LOGGER.debug("ViewSettingsController");
        userOptions = parent.getUserOptions();

        Component compViewSettings = parent.query(".ap-pd-view-settings");
        // View settings
        bpmnMode = (Checkbox) compViewSettings.getFellow("gateways");
        bpmnMode.setChecked(userOptions.getBPMNMode());

        perspectiveSelector = (Combobox) compViewSettings.getFellow("perspectiveSelector");

        defaultPerspective = (Div) compViewSettings.getFellow("defaultPerspective");
        defaultFrequency = (Div) compViewSettings.getFellow("defaultFrequency");
        defaultDuration = (Div) compViewSettings.getFellow("defaultDuration");

        includeSecondary = (Checkbox) compViewSettings.getFellow("includeSecondary");
        includeSecondary.setChecked(userOptions.getIncludeSecondary());

        freqRank = (Label) compViewSettings.getFellow("freqRank");
        freqRank.setVisible(false);
        durationRank = (Label) compViewSettings.getFellow("durationRank");
        durationRank.setVisible(false);

        freqShow = (Span) compViewSettings.getFellow("freqShow");
        freqShow.setVisible(false);
        durationShow = (Span) compViewSettings.getFellow("durationShow");
        durationShow.setVisible(true);

        freqOption = (Div) compViewSettings.getFellow("freqOption");
        durationOption = (Div) compViewSettings.getFellow("durationOption");

        frequencyAggSelector = (Combobox) compViewSettings.getFellow("frequencyAggSelector");
        durationAggSelector = (Combobox) compViewSettings.getFellow("durationAggSelector");
        
        primaryTypeLabel = FREQ_LABEL;
        primaryAggregateCode = measures[0];
    }
    
    @Override
    public void initializeEventListeners(Object data) {
        perspectiveSelector.addEventListener("onSelect", new EventListener<Event>() {
            @Override
            public void onEvent(Event event) throws Exception {
                String value = perspectiveSelector.getSelectedItem().getValue();
                if (value.equals("-")) return;
                String label = perspectiveSelector.getSelectedItem().getLabel();
                parent.setPerspective(value, label);
            }
        });

        EventListener<Event> radioListener = new EventListener<Event>() {
            @Override
            public void onEvent(Event event) throws Exception {
                parent.setBPMNView(bpmnMode.isChecked());
            }
        };
        this.bpmnMode.addEventListener("onCheck", radioListener);

        defaultPerspective.addEventListener("onClick", new EventListener<Event>() {
            @Override
            public void onEvent(Event event) throws Exception {
                if (disabled) return;
                String value = "concept:name";
                selectComboboxByKey(perspectiveSelector, value);
                String label = perspectiveSelector.getSelectedItem().getLabel();
                parent.setPerspective(value, label);
            }
        });

        defaultFrequency.addEventListener("onClick", new EventListener<Event>() {
            @Override
            public void onEvent(Event event) throws Exception {
                if (disabled) return;
                String key = "case";
                selectComboboxByKey(frequencyAggSelector, key);
                userOptions.setRetainZoomPan(true);
                selectFrequencyViz();
            }
        });

        defaultDuration.addEventListener("onClick", new EventListener<Event>() {
            @Override
            public void onEvent(Event event) throws Exception {
                if (disabled) return;
                String key = "mean";
                selectComboboxByKey(durationAggSelector, key);
                userOptions.setRetainZoomPan(true);
                selectDurationViz();
            }
        });

        freqShow.addEventListener("onClick", new EventListener<Event>() {
            @Override
            public void onEvent(Event event) throws Exception {
                if (disabled) return;
                boolean isShown = !includeSecondary.isChecked();
                userOptions.setIncludeSecondary(isShown);
                includeSecondary.setChecked(isShown);
                toggleComponentSclass(freqShow, isShown, "ap-icon-eye-close", "ap-icon-eye-open");
                freqShow.setTooltiptext(isShown ?
                    parent.getLabel("metricHideSecondary_text") :
                    parent.getLabel("metricShowSecondary_text")
                );
                userOptions.setRetainZoomPan(true);
                parent.generateViz();
            }
        });

        durationShow.addEventListener("onClick", new EventListener<Event>() {
            @Override
            public void onEvent(Event event) throws Exception {
                if (disabled) return;
                boolean isShown = !includeSecondary.isChecked();
                userOptions.setIncludeSecondary(isShown);
                includeSecondary.setChecked(isShown);
                toggleComponentSclass(durationShow, isShown, "ap-icon-eye-close", "ap-icon-eye-open");
                durationShow.setTooltiptext(isShown ?
                    parent.getLabel("metricHideSecondary_text") :
                    parent.getLabel("metricShowSecondary_text")
                );
                userOptions.setRetainZoomPan(true);
                parent.generateViz();
            }
        });


        EventListener<Event> frequencyAggSelectorListener = new EventListener<Event>() {
            @Override
            public void onEvent(Event event) throws Exception {
                toggleComponentClass(defaultFrequency, true);
                toggleComponentClass(defaultDuration, false);
                userOptions.setRetainZoomPan(true);
                selectFrequencyViz();
            }
        };
        this.frequencyAggSelector.addEventListener("onSelect", frequencyAggSelectorListener);
        this.frequencyAggSelector.addEventListener("onForceSelect", frequencyAggSelectorListener);

        EventListener<Event> durationAggSelectorListener = new EventListener<Event>() {
            @Override
            public void onEvent(Event event) throws Exception {
                toggleComponentClass(defaultFrequency, false);
                toggleComponentClass(defaultDuration, true);
                userOptions.setRetainZoomPan(true);
                selectDurationViz();
            }
        };
        this.durationAggSelector.addEventListener("onSelect", durationAggSelectorListener);
        this.durationAggSelector.addEventListener("onForceSelect", durationAggSelectorListener);

    }
    
    @Override
    public void setDisabled(boolean disabled) {
        this.disabled = disabled;
        bpmnMode.setDisabled(disabled);
        perspectiveSelector.setDisabled(disabled);
        frequencyAggSelector.setDisabled(disabled);
        durationAggSelector.setDisabled(disabled);
        includeSecondary.setDisabled(disabled);
    }

    @Override
    public void updateUI(Object data) {
        perspectiveSelector.getItems().clear();
        int selIndex = 0, i = 0;
        for (Map.Entry<String, String> entry : getPerspectiveMap().entrySet()) {
            String option = entry.getKey();
            String label = entry.getValue();
            Comboitem comboitem = perspectiveSelector.appendItem(label);
            comboitem.setValue(option);
            if (option.equals("-")) {
                comboitem.setDisabled(true);
                comboitem.setSclass("ap-combobox-separator");
            }
            if (option.equals(userOptions.getMainAttributeKey())) {
                selIndex = i;
                // perspectiveSelector.setValue(option);
            }
            i++;
        }
        perspectiveSelector.setSelectedIndex(selIndex);
    }

    private LinkedHashMap<String, String> getPerspectiveMap() {

        Comparator<Map.Entry<String, String>> comparator = new Comparator<Map.Entry<String, String>>() {
            @Override
            public int compare(Map.Entry<String, String> o1, Map.Entry<String, String> o2) {
                return o1.getValue().compareTo(o2.getValue());
            }
        };

        PDAnalyst analyst = parent.getProcessAnalyst();
        Set<String> set = new HashSet<>();
        for (AbstractAttribute att : analyst.getAvailableAttributes()) {
            String key = att.getKey();
            if (!BLACKLISTED_PERSPECTIVES.contains(key)) {
                set.add(key);
            }
        }
        List<String> list = new ArrayList<>(set);
        HashMap<String, String> map = new HashMap<>();
        HashMap<String, String> canonMap = new HashMap<>();

        for (String key : list) {
            if (!CANONICAL_PERSPECTIVES.contains(key)) {
                map.put(key, key);
            } else {
                // String value = Labels.getLabel("e.pd.perspective." + key.replace(':', '.') + ".hint", key);
                String value = parent.getLabel("perspective_" + key.replace(':', '_') + "_hint", key);
                canonMap.put(key, value);
            }
        }
        List<Map.Entry<String, String>> entries = new ArrayList<>(map.entrySet());
        Collections.sort(entries, comparator);
        List<Map.Entry<String, String>> canonEntries = new ArrayList<>(canonMap.entrySet());
        Collections.sort(canonEntries, comparator);
        LinkedHashMap<String, String> linkMap = new LinkedHashMap<>();

        for (Map.Entry<String, String> entry : canonEntries) {
            linkMap.put(entry.getKey(), entry.getValue());
        }
        if (canonEntries.size() > 0) {
            linkMap.put("-", "----------");
        }
        for (Map.Entry<String, String> entry : entries) {
            linkMap.put(entry.getKey(), entry.getValue());
        }
        return linkMap;
    }

    private void selectFrequencyViz() throws InterruptedException {
        String freqAgg = frequencyAggSelector.getSelectedItem().getValue();
        String durationAgg = durationAggSelector.getSelectedItem().getValue();

        selectComboboxByKey(frequencyAggSelector, freqAgg);
        freqRank.setValue("1");
        durationRank.setValue("2");
        freqShow.setVisible(false);
        durationShow.setVisible(true);
        toggleComponentClass(freqOption, true);
        toggleComponentClass(durationOption, false);
        toggleComponentSclass(durationShow, includeSecondary.isChecked(), "ap-icon-eye-close", "ap-icon-eye-open");
        durationShow.setTooltiptext(includeSecondary.isChecked() ?
            parent.getLabel("metricHideSecondary_text") :
            parent.getLabel("metricShowSecondary_text")
        );

        setOverlay(
            FREQUENCY,
            measureAggMap.get(freqAgg),
            measureRelationMap.get(freqAgg),
            DURATION,
            measureAggMap.get(durationAgg),
            measureRelationMap.get(durationAgg),
            freqAgg
        );
    }

    private void selectDurationViz() throws InterruptedException {
        String durationAgg = durationAggSelector.getSelectedItem().getValue();
        String freqAgg = frequencyAggSelector.getSelectedItem().getValue();

        selectComboboxByKey(durationAggSelector, durationAgg);
        freqRank.setValue("2");
        durationRank.setValue("1");
        freqShow.setVisible(true);
        durationShow.setVisible(false);
        toggleComponentClass(freqOption, false);
        toggleComponentClass(durationOption, true);
        toggleComponentSclass(freqShow, includeSecondary.isChecked(), "ap-icon-eye-close", "ap-icon-eye-open");
        freqShow.setTooltiptext(includeSecondary.isChecked() ?
            parent.getLabel("metricHideSecondary_text") :
            parent.getLabel("metricShowSecondary_text")
        );

        setOverlay(
            DURATION,
            measureAggMap.get(durationAgg),
            measureRelationMap.get(durationAgg),
            FREQUENCY,
            measureAggMap.get(freqAgg),
            measureRelationMap.get(freqAgg),
                durationAgg
        );
    }

    @Override
    public void onEvent(Event event) throws Exception {
        throw new Exception("Unsupported interactive Event Handler");
    }
    
    private void setOverlay(
            MeasureType primaryType,
            MeasureAggregation primaryAggregation,
            MeasureRelation primaryRelation,
            MeasureType secondaryType,
            MeasureAggregation secondaryAggregation,
            MeasureRelation secondaryRelation,
            String aggregateCode
    ) {

        parent.getUserOptions().setPrimaryType(primaryType);
        parent.getUserOptions().setPrimaryAggregation(primaryAggregation);
        parent.getUserOptions().setPrimaryRelation(primaryRelation);
        
        parent.getUserOptions().setSecondaryType(secondaryType);
        parent.getUserOptions().setSecondaryAggregation(secondaryAggregation);
        parent.getUserOptions().setSecondaryRelation(secondaryRelation);

        primaryAggregateCode = aggregateCode;
        if (primaryType == FREQUENCY) {
            primaryTypeLabel = parent.getLabel("common_frequencyLabel_text", FREQ_LABEL);
        } else { // (overlay == DURATION) assume DURATION
            primaryTypeLabel = parent.getLabel("common_durationLabel_text", DURATION_LABEL);
        }
        parent.generateViz();
    }
    
    public String getOutputName() {
        return parent.getContextData().getLogName() + " - " +
            parent.getLabel("stat_" + primaryAggregateCode + "_text") + " " + primaryTypeLabel;
    }
    
    public String getPerspectiveName() {
        String name = "Untitled-perspective";
        if (perspectiveSelector != null) {
            String value = perspectiveSelector.getSelectedItem().getValue();
            if (!value.isEmpty()) {
                name = parent.getLabel("perspective_" + value.replace(':', '_') + "_text", value);
            }
        }
        return name;
    }
    
}
