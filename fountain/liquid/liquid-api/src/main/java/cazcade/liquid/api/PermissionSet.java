/*
 * Copyright (c) 2009-2013 Cazcade Limited  - All Rights Reserved
 */

package cazcade.liquid.api;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;

import static cazcade.liquid.api.Permission.*;

/**
 * @author neilelliz@cazcade.com
 */
public class PermissionSet {
    private static final long BITS_PER_SCOPE = 6;
    @Nonnull
    private static final PermissionSet defaultPermissionSet;
    @Nonnull
    private static final PermissionSet minimalPermissionSet;
    @Nonnull
    private static final PermissionSet publicPermissions;
    @Nonnull
    private static final PermissionSet sharedPermissions;
    @Nonnull
    private static final PermissionSet writeOnlyPermissions;
    @Nonnull
    private static final PermissionSet privatePermissions;
    @Nonnull
    private static final PermissionSet childSafePermissions;


    @Nonnull
    private static final PermissionSet privateSharedPermissions;

    private           long    permissions;
    private transient boolean readonly;

    static {
        defaultPermissionSet = new PermissionSet();
        addAllPermissions(PermissionScope.OWNER_SCOPE, defaultPermissionSet);
        addAllPermissions(PermissionScope.ADULT_SCOPE, defaultPermissionSet);
        addAllPermissions(PermissionScope.TEEN_SCOPE, defaultPermissionSet);
        addModifyPermissions(PermissionScope.MEMBER_SCOPE, defaultPermissionSet);
        addReadPermissions(PermissionScope.FRIEND_SCOPE, defaultPermissionSet);
        addReadPermissions(PermissionScope.WORLD_SCOPE, defaultPermissionSet);
        addReadPermissions(PermissionScope.UNKNOWN_SCOPE, defaultPermissionSet);
        addReadPermissions(PermissionScope.VERIFIED_SCOPE, defaultPermissionSet);

        minimalPermissionSet = new PermissionSet();
        addAllPermissions(PermissionScope.OWNER_SCOPE, minimalPermissionSet);
        addReadPermissions(PermissionScope.ADULT_SCOPE, minimalPermissionSet);
        addReadPermissions(PermissionScope.TEEN_SCOPE, minimalPermissionSet);

        publicPermissions = PermissionSet.getDefaultPermissions();
        PermissionSet.addModifyPermissions(PermissionScope.VERIFIED_SCOPE, publicPermissions);
        PermissionSet.addModifyPermissions(PermissionScope.WORLD_SCOPE, publicPermissions);

        sharedPermissions = PermissionSet.getMinimalPermissionSet();
        PermissionSet.addModifyPermissions(PermissionScope.FRIEND_SCOPE, sharedPermissions);

        writeOnlyPermissions = PermissionSet.getMinimalPermissionSet();
        writeOnlyPermissions.addPermission(PermissionScope.VERIFIED_SCOPE, MODIFY_PERM);
        privatePermissions = PermissionSet.getMinimalPermissionSet();

        privateSharedPermissions = PermissionSet.getMinimalPermissionSet();
        childSafePermissions = PermissionSet.getDefaultPermissions();
        PermissionSet.addReadPermissions(PermissionScope.CHILD_SCOPE, childSafePermissions);
    }

    public static void removeAllPermissions(@Nonnull final PermissionScope permissionScope, @Nonnull final PermissionSet set) {
        set.removePermission(permissionScope, VIEW_PERM);
        //        set.addPermission(permissionScope, Permission.EXECUTE);
        set.removePermission(permissionScope, MODIFY_PERM);
        set.removePermission(permissionScope, EDIT_PERM);
        set.removePermission(permissionScope, DELETE_PERM);
        set.removePermission(permissionScope, SYSTEM_PERM);
    }

    public void removePermission(@Nonnull final PermissionScope scope, @Nonnull final Permission permission) {
        checkNotReadOnly();

        permissions &= ~toPermissionBit(scope, permission);
    }

    public static void addAllPermissions(@Nonnull final PermissionScope permissionScope, @Nonnull final PermissionSet set) {
        set.addPermission(permissionScope, VIEW_PERM);
        //        set.addPermission(permissionScope, Permission.EXECUTE);
        set.addPermission(permissionScope, MODIFY_PERM);
        set.addPermission(permissionScope, EDIT_PERM);
        set.addPermission(permissionScope, DELETE_PERM);
        set.addPermission(permissionScope, SYSTEM_PERM);
    }

    public void addPermission(@Nonnull final PermissionScope scope, @Nonnull final Permission permission) {
        checkNotReadOnly();
        permissions |= toPermissionBit(scope, permission);
    }

    private void checkNotReadOnly() {
        if (readonly) {
            throw new IllegalStateException("Cannot change the permissions on a read only PermissionSet");
        }
    }

    private long toPermissionBit(@Nonnull final PermissionScope scope, @Nonnull final Permission permission) {
        return 1L << permission.ordinal() << BITS_PER_SCOPE * scope.ordinal();
    }

