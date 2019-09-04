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

package org.apromore.plugin.portal.logfilter;

import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zul.*;
import org.apromore.logfilter.criteria.LogFilterCriterion;
import org.apromore.logfilter.criteria.factory.LogFilterCriterionFactory;
import org.apromore.logfilter.criteria.model.Action;
import org.apromore.logfilter.criteria.model.Containment;
import org.apromore.logfilter.criteria.model.Level;
import org.apromore.logfilter.criteria.model.LogFilterTypeSelector;
import org.apromore.logfilter.criteria.model.Type;
import org.apromore.plugin.portal.PortalContext;
import org.apromore.plugin.portal.logfilter.util.NumberComparator;
import org.apromore.plugin.portal.logfilter.util.StringComparator;
import org.apromore.plugin.portal.logfilter.util.TimeConverter;
import java.io.IOException;
import java.sql.Time;
import java.text.DecimalFormat;
import java.util.*;
import java.util.Calendar;

/**
 * This is a dialog to create one filter criterion
 * Created by Raffaele Conforti (conforti.raffaele@gmail.com) on 05/08/2018.
 * Modified by Bruce Nguyen
 */
public class FilterCriterionDialog {

    private final DecimalFormat decimalFormat = new DecimalFormat("##############0.##");
    private final String label;

    private Window createFilterCriterionW;
    private Radiogroup level;
    private Radiogroup containment;
    private Radiogroup action;

    private Radio levelTrace;
    private Radio levelEvent;

    private Grid gridContainment;
    private Grid gridTimeframe;
    private Grid gridDuration;

//    private Tab tabTimeframe;
//    private Tab tabAttribute;
//    private Tab tabDFollow;
//    private Tab tabEFollow;

    private LogFilterController filterCriterionSelector;
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

    private Listbox lbxFilterType;
    private Listbox lbxValue;
    private Datebox dateBxStartDate;
    private Datebox dateBxEndDate;

//    private Decimalbox duration;
//    private Listbox durationUnits;
    private Decimalbox dBxDurationFrom, dBxDurationTo;
    private Listbox lbxDurationUnitsFrom, lbxDurationUnitsTo;

    private Listheader lhValue1;
    private Listheader lhValue2;
    private Listheader lhValue3;

    private Hlayout hlDFollow;
    private Listheader lhDFollowFrom;
    private Listheader lhDFollowTo;
    private Listbox lbxDFollowFrom;
    private Listbox lbxDFollowTo;

    private Hlayout hlEFollow;
    private Listheader lhEFollowFrom;
    private Listheader lhEFollowTo;
    private Listbox lbxEFollowFrom;
    private Listbox lbxEFollowTo;

    private West detailView;
    private Listbox lbxItemDetail;

    private Button okButton;
    private Button cancelButton;
//    private Button btCloseWindow;
    
    private PortalContext portalContext;
    
    private LogFilterCriterionFactory logFilterCriterionFactory;

    public FilterCriterionDialog(PortalContext portalContext, String label, LogFilterController filterCriterionSelector,
    							LogFilterCriterionFactory logFilterCriterionFactory,
    							List<LogFilterCriterion> criteria, 
    							Map<String, Map<String, Integer>> options_frequency, 
    							long min, long max, int pos) throws IOException {
    	this.portalContext = portalContext;
    	this.logFilterCriterionFactory = logFilterCriterionFactory;
        this.label = label;
        setInputs(filterCriterionSelector, criteria, options_frequency, min, max, pos);
        initComponents(); 		// Initialize values
        readValues(); 			// Set values based on attributes of the Filter Criterion
        addEventListeners(); 	// Set event listeners for UI elements
        setStatus(); 	// Set initial values for UI elements
        
        createFilterCriterionW.doModal();
    }

    public FilterCriterionDialog(PortalContext portalContext, String label, LogFilterController filterCriterionSelector,
    							LogFilterCriterionFactory logFilterCriterionFactory,
    							List<LogFilterCriterion> criteria, 
    							Map<String, Map<String, Integer>> options_frequency, long min, long max) throws IOException {
        this(portalContext, label, filterCriterionSelector, logFilterCriterionFactory, criteria, options_frequency, min, max, -1);
    }

