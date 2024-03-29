package cazcade.vortex.widgets.client.panels.scroll;

import cazcade.vortex.dnd.client.browser.BrowserUtil;
import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.ui.Widget;

/**
 * @author neilellis@cazcade.com
 */
public class VerticalScrollBar extends ScrollBar {

    private final BrowserUtil browserUtil = GWT.create(BrowserUtil.class);

    public VerticalScrollBar(final Widget outer, final Widget inner) {
        super(outer, inner);
        addStyleName("vortex-vertical-scroll-bar");
//        DOM.setStyleAttribute(getElement(), "border", "1px solid purple");
    }

    @Override
    protected void onLoad() {
        super.onLoad();
        DOM.setStyleAttribute(bar.getElement(), "position", "absolute");
        DOM.setStyleAttribute(bar.getElement(), "left", "0px");
//        DOM.setStyleAttribute(getElement(), "position", "absolute");
        DOM.setStyleAttribute(getElement(), "right", "0px");
        DOM.setStyleAttribute(getElement(), "top", "0px");
        DOM.setStyleAttribute(getElement(), "zIndex", "100000");
        setHeight("100%");
        setWidth("8px");
        update(0);
    }

    public void update(final int offset) {
//        Window.alert("Updated "+outer.getElement().getOffsetHeight());
        if (inner.getElement().getOffsetHeight() > 0) {
            final int outerHeight = outer.getElement().getOffsetHeight();
            final int innerHeight = inner.getElement().getOffsetHeight();
            final int max = innerHeight - outerHeight;
            final double scale = (double) outerHeight / (double) innerHeight;
            browserUtil.translateXY(bar, 0, (int) (-offset * scale), 50);

            if (getOffsetHeight() * scale > 8) {
                bar.setHeight((int) (getOffsetHeight() * scale) - 8 + "px");
            } else {
                bar.setHeight("100%");
            }
//            bar.setHeight("100px");
            bar.setWidth(String.valueOf(getOffsetWidth()));

        }
    }


}