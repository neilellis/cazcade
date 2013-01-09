package cazcade.liquid.api.lsd;

import com.google.gwt.user.client.rpc.SerializationException;
import com.google.gwt.user.client.rpc.SerializationStreamReader;
import com.google.gwt.user.client.rpc.SerializationStreamWriter;
import com.google.gwt.user.client.rpc.impl.ReflectionHelper;

@SuppressWarnings("deprecation")
public class LSDDictionaryTypes_FieldSerializer implements com.google.gwt.user.client.rpc.impl.TypeHandler {
  private static native java.lang.String getDescription(cazcade.liquid.api.lsd.LSDDictionaryTypes instance) /*-{
    return instance.@cazcade.liquid.api.lsd.LSDDictionaryTypes::description;
  }-*/;
  
  private static native void setDescription(cazcade.liquid.api.lsd.LSDDictionaryTypes instance, java.lang.String value) 
  /*-{
    instance.@cazcade.liquid.api.lsd.LSDDictionaryTypes::description = value;
  }-*/;
  
  public static void deserialize(SerializationStreamReader streamReader, cazcade.liquid.api.lsd.LSDDictionaryTypes instance) throws SerializationException {
    // Enum deserialization is handled via the instantiate method
  }
  
  public static cazcade.liquid.api.lsd.LSDDictionaryTypes instantiate(SerializationStreamReader streamReader) throws SerializationException {
    int ordinal = streamReader.readInt();
    cazcade.liquid.api.lsd.LSDDictionaryTypes[] values = cazcade.liquid.api.lsd.LSDDictionaryTypes.values();
    assert (ordinal >= 0 && ordinal < values.length);
    return values[ordinal];
  }
  
  public static void serialize(SerializationStreamWriter streamWriter, cazcade.liquid.api.lsd.LSDDictionaryTypes instance) throws SerializationException {
    assert (instance != null);
    streamWriter.writeInt(instance.ordinal());
  }
  
  public Object create(SerializationStreamReader reader) throws SerializationException {
    return cazcade.liquid.api.lsd.LSDDictionaryTypes_FieldSerializer.instantiate(reader);
  }
  
  public void deserial(SerializationStreamReader reader, Object object) throws SerializationException {
    cazcade.liquid.api.lsd.LSDDictionaryTypes_FieldSerializer.deserialize(reader, (cazcade.liquid.api.lsd.LSDDictionaryTypes)object);
  }
  
  public void serial(SerializationStreamWriter writer, Object object) throws SerializationException {
    cazcade.liquid.api.lsd.LSDDictionaryTypes_FieldSerializer.serialize(writer, (cazcade.liquid.api.lsd.LSDDictionaryTypes)object);
  }
  
}
