/*-
 * #%L
 * This file is part of "Apromore Core".
 * %%
 * Copyright (C) 2018 - 2022 Apromore Pty Ltd.
 * %%
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 *
 * <p>This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Lesser Public License for more details.
 *
 * <p>You should have received a copy of the GNU General Lesser Public
 * License along with this program.  If not @see <a href="http://www.gnu.org/licenses/lgpl-3.0.html"></a>
 * #L%
 */

package org.apromore.processsimulation.config;

import lombok.Data;
import org.apromore.processsimulation.model.Currency;
import org.apromore.processsimulation.model.DistributionType;
import org.apromore.processsimulation.model.TimeUnit;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Data
@Configuration
@ConfigurationProperties(prefix = "process-simulation-info-export")
public class SimulationInfoConfig {

    private boolean enable;
    private String defaultTimeUnit = TimeUnit.SECONDS.toString();
    private String defaultDistributionType = DistributionType.EXPONENTIAL.toString();
    private String defaultCurrency = Currency.EUR.toString();
    private String defaultTimetableId = "DEFAULT_TIMETABLE";
    private String customTimetableId = "CUSTOM_TIMETABLE";
    private String defaultTimeslotName = "Default Timeslot";
    private String defaultResourceId = "DEFAULT_RESOURCE";
    private String defaultResourceIdPrefix = "QBP_";
    private String defaultResourceName = "Default";
    private long defaultMaxProcessInstances = 25000;
}
