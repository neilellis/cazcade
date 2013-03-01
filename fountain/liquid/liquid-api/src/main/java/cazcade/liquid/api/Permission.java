/*
 * Copyright (c) 2009-2013 Cazcade Limited  - All Rights Reserved
 */

package cazcade.liquid.api;

import javax.annotation.Nonnull;

/**
 * @author neilelliz@cazcade.com
 */
public enum Permission {
    P_VIEW('v'), P_MODIFY('m'), P_EDIT('e'), P_SYSTEM('s'), P_DELETE('d'), /*EXECUTE('x')*/;


    private final char shortForm;

    @Nonnull
    public static Permission fromChar(final char c) {
        switch (Character.toLowerCase(c)) {
            case 'v':
                return P_VIEW;
            case 'm':
                return P_MODIFY;
            case 'e':
                return P_EDIT;
            //            case 'x':
            //                return EXECUTE;
            case 'd':
                return P_DELETE;
            case 's':
                return P_SYSTEM;
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
