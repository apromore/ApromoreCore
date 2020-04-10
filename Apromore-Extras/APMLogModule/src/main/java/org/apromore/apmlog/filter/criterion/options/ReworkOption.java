/*
 * This file is part of "Apromore".
 *
 * Copyright (C) 2019 - 2020 The University of Melbourne. All Rights Reserved.
 *
 */
package org.apromore.apmlog.filter.criterion.options;

import org.eclipse.collections.impl.map.mutable.UnifiedMap;
import org.eclipse.collections.impl.set.mutable.UnifiedSet;

/**
 * @author Chii Chang
 * Modified: 08/02/2020
 * Modified: 24/02/2020
 */
public class ReworkOption {

    enum BoundOption {
        HAS_OPTIONS, HAS_GREATER, HAS_GREATER_EQUAL, HAS_LESS, HAS_LESS_EQUAL
    }

    public UnifiedSet<String> selectedValues;
    public UnifiedMap<String, Integer> frequencyGreaterMap, frequencyGreaterEqualMap,
            frequencyLessMap, frequencyLessEqualMap;

    public UnifiedMap<String, UnifiedMap<BoundOption, Boolean>> optionMap = new UnifiedMap<>();

    public ReworkOption(UnifiedSet<String> criterionValues) {
        selectedValues = new UnifiedSet<>();
        frequencyGreaterMap = new UnifiedMap<>();
        frequencyGreaterEqualMap = new UnifiedMap<>();
        frequencyLessMap = new UnifiedMap<>();
        frequencyLessEqualMap = new UnifiedMap<>();
        for (String s : criterionValues) {
            String valueId = "";
            if (!s.contains("@")) valueId = s;
            else {
                valueId = s.substring(0, s.indexOf("@"));

                UnifiedMap<BoundOption, Boolean> boundOptMap = new UnifiedMap<>();
                boundOptMap.put(BoundOption.HAS_OPTIONS, true);
                boundOptMap.put(BoundOption.HAS_GREATER, false);
                boundOptMap.put(BoundOption.HAS_GREATER_EQUAL, false);
                boundOptMap.put(BoundOption.HAS_LESS, false);
                boundOptMap.put(BoundOption.HAS_LESS_EQUAL, false);

                if (s.contains("@>")) {
                    if (!s.contains("@>=")) boundOptMap.put(BoundOption.HAS_GREATER, true);
                    else boundOptMap.put(BoundOption.HAS_GREATER_EQUAL, true);
                }
                if (s.contains("@<")) {
                    if (!s.contains("@<=")) boundOptMap.put(BoundOption.HAS_LESS, true);
                    else boundOptMap.put(BoundOption.HAS_LESS_EQUAL, true);
                }

                optionMap.put(valueId, boundOptMap);

                String optionString = s.substring(s.indexOf("@"));
                setOptionValues(valueId, optionString);
            }
            selectedValues.add(valueId);
        }
    }

    private void setOptionValues(String valueId, String optionValueString) {
        String lowBoundVString = "1", upBoundVString = "1";

        if (optionMap.containsKey(valueId)) {
            UnifiedMap<BoundOption, Boolean> boundOptMap = optionMap.get(valueId);

            if (boundOptMap.get(BoundOption.HAS_GREATER_EQUAL)) {
                lowBoundVString = optionValueString.substring(optionValueString.indexOf("@>=") + 3);
                if (lowBoundVString.contains("@")) lowBoundVString = lowBoundVString.substring(0, lowBoundVString.indexOf("@"));
//                System.out.println(lowBoundVString);
            }

            if (boundOptMap.get(BoundOption.HAS_GREATER)) {
                lowBoundVString = optionValueString.substring(optionValueString.indexOf("@>") + 2);
                if (lowBoundVString.contains("@")) lowBoundVString = lowBoundVString.substring(0, lowBoundVString.indexOf("@"));
//                System.out.println(lowBoundVString);
            }

            if (boundOptMap.get(BoundOption.HAS_LESS)) {
                upBoundVString = optionValueString.substring(optionValueString.indexOf("@<") + 2);
                if (upBoundVString.contains("@")) upBoundVString = upBoundVString.substring(0, upBoundVString.indexOf("@"));

//                System.out.println(upBoundVString);
            }

            if (boundOptMap.get(BoundOption.HAS_LESS_EQUAL)) {
                upBoundVString = optionValueString.substring(optionValueString.indexOf("@<=") + 3);
                if (upBoundVString.contains("@")) upBoundVString = upBoundVString.substring(0, upBoundVString.indexOf("@"));

//                System.out.println(upBoundVString);
            }

            int lbValue = new Integer(lowBoundVString);
            int ubValue = new Integer(upBoundVString);

            if (boundOptMap.get(BoundOption.HAS_GREATER_EQUAL)) frequencyGreaterEqualMap.put(valueId, lbValue);
            if (boundOptMap.get(BoundOption.HAS_GREATER)) frequencyGreaterMap.put(valueId, lbValue);
            if (boundOptMap.get(BoundOption.HAS_LESS_EQUAL)) frequencyLessEqualMap.put(valueId, ubValue);
            if (boundOptMap.get(BoundOption.HAS_LESS)) frequencyLessMap.put(valueId, ubValue);
        }

//        System.out.println(lowBoundVString + ";" + upBoundVString);
    }
}
