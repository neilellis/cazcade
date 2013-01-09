package cazcade.liquid.api;

import com.google.gwt.user.client.rpc.SerializationException;
import com.google.gwt.user.client.rpc.SerializationStreamReader;
import com.google.gwt.user.client.rpc.SerializationStreamWriter;
import com.google.gwt.user.client.rpc.impl.ReflectionHelper;

@SuppressWarnings("deprecation")
public class LiquidSessionIdentifier_FieldSerializer implements com.google.gwt.user.client.rpc.impl.TypeHandler {
  private static native cazcade.liquid.api.LiquidURI getAlias(cazcade.liquid.api.LiquidSessionIdentifier instance) /*-{
    return instance.@cazcade.liquid.api.LiquidSessionIdentifier::alias;
  }-*/;
  
  private static native void setAlias(cazcade.liquid.api.LiquidSessionIdentifier instance, cazcade.liquid.api.LiquidURI value) 
  /*-{
    instance.@cazcade.liquid.api.LiquidSessionIdentifier::alias = value;
  }-*/;
  
  private static native cazcade.liquid.api.LiquidUUID getSession(cazcade.liquid.api.LiquidSessionIdentifier instance) /*-{
    return instance.@cazcade.liquid.api.LiquidSessionIdentifier::session;
  }-*/;
  
  private static native void setSession(cazcade.liquid.api.LiquidSessionIdentifier instance, cazcade.liquid.api.LiquidUUID value) 
  /*-{
    instance.@cazcade.liquid.api.LiquidSessionIdentifier::session = value;
  }-*/;
  
  public static void deserialize(SerializationStreamReader streamReader, cazcade.liquid.api.LiquidSessionIdentifier instance) throws SerializationException {
    setAlias(instance, (cazcade.liquid.api.LiquidURI) streamReader.readObject());
    setSession(instance, (cazcade.liquid.api.LiquidUUID) streamReader.readObject());
    
  }
  
  public static cazcade.liquid.api.LiquidSessionIdentifier instantiate(SerializationStreamReader streamReader) throws SerializationException {
    return new cazcade.liquid.api.LiquidSessionIdentifier();
  }
  
  public static void serialize(SerializationStreamWriter streamWriter, cazcade.liquid.api.LiquidSessionIdentifier instance) throws SerializationException {
    streamWriter.writeObject(getAlias(instance));
    streamWriter.writeObject(getSession(instance));
    
  }
  
  public Object create(SerializationStreamReader reader) throws SerializationException {
    return cazcade.liquid.api.LiquidSessionIdentifier_FieldSerializer.instantiate(reader);
  }
  
  public void deserial(SerializationStreamReader reader, Object object) throws SerializationException {
    cazcade.liquid.api.LiquidSessionIdentifier_FieldSerializer.deserialize(reader, (cazcade.liquid.api.LiquidSessionIdentifier)object);
  }
  
  public void serial(SerializationStreamWriter writer, Object object) throws SerializationException {
    cazcade.liquid.api.LiquidSessionIdentifier_FieldSerializer.serialize(writer, (cazcade.liquid.api.LiquidSessionIdentifier)object);
  }
  
}
