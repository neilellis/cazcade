package cazcade.vortex.gwt.util.client.analytics;

import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;

import java.util.Map;

/**
 * @author neilellis@cazcade.com
 */
public class Track implements ValueChangeHandler<String> {


    private String googleId;

    /**
     * constructor - nothing to do
     *
     * @param googleId
     */
    public Track(String googleId) {
        this.googleId = googleId;
    }


    public void registerUser(String id, String fullname, Map<String, String> map) {
//        identifyUserMixpanel(id);
        identifyUserNameMixpanel(fullname);
        registerUserMixpanel(map);
    }

    /**
     * track an event
     *
     * @param historyToken
     */
    public void trackPage(String historyToken) {

        if (historyToken == null) {
            historyToken = "historyToken_null";
        }

        historyToken = "/" + historyToken;

        trackGoogleAnalytics(googleId, historyToken);
        trackMixpanel(historyToken);

    }


    public static native void registerUserMixpanel(Map<String, String> map) /*-{
        $wnd.mpq.register(map, "all", "False", 31);
    }-*/;

    public static native void identifyUserMixpanel(String name) /*-{
        $wnd.mpq.identify(name);
    }-*/;

    public static native void identifyUserNameMixpanel(String name) /*-{
        $wnd.mpq.name_tag(name);
    }-*/;

    public static native void trackMixpanel(String event) /*-{
        $wnd.mpq.track(name);
    }-*/;

    /**
     * trigger google analytic native js - included in the build
     * CHECK - DemoGoogleAnalytics.gwt.xml for -> <script src="../ga.js"/>
     * <p/>
     * http://code.google.com/intl/en-US/apis/analytics/docs/gaJS/gaJSApiEventTracking.html
     *
     * @param token
     */
    public static native void trackGoogleAnalytics(String trackerId, String token) /*-{
        return; //TODO: fix this
        try {

            // setup tracking object with account
            var pageTracker = _gat._getTracker(trackerId); // change account please!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!

            pageTracker._setRemoteServerMode();

            // turn on anchor observing
            pageTracker._setAllowAnchor(true);

            // send event to google server
            pageTracker._trackPageview(token);

        } catch(err) {

            // debug
            alert('FAILURE: to send in event to google analytics: ' + err);
        }


    }-*/;


    /**
     * This is so this class can be registered  for history changes.
     */
    @Override
    public void onValueChange(ValueChangeEvent<String> stringValueChangeEvent) {
        trackPage(stringValueChangeEvent.getValue());
    }
}