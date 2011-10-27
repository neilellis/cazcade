package cazcade.hashbo.client.main.menus.board;

import cazcade.liquid.api.LiquidPermissionChangeType;
import cazcade.liquid.api.LiquidURI;
import cazcade.liquid.api.request.ChangePermissionRequest;
import cazcade.vortex.bus.client.AbstractResponseCallback;
import cazcade.vortex.bus.client.BusFactory;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.Window;

/**
 * @author neilellis@cazcade.com
 */
public class ChangePermissionCommand implements Command {
    private final LiquidPermissionChangeType change;
    private LiquidURI poolURI;

    public ChangePermissionCommand(LiquidPermissionChangeType change, LiquidURI poolURI) {
        this.change = change;
        this.poolURI = poolURI;
    }

    @Override
    public void execute() {

        BusFactory.getInstance().send(new ChangePermissionRequest(poolURI, change), new AbstractResponseCallback<ChangePermissionRequest>() {
            @Override
            public void onSuccess(ChangePermissionRequest message, ChangePermissionRequest response) {
            }

            @Override
            public void onFailure(ChangePermissionRequest message, ChangePermissionRequest response) {
                Window.alert("Failed to (un)lock.");
            }
        });
    }
}
