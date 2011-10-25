package cazcade.vortex.widgets.client.panels.scroll;

import cazcade.vortex.dnd.client.browser.BrowserUtil;
import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.ui.Widget;

/**
 * @author neilellis@cazcade.com
 */
public class HorizontalScrollBar extends ScrollBar {

    private BrowserUtil browserUtil = GWT.create(BrowserUtil.class);

    public HorizontalScrollBar(Widget outer, Widget inner) {
        super(outer, inner);
        setHeight("8px");
        setWidth("100%");
        addStyleName("vortex-horizontal-scroll-bar");
        update(0);
//        DOM.setStyleAttribute(getElement(), "border", "1px solid purple");
        DOM.setStyleAttribute(bar.getElement(), "position", "absolute");
        DOM.setStyleAttribute(bar.getElement(), "top", "0px");
        DOM.setStyleAttribute(bar.getElement(), "height", "8px");
        DOM.setStyleAttribute(getElement(), "bottom", "0px");
        DOM.setStyleAttribute(getElement(), "zIndex", "100000");

    }


    public void update(int offset) {
//        Window.alert("Updated "+outer.getElement().getOffsetWidth());
        if (inner.getElement().getOffsetWidth() > 0) {
            int outerWidth = outer.getElement().getOffsetWidth();
            int innerWidth = inner.getElement().getOffsetWidth();
            int max = (innerWidth - outerWidth);
            double scale = ((double) (outerWidth) / ((double) innerWidth));
            browserUtil.translateXY(bar, (int) ((-offset) * scale), 0, 50);

            if (getOffsetWidth() > 8) {
                bar.setWidth(String.valueOf((int) (getOffsetWidth() * scale) - 8) + "px");
            } else {
                bar.setWidth("100%");
            }
//            bar.setWidth("100px");
            bar.setHeight(String.valueOf(getOffsetHeight()));

        }
    }


}
