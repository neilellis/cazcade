package cazcade.vortex.pool.objects.youtube;

import cazcade.vortex.common.client.FormatUtil;
import cazcade.vortex.gwt.util.client.WidgetUtil;
import cazcade.vortex.pool.objects.PoolObjectView;
import cazcade.vortex.widgets.client.image.CachedImage;
import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.Scheduler;
import com.google.gwt.dom.client.IFrameElement;
import com.google.gwt.event.dom.client.*;
import com.google.gwt.event.logical.shared.HasValueChangeHandlers;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.TextBox;

/**
 * @author neilellis@cazcade.com
 */
public class YouTubeView extends PoolObjectView implements HasValueChangeHandlers<String> {

    public static final String INVALID_URL_MESSAGE = "Please supply a valid YouTube id or URL (e.g. http://www.youtube.com/watch?v=hfjGRBFd7mQ or hfjGRBFd7mQ)";
    private String videoId;

    public void setVideoId(String videoId) {
        this.videoId = videoId;
        //wmode=transparent is required to stop visual artifacts, but we do need to look at a way to optimize this
        //because transparent is a lot slower.
        image.setUrl("http://img.youtube.com/vi/" + videoId + ((size == null || !size.equals(LARGE)) ? "/default.jpg" : "/hqdefault.jpg"));
        imageViewOn();
    }


    public void resetMode() {
        if (videoId == null) {
            editMode();
        } else {
            viewMode();
        }
    }

    public String getVideoId() {
        return videoId;
    }

    @Override
    public HandlerRegistration addValueChangeHandler(ValueChangeHandler<String> stringValueChangeHandler) {
        return addHandler(stringValueChangeHandler, ValueChangeEvent.getType());
    }


    public void imageViewOn() {
        if (!editing) {
            if (videoFrameHolder.isVisible()) {
                WidgetUtil.swap(videoFrameHolder, image);
                videoFrameHolder.setVisible(false);
                image.setVisible(true);
                videoFrame.setSrc("");
            }
        }
    }

    public void imageViewOff() {
        if (!editing) {
            if (!videoFrameHolder.isVisible()) {
                videoFrame.setSrc("http://www.youtube.com/embed/" + videoId + "?wmode=transparent");
                WidgetUtil.swap(image, videoFrameHolder);
                videoFrameHolder.setVisible(true);
                image.setVisible(false);
            }
        }
    }

    interface YouTubeUiBinder extends UiBinder<HTMLPanel, YouTubeView> {
    }


    @UiField
    IFrameElement videoFrame;
    @UiField
    HTMLPanel videoFrameHolder;
    @UiField
    Image image;


    @Override
    public void onAddToPool() {
        super.onAddToPool();
        if (size != null) {
            if (size.equals(SMALL)) {
                videoFrame.setAttribute("width", SMALL_WIDTH + "px");
                videoFrame.setAttribute("height", SMALL_HEIGHT + "px");
                image.setWidth(SMALL_WIDTH + "px");
                image.setHeight(SMALL_HEIGHT + "px");
            }
            if (size.equals(MEDIUM)) {
                videoFrame.setAttribute("width", MEDIUM_WIDTH + "px");
                videoFrame.setAttribute("height", MEDIUM_HEIGHT + "px");
                image.setWidth(MEDIUM_WIDTH + "px");
                image.setHeight(MEDIUM_HEIGHT + "px");
            }
            if (size.equals(LARGE)) {
                videoFrame.setAttribute("width", LARGE_WIDTH + "px");
                videoFrame.setAttribute("height", LARGE_HEIGHT + "px");
                image.setWidth(LARGE_WIDTH + "px");
                image.setHeight(LARGE_HEIGHT + "px");
            }
        }
    }

    @Override
    protected void onLoad() {
        super.onLoad();
        resetMode();
        videoFrameHolder.removeFromParent();
        imageViewOn();
        super.onLoad();
    }

    private static YouTubeUiBinder ourUiBinder = GWT.create(YouTubeUiBinder.class);

    public YouTubeView() {
        HTMLPanel widget = ourUiBinder.createAndBindUi(this);
        initWidget(widget);
        addDomHandler(new DoubleClickHandler() {
            @Override
            public void onDoubleClick(DoubleClickEvent event) {
                editMode();
            }
        }, DoubleClickEvent.getType());

        image.addMouseOverHandler(new MouseOverHandler() {
            @Override
            public void onMouseOver(MouseOverEvent event) {
                imageViewOff();
            }
        });


    }

    @Override
    public void setLogicalWidth(int width) {
        super.setLogicalWidth(width);
//        videoFrameHolder.setWidth(width + "px");
    }

    @Override
    public void setLogicalHeight(int height) {
        //resizing not supported
        super.setLogicalHeight(height);
//        videoFrameHolder.setHeight(height + "px");
    }


    @Override
    public int getLogicalWidth() {
        return 290;
    }

    @Override
    public int getLogicalHeight() {
        return 190;
    }
}