/*
 * Copyright (c) 2009-2013 Cazcade Limited  - All Rights Reserved
 */

package cazcade.vortex.widgets.client.image;

import cazcade.vortex.dnd.client.browser.BrowserUtil;
import cazcade.vortex.widgets.client.spinner.Spinner;
import com.google.gwt.core.client.Scheduler;
import com.google.gwt.event.dom.client.ErrorEvent;
import com.google.gwt.event.dom.client.ErrorHandler;
import com.google.gwt.event.dom.client.LoadEvent;
import com.google.gwt.event.dom.client.LoadHandler;
import com.google.gwt.http.client.URL;
import com.google.gwt.user.client.Timer;
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
    protected void onLoad() {
        super.onLoad();
        if (getUrl() == null || getUrl().isEmpty()) {
            super.setUrl(placeholderImage());
        }
        updateImageUrl();
    }

    @Nonnull
    private String placeholderImage() {
        return "/_static/_images/blank.png";
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
        if (isAttached()) {
            updateImageUrl();
        }
        if (isAttached() && (oldUrl == null || !oldUrl.equals(url))) {
            if (onChangeAction != null) { onChangeAction.run(); }
        }
    }


    private void updateImageUrl() {
        final String prefix = website
                              ? "http://cache.snapito.com/api/image?_cache_redirect=true&"
                              : "http://cache.snapito.com/api/image?image&_cache_redirect=true&";

        //        getElement().getStyle().setBackgroundImage(placeholderImage());
        if (url != null && !url.isEmpty()) {
            if (CACHING && cached && !BrowserUtil.isInternalImage(url)) {
                if (url.startsWith("http")) {
                    swapUrl(prefix + "url=" +
                            URL.encode(url) +
                            "&size=" +
                            size +
                            "&width=" +
                            getWidthWithDefault() +
                            "&height=" +
                            getHeightWithDefault());
                } else {
                    swapUrl(prefix + "url=" +
                            BrowserUtil.convertRelativeUrlToAbsolute(url) +
                            "&size=" +
                            size +
                            "&width=" +
                            getWidthWithDefault() +
                            "&height=" +
                            getHeightWithDefault());
                }
            } else {
                swapUrl(url);
            }
        } else if (defaultUrl != null) {
            super.setUrl(defaultUrl);
        } else {
            super.setUrl(placeholderImage());
        }
    }

    private void swapUrl(final String newUrl) {

        //        final RequestBuilder builder = new RequestBuilder(RequestBuilder.GET, "./_validate-url?url=" + URL.encode(newUrl));
        //
        //        try {
        //            builder.sendRequest(null, new RequestCallback() {
        //                @Override public void onError(final Request request, final Throwable exception) {
        //                    // Couldn't connect to server (could be timeout, SOP violation, etc.)
        //                    ClientLog.log(exception);
        //                    Window.alert("Image Failed");
        //                }
        //
        //                @Override public void onResponseReceived(final Request request, final Response response) {
        //                    if(response.getStatusCode() < 400) {
        //                        CachedImage.super.setUrl(newUrl);
        //                    }
        //                }
        //            });
        //        } catch (RequestException e) {
        //            ClientLog.log(e);
        //        }


        final Image newImage = new Image(newUrl);
        newImage.setVisible(false);
        newImage.addErrorHandler(new ErrorHandler() {
            @Override public void onError(final ErrorEvent event) {
                Scheduler.get().scheduleDeferred(new Scheduler.ScheduledCommand() {
                    @Override public void execute() {
                        unspin();
                        CachedImage.super.setUrl(defaultDefaultMessage("Image Failed"));
                        CachedImage.super.getElement().setAttribute("x-vortex-failed-url", newUrl);
                        CachedImage.super.getElement().setAttribute("x-vortex-failed-event", event.toDebugString());
                        if (newImage.isAttached()) {
                            newImage.removeFromParent();
                        }
                    }
                });
            }
        });
        newImage.addLoadHandler(new LoadHandler() {
            @Override public void onLoad(final LoadEvent event) {
                unspin();
                CachedImage.super.setUrl(newUrl);
                if (newImage.isAttached()) {
                    newImage.removeFromParent();
                }
                ready = true;

            }
        });
        new Timer() {
            @Override public void run() {
                if (newUrl != null && !newUrl.equals(CachedImage.super.getUrl())) {
                    spin();
                }
            }
        }.schedule(1000);
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
        } else {
            return requestedWidth;
        }
    }

    public int getHeightWithDefault() {
        if (size.equals(LARGE)) {
            return 2048;
        }
        if (getOffsetHeight() > 0 && getOffsetHeight() > requestedHeight) {
            return getOffsetHeight();
        } else if (requestedHeight > 0) {
            return requestedHeight;
        } else {
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

    public String getRawUrl() {
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

    public void clear() {
        super.setUrl(this.placeholderImage());
    }
}



