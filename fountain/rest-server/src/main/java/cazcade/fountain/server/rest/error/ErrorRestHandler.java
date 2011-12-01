package cazcade.fountain.server.rest.error;

import cazcade.common.Logger;
import cazcade.fountain.server.rest.AbstractRestHandler;

import javax.annotation.Nonnull;
import java.net.URISyntaxException;
import java.util.Map;

/**
 * @author Neil Ellis
 */

public class ErrorRestHandler extends AbstractRestHandler {

    @Nonnull
    private static final Logger log = Logger.getLogger(ErrorRestHandler.class);


    public void create(@Nonnull final Map<String, String[]> parameters) throws URISyntaxException {
        final String error = parameters.get("error")[0];
        final String detail = parameters.get("detail")[0];
        final String hash = log.hash(detail.replaceAll("0x[0-9a-z]+", ""));
        log.sendToJira(error, hash, error, detail, "maelstrom");
    }


}