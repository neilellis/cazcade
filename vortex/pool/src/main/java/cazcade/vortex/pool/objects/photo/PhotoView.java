package cazcade.vortex.pool.objects.photo;

import cazcade.vortex.pool.objects.PoolObjectView;
import cazcade.vortex.widgets.client.image.CachedImage;
import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Style;
import com.google.gwt.event.dom.client.DoubleClickHandler;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.Image;

/**
 * @author neilellis@cazcade.com
 */
public class PhotoView extends PoolObjectView {


    public void setUrl(final String url) {
        image.setUrl(url);
    }

    public void addDoubleClickHandler(final DoubleClickHandler handler) {
        effect.addDoubleClickHandler(handler);
    }


    interface ImageObjectUiBinder extends UiBinder<HTMLPanel, PhotoView> {
    }

    @UiField
    CachedImage image;
    @UiField
    HTMLPanel imageSurround;
    @UiField
    Image effect;


    private static final ImageObjectUiBinder ourUiBinder = GWT.create(ImageObjectUiBinder.class);

    public PhotoView() {
        super();
        final HTMLPanel widget = ourUiBinder.createAndBindUi(this);
        initWidget(widget);
        effect.getElement().getStyle().setPosition(Style.Position.ABSOLUTE);
    }

    @Override
    public void onAddToPool() {
        super.onAddToPool();
        if (size != null) {
            if (size.equals(THUMBNAIL)) {
                image.setSize(CachedImage.SMALL);
                image.setWidth(THUMBNAIL_WIDTH + "px");
                image.setHeight(THUMBNAIL_HEIGHT + "px");
            }
            if (size.equals(SMALL)) {
                image.setSize(CachedImage.MEDIUM);
                image.setWidth(SMALL_WIDTH + "px");
                image.setHeight(SMALL_HEIGHT + "px");
            }
            if (size.equals(MEDIUM)) {
                image.setSize(CachedImage.LARGE);
                image.setWidth(MEDIUM_WIDTH + "px");
                image.setHeight(MEDIUM_HEIGHT + "px");
            }
            if (size.equals(LARGE)) {
                image.setSize(CachedImage.LARGE);
                image.setWidth(LARGE_WIDTH + "px");
//                image.setHeight(LARGE_HEIGHT + "px");
            }
        }
        image.setDefaultMessage("No Image");
        effect.setWidth(image.getOffsetWidth() + "px");
        effect.setHeight(image.getOffsetHeight() + "px");
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


}