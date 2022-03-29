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

package org.apromore.commons.datetime;

import java.time.format.DateTimeFormatter;
import java.util.Locale;

public interface Constants {

  public static final String DATE_TIME_FORMAT_DEFAULT = "dd.MM.yyyy HH:mm:ss";
  public static final String DATE_TIME_FORMAT_MS = "dd.MM.yyyy HH:mm:ss.SSS";
  public static final String DATE_TIME_FORMAT_HUMANIZED = "dd MMM yy, HH:mm";

  public static final DateTimeFormatter DATE_TIME_FORMATTER_HUMANIZED = DateTimeFormatter.ofPattern(DATE_TIME_FORMAT_HUMANIZED, Locale.ENGLISH);
}
