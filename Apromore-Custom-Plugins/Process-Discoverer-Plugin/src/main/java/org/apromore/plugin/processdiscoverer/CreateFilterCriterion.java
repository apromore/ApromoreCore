/*
 * Copyright © 2019 The University of Melbourne.
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

package org.apromore.plugin.processdiscoverer;

import org.apromore.plugin.processdiscoverer.impl.filter.LogFilterCriterionFactory;
import org.apromore.plugin.processdiscoverer.impl.filter.LogFilterTypeSelector;
import org.apromore.plugin.processdiscoverer.impl.util.StringValues;
import org.apromore.plugin.processdiscoverer.impl.util.TimeConverter;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zul.*;

import static org.apromore.plugin.processdiscoverer.impl.filter.Action.REMOVE;
import static org.apromore.plugin.processdiscoverer.impl.filter.Action.RETAIN;
import static org.apromore.plugin.processdiscoverer.impl.filter.Containment.CONTAIN_ALL;
import static org.apromore.plugin.processdiscoverer.impl.filter.Containment.CONTAIN_ANY;
import static org.apromore.plugin.processdiscoverer.impl.filter.Level.EVENT;
import static org.apromore.plugin.processdiscoverer.impl.filter.Level.TRACE;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.text.DecimalFormat;
import java.util.*;
import java.util.Calendar;

/**
 * Created by Raffaele Conforti (conforti.raffaele@gmail.com) on 05/08/2018.
 */
class CreateFilterCriterion {

    private final DecimalFormat decimalFormat = new DecimalFormat(StringValues.b[123]);
    private final String label;

    private Window createFilterCriterionW;
    private Radiogroup level;
    private Radiogroup containment;
    private Radiogroup action;

    private FilterCriterionSelector filterCriterionSelector;
    private List<LogFilterCriterion> criteria;
    private Map<String, Map<String, Integer>> options_frequency;
    private long min;
    private long max;
    private int pos;

    private List<String> attributes;
    private ListModelList<String> modelAttribute;
    private Listbox attribute;
    private Listbox value;
    private Datebox startDate;
    private Datebox endDate;
    private Decimalbox decimalbox;
    private Listbox timespan;

    private Button okButton;
    private Button cancelButton;

    public CreateFilterCriterion(String label, FilterCriterionSelector filterCriterionSelector, List<LogFilterCriterion> criteria, Map<String, Map<String, Integer>> options_frequency, long min, long max, int pos) throws IOException {
        this.label = label;
        setInputs(filterCriterionSelector, criteria, options_frequency, min, max, pos);
        initComponents();
        importValues();
        addEventListeners();

        createFilterCriterionW.doModal();
    }

    public CreateFilterCriterion(String label, FilterCriterionSelector filterCriterionSelector, List<LogFilterCriterion> criteria, Map<String, Map<String, Integer>> options_frequency, long min, long max) throws IOException {
        this(label, filterCriterionSelector, criteria, options_frequency, min, max, -1);
    }

    private void setInputs(FilterCriterionSelector filterCriterionSelector, List<LogFilterCriterion> criteria, Map<String, Map<String, Integer>> options_frequency, long min, long max, int pos) throws IOException {
        this.createFilterCriterionW = (Window) filterCriterionSelector.portalContext.getUI().createComponent(getClass().getClassLoader(), "zul/createFilterCriterion.zul", null, null);
        this.createFilterCriterionW.setTitle("Create Filter Criterion");
        this.filterCriterionSelector = filterCriterionSelector;
        this.criteria = criteria;
        this.options_frequency = options_frequency;
        this.min = min;
        this.max = max;
        this.pos = pos;
    }

