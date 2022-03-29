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


import java.io.Serializable;
import org.apromore.apmlog.filter.PLog;

public class LogExportItem implements Serializable {

    private final PLog pLog;
    private String label;
    private final String sourceLogName;
    private final String originalLogLabel;

    public LogExportItem(APMLogWrapper apmLogWrapper)  {
        this.pLog = new PLog(apmLogWrapper.getAPMLog());
        this.sourceLogName = apmLogWrapper.getName();
        this.originalLogLabel = apmLogWrapper.getOriginalLabel();
        resetStyle();
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
    }

}
