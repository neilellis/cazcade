package cazcade.vortex.pool.objects.microblog;

import cazcade.vortex.pool.objects.PoolObjectView;
import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.HeadingElement;
import com.google.gwt.dom.client.ImageElement;
import com.google.gwt.dom.client.SpanElement;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.HTMLPanel;

/**
 * @author neilellis@cazcade.com
 */
public class MicroBlogView extends PoolObjectView {
    interface MicroBlogViewUiBinder extends UiBinder<HTMLPanel, MicroBlogView> {
    }

    private static MicroBlogViewUiBinder ourUiBinder = GWT.create(MicroBlogViewUiBinder.class);
    @UiField
    ImageElement image;
    @UiField
    HeadingElement title;
    @UiField
    SpanElement shortText;

    public MicroBlogView() {
        initWidget(ourUiBinder.createAndBindUi(this));
        setLogicalWidth(300);
        setLogicalHeight(100);
    }

    public void setProfileImage(String url) {
        image.setSrc(url);
    }

    public void setMicroBlogTitle(String title) {
        this.title.setInnerText(title);
    }

    public void setMicroBlogShortText(String text) {
        this.shortText.setInnerText(text);
    }
}