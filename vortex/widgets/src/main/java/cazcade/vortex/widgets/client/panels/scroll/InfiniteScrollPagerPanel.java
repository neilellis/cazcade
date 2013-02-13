/*
 * Copyright (c) 2009-2013 Cazcade Limited  - All Rights Reserved
 */

package cazcade.vortex.widgets.client.panels.scroll;

import com.google.gwt.core.client.Scheduler;
import com.google.gwt.user.cellview.client.AbstractPager;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.view.client.HasRows;

/**
 * A scrolling pager that automatically increases the range every time the
 * window's scroll bar reaches the bottom.
 */
public class InfiniteScrollPagerPanel extends AbstractPager {

    /**
     * The default increment size.
     */
    private static final int       DEFAULT_INCREMENT = 20;
    /**
     * The scrollable panel.
     */
    private final        HTMLPanel widget            = new HTMLPanel("");
    /**
     * The increment size.
     */
    private              int       incrementSize     = DEFAULT_INCREMENT;

    /**
     * Construct a new {@link InfiniteScrollPagerPanel}.
     */
    public InfiniteScrollPagerPanel() {
        initWidget(widget);

        // Do not let the scrollable take tab focus.
        widget.getElement().setTabIndex(-1);
        Window.addWindowScrollHandler(new Window.ScrollHandler() {
            @Override public void onWindowScroll(final Window.ScrollEvent event) {
                Scheduler.get().scheduleDeferred(new Scheduler.ScheduledCommand() {
                    @Override public void execute() {
                        HasRows display = getDisplay();
                        if (display == null) {
                            return;
                        }

                        if (event.getScrollTop() >= RootPanel.getBodyElement().getOffsetHeight() - Window.getClientHeight() * 1.5) {
                            int newPageSize = Math.min(display.getVisibleRange().getLength() + incrementSize, display.getRowCount()
                                                                                                              + incrementSize);
                            //                            Window.alert("new page size "+newPageSize);
                            display.setVisibleRange(0, newPageSize);

                        }
                        else {
                            //                            Window.alert("RootPanel.getBodyElement().getOffsetHeight() - Window.getClientHeight() = "+(RootPanel.getBodyElement().getOffsetHeight() - Window.getClientHeight())+"event.getScrollTop() = "+event.getScrollTop());
                        }
                    }
                });
            }
        });
    }

    /**
     * Get the number of rows by which the range is increased when the scrollbar
     * reaches the bottom.
     *
     * @return the increment size
     */
    public int getIncrementSize() {
        return incrementSize;
    }

    /**
     * Set the number of rows by which the range is increased when the scrollbar
     * reaches the bottom.
     *
     * @param incrementSize the incremental number of rows
     */
    public void setIncrementSize(int incrementSize) {
        this.incrementSize = incrementSize;
    }

    @Override
    public void setDisplay(HasRows display) {
        assert display instanceof Widget : "display must extend Widget";
        widget.add((Widget) display);
        super.setDisplay(display);
    }

    @Override
    protected void onRangeOrRowCountChanged() {
    }


}
