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
 * Common CSV header labels should be managed in this class.
 * In order to compliant to Parquet, header labels should not contain space.
 * To be consistent to general database table column headers, use only lowercase for the headers.
 *
 * @since v8.0
 */
public class HeaderLabels {

    private HeaderLabels() {
        throw new IllegalStateException("Utility class");
    }

    public static final String HEADER = "header";

    public static final String TIMESTAMP = "timestamp";
    public static final String CASE_UTILIZATION = "case_utilization";
    public static final String CASE_VARIANT = "case_variant";
    public static final String AVERAGE_PROCESSING_TIME = "average_processing_time";
    public static final String MAX_PROCESSING_TIME = "max_processing_time";
    public static final String TOTAL_PROCESSING_TIME = "total_processing_time";
    public static final String AVERAGE_WAITING_TIME = "average_waiting_time";
    public static final String MAX_WAITING_TIME = "max_waiting_time";
    public static final String TOTAL_WAITING_TIME = "total_waiting_time";
    public static final String ACTIVITIES_PER_CASE = "activity_instance";
    public static final String DURATION = "duration";

    public static final String CASE_VARIANTS = "case_variants";
    public static final String UNIQUE_ACTIVITIES = "activities";
    public static final String UNIQUE_VALUES = "unique_values";
    public static final String SECTION = "section";
    public static final String KEY = "key";
    public static final String ATTRIBUTE = "attribute";
    public static final String FREQUENCY_STDV = "frequency_standard_deviation";

    public static final String MIN_DURATION = "min_duration";
    public static final String MEDIAN_DURATION = "median_duration";
    public static final String AVERAGE_DURATION = "average_duration";
    public static final String MAX_DURATION = "max_duration";

    public static final String MIN_FREQUENCY = "min_frequency";
    public static final String MEDIAN_FREQUENCY = "median_frequency";
    public static final String AVERAGE_FREQUENCY = "average_frequency";
    public static final String MAX_FREQUENCY = "max_frequency";

    public static final String CASE_ID = "case_id";
    public static final String ACTIVITY_INSTANCES = "activity_instances";
    public static final String CASES = "cases";
    public static final String FREQUENCY = "frequency";
    public static final String VALUE = "value";
    public static final String START_TIME = "start_time";
    public static final String END_TIME = "end_time";

    public static final String TITLE = "title";
    public static final String LOG_NAME = "log_name";
    public static final String SUB_VALUE = "sub_value";

    public static final String MIN_CASE_DURATION = "min_case_duration";
    public static final String MEDIAN_CASE_DURATION = "median_case_duration";
    public static final String AVERAGE_CASE_DURATION = "average_case_duration";
    public static final String MAX_CASE_DURATION = "max_case_duration";

    public static String getLabelWithComma(String headerLabel) {
        return headerLabel + ",";
    }
}
