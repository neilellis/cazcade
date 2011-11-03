package cazcade.boardcast.client.main.widgets.login;

import cazcade.liquid.api.lsd.LSDEntity;
import cazcade.vortex.comms.datastore.client.DataStoreService;
import cazcade.vortex.gwt.util.client.ClientLog;
import cazcade.vortex.widgets.client.form.fields.RegexTextBox;
import cazcade.vortex.widgets.client.form.fields.UsernameTextBox;
import cazcade.vortex.widgets.client.form.fields.VortexPasswordTextBox;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.*;

/**
 * @author neilellis@cazcade.com
 */
public class HashboRegisterPanel extends Composite {

    private Runnable onSwitchToLoginAction;
    private Runnable onSuccessAction;
    private LSDEntity newUser;

    public LSDEntity getNewUser() {
        return newUser;
    }


    interface RegisterPanelUiBinder extends UiBinder<HTMLPanel, HashboRegisterPanel> {
    }

    private static RegisterPanelUiBinder ourUiBinder = GWT.create(RegisterPanelUiBinder.class);
    @UiField
    UsernameTextBox username;
    @UiField
    VortexPasswordTextBox password;
    @UiField
    VortexPasswordTextBox passwordConfirm;
    @UiField
    Button registerButton;
    @UiField
    Label registerErrorMessage;
    @UiField
    Hyperlink login;
    @UiField
    RegexTextBox fullname;
    @UiField
    RegexTextBox email;

    public HashboRegisterPanel() {
        initWidget(ourUiBinder.createAndBindUi(this));
        registerButton.addClickHandler(new ClickHandler() {
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

        passwordConfirm.setOnChangeAction(new Runnable() {

            @Override
            public void run() {
                submit();
            }
        });

        login.addClickHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                onSwitchToLoginAction.run();
            }
        });

        passwordConfirm.setPairedBox(password);


    }

    private void submit() {
        if (!password.getStringValue().equals(passwordConfirm.getStringValue())) {
            registerErrorMessage.setText("Passwords did not match.");
            return;
        }
        if (!username.isValid()) {
            registerErrorMessage.setText("That username is taken.");
            return;
        }
        if (!password.isValid()) {
            registerErrorMessage.setText("Password must include letters and numbers.");
            return;
        }
        DataStoreService.App.getInstance().register(fullname.getStringValue(), username.getStringValue(), password.getStringValue(), email.getStringValue(), new AsyncCallback<LSDEntity>() {
            @Override
            public void onFailure(Throwable caught) {
                ClientLog.log(caught);
            }

            @Override
            public void onSuccess(LSDEntity result) {
                if (result == null) {
                    registerErrorMessage.setText("Could not register you.");
                } else {
                    newUser = result;
                    onSuccessAction.run();
                }
            }
        });
    }

    public void setOnSwitchToLoginAction(Runnable onSwitchToLoginAction) {
        this.onSwitchToLoginAction = onSwitchToLoginAction;
    }

    public void setOnSuccessAction(Runnable onSuccessAction) {
        this.onSuccessAction = onSuccessAction;
    }


}