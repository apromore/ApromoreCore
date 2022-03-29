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
package org.apromore.apmlog.csv;

/**
 * Common CSV content labels should be managed in this class
 *
 * @since v8.0
 */
public class ContentLabels {

    private ContentLabels() {
        throw new IllegalStateException("Utility class");
    }

    public static final String CASE = "Case";
    public static final String EVENT = "Event";

    public static String getLabelWithComma(String contentLabel) {
        return contentLabel + ",";
    }

    public static String getFloatDurationOf(double duration) {
        return String.format("%.3f", duration);
    }
}
