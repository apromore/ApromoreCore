/**
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

package org.apromore.processsimulation.config;

import java.util.Map;
import lombok.Data;
import lombok.ToString;
import org.apromore.processsimulation.model.Currency;
import org.apromore.processsimulation.model.DistributionType;
import org.apromore.processsimulation.model.TimeUnit;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Data
@ToString
@Configuration
@ConfigurationProperties(prefix = "process.simulation.info.export")
public class SimulationInfoConfig {
    public static final String CONFIG_DEFAULT_ID_KEY = "id";
    public static final String CONFIG_DEFAULT_NAME_KEY = "name";
    public static final String CONFIG_DEFAULT_TIMESLOT_NAME_KEY = "timeslot-name";
    public static final String CONFIG_DEFAULT_TIMESLOT_FROM_WEEKDAY_KEY = "timeslot-from-weekday";
    public static final String CONFIG_DEFAULT_TIMESLOT_TO_WEEKDAY_KEY = "timeslot-to-weekday";
    public static final String CONFIG_DEFAULT_TIMESLOT_FROM_TIME = "timeslot-from-time";
    public static final String CONFIG_DEFAULT_TIMESLOT_TO_TIME = "timeslot-to-time";

    private boolean enable;
    private String defaultTimeUnit = TimeUnit.SECONDS.toString();
    private String defaultDistributionType = DistributionType.EXPONENTIAL.toString();
    private String defaultCurrency = Currency.EUR.toString();
    private Map<String, String> defaultTimetable;
    private Map<String, String> defaultResource;
}
