package cazcade.liquid.api.lsd;

import com.google.gwt.user.client.rpc.SerializationException;
import com.google.gwt.user.client.rpc.SerializationStreamReader;
import com.google.gwt.user.client.rpc.SerializationStreamWriter;
import com.google.gwt.user.client.rpc.impl.ReflectionHelper;

@SuppressWarnings("deprecation")
public class LSDTypeImpl_FieldSerializer implements com.google.gwt.user.client.rpc.impl.TypeHandler {
  private static native java.lang.String getFamily(cazcade.liquid.api.lsd.LSDTypeImpl instance) /*-{
    return instance.@cazcade.liquid.api.lsd.LSDTypeImpl::family;
  }-*/;
  
  private static native void setFamily(cazcade.liquid.api.lsd.LSDTypeImpl instance, java.lang.String value) 
  /*-{
    instance.@cazcade.liquid.api.lsd.LSDTypeImpl::family = value;
  }-*/;
  
  private static native java.lang.String getGenus(cazcade.liquid.api.lsd.LSDTypeImpl instance) /*-{
    return instance.@cazcade.liquid.api.lsd.LSDTypeImpl::genus;
  }-*/;
  
  private static native void setGenus(cazcade.liquid.api.lsd.LSDTypeImpl instance, java.lang.String value) 
  /*-{
    instance.@cazcade.liquid.api.lsd.LSDTypeImpl::genus = value;
  }-*/;
  
  private static native java.lang.String getTypeClass(cazcade.liquid.api.lsd.LSDTypeImpl instance) /*-{
    return instance.@cazcade.liquid.api.lsd.LSDTypeImpl::typeClass;
  }-*/;
  
  private static native void setTypeClass(cazcade.liquid.api.lsd.LSDTypeImpl instance, java.lang.String value) 
  /*-{
    instance.@cazcade.liquid.api.lsd.LSDTypeImpl::typeClass = value;
  }-*/;
  
  private static native java.lang.String getTypeString(cazcade.liquid.api.lsd.LSDTypeImpl instance) /*-{
    return instance.@cazcade.liquid.api.lsd.LSDTypeImpl::typeString;
  }-*/;
  
  private static native void setTypeString(cazcade.liquid.api.lsd.LSDTypeImpl instance, java.lang.String value) 
  /*-{
    instance.@cazcade.liquid.api.lsd.LSDTypeImpl::typeString = value;
  }-*/;
  
  public static void deserialize(SerializationStreamReader streamReader, cazcade.liquid.api.lsd.LSDTypeImpl instance) throws SerializationException {
    setFamily(instance, streamReader.readString());
    setGenus(instance, streamReader.readString());
    setTypeClass(instance, streamReader.readString());
    setTypeString(instance, streamReader.readString());
    
  }
  
  public static cazcade.liquid.api.lsd.LSDTypeImpl instantiate(SerializationStreamReader streamReader) throws SerializationException {
    return new cazcade.liquid.api.lsd.LSDTypeImpl();
  }
  
  public static void serialize(SerializationStreamWriter streamWriter, cazcade.liquid.api.lsd.LSDTypeImpl instance) throws SerializationException {
    streamWriter.writeString(getFamily(instance));
    streamWriter.writeString(getGenus(instance));
    streamWriter.writeString(getTypeClass(instance));
    streamWriter.writeString(getTypeString(instance));
    
  }
  
  public Object create(SerializationStreamReader reader) throws SerializationException {
    return cazcade.liquid.api.lsd.LSDTypeImpl_FieldSerializer.instantiate(reader);
  }
  
  public void deserial(SerializationStreamReader reader, Object object) throws SerializationException {
    cazcade.liquid.api.lsd.LSDTypeImpl_FieldSerializer.deserialize(reader, (cazcade.liquid.api.lsd.LSDTypeImpl)object);
  }
  
  public void serial(SerializationStreamWriter writer, Object object) throws SerializationException {
    cazcade.liquid.api.lsd.LSDTypeImpl_FieldSerializer.serialize(writer, (cazcade.liquid.api.lsd.LSDTypeImpl)object);
  }
  
}
