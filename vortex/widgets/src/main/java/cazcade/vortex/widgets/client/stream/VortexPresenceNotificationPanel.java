package cazcade.vortex.widgets.client.stream;

import cazcade.liquid.api.LiquidURI;
import cazcade.liquid.api.lsd.LSDAttribute;
import cazcade.liquid.api.lsd.LSDEntity;
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


    @UiField
    UserProfileImage profileImage;
    @UiField
    SpanElement text;
    @UiField
    SelfUpdatingRelativeDate dateTime;

    @Nonnull
    private final LSDEntity entity;
    @Nonnull
    private final Date date = new Date();
    @Nonnull
    private final LSDEntity visitor;
    private final String id;

    interface VortexPresenceNotificationPanelUiBinder extends UiBinder<HTMLPanel, VortexPresenceNotificationPanel> {
    }

    private static final VortexPresenceNotificationPanelUiBinder ourUiBinder = GWT.create(VortexPresenceNotificationPanelUiBinder.class);


    public VortexPresenceNotificationPanel(@Nonnull final LSDEntity streamEntry, @Nonnull final LiquidURI pool, final String id) {
        super();
        this.id = id;

        initWidget(ourUiBinder.createAndBindUi(this));
        entity = streamEntry;
        visitor = streamEntry.getSubEntity(LSDAttribute.VISITOR, false);
        profileImage.setUrl(visitor.getAttribute(LSDAttribute.IMAGE_URL));
        if (pool.equals(entity.getURI())) {
            text.setInnerText(visitor.getAttribute(LSDAttribute.FULL_NAME) + " has just entered.");
        } else {
            text.setInnerText(visitor.getAttribute(LSDAttribute.FULL_NAME) + " has just left for " + streamEntry.getURI().asShortUrl());
        }
        dateTime.setDate(date);
    }

    @Nonnull
    @Override
    public LSDEntity getEntity() {
        return entity;
    }


    @Override
    public String getStreamIdentifier() {
        return id;
    }

    @Nonnull
    @Override
    public Date getSortDate() {
        return date;
    }

    @Override
    public int getAutoDeleteLifetime() {
        return 20;
    }
}