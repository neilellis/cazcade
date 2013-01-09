package cazcade.liquid.api;

import com.google.gwt.user.client.rpc.SerializationException;
import com.google.gwt.user.client.rpc.SerializationStreamReader;
import com.google.gwt.user.client.rpc.SerializationStreamWriter;
import com.google.gwt.user.client.rpc.impl.ReflectionHelper;

@SuppressWarnings("deprecation")
public class LiquidUUID_FieldSerializer implements com.google.gwt.user.client.rpc.impl.TypeHandler {
  private static native java.lang.String getS(cazcade.liquid.api.LiquidUUID instance) /*-{
    return instance.@cazcade.liquid.api.LiquidUUID::s;
  }-*/;
  
  private static native void setS(cazcade.liquid.api.LiquidUUID instance, java.lang.String value) 
  /*-{
    instance.@cazcade.liquid.api.LiquidUUID::s = value;
  }-*/;
  
  public static void deserialize(SerializationStreamReader streamReader, cazcade.liquid.api.LiquidUUID instance) throws SerializationException {
    setS(instance, streamReader.readString());
    
  }
  
  public static cazcade.liquid.api.LiquidUUID instantiate(SerializationStreamReader streamReader) throws SerializationException {
    return new cazcade.liquid.api.LiquidUUID();
  }
  
  public static void serialize(SerializationStreamWriter streamWriter, cazcade.liquid.api.LiquidUUID instance) throws SerializationException {
    streamWriter.writeString(getS(instance));
    
  }
  
  public Object create(SerializationStreamReader reader) throws SerializationException {
    return cazcade.liquid.api.LiquidUUID_FieldSerializer.instantiate(reader);
  }
  
  public void deserial(SerializationStreamReader reader, Object object) throws SerializationException {
    cazcade.liquid.api.LiquidUUID_FieldSerializer.deserialize(reader, (cazcade.liquid.api.LiquidUUID)object);
  }
  
  public void serial(SerializationStreamWriter writer, Object object) throws SerializationException {
    cazcade.liquid.api.LiquidUUID_FieldSerializer.serialize(writer, (cazcade.liquid.api.LiquidUUID)object);
  }
  
}
