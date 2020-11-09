package org.apromore.calendar.model;

import java.time.Duration;
import java.time.temporal.ChronoUnit;
import lombok.Data;
import lombok.ToString;

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
