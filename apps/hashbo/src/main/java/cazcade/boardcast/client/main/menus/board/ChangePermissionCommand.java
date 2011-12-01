package cazcade.boardcast.client.main.menus.board;

import cazcade.liquid.api.LiquidPermissionChangeType;
import cazcade.liquid.api.LiquidURI;
import cazcade.liquid.api.request.ChangePermissionRequest;
import cazcade.vortex.bus.client.AbstractResponseCallback;
import cazcade.vortex.bus.client.BusFactory;
import cazcade.vortex.gwt.util.client.analytics.Track;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.Window;

import javax.annotation.Nonnull;


/**
 * @author neilellis@cazcade.com
 */
public class ChangePermissionCommand implements Command {
    private final LiquidPermissionChangeType change;
    private final LiquidURI poolURI;

    public ChangePermissionCommand(LiquidPermissionChangeType change, LiquidURI poolURI) {
        this.change = change;
        this.poolURI = poolURI;
    }

    @Override
    public void execute() {
        Track.getInstance().trackEvent("Permission Change", "Changed board permission to " + change);
        BusFactory.getInstance().send(new ChangePermissionRequest(poolURI, change), new AbstractResponseCallback<ChangePermissionRequest>() {
            @Override
            public void onSuccess(ChangePermissionRequest message, ChangePermissionRequest response) {
            }

            @Override
            public void onFailure(ChangePermissionRequest message, @Nonnull ChangePermissionRequest response) {
                Window.alert("Failed to (un)lock.");
            }
        });
    }
}
