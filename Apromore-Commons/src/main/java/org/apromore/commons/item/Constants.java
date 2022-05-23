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

package org.apromore.commons.item;

public interface Constants {

  // Original regex used in PD: "^[a-zA-Z0-9_\\(\\)\\-\\s\\.]{1,60}$"
  // Sync with ApromoreCore/Apromore-Core-Components/Apromore-Portal/src/main/webapp/WEB-INF/ui.properties
  public static final String VALID_NAME_REGEX = "^[a-zA-Z0-9, &\\u0080-\\uffff\\u005B\\u005D\\._\\+\\-\\(\\)%]{1,100}$";
  public static final String VALID_NAME_MESSAGE = "Use only letters, numbers, space and .+-_[]()&,%. No more than 100 chars.";
  public static final int VALID_NAME_MAX_LENGTH = 100;
  public static final String HOME_FOLDER_NAME = "Home";
}
