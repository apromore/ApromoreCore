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
        assertFalse(user.hasAnyPermission());
        assertFalse(user.hasAnyPermission(PermissionType.MODEL_VIEW));
    }

    @Test
    public void testHasOnePermissionContainsPermission() {
        UserType user = new UserType();
        user.getPermissions().add(PermissionType.MODEL_VIEW);
        assertTrue(user.hasAnyPermission(PermissionType.MODEL_VIEW));
    }

    @Test
    public void testHasAnyTwoPermissionContainsPermission() {
        UserType user = new UserType();
        user.getPermissions().add(PermissionType.MODEL_VIEW);
        assertTrue(user.hasAnyPermission(PermissionType.MODEL_VIEW, PermissionType.MODEL_EDIT));
    }
}
