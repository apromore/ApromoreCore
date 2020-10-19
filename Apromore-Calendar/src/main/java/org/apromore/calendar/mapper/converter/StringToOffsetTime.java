package org.apromore.calendar.mapper.converter;

import java.time.OffsetTime;
import org.modelmapper.AbstractConverter;
import org.springframework.stereotype.Component;

@Component
public class StringToOffsetTime extends AbstractConverter<String,OffsetTime> {
  
  @Override
  protected OffsetTime convert(String source) {
    OffsetTime localDate = OffsetTime.parse(source);
    return localDate;
  }

}
