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

package org.apromore.portal.model;

public final class RoleName {

    private RoleName() {
        throw new IllegalStateException("Utility class");
    }

    public static final String ROLE_ADMIN = "ROLE_ADMIN";
    public static final String ROLE_SUPERUSER = "ROLE_SUPERUSER";
    public static final String ROLE_MANAGER = "ROLE_MANAGER";
    public static final String ROLE_ANALYST = "ROLE_ANALYST";
    public static final String ROLE_VIEWER = "ROLE_VIEWER";
    public static final String ROLE_DESIGNER = "ROLE_DESIGNER";
    public static final String ROLE_DATA_ENGINEER = "ROLE_DATA_ENGINEER";
    public static final String ROLE_DATA_SCIENTIST = "ROLE_DATA_SCIENTIST";
    public static final String ROLE_INTEGRATOR = "ROLE_INTEGRATOR";
    public static final String ROLE_VIEWER_MODELS = "ROLE_VIEWER_MODELS";
}
