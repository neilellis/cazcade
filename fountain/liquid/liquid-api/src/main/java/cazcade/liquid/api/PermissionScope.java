/*
 * Copyright (c) 2009-2013 Cazcade Limited  - All Rights Reserved
 */

package cazcade.liquid.api;

import javax.annotation.Nonnull;

/**
 * @author neilelliz@cazcade.com
 */
public enum PermissionScope {
    /**
     * The owner of the resource. *
     */
    OWNER_SCOPE('o'),

    /**
     * Friend of the owner*
     */
    FRIEND_SCOPE('f'),
    /**
     * Has membership relationship to this resource*
     */
    MEMBER_SCOPE('m'),
    /**
     * A user who has been through a verification process, might not be the owner.
     */
    VERIFIED_SCOPE('v'),
    /**
     * Everyone *
     */
    WORLD_SCOPE('w'),
    /**
     * Anonymous
     */
    UNKNOWN_SCOPE('u'),
    /**
     * A user who has identified themselves as being an adult *
     */
    ADULT_SCOPE('a'),
    /**
     * A user who has identified themselves as being 13+ *
     */
    TEEN_SCOPE('t'),
    /**
     * A user who has identified themselves as being 12 or younger *
     */
    CHILD_SCOPE('c'),

    /*
     The person responsible for this version of an object.
     */
    EDITOR_SCOPE('e');


    private final char shortForm;

    @Nonnull
    public static PermissionScope fromChar(final char c) {
        switch (Character.toLowerCase(c)) {
            case 'o':
                return OWNER_SCOPE;
            case 'e':
                return EDITOR_SCOPE;
            case 'f':
                return FRIEND_SCOPE;
            case 'm':
                return MEMBER_SCOPE;
            case 'v':
                return VERIFIED_SCOPE;
            case 'w':
                return WORLD_SCOPE;
            case 'u':
                return UNKNOWN_SCOPE;
            case 'a':
                return ADULT_SCOPE;
            case 't':
                return TEEN_SCOPE;
            case 'c':
                return CHILD_SCOPE;
            default:
                //todo: log this usage.
                return WORLD_SCOPE;
        }
    }

    PermissionScope(final char shortForm) {
        this.shortForm = shortForm;
    }

    public char asShortForm() {
        return shortForm;
    }
}
