package cazcade.liquid.impl.xstream;


import cazcade.common.Logger;
import cazcade.liquid.api.LURI;
import com.thoughtworks.xstream.converters.basic.AbstractSingleValueConverter;

import javax.annotation.Nonnull;

public class LiquidURIConverter extends AbstractSingleValueConverter {
    @Nonnull
    private static final Logger log = Logger.getLogger(LiquidURIConverter.class);


    public boolean canConvert(final Class clazz) {
        return LURI.class.isAssignableFrom(clazz);
    }

    @Nonnull
    public Object fromString(final String str) {
        return new LURI(str);
    }
}