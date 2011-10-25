package cazcade.vortex.widgets.client.date;

import cazcade.vortex.common.client.DateUtil;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.ui.Label;

import java.util.Date;

import static cazcade.vortex.common.client.DateUtil.*;

/**
 * @author neilellis@cazcade.com
 */
public class SelfUpdatingRelativeDate extends Label {

    private Timer timer;

    public SelfUpdatingRelativeDate(final Date date) {
        this();
        setDate(date);
    }

    public SelfUpdatingRelativeDate() {
        addStyleName("self-updating-relative-date");
    }


    public void setDate(final Date date) {

        if (date == null) {
            throw new NullPointerException("Passed a null date into SelfUpdatingRelativeDate.setDate");
        }
        final long time = date.getTime();
        timer = new Timer() {
            @Override
            public void run() {
                setText(DateUtil.toRelativeDate(date));
                if (isAttached() || !isOrWasAttached()) {
                    if (Math.abs(System.currentTimeMillis() - time) < 30 * SECOND) {
                        timer.schedule(5 * SECOND);
                    } else if (Math.abs(System.currentTimeMillis() - time) < MINUTE) {
                        timer.schedule(10 * SECOND);
                    } else if (Math.abs(System.currentTimeMillis() - time) < HOUR) {
                        timer.schedule(MINUTE);
                    } else {
                        timer.schedule(HOUR);
                    }
                }

            }
        };
        timer.schedule(10);
    }
}