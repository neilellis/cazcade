package cazcade.vortex.widgets.client.profile;

import cazcade.liquid.api.LiquidURI;
import cazcade.vortex.common.client.FormatUtil;
import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.gwt.user.client.ui.UIObject;

import javax.annotation.Nonnull;

/**
 * @author neilellis@cazcade.com
 */
public class ViewAliasDetailPanel extends PopupPanel {

    public static final int POPUP_HEIGHT = 180;
    public static final int POPUP_WIDTH = 360;

    private static ViewAliasDetailPanel current;

    public static ViewAliasDetailPanel createViewAliasDetailPanel(@Nonnull LiquidURI aliasURI, FormatUtil features) {
        if (current == null) {
            current = new ViewAliasDetailPanel(aliasURI, features);
        } else {
            current.init(aliasURI, features);
        }
        return current;
    }


    public void show(@Nonnull final UIObject container, final int relativeX, final int relativeY) {
        setPopupPositionAndShow(new PopupPanel.PositionCallback() {
            public void setPosition(int offsetWidth, int offsetHeight) {
                int x = relativeX;
                int y = relativeY;
                if (relativeX + POPUP_WIDTH > container.getOffsetWidth()) {
                    x = relativeX - POPUP_WIDTH;
                }
                if (relativeY + POPUP_HEIGHT > container.getOffsetHeight()) {
                    y = relativeY - POPUP_HEIGHT;
                }
                setPopupPosition(x, y);
            }
        });
    }

    interface ViewUserDetailPanelUiBinder extends UiBinder<HTMLPanel, ViewAliasDetailPanel> {
    }

    private static final ViewUserDetailPanelUiBinder ourUiBinder = GWT.create(ViewUserDetailPanelUiBinder.class);


    @UiField
    AliasDetailPanel detailPanel;


//    @Override
//    protected void onLoad() {
//        popup.showRelativeTo(trigger);
//    }

    private ViewAliasDetailPanel(@Nonnull LiquidURI aliasURI, FormatUtil features) {
        setAutoHideEnabled(true);
        setAutoHideOnHistoryEventsEnabled(true);
        setWidget(ourUiBinder.createAndBindUi(this));
        setWidth("360px");
        setHeight("200px");
        setGlassEnabled(false);
        setModal(false);
        setAutoHideEnabled(true);
        init(aliasURI, features);

    }

    private void init(@Nonnull LiquidURI aliasURI, FormatUtil features) {
        detailPanel.setAliasURI(aliasURI);
        detailPanel.setFeatures(features);
    }
}