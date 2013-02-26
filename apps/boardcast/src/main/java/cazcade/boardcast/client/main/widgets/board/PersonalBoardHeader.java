/*
 * Copyright (c) 2009-2013 Cazcade Limited  - All Rights Reserved
 */

package cazcade.boardcast.client.main.widgets.board;

import cazcade.liquid.api.lsd.Dictionary;
import cazcade.liquid.api.request.UpdatePoolRequest;
import cazcade.vortex.bus.client.AbstractResponseCallback;
import cazcade.vortex.widgets.client.form.fields.VortexEditableLabel;
import cazcade.vortex.widgets.client.profile.Bindable;
import cazcade.vortex.widgets.client.profile.EntityBackedFormPanel;
import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.HTMLPanel;

import javax.annotation.Nonnull;

/**
 * @author neilellis@cazcade.com
 */
public class PersonalBoardHeader extends EntityBackedFormPanel {
    private static final PublicBoardHeaderUiBinder ourUiBinder = GWT.create(PublicBoardHeaderUiBinder.class);

    @UiField VortexEditableLabel title;
    @UiField VortexEditableLabel description;
    @UiField VortexEditableLabel text;

    public PersonalBoardHeader() {
        super();
        final HTMLPanel rootElement = ourUiBinder.createAndBindUi(this);
        initWidget(rootElement);
        bind(title, Dictionary.TITLE);
        bind(description, Dictionary.DESCRIPTION);
        bind(text, Dictionary.TEXT_EXTENDED);
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
                getBus().send(new UpdatePoolRequest(field.getEntityDiff()), new AbstractResponseCallback<UpdatePoolRequest>() {
                    @Override
                    public void onSuccess(final UpdatePoolRequest message, @Nonnull final UpdatePoolRequest response) {
                        $(response.response().$());
                    }

                    @Override
                    public void onFailure(final UpdatePoolRequest message, @Nonnull final UpdatePoolRequest response) {
                        field.setErrorMessage(response.response().$(Dictionary.DESCRIPTION));
                    }
                });
            }
        };
    }

    interface PublicBoardHeaderUiBinder extends UiBinder<HTMLPanel, PersonalBoardHeader> {}
}