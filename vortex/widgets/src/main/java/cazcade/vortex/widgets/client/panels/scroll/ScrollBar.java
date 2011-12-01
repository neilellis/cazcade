package cazcade.vortex.widgets.client.panels.scroll;

import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.ui.AbsolutePanel;
import com.google.gwt.user.client.ui.Widget;

import javax.annotation.Nonnull;

/**
 * @author neilellis@cazcade.com
 */
public class ScrollBar extends AbsolutePanel {
    protected final Widget outer;
    protected final Widget inner;
    @Nonnull
    protected final Bar bar;


    public ScrollBar(Widget outer, Widget inner) {
        this.outer = outer;
        this.inner = inner;
        this.bar = new Bar();
        setStyleName("vortex-scroll-bar");
        DOM.setStyleAttribute(getElement(), "position", "absolute");
        add(bar);
    }


}
