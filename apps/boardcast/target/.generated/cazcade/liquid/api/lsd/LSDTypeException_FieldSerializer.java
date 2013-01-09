package cazcade.liquid.api.lsd;

import com.google.gwt.user.client.rpc.SerializationException;
import com.google.gwt.user.client.rpc.SerializationStreamReader;
import com.google.gwt.user.client.rpc.SerializationStreamWriter;
import com.google.gwt.user.client.rpc.impl.ReflectionHelper;

@SuppressWarnings("deprecation")
public class LSDTypeException_FieldSerializer implements com.google.gwt.user.client.rpc.impl.TypeHandler {
  public static void deserialize(SerializationStreamReader streamReader, cazcade.liquid.api.lsd.LSDTypeException instance) throws SerializationException {
    
    com.google.gwt.user.client.rpc.core.java.lang.RuntimeException_FieldSerializer.deserialize(streamReader, instance);
  }
  
  public static cazcade.liquid.api.lsd.LSDTypeException instantiate(SerializationStreamReader streamReader) throws SerializationException {
    return new cazcade.liquid.api.lsd.LSDTypeException();
  }
  
  public static void serialize(SerializationStreamWriter streamWriter, cazcade.liquid.api.lsd.LSDTypeException instance) throws SerializationException {
    
    com.google.gwt.user.client.rpc.core.java.lang.RuntimeException_FieldSerializer.serialize(streamWriter, instance);
  }
  
  public Object create(SerializationStreamReader reader) throws SerializationException {
    return cazcade.liquid.api.lsd.LSDTypeException_FieldSerializer.instantiate(reader);
  }
  
  public void deserial(SerializationStreamReader reader, Object object) throws SerializationException {
    cazcade.liquid.api.lsd.LSDTypeException_FieldSerializer.deserialize(reader, (cazcade.liquid.api.lsd.LSDTypeException)object);
  }
  
  public void serial(SerializationStreamWriter writer, Object object) throws SerializationException {
    cazcade.liquid.api.lsd.LSDTypeException_FieldSerializer.serialize(writer, (cazcade.liquid.api.lsd.LSDTypeException)object);
  }
  
}
