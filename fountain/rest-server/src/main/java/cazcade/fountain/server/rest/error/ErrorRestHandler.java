package cazcade.fountain.server.rest.error;

import cazcade.common.Logger;
import cazcade.fountain.server.rest.AbstractRestHandler;

import java.net.URISyntaxException;
import java.util.Map;

/**
 * @author Neil Ellis
 */

public class ErrorRestHandler extends AbstractRestHandler {

    private final static Logger log = Logger.getLogger(ErrorRestHandler.class);


    public void create(Map<String, String[]> parameters) throws URISyntaxException {
        String error = parameters.get("error")[0];
        String detail = parameters.get("detail")[0];
        String hash = log.hash(detail.replaceAll("0x[0-9a-z]+", ""));
        log.sendToJira(error, hash, error, detail, "maelstrom");
    }


}