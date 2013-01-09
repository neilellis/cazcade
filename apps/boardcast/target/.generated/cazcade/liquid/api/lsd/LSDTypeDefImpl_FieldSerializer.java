package cazcade.liquid.api.lsd;

import com.google.gwt.user.client.rpc.SerializationException;
import com.google.gwt.user.client.rpc.SerializationStreamReader;
import com.google.gwt.user.client.rpc.SerializationStreamWriter;
import com.google.gwt.user.client.rpc.impl.ReflectionHelper;

@SuppressWarnings("deprecation")
public class LSDTypeDefImpl_FieldSerializer implements com.google.gwt.user.client.rpc.impl.TypeHandler {
  private static native cazcade.liquid.api.lsd.LSDType getPrimaryType(cazcade.liquid.api.lsd.LSDTypeDefImpl instance) /*-{
    return instance.@cazcade.liquid.api.lsd.LSDTypeDefImpl::primaryType;
  }-*/;
  
  private static native void setPrimaryType(cazcade.liquid.api.lsd.LSDTypeDefImpl instance, cazcade.liquid.api.lsd.LSDType value) 
  /*-{
    instance.@cazcade.liquid.api.lsd.LSDTypeDefImpl::primaryType = value;
  }-*/;
  
  private static native java.lang.String getTypeString(cazcade.liquid.api.lsd.LSDTypeDefImpl instance) /*-{
    return instance.@cazcade.liquid.api.lsd.LSDTypeDefImpl::typeString;
  }-*/;
  
  private static native void setTypeString(cazcade.liquid.api.lsd.LSDTypeDefImpl instance, java.lang.String value) 
  /*-{
    instance.@cazcade.liquid.api.lsd.LSDTypeDefImpl::typeString = value;
  }-*/;
  
  public static void deserialize(SerializationStreamReader streamReader, cazcade.liquid.api.lsd.LSDTypeDefImpl instance) throws SerializationException {
    setPrimaryType(instance, (cazcade.liquid.api.lsd.LSDType) streamReader.readObject());
    setTypeString(instance, streamReader.readString());
    
  }
  
  public static cazcade.liquid.api.lsd.LSDTypeDefImpl instantiate(SerializationStreamReader streamReader) throws SerializationException {
    return new cazcade.liquid.api.lsd.LSDTypeDefImpl();
  }
  
  public static void serialize(SerializationStreamWriter streamWriter, cazcade.liquid.api.lsd.LSDTypeDefImpl instance) throws SerializationException {
    streamWriter.writeObject(getPrimaryType(instance));
    streamWriter.writeString(getTypeString(instance));
    
  }
  
  public Object create(SerializationStreamReader reader) throws SerializationException {
    return cazcade.liquid.api.lsd.LSDTypeDefImpl_FieldSerializer.instantiate(reader);
  }
  
  public void deserial(SerializationStreamReader reader, Object object) throws SerializationException {
    cazcade.liquid.api.lsd.LSDTypeDefImpl_FieldSerializer.deserialize(reader, (cazcade.liquid.api.lsd.LSDTypeDefImpl)object);
  }
  
  public void serial(SerializationStreamWriter writer, Object object) throws SerializationException {
    cazcade.liquid.api.lsd.LSDTypeDefImpl_FieldSerializer.serialize(writer, (cazcade.liquid.api.lsd.LSDTypeDefImpl)object);
  }
  
}
