package cazcade.liquid.api.lsd;

import cazcade.liquid.api.LiquidUUID;

import javax.annotation.Nonnull;
import java.util.Map;

/**
 * @author Neil Ellis
 */

public interface LSDEntityFactory {

    @Nonnull
    LSDBaseEntity create(LiquidUUID uuid);

    /**
     * @param properties
     * @param dotPrefixed true if it should only be built from properties starting with "."
     * @return
     */
    @Nonnull
    LSDBaseEntity create(Map<String, String> properties, boolean dotPrefixed);

    @Nonnull
    LSDTransferEntity createFromServletProperties(Map<String, String[]> properties);


}
