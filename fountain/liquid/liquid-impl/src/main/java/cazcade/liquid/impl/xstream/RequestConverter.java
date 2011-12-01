package cazcade.liquid.impl.xstream;

import cazcade.common.Logger;
import cazcade.liquid.api.LiquidMessage;
import cazcade.liquid.api.LiquidRequest;
import cazcade.liquid.api.LiquidRequestType;
import com.thoughtworks.xstream.converters.Converter;
import com.thoughtworks.xstream.converters.MarshallingContext;
import com.thoughtworks.xstream.converters.UnmarshallingContext;
import com.thoughtworks.xstream.converters.reflection.ReflectionConverter;
import com.thoughtworks.xstream.io.HierarchicalStreamReader;
import com.thoughtworks.xstream.io.HierarchicalStreamWriter;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

/**
 * @author neilelliz@cazcade.com
 */
public class RequestConverter implements Converter {
    @Nonnull
    private final static Logger log = Logger.getLogger(RequestConverter.class);

    @Nonnull
    private static final ReflectionConverter CONVERTER = new ReflectionConverter(LiquidXStreamFactory.getXstream().getMapper(), LiquidXStreamFactory.getXstream().getReflectionProvider());

    public void marshal(@Nonnull Object o, @Nonnull HierarchicalStreamWriter hierarchicalStreamWriter, MarshallingContext marshallingContext) {
        LiquidRequest request = (LiquidRequest) o;
        hierarchicalStreamWriter.startNode("type");
        hierarchicalStreamWriter.setValue(request.getRequestType().name().toLowerCase());
        hierarchicalStreamWriter.endNode();
        hierarchicalStreamWriter.startNode("body");
        try {
            CONVERTER.marshal(o, hierarchicalStreamWriter, marshallingContext);
        } catch (RuntimeException e) {
            log.error("Error attempting to serialize an {0}", o.getClass());
            log.error(e);
        }
        hierarchicalStreamWriter.endNode();
//        Class clazz = o.getClass();
//        while (clazz.getSuperclass() != Object.class) {
//            Field[] fields = clazz.getDeclaredFields();
//            for (Field field : fields) {
//                try {
//                    if (!Modifier.isStatic(field.getModifiers())) {
//                        field.setAccessible(true);
//                    }
//                } catch (IllegalAccessException e) {
//                    log.error(e.getMessage(), e);
//                }
//
//            }
//            clazz = clazz.getSuperclass();
//        }
    }

    public Object unmarshal(@Nonnull HierarchicalStreamReader hierarchicalStreamReader, @Nonnull UnmarshallingContext unmarshallingContext) {
        hierarchicalStreamReader.moveDown();
        if (!hierarchicalStreamReader.getNodeName().equals("type")) {
            throw new IllegalStateException("Expected to find 'type' node here.");
        }
        String type = hierarchicalStreamReader.getValue();
        hierarchicalStreamReader.moveUp();

        LiquidRequestType liquidRequestType = LiquidRequestType.valueOf(type.toUpperCase());
        Class<? extends LiquidMessage> requestClass = liquidRequestType.getRequestClass();
        LiquidRequest liquidRequest = null;
        try {
            Class[] empty = {};
            Constructor<? extends LiquidMessage> c = requestClass.getConstructor(empty);
            c.setAccessible(true);
            liquidRequest = (LiquidRequest) c.newInstance();
        } catch (InstantiationException e) {
            log.error(e.getMessage(), e);
        } catch (IllegalAccessException e) {
            log.error(e.getMessage(), e);
        } catch (NoSuchMethodException e) {
            log.error(e.getMessage(), e);
        } catch (InvocationTargetException e) {
            log.error(e.getMessage(), e);
        }
        hierarchicalStreamReader.moveDown();
        if (!hierarchicalStreamReader.getNodeName().equals("body")) {
            throw new IllegalStateException("Expected to find 'body' node here.");
        }
        Object result = unmarshallingContext.convertAnother(liquidRequest, requestClass, CONVERTER);
        hierarchicalStreamReader.moveUp();
//        System.out.println(liquidRequest.getId().toString());
        return result;
    }


    @Nullable
    public String toString(Object o) {
        return null;
    }

    @Nullable
    public Object fromString(String s) {
        return null;
    }

    public boolean canConvert(Class aClass) {
        return LiquidRequest.class.isAssignableFrom(aClass);
    }
}