    public static void addEditPermissions(@Nonnull final PermissionScope permissionScope, @Nonnull final PermissionSet set) {
        set.addPermission(permissionScope, VIEW_PERM);
        //        set.addPermission(permissionScope, Permission.EXECUTE);
        set.addPermission(permissionScope, MODIFY_PERM);
        set.addPermission(permissionScope, EDIT_PERM);
        set.addPermission(permissionScope, DELETE_PERM);
    }

    public static void addModifyPermissions(@Nonnull final PermissionScope permissionScope, @Nonnull final PermissionSet set) {
        set.addPermission(permissionScope, VIEW_PERM);
        //        set.addPermission(permissionScope, Permission.EXECUTE);
        set.addPermission(permissionScope, MODIFY_PERM);
    }

    public static void addReadPermissions(@Nonnull final PermissionScope permissionScope, @Nonnull final PermissionSet set) {
        set.addPermission(permissionScope, VIEW_PERM);
        //        set.addPermission(permissionScope, Permission.EXECUTE);
    }

    @Nonnull
    public static PermissionSet getEmptyPermissionSet() {
        return new PermissionSet();
    }

    @Nonnull
    public static PermissionSet createPermissionSet(@Nullable final String permissions) {
        if (permissions == null) {
            return defaultPermissionSet.copy();
        } else {
            return new PermissionSet(permissions);
        }
    }

    @Nonnull
    public static PermissionSet getMinimalPermissionSet() {
        return minimalPermissionSet.copy();
    }

    @Nonnull
    public static PermissionSet getWriteOnlyPermissionSet() {
        return writeOnlyPermissions.copy();
    }

    @Nonnull
    public static PermissionSet getSharedPermissionSet() {
        return sharedPermissions.copy();
    }

    @Nonnull
    public static PermissionSet getPrivateSharedPermissionSet() {
        return privateSharedPermissions.copy();
    }

    @Nonnull
    public static PermissionSet getChildSafePermissionSet() {
        return childSafePermissions.copy();
    }

    @Nonnull
    public static PermissionSet getMinimalNoDeletePermissionSet() {
        return getMinimalPermissionSet().removeDeletePermission();
    }

    @Nonnull
    public PermissionSet removeDeletePermission() {
        removePermission(PermissionScope.OWNER_SCOPE, DELETE_PERM);
        removePermission(PermissionScope.EDITOR_SCOPE, DELETE_PERM);
        removePermission(PermissionScope.UNKNOWN_SCOPE, DELETE_PERM);
        removePermission(PermissionScope.ADULT_SCOPE, DELETE_PERM);
        removePermission(PermissionScope.TEEN_SCOPE, DELETE_PERM);
        removePermission(PermissionScope.MEMBER_SCOPE, DELETE_PERM);
        removePermission(PermissionScope.FRIEND_SCOPE, DELETE_PERM);
        removePermission(PermissionScope.WORLD_SCOPE, DELETE_PERM);
        removePermission(PermissionScope.VERIFIED_SCOPE, DELETE_PERM);
        return this;
    }

    @Nonnull
    public static PermissionSet getPrivateNoDeletePermissionSet() {
        return getPrivatePermissionSet().removeDeletePermission();
    }

    @Nonnull
    public static PermissionSet getWriteOnlyNoDeletePermissionSet() {
        return getWriteOnlyPermissionSet().removeDeletePermission();
    }

    @Nonnull
    public static PermissionSet getSharedNoDeletePermissionSet() {
        return getSharedPermissionSet().removeDeletePermission();
    }

    @Nonnull
    public static PermissionSet getPrivateSharedNoDeletePermissionSet() {
        return getPrivateSharedPermissionSet().removeDeletePermission();
    }

    @Nonnull
    public static PermissionSet getChildSafeNoDeletePermissionSet() {
        return getChildSafePermissionSet().removeDeletePermission();
    }

    @Nonnull
    public static PermissionSet getPublicNoDeletePermissionSet() {
        return getPublicPermissionSet().removeDeletePermission();
    }

    @Nonnull
    public static PermissionSet getDefaultPermissionsNoDelete() {
        return defaultPermissionSet.copy().removeDeletePermission();
    }

    private PermissionSet(final long permissions, final boolean readonly) {
        this.permissions = permissions;
        this.readonly = readonly;
    }

    private PermissionSet(@Nonnull final String permissions, final boolean readonly) {
        parse(permissions);
        this.readonly = readonly;
    }

    private PermissionSet(@Nonnull final String permissions) {
        parse(permissions);
    }

    private void parse(@Nonnull final String permissions) {
        final String[] permissionGroups = permissions.split(",");
        for (final String permissionGroup : permissionGroups) {
            final PermissionScope permissionScope = PermissionScope.fromChar(permissionGroup.charAt(0));
            for (int i = 2; i < permissionGroup.length(); i++) {
                addPermission(permissionScope, fromChar(permissionGroup.charAt(i)));
            }
        }
    }

    private PermissionSet() {
    }

    public void addPermissions(@Nonnull final PermissionScope scope, @Nonnull final List<Permission> permissions) {
        checkNotReadOnly();
        for (final Permission permission : permissions) {
            addPermission(scope, permission);
        }
    }

