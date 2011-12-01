package cazcade.liquid.impl.xstream;


import cazcade.common.Logger;
import cazcade.liquid.api.LiquidURI;
import com.thoughtworks.xstream.converters.basic.AbstractSingleValueConverter;

import javax.annotation.Nonnull;

public class LiquidURIConverter extends AbstractSingleValueConverter {
    @Nonnull
    private final static Logger log = Logger.getLogger(LiquidURIConverter.class);


    public boolean canConvert(Class clazz) {
        return LiquidURI.class.isAssignableFrom(clazz);
    }

    @Nonnull
    public Object fromString(String str) {
        return new LiquidURI(str);
    }
}