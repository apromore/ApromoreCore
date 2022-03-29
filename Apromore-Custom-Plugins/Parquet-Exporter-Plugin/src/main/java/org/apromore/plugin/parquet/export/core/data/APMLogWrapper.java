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
package org.apromore.plugin.parquet.export.core.data;

import org.apromore.apmlog.APMLog;
import org.apromore.apmlog.exceptions.EmptyInputException;
import org.apromore.plugin.parquet.export.util.SeriesIdGenerator;
import org.deckfour.xes.model.XLog;

/**
 * @author Mohammad Ali
 */
public class APMLogWrapper {
    private int id;
    private String name;
    private APMLog apmLog;
    private XLog xLog;
    private String color;
    private String label;
    private String originalLabel;
    private String originalColor;

    public APMLogWrapper(int id, String name, APMLog apmLog, XLog xLog, String color, String label) {
        this.id = id;
        this.apmLog = apmLog;
        this.xLog = xLog;
        this.color = color;

        // ==========================================================================================
        // Unifying the log identification
        // All the log-names must be generated from SeriesIdGenerator
        // which will be applicable for ZK-Chart series ID (preventing special symbols and space etc.
        // ==========================================================================================
        this.name = SeriesIdGenerator.getSeriesId(name);
        this.label = label;
        this.originalLabel = label;
        this.originalColor = color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public void setApmLog(APMLog apmLog) {
        this.apmLog = apmLog;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getSeriesName() {
        return name;
    }

    public APMLog getAPMLog() {
        return apmLog;
    }

    public XLog getXLog() {
        return xLog;
    }

    public String getColor() {
        return color;
    }

    public String getLabel() {
        return label != null ? label : name != null ? name : id + "";
    }

    public String getOriginalLabel() {
        return originalLabel;
    }

    public String getOriginalColor() {
        return originalColor;
    }

    public void reset() {
        this.label = originalLabel;
        this.color = originalColor;
    }

    public APMLogWrapper clone(int logId, String color) throws EmptyInputException {
        APMLog clonedAPMLog = this.apmLog.deepClone();
        return new APMLogWrapper(logId, name + "", clonedAPMLog, this.xLog, color, label);
    }
}
