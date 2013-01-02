package cazcade.boardcast.client.main.menus.account;

import cazcade.vortex.common.client.UserUtil;
import cazcade.vortex.comms.datastore.client.DataStoreService;
import cazcade.vortex.gwt.util.client.ClientLog;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;

/**
 * @author neilellis@cazcade.com
 */
public class LogoutCommand implements Command {
    @Override
    public void execute() {
        DataStoreService.App.getInstance().logout(UserUtil.getIdentity(), new AsyncCallback<Void>() {
            @Override
            public void onFailure(final Throwable caught) {
                ClientLog.log(caught);
            }

            @Override
            public void onSuccess(final Void result) {
                Window.Location.reload();
            }
        }
                                                 );
    }
}

