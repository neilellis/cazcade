/*
 * Copyright (c) 2009-2013 Cazcade Limited  - All Rights Reserved
 */

package cazcade.boardcast.client.main.widgets.board;

import cazcade.liquid.api.lsd.LSDAttribute;
import cazcade.liquid.api.lsd.LSDBaseEntity;
import cazcade.liquid.api.lsd.LSDTransferEntity;
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
    private static final PublicBoardHeaderUiBinder ourUiBinder = GWT.create(PublicBoardHeaderUiBinder.class);

    @UiField VortexEditableLabel title;
    @UiField VortexEditableLabel description;
    @UiField VortexEditableLabel tag;
    @UiField VortexEditableLabel url;
    @UiField DivElement          contentArea;
    @UiField EditableImage       boardImage;

    public PublicBoardHeader() {
        super();
        final HTMLPanel rootElement = ourUiBinder.createAndBindUi(this);
        initWidget(rootElement);
        WidgetUtil.hide(contentArea, false);

        tag.addClickHandler(new ClickHandler() {
            @Override
            public void onClick(final ClickEvent event) {
                Window.alert("What did you expect to happen when you clicked this? Please let us know (info@boardcast.com)");
            }
        });
        url.addClickHandler(new ClickHandler() {
            @Override
            public void onClick(final ClickEvent event) {
                Window.Location.replace(url.getValue());
            }
        });
        boardImage.sinkEvents(Event.MOUSEEVENTS);
        addBinding(title, LSDAttribute.TITLE);
        addBinding(description, LSDAttribute.DESCRIPTION);
        addBinding(boardImage, LSDAttribute.IMAGE_URL);
    }

    public void bindEntity(final LSDTransferEntity entity) {
        super.bindEntity(entity);
    }

    @Override protected boolean isSaveOnExit() {
        return false;
    }

    @Nonnull @Override
    protected String getReferenceDataPrefix() {
        return "board";
    }

    @Nonnull @Override
    protected Runnable getUpdateEntityAction(@Nonnull final Bindable field) {
        return new Runnable() {
            @Override
            public void run() {
                //                Window.alert("Sending..");
                getBus().send(new UpdatePoolRequest(field.getEntityDiff()), new AbstractResponseCallback<UpdatePoolRequest>() {
                    @Override
                    public void onSuccess(final UpdatePoolRequest message, @Nonnull final UpdatePoolRequest response) {
                        setEntity(response.getResponse().copy());
                        //                        Window.alert("Success..");
                    }

                    @Override
                    public void onFailure(final UpdatePoolRequest message, @Nonnull final UpdatePoolRequest response) {
                        //                        Window.alert("Failed.");
                        field.setErrorMessage(response.getResponse().getAttribute(LSDAttribute.DESCRIPTION));
                    }
                });
            }
        };
    }

    @Override
    protected void onChange(@Nonnull final LSDBaseEntity entity) {
        super.onChange(entity);
        WidgetUtil.showGracefully(this, true);
        final String shortUrl = entity.getURI().asBoardURL().asUrlSafe();
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

    interface PublicBoardHeaderUiBinder extends UiBinder<HTMLPanel, PublicBoardHeader> {}


    public void clear() {
        title.clear();
        description.clear();
        tag.clear();
        url.clear();
        boardImage.clear();
    }
}