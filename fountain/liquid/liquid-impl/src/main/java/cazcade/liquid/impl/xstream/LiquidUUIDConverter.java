package cazcade.liquid.impl.xstream;


import cazcade.common.Logger;
import cazcade.liquid.api.LiquidUUID;
import com.thoughtworks.xstream.converters.basic.AbstractSingleValueConverter;

import javax.annotation.Nonnull;

public class LiquidUUIDConverter extends AbstractSingleValueConverter {
    @Nonnull
    private final static Logger log = Logger.getLogger(LiquidUUIDConverter.class);


    public boolean canConvert(Class clazz) {
        return LiquidUUID.class.isAssignableFrom(clazz);
    }

    @Nonnull
    public Object fromString(String str) {
        return new LiquidUUID(str);
    }
}