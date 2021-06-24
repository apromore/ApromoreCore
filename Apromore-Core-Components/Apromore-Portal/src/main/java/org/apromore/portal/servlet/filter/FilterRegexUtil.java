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
package org.apromore.portal.servlet.filter;

import java.io.File;
import java.util.regex.Pattern;

public class FilterRegexUtil {

  static Pattern p = Pattern.compile("^/(zkau|zkau/.*|.*\\.css|.*\\.js"
      + "|.*\\.(svg|jpg|jpeg|png)|themes/.*|favicon\\.ico" + "|css/.*|" + ".*/js/.* |.*/fonts/.*)",
      Pattern.DOTALL);

  public static boolean isMatchingFilterRegex(String urlData) {
    return p.matcher(urlData).matches();
  }

  public static Pattern getPattern() {
    return p;
  }

  public static boolean useKeyCloak() {
    return new File(System.getProperty("karaf.etc"), "/config/" + "keycloak.json").exists();

  }

}
