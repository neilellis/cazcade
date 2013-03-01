/*
 * Copyright (c) 2009-2013 Cazcade Limited  - All Rights Reserved
 */

package cazcade.vortex.widgets.client.panels.list;

import cazcade.vortex.bus.client.Bus;
import cazcade.vortex.bus.client.BusService;
import cazcade.vortex.gwt.util.client.VortexThreadSafeExecutor;
import cazcade.vortex.gwt.util.client.WidgetUtil;
import cazcade.vortex.widgets.client.panels.scroll.VortexScrollPanel;
import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;

import javax.annotation.Nonnull;

/**
 * @author neilellis@cazcade.com
 */
public class ScrollableList extends Composite {
    interface ScrollableListUiBinder extends UiBinder<HTMLPanel, ScrollableList> {}

    private static final ScrollableListUiBinder ourUiBinder = GWT.create(ScrollableListUiBinder.class);

    @Nonnull
    private final BusService               bus                = Bus.get();
    private       int                      maxRows            = 100;
    @Nonnull
    private final VortexThreadSafeExecutor threadSafeExecutor = new VortexThreadSafeExecutor();

    @Nonnull
    private final VortexScrollPanel scrollPanel;

    public void setMaxRows(final int maxRows) {
        this.maxRows = maxRows;
    }


    public void clear() {
        WidgetUtil.removeAllChildren(parentPanel);
    }


    @Nonnull
    final VerticalPanel parentPanel;

    public ScrollableList() {
        super();
        final HTMLPanel widget = ourUiBinder.createAndBindUi(this);
        initWidget(widget);
        parentPanel = new VerticalPanel();
        parentPanel.setWidth("100%");
        scrollPanel = new VortexScrollPanel(parentPanel, false, true, false, null);
        widget.add(scrollPanel);
        //This will become the drag to refresh panel in time.
        parentPanel.add(new HTMLPanel(""));
    }


    public void addEntry(@Nonnull final ScrollableListEntry scrollableStreamContent) {
        threadSafeExecutor.execute(new Runnable() {
            @Override
            public void run() {
                //        entryEntities.add(scrollableStreamContent.$());
                boolean inserted = false;
                //                boolean atBottom = scrollPanel.isAtBottom();
                int i = parentPanel.getWidgetCount();
                while (i > 0) {
                    i--;
                    if (!(parentPanel.getWidget(i) instanceof ScrollableListEntry)) {
                        continue;
                    }
                    final ScrollableListEntry panel = (ScrollableListEntry) parentPanel.getWidget(i);
                    if (scrollableStreamContent.getListIdentifier().equals(panel.getListIdentifier())) {
                        parentPanel.remove(panel);
                        break;
                    }
                }

                i = parentPanel.getWidgetCount();
                while (i > 0) {
                    i--;
                    if (!(parentPanel.getWidget(i) instanceof ScrollableListEntry)) {
                        continue;
                    }
                    final ScrollableListEntry panel = (ScrollableListEntry) parentPanel.getWidget(i);
                    if (panel.compareTo(scrollableStreamContent) > 0) {
                        parentPanel.insert(scrollableStreamContent, i + 1);
                        inserted = true;
                        break;
                    }
                }
                if (!inserted) {
                    if (parentPanel.getWidgetCount() > 1) {
                        parentPanel.insert(scrollableStreamContent, 1);
                    } else {
                        parentPanel.add(scrollableStreamContent);
                    }
                }
                if (parentPanel.getWidgetCount() > maxRows) {
                    final Widget widgetToRemove = parentPanel.getWidget(1);
                    widgetToRemove.removeFromParent();
                }
                //                scrollPanel.scrollToBottom();
            }
        });

    }


}