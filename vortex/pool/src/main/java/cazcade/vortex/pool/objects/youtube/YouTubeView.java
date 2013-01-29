/*
 * Copyright (c) 2009-2013 Cazcade Limited  - All Rights Reserved
 */

package cazcade.vortex.pool.objects.youtube;

import cazcade.vortex.gwt.util.client.WidgetUtil;
import cazcade.vortex.pool.objects.PoolObjectView;
import cazcade.vortex.widgets.client.image.CachedImage;
import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.IFrameElement;
import com.google.gwt.event.dom.client.DoubleClickEvent;
import com.google.gwt.event.dom.client.DoubleClickHandler;
import com.google.gwt.event.dom.client.MouseOverEvent;
import com.google.gwt.event.dom.client.MouseOverHandler;
import com.google.gwt.event.logical.shared.HasValueChangeHandlers;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.HTMLPanel;

import javax.annotation.Nonnull;

/**
 * @author neilellis@cazcade.com
 */
public class YouTubeView extends PoolObjectView implements HasValueChangeHandlers<String> {

    @Nonnull
    public static final String INVALID_URL_MESSAGE = "Please supply a valid YouTube id or URL (e.g. http://www.youtube.com/watch?v=hfjGRBFd7mQ or hfjGRBFd7mQ)";
    private String videoId;

    public void setVideoId(final String videoId) {
        this.videoId = videoId;
        //wmode=transparent is required to stop visual artifacts, but we do need to look at a way to optimize this
        //because transparent is a lot slower.
        //        final String imageUrl = "http://img.youtube.com/vi/" + videoId + ((size == null || size.equals(SMALL)) ? "/default.jpg" : "/hqdefault.jpg");
        final String imageUrl = "http://img.youtube.com/vi/" + videoId + "/hqdefault.jpg";
        image.setUrl(imageUrl);
        if (SMALL.equals(size)) {
            image.setSize(CachedImage.MEDIUM);
        }
        else {
            image.setSize(CachedImage.LARGE);
        }
        videoFrameHolder.getElement().getStyle().setBackgroundImage(imageUrl);
        imageViewOn();
    }


    public void resetMode() {
        if (videoId == null) {
            editMode();
        }
        else {
            viewMode();
        }
    }

    public String getVideoId() {
        return videoId;
    }

    @Override
    public HandlerRegistration addValueChangeHandler(final ValueChangeHandler<String> stringValueChangeHandler) {
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

    interface YouTubeUiBinder extends UiBinder<HTMLPanel, YouTubeView> {}


    @UiField IFrameElement videoFrame;
    @UiField HTMLPanel     videoFrameHolder;
    @UiField CachedImage   image;


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

    @Override public int getDefaultZIndex() {
        return 1000;
    }

    @Override
    protected void onLoad() {
        super.onLoad();
        resetMode();
        videoFrameHolder.removeFromParent();
        imageViewOn();
        super.onLoad();
    }

    private static final YouTubeUiBinder ourUiBinder = GWT.create(YouTubeUiBinder.class);

    public YouTubeView() {
        super();
        final HTMLPanel widget = ourUiBinder.createAndBindUi(this);
        initWidget(widget);
        addDomHandler(new DoubleClickHandler() {
            @Override
            public void onDoubleClick(final DoubleClickEvent event) {
                editMode();
            }
        }, DoubleClickEvent.getType());

        image.addMouseOverHandler(new MouseOverHandler() {
            @Override
            public void onMouseOver(final MouseOverEvent event) {
                imageViewOff();
            }
        });


    }

    @Override
    public void setLogicalWidth(final int width) {
        super.setLogicalWidth(width);
        //        videoFrameHolder.setWidth(width + "px");
    }

    @Override
    public void setLogicalHeight(final int height) {
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