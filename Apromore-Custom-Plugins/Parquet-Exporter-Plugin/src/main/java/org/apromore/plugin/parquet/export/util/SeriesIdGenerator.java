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
package org.apromore.plugin.parquet.export.util;

public class SeriesIdGenerator {

    public static String getSeriesId(String logName) {
        logName = logName.replaceAll("'", "");
        logName = logName.replaceAll("\"", "");
        logName = logName.replaceAll("[^a-zA-Z0-9]", "");
        if (!logName.isEmpty() && Util.isNumeric(String.valueOf(logName.charAt(0)))) {
            logName = "Log" + logName;
        }
        return logName;
    }
}
