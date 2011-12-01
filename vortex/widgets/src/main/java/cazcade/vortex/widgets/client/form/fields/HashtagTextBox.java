package cazcade.vortex.widgets.client.form.fields;

import cazcade.liquid.api.LiquidBoardURL;
import cazcade.liquid.api.LiquidURI;
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
public class HashtagTextBox extends AbstractServerValidatedTextBox {


    private static final HashtagTextBoxUiBinder ourUiBinder = GWT.create(HashtagTextBoxUiBinder.class);

    interface HashtagTextBoxUiBinder extends UiBinder<HTMLPanel, HashtagTextBox> {
    }


    public HashtagTextBox() {
        initWidget(ourUiBinder.createAndBindUi(this));
        init();
    }

    @Override
    protected void checkAvailable() {

        if (showAvailability && isValidName()) {
            new Timer() {
                @Override
                public void run() {
                    DataStoreService.App.getInstance().checkBoardAvailability(new LiquidURI(LiquidBoardURL.convertFromShort(textBox.getText())), new AsyncCallback<Boolean>() {


                        @Override
                        public void onFailure(Throwable caught) {
                            ClientLog.log(caught);
                        }

                        @Override
                        public void onSuccess(Boolean result) {
                            acceptable = result;
                            if (result) {
                                showAvailable();


                            } else {
                                showTaken();
                            }
                        }
                    });
                }
            }.schedule(300);

        }
    }


}