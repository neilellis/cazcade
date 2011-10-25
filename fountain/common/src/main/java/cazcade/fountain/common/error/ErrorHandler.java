package cazcade.fountain.common.error;


/**
 * @author Neil Ellis
 */

public class ErrorHandler {

    public static void handle(Throwable t) {
        t.printStackTrace(System.err);
    }




}
