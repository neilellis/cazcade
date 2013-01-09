package cazcade.liquid.api.request;

import com.google.gwt.user.client.rpc.SerializationException;
import com.google.gwt.user.client.rpc.SerializationStreamReader;
import com.google.gwt.user.client.rpc.SerializationStreamWriter;
import com.google.gwt.user.client.rpc.impl.ReflectionHelper;

@SuppressWarnings("deprecation")
public class SerializedRequest_FieldSerializer implements com.google.gwt.user.client.rpc.impl.TypeHandler {
  private static native java.util.HashMap getEntity(cazcade.liquid.api.request.SerializedRequest instance) /*-{
    return instance.@cazcade.liquid.api.request.SerializedRequest::entity;
  }-*/;
  
  private static native void setEntity(cazcade.liquid.api.request.SerializedRequest instance, java.util.HashMap value) 
  /*-{
    instance.@cazcade.liquid.api.request.SerializedRequest::entity = value;
  }-*/;
  
  private static native java.lang.String getType(cazcade.liquid.api.request.SerializedRequest instance) /*-{
    return instance.@cazcade.liquid.api.request.SerializedRequest::type;
  }-*/;
  
  private static native void setType(cazcade.liquid.api.request.SerializedRequest instance, java.lang.String value) 
  /*-{
    instance.@cazcade.liquid.api.request.SerializedRequest::type = value;
  }-*/;
  
  public static void deserialize(SerializationStreamReader streamReader, cazcade.liquid.api.request.SerializedRequest instance) throws SerializationException {
    setEntity(instance, (java.util.HashMap) streamReader.readObject());
    setType(instance, streamReader.readString());
    
  }
  
  public static cazcade.liquid.api.request.SerializedRequest instantiate(SerializationStreamReader streamReader) throws SerializationException {
    return new cazcade.liquid.api.request.SerializedRequest();
  }
  
  public static void serialize(SerializationStreamWriter streamWriter, cazcade.liquid.api.request.SerializedRequest instance) throws SerializationException {
    streamWriter.writeObject(getEntity(instance));
    streamWriter.writeString(getType(instance));
    
  }
  
  public Object create(SerializationStreamReader reader) throws SerializationException {
    return cazcade.liquid.api.request.SerializedRequest_FieldSerializer.instantiate(reader);
  }
  
  public void deserial(SerializationStreamReader reader, Object object) throws SerializationException {
    cazcade.liquid.api.request.SerializedRequest_FieldSerializer.deserialize(reader, (cazcade.liquid.api.request.SerializedRequest)object);
  }
  
  public void serial(SerializationStreamWriter writer, Object object) throws SerializationException {
    cazcade.liquid.api.request.SerializedRequest_FieldSerializer.serialize(writer, (cazcade.liquid.api.request.SerializedRequest)object);
  }
  
}