    private void initComponents() {
        level = (Radiogroup) createFilterCriterionW.getFellow("level");
        containment = (Radiogroup) createFilterCriterionW.getFellow("containment");
        action = (Radiogroup) createFilterCriterionW.getFellow("action");

        attribute = (Listbox) createFilterCriterionW.getFellow("attribute");

        Calendar c = Calendar.getInstance();
        c.setTime(new Date(min));
        c.add(Calendar.DATE, -1);
        Date s = c.getTime();
        c.setTime(new Date(max));
        c.add(Calendar.DATE, 1);
        Date e = c.getTime();
        SimpleDateConstraint simpleDateConstraint = new SimpleDateConstraint(SimpleConstraint.NO_ZERO, s, e, null);

        startDate = (Datebox) createFilterCriterionW.getFellow("startDate");
        startDate.setConstraint(simpleDateConstraint);
        startDate.setValue(new Date(min));
        startDate.setDisabled(true);

        endDate = (Datebox) createFilterCriterionW.getFellow("endDate");
        endDate.setConstraint(simpleDateConstraint);
        endDate.setValue(new Date(max));
        endDate.setDisabled(true);

        decimalbox = (Decimalbox) createFilterCriterionW.getFellow("duration");
        decimalbox.setDisabled(true);
        timespan = (Listbox) createFilterCriterionW.getFellow("timespan");
        timespan.setDisabled(true);

        value = (Listbox) createFilterCriterionW.getFellow("value");
        Listheader frequency_header = (Listheader) createFilterCriterionW.getFellow("frequency_header");
        frequency_header.setSortAscending(new NumberComparator(true, 1));
        frequency_header.setSortDescending(new NumberComparator(false, 1));
        Listheader percentage_header = (Listheader) createFilterCriterionW.getFellow("percentage_header");
        percentage_header.setSortAscending(new NumberComparator(true, 1));
        percentage_header.setSortDescending(new NumberComparator(false, 1));

        okButton = (Button) createFilterCriterionW.getFellow("criterionOkButton");
        cancelButton = (Button) createFilterCriterionW.getFellow("criterionCancelButton");

        modelAttribute = new ListModelList<>();

        attributes = new ArrayList<>(options_frequency.keySet());
        Collections.sort(attributes, new Comparator<String>() {
            @Override
            public int compare(String o1, String o2) {
                if(LogFilterTypeSelector.getType(o1) == 0) return -1;
                if(LogFilterTypeSelector.getType(o2) == 0) return 1;
                if(LogFilterTypeSelector.getType(o1) == 8) return -1;
                if(LogFilterTypeSelector.getType(o2) == 8) return 1;
                if(LogFilterTypeSelector.getType(o1) == 7) return -1;
                if(LogFilterTypeSelector.getType(o2) == 7) return 1;
                if(LogFilterTypeSelector.getType(o1) == 1) return -1;
                if(LogFilterTypeSelector.getType(o2) == 1) return 1;
                if(LogFilterTypeSelector.getType(o1) == 2) return -1;
                if(LogFilterTypeSelector.getType(o2) == 2) return 1;
                if(LogFilterTypeSelector.getType(o1) == 5) return -1;
                if(LogFilterTypeSelector.getType(o2) == 5) return 1;
                if(LogFilterTypeSelector.getType(o1) == 4) return -1;
                if(LogFilterTypeSelector.getType(o2) == 4) return 1;
                if(LogFilterTypeSelector.getType(o1) == 6) return -1;
                if(LogFilterTypeSelector.getType(o2) == 6) return 1;
                if(LogFilterTypeSelector.getType(o1) == 3) return -1;
                if(LogFilterTypeSelector.getType(o2) == 3) return 1;
                return o1.compareTo(o2);
            }
        });

        for(String option : attributes) {
            if(LogFilterTypeSelector.getType(option) > -1) modelAttribute.add(LogFilterTypeSelector.getMatch(option));
            else modelAttribute.add(option);
        }

        for(String option : attributes) {
            if(LogFilterTypeSelector.getType(option) > -1) attribute.appendItem(LogFilterTypeSelector.getMatch(option), LogFilterTypeSelector.getMatch(option));
            else attribute.appendItem("\"" + option + "\"", "\"" + option + "\"");
        }

    }

    private void importValues() {
        if(pos != -1) {
            LogFilterCriterion criterion = criteria.get(pos);
            level.setSelectedIndex(criterion.getLevel()== EVENT ? 0 : 1);
            containment.setSelectedIndex(criterion.getContainment() == CONTAIN_ANY ? 0 : 1);
            action.setSelectedIndex(criterion.getAction() == RETAIN ? 0 : 1);

            String a = null;
            int attribute_index = -1;
            for (int i = 0; i < attributes.size(); i++) {
                if (attributes.get(i).equals(criterion.getAttribute())) {
                    a = attributes.get(i);
                    attribute_index = i;
                    break;
                }
            }
            setValues(attribute_index);
            attribute.setSelectedIndex(attribute_index);

            if (LogFilterTypeSelector.getType(a) == 8) {
                Long start = null;
                Long end = null;
                for (String v : criterion.getValue()) {
                    if (v.startsWith(">")) start = Long.parseLong(v.substring(1));
                    if (v.startsWith("<")) end = Long.parseLong(v.substring(1));
                }

                startDate.setValue(new Date(start));
                startDate.setDisabled(false);

                endDate.setValue(new Date(end));
                endDate.setDisabled(false);
            }else if (LogFilterTypeSelector.getType(a) == 7) {
                Double d = null;
                for (String v : criterion.getValue()) {
                    if (v.startsWith(">")) d = Double.parseDouble(v.substring(1));
                }
                String[] p = TimeConverter.parseDuration(d);
                decimalbox.setValue(p[0]);
                decimalbox.setDisabled(false);
                timespan.setSelectedIndex(Integer.parseInt(p[1]));
                timespan.setDisabled(false);
            }else {
                for(Listitem listitem : value.getItems()) {
                    if(criterion.getValue().contains(listitem.getLabel())) listitem.setSelected(true);
                }
            }
        }
    }

