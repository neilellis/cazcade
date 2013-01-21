/*
 * Copyright (c) 2009-2013 Cazcade Limited  - All Rights Reserved
 */

package cazcade.vortex.widgets.client.image;

import cazcade.vortex.dnd.client.browser.BrowserUtil;
import cazcade.vortex.widgets.client.spinner.Spinner;
import com.google.gwt.event.dom.client.ErrorEvent;
import com.google.gwt.event.dom.client.ErrorHandler;
import com.google.gwt.event.dom.client.LoadEvent;
import com.google.gwt.event.dom.client.LoadHandler;
import com.google.gwt.http.client.URL;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.RootPanel;

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
    private Spinner spinner;


    public CachedImage(final String url, final String size) {
        this();
        setSize(size);
        setUrl(url);
    }


    public CachedImage(@Nonnull final Image image, final String size) {
        this(image.getUrl(), size);
    }

    public CachedImage() {
        super();
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
    }

    @Nonnull
    private String placeholderImage() {
        return "./_images/blank.png";
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
        //        WidgetUtil.hideButKeepSize(getElement());
        //        if (isAttached()) {
        //            new Timer() {
        //                @Override public void run() {
        //                    if (!ready && isAttached()) {
        //                        spin();
        //                    }
        //                }
        //            }.schedule(1000);
        //        }
        final String oldUrl = this.url;
        this.url = url;
        updateImageUrl();
        if (isAttached() && (oldUrl == null || !oldUrl.equals(url))) {
            if (onChangeAction != null) { onChangeAction.run(); }
        }
    }

    @Override
    protected void onLoad() {
        super.onLoad();
        updateImageUrl();

    }

    private void updateImageUrl() {
        final String prefix = website ? "./_website-snapshot" : "./_image-cache";

        //        getElement().getStyle().setBackgroundImage(placeholderImage());
        if (url != null && !url.isEmpty()) {
            if (CACHING && cached && !BrowserUtil.isInternalImage(url)) {
                if (url.startsWith("http")) {
                    swapUrl(prefix + "?url=" +
                            URL.encode(url) +
                            "&size=" +
                            size +
                            "&width=" +
                            getWidthWithDefault() +
                            "&height=" +
                            getHeightWithDefault());
                }
                else {
                    swapUrl(prefix + "?url=" +
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
                swapUrl(url);
            }
        }
        else if (defaultUrl != null) {
            super.setUrl(defaultUrl);
        }
        else {
            super.setUrl(placeholderImage());
        }
    }

    private void swapUrl(final String newUrl) {
        final Image newImage = new Image();
        newImage.addErrorHandler(new ErrorHandler() {
            @Override public void onError(final ErrorEvent event) {
                unspin();
                CachedImage.super.setUrl(defaultDefaultMessage("Image Failed"));
                CachedImage.super.getElement().setAttribute("x-vortex-failed-url", newUrl);
                newImage.removeFromParent();
            }
        });
        newImage.addLoadHandler(new LoadHandler() {
            @Override public void onLoad(final LoadEvent event) {
                //                WidgetUtil.showGracefully(CachedImage.this.getElement(), false);
                ready = true;
                unspin();
                newImage.removeFromParent();
                CachedImage.super.setUrl(newUrl);
            }
        });
        spin();
        newImage.setVisible(false);
        newImage.setUrl(newUrl);
        RootPanel.get().add(newImage);

    }

    private void spin() {
        if (spinner != null) {
            spinner.start();
        }
    }

    private void unspin() {
        if (spinner != null) {
            spinner.stop();
        }
    }


    public int getWidthWithDefault() {
        if (getOffsetWidth() > 0 && getOffsetWidth() > requestedWidth) {
            return getOffsetWidth();
        }
        else {
            return requestedWidth;
        }
    }

    public int getHeightWithDefault() {
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
        //        updateImageUrl();
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
        if (spinner != null) { spinner.update(); }
    }

    @Override
    public void setWidth(@Nonnull final String width) {
        super.setWidth(width);
        getElement().getStyle().setProperty("minWidth", width);
        if (width.toLowerCase().endsWith("px")) {
            requestedWidth = Integer.parseInt(width.substring(0, width.length() - 2));
        }
        if (spinner != null) { spinner.update(); }
    }

    public void setWebsite(boolean website) {


        this.website = website;
    }

    public void setSpinner(Spinner spinner) {
        this.spinner = spinner;
    }

    public int getRequestedWidth() {
        return requestedWidth;
    }

    public void setRequestedWidth(int requestedWidth) {
        this.requestedWidth = requestedWidth;
    }

    public int getRequestedHeight() {
        return requestedHeight;
    }

    public void setRequestedHeight(int requestedHeight) {
        this.requestedHeight = requestedHeight;
    }
}



