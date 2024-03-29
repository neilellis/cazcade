/*
 * Copyright (c) 2009-2013 Cazcade Limited  - All Rights Reserved
 */

package cazcade.vortex.gwt.util.client.history;

import com.google.gwt.user.client.impl.HistoryImpl;

//https://groups.google.com/forum/#!msg/google-web-toolkit/kZp4R-DVNww/BQNo7ct_TpcJ

/**
 * History implementation based on pushState
 */
public class HTML5History extends HistoryImpl {

    public native boolean init() /*-{
        var token = '';

        var path = $wnd.location.pathname;
        if (path.charAt(0) == '/') {
            path = path.substring(1);
        }
        if (path.length > 0) {
            token =
                this.@com.google.gwt.user.client.impl.HistoryImpl::decodeFragment(Ljava/lang/String;)(path);
        }

        @com.google.gwt.user.client.impl.HistoryImpl::setToken(Ljava/lang/String;)(token);

        var historyImpl = this;

        var oldHandler = $wnd.history.onpopstate;

        $wnd.onpopstate = $entry(function () {
            var token = '';

            var path = $wnd.location.pathname;
            if (path.charAt(0) == '/') {
                path = path.substring(1);
            }

            if (path.length > 0) {
                token = historyImpl.@com.google.gwt.user.client.impl.HistoryImpl::decodeFragment(Ljava/lang/String;)(path);
            }


            historyImpl.@com.google.gwt.user.client.impl.HistoryImpl::newItemOnEvent(Ljava/lang/String;)(token);

            if (oldHandler) {
                oldHandler();
            }
        });

        $wnd.fireGWTHistoryEvent = function (nativeToken) {
            historyImpl.@com.google.gwt.user.client.impl.HistoryImpl::newItemOnEvent(Ljava/lang/String;)(nativeToken);
        };

        return true;
    }-*/;

    protected native void nativeUpdate(String historyToken) /*-{
        var encodedToken =
            this.@com.google.gwt.user.client.impl.HistoryImpl::encodeFragment(Ljava/lang/String;)(historyToken);
        if ($wnd.history.state != encodedToken) {
            $wnd.history.pushState(encodedToken, $wnd.document.title,
                encodedToken);
        }
    }-*/;
}
