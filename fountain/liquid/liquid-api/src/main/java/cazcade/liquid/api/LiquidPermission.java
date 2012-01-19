package cazcade.liquid.api;

import javax.annotation.Nonnull;

/**
 * @author neilelliz@cazcade.com
 */
public enum LiquidPermission {
    VIEW('v'), MODIFY('m'), EDIT('e'), SYSTEM('s'), DELETE('d'), /*EXECUTE('x')*/;


    private final char shortForm;

    @Nonnull
    public static LiquidPermission fromChar(final char c) {
        switch (Character.toLowerCase(c)) {
            case 'v':
                return VIEW;
            case 'm':
                return MODIFY;
            case 'e':
                return EDIT;
//            case 'x':
//                return EXECUTE;
            case 'd':
                return DELETE;
            case 's':
                return SYSTEM;
            default:
                throw new IllegalArgumentException("Unrecognized liquid action " + c);
        }
    }

    LiquidPermission(final char c) {
        shortForm = c;
    }

    public char asShortForm() {
        return shortForm;
    }
}
