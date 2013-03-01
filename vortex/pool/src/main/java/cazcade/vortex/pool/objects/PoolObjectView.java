/*
 * Copyright (c) 2009-2013 Cazcade Limited  - All Rights Reserved
 */

package cazcade.vortex.pool.objects;

import cazcade.vortex.common.client.events.EditFinishEvent;
import cazcade.vortex.common.client.events.EditStartEvent;
import cazcade.vortex.dnd.client.browser.BrowserUtil;
import cazcade.vortex.dnd.client.gesture.drag.DragEvent;
import cazcade.vortex.dnd.client.gesture.drag.DragHandler;
import cazcade.vortex.dnd.client.gesture.drag.HasDragHandler;
import cazcade.vortex.dnd.client.gesture.enddrag.EndDragEvent;
import cazcade.vortex.dnd.client.gesture.enddrag.EndDragHandler;
import cazcade.vortex.dnd.client.gesture.enddrag.HasEndDragHandler;
import cazcade.vortex.dnd.client.gesture.hdrag.HasHoldDragHandler;
import cazcade.vortex.dnd.client.gesture.hdrag.HoldDragEvent;
import cazcade.vortex.dnd.client.gesture.hdrag.HoldDragHandler;
import cazcade.vortex.pool.widgets.dnd.GestureAwareView;
import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Element;
import com.google.gwt.event.dom.client.*;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.ui.Panel;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.Widget;

import javax.annotation.Nonnull;


/**
 * @author neilellis@cazcade.com
 */
public abstract class PoolObjectView extends GestureAwareView implements HasDragHandler, HasHoldDragHandler, HasEndDragHandler, HasClickHandlers {

    @Nonnull
    public static final String THUMBNAIL         = "thumbnail";
    @Nonnull
    public static final String SMALL             = "small";
    @Nonnull
    public static final String MEDIUM            = "medium";
    @Nonnull
    public static final String LARGE             = "large";
    public static final double CORE_RATIO        = 3.0 / 2.0;
    public static final double ROOT_2_RATIO      = Math.sqrt(2.0);
    public static final double ALT_RATIO_1       = 4.0 / 3.0;
    public static final double ALT_RATIO_2       = 16.0 / 9.0;
    public static final double GOLDEN_RATIO      = 1.618;
    public static final int    BOARD_WIDTH       = 1024;
    public static final int    PADDING           = 10;
    public static final int    BORDER            = 2;
    public static final double ASPECT_RATIO      = CORE_RATIO;
    public static final int    ADDITIONAL_WIDTH  = PADDING * 2 + BORDER * 2;
    public static final int    ADDITIONAL_HEIGHT = PADDING * 2 + BORDER * 2;
    public static final int    THUMBNAIL_WIDTH   = 92;
    public static final int    THUMBNAIL_HEIGHT  = (int) (THUMBNAIL_WIDTH / ASPECT_RATIO);
    public static final int    SMALL_WIDTH       = (int) (BOARD_WIDTH - BOARD_WIDTH / CORE_RATIO) - ADDITIONAL_WIDTH;
    public static final int    SMALL_HEIGHT      = (int) ((BOARD_WIDTH - BOARD_WIDTH / CORE_RATIO) / ASPECT_RATIO)
                                                   - ADDITIONAL_HEIGHT;
    public static final int    MEDIUM_WIDTH      = (int) (BOARD_WIDTH / CORE_RATIO) - ADDITIONAL_WIDTH;
    public static final int    MEDIUM_HEIGHT     = (int) ((BOARD_WIDTH / CORE_RATIO) / ASPECT_RATIO) - ADDITIONAL_HEIGHT;
    public static final int    LARGE_WIDTH       = BOARD_WIDTH - ADDITIONAL_WIDTH;
    public static final int    LARGE_HEIGHT      = (int) (BOARD_WIDTH / CORE_RATIO) - ADDITIONAL_HEIGHT;

