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

import static org.apromore.logman.attribute.graph.MeasureAggregation.CASES;
import static org.apromore.logman.attribute.graph.MeasureAggregation.MEAN;
import static org.apromore.logman.attribute.graph.MeasureAggregation.TOTAL;
import static org.apromore.logman.attribute.graph.MeasureType.DURATION;
import static org.apromore.logman.attribute.graph.MeasureType.FREQUENCY;

import org.apromore.plugin.portal.PortalLoggerFactory;
import org.apromore.plugin.portal.processdiscoverer.PDController;
import org.apromore.plugin.portal.processdiscoverer.data.UserOptionsData;
import org.slf4j.Logger;
import org.zkoss.util.resource.Labels;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zk.ui.event.InputEvent;
import org.zkoss.zul.Button;
import org.zkoss.zul.Checkbox;
import org.zkoss.zul.Combobox;
import org.zkoss.zul.Intbox;
import org.zkoss.zul.Label;
import org.zkoss.zul.Row;
import org.zkoss.zul.Slider;

/**
 * @author Ivo Widjaja
 * Modified: Ivo Widjaja
 */
public class GraphSettingsController extends VisualController {
    private static final Logger LOGGER = PortalLoggerFactory.getLogger(GraphSettingsController.class);

    private final String METRIC_CASE_FREQ = "metricCaseFreq";
    private final String METRIC_AVG_DURATION = "metricAvgDuration";
    private final String METRIC_CASE_VARIANT = "metricCaseVariant";

    // Graph settings
    private Checkbox metricCaseFreq;
    private Checkbox metricAvgDuration;
    private Checkbox metricCaseVariant;
    private Combobox metricSelector;

    private Button swapOrdering;
    private Label orderingStart;
    private Label orderingEnd;

    private Slider nodeSlider;
    private Intbox nodeInput;
    private Slider arcSlider;
    private Intbox arcInput;
    private Slider parallelismSlider;
    private Intbox parallelismInput;
    private Row parallelismRow;

    private String[] orderingLabels; // = Labels.getLabel("e.pd.orderCaseFreq.text").split(",");

    private boolean isNormalOrdering = true;

    private UserOptionsData userOptions;
    
    private boolean disabled = false;

    public GraphSettingsController(PDController parent) {
        super(parent);
        orderingLabels = parent.getLabel("orderCaseFreq_text").split(",");
    }

    @Override
    public void initializeControls(Object data) {
        if (this.parent == null) return;

        LOGGER.debug("GraphSettingsController");
        userOptions = parent.getUserOptions();
        Component compGraphSettings = parent.query(".ap-pd-graph-settings");

        // Graph/abstraction settings
        metricCaseFreq = (Checkbox) compGraphSettings.getFellow(METRIC_CASE_FREQ);
        metricAvgDuration = (Checkbox) compGraphSettings.getFellow(METRIC_AVG_DURATION);
        metricCaseVariant = (Checkbox) compGraphSettings.getFellow(METRIC_CASE_VARIANT);
        metricSelector = (Combobox) compGraphSettings.getFellow("metricSelector");

        swapOrdering = (Button) compGraphSettings.getFellow("swapOrdering");
        orderingStart = (Label) compGraphSettings.getFellow("orderingStart");
        orderingEnd = (Label) compGraphSettings.getFellow("orderingEnd");

        nodeSlider = (Slider) compGraphSettings.getFellow("nodeSlider");
        nodeSlider.setCurpos(userOptions.getNodeFilterValue());
        nodeInput = (Intbox) compGraphSettings.getFellow("nodeInput");
        arcSlider = (Slider) compGraphSettings.getFellow("arcSlider");
        arcSlider.setCurpos(userOptions.getArcFilterValue());
        arcInput = (Intbox) compGraphSettings.getFellow("arcInput");
        parallelismSlider = (Slider) compGraphSettings.getFellow("parallelismSlider");
        parallelismSlider.setCurpos(userOptions.getParallelismFilterValue());
        parallelismInput = (Intbox) compGraphSettings.getFellow("parallelismInput");
        parallelismRow = (Row) compGraphSettings.getFellow("parallelismRow");
        // parallelismSlider.setStyle("background-color:#D8D8D8; width:100%; height:25px;");
        parallelismInput.setDisabled(true);
        parallelismRow.setSclass("z-row-disabled");
    }

