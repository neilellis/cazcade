package cazcade.vortex.pool.objects.image;

import cazcade.vortex.pool.objects.PoolObjectView;
import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.Image;

/**
 * @author neilellis@cazcade.com
 */
public class ImageView extends PoolObjectView {


    public void setUrl(String url) {
        image.setUrl(url);
    }


    interface ImageObjectUiBinder extends UiBinder<HTMLPanel, ImageView> {
    }

    @UiField
    Image image;

    private static final ImageObjectUiBinder ourUiBinder = GWT.create(ImageObjectUiBinder.class);

    public ImageView() {
        HTMLPanel widget = ourUiBinder.createAndBindUi(this);
        initWidget(widget);
        getElement().getStyle().setZIndex(9000);
    }

    @Override
    public void onAddToPool() {
        super.onAddToPool();
        image.setHeight("auto");
        image.setWidth("auto");

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