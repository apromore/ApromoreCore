/*-
 * #%L
 * This file is part of "Apromore Enterprise Edition".
 * %%
 * Copyright (C) 2019 - 2021 Apromore Pty Ltd. All Rights Reserved.
 * %%
 * NOTICE:  All information contained herein is, and remains the
 * property of Apromore Pty Ltd and its suppliers, if any.
 * The intellectual and technical concepts contained herein are
 * proprietary to Apromore Pty Ltd and its suppliers and may
 * be covered by U.S. and Foreign Patents, patents in process,
 * and are protected by trade secret or copyright law.
 * Dissemination of this information or reproduction of this
 * material is strictly forbidden unless prior written permission
 * is obtained from Apromore Pty Ltd.
 * #L%
 */
package org.apromore.plugin.parquet.export.service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import org.apromore.apmlog.APMLog;
import org.apromore.plugin.parquet.export.core.data.APMLogCombo;
import org.deckfour.xes.model.XLog;

/**
 * @author Chii Chang
 */
public class APMLogComboManager {

    List<APMLogCombo> apmLogComboList = new ArrayList<>();
    public boolean contains(String logName) {
        APMLogCombo apmLogCombo = apmLogComboList.stream()
                .filter(x -> x.getName().equals(logName) || x.getSeriesName().equals(logName))
                .findFirst()
                .orElse(null);

        return apmLogCombo != null;
    }

    public void put(int id, String name, APMLog apmLog, XLog xLog, String color, String label) {
        if(!contains(name)) {
            apmLogComboList.add(new APMLogCombo(id, name, apmLog, xLog, color, label));
        }else{
            int nameIndex = checkNameIndex(name);
            apmLogComboList.set(nameIndex, new APMLogCombo(id, name, apmLog, xLog, color, label));
        }
    }

    public void add(APMLogCombo apmLogCombo) {
        apmLogComboList.add(apmLogCombo);
    }

     private int checkNameIndex(String logName) {
        for(int i = 0; i< apmLogComboList.size(); i++) {
            String name = apmLogComboList.get(i).getName();
            if(name.equals(logName)) return i;
        }
        return apmLogComboList.size();
    }


    public List<APMLogCombo> getAPMLogComboList() {
        return apmLogComboList;
    }

    public List<APMLog> getAPMLogs() {
        return apmLogComboList.stream().map(APMLogCombo::getAPMLog).collect(Collectors.toList());
    }

    public int size() {
        return apmLogComboList.size();
    }

    public XLog getXLog(int index) {
        return apmLogComboList.get(index).getXLog();
    }

    public XLog getXLogByLogName(String logName) {
        return apmLogComboList.stream()
                .filter(x -> x.getName().equals(logName))
                .map(APMLogCombo::getXLog)
                .findFirst()
                .orElse(null);
    }

    public int getId(int index) {
        return apmLogComboList.get(index).getId();
    }

    public APMLog getAPMLog(int index) {
        return apmLogComboList.get(index).getAPMLog();
    }

    public APMLog getAPMLogByName(String logName) {
        return apmLogComboList.stream()
                .filter(x -> x.getName().equals(logName))
                .map(APMLogCombo::getAPMLog)
                .findFirst()
                .orElse(null);
    }

    public APMLogCombo getById(int logId) {
        for (APMLogCombo combo : apmLogComboList) {
            if (combo.getId() == logId) {
                return combo;
            }
        }

        return null;
    }

    public APMLogCombo get(int index) {
        if (index < apmLogComboList.size())
            return apmLogComboList.get(index);

        return null;
    }

    public APMLogCombo get(String logName) {
        for (APMLogCombo combo : apmLogComboList) {
            if (combo.getName().equals(logName)) {
                return combo;
            }
        }

        return null;
    }

}
