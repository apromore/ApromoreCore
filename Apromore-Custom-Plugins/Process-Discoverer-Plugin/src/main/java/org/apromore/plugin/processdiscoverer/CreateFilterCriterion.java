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

import org.apromore.plugin.processdiscoverer.impl.filter.Level;
import org.apromore.plugin.processdiscoverer.impl.filter.LogFilterCriterionFactory;
import org.apromore.plugin.processdiscoverer.impl.filter.LogFilterTypeSelector;
import org.apromore.plugin.processdiscoverer.impl.filter.Type;
import org.apromore.plugin.processdiscoverer.impl.util.StringValues;
import org.apromore.plugin.processdiscoverer.impl.util.TimeConverter;
import org.zkoss.zk.ui.Executions;
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
import java.text.DecimalFormat;
import java.util.*;
import java.util.Calendar;

/**
 * Created by Raffaele Conforti (conforti.raffaele@gmail.com) on 05/08/2018.
 * Modified by Bruce Nguyen
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
    private Map<String, Map<String, Integer>> options_frequency; // key: filter type code, value: map{key:code value, value: count}
    private long min; // the earliest timstamp in the log
    private long max; // the latest timstamp in the log
    private int pos; // the index of the Filter Criterion in the list

    private List<String> allFilterTypeCodes; // list of all filter type codes (standard + non-standard)
    private List<String> allFilterTypeNames; // list of all corresponding filter type names (standard + non-standard)
    										 // This list is needed because the values displayed on UI could be different, e.g. with quotes
    
    // The indexes of filter type labels displayed on UI and these lists are the same
    // The values could be different: one is code, name and label (displayed in the list box)
    // Some non-standard types in the list are displayed in quotes 
    private List<String> filterTypeCodes; // valid filter type codes according to the selected level
    private List<String> filterTypeNames; // valid filter type names according to the selected level
    
    private Listbox filterType;
    private Listbox value;
    private Datebox startDate;
    private Datebox endDate;
    private Decimalbox duration;
    private Listbox durationUnits;

    private Button okButton;
    private Button cancelButton;

    public CreateFilterCriterion(String label, FilterCriterionSelector filterCriterionSelector, List<LogFilterCriterion> criteria, Map<String, Map<String, Integer>> options_frequency, long min, long max, int pos) throws IOException {
        this.label = label;
        setInputs(filterCriterionSelector, criteria, options_frequency, min, max, pos);
        initComponents(); 		// Initialize values
        readValues(); 			// Set values based on attributes of the Filter Criterion
        addEventListeners(); 	// Set event listeners for UI elements
        setStatus(); 	// Set initial values for UI elements
        
        createFilterCriterionW.doModal();
    }

    public CreateFilterCriterion(String label, FilterCriterionSelector filterCriterionSelector, List<LogFilterCriterion> criteria, Map<String, Map<String, Integer>> options_frequency, long min, long max) throws IOException {
        this(label, filterCriterionSelector, criteria, options_frequency, min, max, -1);
    }

    private void setInputs(FilterCriterionSelector filterCriterionSelector, List<LogFilterCriterion> criteria, Map<String, Map<String, Integer>> options_frequency, long min, long max, int pos) throws IOException {
        //this.createFilterCriterionW = (Window) filterCriterionSelector.portalContext.getUI().createComponent(getClass().getClassLoader(), "zul/createFilterCriterion.zul", null, null);
    	this.createFilterCriterionW = (Window) Executions.createComponents("/zul/createFilterCriterion.zul", null, null);
        this.createFilterCriterionW.setTitle("Create Filter Criterion");
        this.filterCriterionSelector = filterCriterionSelector;
        this.criteria = criteria;
        this.options_frequency = options_frequency;
        this.min = min;
        this.max = max;
        this.pos = pos;
    }

    // Initialize the initial values for form fields
    private void initComponents() {
        level = (Radiogroup) createFilterCriterionW.getFellow("level");
        containment = (Radiogroup) createFilterCriterionW.getFellow("containment");
        action = (Radiogroup) createFilterCriterionW.getFellow("action");

        filterType = (Listbox) createFilterCriterionW.getFellow("filterType");

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

        duration = (Decimalbox) createFilterCriterionW.getFellow("duration");
        duration.setDisabled(true);
        durationUnits = (Listbox) createFilterCriterionW.getFellow("durationUnits");
        durationUnits.setSelectedIndex(0);
        durationUnits.setDisabled(true);

        value = (Listbox) createFilterCriterionW.getFellow("value");
        Listheader frequency_header = (Listheader) createFilterCriterionW.getFellow("frequency_header");
        frequency_header.setSortAscending(new NumberComparator(true, 1));
        frequency_header.setSortDescending(new NumberComparator(false, 1));
        Listheader percentage_header = (Listheader) createFilterCriterionW.getFellow("percentage_header");
        percentage_header.setSortAscending(new NumberComparator(true, 1));
        percentage_header.setSortDescending(new NumberComparator(false, 1));

        okButton = (Button) createFilterCriterionW.getFellow("criterionOkButton");
        cancelButton = (Button) createFilterCriterionW.getFellow("criterionCancelButton");

        // Update all codes and names to be used
        allFilterTypeCodes = new ArrayList<>(options_frequency.keySet()); // list of filterType types
        this.sortFilterTypeCodes(allFilterTypeCodes);
        allFilterTypeNames = new ArrayList<>();
        for(String option : allFilterTypeCodes) {
        	allFilterTypeNames.add(this.getFilterTypeName(option));
        }
        
        // Update codes and names according to the level
        filterTypeCodes = getValidFilterTypeCodes(allFilterTypeCodes, level);
        filterTypeNames = getValidFilterTypeNames(level);

        this.populateFilterTypes();

    }
    
    /*
     * Standard filter types have labels = "filtername"
     * Non-standard filter types have label = "filtername" (in quotes) 
     */
    private void populateFilterTypes() {
    	filterType.getItems().clear();
        for(String option : filterTypeCodes) {
        	filterType.appendItem(getFilterTypeLabel(option), getFilterTypeLabel(option));
        }
    }
    

    // Set values on the form based on the corresponding attributes of the Filter Criterion 
    private void readValues() {
    	// If an existing Filter Criterion is provided, set form field values to those in the Filter Criterion 
        if(pos != -1) {
            LogFilterCriterion criterion = criteria.get(pos);
            level.setSelectedIndex(criterion.getLevel()== EVENT ? 0 : 1);
            containment.setSelectedIndex(criterion.getContainment() == CONTAIN_ANY ? 0 : 1);
            action.setSelectedIndex(criterion.getAction() == RETAIN ? 0 : 1);
            
            // Update codes, names and displayed labels according to the level of the current Filter Criterion
            filterTypeCodes = getValidFilterTypeCodes(allFilterTypeCodes, level);
            filterTypeNames = getValidFilterTypeNames(level);
            this.populateFilterTypes();

            String a = null;
            int attribute_index = -1;
            for (int i = 0; i < filterTypeCodes.size(); i++) {
                if (filterTypeCodes.get(i).equals(criterion.getAttribute())) {
                    a = filterTypeCodes.get(i);
                    attribute_index = i;
                    break;
                }
            }
            setValues(attribute_index);
            filterType.setSelectedIndex(attribute_index);

            if (LogFilterTypeSelector.getType(a) == Type.TIME_TIMESTAMP) {
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
            }else if (LogFilterTypeSelector.getType(a) == Type.TIME_DURATION) {
                String d = null;
                for (String v : criterion.getValue()) {
                    if (v.startsWith(">")) d = v.substring(1);
                }
                String[] values = TimeConverter.parseDuration2(d);
                duration.setValue(values[0]);
                duration.setDisabled(false);
//                Listitem found = durationUnits.getItems().stream()
//                	.filter(item -> item.getLabel().equals(values[1]))
//                	.findAny()
//                	.orElse(null);
                Listitem found = null;
                for (Listitem item : durationUnits.getItems()) {
                	if (item.getLabel().equals(values[1])) {
                		found = item;
                		break;
                	}
                }
                if (found != null) durationUnits.setSelectedItem(found);
                durationUnits.setDisabled(false);
            }else {
                for(Listitem listitem : value.getItems()) {
                    if(criterion.getValue().contains(listitem.getLabel())) listitem.setSelected(true);
                }
            }
        }
    }

    private void setStatus() {
        if(level.getSelectedIndex() == 0) { // Event Level
            if(filterType.getSelectedIndex() >= 0) {
            	Type type = LogFilterTypeSelector.getType(filterTypeCodes.get(filterType.getSelectedIndex()));
                switch (type) {
	            	case TIME_TIMESTAMP:
	            		okButton.setDisabled(false);
	            		break;
	            	default:
	            		okButton.setDisabled(value.getItems().size() == 0);
                }
            }
            else {
            	okButton.setDisabled(true);
            }
            
            duration.setDisabled(true);
            durationUnits.setDisabled(true);
            for(Radio radio : containment.getItems()) {
                radio.setDisabled(true);
            }
            containment.setStyle("background-color: #D3D3D3;");
        }else { //Trace Level
            //okButton.setDisabled(false);
        	
            if(filterType.getSelectedIndex() >= 0) {  
            	boolean eventInvalid = !LogFilterTypeSelector.checkLevelValidity(filterTypeCodes.get(filterType.getSelectedIndex()), Level.EVENT);
                for (Radio radio : containment.getItems()) {
                    radio.setDisabled(eventInvalid);
                }
                containment.setStyle(eventInvalid ? "background-color: #D3D3D3;" : "transparent;");
                
                Type type = LogFilterTypeSelector.getType(filterTypeCodes.get(filterType.getSelectedIndex()));
                switch (type) {
                	case TIME_TIMESTAMP:
                		okButton.setDisabled(false);
                		break;
                	case TIME_DURATION: 
                		duration.setDisabled(false);
                        durationUnits.setDisabled(false);
                		okButton.setDisabled(false);
                		break;
                	default:
                		okButton.setDisabled(value.getItems().size() == 0);
                }
            }else {
//                for (Radio radio : containment.getItems()) {
//                    radio.setDisabled(false);
//                }
            	okButton.setDisabled(true);
            }
        }
        
    }

    private void addEventListeners() {
        level.addEventListener("onCheck", new EventListener<Event>() {
            @Override
            public void onEvent(Event event) {
            	if (filterType.getSelectedIndex() >= 0) {
            		String currentLabel = filterType.getSelectedItem().getLabel();
                	String currentFilterType = filterTypeCodes.get(filterType.getSelectedIndex());
                	
                	filterTypeCodes = getValidFilterTypeCodes(allFilterTypeCodes, level);
                	filterTypeNames = getValidFilterTypeNames(level);
            		populateFilterTypes(); // filterType is updated
            		
	            	if (isLevelValid(currentFilterType, level)) {
	            		int selectedIndex = -1;
	            		for (Listitem item : filterType.getItems()) {
	            			if (item.getLabel().equals(currentLabel)) {
	            				selectedIndex = item.getIndex();
	            				break;
	            			}
	            		}
	            		if (selectedIndex >= 0) {
	            			filterType.setSelectedIndex(selectedIndex);
	            		}
	            		else {
	            			value.getItems().clear();
	            		}
	            	}
	            	else {
	            		value.getItems().clear();
	            	}
            	}
            	else {
            		filterTypeCodes = getValidFilterTypeCodes(allFilterTypeCodes, level);
                	filterTypeNames = getValidFilterTypeNames(level);
            		populateFilterTypes();
            	}
                setStatus();
            }
        });

        filterType.addEventListener("onSelect", new EventListener<Event>() {
            @Override
            public void onEvent(Event event) {
            	CreateFilterCriterion.this.setValues(filterType.getSelectedIndex());
                setStatus();
            }
        });

        okButton.addEventListener("onClick", new EventListener<Event>() {
            public void onEvent(Event event) throws Exception {
                Set<String> set = new HashSet<>();
                String option = filterTypeCodes.get(filterType.getSelectedIndex());
                //String option = filterTypeNames.get(filterType.getSelectedIndex());
                //if(LogFilterTypeSelector.getName(option) > -1) option = LogFilterTypeSelector.getReverseMatch(option);

                if(LogFilterTypeSelector.getType(option) == Type.TIME_TIMESTAMP) {
                    set.add(">" + startDate.getValue().getTime());
                    set.add("<" + endDate.getValue().getTime());
                }else if(LogFilterTypeSelector.getType(option) == Type.TIME_DURATION) {
                    String span = durationUnits.getSelectedItem().getLabel();
                    //BigDecimal d = TimeConverter.convertMilliseconds(duration.getValue(), span);
                    set.add(">" + duration.getValue().doubleValue() + TimeConverter.DURATION_UNIT_MARKER + span);
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

    /**
     * Set value or populate values to fields/list on the form 
     * @param index: the index of the selected filter type in filterTypeCodes 
     */
    private void setValues(int index) {
        String option = filterTypeNames.get(index); //option is the label of the selected filter type code 

        if(option.equals("Time-frame")) {
            //value.setModel(modelValue);
        	value.getItems().clear();
            startDate.setDisabled(false);
            endDate.setDisabled(false);
            duration.setDisabled(true);
            durationUnits.setDisabled(true);
        }else if(option.equals("Duration")) {
            // value.setModel(modelValue);
        	value.getItems().clear();
            startDate.setDisabled(true);
            endDate.setDisabled(true);
            duration.setDisabled(false);
            durationUnits.setDisabled(false);
        }else {       	
            startDate.setDisabled(true);
            endDate.setDisabled(true);
            duration.setDisabled(true);
            durationUnits.setDisabled(true);

            Collection<String> set;
            String coded_option; // the filter type corresponding to option 
            if(LogFilterTypeSelector.getName(option) > -1) coded_option = LogFilterTypeSelector.getReverseMatch(option);
            else coded_option = option;

            set = options_frequency.get(coded_option).keySet(); // list of values

            value.getItems().clear();
            double total = 0;
            for (String option_value : set) {
                total += options_frequency.get(coded_option).get(option_value); // calculate total frequency
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
                //modelValue.add(listitem);
            }
            value.setCheckmark(true);
            value.setMultiple(true);
            //value.selectAll();
            //modelValue.setMultiple(true);

        }
    }
    
    private Level getLevel(Radiogroup level) {
    	return level.getSelectedIndex() == 0 ? Level.EVENT : Level.TRACE;
    }
    
    private boolean isStandard(String filterType) {
    	return (LogFilterTypeSelector.getType(filterType) != Type.UNKNOWN);
    }
    
    private boolean isLevelValid(String filterType, Radiogroup level) {
    	return LogFilterTypeSelector.checkLevelValidity(filterType, getLevel(level));
    }
    
    private List<String> getValidFilterTypeCodes(List<String> types, Radiogroup level) {
    	List<String> selection = new ArrayList<>();
    	for (String code : types) {
    		if (isLevelValid(code, level)) selection.add(code);
    	}
    	return selection;
    }
    
    private String getFilterTypeName(String type) {
    	if(isStandard(type)) {
        	return LogFilterTypeSelector.getMatch(type);
        }
        else {
        	return type;
        }
    }
   
    private List<String> getValidFilterTypeNames(Radiogroup level) {
    	List<String> labels = new ArrayList<>();
        for(String option : this.getValidFilterTypeCodes(allFilterTypeCodes, level)) {
            labels.add(this.getFilterTypeName(option));
        }
        return labels;
    }
    
    private void sortFilterTypeCodes(List<String> codes) {
        Collections.sort(codes, new Comparator<String>() {
            @Override
            public int compare(String o1, String o2) {
                if(LogFilterTypeSelector.getType(o1) == Type.CONCEPT_NAME) return -1;
                if(LogFilterTypeSelector.getType(o2) == Type.CONCEPT_NAME) return 1;
                
                if(LogFilterTypeSelector.getType(o1) == Type.TIME_TIMESTAMP) return -1;
                if(LogFilterTypeSelector.getType(o2) == Type.TIME_TIMESTAMP) return 1;
                
                if(LogFilterTypeSelector.getType(o1) == Type.TIME_DURATION) return -1;
                if(LogFilterTypeSelector.getType(o2) == Type.TIME_DURATION) return 1;
                
                if(LogFilterTypeSelector.getType(o1) == Type.DIRECT_FOLLOW) return -1;
                if(LogFilterTypeSelector.getType(o2) == Type.DIRECT_FOLLOW) return 1;
                
                if(LogFilterTypeSelector.getType(o1) == Type.EVENTUAL_FOLLOW) return -1;
                if(LogFilterTypeSelector.getType(o2) == Type.EVENTUAL_FOLLOW) return 1;
                
                if(LogFilterTypeSelector.getType(o1) == Type.ORG_RESOURCE) return -1;
                if(LogFilterTypeSelector.getType(o2) == Type.ORG_RESOURCE) return 1;
                
                if(LogFilterTypeSelector.getType(o1) == Type.ORG_GROUP) return -1;
                if(LogFilterTypeSelector.getType(o2) == Type.ORG_GROUP) return 1;
                
                if(LogFilterTypeSelector.getType(o1) == Type.ORG_ROLE) return -1;
                if(LogFilterTypeSelector.getType(o2) == Type.ORG_ROLE) return 1;
                
                if(LogFilterTypeSelector.getType(o1) == Type.LIFECYCLE_TRANSITION) return -1;
                if(LogFilterTypeSelector.getType(o2) == Type.LIFECYCLE_TRANSITION) return 1;
                return o1.compareTo(o2);
            }
        });
    }
    
    //Note: non-standard type has its label displayed in quotes ("").
    private String getFilterTypeLabel(String type) {
        if(isStandard(type)) { 
        	return LogFilterTypeSelector.getMatch(type);
        }
        else { 
        	return "\"" + type + "\"";
        }
    }
   
}
