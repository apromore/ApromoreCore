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
package org.apromore.plugin.parquet.export.core.data;


import java.io.Serializable;
import java.util.Map;
import org.apromore.apmlog.filter.PLog;

public class LogItem implements Serializable {

    private final String seriesName;
    private final PLog pLog;
    private String label;
    private String color;
    private final String sourceLogName;
    private final int sourceLogId;
    private final String sourceLogLabel;
    private final String seriesDesc;
    private final String originalLogLabel;
    private final String originalColor;
    private final Map<String, Integer> caseAttributeUniqueValues;
    private final Map<String, Integer> eventAttributeUniqueValues;

    public LogItem(APMLogCombo apmLogCombo)  {
        this.seriesName = apmLogCombo.getName();
        this.pLog = new PLog(apmLogCombo.getAPMLog());
        this.sourceLogName = apmLogCombo.getName();
        this.sourceLogId = apmLogCombo.getId();
        this.sourceLogLabel = apmLogCombo.getOriginalLabel();
        this.originalLogLabel = apmLogCombo.getOriginalLabel();
        this.originalColor = apmLogCombo.getColor();
        resetStyle();

        this.seriesDesc = label.equals(sourceLogLabel) ? sourceLogLabel : getComposeSeriesDesc();
        caseAttributeUniqueValues = APMLogData.getCaseAttributeUniqueValues(pLog);
        eventAttributeUniqueValues = APMLogData.getEventAttributeUniqueValues(pLog);
    }

    private String getComposeSeriesDesc() {
        return label + " (" + sourceLogLabel + ")";
    }
    public PLog getPLog() {
        return pLog;
    }
    public String getLabel() {
        return label;
    }
    public String getSourceLogName() {
        return sourceLogName;
    }
    public void setLabel(String label) {
        this.label = label;
    }
    public void resetStyle() {
        this.label = originalLogLabel;
        this.color = originalColor;
    }

}
