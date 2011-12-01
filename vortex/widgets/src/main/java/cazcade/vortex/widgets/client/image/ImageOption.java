package cazcade.vortex.widgets.client.image;

import cazcade.vortex.dnd.client.browser.BrowserUtil;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HTMLPanel;


/**
 * @author neilellis@cazcade.com
 */
public class ImageOption extends Composite {

    private ImageSelection imageSelection;
    private String url;

    public void setThumbnail(final String thumbnail) {
        image.setUrl(thumbnail);
    }

    public void setImageSelection(final ImageSelection imageSelection) {
        this.imageSelection = imageSelection;
    }

    public String getUrl() {
        return BrowserUtil.convertRelativeUrlToAbsolute(url);
    }

    public void setUrl(final String url) {
        this.url = url;
    }


    interface ImageOptionUiBinder extends UiBinder<HTMLPanel, ImageOption> {
    }

    private static final ImageOptionUiBinder ourUiBinder = GWT.create(ImageOptionUiBinder.class);

    @UiField
    CachedImage image;

    public ImageOption() {
        super();
        final HTMLPanel rootElement = ourUiBinder.createAndBindUi(this);
        initWidget(rootElement);
//        image.sinkEvents(Event.MOUSEEVENTS);
    }

    @UiHandler("image")
    public void handleClick(final ClickEvent e) {
        imageSelection.selected(ImageOption.this);
    }

}