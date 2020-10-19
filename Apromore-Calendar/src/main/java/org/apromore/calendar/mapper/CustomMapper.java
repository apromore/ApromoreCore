package org.apromore.calendar.mapper;

import java.util.List;
import javax.annotation.PostConstruct;
import org.modelmapper.AbstractConverter;
import org.modelmapper.ModelMapper;
import org.modelmapper.config.Configuration.AccessLevel;
import org.modelmapper.convention.NamingConventions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class CustomMapper {


  List<AbstractConverter> converters;

  ModelMapper mapper;

  @Autowired
  public CustomMapper(@SuppressWarnings("rawtypes") List<AbstractConverter> converters) {
    super();
    this.converters = converters;
  }

  @PostConstruct
  public void init() {
    mapper = new ModelMapper();


    mapper.getConfiguration()
        .setFieldMatchingEnabled(true)
        .setFieldAccessLevel(AccessLevel.PRIVATE)
        .setSourceNamingConvention(NamingConventions.JAVABEANS_MUTATOR);

    for (AbstractConverter<?, ?> converter : converters) {
      mapper.addConverter(converter);
    }
  }

  public ModelMapper getMapper() {
    return mapper;
  }



}
