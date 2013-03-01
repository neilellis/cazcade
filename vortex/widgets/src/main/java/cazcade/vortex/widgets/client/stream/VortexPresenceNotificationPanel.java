/*
 * Copyright (c) 2009-2013 Cazcade Limited  - All Rights Reserved
 */

package cazcade.vortex.widgets.client.stream;

import cazcade.liquid.api.LURI;
import cazcade.liquid.api.lsd.Dictionary;
import cazcade.liquid.api.lsd.Entity;
import cazcade.vortex.widgets.client.date.SelfUpdatingRelativeDate;
import cazcade.vortex.widgets.client.image.UserProfileImage;
import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.SpanElement;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HTMLPanel;

import javax.annotation.Nonnull;
import java.util.Date;

/**
 * @author neilellis@cazcade.com
 */
public class VortexPresenceNotificationPanel extends Composite implements StreamEntry {


    @UiField UserProfileImage         profileImage;
    @UiField SpanElement              text;
    @UiField SelfUpdatingRelativeDate dateTime;

    @Nonnull
    private final Entity entity;
    @Nonnull
    private final Date date = new Date();
    @Nonnull
    private final Entity visitor;
    private final String id;

    interface VortexPresenceNotificationPanelUiBinder extends UiBinder<HTMLPanel, VortexPresenceNotificationPanel> {}

    private static final VortexPresenceNotificationPanelUiBinder ourUiBinder = GWT.create(VortexPresenceNotificationPanelUiBinder.class);


    public VortexPresenceNotificationPanel(@Nonnull final Entity streamEntry, @Nonnull final LURI pool, final String id) {
        super();
        this.id = id;

        initWidget(ourUiBinder.createAndBindUi(this));
        entity = streamEntry;
        visitor = streamEntry.child(Dictionary.VISITOR_A, false);
        profileImage.setUrl(visitor.$(Dictionary.IMAGE_URL));
        if (pool.equals(entity.uri())) {
            text.setInnerText(visitor.$(Dictionary.FULL_NAME) + " has just entered.");
        } else {
            text.setInnerText(visitor.$(Dictionary.FULL_NAME) + " has just left for " + streamEntry.uri().board());
        }
        dateTime.setDate(date);
    }

    @Nonnull @Override
    public Entity getEntity() {
        return entity;
    }


    @Override
    public String getStreamIdentifier() {
        return id;
    }

    @Nonnull @Override
    public Date getSortDate() {
        return date;
    }

    @Override
    public int getAutoDeleteLifetime() {
        return 20;
    }
}