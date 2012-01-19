package cazcade.liquid.api;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;

import static cazcade.liquid.api.LiquidPermission.*;

/**
 * @author neilelliz@cazcade.com
 */
public class LiquidPermissionSet {
    private static final long BITS_PER_SCOPE = 6;
    @Nonnull
    private static final LiquidPermissionSet defaultPermissionSet;
    @Nonnull
    private static final LiquidPermissionSet minimalPermissionSet;
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

    private long permissions;
    private transient boolean readonly;

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

    public static void removeAllPermissions(@Nonnull final LiquidPermissionScope liquidPermissionScope,
                                            @Nonnull final LiquidPermissionSet set) {
        set.removePermission(liquidPermissionScope, VIEW);
//        set.addPermission(liquidPermissionScope, LiquidPermission.EXECUTE);
        set.removePermission(liquidPermissionScope, MODIFY);
        set.removePermission(liquidPermissionScope, EDIT);
        set.removePermission(liquidPermissionScope, DELETE);
        set.removePermission(liquidPermissionScope, SYSTEM);
    }

    public void removePermission(@Nonnull final LiquidPermissionScope scope, @Nonnull final LiquidPermission permission) {
        checkNotReadOnly();

        permissions &= ~toPermissionBit(scope, permission);
    }

    public static void addAllPermissions(@Nonnull final LiquidPermissionScope liquidPermissionScope,
                                         @Nonnull final LiquidPermissionSet set) {
        set.addPermission(liquidPermissionScope, VIEW);
//        set.addPermission(liquidPermissionScope, LiquidPermission.EXECUTE);
        set.addPermission(liquidPermissionScope, MODIFY);
        set.addPermission(liquidPermissionScope, EDIT);
        set.addPermission(liquidPermissionScope, DELETE);
        set.addPermission(liquidPermissionScope, SYSTEM);
    }

    public void addPermission(@Nonnull final LiquidPermissionScope scope, @Nonnull final LiquidPermission permission) {
        checkNotReadOnly();
        permissions |= toPermissionBit(scope, permission);
    }

    private void checkNotReadOnly() {
        if (readonly) {
            throw new IllegalStateException("Cannot change the permissions on a read only LiquidPermissionSet");
        }
    }

    private long toPermissionBit(@Nonnull final LiquidPermissionScope scope, @Nonnull final LiquidPermission permission) {
        return 1L << permission.ordinal() << BITS_PER_SCOPE * scope.ordinal();
    }

    public static void addEditPermissions(@Nonnull final LiquidPermissionScope liquidPermissionScope,
                                          @Nonnull final LiquidPermissionSet set) {
        set.addPermission(liquidPermissionScope, VIEW);
//        set.addPermission(liquidPermissionScope, LiquidPermission.EXECUTE);
        set.addPermission(liquidPermissionScope, MODIFY);
        set.addPermission(liquidPermissionScope, EDIT);
        set.addPermission(liquidPermissionScope, DELETE);
    }

    public static void addModifyPermissions(@Nonnull final LiquidPermissionScope liquidPermissionScope,
                                            @Nonnull final LiquidPermissionSet set) {
        set.addPermission(liquidPermissionScope, VIEW);
//        set.addPermission(liquidPermissionScope, LiquidPermission.EXECUTE);
        set.addPermission(liquidPermissionScope, MODIFY);
    }

    public static void addReadPermissions(@Nonnull final LiquidPermissionScope liquidPermissionScope,
                                          @Nonnull final LiquidPermissionSet set) {
        set.addPermission(liquidPermissionScope, VIEW);
//        set.addPermission(liquidPermissionScope, LiquidPermission.EXECUTE);
    }

    @Nonnull
    public static LiquidPermissionSet getEmptyPermissionSet() {
        return new LiquidPermissionSet();
    }

