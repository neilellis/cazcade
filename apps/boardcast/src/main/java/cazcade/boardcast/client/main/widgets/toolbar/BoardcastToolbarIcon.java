/*
 * Copyright (c) 2009-2013 Cazcade Limited  - All Rights Reserved
 */

package cazcade.boardcast.client.main.widgets.toolbar;

import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;

/**
 * Created by IntelliJ IDEA.
 * User: neilellis
 * Date: 22/08/2011
 * Time: 15:32
 * To change this template use File | Settings | File Templates.
 */
public class BoardcastToolbarIcon extends Composite {

    public void setSrc(final String src) {
        image.setUrl(src);
    }

    public void setText(final String text) {
        label.setText(text);
    }

    interface ToolbarIconUiBinder extends UiBinder<HTMLPanel, BoardcastToolbarIcon> {}

    private static final ToolbarIconUiBinder ourUiBinder = GWT.create(ToolbarIconUiBinder.class);
    @UiField Image image;
    @UiField Label label;

    public BoardcastToolbarIcon() {
        super();
        initWidget(ourUiBinder.createAndBindUi(this));
        getWidget().sinkEvents(Event.ONCLICK);
    }
}