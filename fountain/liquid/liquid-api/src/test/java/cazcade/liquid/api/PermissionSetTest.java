/*
 * Copyright (c) 2009-2013 Cazcade Limited  - All Rights Reserved
 */

package cazcade.liquid.api;

import junit.framework.TestCase;

/**
 * @author neilelliz@cazcade.com
 */
public class PermissionSetTest extends TestCase {
    public void testCreateAndParse1() {
        final String permissionString = PermissionSet.getDefaultPermissions().toString();
        final PermissionSet permissionSet = PermissionSet.createPermissionSet(permissionString);
        System.out.println(permissionString);
        assertEquals(permissionString, permissionSet.toString());
        assertTrue("Default permission set should have owner admin.", permissionSet.hasPermission(PermissionScope.OWNER_SCOPE, Permission.SYSTEM_PERM));
        assertTrue("Default permission set should have adult view.", permissionSet.hasPermission(PermissionScope.ADULT_SCOPE, Permission.VIEW_PERM));
    }

    public void testCreateAndParse2() {
        final PermissionSet permissionSet = PermissionSet.createPermissionSet(PermissionSet.getWriteOnlyPermissionSet().toString());
        System.out.println(permissionSet.toString());
        assertFalse("Write only permission set should not have world view.", permissionSet.hasPermission(PermissionScope.WORLD_SCOPE, Permission.VIEW_PERM));
    }
}
