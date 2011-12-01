package cazcade.vortex.common.client;

import com.google.gwt.i18n.client.DateTimeFormat;

import javax.annotation.Nonnull;
import java.util.Date;

/**
 * @author neilellis@cazcade.com
 */
public class DateUtil {
    public static final int MINUTE = 60000;
    public static final int HOUR = 3600000;
    public static final int SECOND = 1000;

    public static String toRelativeDate(@Nonnull final Date date) {
        final long now = System.currentTimeMillis();
        final long then = date.getTime();
        final long signedDiffSec = (then - now) / 1000;
        final long diffSec = Math.abs(signedDiffSec);
        final boolean past = signedDiffSec < 0;

        if (diffSec <= 1) {
            return "just now";
        }
        if (diffSec < 60) {
            if (past) {
                return diffSec + "s ago";
            } else {
                return "in " + diffSec + "s";
            }
        }
        final long diffMin = diffSec / 60;
        if (diffMin < 60) {
            if (past) {
                return diffMin + " min" + plural(diffMin) + " ago";
            } else {
                return "in " + diffMin + " min" + plural(diffMin);
            }
        }
        final long diffHour = diffMin / 60;
        if (diffHour < 12) {
            if (past) {
                return diffHour + " hour" + plural(diffHour) + " ago";
            } else {
                return "in " + diffHour + " hour" + plural(diffHour);
            }
        }
        if (diffHour < 24) {
            return DateTimeFormat.getFormat(DateTimeFormat.PredefinedFormat.TIME_SHORT).format(date);
        } else {
            return DateTimeFormat.getFormat(DateTimeFormat.PredefinedFormat.DATE_SHORT).format(date);
        }


    }

    @Nonnull
    private static String plural(final long amount) {
        return amount > 1 ? "s" : "";
    }
}