    @Override
    public void initializeEventListeners(Object data) {
        EventListener<Event> metricListener = new EventListener<Event>() {
            @Override
            public void onEvent(Event event) throws Exception {
                String compId = ((Checkbox) event.getTarget()).getId();
                setMetric(compId);
            }
        };
        this.metricCaseFreq.addEventListener("onCheck", metricListener);
        this.metricAvgDuration.addEventListener("onCheck", metricListener);
        this.metricCaseVariant.addEventListener("onCheck", metricListener);

        metricSelector.addEventListener("onSelect", new EventListener<Event>() {
            @Override
            public void onEvent(Event event) throws Exception {
                String metric = metricSelector.getSelectedItem().getValue();
                setMetric(metric);
            }
        });

        EventListener<Event> swapOrderingListener = new EventListener<Event>() {
            @Override
            public void onEvent(Event event) throws Exception {
                isNormalOrdering = !isNormalOrdering;
                updateOrderingLabels();
                parent.generateViz();
            }
        };
        this.swapOrdering.addEventListener("onClick", swapOrderingListener);

        this.nodeSlider.addEventListener("onScroll", new EventListener<Event>() {
            @Override
            public void onEvent(Event event) throws Exception {
                if (disabled) {
                    nodeSlider.setCurpos(userOptions.getNodeFilterValue());
                    return;
                }
                nodeInput.setValue(nodeSlider.getCurpos());
                userOptions.setNodeFilterValue(nodeSlider.getCurpos());
                parent.generateViz();
            }
        });

        this.arcSlider.addEventListener("onScroll", new EventListener<Event>() {
            @Override
            public void onEvent(Event event) throws Exception {
                if (disabled) {
                    arcSlider.setCurpos(userOptions.getArcFilterValue());
                    return;
                }
                arcInput.setValue(arcSlider.getCurpos());
                userOptions.setArcFilterValue(arcSlider.getCurpos());
                parent.generateViz();
            }
        });

        this.parallelismSlider.addEventListener("onScrolling", new EventListener<Event>() {
            @Override
            public void onEvent(Event event) throws Exception {
                if (!userOptions.getBPMNMode() || disabled) {
                    parallelismSlider.setCurpos(userOptions.getParallelismFilterValue());
                }
            }
        });

        this.parallelismSlider.addEventListener("onScroll", new EventListener<Event>() {
            @Override
            public void onEvent(Event event) throws Exception {
                if (parent.getUserOptions().getBPMNMode() && !disabled) { // if BPMN mode
                    parallelismInput.setValue(parallelismSlider.getCurpos());
                    userOptions.setParallelismFilterValue(parallelismSlider.getCurpos());
                    parent.generateViz();
                } else {
                    parallelismSlider.setCurpos(userOptions.getParallelismFilterValue());
                }
            }
        });

        EventListener<Event> nodeChangeListener = new EventListener<Event>() {
            @Override
            public void onEvent(Event event) throws Exception {
                ensureSlider(nodeSlider, nodeInput);
                userOptions.setNodeFilterValue(nodeSlider.getCurpos());
                parent.generateViz();
            }
        };
        this.nodeInput.addEventListener("onChange", nodeChangeListener);

        EventListener<Event> arcChangeListener = new EventListener<Event>() {
            @Override
            public void onEvent(Event event) throws Exception {
                ensureSlider(arcSlider, arcInput);
                userOptions.setArcFilterValue(arcSlider.getCurpos());
                parent.generateViz();
            }
        };
        this.arcInput.addEventListener("onChange", arcChangeListener);

        EventListener<Event> parallelismChangeListener = new EventListener<Event>() {
            @Override
            public void onEvent(Event event) throws Exception {
                ensureSlider(parallelismSlider, parallelismInput);
                userOptions.setParallelismFilterValue(parallelismSlider.getCurpos());
                parent.generateViz();
            }
        };
        EventListener<Event> parallelismChangingListener = new EventListener<Event>() {
            @Override
            public void onEvent(Event event) throws Exception {
                String val = ((InputEvent) event).getValue();
                ensureSlider(parallelismSlider, parallelismInput, val);
                parent.generateViz();
            }
        };
        this.parallelismInput.addEventListener("onChange", parallelismChangeListener);
        this.parallelismInput.addEventListener("onChanging", parallelismChangingListener);

    }
    
