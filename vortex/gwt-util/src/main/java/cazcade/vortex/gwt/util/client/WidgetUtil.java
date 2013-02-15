/*
 * Copyright (c) 2009-2013 Cazcade Limited  - All Rights Reserved
 */

package cazcade.vortex.gwt.util.client;

import com.google.gwt.dom.client.Style;
import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.ui.ComplexPanel;
import com.google.gwt.user.client.ui.InsertPanel;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.Widget;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;

/**
 * @author neilellis@cazcade.com
 */
public class WidgetUtil {

    public static final long BEGINNING_OF_BOARDCAST_TIME = 1319231131793L;

    public static void removeAllChildren(@Nonnull final ComplexPanel container) {
        final List<Widget> widgets = new ArrayList<Widget>();
        final int widgetCount = container.getWidgetCount();
        for (int i = 0; i < widgetCount; i++) {
            final Widget widget = container.getWidget(i);
            widgets.add(widget);
        }
        for (final Widget widget : widgets) {
            widget.removeFromParent();
        }

    }

    public static void removeFromParentGracefully(@Nonnull final IsWidget widgetToRemove, final int transitionDelay) {
        widgetToRemove.asWidget().getElement().getStyle().setOpacity(0.0);
        new Timer() {
            @Override
            public void run() {
                widgetToRemove.asWidget().removeFromParent();
            }
        }.schedule(transitionDelay);
    }

    public static void removeFromParent(@Nonnull final IsWidget widgetToRemove) {
        widgetToRemove.asWidget().removeFromParent();
    }

    public static void removeFromParentGracefully(@Nonnull final IsWidget widgetToRemove) {
        removeFromParentGracefully(widgetToRemove, 500);
    }

    public static void swap(@Nonnull final Widget widget, @Nonnull final Widget replacement) {
        if (widget.getParent() != null) {
            if (widget.getParent().equals(replacement.getParent())) {
                widget.removeFromParent();
            }
            else {
                final ComplexPanel parent = (ComplexPanel) widget.getParent();
                widget.removeFromParent();
                parent.add(replacement);
            }
        }
    }

    public static void insert(@Nonnull final InsertPanel parentPanel, @Nonnull final IsWidget widget, final int pos) {
        parentPanel.insert(widget.asWidget(), pos);
    }

    public static void insertGracefully(@Nonnull final InsertPanel parentPanel, @Nonnull final IsWidget widget, final int pos) {
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

    public static void showGracefully(@Nonnull final IsWidget widget, final boolean verticalFlow) {
        widget.asWidget().getElement().getStyle().setOpacity(1.0);
        widget.asWidget().getElement().getStyle().setVisibility(Style.Visibility.VISIBLE);
        if (verticalFlow) {
            widget.asWidget().getElement().getStyle().setProperty("maxHeight", "100%");
        }
    }

    public static void hideGracefully(@Nonnull final IsWidget widget, final boolean verticalFlow) {
        hide(widget.asWidget(), verticalFlow);
        widget.asWidget().getElement().getStyle().setOpacity(0.0);
        new Timer() {
            @Override
            public void run() {
                widget.asWidget().setVisible(false);
            }
        }.schedule(500);
    }

    public static void hide(@Nonnull final IsWidget widget, final boolean verticalFlow) {
        final Widget element = widget.asWidget();
        hide(element, verticalFlow);
    }

    public static void hide(@Nonnull final Widget element, final boolean verticalFlow) {
        hide(element.getElement(), verticalFlow);
    }

    private static void hide(@Nonnull final Element element, final boolean verticalFlow) {
        element.getStyle().setVisibility(Style.Visibility.HIDDEN);
        element.getStyle().setOpacity(0.0);
        if (verticalFlow) {
            element.getStyle().setProperty("maxHeight", "0%");
        }
    }

    public static void hide(@Nonnull final com.google.gwt.dom.client.Element element, final boolean verticalFlow) {
        element.getStyle().setVisibility(Style.Visibility.HIDDEN);
        element.getStyle().setOpacity(0.0);
        if (verticalFlow) {
            element.getStyle().setProperty("maxHeight", "0%");
        }
    }

    public static void hideButKeepSize(@Nonnull final com.google.gwt.dom.client.Element element) {
        element.getStyle().setOpacity(0.0);
    }

    public static void showGracefully(@Nonnull final com.google.gwt.dom.client.Element element, final boolean verticalFlow) {
        element.getStyle().setVisibility(Style.Visibility.VISIBLE);
        element.getStyle().setOpacity(1.0);
        if (verticalFlow) {
            element.getStyle().setProperty("maxHeight", "100%");
        }
    }

    public static int secondsFromBeginningOfBoardcastEpoch() {
        return (int) ((System.currentTimeMillis() - BEGINNING_OF_BOARDCAST_TIME) / 1000);
    }

    public static void addGracefully(@Nonnull final ComplexPanel panel, @Nonnull final IsWidget view) {
        view.asWidget().getElement().getStyle().setOpacity(0.0);
        panel.add(view);
        view.asWidget().getElement().getStyle().setOpacity(1.0);
    }

    public static void show(@Nonnull final IsWidget isWidget) {
        isWidget.asWidget().setVisible(true);
    }

    /**
     * https://developer.mozilla.org/En/DOM:window.getComputedStyle
     * http://help.dottoro.com/ljswreks.php
     *
     * @param el   - dom element
     * @param prop - get the properties style, like width, fontFamily, fontSize, fontSizeAdjust. property must be camelCase
     * @return
     */
    public static native String getComputedStyle(Element el, String prop)  /*-{
        var computedStyle;
        if (document.defaultView && document.defaultView.getComputedStyle) { // standard (includes ie9)
            computedStyle = document.defaultView.getComputedStyle(el, null)[prop];

        } else if (el.currentStyle) { // IE older
            computedStyle = el.currentStyle[prop];

        } else { // inline style
            computedStyle = el.style[prop];
        }
        return computedStyle;
    }-*/;


}
