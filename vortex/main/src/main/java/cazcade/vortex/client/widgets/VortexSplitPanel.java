/*
 * Copyright (c) 2009-2013 Cazcade Limited  - All Rights Reserved
 */

package cazcade.vortex.client.widgets;

import cazcade.liquid.api.LiquidURI;
import cazcade.liquid.api.SessionIdentifier;
import cazcade.liquid.api.lsd.Types;
import cazcade.vortex.common.client.FormatUtil;
import cazcade.vortex.pool.widgets.PoolContentArea;
import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.DockLayoutPanel;

/**
 * @author Neil Ellis
 */

public class VortexSplitPanel extends Composite {

    interface MyUiBinder extends UiBinder<DockLayoutPanel, VortexSplitPanel> {}

    private static final MyUiBinder uiBinder = GWT.create(MyUiBinder.class);
    @UiField PoolContentArea contentArea;

    public VortexSplitPanel(final SessionIdentifier identity) {
        super();
        // bind XML file of same name of this class to this class
        initWidget(uiBinder.createAndBindUi(this));
        contentArea.init(new LiquidURI("pool:///cazcade/playground"), FormatUtil.getInstance(), null, Types.T_POOL2D, false);

    }
}
