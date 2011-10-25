package cazcade.hashbo.client.resources;

import com.google.gwt.core.client.GWT;
import com.google.gwt.resources.client.ClientBundle;
import com.google.gwt.resources.client.CssResource;


public interface HashboClientBundle extends ClientBundle {

    @Source("hashbo.css")
    @CssResource.NotStrict
    public CssResource css();

    public static final HashboClientBundle INSTANCE =  GWT.create(HashboClientBundle.class);
}
