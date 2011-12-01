package cazcade.liquid.api;

import javax.annotation.Nonnull;

/**
 * @author neilelliz@cazcade.com
 */
public enum LiquidPermissionScope {

    /**
     * The owner of the resource. *
     */
    OWNER('o'),

    /**
     * Friend of the owner*
     */
    FRIEND('f'),
    /**
     * Has membership relationship to this resource*
     */
    MEMBER('m'),
    /**
     * A user who has been through a verification process, might not be the owner.
     */
    VERIFIED('v'),
    /**
     * Everyone *
     */
    WORLD('w'),
    /**
     * Anonymous
     */
    UNKNOWN('u'),
    /**
     * A user who has identified themselves as being an adult *
     */
    ADULT('a'),
    /**
     * A user who has identified themselves as being 13+ *
     */
    TEEN('t'),
    /**
     * A user who has identified themselves as being 12 or younger *
     */
    CHILD('c'),

    /*
     The person responsible for this version of an object.
     */
    EDITOR('e');


    private final char shortForm;

    LiquidPermissionScope(char shortForm) {
        this.shortForm = shortForm;
    }

    @Nonnull
    public static LiquidPermissionScope fromChar(char c) {
        switch (Character.toLowerCase(c)) {
            case 'o':
                return OWNER;
            case 'e':
                return EDITOR;
            case 'f':
                return FRIEND;
            case 'm':
                return MEMBER;
            case 'v':
                return VERIFIED;
            case 'w':
                return WORLD;
            case 'u':
                return UNKNOWN;
            case 'a':
                return ADULT;
            case 't':
                return TEEN;
            case 'c':
                return CHILD;
            default:
                //todo: log this usage.
                return WORLD;
        }

    }

    public char asShortForm() {
        return shortForm;
    }
}
