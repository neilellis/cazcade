package cazcade.boardcast.client.resources;

import com.google.gwt.core.client.GWT;
import com.google.gwt.resources.client.ClientBundle;
import com.google.gwt.resources.client.CssResource;

import javax.annotation.Nonnull;


public interface HashboClientBundle extends ClientBundle {

    @Nonnull
    @Source("hashbo.css")
    @CssResource.NotStrict
    public CssResource css();

    public static final HashboClientBundle INSTANCE = GWT.create(HashboClientBundle.class);
}
