package cazcade.hashbo.client.main.widgets.login;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.IFrameElement;
import com.google.gwt.dom.client.Style;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.LoadEvent;
import com.google.gwt.event.dom.client.LoadHandler;
import com.google.gwt.event.logical.shared.InitializeEvent;
import com.google.gwt.event.logical.shared.InitializeHandler;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Frame;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.PopupPanel;

/**
 * @author neilellis@cazcade.com
 */
public class TwitterLoginPopup extends PopupPanel {
    interface TwitterLoginPopupUiBinder extends UiBinder<HTMLPanel, TwitterLoginPopup> {
    }

    private static TwitterLoginPopupUiBinder ourUiBinder = GWT.create(TwitterLoginPopupUiBinder.class);

    public void showCenter() {
        Window.open("./_twitter/signin", "Sign In With Twitter", "width=700,height=512");
    }

    public TwitterLoginPopup() {
        super(true, true);
        HTMLPanel rootElement = ourUiBinder.createAndBindUi(this);
        //    <g:PopupPanel glassEnabled="true" modal="true" width="700" height="512" ui:field="popup" >
        setGlassEnabled(true);
        this.add(rootElement);

    }

}