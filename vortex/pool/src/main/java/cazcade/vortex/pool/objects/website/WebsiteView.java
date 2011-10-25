package cazcade.vortex.pool.objects.website;

import cazcade.vortex.pool.objects.PoolObjectView;
import cazcade.vortex.widgets.client.image.CachedImage;
import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.HTMLPanel;

/**
 * @author neilellis@cazcade.com
 */
public class WebsiteView extends PoolObjectView {


    public void setUrl(String url) {
        website.setUrl(url);
    }


    interface ImageObjectUiBinder extends UiBinder<HTMLPanel, WebsiteView> {
    }

    @UiField
    CachedImage website;
    @UiField
    HTMLPanel websiteSurround;


    private static ImageObjectUiBinder ourUiBinder = GWT.create(ImageObjectUiBinder.class);

    public WebsiteView() {
        HTMLPanel widget = ourUiBinder.createAndBindUi(this);
        initWidget(widget);
    }

    @Override
    public void onAddToPool() {
        super.onAddToPool();
        if (size != null) {
            if (size.equals(SMALL)) {
                website.setSize(CachedImage.MEDIUM);
                website.setWidth(SMALL_WIDTH + "px");
                website.setHeight(SMALL_HEIGHT + "px");
            }
            if (size.equals(MEDIUM)) {
                website.setSize(CachedImage.LARGE);
                website.setWidth(MEDIUM_WIDTH + "px");
                website.setHeight(MEDIUM_HEIGHT + "px");
            }
            if (size.equals(LARGE)) {
                website.setSize(CachedImage.LARGE);
                website.setWidth(LARGE_WIDTH + "px");
//                image.setHeight(LARGE_HEIGHT + "px");
            }
        }
    }

    @Override
    protected void onLoad() {
        super.onLoad();
    }

    @Override
    public void setLogicalWidth(int width) {
        super.setLogicalWidth(width);
//        image.setWidth(width + "px");
    }

    @Override
    public void setLogicalHeight(int height) {
        super.setLogicalHeight(height);
//        image.setHeight(height + "px");
    }


}