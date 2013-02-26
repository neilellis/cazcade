/*
 * Copyright (c) 2009-2013 Cazcade Limited  - All Rights Reserved
 */

package cazcade.vortex.widgets.client.image;

import cazcade.liquid.api.LiquidURI;
import cazcade.liquid.api.lsd.Attribute;
import cazcade.liquid.api.lsd.Dictionary;
import cazcade.liquid.api.lsd.TransferEntity;
import cazcade.vortex.common.client.FormatUtil;
import cazcade.vortex.gwt.util.client.ClientApplicationConfiguration;
import cazcade.vortex.widgets.client.profile.ViewAliasDetailPanel;
import com.google.gwt.event.dom.client.MouseEvent;
import com.google.gwt.event.dom.client.MouseOverEvent;
import com.google.gwt.event.dom.client.MouseOverHandler;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * @author neilellis@cazcade.com
 */
public class UserProfileImage extends EditableImage {

    private LiquidURI aliasUri;


    @Override
    protected void onLoad() {
        super.onLoad();
    }


    @Override
    protected void onAttach() {
        super.onAttach();
    }


    @Override
    public void bind(@Nonnull final TransferEntity entity, final Attribute attribute, final String referenceDataPrefix) {
        super.bind(entity, attribute, referenceDataPrefix);
        setAliasUri(entity.uri());
        if (ClientApplicationConfiguration.isAlphaFeatures() && (!entity.default$bool(Dictionary.EDITABLE, false) || !editable)) {
            image.addMouseOverHandler(new MouseOverHandler() {
                private ViewAliasDetailPanel aliasDetailPanel;

                @Override
                public void onMouseOver(final MouseOverEvent event) {
                    if (aliasDetailPanel == null) {
                        aliasDetailPanel = ViewAliasDetailPanel.createViewAliasDetailPanel(getAliasUri(), FormatUtil.getInstance());
                        showPopup(event);
                    } else {
                        showPopup(event);
                    }
                }

                private void showPopup(final MouseEvent event) {
                    aliasDetailPanel.showRelativeTo(getWidget());
                    //                    aliasDetailPanel.show(RootPanel.get(), event.getRelativeX(RootPanel.get().getElement()), event.getRelativeY(RootPanel.get().getElement()));
                }
            });
        }
    }

    public UserProfileImage() {
        super();
        setDefaultUrl("http://placehold.it/32x32");
        addStyleName("user-profile-image");

    }


    public UserProfileImage(final String url) {
        this();
        setUrl(url);

    }

    public void setUrl(@Nullable final String url) {
        if (url != null && !url.isEmpty()) {
            image.setUrl(url);
        }
    }

    public LiquidURI getAliasUri() {
        return aliasUri;
    }

    public void setAliasUri(final LiquidURI aliasUri) {
        this.aliasUri = aliasUri;
    }


    public void setDefaultUrl(final String defaultUrl) {
        image.setDefaultUrl(defaultUrl);
    }


}
