package cazcade.vortex.widgets.client.login;

import cazcade.liquid.api.LiquidSessionIdentifier;
import cazcade.vortex.gwt.util.client.history.HistoryAwareComposite;
import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.ui.Panel;
import com.google.gwt.user.client.ui.SimplePanel;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * @author neilellis@cazcade.com
 */
public class VortexLoginOrRegisterPanel extends HistoryAwareComposite {
    private boolean registerPanelShowing;

    @Nullable
    public LiquidSessionIdentifier getIdentity() {
        if (registerPanelShowing) {
            return null;
        } else {
            return loginPanel.getIdentity();
        }
    }

    @Override
    public void onLocalHistoryTokenChanged(final String token) {

    }

    interface LoginOrRegisterPanelUiBinder extends UiBinder<SimplePanel, VortexLoginOrRegisterPanel> {
    }

    private static final LoginOrRegisterPanelUiBinder ourUiBinder = GWT.create(LoginOrRegisterPanelUiBinder.class);

    @UiField
    LoginPanel loginPanel;

    @Nonnull
    final RegisterPanel registerPanel;

    public VortexLoginOrRegisterPanel(final Runnable loginAction, final Runnable registerAction) {
        super();
        initWidget(ourUiBinder.createAndBindUi(this));
        registerPanel = new RegisterPanel();
        loginPanel.setOnSuccessAction(loginAction);
        registerPanel.setOnSuccessAction(registerAction);

        loginPanel.setOnFailureAction(new Runnable() {
            @Override
            public void run() {
                getWidget().addStyleName("login-failed");
                new Timer() {
                    @Override
                    public void run() {
                        getWidget().removeStyleName("login-failed");

                    }
                }.schedule(2000);
            }
        });

        loginPanel.setOnSwitchToRegisterAction(new Runnable() {
            @Override
            public void run() {
                registerPanelShowing = true;
                loginPanel.addStyleName("invisible");
                getWidget().removeStyleName("login-panel");
                getWidget().addStyleName("register-panel");
                new Timer() {
                    @Override
                    public void run() {
                        final Panel parent = (Panel) loginPanel.getParent();
                        loginPanel.removeFromParent();
                        parent.add(registerPanel);
                        registerPanel.removeStyleName("invisible");
                    }
                }.schedule(600);
            }
        });

        registerPanel.setOnSwitchToLoginAction(new Runnable() {
            @Override
            public void run() {
                registerPanelShowing = false;
                registerPanel.addStyleName("invisible");
                getWidget().removeStyleName("register-panel");
                getWidget().addStyleName("login-panel");
                new Timer() {
                    @Override
                    public void run() {
                        final Panel parent = (Panel) registerPanel.getParent();
                        registerPanel.removeFromParent();
                        parent.add(loginPanel);
                        loginPanel.removeStyleName("invisible");
                    }
                }.schedule(600);
            }
        });
    }
}