package cazcade.liquid.api;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;

import static cazcade.liquid.api.LiquidPermission.*;

/**
 * @author neilelliz@cazcade.com
 */
public class LiquidPermissionSet {

    private long permissions;
    private static final long BITS_PER_SCOPE = 6;
    @Nonnull
    private static final LiquidPermissionSet defaultPermissionSet;
    @Nonnull
    private static final LiquidPermissionSet minimalPermissionSet;
    private transient boolean readonly;
    @Nonnull
    private static final LiquidPermissionSet publicPermissions;
    @Nonnull
    private static final LiquidPermissionSet sharedPermissions;
    @Nonnull
    private static final LiquidPermissionSet writeOnlyPermissions;
    @Nonnull
    private static final LiquidPermissionSet privatePermissions;
    @Nonnull
    private static final LiquidPermissionSet childSafePermissions;


    @Nonnull
    private static final LiquidPermissionSet privateSharedPermissions;

    static {
        defaultPermissionSet = new LiquidPermissionSet();
        addAllPermissions(LiquidPermissionScope.OWNER, defaultPermissionSet);
        addAllPermissions(LiquidPermissionScope.ADULT, defaultPermissionSet);
        addAllPermissions(LiquidPermissionScope.TEEN, defaultPermissionSet);
        addModifyPermissions(LiquidPermissionScope.MEMBER, defaultPermissionSet);
        addReadPermissions(LiquidPermissionScope.FRIEND, defaultPermissionSet);
        addReadPermissions(LiquidPermissionScope.WORLD, defaultPermissionSet);
        addReadPermissions(LiquidPermissionScope.UNKNOWN, defaultPermissionSet);
        addReadPermissions(LiquidPermissionScope.VERIFIED, defaultPermissionSet);

        minimalPermissionSet = new LiquidPermissionSet();
        addAllPermissions(LiquidPermissionScope.OWNER, minimalPermissionSet);
        addReadPermissions(LiquidPermissionScope.ADULT, minimalPermissionSet);
        addReadPermissions(LiquidPermissionScope.TEEN, minimalPermissionSet);

        publicPermissions = LiquidPermissionSet.getDefaultPermissions();
        LiquidPermissionSet.addModifyPermissions(LiquidPermissionScope.VERIFIED, publicPermissions);
        LiquidPermissionSet.addModifyPermissions(LiquidPermissionScope.WORLD, publicPermissions);

        sharedPermissions = LiquidPermissionSet.getMinimalPermissionSet();
        LiquidPermissionSet.addModifyPermissions(LiquidPermissionScope.FRIEND, sharedPermissions);

        writeOnlyPermissions = LiquidPermissionSet.getMinimalPermissionSet();
        writeOnlyPermissions.addPermission(LiquidPermissionScope.VERIFIED, MODIFY);
        privatePermissions = LiquidPermissionSet.getMinimalPermissionSet();

        privateSharedPermissions = LiquidPermissionSet.getMinimalPermissionSet();
        childSafePermissions = LiquidPermissionSet.getDefaultPermissions();
        LiquidPermissionSet.addReadPermissions(LiquidPermissionScope.CHILD, childSafePermissions);

    }

    public static void removeAllPermissions(@Nonnull LiquidPermissionScope liquidPermissionScope, @Nonnull LiquidPermissionSet set) {
        set.removePermission(liquidPermissionScope, VIEW);
//        set.addPermission(liquidPermissionScope, LiquidPermission.EXECUTE);
        set.removePermission(liquidPermissionScope, MODIFY);
        set.removePermission(liquidPermissionScope, EDIT);
        set.removePermission(liquidPermissionScope, DELETE);
        set.removePermission(liquidPermissionScope, SYSTEM);
    }

