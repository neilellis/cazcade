package cazcade.liquid.impl.xstream;


import cazcade.common.Logger;
import cazcade.liquid.api.LiquidURI;
import cazcade.liquid.api.LiquidUUID;
import com.thoughtworks.xstream.converters.basic.AbstractSingleValueConverter;

public class LiquidUUIDConverter extends AbstractSingleValueConverter {
    private final static Logger log = Logger.getLogger(LiquidUUIDConverter.class);


    public boolean canConvert(Class clazz) {
        return LiquidUUID.class.isAssignableFrom(clazz);
    }

    public Object fromString(String str) {
        return new LiquidUUID(str);
    }
}