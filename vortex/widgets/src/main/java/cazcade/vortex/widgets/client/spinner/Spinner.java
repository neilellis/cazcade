/*
 * Copyright (c) 2009-2013 Cazcade Limited  - All Rights Reserved
 */

package cazcade.vortex.widgets.client.spinner;

import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.dom.client.Element;
import com.google.gwt.user.client.ui.Widget;

import javax.annotation.Nullable;

/**
 * @author neilellis@cazcade.com
 */
public class Spinner {

    private final     Widget           widget;
    @Nullable private JavaScriptObject spinner;
    private           boolean          started;

    public Spinner(final Widget w) {
        super();
        widget = w;
        spinner = init();

    }

    private static native JavaScriptObject start(JavaScriptObject spinnerInstance, Element element) /*-{
        return spinnerInstance.spin(element);
    }-*/;

    private static native void stop(JavaScriptObject spinnerInstance) /*-{
        spinnerInstance.stop();
    }-*/;

    private static native JavaScriptObject init() /*-{
        var opts = {
            lines: 13, // The number of lines to draw
            length: 21, // The length of each line
            width: 8, // The line thickness
            radius: 21, // The radius of the inner circle
            corners: 1, // Corner roundness (0..1)
            rotate: 0, // The rotation offset
            color: '#ddd', // #rgb or #rrggbb
            speed: 1, // Rounds per second
            trail: 60, // Afterglow percentage
            shadow: false, // Whether to render a shadow
            hwaccel: false, // Whether to use hardware acceleration
            className: 'spinner', // The CSS class to assign to the spinner
            zIndex: 2e9, // The z-index (defaults to 2000000000)
            top: 'auto', // Top position relative to parent in px
            left: 'auto' // Left position relative to parent in px
        };
        return new $wnd.Spinner(opts);
    }-*/;

    public void start() {
        started = true;
        spinner = start(spinner, widget.getElement());
    }

    public void stop() {
        if (started) {
            stop(spinner);
            started = false;
        }

    }


    public void update() {
        if (started && widget.isAttached()) {
            stop();
            start();
        }

    }
}