    public static void addAllPermissions(@Nonnull LiquidPermissionScope liquidPermissionScope, @Nonnull LiquidPermissionSet set) {
        set.addPermission(liquidPermissionScope, VIEW);
//        set.addPermission(liquidPermissionScope, LiquidPermission.EXECUTE);
        set.addPermission(liquidPermissionScope, MODIFY);
        set.addPermission(liquidPermissionScope, EDIT);
        set.addPermission(liquidPermissionScope, DELETE);
        set.addPermission(liquidPermissionScope, SYSTEM);
    }

    public static void addEditPermissions(@Nonnull LiquidPermissionScope liquidPermissionScope, @Nonnull LiquidPermissionSet set) {
        set.addPermission(liquidPermissionScope, VIEW);
//        set.addPermission(liquidPermissionScope, LiquidPermission.EXECUTE);
        set.addPermission(liquidPermissionScope, MODIFY);
        set.addPermission(liquidPermissionScope, EDIT);
        set.addPermission(liquidPermissionScope, DELETE);
    }

    public static void addModifyPermissions(@Nonnull LiquidPermissionScope liquidPermissionScope, @Nonnull LiquidPermissionSet set) {
        set.addPermission(liquidPermissionScope, VIEW);
//        set.addPermission(liquidPermissionScope, LiquidPermission.EXECUTE);
        set.addPermission(liquidPermissionScope, MODIFY);
    }

    public static void addReadPermissions(@Nonnull LiquidPermissionScope liquidPermissionScope, @Nonnull LiquidPermissionSet set) {
        set.addPermission(liquidPermissionScope, VIEW);
//        set.addPermission(liquidPermissionScope, LiquidPermission.EXECUTE);
    }


    private LiquidPermissionSet() {
    }

    private LiquidPermissionSet(@Nonnull String permissions) {
        parse(permissions);
    }

    private LiquidPermissionSet(long permissions, boolean readonly) {
        this.permissions = permissions;
        this.readonly = readonly;
    }


    private void parse(@Nonnull String permissions) {
        String[] permissionGroups = permissions.split(",");
        for (String permissionGroup : permissionGroups) {
            LiquidPermissionScope permissionScope = LiquidPermissionScope.fromChar(permissionGroup.charAt(0));
            for (int i = 2; i < permissionGroup.length(); i++) {
                addPermission(permissionScope, fromChar(permissionGroup.charAt(i)));
            }

        }
    }

    private LiquidPermissionSet(@Nonnull String permissions, boolean readonly) {
        parse(permissions);
        this.readonly = readonly;
    }


    public void addPermission(@Nonnull LiquidPermissionScope scope, @Nonnull LiquidPermission permission) {
        checkNotReadOnly();
        permissions |= toPermissionBit(scope, permission);

    }

    private void checkNotReadOnly() {
        if (readonly) {
            throw new IllegalStateException("Cannot change the permissions on a read only LiquidPermissionSet");
        }
    }

    private long toPermissionBit(@Nonnull LiquidPermissionScope scope, @Nonnull LiquidPermission permission) {
        return (1L << permission.ordinal()) << (BITS_PER_SCOPE * scope.ordinal());
    }

    public void removePermission(@Nonnull LiquidPermissionScope scope, @Nonnull LiquidPermission permission) {
        checkNotReadOnly();

        permissions &= (~toPermissionBit(scope, permission));

    }

    public boolean hasPermission(@Nonnull LiquidPermissionScope scope, @Nonnull LiquidPermission permission) {

        return (permissions & toPermissionBit(scope, permission)) != 0;

    }

    public void addPermissions(@Nonnull LiquidPermissionScope scope, @Nonnull List<LiquidPermission> permissions) {
        checkNotReadOnly();
        for (LiquidPermission permission : permissions) {
            addPermission(scope, permission);
        }
    }


    public void addPermissions(@Nonnull LiquidPermissionScope scope, @Nonnull LiquidPermission... permissions) {
        checkNotReadOnly();
        for (LiquidPermission permission : permissions) {
            addPermission(scope, permission);
        }
    }