    private Runnable onDelete;
    private int      logicalWidth;
    private int      logicalHeight;
    protected final BrowserUtil browserUtil = GWT.create(BrowserUtil.class);
    protected boolean editing;
    private   boolean editable;
    private   Widget  innerWidget;
    protected String  size;
    protected String  theme;


    protected PoolObjectView() {
        super();

    }


    public void setStyleSize(final String size) {
        this.size = size;
        getInnerWidget().addStyleName("size-" + size);

    }


    public void setStyleTheme(final String theme) {
        this.theme = theme;
        getInnerWidget().addStyleName("theme-" + theme);

    }

    public void viewMode() {
        getWidget().removeStyleName("pool-object-edit-mode");
        editing = false;
        fireEvent(new EditFinishEvent());
    }

    public void editMode() {
        if (isEditable()) {
            getWidget().addStyleName("pool-object-edit-mode");
            editing = true;
            fireEvent(new EditStartEvent());
        }
    }

    public boolean isEditing() {
        return editing;
    }

    public void setEditable(final boolean editable) {
        this.editable = editable;
    }

    public boolean isEditable() {
        return editable;
    }


    @Override
    protected void initWidget(@Nonnull final Widget widget) {
        innerWidget = widget;
        final SimplePanel simplePanel = new SimplePanel();
        simplePanel.addStyleName("pool-object");
        simplePanel.add(widget);
        browserUtil.initDraggable(simplePanel);
        super.initWidget(simplePanel);
        widget.addStyleName("pool-object-inner");
        final SimplePanel deleteButton = new SimplePanel();
        deleteButton.addStyleName("pool-object-delete");
        deleteButton.addStyleName("invisible");
        ((Panel) widget).add(deleteButton);
        deleteButton.sinkEvents(Event.ONCLICK);
        deleteButton.addHandler(new ClickHandler() {
            @Override
            public void onClick(final ClickEvent event) {
                onDelete.run();
            }
        }, ClickEvent.getType());

        addMouseOverHandler(new MouseOverHandler() {
            @Override
            public void onMouseOver(final MouseOverEvent event) {
                if (isEditable()) {
                    deleteButton.removeStyleName("invisible");
                }
            }
        });
        addMouseOutHandler(new MouseOutHandler() {
            @Override
            public void onMouseOut(final MouseOutEvent event) {
                deleteButton.addStyleName("invisible");
            }
        });


    }

    public HandlerRegistration addDragHandler(final DragHandler dragHandler) {
        return addHandler(dragHandler, DragEvent.getType());
    }


    public HandlerRegistration addHoldDragHandler(final HoldDragHandler dragHandler) {
        return addHandler(dragHandler, HoldDragEvent.getType());
    }

    public HandlerRegistration addEndDragHandler(final EndDragHandler endDragHandler) {
        return addHandler(endDragHandler, EndDragEvent.getType());
    }

    public void setLogicalWidth(final int width) {
        //resizing not supported

        //        super.setWidth(width + "px");
        logicalWidth = width;
    }

    public void setLogicalHeight(final int height) {
        //resizing not supported

        //        super.setHeight(height + "px");
        logicalHeight = height;
    }

    public int getLogicalWidth() {
        return logicalWidth;
    }

    public int getLogicalHeight() {
        return logicalHeight;
    }


    public int getPageX() {
        int x = 0;
        for (Element pos = getWidget().getElement(); pos != null; pos = pos.getOffsetParent()) {
            x += pos.getOffsetLeft();
        }
        return x;
    }

    public int getPageY() {
        int y = 0;
        for (Element pos = getWidget().getElement(); pos != null; pos = pos.getOffsetParent()) {
            y += pos.getOffsetTop();
        }
        return y;
    }

    public void setOnDelete(final Runnable onDelete) {
        this.onDelete = onDelete;
    }

    public void removeView(final Widget widget) {
        throw new UnsupportedOperationException("This view does not support removing child views.");
    }

    public void add(final Widget widget) {
        throw new UnsupportedOperationException("This view does not support adding child views.");
    }

    public Widget getInnerWidget() {
        return innerWidget;
    }

    public void onAddToPool() {
    }

    public abstract int getDefaultZIndex();
}
