package cazcade.vortex.comms.datastore.client;

import com.google.gwt.user.client.rpc.SerializationException;
import com.google.gwt.user.client.rpc.SerializationStreamReader;
import com.google.gwt.user.client.rpc.SerializationStreamWriter;
import com.google.gwt.user.client.rpc.impl.ReflectionHelper;

@SuppressWarnings("deprecation")
public class LoggedOutException_FieldSerializer implements com.google.gwt.user.client.rpc.impl.TypeHandler {
  public static void deserialize(SerializationStreamReader streamReader, cazcade.vortex.comms.datastore.client.LoggedOutException instance) throws SerializationException {
    
    com.google.gwt.user.client.rpc.core.java.lang.RuntimeException_FieldSerializer.deserialize(streamReader, instance);
  }
  
  public static cazcade.vortex.comms.datastore.client.LoggedOutException instantiate(SerializationStreamReader streamReader) throws SerializationException {
    return new cazcade.vortex.comms.datastore.client.LoggedOutException();
  }
  
  public static void serialize(SerializationStreamWriter streamWriter, cazcade.vortex.comms.datastore.client.LoggedOutException instance) throws SerializationException {
    
    com.google.gwt.user.client.rpc.core.java.lang.RuntimeException_FieldSerializer.serialize(streamWriter, instance);
  }
  
  public Object create(SerializationStreamReader reader) throws SerializationException {
    return cazcade.vortex.comms.datastore.client.LoggedOutException_FieldSerializer.instantiate(reader);
  }
  
  public void deserial(SerializationStreamReader reader, Object object) throws SerializationException {
    cazcade.vortex.comms.datastore.client.LoggedOutException_FieldSerializer.deserialize(reader, (cazcade.vortex.comms.datastore.client.LoggedOutException)object);
  }
  
  public void serial(SerializationStreamWriter writer, Object object) throws SerializationException {
    cazcade.vortex.comms.datastore.client.LoggedOutException_FieldSerializer.serialize(writer, (cazcade.vortex.comms.datastore.client.LoggedOutException)object);
  }
  
}
