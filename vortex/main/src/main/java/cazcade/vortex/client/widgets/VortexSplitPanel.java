package cazcade.vortex.client.widgets;

import cazcade.liquid.api.LiquidSessionIdentifier;
import cazcade.liquid.api.LiquidURI;
import cazcade.liquid.api.lsd.LSDDictionaryTypes;
import cazcade.vortex.common.client.FormatUtil;
import cazcade.vortex.pool.widgets.PoolContentArea;
import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.*;

/**
 * @author Neil Ellis
 */

public class VortexSplitPanel extends Composite {

    interface MyUiBinder extends UiBinder<DockLayoutPanel, VortexSplitPanel> {
    }

    private static MyUiBinder uiBinder = GWT.create(MyUiBinder.class);
    @UiField
    PoolContentArea contentArea;

    public VortexSplitPanel(LiquidSessionIdentifier identity) {
        // bind XML file of same name of this class to this class
        initWidget(uiBinder.createAndBindUi(this));
        contentArea.init(new LiquidURI("pool:///cazcade/playground"), FormatUtil.getInstance(), null, LSDDictionaryTypes.POOL2D, false);

    }
}
