package cazcade.fountain.server.rest;

import java.util.Map;

/**
 * @author neilelliz@cazcade.com
 */
public class AbstractRestHandler implements RestHandler {

    protected void checkForSingleValueParams(Map<String, String[]> params, String... expected) {
        for (String expectedKey : expected) {
            String[] paramArray = params.get(expectedKey);
            if (paramArray == null) {
                throw new RestHandlerException("Expected parameter " + expectedKey + " was missing for this call.");
            }
            if (paramArray.length != 1) {
                throw new RestHandlerException("Expected parameter " + expectedKey + " to have a single value, it had " + paramArray.length + ".");
            }
            if (paramArray[0] == null) {
                throw new RestHandlerException("Parameter " + expectedKey + " must have a value.");
            }
        }
    }
}
