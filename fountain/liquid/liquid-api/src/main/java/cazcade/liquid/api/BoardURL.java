/*
 * Copyright (c) 2009-2013 Cazcade Limited  - All Rights Reserved
 */

package cazcade.liquid.api;


import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * A form of URI that relates only to actual boards, not to generic pools.
 *
 * @author neilellis@cazcade.com
 */
public class BoardURL {
    @Nonnull
    public static final String BOARD_PREFIX = "";

    @Nonnull
    private static final String PUBLIC_BOARD_USER_STEM = "pool:///boards/public";
    @Nonnull
    private static final String USER_STEM              = "pool:///people";

    @Nonnull
    private final LURI   uri;
    private       String shortURL;

    public static boolean isConvertable(@Nullable final LURI uri) {
        return uri != null && isConvertable(uri.asString());
    }

    public static boolean isConvertable(final String longURL) {
        try {
            convertToShort(longURL);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public BoardURL(@Nonnull final LURI uri) {
        this.uri = uri;
        shortURL = convertToShort(uri.withoutFragmentOrComment().asString());
    }

    public static String convertToShort(@Nullable final String longURL) {
        if (longURL == null) {
            throw new NullPointerException("Attempted to pass in a null longURL to BoardURL.convertToShort()");
        }
        String result = "";
        if (longURL.startsWith(PUBLIC_BOARD_USER_STEM)) {
            result = longURL.substring(PUBLIC_BOARD_USER_STEM.length() + 1);
        } else if (longURL.startsWith(USER_STEM)) {
            final String str = longURL.substring(USER_STEM.length() + 1);
            final String[] strings = str.split("/");
            if (strings.length > 1) {
                if (!"public".equals(strings[1]) && !"profile".equals(strings[1])) {
                    throw new IllegalArgumentException(
                            "Format not valid for conversion to short url, needs to be in the boards pool to be a short url, '"
                            +
                            longURL
                            +
                            "'.");
                }
                if ("profile".equals(strings[1])) {
                    result = "@" + strings[0];
                } else {
                    final String board = str.substring(strings[0].length() + strings[1].length() + 2);
                    result = board + "@" + strings[0];
                }
            } else {
                throw new IllegalArgumentException(
                        "Format not valid for conversion to short url cannot reference top level for user, '"
                        + longURL
                        + "'.");
            }
        } else {
            throw new IllegalArgumentException("Liquid URIs which require short form should start with " +
                                               PUBLIC_BOARD_USER_STEM +
                                               " or " +
                                               USER_STEM +
                                               " the supplied URI was " +
                                               longURL);
        }
        return result;
    }

    public BoardURL(@Nonnull final String shortURL) {
        this.shortURL = shortURL;
        uri = new LURI(from(shortURL));
    }

    public static String from(@Nonnull final String url) {
        final String shortURL = url.replaceAll("~", "@");
        String str;
        if (shortURL.contains("@")) {
            str = USER_STEM + "/";
            //            if (shortURL.contains("+")) {
            //                final String username = shortURL.substring(1).substring(0, shortURL.indexOf("+") - 1);
            //                final String friends = shortURL.substring(shortURL.indexOf("+") + 1);
            //                str = str + username + "/private/" + BOARD_PREFIX + "group";
            //                int count = 0;
            //                final String[] parts = friends.split("\\+");
            //                Arrays.sort(parts);
            //                for (String part : parts) {
            //                    if (part.startsWith("@")) {
            //                        str = str + "__u__" + part.substring(1);
            //                    } else {
            //                        str = str + "__b__" + part;
            //                    }
            //                }
            //            } else


            if (shortURL.startsWith("@")) {
                str = str + shortURL.substring(1) + "/profile";
            } else {
                final String[] strings = shortURL.split("@");
                str = str + strings[1] + "/public/" + strings[0];
            }
        } else {
            str = PUBLIC_BOARD_USER_STEM + "/" + BOARD_PREFIX + shortURL;
        }
        return str;
    }

    @Nonnull
    public LURI asURI() {
        return uri;
    }

    public String safe() {
        return shortURL.replaceAll("@", "~");
    }

    /**
     * We use this to deduce if the board should be listed by default,
     * anything starting with a - sign is unlisted by default eg.
     * <p/>
     * <pre>
     * -ABC1234-DD1456-567 is unlisted
     * music is listed
     * music@fred is listed
     * -music@fred is unlisted
     * </pre>
     * it's just a simple convention really, boards can be made listed or unlisted (in theory) at any time.
     *
     * @return true if the board name starts with -
     */
    public boolean listedConvention() {
        return !shortURL.startsWith("-");
    }

    public boolean personal() {
        return shortURL.contains("@");
    }

    public boolean profile() {
        return shortURL.startsWith("@");
    }

    public boolean publicBoard() {
        return !shortURL.startsWith("@") && !shortURL.startsWith("$");
    }

    @Override
    public String toString() {
        //We have to do this to ensure a canonical format.
        return shortURL = convertToShort(uri.asString());
    }
}