    public void removePermissions(@Nonnull LiquidPermissionScope scope, @Nonnull List<LiquidPermission> permissions) {
        checkNotReadOnly();
        for (LiquidPermission permission : permissions) {
            removePermission(scope, permission);
        }
    }

    public void removePermissions(@Nonnull LiquidPermissionScope scope, @Nonnull LiquidPermission... permissions) {
        checkNotReadOnly();
        for (LiquidPermission permission : permissions) {
            removePermission(scope, permission);
        }
    }

    public String toString() {
        return asFriendlyString();
    }

    @Nonnull
    public LiquidPermissionSet removeDeletePermission() {
        removePermission(LiquidPermissionScope.OWNER, DELETE);
        removePermission(LiquidPermissionScope.EDITOR, DELETE);
        removePermission(LiquidPermissionScope.UNKNOWN, DELETE);
        removePermission(LiquidPermissionScope.ADULT, DELETE);
        removePermission(LiquidPermissionScope.TEEN, DELETE);
        removePermission(LiquidPermissionScope.MEMBER, DELETE);
        removePermission(LiquidPermissionScope.FRIEND, DELETE);
        removePermission(LiquidPermissionScope.WORLD, DELETE);
        removePermission(LiquidPermissionScope.VERIFIED, DELETE);
        return this;
    }

    @Nonnull
    public static LiquidPermissionSet getDefaultPermissions() {
        return defaultPermissionSet.copy();
    }

    @Nonnull
    public static LiquidPermissionSet getEmptyPermissionSet() {
        return new LiquidPermissionSet();
    }

    @Nonnull
    public static LiquidPermissionSet createPermissionSet(@Nullable String permissions) {
        if (permissions == null) {
            return defaultPermissionSet.copy();
        } else {
            return new LiquidPermissionSet(permissions);
        }
    }


    @Nonnull
    public LiquidPermissionSet readOnlyCopy() {
        return new LiquidPermissionSet(permissions, true);
    }

    @Nonnull
    public LiquidPermissionSet copy() {
        return new LiquidPermissionSet(permissions, readonly);
    }

    public String asFriendlyString() {
        final StringBuilder sb = new StringBuilder();
        for (LiquidPermissionScope liquidPermissionScope : LiquidPermissionScope.values()) {
            sb.append(Character.toLowerCase(liquidPermissionScope.asShortForm()));
            sb.append('=');
            if (hasPermission(liquidPermissionScope, VIEW)) {
                sb.append(VIEW.asShortForm());
            }
            if (hasPermission(liquidPermissionScope, MODIFY)) {
                sb.append(MODIFY.asShortForm());
            }
            if (hasPermission(liquidPermissionScope, EDIT)) {
                sb.append(EDIT.asShortForm());
            }
//            if (hasPermission(liquidPermissionScope, LiquidPermission.EXECUTE)) {
//                sb.append(LiquidPermission.EXECUTE.asShortForm());
//            }
            if (hasPermission(liquidPermissionScope, DELETE)) {
                sb.append(DELETE.asShortForm());
            }
            if (hasPermission(liquidPermissionScope, SYSTEM)) {
                sb.append(SYSTEM.asShortForm());
            }
            sb.append(',');
        }
        sb.deleteCharAt(sb.length() - 1);
        return sb.toString();

    }

    @Nonnull
    public static LiquidPermissionSet getMinimalPermissionSet() {
        return minimalPermissionSet.copy();
    }

    @Nonnull
    public static LiquidPermissionSet getPublicPermissionSet() {
        return publicPermissions.copy();
    }

    @Nonnull
    public static LiquidPermissionSet getPrivatePermissionSet() {
        return privatePermissions.copy();
    }

    @Nonnull
    public static LiquidPermissionSet getWriteOnlyPermissionSet() {
        return writeOnlyPermissions.copy();
    }

