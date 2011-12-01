package cazcade.vortex.widgets.client.stream;

import cazcade.liquid.api.lsd.LSDAttribute;
import cazcade.liquid.api.lsd.LSDEntity;
import cazcade.vortex.common.client.FormatUtil;
import cazcade.vortex.gwt.util.client.history.HistoryManager;
import cazcade.vortex.widgets.client.date.SelfUpdatingRelativeDate;
import cazcade.vortex.widgets.client.image.UserProfileImage;
import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.SpanElement;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.Label;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Date;

/**
 * @author neilellis@cazcade.com
 */
public class CommentEntryPanel extends Composite implements StreamEntry {

    LSDEntity entity;

    public LSDEntity getEntity() {
        return entity;
    }


    @Nullable
    @Override
    public String getStreamIdentifier() {
        return entity.getURI().toString();
    }

    @Nullable
    @Override
    public Date getSortDate() {
        return entity.getPublished();
    }

    @Override
    public int getAutoDeleteLifetime() {
        return 20;
    }

    interface CommentEntryPanelUiBinder extends UiBinder<HTMLPanel, CommentEntryPanel> {
    }

    private static final CommentEntryPanelUiBinder ourUiBinder = GWT.create(CommentEntryPanelUiBinder.class);

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
    @UiField
    Label authorFullname;

    protected CommentEntryPanel() {
        super();
    }

    public CommentEntryPanel(@Nonnull final LSDEntity streamEntry) {
        super();
        entity = streamEntry;
        initWidget(ourUiBinder.createAndBindUi(this));
        final String locationText;
        if (streamEntry.hasAttribute(LSDAttribute.EURI)) {
            locationText = streamEntry.getAttribute(LSDAttribute.EURI);
        } else if (streamEntry.hasAttribute(LSDAttribute.SOURCE)) {
            locationText = streamEntry.getAttribute(LSDAttribute.SOURCE);
        } else {
            locationText = streamEntry.getURI().toString();
        }
//        location.setText(locationText);
        final LSDEntity author = streamEntry.getSubEntity(LSDAttribute.AUTHOR, false);
        profileImage.bind(author, LSDAttribute.IMAGE_URL, "");
        final String name = author.getAttribute(LSDAttribute.NAME);
        profileName.setText("@" + name);
        profileName.addClickHandler(new ClickHandler() {
            @Override
            public void onClick(final ClickEvent event) {
                HistoryManager.navigate("~" + name);
            }
        });
        authorFullname.setText(author.getAttribute(LSDAttribute.FULL_NAME));
        authorFullname.addClickHandler(new ClickHandler() {
            @Override
            public void onClick(final ClickEvent event) {
                HistoryManager.navigate("~" + name);
            }
        });
//        profileName.addClickHandler(new ClickHandler() {
//            @Override
//            public void onClick(ClickEvent clickEvent) {
//                History.newItem(profileName.getText());
//            }
//        });
        /* PRESENCE_UPDATE("Text.StatusUpdate.Presence", "A pool object status update."),
    OBJECT_UPDATE("Text.StatusUpdate.Object", "A pool object status update."),
    COMMENT_UPDATE("Text.StatusUpdate.Comment", "A comment based status update."),*/

        text.setInnerHTML(FormatUtil.getInstance().formatRichText(streamEntry.getAttribute(LSDAttribute.TEXT_BRIEF)));
        //todo - format date :-)
        final String publishedDateStringInMillis = streamEntry.getAttribute(LSDAttribute.PUBLISHED);
        if (publishedDateStringInMillis != null) {
            final Long publishedInMillis = Long.valueOf(publishedDateStringInMillis);
            dateTime.setDate(new Date(publishedInMillis));
            final double logTime = Math.log10((System.currentTimeMillis() - publishedInMillis) / 500000);
            //fade out older messages
            getElement().getStyle().setOpacity(1 - logTime / 10);
        }
    }
}