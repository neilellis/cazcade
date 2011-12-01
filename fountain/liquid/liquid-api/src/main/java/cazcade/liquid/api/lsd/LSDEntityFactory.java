package cazcade.liquid.api.lsd;

import cazcade.liquid.api.LiquidUUID;

import javax.annotation.Nonnull;
import java.util.Map;

/**
 * @author Neil Ellis
 */

public interface LSDEntityFactory {

    @Nonnull
    LSDEntity create(LiquidUUID uuid);

    /**
     * @param properties
     * @param dotPrefixed true if it should only be built from properties starting with "."
     * @return
     */
    @Nonnull
    LSDEntity create(Map<String, String> properties, boolean dotPrefixed);

    @Nonnull
    LSDEntity createFromServletProperties(Map<String, String[]> properties);


}
