package cazcade.liquid.api;

import com.google.gwt.user.client.rpc.SerializationException;
import com.google.gwt.user.client.rpc.SerializationStreamReader;
import com.google.gwt.user.client.rpc.SerializationStreamWriter;
import com.google.gwt.user.client.rpc.impl.ReflectionHelper;

@SuppressWarnings("deprecation")
public class LiquidURI_FieldSerializer implements com.google.gwt.user.client.rpc.impl.TypeHandler {
  public static void deserialize(SerializationStreamReader streamReader, cazcade.liquid.api.LiquidURI instance) throws SerializationException {
    instance.uri = streamReader.readString();
    
  }
  
  public static cazcade.liquid.api.LiquidURI instantiate(SerializationStreamReader streamReader) throws SerializationException {
    return new cazcade.liquid.api.LiquidURI();
  }
  
  public static void serialize(SerializationStreamWriter streamWriter, cazcade.liquid.api.LiquidURI instance) throws SerializationException {
    streamWriter.writeString(instance.uri);
    
  }
  
  public Object create(SerializationStreamReader reader) throws SerializationException {
    return cazcade.liquid.api.LiquidURI_FieldSerializer.instantiate(reader);
  }
  
  public void deserial(SerializationStreamReader reader, Object object) throws SerializationException {
    cazcade.liquid.api.LiquidURI_FieldSerializer.deserialize(reader, (cazcade.liquid.api.LiquidURI)object);
  }
  
  public void serial(SerializationStreamWriter writer, Object object) throws SerializationException {
    cazcade.liquid.api.LiquidURI_FieldSerializer.serialize(writer, (cazcade.liquid.api.LiquidURI)object);
  }
  
}