    @Nonnull
    public static LiquidPermissionSet createPermissionSet(@Nullable final String permissions) {
        if (permissions == null) {
            return defaultPermissionSet.copy();
        }
        else {
            return new LiquidPermissionSet(permissions);
        }
    }

    @Nonnull
    public static LiquidPermissionSet getMinimalPermissionSet() {
        return minimalPermissionSet.copy();
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

    private LiquidPermissionSet(final long permissions, final boolean readonly) {
        this.permissions = permissions;
        this.readonly = readonly;
    }

    private LiquidPermissionSet(@Nonnull final String permissions, final boolean readonly) {
        parse(permissions);
        this.readonly = readonly;
    }

    private LiquidPermissionSet(@Nonnull final String permissions) {
        parse(permissions);
    }

    private void parse(@Nonnull final String permissions) {
        final String[] permissionGroups = permissions.split(",");
        for (final String permissionGroup : permissionGroups) {
            final LiquidPermissionScope permissionScope = LiquidPermissionScope.fromChar(permissionGroup.charAt(0));
            for (int i = 2; i < permissionGroup.length(); i++) {
                addPermission(permissionScope, fromChar(permissionGroup.charAt(i)));
            }
        }
    }

    private LiquidPermissionSet() {
    }

    public void addPermissions(@Nonnull final LiquidPermissionScope scope, @Nonnull final List<LiquidPermission> permissions) {
        checkNotReadOnly();
        for (final LiquidPermission permission : permissions) {
            addPermission(scope, permission);
        }
    }

    public void addPermissions(@Nonnull final LiquidPermissionScope scope, @Nonnull final LiquidPermission... permissions) {
        checkNotReadOnly();
        for (final LiquidPermission permission : permissions) {
            addPermission(scope, permission);
        }
    }

    @Nonnull
    public LiquidPermissionSet convertChangeRequestIntoPermissionSet(final LiquidPermissionChangeType change) {
        if (change == LiquidPermissionChangeType.MAKE_PRIVATE) {
            return getPrivatePermissionSet();
        }
        else if (change == LiquidPermissionChangeType.MAKE_PUBLIC) {
            return getPublicPermissionSet();
        }
        else if (change == LiquidPermissionChangeType.MAKE_PUBLIC_READONLY) {
            return getDefaultPermissions();
        }
        else {
            return this;
        }
    }

    @Nonnull
    public static LiquidPermissionSet getPrivatePermissionSet() {
        return privatePermissions.copy();
    }

    @Nonnull
    public static LiquidPermissionSet getPublicPermissionSet() {
        return publicPermissions.copy();
    }

    @Nonnull
    public static LiquidPermissionSet getDefaultPermissions() {
        return defaultPermissionSet.copy();
    }

    @Nonnull
    public LiquidPermissionSet copy() {
        return new LiquidPermissionSet(permissions, readonly);
    }

    @Nonnull
    public LiquidPermissionSet readOnlyCopy() {
        return new LiquidPermissionSet(permissions, true);
    }

    public void removePermissions(@Nonnull final LiquidPermissionScope scope, @Nonnull final List<LiquidPermission> permissions) {
        checkNotReadOnly();
        for (final LiquidPermission permission : permissions) {
            removePermission(scope, permission);
        }
    }

    public void removePermissions(@Nonnull final LiquidPermissionScope scope, @Nonnull final LiquidPermission... permissions) {
        checkNotReadOnly();
        for (final LiquidPermission permission : permissions) {
            removePermission(scope, permission);
        }
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

    public String toString() {
        return asFriendlyString();
    }

    public String asFriendlyString() {
        final StringBuilder sb = new StringBuilder();
        for (final LiquidPermissionScope liquidPermissionScope : LiquidPermissionScope.values()) {
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

    public boolean hasPermission(@Nonnull final LiquidPermissionScope scope, @Nonnull final LiquidPermission permission) {
        return (permissions & toPermissionBit(scope, permission)) != 0;
    }
}