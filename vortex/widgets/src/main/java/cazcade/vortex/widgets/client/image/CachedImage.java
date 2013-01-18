/*
 * Copyright (c) 2009-2013 Cazcade Limited  - All Rights Reserved
 */

package cazcade.vortex.widgets.client.image;

import cazcade.vortex.dnd.client.browser.BrowserUtil;
import cazcade.vortex.gwt.util.client.WidgetUtil;
import com.google.gwt.event.dom.client.ErrorEvent;
import com.google.gwt.event.dom.client.ErrorHandler;
import com.google.gwt.event.dom.client.LoadEvent;
import com.google.gwt.event.dom.client.LoadHandler;
import com.google.gwt.http.client.URL;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.ui.Image;

import javax.annotation.Nonnull;

/**
 * @author neilellis@cazcade.com
 */
public class CachedImage extends Image {
    @Nonnull
    public static final String  PROFILE_SMALL = "PROFILE_SMALL";
    @Nonnull
    public static final String  SMALL         = "CLIPPED_SMALL";
    @Nonnull
    public static final String  MEDIUM        = "CLIPPED_MEDIUM";
    @Nonnull
    public static final String  CLIPPED_LARGE = "CLIPPED_LARGE";
    @Nonnull
    public static final String  LARGE         = "LARGE";
    public static final boolean CACHING       = true;
    private Runnable onChangeAction;
    private String size = SMALL;
    private String defaultUrl;
    private String url;
    private String  notReadyText = "Loading";
    private boolean cached       = true;
    private int requestedWidth;
    private int requestedHeight;
    private boolean ready = false;
    private boolean website;


    public CachedImage(final String url, final String size) {
        this();
        setSize(size);
        setUrl(url);
        addErrorHandler(new ErrorHandler() {
            @Override public void onError(ErrorEvent event) {
                setUrl(defaultDefaultMessage("Image Failed"));
            }
        });
        addLoadHandler(new LoadHandler() {
            @Override public void onLoad(LoadEvent event) {
                ready = true;
            }
        });
    }

    public CachedImage(@Nonnull final Image image, final String size) {
        this(image.getUrl(), size);
    }

    public CachedImage() {
        super();
        WidgetUtil.hide(getElement(), false);
        addLoadHandler(new LoadHandler() {
            @Override
            public void onLoad(final LoadEvent event) {
                WidgetUtil.showGracefully(getElement(), false);
            }
        });

    }

    public CachedImage(@Nonnull final Image image) {
        this();
        setUrl(image.getUrl());
    }

    @Override
    protected void onAttach() {
        super.onAttach();
        if (getUrl() == null || getUrl().isEmpty()) {
            super.setUrl(placeholderImage());
        }
        setUrl(url);
    }

    @Nonnull
    private String placeholderImage() {
        return defaultDefaultMessage(notReadyText);
    }

    public void setDefaultMessage(final String message) {
        defaultUrl = defaultDefaultMessage(message);
    }

    @Nonnull
    private String defaultDefaultMessage(final String message) {
        return "http://placehold.it/" + getWidthWithDefault() + "x" + getHeightWithDefault() + "&text=" + URL.encode(message);
    }

    @Override
    public void setUrl(final String url) {
        ready = false;
        new Timer() {
            @Override public void run() {
                //In case the load handler has not been triggered.
                WidgetUtil.showGracefully(getElement(), false);

            }
        }.schedule(2000);
        final String oldUrl = this.url;
        this.url = url;
        updateImageUrl();
        if (isAttached() && (oldUrl == null || !oldUrl.equals(url)) && onChangeAction != null) {
            onChangeAction.run();
        }
    }

    @Override
    protected void onLoad() {
        super.onLoad();
        updateImageUrl();

    }

    private void updateImageUrl() {
        String prefix = website ? "./_website-snapshot" : "./_image-cache";

        //        getElement().getStyle().setBackgroundImage(placeholderImage());
        if (url != null && !url.isEmpty()) {
            if (CACHING && cached && !BrowserUtil.isInternalImage(url)) {
                if (url.startsWith("http")) {
                    super.setUrl(prefix + "?url=" +
                                 URL.encode(url) +
                                 "&size=" +
                                 size +
                                 "&width=" +
                                 getWidthWithDefault() +
                                 "&height=" +
                                 getHeightWithDefault());
                }
                else {
                    super.setUrl(prefix + "?url=" +
                                 BrowserUtil.convertRelativeUrlToAbsolute(url) +
                                 "&size=" +
                                 size +
                                 "&width=" +
                                 getWidthWithDefault() +
                                 "&height=" +
                                 getHeightWithDefault());
                }
            }
            else {
                super.setUrl(url);
            }
        }
        else if (defaultUrl != null) {
            super.setUrl(defaultUrl);
        }
        else {
            super.setUrl(defaultDefaultMessage("No Image"));
        }

    }

    private int getWidthWithDefault() {
        if (getOffsetWidth() > 0 && getOffsetWidth() > requestedWidth) {
            return getOffsetWidth();
        }
        else {
            return requestedWidth;
        }
    }

    private int getHeightWithDefault() {
        if (size.equals(LARGE)) {
            return 2048;
        }
        if (getOffsetHeight() > 0 && getOffsetHeight() > requestedHeight) {
            return getOffsetHeight();
        }
        else if (requestedHeight > 0) {
            return requestedHeight;
        }
        else {
            return 2048;
        }
    }

    public void setSize(final String size) {
        this.size = size;
        updateImageUrl();
    }

    public void setDefaultUrl(final String defaultUrl) {
        this.defaultUrl = defaultUrl;
        if (getUrl() == null) {
            super.setUrl(defaultUrl);
        }
    }

    public String getUnCachedUrl() {
        return url;
    }

    public void setOnChangeAction(final Runnable onChangeAction) {
        this.onChangeAction = onChangeAction;
    }

    public void setNotReadyText(final String notReadyText) {
        this.notReadyText = notReadyText;
    }

    public void setCached(final boolean cached) {
        this.cached = cached;
    }

    @Override
    public int getOffsetWidth() {
        return super.getOffsetWidth();
    }

    @Override
    public void setHeight(@Nonnull final String height) {
        super.setHeight(height);
        getElement().getStyle().setProperty("minHeight", height);
        if (height.toLowerCase().endsWith("px")) {
            requestedHeight = Integer.parseInt(height.substring(0, height.length() - 2));
        }
    }

    @Override
    public void setWidth(@Nonnull final String width) {
        super.setWidth(width);
        getElement().getStyle().setProperty("minWidth", width);
        if (width.toLowerCase().endsWith("px")) {
            requestedWidth = Integer.parseInt(width.substring(0, width.length() - 2));
        }
    }

    public void setWebsite(boolean website) {


        this.website = website;
    }
}



