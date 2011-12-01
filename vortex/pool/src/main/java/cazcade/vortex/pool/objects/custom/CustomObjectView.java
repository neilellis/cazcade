package cazcade.vortex.pool.objects.custom;

import cazcade.vortex.pool.objects.PoolObjectView;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.DoubleClickEvent;
import com.google.gwt.event.dom.client.DoubleClickHandler;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.Image;

/**
 * @author neilellis@cazcade.com
 */
public class CustomObjectView extends PoolObjectView {

    private String href;

    public void setImageUrl(String url) {
        image.setUrl(url);
    }


    @Override
    public void viewMode() {
        super.viewMode();
    }

    @Override
    public void editMode() {
        if (isEditable()) {
            super.editMode();
        }
    }

    public void resetMode() {
        if (image.getUrl() == null || image.getUrl().isEmpty()) {
            editMode();
        } else {
            viewMode();
        }
    }

    public void setHref(String href) {
        this.href = href;
    }


    interface ImageObjectUiBinder extends UiBinder<HTMLPanel, CustomObjectView> {
    }

    @UiField
    Image image;

    @Override
    protected void onLoad() {
        resetMode();
        super.onLoad();
    }

    private static final ImageObjectUiBinder ourUiBinder = GWT.create(ImageObjectUiBinder.class);

    public CustomObjectView() {
        HTMLPanel widget = ourUiBinder.createAndBindUi(this);
        initWidget(widget);
        addDomHandler(new DoubleClickHandler() {
            @Override
            public void onDoubleClick(DoubleClickEvent event) {
                editMode();
            }
        }, DoubleClickEvent.getType());

        addDomHandler(new CustomObjectClickHandler(), ClickEvent.getType());
    }

    @Override
    public void setLogicalWidth(int width) {
        super.setLogicalWidth(width);
        image.setWidth(width + "px");
    }

    @Override
    public void setLogicalHeight(int height) {
        super.setLogicalHeight(height);
        image.setHeight(height + "px");
    }


    private static class CustomObjectClickHandler implements ClickHandler {
        @Override
        public void onClick(ClickEvent event) {

        }
    }
}