    private void setFunctionalities() {
        if(level.getSelectedIndex() == 0) {
            if(attribute.getSelectedIndex() > 0) {
                if(LogFilterTypeSelector.getType(attributes.get(attribute.getSelectedIndex())) > -1) {
                    okButton.setDisabled(true);
                } else {
                    okButton.setDisabled(false);
                }
            }
            for(Radio radio : containment.getItems()) {
                radio.setDisabled(true);
            }
        }else {
            okButton.setDisabled(false);

            if(attribute.getSelectedIndex() > 0) {
                if(LogFilterTypeSelector.getType(attributes.get(attribute.getSelectedIndex())) > -1) {
                    for (Radio radio : containment.getItems()) {
                        radio.setDisabled(true);
                    }
                }
            }else {
                for (Radio radio : containment.getItems()) {
                    radio.setDisabled(false);
                }
            }
        }
    }

    private void addEventListeners() {
        level.addEventListener("onCheck", new EventListener<Event>() {
            @Override
            public void onEvent(Event event) {
                setFunctionalities();
            }
        });

        attribute.addEventListener("onSelect", new EventListener<Event>() {
            @Override
            public void onEvent(Event event) {
                setFunctionalities();
                CreateFilterCriterion.this.setValues(attribute.getSelectedIndex());
            }
        });

        okButton.addEventListener("onClick", new EventListener<Event>() {
            public void onEvent(Event event) throws Exception {
                Set<String> set = new HashSet<>();
                String option = modelAttribute.get(attribute.getSelectedIndex());
                if(LogFilterTypeSelector.getName(option) > -1) option = LogFilterTypeSelector.getReverseMatch(option);

                if(LogFilterTypeSelector.getType(option) == 8) {
                    set.add(">" + startDate.getValue().getTime());
                    set.add("<" + endDate.getValue().getTime());
                }else if(LogFilterTypeSelector.getType(option) == 7) {
                    String span = timespan.getSelectedItem().getLabel();
                    Double d = decimalbox.getValue().doubleValue();

                    double seconds = 1000.0;
                    double minutes = seconds * 60.0;
                    double hours = minutes * 60.0;
                    double days = hours * 24.0;
                    double weeks = days * 7.0;
                    double months = days * 30.0;
                    double years = days * 365.0;

                    if(span.equals("Years")) d *= years;
                    else if(span.equals("Months")) d *= months;
                    else if(span.equals("Weeks")) d *= weeks;
                    else if(span.equals("Days")) d *= days;
                    else if(span.equals("Hours")) d *= hours;
                    else if(span.equals("Minutes")) d *= minutes;
                    else if(span.equals("Seconds")) d *= seconds;

                    set.add(">" + d);
                }else {
                    for (Listitem listItem : value.getSelectedItems()) {
                        set.add(((Listcell) listItem.getFirstChild()).getLabel());
                    }
                }
                if (set.size() > 0) {
                    LogFilterCriterion criterion = LogFilterCriterionFactory.getLogFilterCriterion(
                            action.getSelectedIndex() == 0 ? RETAIN : REMOVE,
                            containment.getSelectedIndex() == 0 ? CONTAIN_ANY : CONTAIN_ALL,
                            level.getSelectedIndex() == 0 ? EVENT : TRACE,
                            label,
                            option,
                            set
                    );
                    if(pos == -1) {
                        criteria.add(criterion);
                    }else {
                        criteria.set(pos, criterion);
                    }
                    filterCriterionSelector.updateList();
                }
                createFilterCriterionW.detach();
            }
        });
        cancelButton.addEventListener("onClick", new EventListener<Event>() {
            public void onEvent(Event event) {
                createFilterCriterionW.detach();
            }
        });
    }

    private void setValues(int index) {
        ListModelList<String> modelValue = new ListModelList<>();
        String option = modelAttribute.get(index);

        if(option.equals("Time-frame")) {
            value.setModel(modelValue);
            startDate.setDisabled(false);
            endDate.setDisabled(false);
            decimalbox.setDisabled(true);
            timespan.setDisabled(true);
        }else if(option.equals("Duration")) {
            value.setModel(modelValue);
            startDate.setDisabled(true);
            endDate.setDisabled(true);
            decimalbox.setDisabled(false);
            timespan.setDisabled(false);
        }else {
            startDate.setDisabled(true);
            endDate.setDisabled(true);
            decimalbox.setDisabled(true);
            timespan.setDisabled(true);

            Collection<String> set;
            String coded_option;
            if(LogFilterTypeSelector.getName(option) > -1) coded_option = LogFilterTypeSelector.getReverseMatch(option);
            else coded_option = option;

            set = options_frequency.get(coded_option).keySet();

            value.getItems().clear();
            double total = 0;
            for (String option_value : set) {
                total += options_frequency.get(coded_option).get(option_value);
            }

            for (String option_value : set) {
                Listcell listcell1 = new Listcell(option_value);
                Listcell listcell2 = new Listcell(options_frequency.get(coded_option).get(option_value).toString());
                Listcell listcell3 = new Listcell(decimalFormat.format(100 * ((double) options_frequency.get(coded_option).get(option_value) / total)) + "%");

                Listitem listitem = new Listitem();
                listitem.appendChild(listcell1);
                listitem.appendChild(listcell2);
                listitem.appendChild(listcell3);
                value.appendChild(listitem);
            }
            value.setCheckmark(true);
            value.setMultiple(true);

        }
    }

}
