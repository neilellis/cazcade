package cazcade.fountain.common.error;


import javax.annotation.Nonnull;

/**
 * @author Neil Ellis
 */

public class ErrorHandler {

    public static void handle(@Nonnull final Throwable t) {
        t.printStackTrace(System.err);
    }


}