    @Nonnull
    public static LiquidPermissionSet getSharedPermissionSet() {
        return sharedPermissions.copy();
    }

    @Nonnull
    public static LiquidPermissionSet getPrivateSharedPermissionSet() {
        return privateSharedPermissions.copy();
    }

    @Nonnull
    public static LiquidPermissionSet getChildSafePermissionSet() {
        return childSafePermissions.copy();
    }

    @Nonnull
    public static LiquidPermissionSet getMinimalNoDeletePermissionSet() {
        return getMinimalPermissionSet().removeDeletePermission();
    }

    @Nonnull
    public static LiquidPermissionSet getPrivateNoDeletePermissionSet() {
        return getPrivatePermissionSet().removeDeletePermission();
    }

    @Nonnull
    public static LiquidPermissionSet getWriteOnlyNoDeletePermissionSet() {
        return getWriteOnlyPermissionSet().removeDeletePermission();
    }

    @Nonnull
    public static LiquidPermissionSet getSharedNoDeletePermissionSet() {
        return getSharedPermissionSet().removeDeletePermission();
    }

    @Nonnull
    public static LiquidPermissionSet getPrivateSharedNoDeletePermissionSet() {
        return getPrivateSharedPermissionSet().removeDeletePermission();
    }

    @Nonnull
    public static LiquidPermissionSet getChildSafeNoDeletePermissionSet() {
        return getChildSafePermissionSet().removeDeletePermission();
    }

    @Nonnull
    public static LiquidPermissionSet getPublicNoDeletePermissionSet() {
        return getPublicPermissionSet().removeDeletePermission();
    }


    @Nonnull
    public static LiquidPermissionSet getDefaultPermissionsNoDelete() {
        return defaultPermissionSet.copy().removeDeletePermission();
    }

    @Nonnull
    public LiquidPermissionSet restoreDeletePermission() {
        if (hasPermission(LiquidPermissionScope.OWNER, EDIT)) {
            addPermission(LiquidPermissionScope.OWNER, DELETE);
        }
        if (hasPermission(LiquidPermissionScope.ADULT, EDIT)) {
            addPermission(LiquidPermissionScope.ADULT, DELETE);
        }
        if (hasPermission(LiquidPermissionScope.TEEN, EDIT)) {
            addPermission(LiquidPermissionScope.TEEN, DELETE);
        }
        if (hasPermission(LiquidPermissionScope.MEMBER, EDIT)) {
            addPermission(LiquidPermissionScope.MEMBER, DELETE);
        }
        if (hasPermission(LiquidPermissionScope.FRIEND, EDIT)) {
            addPermission(LiquidPermissionScope.FRIEND, DELETE);
        }
        if (hasPermission(LiquidPermissionScope.WORLD, EDIT)) {
            addPermission(LiquidPermissionScope.WORLD, DELETE);
        }
        if (hasPermission(LiquidPermissionScope.EDITOR, EDIT)) {
            addPermission(LiquidPermissionScope.EDITOR, DELETE);
        }
        if (hasPermission(LiquidPermissionScope.UNKNOWN, EDIT)) {
            addPermission(LiquidPermissionScope.UNKNOWN, DELETE);
        }
        if (hasPermission(LiquidPermissionScope.VERIFIED, EDIT)) {
            addPermission(LiquidPermissionScope.VERIFIED, DELETE);
        }
        return this;
    }

    @Nonnull
    public LiquidPermissionSet convertChangeRequestIntoPermissionSet(final LiquidPermissionChangeType change) {
        if (change == LiquidPermissionChangeType.MAKE_PRIVATE) {
            return getPrivatePermissionSet();
        } else if (change == LiquidPermissionChangeType.MAKE_PUBLIC) {
            return getPublicPermissionSet();
        } else if (change == LiquidPermissionChangeType.MAKE_PUBLIC_READONLY) {
            return getDefaultPermissions();
        } else {
            return this;
        }
    }
}