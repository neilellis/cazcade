/*
 * Copyright (c) 2009-2013 Cazcade Limited  - All Rights Reserved
 */

package cazcade.vortex.widgets.client.profile;

import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.user.client.ui.HTMLPanel;

/**
 * @author neilellis@cazcade.com
 */
public class AliasDetailPanel extends AbstractAliasDetailPanel {


    interface AliasDetailPanelUiBinder extends UiBinder<HTMLPanel, AliasDetailPanel> {}

    private static final AliasDetailPanelUiBinder ourUiBinder = GWT.create(AliasDetailPanelUiBinder.class);


    public AliasDetailPanel() {
        super();
        initWidget(ourUiBinder.createAndBindUi(this));
        addBindings();

    }
}