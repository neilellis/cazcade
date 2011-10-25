package cazcade.liquid.api;

import junit.framework.TestCase;

/**
 * @author neilelliz@cazcade.com
 */
public class LiquidPermissionSetTest extends TestCase{

    public void testCreateAndParse1() {
        String permissionString = LiquidPermissionSet.getDefaultPermissions().toString();
        LiquidPermissionSet liquidPermissionSet = LiquidPermissionSet.createPermissionSet(permissionString);
        System.out.println(permissionString);
        assertEquals(permissionString, liquidPermissionSet.toString());
        assertTrue("Default permission set should have owner admin.",liquidPermissionSet.hasPermission(LiquidPermissionScope.OWNER, LiquidPermission.SYSTEM));
        assertTrue("Default permission set should have adult view.",liquidPermissionSet.hasPermission(LiquidPermissionScope.ADULT, LiquidPermission.VIEW));
    }

    public void testCreateAndParse2() {
        LiquidPermissionSet liquidPermissionSet = LiquidPermissionSet.createPermissionSet(LiquidPermissionSet.getWriteOnlyPermissionSet().toString());
        System.out.println(liquidPermissionSet.toString());
        assertFalse("Write only permission set should not have world view.",liquidPermissionSet.hasPermission(LiquidPermissionScope.WORLD, LiquidPermission.VIEW));
    }
}