    private void setInputs(LogFilterController filterCriterionSelector, List<LogFilterCriterion> criteria, Map<String, Map<String, Integer>> options_frequency, long min, long max, int pos) throws IOException {
        //this.createFilterCriterionW = (Window) filterCriterionSelector.portalContext.getUI().createComponent(getClass().getClassLoader(), "zul/createFilterCriterion.zul", null, null);
    	//this.createFilterCriterionW = (Window) Executions.createComponents("/zul/createFilterCriterion.zul", null, null);
    	this.createFilterCriterionW = (Window) portalContext.getUI().createComponent(getClass().getClassLoader(), "zul/createFilterCriterion.zul", null, null);
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

        lbxFilterType = (Listbox) createFilterCriterionW.getFellow("lbxFilterType");
        gridContainment = (Grid) createFilterCriterionW.getFellow("gridContainment");
        gridTimeframe = (Grid) createFilterCriterionW.getFellow("gridTimeframe");
        gridTimeframe.setVisible(false);

        levelTrace = (Radio) createFilterCriterionW.getFellow("levelTrace");
        levelEvent = (Radio) createFilterCriterionW.getFellow("levelEvent");

        Calendar c = Calendar.getInstance();
        c.setTime(new Date(min));
        c.add(Calendar.DATE, -1);
        Date s = c.getTime();
        c.setTime(new Date(max));
        c.add(Calendar.DATE, 1);
        Date e = c.getTime();
        SimpleDateConstraint simpleDateConstraint = new SimpleDateConstraint(SimpleConstraint.NO_ZERO, s, e, null);

        dateBxStartDate = (Datebox) createFilterCriterionW.getFellow("dateBxStartDate");
//        dateBxStartDate.setConstraint(simpleDateConstraint); // this cause the value never get update
        dateBxStartDate.setValue(new Date(min));
        dateBxStartDate.setDisabled(true);

        dateBxEndDate = (Datebox) createFilterCriterionW.getFellow("dateBxEndDate");
//        dateBxEndDate.setConstraint(simpleDateConstraint); // this cause the value never get update
        dateBxEndDate.setValue(new Date(max));
        dateBxEndDate.setDisabled(true);

        gridDuration = (Grid) createFilterCriterionW.getFellow("gridDuration");
        gridDuration.setVisible(false);
//        duration = (Decimalbox) createFilterCriterionW.getFellow("duration");
//        duration.setDisabled(true);
//        durationUnits = (Listbox) createFilterCriterionW.getFellow("durationUnits");
//        durationUnits.setSelectedIndex(0);
//        durationUnits.setDisabled(true);

        dBxDurationFrom = (Decimalbox) createFilterCriterionW.getFellow("dBxDurationFrom");
        dBxDurationTo = (Decimalbox) createFilterCriterionW.getFellow("dBxDurationTo");
        lbxDurationUnitsFrom = (Listbox) createFilterCriterionW.getFellow("lbxDurationUnitsFrom");
        lbxDurationUnitsTo = (Listbox) createFilterCriterionW.getFellow("lbxDurationUnitsTo");
        lbxDurationUnitsFrom.setSelectedIndex(0);
        lbxDurationUnitsTo.setSelectedIndex(0);

        lhValue1 = (Listheader) createFilterCriterionW.getFellow("lhValue1");
        lhValue2 = (Listheader) createFilterCriterionW.getFellow("lhValue2");
        lhValue2.setSortAscending(new NumberComparator(true, 1));
        lhValue2.setSortDescending(new NumberComparator(false, 1));
        lhValue3 = (Listheader) createFilterCriterionW.getFellow("lhValue3");
//        lhValue3.setSortAscending(new NumberComparator(true, 2));
//        lhValue3.setSortDescending(new NumberComparator(false, 2));


        lbxValue = (Listbox) createFilterCriterionW.getFellow("lbxValue");
        lbxValue.setVisible(false);
//        Listheader frequency_header = (Listheader) createFilterCriterionW.getFellow("frequency_header");
//        frequency_header.setSortAscending(new NumberComparator(true, 1));
//        frequency_header.setSortDescending(new NumberComparator(false, 1));
//        Listheader percentage_header = (Listheader) createFilterCriterionW.getFellow("percentage_header");
//        percentage_header.setSortAscending(new NumberComparator(true, 1));
//        percentage_header.setSortDescending(new NumberComparator(false, 1));

        lbxDFollowFrom = (Listbox) createFilterCriterionW.getFellow("lbxDFollowFrom");
        lhDFollowFrom = (Listheader) createFilterCriterionW.getFellow("lhDFollowFrom");
        lhDFollowTo = (Listheader) createFilterCriterionW.getFellow("lhDFollowTo");
        lbxDFollowTo = (Listbox) createFilterCriterionW.getFellow("lbxDFollowTo");
        hlDFollow = (Hlayout) createFilterCriterionW.getFellow("hlDFollow");

        lbxEFollowFrom = (Listbox) createFilterCriterionW.getFellow("lbxEFollowFrom");
        lhEFollowFrom = (Listheader) createFilterCriterionW.getFellow("lhEFollowFrom");
        lhEFollowTo = (Listheader) createFilterCriterionW.getFellow("lhEFollowTo");
        lbxEFollowTo = (Listbox) createFilterCriterionW.getFellow("lbxEFollowTo");
        hlEFollow = (Hlayout) createFilterCriterionW.getFellow("hlEFollow");

        detailView = (West) createFilterCriterionW.getFellow("detailView");
        lbxItemDetail = (Listbox) createFilterCriterionW.getFellow("lbxItemDetail");

        okButton = (Button) createFilterCriterionW.getFellow("criterionOkButton");
        cancelButton = (Button) createFilterCriterionW.getFellow("criterionCancelButton");
//        btCloseWindow = (Button) createFilterCriterionW.getFellow("btCloseWindow");
        // Update all codes and names to be used
        allFilterTypeCodes = new ArrayList<>(options_frequency.keySet()); // list of filterType types
        this.sortFilterTypeCodes(allFilterTypeCodes);
//        allFilterTypeNames = new ArrayList<>();
//        for(String option : allFilterTypeCodes) {
//        	allFilterTypeNames.add(this.getFilterTypeName(option));
//        }
        
        // Update codes and names according to the level
        filterTypeCodes = getValidFilterTypeCodes(allFilterTypeCodes, level);
//        filterTypeNames = getValidFilterTypeNames(level);

        this.populateFilterTypes();
    }

    /*
     * Standard filter types have labels = "filtername"
     * Non-standard filter types have label = "filtername" (in quotes) 
     */
    private void populateFilterTypes() {
        lbxFilterType.getItems().clear();
//        String optionListString = "";
        for(int i = 0; i < filterTypeCodes.size(); i++) {
            String filterCode = filterTypeCodes.get(i);
//            optionListString += option + "\n";
            if(!filterCode.equals("time:duration")) lbxFilterType.appendItem(getFilterTypeLabel(filterCode), filterCode);// getFilterTypeLabel(option));
        }
//        Messagebox.show(optionListString);
    }
    

