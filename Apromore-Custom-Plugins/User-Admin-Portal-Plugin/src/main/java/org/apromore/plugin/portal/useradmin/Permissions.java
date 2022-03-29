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

package org.apromore.plugin.portal.useradmin;

/**
 * These permissions must be a subset of the ones in the database.
 */
public enum Permissions {
    VIEW_USERS("dff60714-1d61-4544-8884-0d8b852ba41e"),
    EDIT_USERS("2e884153-feb2-4842-b291-769370c86e44"),
    EDIT_GROUPS("d9ade57c-14c7-4e43-87e5-6a9127380b1b"),
    EDIT_ROLES("ea31a607-212f-447e-8c45-78f1e59b1dde");

    private final String rowGuid;

    Permissions(String newRowGuid) {
        this.rowGuid = newRowGuid;
    }

    public String getRowGuid() {
        return rowGuid;
    }
}