    public void addPermissions(@Nonnull final PermissionScope scope, @Nonnull final Permission... permissions) {
        checkNotReadOnly();
        for (final Permission permission : permissions) {
            addPermission(scope, permission);
        }
    }

    @Nonnull
    public PermissionSet convertChangeRequestIntoPermissionSet(final PermissionChangeType change) {
        if (change == PermissionChangeType.MAKE_PRIVATE) {
            return getPrivatePermissionSet();
        } else if (change == PermissionChangeType.MAKE_PUBLIC) {
            return getPublicPermissionSet();
        } else if (change == PermissionChangeType.MAKE_PUBLIC_READONLY) {
            return getDefaultPermissions();
        } else {
            return this;
        }
    }

    @Nonnull
    public static PermissionSet getPrivatePermissionSet() {
        return privatePermissions.copy();
    }

    @Nonnull
    public static PermissionSet getPublicPermissionSet() {
        return publicPermissions.copy();
    }

    @Nonnull
    public static PermissionSet getDefaultPermissions() {
        return defaultPermissionSet.copy();
    }

    @Nonnull
    public PermissionSet copy() {
        return new PermissionSet(permissions, readonly);
    }

    @Nonnull
    public PermissionSet readOnlyCopy() {
        return new PermissionSet(permissions, true);
    }

    public void removePermissions(@Nonnull final PermissionScope scope, @Nonnull final List<Permission> permissions) {
        checkNotReadOnly();
        for (final Permission permission : permissions) {
            removePermission(scope, permission);
        }
    }

    public void removePermissions(@Nonnull final PermissionScope scope, @Nonnull final Permission... permissions) {
        checkNotReadOnly();
        for (final Permission permission : permissions) {
            removePermission(scope, permission);
        }
    }

    @Nonnull
    public PermissionSet restoreDeletePermission() {
        if (hasPermission(PermissionScope.OWNER_SCOPE, EDIT_PERM)) {
            addPermission(PermissionScope.OWNER_SCOPE, DELETE_PERM);
        }
        if (hasPermission(PermissionScope.ADULT_SCOPE, EDIT_PERM)) {
            addPermission(PermissionScope.ADULT_SCOPE, DELETE_PERM);
        }
        if (hasPermission(PermissionScope.TEEN_SCOPE, EDIT_PERM)) {
            addPermission(PermissionScope.TEEN_SCOPE, DELETE_PERM);
        }
        if (hasPermission(PermissionScope.MEMBER_SCOPE, EDIT_PERM)) {
            addPermission(PermissionScope.MEMBER_SCOPE, DELETE_PERM);
        }
        if (hasPermission(PermissionScope.FRIEND_SCOPE, EDIT_PERM)) {
            addPermission(PermissionScope.FRIEND_SCOPE, DELETE_PERM);
        }
        if (hasPermission(PermissionScope.WORLD_SCOPE, EDIT_PERM)) {
            addPermission(PermissionScope.WORLD_SCOPE, DELETE_PERM);
        }
        if (hasPermission(PermissionScope.EDITOR_SCOPE, EDIT_PERM)) {
            addPermission(PermissionScope.EDITOR_SCOPE, DELETE_PERM);
        }
        if (hasPermission(PermissionScope.UNKNOWN_SCOPE, EDIT_PERM)) {
            addPermission(PermissionScope.UNKNOWN_SCOPE, DELETE_PERM);
        }
        if (hasPermission(PermissionScope.VERIFIED_SCOPE, EDIT_PERM)) {
            addPermission(PermissionScope.VERIFIED_SCOPE, DELETE_PERM);
        }
        return this;
    }

    public String toString() {
        return asFriendlyString();
    }

    public String asFriendlyString() {
        final StringBuilder sb = new StringBuilder();
        for (final PermissionScope permissionScope : PermissionScope.values()) {
            sb.append(Character.toLowerCase(permissionScope.asShortForm()));
            sb.append('=');
            if (hasPermission(permissionScope, VIEW_PERM)) {
                sb.append(VIEW_PERM.asShortForm());
            }
            if (hasPermission(permissionScope, MODIFY_PERM)) {
                sb.append(MODIFY_PERM.asShortForm());
            }
            if (hasPermission(permissionScope, EDIT_PERM)) {
                sb.append(EDIT_PERM.asShortForm());
            }
            //            if (allowed(permissionScope, Permission.EXECUTE)) {
            //                sb.append(Permission.EXECUTE.asShortForm());
            //            }
            if (hasPermission(permissionScope, DELETE_PERM)) {
                sb.append(DELETE_PERM.asShortForm());
            }
            if (hasPermission(permissionScope, SYSTEM_PERM)) {
                sb.append(SYSTEM_PERM.asShortForm());
            }
            sb.append(',');
        }
        sb.deleteCharAt(sb.length() - 1);
        return sb.toString();
    }

    public boolean hasPermission(@Nonnull final PermissionScope scope, @Nonnull final Permission permission) {
        return (permissions & toPermissionBit(scope, permission)) != 0;
    }
}