    // Set values on the form based on the corresponding attributes of the Filter Criterion 
    private void readValues() {

        /**
         * Set direct follow relation: 'FROM' values
         */
        // Add [Start] element to the list
        Listcell cellStart = new Listcell("[Start]");
        Listitem itemStart = new Listitem();
        itemStart.appendChild(cellStart);
        lbxDFollowFrom.appendChild(itemStart);
        Map<String, Set<String>> dfFollowMap =  filterCriterionSelector.getDirectFollowMap();
        for(String key : dfFollowMap.keySet()) {
            Listcell listcell = new Listcell();
            listcell.setLabel(key);
            Listitem listitem = new Listitem();
            listitem.appendChild(listcell);
            lbxDFollowFrom.appendChild(listitem);
        }
        lbxDFollowFrom.setMultiple(true);
        lbxDFollowFrom.setCheckmark(true);
//        lhDFollowFrom.setSortAscending(new StringComparator(true, 0));

        lhDFollowFrom.sort(true);

        /**
         * Set direct follow relation: 'TO' values
         */
        Map<String, Set<String>> dtFollowMap =  filterCriterionSelector.getDirectFollowMap();
        Set<String> uniqueFollowMap = new HashSet<String>();
        for(String key : dtFollowMap.keySet()) {
            Set<String> fSet = dtFollowMap.get(key);
            for(String fKey : fSet) {
                if(!uniqueFollowMap.contains(fKey)) uniqueFollowMap.add(fKey);
            }
        }
        // Add [End] element to the list
        Listcell cellEnd = new Listcell("[End]");
        Listitem itemEnd = new Listitem();
        itemEnd.appendChild(cellEnd);
        lbxDFollowTo.appendChild(itemEnd);
        for(String key : uniqueFollowMap) {
            Listcell listcell = new Listcell(key);
            Listitem listitem = new Listitem();
            listitem.appendChild(listcell);
            lbxDFollowTo.appendChild(listitem);
        }
        lbxDFollowTo.setMultiple(true);
        lbxDFollowTo.setCheckmark(true);
//        lhDFollowTo.setSortAscending(new StringComparator(true, 0));

        lhDFollowTo.sort(true);

        /**
         * Set eventually follow relation: 'FROM' values
         */
        Map<String, Set<String>> efFollowMap =  filterCriterionSelector.getEventualFollowMap();
        for(String key : efFollowMap.keySet()) {
            Listcell listcell = new Listcell();
            listcell.setLabel(key);
            Listitem listitem = new Listitem();
            listitem.appendChild(listcell);
            lbxEFollowFrom.appendChild(listitem);
        }
        lbxEFollowFrom.setMultiple(true);
        lbxEFollowFrom.setCheckmark(true);
//        lhEFollowFrom.setSortAscending(new StringComparator(true, 0));
        lhEFollowFrom.sort(true);
        /**
         * Set eventually follow relation: 'TO' values
         */
        Map<String, Set<String>> etFollowMap =  filterCriterionSelector.getEventualFollowMap();
        Set<String> uniqueFollowMap2 = new HashSet<String>();
        for(String key : etFollowMap.keySet()) {
            Set<String> fSet = etFollowMap.get(key);
            for(String fKey : fSet) {
                if(!uniqueFollowMap2.contains(fKey)) uniqueFollowMap2.add(fKey);
            }
        }
        for(String key : uniqueFollowMap2) {
            Listcell listcell = new Listcell(key);
            Listitem listitem = new Listitem();
            listitem.appendChild(listcell);
            lbxEFollowTo.appendChild(listitem);
        }
        lbxEFollowTo.setMultiple(true);
        lbxEFollowTo.setCheckmark(true);
//        lhEFollowTo.setSortAscending(new StringComparator(true, 0));
        lhEFollowTo.sort(true);


    	// If an existing Filter Criterion is provided, set form field values to those in the Filter Criterion 
        if(pos != -1) {
            LogFilterCriterion criterion = criteria.get(pos);
//            level.setSelectedIndex(criterion.getLevel()== Level.EVENT ? 0 : 1);
            if(criterion.getLevel()== Level.EVENT) level.setSelectedItem(levelEvent);
            else level.setSelectedItem(levelTrace);
            containment.setSelectedIndex(criterion.getContainment() == Containment.CONTAIN_ANY ? 0 : 1);
            action.setSelectedIndex(criterion.getAction() == Action.RETAIN ? 0 : 1);
            
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
//            setValues(attribute_index);
            lbxFilterType.setSelectedIndex(attribute_index);
            setValues(lbxFilterType.getSelectedItem());
            lbxValue.setVisible(false);

            if (LogFilterTypeSelector.getType(a) == Type.TIME_TIMESTAMP) {
                Long start = null;
                Long end = null;
                for (String v : criterion.getValue()) {
                    if (v.startsWith(">")) start = Long.parseLong(v.substring(1));
                    if (v.startsWith("<")) end = Long.parseLong(v.substring(1));
                }

                dateBxStartDate.setValue(new Date(start));
                dateBxStartDate.setDisabled(false);
                dateBxEndDate.setValue(new Date(end));
                dateBxEndDate.setDisabled(false);
                gridTimeframe.setVisible(true);
                lbxValue.setVisible(false);
            }else if (LogFilterTypeSelector.getType(a) == Type.DURATION_RANGE) {
                gridDuration.setVisible(true);
                String d = null;
                String sTime = null;
                String eTime = null;
                String fromUnit = null;
                String toUnit = null;
                for(String v : criterion.getValue()) {
                    if(v.startsWith(">")) {
                        sTime = v.substring(1, v.indexOf(" "));
                        fromUnit = v.substring(v.indexOf(" ") + 1);
                    }
                    if(v.startsWith("<")) {
                        eTime = v.substring(1, v.indexOf(" "));
                        toUnit = v.substring(v.indexOf(" ") + 1);
                    }
                }
                dBxDurationFrom.setValue(sTime);
                dBxDurationTo.setValue(eTime);
                for(Listitem listitem : lbxDurationUnitsFrom.getItems()) {
                    if(listitem.getLabel().toLowerCase().equals(fromUnit.toLowerCase())) {
                        lbxDurationUnitsFrom.setSelectedItem(listitem);
                        break;
                    }
                }
                for(Listitem listitem : lbxDurationUnitsTo.getItems()) {
                    if(listitem.getLabel().toLowerCase().equals(toUnit.toLowerCase())) {
                        lbxDurationUnitsTo.setSelectedItem(listitem);
                        break;
                    }
                }
//            }else if (LogFilterTypeSelector.getType(a) == Type.TIME_DURATION) {
//                String d = null;
//                for (String v : criterion.getValue()) {
//                    if (v.startsWith(">")) d = v.substring(1);
//                }
//                String[] values = TimeConverter.parseDuration2(d);
//                duration.setValue(values[0]);
//                duration.setDisabled(false);
//                gridDuration.setVisible(true);
////                Listitem found = durationUnits.getItems().stream()
////                	.filter(item -> item.getLabel().equals(values[1]))
////                	.findAny()
////                	.orElse(null);
//                Listitem found = null;
//                for (Listitem item : durationUnits.getItems()) {
//                	if (item.getLabel().equals(values[1])) {
//                		found = item;
//                		break;
//                	}
//                }
//                if (found != null) durationUnits.setSelectedItem(found);
//                durationUnits.setDisabled(false);
            }else if (LogFilterTypeSelector.getType(a) == Type.DIRECT_FOLLOW) {
                // get the criterion values then break them down to FROM and TO
                Set<String> fromSet = new HashSet<String>();
                Set<String> toSet = new HashSet<String>();
                Set<String> valueSet = criterion.getValue();
                for(String composedValue : valueSet) {
                    int arrowIndex = composedValue.indexOf(" => ");
                    String fromValue = composedValue.substring(0, arrowIndex);
//                    if(fromValue.equals("|>")) fromValue = "[Start]";
                    String toValue = composedValue.substring(arrowIndex+4, composedValue.length());
//                    if(toValue.equals("[]")) toValue = "[End]";
                    fromSet.add(fromValue);
                    toSet.add(toValue);
                }
                for(Listitem listitem : lbxDFollowFrom.getItems()) {
                    if(fromSet.contains(listitem.getLabel())) listitem.setSelected(true);
                }
                for(Listitem listitem : lbxDFollowTo.getItems()) {
                    if(toSet.contains(listitem.getLabel())) listitem.setSelected(true);
                }
            }else if (LogFilterTypeSelector.getType(a) == Type.EVENTUAL_FOLLOW) {
                Set<String> fromSet = new HashSet<String>();
                Set<String> toSet = new HashSet<String>();
                Set<String> valueSet = criterion.getValue();
                for(String composedValue : valueSet) {
                    int arrowIndex = composedValue.indexOf(" => ");
                    String fromValue = composedValue.substring(0, arrowIndex);
                    String toValue = composedValue.substring(arrowIndex+4, composedValue.length());
                    fromSet.add(fromValue);
                    toSet.add(toValue);
                }
                for(Listitem listitem : lbxEFollowFrom.getItems()) {
                    if(fromSet.contains(listitem.getLabel())) listitem.setSelected(true);
                }
                for(Listitem listitem : lbxEFollowTo.getItems()) {
                    if(toSet.contains(listitem.getLabel())) listitem.setSelected(true);
                }
            }else {
                for(Listitem listitem : lbxValue.getItems()) {
                    if(criterion.getValue().contains(listitem.getLabel())) listitem.setSelected(true);
                }
                lbxValue.setVisible(true);
            }
        }
    }

