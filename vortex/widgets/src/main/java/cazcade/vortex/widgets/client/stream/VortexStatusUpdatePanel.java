package cazcade.vortex.widgets.client.stream;

import cazcade.liquid.api.LiquidURI;
import cazcade.liquid.api.lsd.LSDAttribute;
import cazcade.liquid.api.lsd.LSDDictionaryTypes;
import cazcade.liquid.api.lsd.LSDEntity;
import cazcade.vortex.common.client.FormatUtil;
import cazcade.vortex.widgets.client.date.SelfUpdatingRelativeDate;
import cazcade.vortex.widgets.client.image.UserProfileImage;
import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.SpanElement;
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
public class VortexStatusUpdatePanel extends Composite implements StreamEntry {


    public static final String THE_VOID = "the void";
    @UiField
    UserProfileImage profileImage;
    @UiField
    SpanElement text;
    @UiField
    SelfUpdatingRelativeDate dateTime;

    private LSDEntity entity;
    private LSDEntity author;
    private String authorFullName;
    private String typeAsString;
    private String locationName;
    private Date date;

    interface VortexStatusUpdatePanelUiBinder extends UiBinder<HTMLPanel, VortexStatusUpdatePanel> {
    }

    private static VortexStatusUpdatePanelUiBinder ourUiBinder = GWT.create(VortexStatusUpdatePanelUiBinder.class);


    public VortexStatusUpdatePanel(LSDEntity statusUpdate, FormatUtil features) {
        initWidget(ourUiBinder.createAndBindUi(this));
        this.entity = statusUpdate;
        author = statusUpdate.getSubEntity(LSDAttribute.AUTHOR);
        profileImage.setUrl(author.getAttribute(LSDAttribute.IMAGE_URL));
        if (statusUpdate.hasAttribute(LSDAttribute.SOURCE)) {
            locationName = features.formatPoolName(new LiquidURI(entity.getAttribute(LSDAttribute.SOURCE)).getWithoutFragmentOrComment().asString());
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
            public void onClick(ClickEvent event) {
                if (locationName != null && !THE_VOID.equals(locationName)) {
                    History.newItem(locationName);
                }
            }
        }, ClickEvent.getType());
        date = entity.getPublished();
        dateTime.setDate(date);
    }

    @Override
    public LSDEntity getEntity() {
        return entity;
    }


    @Override
    public String getStreamIdentifier() {
        return authorFullName + locationName + typeAsString;
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