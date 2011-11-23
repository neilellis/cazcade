package cazcade.liquid.api;


/**
 * A form of URI that relates only to actual boards, not to generic pools.
 *
 * @author neilellis@cazcade.com
 */
public class LiquidBoardURL {

    private static final String PUBLIC_BOARD_USER_STEM = "pool:///boards";
    private static final String USER_STEM = "pool:///people";
    public static final String BOARD_PREFIX = "";

    private LiquidURI uri;
    private String shortURL;

    public LiquidBoardURL(LiquidURI uri) {
        this.uri = uri;
        shortURL = convertToShort(uri.getWithoutFragmentOrComment().asString());
    }

    public LiquidBoardURL(String shortURL) {
        this.shortURL = shortURL;
        uri = new LiquidURI(convertFromShort(shortURL));
    }

    @Override
    public String toString() {
        //We have to do this to ensure a canonical format.
        return shortURL = convertToShort(uri.asString());
    }

    public static String convertToShort(String longURL) {
        if (longURL == null) {
            throw new NullPointerException("Attempted to pass in a null longURL to LiquidBoardURL.converToShort()");
        }
        String result = "";
        if (longURL.startsWith(PUBLIC_BOARD_USER_STEM)) {
            result = longURL.substring(PUBLIC_BOARD_USER_STEM.length() + 1);

        } else if (longURL.startsWith(USER_STEM)) {
            String str = longURL.substring(USER_STEM.length() + 1);
            final String[] strings = str.split("/");
            if (strings.length > 1) {
                if (strings.length == 1 || (!strings[1].equals("public") && !strings[1].equals("profile"))) {
                    throw new IllegalArgumentException("Format not valid for conversion to short url, needs to be in the boards pool to be a short url, '" + longURL + "'.");
                }
                if (strings[1].equals("profile")) {
                    result = "@" + strings[0];
                } else {
                    String board = str.substring(strings[0].length() + strings[1].length() + 2);
                    result = board + "@" + strings[0];
                }

            } else {
                throw new IllegalArgumentException("Format not valid for conversion to short url cannot reference top level for user, '" + longURL + "'.");
            }
        } else {
            throw new IllegalArgumentException("Liquid URIs should start with " + PUBLIC_BOARD_USER_STEM + " or " + USER_STEM + " the supplied URI was " + longURL);
        }
        return result;
    }

    public static String convertFromShort(String url) {
        String shortURL = url.replaceAll("~", "@");
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

    public LiquidURI asURI() {
        return uri;
    }

    public boolean isPublicBoard() {
        return !shortURL.startsWith("@") && !shortURL.startsWith("$");
    }

    public static boolean isConvertable(LiquidURI uri) {
        String longURL = uri.asString();
        return isConvertable(longURL);
    }

    public static boolean isConvertable(String longURL) {
        try {
            convertToShort(longURL);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public boolean isProfileBoard() {
        return shortURL.startsWith("@");
    }

    public boolean isPersonalBoard() {
        return shortURL.contains("@");
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
    public boolean isListedByConvention() {
        return !shortURL.startsWith("-");
    }


    public String asUrlSafe() {
        return shortURL.replaceAll("@", "~");
    }

}