    private void setStatus() {

        detailView.setVisible(false);

//        if(level.getSelectedIndex() == 0) { // Event Level
        if(level.getSelectedItem().equals(levelEvent)) {
            if(lbxFilterType.getSelectedIndex() >= 0) {
            	Type type = LogFilterTypeSelector.getType(filterTypeCodes.get(lbxFilterType.getSelectedIndex()));
                switch (type) {
	            	case TIME_TIMESTAMP:
	            		okButton.setDisabled(false);
	            		break;
	            	default:
	            		okButton.setDisabled(lbxValue.getItems().size() == 0);
                }
            }
            else {
            	okButton.setDisabled(true);
                lbxValue.setVisible(false);
            }
            
//            duration.setDisabled(true);
//            durationUnits.setDisabled(true);
            gridDuration.setVisible(false);
//            gridTimeframe.setVisible(false);

            for(Radio radio : containment.getItems()) {
                radio.setDisabled(true);
            }
            /**
             * 2019-09-16
             */
//            gridContainment.setVisible(false);
            for(Radio radio : containment.getItems()) {
                radio.setDisabled(true);
                radio.setStyle("color:#CCC");
            }

            containment.setStyle("background-color: #D3D3D3;");
        }else { //Trace Level
            okButton.setDisabled(false);
//            duration.setDisabled(true);
//            durationUnits.setDisabled(true);
            gridDuration.setVisible(false);
            gridTimeframe.setVisible(false);
        	
            if(lbxFilterType.getSelectedIndex() >= 0) {
            	boolean eventInvalid = !LogFilterTypeSelector.isValidCode(filterTypeCodes.get(lbxFilterType.getSelectedIndex()), Level.EVENT);
            	for (Radio radio : containment.getItems()) {
                    radio.setDisabled(eventInvalid);
                }
            	if(eventInvalid) {
//            	    gridContainment.setVisible(false);
                    for(Radio radio : containment.getItems()) {
                        radio.setDisabled(true);
                        radio.setStyle("color:#CCC");
                    }
                }else{
//            	    gridContainment.setVisible(true);
                    for(Radio radio : containment.getItems()) {
                        radio.setDisabled(false);
                        radio.setStyle("color:#333");
                    }
                }
                containment.setStyle(eventInvalid ? "background-color: #D3D3D3;" : "transparent;");
                
                Type type = LogFilterTypeSelector.getType(lbxFilterType.getSelectedItem().getValue());
                switch (type) {
                    case DIRECT_FOLLOW:
                        okButton.setDisabled(false);
                        break;
                    case EVENTUAL_FOLLOW:
                        okButton.setDisabled(false);
                        break;
                	case TIME_TIMESTAMP:
                		okButton.setDisabled(false);
                        gridTimeframe.setVisible(true);
                		break;
//                	case TIME_DURATION:
//                		duration.setDisabled(false);
//                        durationUnits.setDisabled(false);
                    case DURATION_RANGE:
                        gridDuration.setVisible(true);
                		okButton.setDisabled(false);
                        lbxValue.setVisible(false);
                		break;
                	default:
                        lbxValue.setVisible(true);
                		okButton.setDisabled(lbxValue.getItems().size() == 0);
                }
            }else {
                for (Radio radio : containment.getItems()) {
                    radio.setDisabled(false);
                }
//                gridContainment.setVisible(true);
                for(Radio radio : containment.getItems()) {
                    radio.setDisabled(false);
                    radio.setStyle("color:#333");
                }
            	okButton.setDisabled(true);
            }
        }
        
    }

