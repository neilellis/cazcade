package cazcade.vortex.widgets.client.image;

import cazcade.vortex.dnd.client.browser.BrowserUtil;
import com.google.gwt.event.dom.client.LoadEvent;
import com.google.gwt.event.dom.client.LoadHandler;
import com.google.gwt.http.client.URL;
import com.google.gwt.user.client.ui.Image;

import javax.annotation.Nonnull;

/**
 * @author neilellis@cazcade.com
 */
public class CachedImage extends Image {
    @Nonnull
    public static final String PROFILE_SMALL = "PROFILE_SMALL";
    @Nonnull
    public static final String SMALL = "CLIPPED_SMALL";
    @Nonnull
    public static final String MEDIUM = "CLIPPED_MEDIUM";
    @Nonnull
    public static final String CLIPPED_LARGE = "CLIPPED_LARGE";
    @Nonnull
    public static final String LARGE = "LARGE";
    public static final boolean CACHING = true;
    private Runnable onChangeAction;

    /**
     * @see com.cazcade.billabong.image.ImageSize
     */
    private String size = SMALL;

    private String defaultUrl;
    private String url;
    private String notReadyText = "Loading";
    private boolean cached = true;
    private int requestedWidth;
    private int requestedHeight;

    public CachedImage(String url, String size) {
        setVisible(false);
        setSize(size);
        setUrl(url);
        addLoadHandler(new LoadHandler() {
            @Override
            public void onLoad(LoadEvent event) {
                setVisible(true);
            }
        });
    }

    public CachedImage(@Nonnull Image image, String size) {
        this(image.getUrl(), size);
    }

    @Override
    protected void onAttach() {
        super.onAttach();
        if (super.getUrl() == null || super.getUrl().isEmpty()) {
            super.setUrl(placeholderImage());
        }
        setUrl(this.url);
    }

    @Nonnull
    private String placeholderImage() {
        return defaultDefaultMessage(notReadyText);
    }

    public void setDefaultMessage(String message) {
        defaultUrl = defaultDefaultMessage(message);
    }

    @Nonnull
    private String defaultDefaultMessage(String message) {
        return "http://placehold.it/" + getWidthWithDefault() + "x" + getHeightWithDefault() + "&text=" + URL.encode(message);
    }

    public CachedImage() {
    }

    public CachedImage(@Nonnull Image image) {
        this();
        setUrl(image.getUrl());
    }

    @Override
    public void setUrl(String url) {
        String oldUrl = this.url;
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
        getElement().getStyle().setBackgroundImage(placeholderImage());
        if (url != null && !url.isEmpty()) {
            if (CACHING && cached && !BrowserUtil.isInternalImage(url)) {
                if (url.startsWith("http")) {
                    super.setUrl("./_image-service?url=" + URL.encode(url) + "&size=" + size + "&width=" + getWidthWithDefault() + "&height=" + getHeightWithDefault());
                } else {
                    super.setUrl("./_image-service?url=" + BrowserUtil.convertRelativeUrlToAbsolute(url) + "&size=" + size + "&width=" + getWidthWithDefault() + "&height=" + getHeightWithDefault());
                }
            } else {
                super.setUrl(url);
            }
        } else if (defaultUrl != null) {
            super.setUrl(defaultUrl);
        } else {
            super.setUrl(defaultDefaultMessage("No Image"));
        }

    }

    private int getWidthWithDefault() {
        if (getOffsetWidth() > 0) {
            return getOffsetWidth();
        } else {
            return requestedWidth;
        }
    }

    private int getHeightWithDefault() {
        if (size.equals(LARGE)) {
            return 2048;
        }
        if (getOffsetHeight() > 0) {
            return getOffsetHeight();
        } else if (requestedHeight > 0) {
            return requestedHeight;
        } else {
            return 2048;
        }
    }

    public void setSize(String size) {
        this.size = size;
        updateImageUrl();
    }

    public void setDefaultUrl(String defaultUrl) {
        this.defaultUrl = defaultUrl;
        if (getUrl() == null) {
            super.setUrl(defaultUrl);
        }
    }

    public String getUnCachedUrl() {
        return url;
    }

    public void setOnChangeAction(Runnable onChangeAction) {
        this.onChangeAction = onChangeAction;
    }

    public void setNotReadyText(String notReadyText) {
        this.notReadyText = notReadyText;
    }

    public void setCached(boolean cached) {
        this.cached = cached;
    }

    @Override
    public int getOffsetWidth() {
        return super.getOffsetWidth();
    }

    @Override
    public void setWidth(@Nonnull String width) {
        super.setWidth(width);
        getElement().getStyle().setProperty("minWidth", width);
        if (width.toLowerCase().endsWith("px")) {
            this.requestedWidth = Integer.parseInt(width.substring(0, width.length() - 2));
        }
    }

    @Override
    public void setHeight(@Nonnull String height) {
        super.setHeight(height);
        getElement().getStyle().setProperty("minHeight", height);
        if (height.toLowerCase().endsWith("px")) {
            this.requestedHeight = Integer.parseInt(height.substring(0, height.length() - 2));
        }
    }
}



