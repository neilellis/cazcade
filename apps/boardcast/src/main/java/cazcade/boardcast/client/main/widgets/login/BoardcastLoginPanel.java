/*
 * Copyright (c) 2009-2013 Cazcade Limited  - All Rights Reserved
 */

package cazcade.boardcast.client.main.widgets.login;

import cazcade.liquid.api.LiquidSessionIdentifier;
import cazcade.vortex.comms.datastore.client.DataStoreService;
import cazcade.vortex.gwt.util.client.ClientLog;
import cazcade.vortex.widgets.client.form.fields.UsernameTextBox;
import cazcade.vortex.widgets.client.form.fields.VortexPasswordTextBox;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.*;

import javax.annotation.Nullable;

/**
 * @author neilellis@cazcade.com
 */
public class BoardcastLoginPanel extends Composite {
    private static final LoginPanelUiBinder ourUiBinder = GWT.create(LoginPanelUiBinder.class);

    @UiField UsernameTextBox username;

    @UiField VortexPasswordTextBox password;

    @UiField Button loginButton;

    @UiField Label loginErrorMessage;

    @UiField Hyperlink               register;
    private  Runnable                onSuccessAction;
    private  Runnable                onFailureAction;
    private  Runnable                onSwitchToRegisterAction;
    @Nullable
    private  LiquidSessionIdentifier identity;

    public BoardcastLoginPanel() {
        super();
        initWidget(ourUiBinder.createAndBindUi(this));
        loginButton.addClickHandler(new ClickHandler() {
            @Override
            public void onClick(final ClickEvent event) {
                submit();
            }
        });
        username.addChangeHandler(new ValueChangeHandler() {
            @Override public void onValueChange(ValueChangeEvent event) {
                submit();

            }
        });
        password.addChangeHandler(new ValueChangeHandler() {
            @Override public void onValueChange(ValueChangeEvent event) {
                submit();
            }
        });

        register.addClickHandler(new ClickHandler() {
            @Override
            public void onClick(final ClickEvent event) {
                onSwitchToRegisterAction.run();
            }
        });
    }

    private void submit() {
        if (password.isValid() && username.isValid()) {
            DataStoreService.App
                            .getInstance()
                            .login(username.getStringValue(), password.getStringValue(), new AsyncCallback<LiquidSessionIdentifier>() {
                                @Override
                                public void onFailure(final Throwable caught) {
                                    ClientLog.log(caught);
                                }

                                @Override
                                public void onSuccess(@Nullable final LiquidSessionIdentifier result) {
                                    if (result == null) {
                                        doFailure();
                                    }
                                    else {
                                        identity = result;
                                        onSuccessAction.run();
                                    }
                                }
                            });
        }
    }

    private void doFailure() {
        onFailureAction.run();
        loginErrorMessage.setText("Login failed");
    }

    @Nullable
    public LiquidSessionIdentifier getIdentity() {
        return identity;
    }

    public void setOnFailureAction(final Runnable onFailureAction) {
        this.onFailureAction = onFailureAction;
    }

    public void setOnSuccessAction(final Runnable onSuccessAction) {
        this.onSuccessAction = onSuccessAction;
    }

    public void setOnSwitchToRegisterAction(final Runnable onSwitchToRegisterAction) {
        this.onSwitchToRegisterAction = onSwitchToRegisterAction;
    }

    interface LoginPanelUiBinder extends UiBinder<HTMLPanel, BoardcastLoginPanel> {}
}