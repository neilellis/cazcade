package cazcade.fountain.index.common.util;


import javax.annotation.Nonnull;

/**
 * @author neilellis@cazcade.com
 */
public class BoardUtil {
    public static boolean isUserName(@Nonnull final String boardName) {
        return boardName.matches("^@[a-zA-Z0-9_]+$");
    }

    public static boolean isPrivateBoardName(@Nonnull final String boardName) {
        return boardName.matches("@[a-zA-Z0-9_]+\\+.*");
    }

    public static boolean isQueryBoardName(@Nonnull final String boardName) {
        return boardName.startsWith("!");
    }
}
