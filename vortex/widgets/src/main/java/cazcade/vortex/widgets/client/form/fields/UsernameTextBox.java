/*
 * Copyright (c) 2009-2013 Cazcade Limited  - All Rights Reserved
 */

package cazcade.vortex.widgets.client.form.fields;

import cazcade.vortex.comms.datastore.client.DataStoreService;
import cazcade.vortex.gwt.util.client.ClientLog;
import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.HTMLPanel;

/**
 * @author neilellis@cazcade.com
 */
public class UsernameTextBox extends AbstractServerValidatedTextBox {


    interface UsernameTextBoxUiBinder extends UiBinder<HTMLPanel, UsernameTextBox> {}

    private static final UsernameTextBoxUiBinder ourUiBinder = GWT.create(UsernameTextBoxUiBinder.class);

    public UsernameTextBox() {
        super();
        initWidget(ourUiBinder.createAndBindUi(this));
        init();

        //        textBox.addValueChangeHandler(new ValueChangeHandler<String>() {
        //            @Override
        //            public void onValueChange(ValueChangeEvent<String> stringValueChangeEvent) {
        //
        //                String boardName = stringValueChangeEvent.getAttribute();
        //                boardName = cleanUpName(boardName);
        //                textBox.setText(boardName);
        ////                History.newItem(boardName);
        //
        //            }
        //        });
    }

    @Override
    protected void checkAvailable() {
        if (showAvailability) {
            new Timer() {
                @Override
                public void run() {
                    DataStoreService.App.getInstance().checkUsernameAvailability(textBox.getText(), new AsyncCallback<Boolean>() {


                        @Override
                        public void onFailure(final Throwable caught) {
                            ClientLog.log(caught);
                        }

                        @Override
                        public void onSuccess(final Boolean result) {
                            acceptable = result;
                            if (result) {
                                showAvailable();

                            }
                            else {
                                showTaken();
                            }
                        }
                    });
                }
            }.schedule(300);

        }
    }

    @Override
    public boolean isValid() {
        return acceptable && textBox.getText().matches("[a-zA-Z][a-zA-Z0-9_]*");
    }


}