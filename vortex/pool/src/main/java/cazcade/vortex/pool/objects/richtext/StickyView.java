/*
 * Copyright (c) 2009-2013 Cazcade Limited  - All Rights Reserved
 */

package cazcade.vortex.pool.objects.richtext;

import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.user.client.ui.HTMLPanel;

/**
 * @author neilellis@cazcade.com
 */
public class StickyView extends RichTextView {

    protected static final StickyViewUiBinder ourUiBinder = GWT.create(StickyViewUiBinder.class);

    interface StickyViewUiBinder extends UiBinder<HTMLPanel, RichTextView> {}

    public StickyView() {
        super();
        initWidget(ourUiBinder.createAndBindUi(this));
    }


    @Override public int getDefaultZIndex() {
        return 5000;
    }
}