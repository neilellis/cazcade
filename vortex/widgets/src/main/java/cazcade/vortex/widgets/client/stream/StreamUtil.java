package cazcade.vortex.widgets.client.stream;

import cazcade.vortex.gwt.util.client.VortexThreadSafeExecutor;
import cazcade.vortex.gwt.util.client.WidgetUtil;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.ComplexPanel;
import com.google.gwt.user.client.ui.InsertPanel;
import com.google.gwt.user.client.ui.VerticalPanel;

/**
 * @author neilellis@cazcade.com
 */
public class StreamUtil {

    public static void addStreamEntry(final int maxRows, final InsertPanel parentPanel, VortexThreadSafeExecutor threadSafeExecutor, final StreamEntry streamEntry, boolean autoDelete) {
        if (autoDelete) {
            WidgetUtil.removeFromParentGracefully(streamEntry, streamEntry.getAutoDeleteLifetime()*1000);
        }
        threadSafeExecutor.execute(new Runnable() {
            @Override
            public void run() {
                boolean inserted = false;
                int i = 0;
                while (i < parentPanel.getWidgetCount()) {
                    if (!(parentPanel.getWidget(i) instanceof StreamEntry)) {
                        continue;
                    }
                    final StreamEntry panel = (StreamEntry) parentPanel.getWidget(i);
                    if (streamEntry.getStreamIdentifier().equals(panel.getStreamIdentifier())) {

                        WidgetUtil.removeFromParentGracefully(panel);
//                        parentPanel.remove(panel);
                        break;
                    }
                    i++;
                }

                i = 0;
                for (i = 0; i < parentPanel.getWidgetCount(); i++) {
                    if (!(parentPanel.getWidget(i) instanceof StreamEntry)) {
                        continue;
                    }
                    final StreamEntry panel = (StreamEntry) parentPanel.getWidget(i);
                    if (panel.getSortDate().before(streamEntry.getSortDate())) {
                        WidgetUtil.insertGracefully(parentPanel, streamEntry, i);
                        inserted = true;
                        break;
                    }
                }
                if (!inserted) {
                    if (parentPanel.getWidgetCount() > 0) {
                        WidgetUtil.insertGracefully(parentPanel, streamEntry, parentPanel.getWidgetCount());
                    } else {
                        WidgetUtil.insertGracefully(parentPanel, streamEntry, 0);
                    }
                }
                if (parentPanel.getWidgetCount() > maxRows) {

                    WidgetUtil.removeFromParentGracefully(parentPanel.getWidget(parentPanel.getWidgetCount() - 1));
                }
            }
        });

    }
}
