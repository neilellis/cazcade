package cazcade.vortex.gwt.util.client;

import com.google.gwt.core.client.RunAsyncCallback;
import com.google.gwt.dom.client.DivElement;
import com.google.gwt.dom.client.Style;
import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.ui.*;

import java.util.ArrayList;
import java.util.List;

/**
 * @author neilellis@cazcade.com
 */
public class WidgetUtil {

    public static final long BEGINNING_OF_BOARDCAST_TIME = 1319231131793L;

    public static void removeAllChildren(ComplexPanel container) {
        List<Widget> widgets = new ArrayList<Widget>();
        final int widgetCount = container.getWidgetCount();
        for (int i = 0; i < widgetCount; i++) {
            final Widget widget = container.getWidget(i);
            widgets.add(widget);
        }
        for (Widget widget : widgets) {
            widget.removeFromParent();
        }

    }
    public static void removeFromParentGracefully(final IsWidget widgetToRemove, int transitionDelay) {
        widgetToRemove.asWidget().getElement().getStyle().setOpacity(0.0);
        new Timer() {
            @Override
            public void run() {
                widgetToRemove.asWidget().removeFromParent();
            }
        }.schedule(transitionDelay);
    }

    public static void removeFromParentGracefully(final IsWidget widgetToRemove) {
        removeFromParentGracefully(widgetToRemove,  500);
    }

    public static void swap(Widget widget, Widget replacement) {
        if (widget.getParent() != null) {
            if (widget.getParent() == replacement.getParent()) {
                widget.removeFromParent();
            } else {
                final ComplexPanel parent = (ComplexPanel) widget.getParent();
                widget.removeFromParent();
                parent.add(replacement);
            }
        }
    }

    public static void insertGracefully(final InsertPanel parentPanel, final IsWidget widget, final int pos) {
        final Element childElement = widget.asWidget().getElement();
        widget.asWidget().getElement().getStyle().setProperty("maxHeight", "0");
        final Style style = childElement.getStyle();
        style.setOpacity(0.0);
//        final Element parentElement = parentPanel.getElement();
//        if (pos == 0 && parentElement.getChildCount() == 0) {
//            parentElement.appendChild(childElement);
//        } else {
//            parentElement.insertBefore(childElement, parentElement.getChild(pos));
//        }
        parentPanel.insert(widget.asWidget(), pos);
        new Timer() {
            @Override
            public void run() {
                widget.asWidget().getElement().getStyle().setProperty("maxHeight", "999px");
            }
        }.schedule(10);
        new Timer() {
            @Override
            public void run() {
                style.setOpacity(1.0);
            }
        }.schedule(400);

    }

    public static void showGracefully(IsWidget widget, boolean verticalFlow) {
        widget.asWidget().getElement().getStyle().setOpacity(1.0);
        widget.asWidget().getElement().getStyle().setVisibility(Style.Visibility.VISIBLE);
        if(verticalFlow) {
            widget.asWidget().getElement().getStyle().setProperty("maxHeight", "100%");
        }
    }

    public static void hideGracefully(final IsWidget widget, boolean verticalFlow) {
        hide(widget.asWidget(), verticalFlow);
        widget.asWidget().getElement().getStyle().setOpacity(0.0);
        new Timer() {
            @Override
            public void run() {
                widget.asWidget().setVisible(false);
            }
        }.schedule(500);    }

    public static void hide(final IsWidget widget, boolean verticalFlow) {
        final Widget element = widget.asWidget();
        hide(element, verticalFlow);
    }

    public static void hide(Widget element, boolean verticalFlow) {
        hide(element.getElement(), verticalFlow);
    }

    private static void hide(Element element, boolean verticalFlow) {
        element.getStyle().setVisibility(Style.Visibility.HIDDEN);
        element.getStyle().setOpacity(0.0);
        if(verticalFlow) {
            element.getStyle().setProperty("maxHeight", "0%");
        }
    }

    public static void hide(com.google.gwt.dom.client.Element element, boolean verticalFlow) {
        element.getStyle().setVisibility(Style.Visibility.HIDDEN);
        element.getStyle().setOpacity(0.0);
        if(verticalFlow) {
            element.getStyle().setProperty("maxHeight", "0%");
        }
    }

    public static void showGracefully(com.google.gwt.dom.client.Element element, boolean verticalFlow) {
        element.getStyle().setVisibility(Style.Visibility.VISIBLE);
        element.getStyle().setOpacity(1.0);
        if(verticalFlow) {
            element.getStyle().setProperty("maxHeight", "100%");
        }
    }


    public static int secondsFromBeginningOfBoardcastEpoch() {
        return (int) ((System.currentTimeMillis() - BEGINNING_OF_BOARDCAST_TIME) / 1000);
    }
}
