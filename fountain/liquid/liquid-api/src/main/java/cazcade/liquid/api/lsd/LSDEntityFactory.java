package cazcade.liquid.api.lsd;

import cazcade.liquid.api.LiquidUUID;

import java.util.Map;

/**
 * @author Neil Ellis
 */

public interface LSDEntityFactory {

    LSDEntity create(LiquidUUID uuid);

    /**
     * @param properties
     * @param dotPrefixed true if it should only be built from properties starting with "."
     * @return
     */
    LSDEntity create(Map<String, String> properties, boolean dotPrefixed);

    LSDEntity createFromServletProperties(Map<String, String[]> properties);


}
