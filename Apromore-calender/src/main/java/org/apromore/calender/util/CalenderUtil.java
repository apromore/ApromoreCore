package org.apromore.calender.util;

import java.time.DayOfWeek;
import java.util.function.Predicate;

public class CalenderUtil {

  public static Predicate<DayOfWeek> getWeekendOffPRedicate(boolean weekendsOff) {
    Predicate<DayOfWeek> isWeekendOff = (DayOfWeek dayOfWeek) -> {
      return (weekendsOff
          && (dayOfWeek.equals(DayOfWeek.SATURDAY) ||
              dayOfWeek.equals(DayOfWeek.SUNDAY)));
    };
    return isWeekendOff;
  }
  
}
