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

package org.apromore.commons.mapper.converter;

import com.github.dozermapper.core.DozerConverter;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

public class StringToLocalTime extends DozerConverter<String, LocalTime> {

    public StringToLocalTime() {
        super(String.class, LocalTime.class);
    }

    @Override
    public LocalTime convertTo(String source, LocalTime destination) {
        if (source.contains("+")) {
            return LocalTime.parse(source.substring(0, source.indexOf("+")));
        } else if (source.contains("Z")) {
            return LocalTime.parse(source, DateTimeFormatter.ISO_TIME);
        } else {
            return LocalTime.parse(source);
        }
    }

    @Override
    public String convertFrom(LocalTime source, String destination) {
        return source.toString();
    }
}
