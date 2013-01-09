package cazcade.vortex.common.client.error;

import com.google.gwt.user.client.rpc.SerializationException;
import com.google.gwt.user.client.rpc.SerializationStreamReader;
import com.google.gwt.user.client.rpc.SerializationStreamWriter;
import com.google.gwt.user.client.rpc.impl.ReflectionHelper;

@SuppressWarnings("deprecation")
public class UnrecognizedPoolEntityException_FieldSerializer implements com.google.gwt.user.client.rpc.impl.TypeHandler {
  public static void deserialize(SerializationStreamReader streamReader, cazcade.vortex.common.client.error.UnrecognizedPoolEntityException instance) throws SerializationException {
    
    com.google.gwt.user.client.rpc.core.java.lang.RuntimeException_FieldSerializer.deserialize(streamReader, instance);
  }
  
  public static cazcade.vortex.common.client.error.UnrecognizedPoolEntityException instantiate(SerializationStreamReader streamReader) throws SerializationException {
    return new cazcade.vortex.common.client.error.UnrecognizedPoolEntityException();
  }
  
  public static void serialize(SerializationStreamWriter streamWriter, cazcade.vortex.common.client.error.UnrecognizedPoolEntityException instance) throws SerializationException {
    
    com.google.gwt.user.client.rpc.core.java.lang.RuntimeException_FieldSerializer.serialize(streamWriter, instance);
  }
  
  public Object create(SerializationStreamReader reader) throws SerializationException {
    return cazcade.vortex.common.client.error.UnrecognizedPoolEntityException_FieldSerializer.instantiate(reader);
  }
  
  public void deserial(SerializationStreamReader reader, Object object) throws SerializationException {
    cazcade.vortex.common.client.error.UnrecognizedPoolEntityException_FieldSerializer.deserialize(reader, (cazcade.vortex.common.client.error.UnrecognizedPoolEntityException)object);
  }
  
  public void serial(SerializationStreamWriter writer, Object object) throws SerializationException {
    cazcade.vortex.common.client.error.UnrecognizedPoolEntityException_FieldSerializer.serialize(writer, (cazcade.vortex.common.client.error.UnrecognizedPoolEntityException)object);
  }
  
}
