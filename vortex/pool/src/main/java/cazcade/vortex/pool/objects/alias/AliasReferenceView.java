/*
 * Copyright (c) 2009-2013 Cazcade Limited  - All Rights Reserved
 */

package cazcade.vortex.pool.objects.alias;

import cazcade.liquid.api.LURI;
import cazcade.vortex.pool.objects.PoolObjectView;
import cazcade.vortex.widgets.client.profile.AliasDetailPanel;
import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.HTMLPanel;

import javax.annotation.Nonnull;

/**
 * @author neilellis@cazcade.com
 */
public class AliasReferenceView extends PoolObjectView {


    interface ImageObjectUiBinder extends UiBinder<HTMLPanel, AliasReferenceView> {}


    @UiField AliasDetailPanel aliasDetailPanel;

    @Override
    protected void onLoad() {
        super.onLoad();
    }

    private static final ImageObjectUiBinder ourUiBinder = GWT.create(ImageObjectUiBinder.class);

    public void setAliasURI(@Nonnull final LURI aliasURI) {
        aliasDetailPanel.setAliasURI(aliasURI);
    }

    public AliasReferenceView() {
        super();
        final HTMLPanel widget = ourUiBinder.createAndBindUi(this);
        initWidget(widget);
    }


    @Override public int getDefaultZIndex() {
        return 1000;
    }
}