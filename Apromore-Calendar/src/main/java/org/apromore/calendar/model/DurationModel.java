/*-
 * #%L
 * This file is part of "Apromore Core".
 * %%
 * Copyright (C) 2018 - 2021 Apromore Pty Ltd.
 * %%
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Lesser Public License for more details.
 * 
 * You should have received a copy of the GNU General Lesser Public
 * License along with this program.  If not, see
 * <http://www.gnu.org/licenses/lgpl-3.0.html>.
 * #L%
 */

/*
 * 
 * This is the DurationModel model, used as a container to retain the final result of the duration calculated from
 * {@link CalendarModel}.
 */

package org.apromore.calendar.model;

import lombok.Data;
import lombok.ToString;

import java.time.Duration;

@Data
@ToString
public class DurationModel {

  Duration duration = Duration.ZERO;
  Duration durationMin = Duration.ZERO;
  Duration durationMax = Duration.ZERO;
  Duration durationMean = Duration.ZERO;
  Duration durationMedian = Duration.ZERO;
  Duration durationSum = Duration.ZERO;

  public void setAll(Duration totalDuration) {
    duration =
        durationMin = durationMax = durationMean = durationMedian = durationSum = totalDuration;

  }


}
