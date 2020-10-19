package org.apromore.calendar.mapper.converter;

import java.time.LocalDate;
import java.time.OffsetTime;
import org.modelmapper.AbstractConverter;
import org.springframework.stereotype.Component;

@Component
public class StringToLocalDate extends AbstractConverter<String,LocalDate> {
  
  @Override
  protected LocalDate convert(String source) {
    LocalDate localDate = LocalDate.parse(source);
    return localDate;
  }

}
