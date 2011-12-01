package cazcade.vortex.widgets.client.panels.list.dm;

import cazcade.liquid.api.lsd.LSDAttribute;
import cazcade.liquid.api.lsd.LSDBaseEntity;
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

    LSDBaseEntity entity;

    public LSDBaseEntity getEntity() {
        return entity;
    }

    @Nullable
    @Override
    public String getListIdentifier() {
        return entity.getURI().toString();
    }

    @Nullable
    @Override
    public String getStreamIdentifier() {
        return getListIdentifier();
    }

    @Nullable
    @Override
    public Date getSortDate() {
        return entity.getUpdated();
    }

    @Override
    public int getAutoDeleteLifetime() {
        return 60;
    }

    @Override
    public int compareTo(@Nonnull final ScrollableListEntry scrollableListEntry) {
        return entity.getUpdated().compareTo(scrollableListEntry.getEntity().getUpdated());
    }

    interface DirectMessageListEntryPanelUiBinder extends UiBinder<HTMLPanel, DirectMessageListEntryPanel> {
    }

    private static final DirectMessageListEntryPanelUiBinder ourUiBinder = GWT.create(DirectMessageListEntryPanelUiBinder.class);

    @UiField
    UserProfileImage profileImage;
    @UiField
    Label profileName;
    @UiField
    SpanElement text;
    @UiField
    SelfUpdatingRelativeDate dateTime;
    @UiField
    HTMLPanel imageSurround;

    protected DirectMessageListEntryPanel() {
        super();
    }

    public DirectMessageListEntryPanel(@Nonnull final LSDBaseEntity streamEntry, @Nonnull final FormatUtil features) {
        super();
        initWidget(ourUiBinder.createAndBindUi(this));
        init(streamEntry, features);
    }

    protected void init(@Nonnull final LSDBaseEntity streamEntry, @Nonnull final FormatUtil features) {
        entity = streamEntry;
        final LSDBaseEntity author = streamEntry.getSubEntity(LSDAttribute.AUTHOR, true);
        profileImage.setUrl(author.getAttribute(LSDAttribute.IMAGE_URL));
        profileImage.setAliasUri(author.getURI());

        profileName.setText("@" + author.getAttribute(LSDAttribute.NAME));
        profileName.addClickHandler(new ClickHandler() {
            @Override
            public void onClick(final ClickEvent clickEvent) {
                History.newItem(profileName.getText());
            }
        });


        text.setInnerHTML(features.formatRichText(streamEntry.getAttribute(LSDAttribute.TEXT_EXTENDED)));
        //todo - format date :-)
        final String publishedDateStringInMillis = streamEntry.getAttribute(LSDAttribute.PUBLISHED);
        if (publishedDateStringInMillis != null) {
            final Long publishedInMillis = Long.valueOf(publishedDateStringInMillis);
            dateTime.setDate(new Date(publishedInMillis));
        }
    }
}