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
package org.apromore.model;

import org.apromore.portal.model.PermissionType;
import org.apromore.portal.model.UserType;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class UserTypeUnitTest {

    @Test
    void testHasPermissionNoPermission() {
        UserType user = new UserType();
        assertFalse(user.hasAnyPermission());
        assertFalse(user.hasAnyPermission(PermissionType.MODEL_VIEW));
    }

    @Test
    void testHasOnePermissionContainsPermission() {
        UserType user = new UserType();
        user.getPermissions().add(PermissionType.MODEL_VIEW);
        assertTrue(user.hasAnyPermission(PermissionType.MODEL_VIEW));
    }

    @Test
    void testHasAnyTwoPermissionContainsPermission() {
        UserType user = new UserType();
        user.getPermissions().add(PermissionType.MODEL_VIEW);
        assertTrue(user.hasAnyPermission(PermissionType.MODEL_VIEW, PermissionType.MODEL_EDIT));
    }
}
