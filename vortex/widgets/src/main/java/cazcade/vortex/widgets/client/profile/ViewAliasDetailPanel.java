package cazcade.vortex.widgets.client.profile;

import cazcade.liquid.api.LiquidURI;
import cazcade.vortex.common.client.FormatUtil;
import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.gwt.user.client.ui.UIObject;

/**
 * @author neilellis@cazcade.com
 */
public class ViewAliasDetailPanel extends Composite {

    public static final int POPUP_HEIGHT = 180;
    public static final int POPUP_WIDTH = 360;

    public void hide() {
        popup.hide();
    }

    public void show(final UIObject container, final int relativeX, final int relativeY) {
        popup.setPopupPositionAndShow(new PopupPanel.PositionCallback() {
            public void setPosition(int offsetWidth, int offsetHeight) {
                int x = relativeX;
                int y = relativeY;
                if (relativeX + POPUP_WIDTH > container.getOffsetWidth()) {
                    x = relativeX - POPUP_WIDTH;
                }
                if (relativeY + POPUP_HEIGHT > container.getOffsetHeight()) {
                    y = relativeY - POPUP_HEIGHT;
                }
                popup.setPopupPosition(x, y);
            }
        });
    }

    interface ViewUserDetailPanelUiBinder extends UiBinder<HTMLPanel, ViewAliasDetailPanel> {
    }

    private static ViewUserDetailPanelUiBinder ourUiBinder = GWT.create(ViewUserDetailPanelUiBinder.class);

    @UiField
    PopupPanel popup;
    @UiField
    AliasDetailPanel detailPanel;


//    @Override
//    protected void onLoad() {
//        popup.showRelativeTo(trigger);
//    }

    public ViewAliasDetailPanel(LiquidURI aliasURI, FormatUtil features) {
        initWidget(ourUiBinder.createAndBindUi(this));
        detailPanel.setAliasURI(aliasURI);
        detailPanel.setFeatures(features);

    }
}