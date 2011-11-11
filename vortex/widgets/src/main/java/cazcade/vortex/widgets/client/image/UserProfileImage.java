package cazcade.vortex.widgets.client.image;

import cazcade.liquid.api.LiquidURI;
import cazcade.liquid.api.lsd.LSDAttribute;
import cazcade.liquid.api.lsd.LSDEntity;
import cazcade.vortex.common.client.FormatUtil;
import cazcade.vortex.widgets.client.profile.ViewAliasDetailPanel;
import com.google.gwt.event.dom.client.MouseEvent;
import com.google.gwt.event.dom.client.MouseOverEvent;
import com.google.gwt.event.dom.client.MouseOverHandler;

/**
 * @author neilellis@cazcade.com
 */
public class UserProfileImage extends EditableImage {

    private LiquidURI aliasUri;


    @Override
    protected void onLoad() {
        super.onLoad();
    }


    @Override
    protected void onAttach() {
        super.onAttach();
    }


    @Override
    public void bind(LSDEntity entity, LSDAttribute attribute, String referenceDataPrefix) {
        super.bind(entity, attribute, referenceDataPrefix);
        setAliasUri(entity.getURI());
        setUrl(entity.getAttribute(attribute));
        if (!entity.getBooleanAttribute(LSDAttribute.EDITABLE) || !editable) {
            image.addMouseOverHandler(new MouseOverHandler() {
                private ViewAliasDetailPanel aliasDetailPanel;

                @Override
                public void onMouseOver(MouseOverEvent event) {
                    if (aliasDetailPanel == null) {
                        aliasDetailPanel = ViewAliasDetailPanel.createViewAliasDetailPanel(getAliasUri(), FormatUtil.getInstance());
                        showPopup(event);
                    } else {
                        showPopup(event);
                    }
                }

                private void showPopup(MouseEvent event) {
                    aliasDetailPanel.showRelativeTo(getWidget());
//                    aliasDetailPanel.show(RootPanel.get(), event.getRelativeX(RootPanel.get().getElement()), event.getRelativeY(RootPanel.get().getElement()));
                }
            });
        }
    }

    public UserProfileImage() {
        setDefaultUrl("http://placehold.it/32x32");
        addStyleName("user-profile-image");

    }


    public UserProfileImage(String url) {
        this();
        setUrl(url);

    }

    public void setUrl(String url) {
        if (url != null && !url.isEmpty()) {
            image.setUrl(url);
        }
    }

    public LiquidURI getAliasUri() {
        return aliasUri;
    }

    public void setAliasUri(LiquidURI aliasUri) {
        this.aliasUri = aliasUri;
    }


    public void setDefaultUrl(String defaultUrl) {
        image.setDefaultUrl(defaultUrl);
    }


}
