/*
 * Copyright (c) 2009-2013 Cazcade Limited  - All Rights Reserved
 */

package cazcade.vortex.widgets.client.login;

import cazcade.liquid.api.lsd.LSDBaseEntity;
import cazcade.liquid.api.lsd.LSDTransferEntity;
import cazcade.vortex.comms.datastore.client.DataStoreService;
import cazcade.vortex.gwt.util.client.ClientLog;
import cazcade.vortex.widgets.client.form.fields.RegexTextBox;
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
public class RegisterPanel extends Composite {

    private Runnable      onSwitchToLoginAction;
    private Runnable      onSuccessAction;
    @Nullable
    private LSDBaseEntity newUser;

    @Nullable
    public LSDBaseEntity getNewUser() {
        return newUser;
    }


    interface RegisterPanelUiBinder extends UiBinder<HTMLPanel, RegisterPanel> {}

    private static final RegisterPanelUiBinder ourUiBinder = GWT.create(RegisterPanelUiBinder.class);
    @UiField UsernameTextBox       username;
    @UiField VortexPasswordTextBox password;
    @UiField VortexPasswordTextBox passwordConfirm;
    @UiField Button                registerButton;
    @UiField Label                 registerErrorMessage;
    @UiField Hyperlink             login;
    @UiField RegexTextBox          fullname;
    @UiField RegexTextBox          email;

    public RegisterPanel() {
        super();
        initWidget(ourUiBinder.createAndBindUi(this));
        registerButton.addClickHandler(new ClickHandler() {
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

        passwordConfirm.addChangeHandler(new ValueChangeHandler() {
            @Override public void onValueChange(ValueChangeEvent event) {
                submit();
            }
        });

        login.addClickHandler(new ClickHandler() {
            @Override
            public void onClick(final ClickEvent event) {
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
        DataStoreService.App
                        .getInstance()
                        .register(fullname.getStringValue(), username.getStringValue(), password.getStringValue(), email.getStringValue(), new AsyncCallback<LSDTransferEntity>() {
                            @Override
                            public void onFailure(final Throwable caught) {
                                ClientLog.log(caught);
                            }

                            @Override
                            public void onSuccess(@Nullable final LSDTransferEntity result) {
                                if (result == null) {
                                    registerErrorMessage.setText("Could not register you.");
                                }
                                else {
                                    newUser = result;
                                    onSuccessAction.run();
                                }
                            }
                        });
    }

    public void setOnSwitchToLoginAction(final Runnable onSwitchToLoginAction) {
        this.onSwitchToLoginAction = onSwitchToLoginAction;
    }

    public void setOnSuccessAction(final Runnable onSuccessAction) {
        this.onSuccessAction = onSuccessAction;
    }


}