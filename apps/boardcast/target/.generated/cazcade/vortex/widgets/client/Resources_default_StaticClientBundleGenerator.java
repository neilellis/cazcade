package cazcade.vortex.widgets.client;

import com.google.gwt.core.client.GWT;
import com.google.gwt.resources.client.ResourcePrototype;

public class Resources_default_StaticClientBundleGenerator implements cazcade.vortex.widgets.client.Resources {
  private static Resources_default_StaticClientBundleGenerator _instance0 = new Resources_default_StaticClientBundleGenerator();
  private void blankInitializer() {
    blank = new com.google.gwt.resources.client.impl.ImageResourcePrototype(
      "blank",
      com.google.gwt.safehtml.shared.UriUtils.fromTrustedString(bundledImage_None),
      16, 12, 1, 1, false, false
    );
  }
  private static class blankInitializer {
    static {
      _instance0.blankInitializer();
    }
    static com.google.gwt.resources.client.ImageResource get() {
      return blank;
    }
  }
  public com.google.gwt.resources.client.ImageResource blank() {
    return blankInitializer.get();
  }
  private void invalidFormValueImageInitializer() {
    invalidFormValueImage = new com.google.gwt.resources.client.impl.ImageResourcePrototype(
      "invalidFormValueImage",
      com.google.gwt.safehtml.shared.UriUtils.fromTrustedString(bundledImage_None),
      0, 0, 16, 16, false, false
    );
  }
  private static class invalidFormValueImageInitializer {
    static {
      _instance0.invalidFormValueImageInitializer();
    }
    static com.google.gwt.resources.client.ImageResource get() {
      return invalidFormValueImage;
    }
  }
  public com.google.gwt.resources.client.ImageResource invalidFormValueImage() {
    return invalidFormValueImageInitializer.get();
  }
  private void userAvailableInitializer() {
    userAvailable = new com.google.gwt.resources.client.impl.ImageResourcePrototype(
      "userAvailable",
      com.google.gwt.safehtml.shared.UriUtils.fromTrustedString(bundledImage_None),
      16, 0, 16, 12, false, false
    );
  }
  private static class userAvailableInitializer {
    static {
      _instance0.userAvailableInitializer();
    }
    static com.google.gwt.resources.client.ImageResource get() {
      return userAvailable;
    }
  }
  public com.google.gwt.resources.client.ImageResource userAvailable() {
    return userAvailableInitializer.get();
  }
  private void userNotAvailableInitializer() {
    userNotAvailable = new com.google.gwt.resources.client.impl.ImageResourcePrototype(
      "userNotAvailable",
      com.google.gwt.safehtml.shared.UriUtils.fromTrustedString(bundledImage_None),
      0, 0, 16, 16, false, false
    );
  }
  private static class userNotAvailableInitializer {
    static {
      _instance0.userNotAvailableInitializer();
    }
    static com.google.gwt.resources.client.ImageResource get() {
      return userNotAvailable;
    }
  }
  public com.google.gwt.resources.client.ImageResource userNotAvailable() {
    return userNotAvailableInitializer.get();
  }
  private void validFormValueImageInitializer() {
    validFormValueImage = new com.google.gwt.resources.client.impl.ImageResourcePrototype(
      "validFormValueImage",
      com.google.gwt.safehtml.shared.UriUtils.fromTrustedString(bundledImage_None),
      16, 0, 16, 12, false, false
    );
  }
  private static class validFormValueImageInitializer {
    static {
      _instance0.validFormValueImageInitializer();
    }
    static com.google.gwt.resources.client.ImageResource get() {
      return validFormValueImage;
    }
  }
  public com.google.gwt.resources.client.ImageResource validFormValueImage() {
    return validFormValueImageInitializer.get();
  }
  private static java.util.HashMap<java.lang.String, com.google.gwt.resources.client.ResourcePrototype> resourceMap;
  private static final java.lang.String bundledImage_None = GWT.getModuleBaseForStaticFiles() + "6A50BB345D3FFF91DFD763B7FD7F8137.cache.png";
  private static com.google.gwt.resources.client.ImageResource blank;
  private static com.google.gwt.resources.client.ImageResource invalidFormValueImage;
  private static com.google.gwt.resources.client.ImageResource userAvailable;
  private static com.google.gwt.resources.client.ImageResource userNotAvailable;
  private static com.google.gwt.resources.client.ImageResource validFormValueImage;
  
  public ResourcePrototype[] getResources() {
    return new ResourcePrototype[] {
      blank(), 
      invalidFormValueImage(), 
      userAvailable(), 
      userNotAvailable(), 
      validFormValueImage(), 
    };
  }
  public ResourcePrototype getResource(String name) {
    if (GWT.isScript()) {
      return getResourceNative(name);
    } else {
      if (resourceMap == null) {
        resourceMap = new java.util.HashMap<java.lang.String, com.google.gwt.resources.client.ResourcePrototype>();
        resourceMap.put("blank", blank());
        resourceMap.put("invalidFormValueImage", invalidFormValueImage());
        resourceMap.put("userAvailable", userAvailable());
        resourceMap.put("userNotAvailable", userNotAvailable());
        resourceMap.put("validFormValueImage", validFormValueImage());
      }
      return resourceMap.get(name);
    }
  }
  private native ResourcePrototype getResourceNative(String name) /*-{
    switch (name) {
      case 'blank': return this.@cazcade.vortex.widgets.client.Resources::blank()();
      case 'invalidFormValueImage': return this.@cazcade.vortex.widgets.client.Resources::invalidFormValueImage()();
      case 'userAvailable': return this.@cazcade.vortex.widgets.client.Resources::userAvailable()();
      case 'userNotAvailable': return this.@cazcade.vortex.widgets.client.Resources::userNotAvailable()();
      case 'validFormValueImage': return this.@cazcade.vortex.widgets.client.Resources::validFormValueImage()();
    }
    return null;
  }-*/;
}
