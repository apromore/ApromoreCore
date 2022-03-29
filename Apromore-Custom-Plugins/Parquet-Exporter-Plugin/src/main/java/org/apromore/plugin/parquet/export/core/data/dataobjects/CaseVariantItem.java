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
package org.apromore.plugin.parquet.export.core.data.dataobjects;


import java.util.List;
import java.util.Map;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.ToString;
import org.apromore.apmlog.logobjects.ActivityInstance;

@Data
@ToString
@AllArgsConstructor
public class CaseVariantItem {
    private final int id;
    private long activities;
    private long cases;
    private long maxCases;
    private double minDuration;
    private double medianDuration;
    private double averageDuration;
    private double maxDuration;
    private List<Map.Entry<Integer, List<ActivityInstance>>> traceEntries;
}
