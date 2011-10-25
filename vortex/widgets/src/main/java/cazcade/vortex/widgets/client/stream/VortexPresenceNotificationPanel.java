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

    private LSDEntity entity;
    private Date date = new Date();
    private LSDEntity visitor;
    private String id;

    interface VortexPresenceNotificationPanelUiBinder extends UiBinder<HTMLPanel, VortexPresenceNotificationPanel> {
    }

    private static VortexPresenceNotificationPanelUiBinder ourUiBinder = GWT.create(VortexPresenceNotificationPanelUiBinder.class);


    public VortexPresenceNotificationPanel(LSDEntity streamEntry, LiquidURI pool, String id) {
        this.id = id;

        initWidget(ourUiBinder.createAndBindUi(this));
        this.entity = streamEntry;
        visitor = streamEntry.getSubEntity(LSDAttribute.VISITOR);
        profileImage.setUrl(visitor.getAttribute(LSDAttribute.IMAGE_URL));
        if (pool.equals(entity.getURI())) {
            text.setInnerText(visitor.getAttribute(LSDAttribute.FULL_NAME) + " has just entered.");
        } else {
            text.setInnerText(visitor.getAttribute(LSDAttribute.FULL_NAME) + " has just left for " + streamEntry.getURI().asShortUrl());
        }
        dateTime.setDate(date);
    }

    @Override
    public LSDEntity getEntity() {
        return entity;
    }


    @Override
    public String getStreamIdentifier() {
        return id;
    }

    @Override
    public Date getSortDate() {
        return date;
    }

    @Override
    public int getAutoDeleteLifetime() {
        return 20;
    }
}