/*
 * Copyright (c) 2009-2013 Cazcade Limited  - All Rights Reserved
 */

package cazcade.vortex.pool.objects.microblog;

import cazcade.vortex.pool.objects.PoolObjectView;
import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.HeadingElement;
import com.google.gwt.dom.client.ImageElement;
import com.google.gwt.dom.client.SpanElement;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.HTMLPanel;

/**
 * @author neilellis@cazcade.com
 */
public class MicroBlogView extends PoolObjectView {
    interface MicroBlogViewUiBinder extends UiBinder<HTMLPanel, MicroBlogView> {}

    private static final MicroBlogViewUiBinder ourUiBinder = GWT.create(MicroBlogViewUiBinder.class);
    @UiField ImageElement   image;
    @UiField HeadingElement title;
    @UiField SpanElement    shortText;

    public MicroBlogView() {
        super();
        initWidget(ourUiBinder.createAndBindUi(this));
        setLogicalWidth(300);
        setLogicalHeight(100);
    }

    @Override public int getDefaultZIndex() {
        return 1000;
    }

    public void setProfileImage(final String url) {
        image.setSrc(url);
    }

    public void setMicroBlogTitle(final String title) {
        this.title.setInnerText(title);
    }

    public void setMicroBlogShortText(final String text) {
        shortText.setInnerText(text);
    }
}