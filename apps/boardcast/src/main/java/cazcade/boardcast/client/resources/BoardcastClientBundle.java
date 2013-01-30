/*
 * Copyright (c) 2009-2013 Cazcade Limited  - All Rights Reserved
 */

package cazcade.boardcast.client.resources;

import com.google.gwt.core.client.GWT;
import com.google.gwt.resources.client.ClientBundle;
import com.google.gwt.resources.client.CssResource;

import javax.annotation.Nonnull;


public interface BoardcastClientBundle extends ClientBundle {

    @Nonnull @Source("boardcast.css") @CssResource.NotStrict CssResource css();

    BoardcastClientBundle INSTANCE = GWT.create(BoardcastClientBundle.class);
}
