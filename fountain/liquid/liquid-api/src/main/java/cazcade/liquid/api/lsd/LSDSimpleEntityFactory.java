package cazcade.liquid.api.lsd;

import cazcade.liquid.api.LiquidUUID;

import javax.annotation.Nonnull;
import java.util.HashMap;
import java.util.Map;

/**
 * @author Neil Ellis
 */

public class LSDSimpleEntityFactory implements LSDEntityFactory {

    @Nonnull
    public LSDEntity create(@Nonnull final LiquidUUID uuid) {
        final LSDEntity structuredPropertyLSDEntity = LSDSimpleEntity.createEmpty();
        structuredPropertyLSDEntity.setAttribute(LSDAttribute.ID, uuid.toString());
        structuredPropertyLSDEntity.setAttribute(LSDAttribute.UPDATED, String.valueOf(System.currentTimeMillis()));
        return structuredPropertyLSDEntity;
    }

    @Nonnull
    public LSDEntity create(@Nonnull final Map<String, String> properties, final boolean dotPrefixed) {
        final Map<String, String> lsdProperties = new HashMap<String, String>();
        if (dotPrefixed) {
            for (final Map.Entry<String, String> entry : properties.entrySet()) {
                final String key = entry.getKey();
                if (key.startsWith(".")) {
                    lsdProperties.put(key.substring(1), entry.getValue());
                }
            }
        } else {
            lsdProperties.putAll(properties);
        }
        return LSDSimpleEntity.createFromProperties(lsdProperties);

    }

    @Nonnull
    public LSDEntity createFromServletProperties(@Nonnull final Map<String, String[]> properties) {
        final Map<String, String> lsdProperties = new HashMap<String, String>();
        for (final Map.Entry<String, String[]> entry : properties.entrySet()) {
            final String key = entry.getKey();
            if (key.startsWith(".")) {
                lsdProperties.put(key.substring(1), entry.getValue()[0]);
            }
        }
        return LSDSimpleEntity.createFromProperties(lsdProperties);
    }


}
