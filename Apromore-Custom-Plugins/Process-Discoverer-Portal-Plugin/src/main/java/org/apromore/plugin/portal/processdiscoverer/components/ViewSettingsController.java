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

import static org.apromore.logman.attribute.graph.MeasureType.DURATION;
import static org.apromore.logman.attribute.graph.MeasureType.FREQUENCY;
import static org.apromore.logman.attribute.graph.MeasureType.COST;

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
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zul.Checkbox;
import org.zkoss.zul.Combobox;
import org.zkoss.zul.Comboitem;
import org.zkoss.zul.Div;
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

    private final String[] measures = {
            "case_count",
            "case_relative",
            "total",
            "mean",
            "median",
            "max",
            "min"
    };

    private final Map<String, MeasureAggregation> measureAggMap = Map.of(
        measures[0], MeasureAggregation.CASES,
        measures[1], MeasureAggregation.CASES,
        measures[2], MeasureAggregation.TOTAL,
        measures[3], MeasureAggregation.MEAN,
        measures[4], MeasureAggregation.MEDIAN,
        measures[5], MeasureAggregation.MAX,
        measures[6], MeasureAggregation.MIN
    );

    private final Map<String, MeasureRelation> measureRelationMap = Map.of(
        measures[0], MeasureRelation.ABSOLUTE,
        measures[1], MeasureRelation.RELATIVE,
        measures[2], MeasureRelation.ABSOLUTE,
        measures[3], MeasureRelation.ABSOLUTE,
        measures[4], MeasureRelation.ABSOLUTE,
        measures[5], MeasureRelation.ABSOLUTE,
        measures[6], MeasureRelation.ABSOLUTE
    );

    private Checkbox bpmnMode;
    private Checkbox includeSecondary;
    private Combobox perspectiveSelector;

    private Combobox frequencyAggSelector;
    private Combobox durationAggSelector;
    private Combobox costAggSelector;

    private Div defaultPerspective;
    private Div defaultFrequency;
    private Div defaultDuration;
    private Div defaultCost;

    private Span freqShow;
    private Span durationShow;
    private Span costShow;

    private Map<MeasureType, Span> showSecondaryMap;
    private Map<MeasureType, Div> measureOptionMap;
    private Map<MeasureType, Combobox> measureAggSelectorMap;
    private Map<MeasureType, Div> defaultAggMap;
    private final Map<MeasureType, MeasureType> defaultSecondaryMap =
        Map.of(
            FREQUENCY, DURATION,
            DURATION, FREQUENCY,
            COST, DURATION
        );
    
    private UserOptionsData userOptions;

    private String primaryTypeLabel;
    private String primaryAggregateCode;
    private static final String FREQ_LABEL = "frequency";
    private static final String DURATION_LABEL = "duration";
    private static final String COST_LABEL = "cost";

    private static final String ON_SELECT = "onSelect";
    private static final String ON_FORCE_SELECT = "onForceSelect";

    private boolean disabled = false;
    private MeasureType primaryMeasureType = FREQUENCY;
    private MeasureType secondaryMeasureType = DURATION;
    private boolean isSecondaryShown = false;

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
        defaultCost = (Div) compViewSettings.getFellow("defaultCost");

        includeSecondary = (Checkbox) compViewSettings.getFellow("includeSecondary");
        includeSecondary.setChecked(userOptions.getIncludeSecondary());

        freqShow = (Span) compViewSettings.getFellow("freqShow");
        freqShow.setVisible(false);
        durationShow = (Span) compViewSettings.getFellow("durationShow");
        durationShow.setVisible(true);
        costShow = (Span) compViewSettings.getFellow("costShow");
        costShow.setVisible(true);

        Div freqOption = (Div) compViewSettings.getFellow("freqOption");
        Div durationOption = (Div) compViewSettings.getFellow("durationOption");
        Div costOption = (Div) compViewSettings.getFellow("costOption");

        frequencyAggSelector = (Combobox) compViewSettings.getFellow("frequencyAggSelector");
        durationAggSelector = (Combobox) compViewSettings.getFellow("durationAggSelector");
        costAggSelector = (Combobox) compViewSettings.getFellow("costAggSelector");

        showSecondaryMap = Map.of(
            FREQUENCY, freqShow,
            DURATION, durationShow,
            COST, costShow
        );
        measureOptionMap = Map.of(
            FREQUENCY, freqOption,
            DURATION, durationOption,
            COST, costOption
        );
        measureAggSelectorMap = Map.of(
            FREQUENCY, frequencyAggSelector,
            DURATION, durationAggSelector,
            COST, costAggSelector
        );
        defaultAggMap = Map.of(
            FREQUENCY, defaultFrequency,
            DURATION, defaultDuration,
            COST, defaultCost
        );

        primaryTypeLabel = FREQ_LABEL;
        primaryAggregateCode = measures[0];
    }

    @Override
    public void initializeEventListeners(Object data) {
        perspectiveSelector.addEventListener(ON_SELECT, event -> {
                String value = perspectiveSelector.getSelectedItem().getValue();
                if (value.equals("-")) return;
                String label = perspectiveSelector.getSelectedItem().getLabel();
                parent.setPerspective(value, label);
        });

        this.bpmnMode.addEventListener("onCheck", event -> parent.setBPMNView(bpmnMode.isChecked()));

        defaultPerspective.addEventListener("onClick", event -> {
                if (disabled) return;
                String value = "concept:name";
                selectComboboxByKey(perspectiveSelector, value);
                String label = perspectiveSelector.getSelectedItem().getLabel();
                parent.setPerspective(value, label);
        });

        defaultFrequency.addEventListener("onClick", event -> {
                if (disabled) return;
                String key = "case";
                selectComboboxByKey(frequencyAggSelector, key);
                selectFrequencyViz();
        });

        defaultDuration.addEventListener("onClick", event ->{
                if (disabled) return;
                String key = "mean";
                selectComboboxByKey(durationAggSelector, key);
                selectDurationViz();
        });

        defaultCost.addEventListener("onClick", event -> {
                if (disabled) return;
                String key = "mean";
                selectComboboxByKey(costAggSelector, key);
                selectCostViz();
        });

        freqShow.addEventListener("onClick", event -> {
                if (disabled) return;
                setSecondaryOverlay(FREQUENCY);
        });

        durationShow.addEventListener("onClick", event -> {
                if (disabled) return;
                setSecondaryOverlay(DURATION);
        });

        costShow.addEventListener("onClick", event -> {
                if (disabled) return;
                setSecondaryOverlay(COST);
        });

        EventListener<Event> frequencyAggSelectorListener = event -> selectFrequencyViz();
        this.frequencyAggSelector.addEventListener(ON_SELECT, frequencyAggSelectorListener);
        this.frequencyAggSelector.addEventListener(ON_FORCE_SELECT, frequencyAggSelectorListener);

        EventListener<Event> durationAggSelectorListener = event -> selectDurationViz();
        this.durationAggSelector.addEventListener(ON_SELECT, durationAggSelectorListener);
        this.durationAggSelector.addEventListener(ON_FORCE_SELECT, durationAggSelectorListener);

        EventListener<Event> costAggSelectorListener = event -> selectCostViz();
        this.costAggSelector.addEventListener(ON_SELECT, costAggSelectorListener);
        this.costAggSelector.addEventListener(ON_FORCE_SELECT, costAggSelectorListener);
    }

    @Override
    public void setDisabled(boolean disabled) {
        this.disabled = disabled;
        bpmnMode.setDisabled(disabled);
        perspectiveSelector.setDisabled(disabled);
        frequencyAggSelector.setDisabled(disabled);
        durationAggSelector.setDisabled(disabled);
        costAggSelector.setDisabled(disabled);
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

        Comparator<Map.Entry<String, String>> comparator = (o1, o2) -> o1.getValue().compareTo(o2.getValue());

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

    private void refreshShowSecondary(MeasureType measureType, boolean isShown) {
        isSecondaryShown = isShown;
        Span showSpan = showSecondaryMap.get(measureType);
        toggleComponentSclass(showSpan, isShown, "ap-icon-eye-close", "ap-icon-eye-open");
        showSpan.setTooltiptext(isShown ?
            parent.getLabel("metricHideSecondary_text") :
            parent.getLabel("metricShowSecondary_text"));
    }

    private Combobox getSelectedAggSelectorByMeasureType(MeasureType measureType) {
        return measureAggSelectorMap.get(measureType);
    }

    private String getSelectedAggByMeasureType(MeasureType measureType) {
        return getSelectedAggSelectorByMeasureType(measureType).getSelectedItem().getValue();
    }

    private void setShowSecondary(boolean isShown) {
        userOptions.setIncludeSecondary(isShown);
        isSecondaryShown = isShown;
        includeSecondary.setChecked(isShown);
    }

    private void setSecondaryOverlay(MeasureType measureType) {
        boolean isShown;
        if (secondaryMeasureType == measureType) {
            isShown = !isSecondaryShown;
            refreshShowSecondary(secondaryMeasureType, isShown);
        } else {
            refreshShowSecondary(secondaryMeasureType, false); // reset old show
            secondaryMeasureType = measureType;
            isShown = true;
        }
        setShowSecondary(isShown);
        refreshShowSecondary(secondaryMeasureType, isShown);
        String primaryAgg = getSelectedAggByMeasureType(primaryMeasureType);
        String secondaryAgg = getSelectedAggByMeasureType(secondaryMeasureType);
        setOverlay(
            primaryMeasureType,
            measureAggMap.get(primaryAgg),
            measureRelationMap.get(primaryAgg),
            secondaryMeasureType,
            measureAggMap.get(secondaryAgg),
            measureRelationMap.get(secondaryAgg),
            primaryAgg
        );
    }

    private void toggleShowSecondary() {
        showSecondaryMap.forEach((key, span) -> span.setVisible(!key.equals(primaryMeasureType)));
    }

    private void toggleMeasureOption() {
        measureOptionMap.forEach((key, div) -> toggleComponentClass(div, key.equals(primaryMeasureType)));
    }

    private void toggleDefaultAgg() {
        defaultAggMap.forEach((key, div) -> toggleComponentClass(div, key.equals(primaryMeasureType)));
    }    

    private void setPrimaryOverlay(MeasureType measureType) {

        primaryMeasureType = measureType;
        if (primaryMeasureType.equals(secondaryMeasureType)) {
            secondaryMeasureType = defaultSecondaryMap.get(primaryMeasureType);
            setShowSecondary(false);
            refreshShowSecondary(secondaryMeasureType, false);
        }

        Combobox primaryAggSelector = getSelectedAggSelectorByMeasureType(primaryMeasureType);
        String primaryAgg = getSelectedAggByMeasureType(primaryMeasureType);
        String secondaryAgg = getSelectedAggByMeasureType(secondaryMeasureType);
        selectComboboxByKey(primaryAggSelector, primaryAgg);

        toggleShowSecondary();
        toggleMeasureOption();
        toggleDefaultAgg();

        setOverlay(
                primaryMeasureType,
                measureAggMap.get(primaryAgg),
                measureRelationMap.get(primaryAgg),
                secondaryMeasureType,
                measureAggMap.get(secondaryAgg),
                measureRelationMap.get(secondaryAgg),
                primaryAgg
        );
    }

    private void selectFrequencyViz() {
        setPrimaryOverlay(FREQUENCY);
    }

    private void selectDurationViz() {
        setPrimaryOverlay(DURATION);
    }

    private void selectCostViz() {
        setPrimaryOverlay(COST);
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
        parent.getUserOptions().setRetainZoomPan(true);

        parent.getUserOptions().setPrimaryType(primaryType);
        parent.getUserOptions().setPrimaryAggregation(primaryAggregation);
        parent.getUserOptions().setPrimaryRelation(primaryRelation);

        parent.getUserOptions().setSecondaryType(secondaryType);
        parent.getUserOptions().setSecondaryAggregation(secondaryAggregation);
        parent.getUserOptions().setSecondaryRelation(secondaryRelation);

        primaryAggregateCode = aggregateCode;
        if (primaryType == FREQUENCY) {
            primaryTypeLabel = parent.getLabel("common_frequencyLabel_text", FREQ_LABEL);
        } else if (primaryType == DURATION) {
            primaryTypeLabel = parent.getLabel("common_durationLabel_text", DURATION_LABEL);
        } else {
            primaryTypeLabel = parent.getLabel("common_durationLabel_text", COST_LABEL);
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