    private void addEventListeners() {
        level.addEventListener("onCheck", new EventListener<Event>() {
            @Override
            public void onEvent(Event event) {

                hlDFollow.setVisible(false);
                hlEFollow.setVisible(false);

            	if (lbxFilterType.getSelectedIndex() >= 0) {
            		String currentLabel = lbxFilterType.getSelectedItem().getLabel();
                	String currentFilterType = filterTypeCodes.get(lbxFilterType.getSelectedIndex());
                	
                	filterTypeCodes = getValidFilterTypeCodes(allFilterTypeCodes, level);
                	filterTypeNames = getValidFilterTypeNames(level);
            		populateFilterTypes(); // filterType is updated
            		
	            	if (isLevelValid(currentFilterType, level)) {
	            		int selectedIndex = -1;
	            		for (Listitem item : lbxFilterType.getItems()) {
	            			if (item.getLabel().equals(currentLabel)) {
	            				selectedIndex = item.getIndex();
	            				break;
	            			}
	            		}
	            		if (selectedIndex >= 0) {
                            lbxFilterType.setSelectedIndex(selectedIndex);
	            		}
	            		else {
                            lbxValue.getItems().clear();
	            		}
	            	}
	            	else {
                        lbxValue.getItems().clear();
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

        lbxFilterType.addEventListener("onSelect", new EventListener<Event>() {
            @Override
            public void onEvent(Event event) {
            	FilterCriterionDialog.this.setValues(lbxFilterType.getSelectedItem());
                setStatus();
            }
        });

        okButton.addEventListener("onClick", new EventListener<Event>() {
            public void onEvent(Event event) throws Exception {
                Set<String> set = new HashSet<>();

//                String option = filterTypeCodes.get(lbxFilterType.getSelectedIndex());
                String option = lbxFilterType.getSelectedItem().getValue();
                //String option = filterTypeNames.get(filterType.getSelectedIndex());
                //if(LogFilterTypeSelector.getName(option) > -1) option = LogFilterTypeSelector.getReverseMatch(option);

                if(LogFilterTypeSelector.getType(option) == Type.TIME_TIMESTAMP) {
                    set.add(">" + dateBxStartDate.getValue().getTime());
                    set.add("<" + dateBxEndDate.getValue().getTime());
                } else if (LogFilterTypeSelector.getType(option) == Type.DURATION_RANGE) {
                    if(lbxDurationUnitsFrom.getSelectedItem() == null ||
                            dBxDurationFrom.getValue() == null ||
                            lbxDurationUnitsTo.getSelectedItem() == null ||
                            dBxDurationTo.getValue() == null) {
                        Messagebox.show("Please enter duration values.", "Error", Messagebox.OK, Messagebox.ERROR);
                    }else{
                        String spanFrom = lbxDurationUnitsFrom.getSelectedItem().getLabel();
                        long fromValue = dBxDurationFrom.getValue().longValue() * stringToMilli(spanFrom);

                        String spanTo = lbxDurationUnitsTo.getSelectedItem().getLabel();
                        long toValue = dBxDurationTo.getValue().longValue() * stringToMilli(spanTo);

                        if(fromValue > toValue) {
                            Messagebox.show("Please enter valid duration values.", "Error", Messagebox.OK, Messagebox.ERROR);
                        }else{
                            String fromValueString = dBxDurationFrom.getValue().doubleValue() + TimeConverter.DURATION_UNIT_MARKER + spanFrom;
                            String toValueString = dBxDurationTo.getValue().doubleValue() + TimeConverter.DURATION_UNIT_MARKER + spanTo;
                            set.add(">" + fromValueString);
                            set.add("<" + toValueString);
                        }
                    }
//                }else if(LogFilterTypeSelector.getType(option) == Type.TIME_DURATION) {
//                    String span = durationUnits.getSelectedItem().getLabel();
//                    //BigDecimal d = TimeConverter.convertMilliseconds(duration.getValue(), span);
//                    set.add(">" + duration.getValue().doubleValue() + TimeConverter.DURATION_UNIT_MARKER + span);
                } else {
//                    for (Listitem listItem : lbxValue.getSelectedItems()) {
//                        set.add(((Listcell) listItem.getFirstChild()).getLabel());
//                    }
                    if(LogFilterTypeSelector.getType(option) == Type.DIRECT_FOLLOW) {
                        for(Listitem liFrom : lbxDFollowFrom.getSelectedItems()) {
                            String lblFrom = ((Listcell) liFrom.getFirstChild()).getLabel();
//                            if(lblFrom.equals("[Start]")) lblFrom = "|>";
                            for(Listitem liTo : lbxDFollowTo.getSelectedItems()) {
                                String lblTo = ((Listcell) liTo.getFirstChild()).getLabel();
//                                if(lblTo.equals("[End]")) lblTo = "[]";
                                String composedString = lblFrom + " => " + lblTo;
                                set.add(composedString);
                            }
                        }
                    }else if(LogFilterTypeSelector.getType(option) == Type.EVENTUAL_FOLLOW) { // is Direct follow relation
                        for(Listitem liFrom : lbxEFollowFrom.getSelectedItems()) {
                            String lblFrom = ((Listcell) liFrom.getFirstChild()).getLabel();
                            for(Listitem liTo : lbxEFollowTo.getSelectedItems()) {
                                String lblTo = ((Listcell) liTo.getFirstChild()).getLabel();
                                String composedString = lblFrom + " => " + lblTo;
                                set.add(composedString);
                            }
                        }
                    }else{
                        for (Listitem listItem : lbxValue.getSelectedItems()) {
                            set.add(((Listcell) listItem.getFirstChild()).getLabel());
                        }
                    }
                }



                if (set.size() > 0) {
                    LogFilterCriterion criterion = logFilterCriterionFactory.getLogFilterCriterion(
                            action.getSelectedIndex() == 0 ? Action.RETAIN : Action.REMOVE,
                            containment.getSelectedIndex() == 0 ? Containment.CONTAIN_ANY : Containment.CONTAIN_ALL,
//                            level.getSelectedIndex() == 0 ? Level.EVENT : Level.TRACE,
                            getLevel(level),
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
//        btCloseWindow.addEventListener("onClick", new EventListener<Event>() {
//            public void onEvent(Event event) {
//                createFilterCriterionW.detach();
//            }
//        });
    }

    /**
     * 15.08: change input parameter
     * Set value or populate values to fields/list on the form
     * @param selectedItem: the index of the selected filter type in filterTypeCodes
     */
    private void setValues(Listitem selectedItem) {
//        String option = filterTypeNames.get(index); //option is the label of the selected filter type code
        String filterCode = selectedItem.getValue();

        /**
         * Change header label based on the option
         */
        if(LogFilterTypeSelector.getType(filterCode) == Type.CASE_VARIANT) {
            lbxValue.setVisible(true);
            lhValue1.setLabel("Case variant ID");
            lhValue2.setLabel("Cases");
            lhValue3.setLabel("Frequency");
            lbxValue.setMold("paging");
            lbxValue.setAutopaging(true);
        }else{
            lbxValue.setVisible(false);
            lhValue1.setLabel("Value");
            lhValue2.setLabel("Frequency");
            lhValue3.setLabel("Relative frequency");
            lbxValue.setMold("default");
            lbxValue.setAutopaging(false);
        }

        if (LogFilterTypeSelector.getType(filterCode) == Type.DIRECT_FOLLOW) {
            lbxValue.setVisible(false);
            hlDFollow.setVisible(true);
            hlEFollow.setVisible(false);
        }else if (LogFilterTypeSelector.getType(filterCode) == Type.EVENTUAL_FOLLOW) {
            lbxValue.setVisible(false);
            hlDFollow.setVisible(false);
            hlEFollow.setVisible(true);
        }else{
            lbxValue.setVisible(true);
            hlDFollow.setVisible(false);
            hlEFollow.setVisible(false);
        }

        if (LogFilterTypeSelector.getType(filterCode) == Type.TIME_TIMESTAMP) {
            //value.setModel(modelValue);
            lbxValue.getItems().clear();
            lbxValue.setVisible(false);
            dateBxStartDate.setDisabled(false);
            dateBxEndDate.setDisabled(false);
            gridTimeframe.setVisible(true);
//            duration.setDisabled(true);
//            durationUnits.setDisabled(true);
            gridDuration.setVisible(false);
        }else if (LogFilterTypeSelector.getType(filterCode) == Type.TIME_DURATION) {
            // value.setModel(modelValue);
            lbxValue.getItems().clear();
            lbxValue.setVisible(false);
            dateBxStartDate.setDisabled(true);
            dateBxEndDate.setDisabled(true);
            gridTimeframe.setVisible(false);
//            duration.setDisabled(false);
//            durationUnits.setDisabled(false);
            gridDuration.setVisible(true);
//        }else if(!option.toLowerCase().equals("direct follow relation") &&
//        !option.toLowerCase().equals("eventually follow relation")) {
        }else if(LogFilterTypeSelector.getType(filterCode) != Type.DIRECT_FOLLOW && LogFilterTypeSelector.getType(filterCode) != Type.EVENTUAL_FOLLOW) {
            dateBxStartDate.setDisabled(true);
            dateBxEndDate.setDisabled(true);
            gridTimeframe.setVisible(false);
//            duration.setDisabled(true);
//            durationUnits.setDisabled(true);
            gridDuration.setVisible(false);

            Collection<String> set;
//            String coded_option; // the filter type corresponding to option
//            if(LogFilterTypeSelector.isStandardName(option)) coded_option = LogFilterTypeSelector.getTypeFromName(option);
//            else coded_option = option;

            set = options_frequency.get(filterCode).keySet(); // list of values

            lbxValue.getItems().clear();

            double total = 0;
            for (String option_value : set) {
                total += options_frequency.get(filterCode).get(option_value); // calculate total frequency
            }

            for (String option_value : set) {
                Listcell listcell1 = new Listcell();
                if(option_value.matches("-?\\d+(\\.\\d+)?")) { // if its numeric
                    boolean hasDot = option_value.contains(".");
                    if(hasDot) { //is  double value
                        double doubleValue = Double.parseDouble(option_value);
                        listcell1.setValue(doubleValue);
                        listcell1.setLabel(doubleValue + "");
                    }else{ // is not double value
                        long numericOptionValue = Long.parseLong(option_value);
                        listcell1.setValue(numericOptionValue);
                        listcell1.setLabel(numericOptionValue + "");
                    }
                    lhValue1.setSortAscending(new NumberComparator(true, 0));
                    lhValue1.setSortDescending(new NumberComparator(false, 0));
                }else{
                    listcell1.setValue(option_value);
                    listcell1.setLabel(option_value);
                    /**
                     * To overwrite the NumberComparator
                     */
                    lhValue1.setSortAscending(new StringComparator(true, 0));
                    lhValue1.setSortDescending(new StringComparator(false, 0));
                    lhValue1.setSort("auto"); // reset the sorting method
                }
                Listcell listcell2 = new Listcell();
                listcell2.setValue(options_frequency.get(filterCode).get(option_value).toString());
                listcell2.setLabel(options_frequency.get(filterCode).get(option_value).toString());
                Listcell listcell3 = new Listcell();
                listcell3.setLabel(decimalFormat.format(100 * ((double) options_frequency.get(filterCode).get(option_value) / total)) + "%");
                listcell3.setValue(100 * ((double) options_frequency.get(filterCode).get(option_value) / total));

                Listitem listitem = new Listitem();
                listitem.appendChild(listcell1);
                listitem.appendChild(listcell2);
                listitem.appendChild(listcell3);

                listitem.addEventListener("onClick", new EventListener<Event>() {
                    public void onEvent(Event event) throws Exception {
//                        String option = filterTypeNames.get(lbxFilterType.getSelectedIndex());
                        String option = filterTypeCodes.get(lbxFilterType.getSelectedIndex());
                        if(option.toLowerCase().equals("case:variant")) {
                            if(lbxValue.getSelectedItems() != null && lbxValue.getSelectedItem()!= null) {
                                lbxItemDetail.getItems().clear();
                                String vIdString = listcell1.getLabel();
                                detailView.setVisible(true);
                                detailView.setTitle("Variant ID: " + vIdString);
                                List<String> actList = filterCriterionSelector.getVariantEventsMap().get(Integer.parseInt(vIdString));
                                for (int i = 0; i < actList.size(); i++) {
                                    Listcell listcell1 = new Listcell((i + 1) + "");
                                    Listcell listcell2 = new Listcell(actList.get(i));
                                    Listitem listitem = new Listitem();
                                    listitem.appendChild(listcell1);
                                    listitem.appendChild(listcell2);
                                    lbxItemDetail.appendChild(listitem);
                                }
                            }else{
                                detailView.setVisible(false);
                            }
                        }
                    }
                });


                lbxValue.appendChild(listitem);
                //modelValue.add(listitem);
            }

            lbxValue.setCheckmark(true);
            lbxValue.setMultiple(true);
            lbxValue.setVisible(true);

            //value.selectAll();
            //modelValue.setMultiple(true);



            lhValue1.sort(true, true);
        }
    }
    
    private Level getLevel(Radiogroup level) {
//    	return level.getSelectedIndex() == 0 ? Level.EVENT : Level.TRACE;
    	if(level.getSelectedItem().equals(levelEvent)) return Level.EVENT;
    	else return Level.TRACE;
    }
    
    private boolean isStandard(String filterType) {
    	return (LogFilterTypeSelector.getType(filterType) != Type.UNKNOWN);
    }
    
    private boolean isLevelValid(String filterType, Radiogroup level) {
//    	return LogFilterTypeSelector.isValidType(filterType, getLevel(level));
        return LogFilterTypeSelector.isValidCode(filterType, getLevel(level));
    }
    
    private List<String> getValidFilterTypeCodes(List<String> types, Radiogroup level) {
    	List<String> selection = new ArrayList<>();
    	for (String code : types) {
    		if (isLevelValid(code, level)) selection.add(code);
    	}

        /**
         * Re-order the filter type codes
         */
        Set<String> optionSet = new HashSet<String>(selection);
        for(int i=0; i<selection.size(); i++) {
            String t = selection.get(i);
            if(t.toLowerCase().equals("case:variant")) {
                selection.remove(i);
                selection.add(0, t);
                break;
            }
        }
        for(int i=0; i<selection.size(); i++) {
            String t = selection.get(i);
            if(t.toLowerCase().equals("time:timestamp")) {
                if(optionSet.contains("case:variant")) {
                    selection.remove(i);
                    selection.add(1, t);
                    break;
                }else{
                    selection.remove(i);
                    selection.add(0, t);
                    break;
                }
            }
        }
        for(int i=0; i<selection.size(); i++) {
            String t = selection.get(i);
            if(t.toLowerCase().equals("duration:range")) {
                for(int j=0; j < selection.size();j++) {
                    String jOption = selection.get(j);
                    if(jOption.toLowerCase().equals("time:timestamp")) {
                        selection.remove(i);
                        if(j < (selection.size()-1)) selection.add(j+1, t);
                        else selection.add(j, t);
                        break;
                    }
                }
            }
        }
//        for(int i=0; i<selection.size(); i++) {
//            String t = selection.get(i);
//            if(t.toLowerCase().equals("time:duration")) {
//                for(int j=0; j<selection.size();j++) {
//                    String jOption = selection.get(j);
//                    if(jOption.toLowerCase().equals("time:timestamp")) {
//                        selection.remove(i);
//                        selection.add(j+1, t);
//                        break;
//                    }
//                }
//            }
//        }
        for(int i=0; i<selection.size(); i++) {
            String t = selection.get(i);
            if(t.toLowerCase().equals("direct:follow")) {
                for(int j=0; j<selection.size();j++) {
                    String jOption = selection.get(j);
//                    if(optionSet.contains("time:duration")) {
//                        if(jOption.toLowerCase().equals("time:duration")) {
//                            selection.remove(i);
//                            selection.add(j+1, t);
//                            break;
//                        }
                    if(optionSet.contains("duration:range")) {
                        if(jOption.toLowerCase().equals("duration:range")) {
                            selection.remove(i);
                            if(j < (selection.size()-1)) selection.add(j+1, t);
                            else selection.add(j, t);
                            break;
                        }
                    }else{
                        if(jOption.toLowerCase().equals("time:timestamp")) {
                            selection.remove(i);
                            if(j < (selection.size()-1)) selection.add(j+1, t);
                            else selection.add(j, t);
                            break;
                        }
                    }
                }
            }
        }
        for(int i=0; i<selection.size(); i++) {
            String t = selection.get(i);
            if(t.toLowerCase().equals("eventually:follow")) {
                for(int j=0; j<selection.size();j++) {
                    String jOption = selection.get(j);
                    if(optionSet.contains("direct:follow")) {
                        if(jOption.toLowerCase().equals("direct:follow")) {
                            selection.remove(i);
                            if(j < (selection.size()-1)) selection.add(j+1, t);
                            else selection.add(j, t);
                            break;
                        }
//                    }else if(optionSet.contains("time:duration")) {
//                        if(jOption.toLowerCase().equals("time:duration")) {
//                            selection.remove(i);
//                            selection.add(j+1, t);
//                            break;
//                        }
                    }else if(optionSet.contains("duration:range")) {
                        if(jOption.toLowerCase().equals("duration:range")) {
                            selection.remove(i);
                            if(j < (selection.size()-1)) selection.add(j+1, t);
                            else selection.add(j, t);
                            break;
                        }
                    }else{
                        if(jOption.toLowerCase().equals("time:timestamp")) {
                            selection.remove(i);
                            if(j < (selection.size()-1)) selection.add(j+1, t);
                            else selection.add(j, t);
                            break;
                        }
                    }
                }
            }
        }

        for(int i=0; i<selection.size(); i++) {
            String t = selection.get(i);
            if(t.toLowerCase().equals("concept:name")) {
                for(int j=0; j<selection.size();j++) {
                    String jOption = selection.get(j);
                    if(optionSet.contains("eventually:follow")) {
                        if(jOption.toLowerCase().equals("eventually:follow")) {
                            selection.remove(i);
                            if(j < (selection.size()-1)) selection.add(j+1, t);
                            else selection.add(j, t);
                            break;
                        }
//                    }else if(optionSet.contains("time:duration")) {
//                        if(jOption.toLowerCase().equals("time:duration")) {
//                            selection.remove(i);
//                            selection.add(j+1, t);
//                            break;
//                        }
                    }else if(optionSet.contains("duration:range")) {
                        if(jOption.toLowerCase().equals("duration:range")) {
                            selection.remove(i);
                            if(j < (selection.size()-1)) selection.add(j+1, t);
                            else selection.add(j, t);
                            break;
                        }
                    }else{
                        if(jOption.toLowerCase().equals("time:timestamp")) {
                            selection.remove(i);
                            if(j < (selection.size()-1)) selection.add(j+1, t);
                            else selection.add(j, t);
                            break;
                        }
                    }
                }
            }
        }
        for(int i=0; i<selection.size(); i++) {
            String t = selection.get(i);
            if(t.toLowerCase().equals("org:resource")) {
                for(int j=0; j<selection.size();j++) {
                    String jOption = selection.get(j);
                    if(optionSet.contains("concept:name")) {
                        if(jOption.toLowerCase().equals("concept:name")) {
                            selection.remove(i);
                            if(j < (selection.size()-1)) selection.add(j+1, t);
                            else selection.add(j, t);
                            break;
                        }
                    }else{
                        if(jOption.toLowerCase().equals("time:timestamp")) {
                            selection.remove(i);
                            if(j < (selection.size()-1)) selection.add(j+1, t);
                            else selection.add(j, t);
                            break;
                        }
                    }
                }
            }
        }
        for(int i=0; i<selection.size(); i++) {
            String t = selection.get(i);
            if(t.toLowerCase().equals("org:group")) {
                for(int j=0; j<selection.size();j++) {
                    String jOption = selection.get(j);
                    if(optionSet.contains("org:resource")) {
                        if(jOption.toLowerCase().equals("org:resource")) {
                            selection.remove(i);
                            if(j < (selection.size()-1)) selection.add(j+1, t);
                            else selection.add(j, t);
                            break;
                        }
                    }else if(optionSet.contains("concept:name")) {
                        if(jOption.toLowerCase().equals("concept:name")) {
                            selection.remove(i);
                            if(j < (selection.size()-1)) selection.add(j+1, t);
                            else selection.add(j, t);
                            break;
                        }
                    }else{
                        if(jOption.toLowerCase().equals("time:timestamp")) {
                            selection.remove(i);
                            if(j < (selection.size()-1)) selection.add(j+1, t);
                            else selection.add(j, t);
                            break;
                        }
                    }
                }
            }
        }
        for(int i=0; i<selection.size(); i++) {
            String t = selection.get(i);
            if(t.toLowerCase().equals("lifecycle:transition")) {
                for(int j=0; j<selection.size();j++) {
                    String jOption = selection.get(j);
                    if(optionSet.contains("org:group")) {
                        if(jOption.toLowerCase().equals("org:group")) {
                            selection.remove(i);
                            if(j < (selection.size()-1)) selection.add(j+1, t);
                            else selection.add(j, t);
                            break;
                        }
                    }else if(optionSet.contains("org:resource")) {
                        if(jOption.toLowerCase().equals("org:resource")) {
                            selection.remove(i);
                            if(j < (selection.size()-1)) selection.add(j+1, t);
                            else selection.add(j, t);
                            break;
                        }
                    }else if(optionSet.contains("concept:name")) {
                        if(jOption.toLowerCase().equals("concept:name")) {
                            selection.remove(i);
                            if(j < (selection.size()-1)) selection.add(j+1, t);
                            else selection.add(j, t);
                            break;
                        }
                    }else{
                        if(jOption.toLowerCase().equals("time:timestamp")) {
                            selection.remove(i);
                            if(j < (selection.size()-1)) selection.add(j+1, t);
                            else selection.add(j, t);
                            break;
                        }
                    }
                }
            }
        }
//        List<String> recorderedList = new ArrayList<String>();
//        List<String> tempList = new ArrayList<String>();
//    	for(int i=0; i<selection.size(); i++) {
//    	    String t = selection.get(i);
//    	    if(t.toLowerCase().equals("case:variant")) {
//    	        tempList.add(0, t);
//            }else{
//    	        tempList.add(t);
//            }
//        }


    	return selection;
    }
    
    private String getFilterTypeName(String type) {
    	if(isStandard(type)) {
        	return LogFilterTypeSelector.getNameFromCode(type);
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
        	return LogFilterTypeSelector.getNameFromCode(type);
        }
        else { 
        	return "\"" + type + "\"";
        }
    }

    private long stringToMilli(String s) {
        if(s.equals("Years")) return new Long("31556952000");
        if(s.equals("Months")) return new Long("2628000000");
        if(s.equals("Weeks")) return 1000 * 60 * 60 * 24 * 7;
        if(s.equals("Days")) return 1000 * 60 * 60 * 24;
        if(s.equals("Hours")) return 1000 * 60 * 60;
        if(s.equals("Minutes")) return 1000 * 60;
        if(s.equals("Seconds")) return 1000;
        return 0;
    }
}
