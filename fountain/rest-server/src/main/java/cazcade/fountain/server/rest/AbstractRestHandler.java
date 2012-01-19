package cazcade.fountain.server.rest;

import javax.annotation.Nonnull;
import java.util.Map;

/**
 * @author neilelliz@cazcade.com
 */
public class AbstractRestHandler implements RestHandler {
    protected void checkForSingleValueParams(@Nonnull final Map<String, String[]> params, @Nonnull final String... expected) {
        for (final String expectedKey : expected) {
            final String[] paramArray = params.get(expectedKey);
            if (paramArray == null) {
                throw new RestHandlerException("Expected parameter " + expectedKey + " was missing for this call.");
            }
            if (paramArray.length != 1) {
                throw new RestHandlerException(
                        "Expected parameter " + expectedKey + " to have a single value, it had " + paramArray.length + "."
                );
            }
            if (paramArray[0] == null) {
                throw new RestHandlerException("Parameter " + expectedKey + " must have a value.");
            }
        }
    }
}
