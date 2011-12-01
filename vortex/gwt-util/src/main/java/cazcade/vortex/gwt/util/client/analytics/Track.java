package cazcade.vortex.gwt.util.client.analytics;

import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Map;

/**
 * @author neilellis@cazcade.com
 */
public class Track implements ValueChangeHandler<String> {


    private static String googleId;
    private static Track instance;

    private Track() {
    }

    public static Track getInstance() {
        if (instance == null) {
            instance = new Track();
        }
        return instance;
    }


    public void registerUser(final String id, final String fullname, final Map<String, String> map) {
        identifyUserMixpanel(id);
        identifyUserNameMixpanel(fullname);
        registerUserMixpanel(map);
    }

    /**
     * track an event
     *
     * @param historyToken
     */
    public void trackPage(@Nullable String historyToken) {

        if (historyToken == null) {
            historyToken = "historyToken_null";
        }

        historyToken = "/" + historyToken;

        trackGoogleAnalytics(googleId, historyToken);

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
        $wnd.mpq.track(event);
    }-*/;

    public static native void trackMixpanelEvent(String event, String details) /*-{
        $wnd.mpq.track(event, {'mp_note': details});
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
    public void onValueChange(@Nonnull final ValueChangeEvent<String> stringValueChangeEvent) {
        trackPage(stringValueChangeEvent.getValue());
    }

    public static void setGoogleId(final String googleId) {
        Track.googleId = googleId;
    }

    public void trackEvent(final String event, final String details) {
        trackMixpanelEvent(event, details);
    }

    public void userRegistered(final String id, final String username, final Map<String, String> map) {
        trackMixpanel("$born");
        trackMixpanelEvent("Registered", username + " registered.");
        identifyUserMixpanel(id);
        identifyUserNameMixpanel(username);
        registerUserMixpanel(map);
    }
}