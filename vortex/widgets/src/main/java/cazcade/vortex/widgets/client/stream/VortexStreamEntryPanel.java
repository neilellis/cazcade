/*
 * Copyright (c) 2009-2013 Cazcade Limited  - All Rights Reserved
 */

package cazcade.vortex.widgets.client.stream;

import cazcade.liquid.api.lsd.Dictionary;
import cazcade.liquid.api.lsd.Entity;
import cazcade.vortex.common.client.FormatUtil;
import cazcade.vortex.widgets.client.date.SelfUpdatingRelativeDate;
import cazcade.vortex.widgets.client.image.UserProfileImage;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.History;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.Label;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Date;

/**
 * @author neilellis@cazcade.com
 */
public class VortexStreamEntryPanel extends Composite implements StreamEntry {

    Entity entity;

    public Entity getEntity() {
        return entity;
    }


    @Nullable @Override
    public String getStreamIdentifier() {
        return String.valueOf(entity.uri());
    }

    @Nullable @Override
    public Date getSortDate() {
        return entity.updated();
    }

    @Override
    public int getAutoDeleteLifetime() {
        return 20;
    }

    interface VortexStreamEntryPanelUiBinder extends UiBinder<HTMLPanel, VortexStreamEntryPanel> {}

    private static final VortexStreamEntryPanelUiBinder ourUiBinder = GWT.create(VortexStreamEntryPanelUiBinder.class);

    @UiField UserProfileImage         profileImage;
    @UiField Label                    profileName;
    @UiField HTML                     text;
    @UiField Label                    location;
    @UiField SelfUpdatingRelativeDate dateTime;
    @UiField HTMLPanel                imageSurround;

    protected VortexStreamEntryPanel() {
        super();
    }

    public VortexStreamEntryPanel(@Nonnull final Entity streamEntry, @Nonnull final FormatUtil features) {
        super();
        entity = streamEntry;
        initWidget(ourUiBinder.createAndBindUi(this));
        final Entity author = streamEntry.child(Dictionary.AUTHOR_A, false);
        profileImage.setUrl(author.$(Dictionary.IMAGE_URL));
        profileImage.setAliasUri(author.uri());
        profileName.setText('@' + author.$(Dictionary.NAME));
        profileName.addClickHandler(new ClickHandler() {
            @Override
            public void onClick(final ClickEvent clickEvent) {
                History.newItem(profileName.getText());
            }
        });
        /* PRESENCE_UPDATE("Text.StatusUpdate.Presence", "A pool object status update."),
    OBJECT_UPDATE("Text.StatusUpdate.Object", "A pool object status update."),
    COMMENT_UPDATE("Text.StatusUpdate.Comment", "A comment based status update."),*/

        text.setHTML(features.formatRichText(streamEntry.$(Dictionary.TEXT_BRIEF)));
        //todo - format date :-)
        final String publishedDateStringInMillis = streamEntry.$(Dictionary.PUBLISHED);
        if (publishedDateStringInMillis != null) {
            final Long publishedInMillis = Long.valueOf(publishedDateStringInMillis);
            dateTime.setDate(new Date(publishedInMillis));
            final double logTime = Math.log10((System.currentTimeMillis() - publishedInMillis) / 500000);
            //fade out older messages
            getElement().getStyle().setOpacity(1 - logTime / 10);
        }
    }
}