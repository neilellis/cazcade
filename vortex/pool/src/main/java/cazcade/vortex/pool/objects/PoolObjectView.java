package cazcade.vortex.pool.objects;

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
import cazcade.vortex.pool.EditFinish;
import cazcade.vortex.pool.EditStart;
import cazcade.vortex.pool.widgets.dnd.GestureAwareView;
import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Element;
import com.google.gwt.event.dom.client.*;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.ui.Panel;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.Widget;


/**
 * @author neilellis@cazcade.com
 */
public abstract class PoolObjectView extends GestureAwareView implements HasDragHandler, HasHoldDragHandler, HasEndDragHandler, HasClickHandlers {

    public static final String SMALL = "small";
    public static final String MEDIUM = "medium";
    public static final String LARGE = "large";
    public static final double GOLDEN_RATIO = 1.618;
    public static final int BOARD_WIDTH = 1024;
    public static final int PADDING = 10;
    public static final int BORDER = 2;
    public static final double ASPECT_RATIO = 1.333;
    public static final int ADDITIONAL_WIDTH = PADDING * 2 + BORDER * 2;
    public static final int ADDITIONAL_HEIGHT = PADDING * 2 + BORDER * 2;
    public static final int SMALL_WIDTH = (int) (BOARD_WIDTH - BOARD_WIDTH / GOLDEN_RATIO) - ADDITIONAL_WIDTH;
    public static final int SMALL_HEIGHT = (int) ((BOARD_WIDTH - BOARD_WIDTH / GOLDEN_RATIO) / ASPECT_RATIO) - ADDITIONAL_HEIGHT;
    public static final int MEDIUM_WIDTH = (int) (BOARD_WIDTH / GOLDEN_RATIO) - ADDITIONAL_WIDTH;
    public static final int MEDIUM_HEIGHT = (int) ((BOARD_WIDTH / GOLDEN_RATIO) / ASPECT_RATIO) - ADDITIONAL_HEIGHT;
    public static final int LARGE_WIDTH = BOARD_WIDTH - ADDITIONAL_WIDTH;
    public static final int LARGE_HEIGHT = (int) ((BOARD_WIDTH) / GOLDEN_RATIO) - ADDITIONAL_HEIGHT;

    private Runnable onDelete;
    private int logicalWidth;
    private int logicalHeight;
    protected BrowserUtil browserUtil = GWT.create(BrowserUtil.class);
    protected boolean editing;
    private boolean editable = false;
    private Widget innerWidget;
    protected String size;
    protected String theme;


    protected PoolObjectView() {
    }


    public void setStyleSize(String size) {
        this.size = size;
        getInnerWidget().addStyleName("size-" + size);

    }


    public void setStyleTheme(String theme) {
        this.theme = theme;
        getInnerWidget().addStyleName("theme-" + theme);

    }

    public void viewMode() {
        getWidget().removeStyleName("pool-object-edit-mode");
        editing = false;
        fireEvent(new EditFinish());
    }

    public void editMode() {
        if (isEditable()) {
            getWidget().addStyleName("pool-object-edit-mode");
            editing = true;
            fireEvent(new EditStart());
        }
    }

    public boolean isEditing() {
        return editing;
    }

    public void setEditable(boolean editable) {
        this.editable = editable;
    }

    public boolean isEditable() {
        return editable;
    }


    @Override
    protected void initWidget(Widget widget) {
        this.innerWidget = widget;
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
            public void onClick(ClickEvent event) {
                onDelete.run();
            }
        }, ClickEvent.getType());

        addMouseOverHandler(new MouseOverHandler() {
            @Override
            public void onMouseOver(MouseOverEvent event) {
                if (isEditable()) {
                    deleteButton.removeStyleName("invisible");
                }
            }
        });
        addMouseOutHandler(new MouseOutHandler() {
            @Override
            public void onMouseOut(MouseOutEvent event) {
                deleteButton.addStyleName("invisible");
            }
        });


    }

    public HandlerRegistration addDragHandler(DragHandler dragHandler) {
        return addHandler(dragHandler, DragEvent.getType());
    }


    public HandlerRegistration addHoldDragHandler(HoldDragHandler dragHandler) {
        return addHandler(dragHandler, HoldDragEvent.getType());
    }

    public HandlerRegistration addEndDragHandler(EndDragHandler endDragHandler) {
        return addHandler(endDragHandler, EndDragEvent.getType());
    }

    public void setLogicalWidth(int width) {
        //resizing not supported

//        super.setWidth(width + "px");
        this.logicalWidth = width;
    }

    public void setLogicalHeight(int height) {
        //resizing not supported

//        super.setHeight(height + "px");
        this.logicalHeight = height;
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

    public void setOnDelete(Runnable onDelete) {
        this.onDelete = onDelete;
    }

    public void removeView(Widget widget) {
        throw new UnsupportedOperationException("This view does not support removing child views.");
    }

    public void addView(Widget widget) {
        throw new UnsupportedOperationException("This view does not support adding child views.");
    }

    public Widget getInnerWidget() {
        return innerWidget;
    }

    public void onAddToPool() {

    }
}
