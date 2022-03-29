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

import java.util.Arrays;
import java.util.List;

public class FileNameGenerator {

    public static String getName(String logName, String dataName) {
        return getName(Arrays.asList(logName), dataName);
    }

    public static String getName(List<String> logNames, String dataName) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < logNames.size(); i++) {
            sb.append(logNames.get(i));
            if (i < logNames.size() - 1)
                sb.append(", ");
        }
        return String.format("%s - %s data", sb, getValidDataName(dataName));
    }

    private static String getValidDataName(String dataName) {
        // ================================================================
        // ":" is not allowed in the filenames of Microsoft Windows OS
        // ================================================================
        return dataName.replaceAll(":", "");
    }
}