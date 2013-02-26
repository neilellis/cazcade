/*
 * Copyright (c) 2009-2013 Cazcade Limited  - All Rights Reserved
 */

package cazcade.liquid.api;

import javax.annotation.Nonnull;

/**
 * @author neilelliz@cazcade.com
 */
public enum Permission {
    VIEW_PERM('v'), MODIFY_PERM('m'), EDIT_PERM('e'), SYSTEM_PERM('s'), DELETE_PERM('d'), /*EXECUTE('x')*/;


    private final char shortForm;

    @Nonnull
    public static Permission fromChar(final char c) {
        switch (Character.toLowerCase(c)) {
            case 'v':
                return VIEW_PERM;
            case 'm':
                return MODIFY_PERM;
            case 'e':
                return EDIT_PERM;
            //            case 'x':
            //                return EXECUTE;
            case 'd':
                return DELETE_PERM;
            case 's':
                return SYSTEM_PERM;
            default:
                throw new IllegalArgumentException("Unrecognized liquid action " + c);
        }
    }

    Permission(final char c) {
        shortForm = c;
    }

    public char asShortForm() {
        return shortForm;
    }
}
