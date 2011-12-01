package cazcade.vortex.widgets.client.stream;

import cazcade.liquid.api.LiquidURI;
import cazcade.liquid.api.lsd.LSDAttribute;
import cazcade.liquid.api.lsd.LSDBaseEntity;
import cazcade.liquid.api.lsd.LSDDictionaryTypes;
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
    @UiField
    UserProfileImage profileImage;
    @UiField
    SpanElement text;
    @UiField
    SelfUpdatingRelativeDate dateTime;

    @Nonnull
    private final LSDBaseEntity entity;
    @Nonnull
    private final LSDBaseEntity author;
    private final String authorFullName;
    private final String typeAsString;
    private final String locationName;
    @Nullable
    private final Date date;

    interface VortexStatusUpdatePanelUiBinder extends UiBinder<HTMLPanel, VortexStatusUpdatePanel> {
    }

    private static final VortexStatusUpdatePanelUiBinder ourUiBinder = GWT.create(VortexStatusUpdatePanelUiBinder.class);


    public VortexStatusUpdatePanel(@Nonnull final LSDBaseEntity statusUpdate, final boolean large) {
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
        author = statusUpdate.getSubEntity(LSDAttribute.AUTHOR, false);
        profileImage.setUrl(author.getAttribute(LSDAttribute.IMAGE_URL));
        if (statusUpdate.hasAttribute(LSDAttribute.SOURCE)) {
            locationName = new LiquidURI(entity.getAttribute(LSDAttribute.SOURCE)).getWithoutFragmentOrComment().asShortUrl().asUrlSafe();
        } else {
            locationName = THE_VOID;
        }
        authorFullName = author.getAttribute(LSDAttribute.FULL_NAME);
        typeAsString = entity.getTypeDef().asString();
        if (statusUpdate.isA(LSDDictionaryTypes.OBJECT_UPDATE)) {
            text.setInnerText(authorFullName + " is making changes in " + locationName);
        } else if (statusUpdate.isA(LSDDictionaryTypes.COMMENT_UPDATE)) {
            text.setInnerText(author.getAttribute(LSDAttribute.FULL_NAME) + " is saying something in " + locationName);
        } else if (statusUpdate.isA(LSDDictionaryTypes.PRESENCE_UPDATE)) {
            text.setInnerText(author.getAttribute(LSDAttribute.FULL_NAME) + " is in " + locationName);
        } else {
            text.setInnerText("Unknown type " + statusUpdate.getTypeDef());
        }
        addDomHandler(new ClickHandler() {
            @Override
            public void onClick(final ClickEvent event) {
                if (locationName != null && !THE_VOID.equals(locationName)) {
                    HistoryManager.navigate(locationName);
                }
            }
        }, ClickEvent.getType());
        date = entity.getPublished();
        dateTime.setDate(date);
    }

    @Nonnull
    @Override
    public LSDBaseEntity getEntity() {
        return entity;
    }


    @Nonnull
    @Override
    public String getStreamIdentifier() {
        return authorFullName + locationName + typeAsString;
    }

    @Nullable
    @Override
    public Date getSortDate() {
        return date;
    }

    @Override
    public int getAutoDeleteLifetime() {
        return 20;
    }
}