    @Override
    public void setDisabled(boolean disabled) {
        this.disabled = disabled;
        metricSelector.setDisabled(disabled);
        swapOrdering.setDisabled(disabled);
        nodeInput.setDisabled(disabled);
        arcInput.setDisabled(disabled);
        parallelismInput.setDisabled(disabled || !parent.getUserOptions().getBPMNMode());
    }
    
    private void setMetric(String metric) {
        switch (metric) {
            case METRIC_CASE_FREQ:
                userOptions.setFixedType(FREQUENCY);
                userOptions.setFixedAggregation(CASES);
                toggleComponentClass(metricCaseFreq, true);
                toggleComponentClass(metricAvgDuration, false);
                toggleComponentClass(metricCaseVariant, false);
                metricAvgDuration.setChecked(false);
                metricCaseVariant.setChecked(false);
                // orderingLabels = Labels.getLabel("e.pd.orderCaseFreq.text").split(",");
                orderingLabels = parent.getLabel("orderCaseFreq_text").split(",");
                break;
            case METRIC_AVG_DURATION:
                userOptions.setFixedType(DURATION);
                userOptions.setFixedAggregation(MEAN);
                toggleComponentClass(metricCaseFreq, false);
                toggleComponentClass(metricAvgDuration, true);
                toggleComponentClass(metricCaseVariant, false);
                metricCaseFreq.setChecked(false);
                metricCaseVariant.setChecked(false);
                // orderingLabels = Labels.getLabel("e.pd.orderDuration.text").split(",");
                orderingLabels = parent.getLabel("orderDuration_text").split(",");
                break;
            case METRIC_CASE_VARIANT:
                userOptions.setFixedType(FREQUENCY);
                userOptions.setFixedAggregation(TOTAL);
                toggleComponentClass(metricCaseFreq, false);
                toggleComponentClass(metricAvgDuration, false);
                toggleComponentClass(metricCaseVariant, true);
                metricAvgDuration.setChecked(false);
                metricCaseFreq.setChecked(false);
                // orderingLabels = Labels.getLabel("e.pd.orderCaseVariant.text").split(",");
                orderingLabels = parent.getLabel("orderCaseVariant_text").split(",");
                break;
        }
        updateOrderingLabels();
        parent.generateViz();
    }

    public boolean getNormalOrdering() {
        return isNormalOrdering;
    }


    private void updateOrderingLabels() {
        String firstLabel, secondLabel;

        if (isNormalOrdering) {
            firstLabel = orderingLabels[0];
            secondLabel = orderingLabels[1];
        } else {
            firstLabel = orderingLabels[1];
            secondLabel = orderingLabels[0];
        }
        orderingStart.setValue(firstLabel);
        orderingEnd.setValue(secondLabel);
    }

    public void updateParallelism(boolean bpmnMode) {
        if (bpmnMode) {
            parallelismRow.setSclass("z-row");
            parallelismInput.setDisabled(false);
        } else {
            parallelismRow.setSclass("z-row-disabled");
            parallelismInput.setDisabled(true);
            userOptions.setParallelismFilterValue(parallelismSlider.getCurpos());
        }
    }

    @Override
    public void ensureSliders() throws Exception {
        ensureSlider(nodeSlider, nodeInput);
        ensureSlider(arcSlider, arcInput);
        ensureSlider(parallelismSlider, parallelismInput);
    }

    @Override
    public void onEvent(Event event) throws Exception {
        throw new Exception("Unsupported interactive Event Handler");
    }


}
