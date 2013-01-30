/*
 * Copyright (c) 2009-2013 Cazcade Limited  - All Rights Reserved
 */

package cazcade.vortex.pool.objects.website;

import cazcade.vortex.pool.objects.PoolObjectView;
import cazcade.vortex.widgets.client.image.CachedImage;
import cazcade.vortex.widgets.client.spinner.Spinner;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.HTMLPanel;

/**
 * @author neilellis@cazcade.com
 */
public class WebsiteView extends PoolObjectView {


    interface ImageObjectUiBinder extends UiBinder<HTMLPanel, WebsiteView> {}

    private static final ImageObjectUiBinder ourUiBinder = GWT.create(ImageObjectUiBinder.class);
    @UiField CachedImage website;
    @UiField HTMLPanel   websiteSurround;
    private  String      url;


    public WebsiteView() {
        super();
        final HTMLPanel widget = ourUiBinder.createAndBindUi(this);
        initWidget(widget);
        website.setSpinner(new Spinner(websiteSurround));
        addClickHandler(new ClickHandler() {
            @Override
            public void onClick(final ClickEvent event) {
                if (!isEditable()) {
                    Window.open(url, "website", "");
                }
            }
        });

    }

    public void setUrl(final String url) {
        website.setUrl(url);
        website.setTitle("Click to visit " + url);
        this.url = url;
    }

    @Override
    protected void onLoad() {
        super.onLoad();
    }

    @Override
    public void setLogicalWidth(final int width) {
        super.setLogicalWidth(width);
        //        image.setWidth(width + "px");
    }

    @Override
    public void setLogicalHeight(final int height) {
        super.setLogicalHeight(height);
        //        image.setHeight(height + "px");
    }

    @Override
    public void onAddToPool() {
        super.onAddToPool();
        if (size != null) {
            if (size.equals(THUMBNAIL)) {
                website.setSize(CachedImage.SMALL);
                website.setWidth(THUMBNAIL_WIDTH + "px");
                website.setHeight((int) (THUMBNAIL_WIDTH * ROOT_2_RATIO) + "px");
            }

            if (size.equals(SMALL)) {
                website.setSize(CachedImage.MEDIUM);
                website.setWidth(SMALL_WIDTH + "px");
                website.setHeight((int) (SMALL_WIDTH * ROOT_2_RATIO) + "px");
            }
            if (size.equals(MEDIUM)) {
                website.setSize(CachedImage.CLIPPED_LARGE);
                website.setWidth(MEDIUM_WIDTH + "px");
                website.setHeight((int) (MEDIUM_WIDTH * ROOT_2_RATIO) + "px");
            }
            if (size.equals(LARGE)) {
                website.setSize(CachedImage.LARGE);
                website.setWidth(LARGE_WIDTH + "px");
                //                image.setHeight(LARGE_HEIGHT + "px");
            }
        }
    }

    @Override public int getDefaultZIndex() {
        return 1000;
    }


}