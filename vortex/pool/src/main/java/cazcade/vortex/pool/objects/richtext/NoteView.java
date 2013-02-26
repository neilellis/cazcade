/*
 * Copyright (c) 2009-2013 Cazcade Limited  - All Rights Reserved
 */

package cazcade.vortex.pool.objects.richtext;

import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.user.client.ui.HTMLPanel;

/**
 * @author neilellis@cazcade.com
 */
public class NoteView extends RichTextView {

    protected static final NoteViewUIBinder ourUiBinder = GWT.create(NoteViewUIBinder.class);

    interface NoteViewUIBinder extends UiBinder<HTMLPanel, RichTextView> {}

    public NoteView() {
        super();
        initWidget(ourUiBinder.createAndBindUi(this));
        getElement().getStyle().setZIndex(5000);
    }

    @Override
    public void onAddToPool() {
        super.onAddToPool();
        if (size != null) {
            if (size.equals(SMALL)) {
                getInnerWidget().setWidth(SMALL_WIDTH + "px");
                //                getWidget().setHeight(SMALL_HEIGHT + "px");
            }
            if (size.equals(MEDIUM)) {
                getInnerWidget().setWidth(MEDIUM_WIDTH + "px");
                //                getWidget().setHeight(MEDIUM_HEIGHT + "px");
            }
            if (size.equals(LARGE)) {
                getInnerWidget().setWidth(LARGE_WIDTH + "px");
                //                getWidget().setHeight(LARGE_HEIGHT + "px");
            }
        }
        if ("black".equals(theme)) {
            label.setColor("white");
        }
    }


}