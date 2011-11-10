package cazcade.vortex.widgets.client.image;

import cazcade.vortex.dnd.client.browser.BrowserUtil;
import com.google.gwt.http.client.URL;
import com.google.gwt.user.client.ui.Image;

/**
 * @author neilellis@cazcade.com
 */
public class CachedImage extends Image {
    public static final String PROFILE_SMALL = "PROFILE_SMALL";
    public static final String SMALL = "CLIPPED_SMALL";
    public static final String MEDIUM = "CLIPPED_MEDIUM";
    public static final String CLIPPED_LARGE = "CLIPPED_LARGE";
    public static final String LARGE = "LARGE";
    public static final boolean CACHING = true;
    private Runnable onChangeAction;

    /**
     * @see com.cazcade.billabong.image.ImageSize
     */
    private String size = SMALL;

    private String defaultUrl;
    private String url;
    private String notReadyText = "No Image";
    private boolean cached = true;
    private int requestedWidth;
    private int requestedHeight;

    public CachedImage(String url, String size) {
        setSize(size);
        setUrl(url);
    }

    public CachedImage(Image image, String size) {
        setSize(size);
        setUrl(image.getUrl());
    }

    @Override
    protected void onAttach() {
        super.onAttach();
        if (super.getUrl() == null || super.getUrl().isEmpty()) {
            super.setUrl(placeholderImage());
        }
        setUrl(this.url);
    }

    private String placeholderImage() {
        return defaultDefaultMessage(notReadyText);
    }

    public void setDefaultMessage(String message) {
        defaultUrl = defaultDefaultMessage(message);
    }

    private String defaultDefaultMessage(String message) {
        return "http://placehold.it/" + getWidthWithDefault() + "x" + getHeightWithDefault() + "&text=" + URL.encode(message);
    }

    public CachedImage() {
    }

    public CachedImage(Image image) {
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
    public void setWidth(String width) {
        super.setWidth(width);
        if (width.toLowerCase().endsWith("px")) {
            this.requestedWidth = Integer.parseInt(width.substring(0, width.length() - 2));
        }
    }

    @Override
    public void setHeight(String height) {
        super.setHeight(height);
        if (height.toLowerCase().endsWith("px")) {
            this.requestedHeight = Integer.parseInt(height.substring(0, height.length() - 2));
        }
    }
}



