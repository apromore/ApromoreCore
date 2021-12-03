package org.apromore.model;

import org.apromore.portal.model.PermissionType;
import org.apromore.portal.model.UserType;
import org.junit.Test;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class UserTypeUnitTest {

    @Test
    public void testHasPermissionNoPermission() {
        UserType user = new UserType();
        assertFalse(user.hasAnyPermission("PERMISSION_CONTAINED"));
    }

    @Test
    public void testHasOnePermissionContainsPermission() {
        PermissionType permission = new PermissionType();
        permission.setName("PERMISSION_CONTAINED");

        UserType user = new UserType();
        user.getPermissions().add(permission);
        assertTrue(user.hasAnyPermission("PERMISSION_CONTAINED"));
    }

    @Test
    public void testHasAnyTwoPermissionContainsPermission() {
        PermissionType permission = new PermissionType();
        permission.setName("PERMISSION_CONTAINED");

        UserType user = new UserType();
        user.getPermissions().add(permission);
        assertTrue(user.hasAnyPermission("PERMISSION_CONTAINED", "ANOTHER_PERMISSION"));
    }
}
