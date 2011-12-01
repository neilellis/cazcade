package cazcade.boardcast.client.main.widgets.login;

import cazcade.liquid.api.LiquidSessionIdentifier;
import cazcade.vortex.comms.datastore.client.DataStoreService;
import cazcade.vortex.gwt.util.client.ClientLog;
import cazcade.vortex.widgets.client.form.fields.UsernameTextBox;
import cazcade.vortex.widgets.client.form.fields.VortexPasswordTextBox;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.*;

import javax.annotation.Nullable;

/**
 * @author neilellis@cazcade.com
 */
public class HashboLoginPanel extends Composite {
    private Runnable onSuccessAction;
    private Runnable onFailureAction;
    private Runnable onSwitchToRegisterAction;
    @Nullable
    private LiquidSessionIdentifier identity;


    interface LoginPanelUiBinder extends UiBinder<HTMLPanel, HashboLoginPanel> {
    }


    private static final LoginPanelUiBinder ourUiBinder = GWT.create(LoginPanelUiBinder.class);

    @UiField
    UsernameTextBox username;

    @UiField
    VortexPasswordTextBox password;

    @UiField
    Button loginButton;

    @UiField
    Label loginErrorMessage;

    @UiField
    Hyperlink register;

    public HashboLoginPanel() {
        initWidget(ourUiBinder.createAndBindUi(this));
        loginButton.addClickHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                submit();
            }
        });
        username.setOnChangeAction(new Runnable() {

            @Override
            public void run() {
                submit();
            }
        });

        password.setOnChangeAction(new Runnable() {

            @Override
            public void run() {
                submit();
            }
        });

        register.addClickHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                onSwitchToRegisterAction.run();
            }
        });


    }

    private void submit() {
        if (password.isValid() && username.isValid()) {
            DataStoreService.App.getInstance().login(username.getStringValue(), password.getStringValue(), new AsyncCallback<LiquidSessionIdentifier>() {
                @Override
                public void onFailure(Throwable caught) {
                    ClientLog.log(caught);
                }

                @Override
                public void onSuccess(@Nullable LiquidSessionIdentifier result) {
                    if (result == null) {
                        doFailure();
                    } else {
                        identity = result;
                        onSuccessAction.run();
                    }
                }
            });
        }
    }

    private void doFailure() {
        this.onFailureAction.run();
        loginErrorMessage.setText("Login failed");
    }


    public void setOnSuccessAction(Runnable onSuccessAction) {
        this.onSuccessAction = onSuccessAction;
    }

    public void setOnSwitchToRegisterAction(Runnable onSwitchToRegisterAction) {
        this.onSwitchToRegisterAction = onSwitchToRegisterAction;
    }

    public void setOnFailureAction(Runnable onFailureAction) {
        this.onFailureAction = onFailureAction;
    }

    @Nullable
    public LiquidSessionIdentifier getIdentity() {
        return identity;
    }


}