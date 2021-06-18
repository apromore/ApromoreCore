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

import java.util.Enumeration;
import javax.servlet.FilterConfig;
import javax.servlet.ServletContext;

public class ApromoreFilterConfig implements FilterConfig {

  ServletContext context;



  public ApromoreFilterConfig(ServletContext context) {
    this.context = context;
  }

  @Override
  public String getFilterName() {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public ServletContext getServletContext() {
    // TODO Auto-generated method stub
    return context;
  }

  @Override
  public String getInitParameter(String name) {
    switch (name) {
      case "keycloak.config.skipPattern":
        return null;


      default:
        break;
    }
    return null;
  }

  @Override
  public Enumeration<String> getInitParameterNames() {
    // TODO Auto-generated method stub
    return null;
  }

}
