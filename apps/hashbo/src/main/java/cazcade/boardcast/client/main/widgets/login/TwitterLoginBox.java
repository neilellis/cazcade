package cazcade.boardcast.client.main.widgets.login;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.Image;

/**
 * @author neilellis@cazcade.com
 */
public class TwitterLoginBox extends Composite {


    interface TwitterLoginBoxUiBinder extends UiBinder<HTMLPanel, TwitterLoginBox> {
    }

    private static TwitterLoginBoxUiBinder ourUiBinder = GWT.create(TwitterLoginBoxUiBinder.class);
    @UiField
    Image imageButton;
    @UiField
    HTMLPanel panel;

    private TwitterLoginPopup popup;

    @Override
    protected void onLoad() {
        popup = new TwitterLoginPopup();

        imageButton.addClickHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                popup.showCenter();
            }
        });


    }

    public TwitterLoginBox() {
        HTMLPanel rootElement = ourUiBinder.createAndBindUi(this);
        initWidget(rootElement);
    }


}