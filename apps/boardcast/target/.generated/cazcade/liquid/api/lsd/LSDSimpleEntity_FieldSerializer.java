package cazcade.liquid.api.lsd;

import com.google.gwt.user.client.rpc.SerializationException;
import com.google.gwt.user.client.rpc.SerializationStreamReader;
import com.google.gwt.user.client.rpc.SerializationStreamWriter;
import com.google.gwt.user.client.rpc.impl.ReflectionHelper;

@SuppressWarnings("deprecation")
public class LSDSimpleEntity_FieldSerializer implements com.google.gwt.user.client.rpc.impl.TypeHandler {
  private static native boolean getReadonly(cazcade.liquid.api.lsd.LSDSimpleEntity instance) /*-{
    return instance.@cazcade.liquid.api.lsd.LSDSimpleEntity::readonly;
  }-*/;
  
  private static native void setReadonly(cazcade.liquid.api.lsd.LSDSimpleEntity instance, boolean value) 
  /*-{
    instance.@cazcade.liquid.api.lsd.LSDSimpleEntity::readonly = value;
  }-*/;
  
  public static void deserialize(SerializationStreamReader streamReader, cazcade.liquid.api.lsd.LSDSimpleEntity instance) throws SerializationException {
    setReadonly(instance, streamReader.readBoolean());
    
  }
  
  public static cazcade.liquid.api.lsd.LSDSimpleEntity instantiate(SerializationStreamReader streamReader) throws SerializationException {
    return new cazcade.liquid.api.lsd.LSDSimpleEntity();
  }
  
  public static void serialize(SerializationStreamWriter streamWriter, cazcade.liquid.api.lsd.LSDSimpleEntity instance) throws SerializationException {
    streamWriter.writeBoolean(getReadonly(instance));
    
  }
  
  public Object create(SerializationStreamReader reader) throws SerializationException {
    return cazcade.liquid.api.lsd.LSDSimpleEntity_FieldSerializer.instantiate(reader);
  }
  
  public void deserial(SerializationStreamReader reader, Object object) throws SerializationException {
    cazcade.liquid.api.lsd.LSDSimpleEntity_FieldSerializer.deserialize(reader, (cazcade.liquid.api.lsd.LSDSimpleEntity)object);
  }
  
  public void serial(SerializationStreamWriter writer, Object object) throws SerializationException {
    cazcade.liquid.api.lsd.LSDSimpleEntity_FieldSerializer.serialize(writer, (cazcade.liquid.api.lsd.LSDSimpleEntity)object);
  }
  
}
