package cazcade.vortex.gwt.util.client.analytics;

import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;

/**
 * @author neilellis@cazcade.com
 */
public class Track implements ValueChangeHandler<String> {


    private String id;

    /**
     * constructor - nothing to do
     * @param id
     */
    public Track(String id) {
        this.id = id;
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

        historyToken = "/history/" + historyToken;

        trackGoogleAnalytics(id, historyToken);

    }

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