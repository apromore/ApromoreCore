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
package org.apromore.commons.mapper;

import java.util.List;

import org.apromore.commons.mapper.converter.DurationConvertor;
import org.apromore.commons.mapper.converter.StringToLocalDate;
import org.apromore.commons.mapper.converter.StringToOffsetDateTime;
import org.apromore.commons.mapper.converter.StringToOffsetTime;

import com.github.dozermapper.core.DozerBeanMapperBuilder;
import com.github.dozermapper.core.Mapper;

public class CustomMapper {

    Mapper mapper;
    List<String> mappingFiles;

    public CustomMapper() {
        super();
    }

    public CustomMapper(List<String> mappingBuilder) {
        this.mappingFiles = mappingBuilder;
    }

    public void init() {
        mapper = DozerBeanMapperBuilder.create()
                .withMappingFiles(mappingFiles)
                .withCustomConverterWithId("stringToOffsetDateTime", new StringToOffsetDateTime())
                .withCustomConverterWithId("stringToLocalDate", new StringToLocalDate())
                .withCustomConverterWithId("stringToOffsetTime", new StringToOffsetTime())
                .withCustomConverterWithId("durationToduration", new DurationConvertor())
                .build();
    }

    public Mapper getMapper() {
        return mapper;
    }

}
