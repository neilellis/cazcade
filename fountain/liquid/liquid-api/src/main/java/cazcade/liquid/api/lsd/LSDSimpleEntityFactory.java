package cazcade.liquid.api.lsd;

import cazcade.liquid.api.LiquidUUID;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Neil Ellis
 */

public class LSDSimpleEntityFactory implements LSDEntityFactory {

    public LSDEntity create(LiquidUUID uuid) {
        LSDEntity structuredPropertyLSDEntity = LSDSimpleEntity.createEmpty();
        structuredPropertyLSDEntity.setAttribute(LSDAttribute.ID, uuid.toString());
        structuredPropertyLSDEntity.setAttribute(LSDAttribute.UPDATED, String.valueOf(System.currentTimeMillis()));
        return structuredPropertyLSDEntity;
    }

    public LSDEntity create(Map<String, String> properties, boolean dotPrefixed) {
        Map<String, String> lsdProperties = new HashMap<String, String>();
        if (dotPrefixed) {
            for (Map.Entry<String, String> entry : properties.entrySet()) {
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

    public LSDEntity createFromServletProperties(Map<String, String[]> properties) {
        Map<String, String> lsdProperties = new HashMap<String, String>();
        for (Map.Entry<String, String[]> entry : properties.entrySet()) {
            final String key = entry.getKey();
            if (key.startsWith(".")) {
                lsdProperties.put(key.substring(1), entry.getValue()[0]);
            }
        }
        return LSDSimpleEntity.createFromProperties(lsdProperties);
    }


}
