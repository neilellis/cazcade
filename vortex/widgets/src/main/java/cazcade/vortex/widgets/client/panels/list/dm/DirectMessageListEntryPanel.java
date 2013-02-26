/*
 * Copyright (c) 2009-2013 Cazcade Limited  - All Rights Reserved
 */

package cazcade.vortex.widgets.client.panels.list.dm;

import cazcade.liquid.api.lsd.Dictionary;
import cazcade.liquid.api.lsd.Entity;
import cazcade.vortex.common.client.FormatUtil;
import cazcade.vortex.widgets.client.date.SelfUpdatingRelativeDate;
import cazcade.vortex.widgets.client.image.UserProfileImage;
import cazcade.vortex.widgets.client.panels.list.ScrollableListEntry;
import cazcade.vortex.widgets.client.stream.StreamEntry;
import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.SpanElement;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.History;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.Label;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Date;

/**
 * @author neilellis@cazcade.com
 */
public class DirectMessageListEntryPanel extends Composite implements ScrollableListEntry, StreamEntry {

    Entity entity;

    public Entity getEntity() {
        return entity;
    }

    @Nullable @Override
    public String getListIdentifier() {
        return entity.uri().toString();
    }

    @Nullable @Override
    public String getStreamIdentifier() {
        return getListIdentifier();
    }

    @Nullable @Override
    public Date getSortDate() {
        return entity.updated();
    }

    @Override
    public int getAutoDeleteLifetime() {
        return 60;
    }

    @Override
    public int compareTo(@Nonnull final ScrollableListEntry scrollableListEntry) {
        return entity.updated().compareTo(scrollableListEntry.getEntity().updated());
    }

    interface DirectMessageListEntryPanelUiBinder extends UiBinder<HTMLPanel, DirectMessageListEntryPanel> {}

    private static final DirectMessageListEntryPanelUiBinder ourUiBinder = GWT.create(DirectMessageListEntryPanelUiBinder.class);

    @UiField UserProfileImage         profileImage;
    @UiField Label                    profileName;
    @UiField SpanElement              text;
    @UiField SelfUpdatingRelativeDate dateTime;
    @UiField HTMLPanel                imageSurround;

    protected DirectMessageListEntryPanel() {
        super();
    }

    public DirectMessageListEntryPanel(@Nonnull final Entity streamEntry) {
        super();
        initWidget(ourUiBinder.createAndBindUi(this));
        init(streamEntry);
    }

    protected void init(@Nonnull final Entity streamEntry) {
        entity = streamEntry;
        final Entity author = streamEntry.child(Dictionary.AUTHOR_A, true);
        profileImage.setUrl(author.$(Dictionary.IMAGE_URL));
        profileImage.setAliasUri(author.uri());

        profileName.setText("@" + author.$(Dictionary.NAME));
        profileName.addClickHandler(new ClickHandler() {
            @Override
            public void onClick(final ClickEvent clickEvent) {
                History.newItem(profileName.getText());
            }
        });


        text.setInnerHTML(FormatUtil.getInstance().formatRichText(streamEntry.$(Dictionary.TEXT_EXTENDED)));
        //todo - format date :-)
        final String publishedDateStringInMillis = streamEntry.$(Dictionary.PUBLISHED);
        if (publishedDateStringInMillis != null) {
            final Long publishedInMillis = Long.valueOf(publishedDateStringInMillis);
            dateTime.setDate(new Date(publishedInMillis));
        }
    }
}