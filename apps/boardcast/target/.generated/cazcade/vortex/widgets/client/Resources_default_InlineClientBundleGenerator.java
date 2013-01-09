package cazcade.vortex.widgets.client;

import com.google.gwt.core.client.GWT;
import com.google.gwt.resources.client.ResourcePrototype;

public class Resources_default_InlineClientBundleGenerator implements cazcade.vortex.widgets.client.Resources {
  private static Resources_default_InlineClientBundleGenerator _instance0 = new Resources_default_InlineClientBundleGenerator();
  private void blankInitializer() {
    blank = new com.google.gwt.resources.client.impl.ImageResourcePrototype(
      "blank",
      com.google.gwt.safehtml.shared.UriUtils.fromTrustedString(externalImage),
      0, 0, 1, 1, false, false
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
      com.google.gwt.safehtml.shared.UriUtils.fromTrustedString(externalImage0),
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
      com.google.gwt.safehtml.shared.UriUtils.fromTrustedString(externalImage1),
      0, 0, 16, 12, false, false
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
      com.google.gwt.safehtml.shared.UriUtils.fromTrustedString(externalImage2),
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
      com.google.gwt.safehtml.shared.UriUtils.fromTrustedString(externalImage3),
      0, 0, 16, 12, false, false
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
  private static final java.lang.String externalImage = "data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAAAEAAAABCAYAAAAfFcSJAAAAC0lEQVR42mNgAAIAAAUAAen63NgAAAAASUVORK5CYII=";
  private static final java.lang.String externalImage0 = "data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAABAAAAAQCAYAAAAf8/9hAAABoklEQVR42q2TTU4CQRCFJ8boAfz3GHoedGPcuUKXxovgUhayJ7hiReRvZoCZgZC4AG9gXAAJML6v021aYlzZSaWnut57VV3VEwQbq9lsHnW73RvZa6fT+cD45oxY8NdqtVpXURSNe71e3u/388FgYIxvzoiB+ZWsDPdhGK6TJMm15/IzZX+xlsqfIwQG7CaZzEuyCfwu/9IvV/4J2eM4NuJg4ZignFORJmTWPhXg3Bev1+vbOi9JfKUdsrmOBCZwUS+irMOlABc+OU3THcWesixD3BC1v8kWVqSIQMjduLNs35HzPN+SX3Hk4XDIXlLWA3piOVEgZ8bd5dQcuVqt7gr47MjsipcRJU5jbb/m3wIcepnLPhkxRL2m1ixnhkBMOZRlyyvZcl3misvMGo1Ge+12O7OcOJBza5uzoEH225BpII30G6skBRoOThO7c2Oc2pdmyHZkj4xw472cgXUjN2NkqYprZVvySBipgGOdHTtio9E4FKEAibuDhfPjNSr4wDO145nLTwSqYfQHcfvM12D//2dyi9K93/kTc7+zfy23vgB8SVTWEiDISQAAAABJRU5ErkJggg==";
  private static final java.lang.String externalImage1 = "data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAABAAAAAMCAYAAABr5z2BAAABB0lEQVR42mNgIAOcOXNG9+zZs07Xr18XJknjqVOnDICaFwJxz5UrVySI1vj48WNOoKaaCxcufADSE4CYlRRbZc+dO7f15s2b/4HOnvL//39GDEUgwZMnTwrj8OslkObTp0+vATqbDatmoMJyoC3XgIpcYeJAjXpA8dvXrl37D6SvXrx4UQxXiNaeP3/+/6VLl/4DDfkM1JgKxJpA8VuXL18Gaf4ExDY4/QjUZAnUcBqkGMgGOfU3ED8HsYG2ggyoISZeJYEatoMMARr2H6YZGIB7gXJcRIX2kSNHeIGKl4A0ggwAGvQZ6DVTkhIKKJSBzu8HuuQPkO4iJ5WCYwWouQHofCVi1AMAt0jbAHnnD/QAAAAASUVORK5CYII=";
  private static final java.lang.String externalImage2 = "data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAABAAAAAQCAYAAAAf8/9hAAABoklEQVR42q2TTU4CQRCFJ8boAfz3GHoedGPcuUKXxovgUhayJ7hiReRvZoCZgZC4AG9gXAAJML6v021aYlzZSaWnut57VV3VEwQbq9lsHnW73RvZa6fT+cD45oxY8NdqtVpXURSNe71e3u/388FgYIxvzoiB+ZWsDPdhGK6TJMm15/IzZX+xlsqfIwQG7CaZzEuyCfwu/9IvV/4J2eM4NuJg4ZignFORJmTWPhXg3Bev1+vbOi9JfKUdsrmOBCZwUS+irMOlABc+OU3THcWesixD3BC1v8kWVqSIQMjduLNs35HzPN+SX3Hk4XDIXlLWA3piOVEgZ8bd5dQcuVqt7gr47MjsipcRJU5jbb/m3wIcepnLPhkxRL2m1ixnhkBMOZRlyyvZcl3misvMGo1Ge+12O7OcOJBza5uzoEH225BpII30G6skBRoOThO7c2Oc2pdmyHZkj4xw472cgXUjN2NkqYprZVvySBipgGOdHTtio9E4FKEAibuDhfPjNSr4wDO145nLTwSqYfQHcfvM12D//2dyi9K93/kTc7+zfy23vgB8SVTWEiDISQAAAABJRU5ErkJggg==";
  private static final java.lang.String externalImage3 = "data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAABAAAAAMCAYAAABr5z2BAAABB0lEQVR42mNgIAOcOXNG9+zZs07Xr18XJknjqVOnDICaFwJxz5UrVySI1vj48WNOoKaaCxcufADSE4CYlRRbZc+dO7f15s2b/4HOnvL//39GDEUgwZMnTwrj8OslkObTp0+vATqbDatmoMJyoC3XgIpcYeJAjXpA8dvXrl37D6SvXrx4UQxXiNaeP3/+/6VLl/4DDfkM1JgKxJpA8VuXL18Gaf4ExDY4/QjUZAnUcBqkGMgGOfU3ED8HsYG2ggyoISZeJYEatoMMARr2H6YZGIB7gXJcRIX2kSNHeIGKl4A0ggwAGvQZ6DVTkhIKKJSBzu8HuuQPkO4iJ5WCYwWouQHofCVi1AMAt0jbAHnnD/QAAAAASUVORK5CYII=";
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
