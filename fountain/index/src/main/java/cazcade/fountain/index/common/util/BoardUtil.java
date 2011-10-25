package cazcade.fountain.index.common.util;


/**
 * @author neilellis@cazcade.com
 */
public class BoardUtil {
    public static boolean isUserName(String boardName) {
        return boardName.matches("^@[a-zA-Z0-9_]+$");
    }

    public static boolean isPrivateBoardName(String boardName) {
        return boardName.matches("@[a-zA-Z0-9_]+\\+.*");
    }

    public static boolean isQueryBoardName(String boardName) {
        return boardName.startsWith("!");
    }

}
