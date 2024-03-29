/*
 * Copyright (c) 2009-2013 Cazcade Limited  - All Rights Reserved
 */

package cazcade.liquid.impl.xstream;

import cazcade.common.Logger;
import cazcade.liquid.api.LiquidMessage;
import cazcade.liquid.api.LiquidRequest;
import cazcade.liquid.api.RequestType;
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
    private static final Logger log = Logger.getLogger(RequestConverter.class);

    @Nonnull
    private static final ReflectionConverter CONVERTER = new ReflectionConverter(LiquidXStreamFactory.getXstream()
                                                                                                     .getMapper(), LiquidXStreamFactory
            .getXstream()
            .getReflectionProvider());

    public boolean canConvert(final Class aClass) {
        return LiquidRequest.class.isAssignableFrom(aClass);
    }

    @Nullable
    public Object fromString(final String s) {
        return null;
    }

    public void marshal(@Nonnull final Object o, @Nonnull final HierarchicalStreamWriter hierarchicalStreamWriter, final MarshallingContext marshallingContext) {
        final LiquidRequest request = (LiquidRequest) o;
        hierarchicalStreamWriter.startNode("type");
        hierarchicalStreamWriter.setValue(request.requestType().name().toLowerCase());
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
        //                    log.error(e);
        //                }
        //
        //            }
        //            clazz = clazz.getSuperclass();
        //        }
    }

    @Nullable
    public String toString(final Object o) {
        return null;
    }

    public Object unmarshal(@Nonnull final HierarchicalStreamReader hierarchicalStreamReader, @Nonnull final UnmarshallingContext unmarshallingContext) {
        hierarchicalStreamReader.moveDown();
        if (!"type".equals(hierarchicalStreamReader.getNodeName())) {
            throw new IllegalStateException("Expected to find 'type' node here.");
        }
        final String type = hierarchicalStreamReader.getValue();
        hierarchicalStreamReader.moveUp();

        final RequestType requestType = RequestType.valueOf(type.toUpperCase());
        final Class<? extends LiquidMessage> requestClass = requestType.getRequestClass();
        LiquidRequest liquidRequest = null;
        try {
            final Class[] empty = {};
            final Constructor<? extends LiquidMessage> c = requestClass.getConstructor(empty);
            c.setAccessible(true);
            liquidRequest = (LiquidRequest) c.newInstance();
        } catch (InstantiationException e) {
            log.error(e);
        } catch (IllegalAccessException e) {
            log.error(e);
        } catch (NoSuchMethodException e) {
            log.error(e);
        } catch (InvocationTargetException e) {
            log.error(e);
        }
        hierarchicalStreamReader.moveDown();
        if (!"body".equals(hierarchicalStreamReader.getNodeName())) {
            throw new IllegalStateException("Expected to find 'body' node here.");
        }
        final Object result = unmarshallingContext.convertAnother(liquidRequest, requestClass, CONVERTER);
        hierarchicalStreamReader.moveUp();
        //        System.out.println(liquidRequest.id().toString());
        return result;
    }
}