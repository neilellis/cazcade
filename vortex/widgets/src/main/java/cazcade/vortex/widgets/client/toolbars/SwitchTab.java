package cazcade.vortex.widgets.client.toolbars;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.SpanElement;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HorizontalPanel;

/**
 * @author Neil Ellis
 */

public class SwitchTab extends Composite {

    interface MyUiBinder extends UiBinder<HorizontalPanel, SwitchTab> {
    }

    private static MyUiBinder uiBinder = GWT.create(MyUiBinder.class);

    @UiField
    SpanElement optionLeftLabel;
    @UiField
    SpanElement optionMiddleLeftLabel;
    @UiField
    SpanElement optionMiddleRightLabel;
    @UiField
    SpanElement optionRightLabel;


    public SwitchTab() {
        // bind XML file of same name of this class to this class
        initWidget(uiBinder.createAndBindUi(this));
        optionLeftLabel.setInnerHTML("Left");
        optionRightLabel.setInnerHTML("Right");


    }
}