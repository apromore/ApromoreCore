/*-
 * #%L
 * This file is part of "Apromore Enterprise Edition".
 * %%
 * Copyright (C) 2019 - 2022 Apromore Pty Ltd. All Rights Reserved.
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
import org.apromore.plugin.parquet.export.core.data.APMLogWrapper;
import org.deckfour.xes.model.XLog;

/**
 * @author Mohammad Ali
 */
public class APMLogWrapperManager {

    List<APMLogWrapper> apmLogComboList = new ArrayList<>();
    public boolean contains(String logName) {
        APMLogWrapper apmLogCombo = apmLogComboList.stream()
                .filter(x -> x.getName().equals(logName) || x.getSeriesName().equals(logName))
                .findFirst()
                .orElse(null);

        return apmLogCombo != null;
    }

    public void put(int id, String name, APMLog apmLog, XLog xLog, String color, String label) {
        apmLogComboList.add(new APMLogWrapper(id, name, apmLog, xLog, color, label));
    }

    public void add(APMLogWrapper apmLogCombo) {
        apmLogComboList.add(apmLogCombo);
    }
    public List<APMLogWrapper> getAPMLogComboList() {
        return apmLogComboList;
    }

    public List<APMLog> getAPMLogs() {
        return apmLogComboList.stream().map(APMLogWrapper::getAPMLog).collect(Collectors.toList());
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
                .map(APMLogWrapper::getXLog)
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
                .map(APMLogWrapper::getAPMLog)
                .findFirst()
                .orElse(null);
    }

    public APMLogWrapper getById(int logId) {
        for (APMLogWrapper combo : apmLogComboList) {
            if (combo.getId() == logId) {
                return combo;
            }
        }

        return null;
    }

    public APMLogWrapper get(int index) {
        if (index < apmLogComboList.size())
            return apmLogComboList.get(index);

        return null;
    }

    public APMLogWrapper get(String logName) {
        for (APMLogWrapper combo : apmLogComboList) {
            if (combo.getName().equals(logName)) {
                return combo;
            }
        }

        return null;
    }

}
