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
package org.apromore.security.filter;

import java.security.Principal;

public class SecurityPrincipal implements Principal, java.io.Serializable {

	private String name;

    public SecurityPrincipal(final String name) {
        if (name == null) {
            throw new IllegalArgumentException("parameter 'name' has an illegal null input");
        }

        this.name = name;
    }

    public String getName() {
        return name;
    }

    public String toString() {
        return("SecurityPrincipal:  " + name);
    }

    public boolean equals(final Object o) {
        if (o == null)
            return false;

        if (this == o)
            return true;

        if (!(o instanceof SecurityPrincipal))
            return false;
        SecurityPrincipal that = (SecurityPrincipal)o;

        if (this.getName().equals(that.getName()))
            return true;
        return false;
    }

    public int hashCode() {
        return name.hashCode();
    }
}
