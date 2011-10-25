package cazcade.vortex.widgets.client.stream;

import cazcade.liquid.api.lsd.LSDAttribute;
import cazcade.liquid.api.lsd.LSDEntity;
import cazcade.vortex.common.client.FormatUtil;
import cazcade.vortex.widgets.client.date.SelfUpdatingRelativeDate;
import cazcade.vortex.widgets.client.image.UserProfileImage;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.History;
import com.google.gwt.user.client.ui.*;

import java.util.Date;

/**
 * @author neilellis@cazcade.com
 */
public class VortexStreamEntryPanel extends Composite implements StreamEntry {

    LSDEntity entity;

    public LSDEntity getEntity() {
        return entity;
    }


    @Override
    public String getStreamIdentifier() {
        return entity.getURI().toString();
    }

    @Override
    public Date getSortDate() {
        return entity.getUpdated();
    }

    @Override
    public int getAutoDeleteLifetime() {
        return 20;
    }

    interface VortexStreamEntryPanelUiBinder extends UiBinder<HTMLPanel, VortexStreamEntryPanel> {
    }

    private static VortexStreamEntryPanelUiBinder ourUiBinder = GWT.create(VortexStreamEntryPanelUiBinder.class);

    @UiField
    UserProfileImage profileImage;
    @UiField
    Label profileName;
    @UiField
    HTML text;
    @UiField
    Label location;
    @UiField
    SelfUpdatingRelativeDate dateTime;
    @UiField
    HTMLPanel imageSurround;

    protected VortexStreamEntryPanel() {
    }

    public VortexStreamEntryPanel(LSDEntity streamEntry, final FormatUtil features) {
        this.entity = streamEntry;
        initWidget(ourUiBinder.createAndBindUi(this));
        final LSDEntity author = streamEntry.getSubEntity(LSDAttribute.AUTHOR);
        profileImage.setUrl(author.getAttribute(LSDAttribute.IMAGE_URL));
        profileImage.setAliasUri(author.getURI());
        profileName.setText("@" + author.getAttribute(LSDAttribute.NAME));
        profileName.addClickHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent clickEvent) {
                History.newItem(profileName.getText());
            }
        });
        /* PRESENCE_UPDATE("Text.StatusUpdate.Presence", "A pool object status update."),
    OBJECT_UPDATE("Text.StatusUpdate.Object", "A pool object status update."),
    COMMENT_UPDATE("Text.StatusUpdate.Comment", "A comment based status update."),*/

        text.setHTML(features.formatRichText(streamEntry.getAttribute(LSDAttribute.TEXT_BRIEF)));
        //todo - format date :-)
        final String publishedDateStringInMillis = streamEntry.getAttribute(LSDAttribute.PUBLISHED);
        if (publishedDateStringInMillis != null) {
            final Long publishedInMillis = Long.valueOf(publishedDateStringInMillis);
            dateTime.setDate(new Date(publishedInMillis));
            final double logTime = Math.log10((System.currentTimeMillis() - publishedInMillis) / 500000);
            //fade out older messages
            this.getElement().getStyle().setOpacity(1 - (logTime / 10));
        }
    }
}