/*
 * Copyright (c) 2009-2013 Cazcade Limited  - All Rights Reserved
 */

package cazcade.boardcast.client.main.widgets.login;

import cazcade.liquid.api.LiquidSessionIdentifier;
import cazcade.vortex.gwt.util.client.WidgetUtil;
import cazcade.vortex.gwt.util.client.analytics.Track;
import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.ui.DialogBox;
import com.google.gwt.user.client.ui.HTMLPanel;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * @author neilellis@cazcade.com
 */
public class BoardcastLoginOrRegisterPanel extends DialogBox {
    private static final LoginOrRegisterPanelUiBinder ourUiBinder = GWT.create(LoginOrRegisterPanelUiBinder.class);

    @UiField BoardcastLoginPanel loginPanel;

    @Nonnull
    final   BoardcastRegisterPanel registerPanel;
    private boolean                registerPanelShowing;

    public BoardcastLoginOrRegisterPanel(final boolean register, final Runnable loginAction, final Runnable registerAction) {
        super();
        setWidget(ourUiBinder.createAndBindUi(this));
        setWidth("640px");
        setHeight("350px");
        setText("Login");
        setGlassEnabled(true);
        setModal(false);
        registerPanel = new BoardcastRegisterPanel();
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
                switchToRegister();
            }
        });

        registerPanel.setOnSwitchToLoginAction(new Runnable() {
            @Override
            public void run() {
                setText("Login");
                registerPanelShowing = false;
                WidgetUtil.swap(registerPanel, loginPanel);
                setWidth("640px");
                setHeight("350px");
                getWidget().removeStyleName("register-panel");
                getWidget().addStyleName("login-panel");
                Track.getInstance().trackEvent("Login", "Switched to login panel.");
            }
        });
        if (register) {
            switchToRegister();
        }
    }

    private void switchToRegister() {
        setText("Register with Boardcast");
        registerPanelShowing = true;
        getWidget().removeStyleName("login-panel");
        getWidget().addStyleName("register-panel");
        WidgetUtil.swap(loginPanel, registerPanel);
        setWidth("650px");
        setHeight("450px");
        Track.getInstance().trackEvent("Register", "Switched to register panel.");
    }

    @Nullable
    public LiquidSessionIdentifier getIdentity() {
        if (registerPanelShowing) {
            return null;
        }
        else {
            return loginPanel.getIdentity();
        }
    }

    interface LoginOrRegisterPanelUiBinder extends UiBinder<HTMLPanel, BoardcastLoginOrRegisterPanel> {}
}