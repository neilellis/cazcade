package cazcade.vortex.widgets.client.profile;

import cazcade.liquid.api.lsd.LSDEntity;
import cazcade.vortex.gwt.util.client.WidgetUtil;
import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.DivElement;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.HTMLPanel;

/**
 * @author neilellis@cazcade.com
 */
public class ProfileBoardHeader extends AbstractAliasDetailPanel {


    interface PublicBoardHeaderUiBinder extends UiBinder<HTMLPanel, ProfileBoardHeader> {
    }

    private static final PublicBoardHeaderUiBinder ourUiBinder = GWT.create(PublicBoardHeaderUiBinder.class);
    @UiField
    DivElement contentArea;

    public ProfileBoardHeader() {
        HTMLPanel rootElement = ourUiBinder.createAndBindUi(this);
        initWidget(rootElement);
        WidgetUtil.hide(contentArea, false);

    }


    @Override
    public void onChange(LSDEntity entity) {
        super.onChange(entity);
        WidgetUtil.showGracefully(contentArea, false);
    }
}