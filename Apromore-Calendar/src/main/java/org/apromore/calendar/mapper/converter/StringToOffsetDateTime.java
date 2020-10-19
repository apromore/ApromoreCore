package org.apromore.calendar.mapper.converter;

import java.time.OffsetDateTime;
import org.modelmapper.AbstractConverter;
import org.springframework.stereotype.Component;

@Component
public class StringToOffsetDateTime extends AbstractConverter<String,OffsetDateTime> {
  
  @Override
  protected OffsetDateTime convert(String source) {
    OffsetDateTime localDate = OffsetDateTime.parse(source);
    return localDate;
  }

}
