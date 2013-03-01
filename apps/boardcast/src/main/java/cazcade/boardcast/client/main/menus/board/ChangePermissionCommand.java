/*
 * Copyright (c) 2009-2013 Cazcade Limited  - All Rights Reserved
 */

package cazcade.boardcast.client.main.menus.board;

import cazcade.liquid.api.LURI;
import cazcade.liquid.api.PermissionChangeType;
import cazcade.liquid.api.request.ChangePermissionRequest;
import cazcade.vortex.bus.client.AbstractMessageCallback;
import cazcade.vortex.bus.client.Bus;
import cazcade.vortex.gwt.util.client.analytics.Track;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.Window;

import javax.annotation.Nonnull;


/**
 * @author neilellis@cazcade.com
 */
public class ChangePermissionCommand implements Command {
    private final PermissionChangeType change;
    private final LURI                 poolURI;

    public ChangePermissionCommand(final PermissionChangeType change, final LURI poolURI) {
        this.change = change;
        this.poolURI = poolURI;
    }

    @Override
    public void execute() {
        Track.getInstance().trackEvent("Permission Change", "Changed board permission to " + change);
        Bus.get().send(new ChangePermissionRequest(poolURI, change), new AbstractMessageCallback<ChangePermissionRequest>() {
            @Override
            public void onSuccess(final ChangePermissionRequest original, final ChangePermissionRequest message) {
            }

            @Override
                      public void onFailure(final ChangePermissionRequest original, @Nonnull final ChangePermissionRequest message) {
                          Window.alert("Failed to (un)lock.");
                      }
                  });
    }
}
