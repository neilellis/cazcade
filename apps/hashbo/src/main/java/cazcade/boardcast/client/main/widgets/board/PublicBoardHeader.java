package cazcade.boardcast.client.main.widgets.board;

import cazcade.liquid.api.lsd.LSDAttribute;
import cazcade.liquid.api.lsd.LSDEntity;
import cazcade.liquid.api.request.UpdatePoolRequest;
import cazcade.vortex.bus.client.AbstractResponseCallback;
import cazcade.vortex.gwt.util.client.WidgetUtil;
import cazcade.vortex.widgets.client.form.fields.VortexEditableLabel;
import cazcade.vortex.widgets.client.image.EditableImage;
import cazcade.vortex.widgets.client.profile.Bindable;
import cazcade.vortex.widgets.client.profile.EntityBackedFormPanel;
import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.DivElement;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.HTMLPanel;

import javax.annotation.Nonnull;


/**
 * @author neilellis@cazcade.com
 */
public class PublicBoardHeader extends EntityBackedFormPanel {

    @UiField
    VortexEditableLabel title;
    @UiField
    VortexEditableLabel description;
    @UiField
    VortexEditableLabel tag;
    @UiField
    VortexEditableLabel url;
    @UiField
    DivElement contentArea;
    @UiField
    EditableImage boardIcon;

    @Nonnull
    @Override
    protected String getReferenceDataPrefix() {
        return "board";
    }


    @Nonnull
    @Override
    protected Runnable getUpdateEntityAction(@Nonnull final Bindable field) {
        return new Runnable() {
            @Override
            public void run() {
//                Window.alert("Sending..");
                getBus().send(new UpdatePoolRequest(field.getEntityDiff()), new AbstractResponseCallback<UpdatePoolRequest>() {
                    @Override
                    public void onSuccess(UpdatePoolRequest message, @Nonnull UpdatePoolRequest response) {
                        setEntity(response.getResponse().copy());
//                        Window.alert("Success..");
                    }

                    @Override
                    public void onFailure(UpdatePoolRequest message, @Nonnull UpdatePoolRequest response) {
//                        Window.alert("Failed.");
                        field.setErrorMessage(response.getResponse().getAttribute(LSDAttribute.DESCRIPTION));
                    }


                });
            }
        };
    }

    public void bind(LSDEntity entity) {
        super.bind(entity);
        addBinding(title, LSDAttribute.TITLE);
        addBinding(description, LSDAttribute.DESCRIPTION);
        addBinding(boardIcon, LSDAttribute.ICON_URL);

    }

    interface PublicBoardHeaderUiBinder extends UiBinder<HTMLPanel, PublicBoardHeader> {
    }

    private static final PublicBoardHeaderUiBinder ourUiBinder = GWT.create(PublicBoardHeaderUiBinder.class);

    public PublicBoardHeader() {
        HTMLPanel rootElement = ourUiBinder.createAndBindUi(this);
        initWidget(rootElement);
        WidgetUtil.hide(contentArea, false);

        tag.addClickHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                Window.alert("What did you expect to happen when you clicked this? Please let us know (info@hashbo.com)");
            }
        });
        url.addClickHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                Window.Location.replace(url.getValue());
            }
        });
        boardIcon.sinkEvents(Event.MOUSEEVENTS);
    }

    @Override
    protected void onChange(@Nonnull LSDEntity entity) {
        super.onChange(entity);
        WidgetUtil.showGracefully(this, true);
        final String shortUrl = entity.getURI().asShortUrl().asUrlSafe();
        if (shortUrl.startsWith("-")) {
            //unlisted board, probably has a nasty name so let's not show it here.
            WidgetUtil.hideGracefully(tag, false);
            WidgetUtil.hideGracefully(url, false);
        } else {
            WidgetUtil.showGracefully(tag, false);
            WidgetUtil.showGracefully(url, false);
        }

        tag.setValue("#" + shortUrl);
        final String externalURL = "http://boardca.st/" + shortUrl;
        url.setValue(externalURL);
        WidgetUtil.showGracefully(contentArea, false);
    }

}