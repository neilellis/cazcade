package cazcade.liquid.api.lsd;

import com.google.gwt.user.client.rpc.SerializationException;
import com.google.gwt.user.client.rpc.SerializationStreamReader;
import com.google.gwt.user.client.rpc.SerializationStreamWriter;
import com.google.gwt.user.client.rpc.impl.ReflectionHelper;

@SuppressWarnings("deprecation")
public class LSDMapPropertyStore_FieldSerializer implements com.google.gwt.user.client.rpc.impl.TypeHandler {
  public static void deserialize(SerializationStreamReader streamReader, cazcade.liquid.api.lsd.LSDMapPropertyStore instance) throws SerializationException {
    
  }
  
  public static cazcade.liquid.api.lsd.LSDMapPropertyStore instantiate(SerializationStreamReader streamReader) throws SerializationException {
    return new cazcade.liquid.api.lsd.LSDMapPropertyStore();
  }
  
  public static void serialize(SerializationStreamWriter streamWriter, cazcade.liquid.api.lsd.LSDMapPropertyStore instance) throws SerializationException {
    
  }
  
  public Object create(SerializationStreamReader reader) throws SerializationException {
    return cazcade.liquid.api.lsd.LSDMapPropertyStore_FieldSerializer.instantiate(reader);
  }
  
  public void deserial(SerializationStreamReader reader, Object object) throws SerializationException {
    cazcade.liquid.api.lsd.LSDMapPropertyStore_FieldSerializer.deserialize(reader, (cazcade.liquid.api.lsd.LSDMapPropertyStore)object);
  }
  
  public void serial(SerializationStreamWriter writer, Object object) throws SerializationException {
    cazcade.liquid.api.lsd.LSDMapPropertyStore_FieldSerializer.serialize(writer, (cazcade.liquid.api.lsd.LSDMapPropertyStore)object);
  }
  
}
