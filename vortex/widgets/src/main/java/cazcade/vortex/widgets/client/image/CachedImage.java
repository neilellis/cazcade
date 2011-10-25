package cazcade.vortex.widgets.client.image;

import cazcade.vortex.dnd.client.browser.BrowserUtil;
import com.google.gwt.http.client.URL;
import com.google.gwt.user.client.Window;
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
    private String notReadyText="No Image";
    private boolean cached= true;

    public CachedImage(String url, String size) {
        setSize(size);
        setUrl(url);
    }

    @Override
    protected void onAttach() {
        super.onAttach();
        if(super.getUrl() == null || super.getUrl().isEmpty()) {
            super.setUrl("http://placehold.it/" + getOffsetWidth() + "x" + getOffsetHeight() + "&text="+URL.encode(notReadyText));
        }
    }

    public CachedImage() {
    }

    public CachedImage(Image image) {
        this();
        setUrl(image.getUrl());
    }

    @Override
    public void setUrl(String url) {
        String oldUrl= this.url;
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
        if (url != null) {
            if (CACHING && cached) {
                if (url.startsWith("http")) {
                    super.setUrl("./image.service?url=" + URL.encode(url) + "&size=" + size);
                } else {
                    super.setUrl("./image.service?url=" + BrowserUtil.convertRelativeUrlToAbsolute(url) + "&size=" + size + "&width=" + getWidth() + "&height=" + getHeight());
                }
            } else {
                super.setUrl(url);
            }
        } else {
            super.setUrl(defaultUrl);
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
}
