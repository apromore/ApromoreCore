/*-
 * #%L
 * This file is part of "Apromore Core".
 * %%
 * Copyright (C) 2018 - 2020 Apromore Pty Ltd.
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
package org.apromore.commons.mapper;

import java.util.List;
import javax.annotation.PostConstruct;
import org.modelmapper.AbstractConverter;
import org.modelmapper.ModelMapper;
import org.modelmapper.config.Configuration.AccessLevel;
import org.modelmapper.convention.NamingConventions;

public class CustomMapper {

  List<AbstractConverter> converters;

  ModelMapper mapper;

  public CustomMapper(@SuppressWarnings("rawtypes") List<AbstractConverter> converters) {
    super();
    this.converters = converters;
  }

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
