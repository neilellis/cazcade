/*
 * Copyright (c) 2009-2013 Cazcade Limited  - All Rights Reserved
 */

package cazcade.vortex.widgets.client.stream;

import cazcade.liquid.api.LiquidURI;
import cazcade.liquid.api.lsd.Dictionary;
import cazcade.liquid.api.lsd.Entity;
import cazcade.liquid.api.lsd.Types;
import cazcade.vortex.gwt.util.client.history.HistoryManager;
import cazcade.vortex.widgets.client.date.SelfUpdatingRelativeDate;
import cazcade.vortex.widgets.client.image.CachedImage;
import cazcade.vortex.widgets.client.image.UserProfileImage;
import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.SpanElement;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HTMLPanel;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Date;

/**
 * @author neilellis@cazcade.com
 */
public class VortexStatusUpdatePanel extends Composite implements StreamEntry {


    @Nonnull
    public static final String THE_VOID = "the void";
    @UiField UserProfileImage         profileImage;
    @UiField SpanElement              text;
    @UiField SelfUpdatingRelativeDate dateTime;

    @Nonnull
    private final Entity entity;
    @Nonnull
    private final Entity author;
    private final String authorFullName;
    private final String typeAsString;
    private final String locationName;
    @Nullable
    private final Date   date;

    interface VortexStatusUpdatePanelUiBinder extends UiBinder<HTMLPanel, VortexStatusUpdatePanel> {}

    private static final VortexStatusUpdatePanelUiBinder ourUiBinder = GWT.create(VortexStatusUpdatePanelUiBinder.class);


    public VortexStatusUpdatePanel(@Nonnull final Entity statusUpdate, final boolean large) {
        super();
        initWidget(ourUiBinder.createAndBindUi(this));
        if (large) {
            profileImage.setWidth("48px");
            profileImage.setHeight("64px");
            profileImage.setSize(CachedImage.MEDIUM);
        } else {
            profileImage.setWidth("14px");
            profileImage.setHeight("18px");
            profileImage.setSize(CachedImage.SMALL);
        }
        entity = statusUpdate;
        author = statusUpdate.child(Dictionary.AUTHOR_A, false);
        profileImage.setUrl(author.$(Dictionary.IMAGE_URL));
        if (statusUpdate.has$(Dictionary.SOURCE)) {
            locationName = new LiquidURI(entity.$(Dictionary.SOURCE)).withoutFragmentOrComment().board().safe();
        } else {
            locationName = THE_VOID;
        }
        authorFullName = author.$(Dictionary.FULL_NAME);
        typeAsString = entity.type().asString();
        if (statusUpdate.is(Types.T_OBJECT_UPDATE)) {
            text.setInnerText(authorFullName + " is making changes in " + locationName);
        } else if (statusUpdate.is(Types.T_COMMENT_UPDATE)) {
            text.setInnerText(author.$(Dictionary.FULL_NAME) + " is saying something in " + locationName);
        } else if (statusUpdate.is(Types.T_PRESENCE_UPDATE)) {
            text.setInnerText(author.$(Dictionary.FULL_NAME) + " is in " + locationName);
        } else {
            text.setInnerText("Unknown type " + statusUpdate.type());
        }
        addDomHandler(new ClickHandler() {
            @Override
            public void onClick(final ClickEvent event) {
                if (locationName != null && !THE_VOID.equals(locationName)) {
                    HistoryManager.get().navigate(locationName);
                }
            }
        }, ClickEvent.getType());
        date = entity.published();
        dateTime.setDate(date);
    }

    @Nonnull @Override
    public Entity getEntity() {
        return entity;
    }


    @Nonnull @Override
    public String getStreamIdentifier() {
        return authorFullName + locationName + typeAsString;
    }

    @Nullable @Override
    public Date getSortDate() {
        return date;
    }

    @Override
    public int getAutoDeleteLifetime() {
        return 20;
    }
}