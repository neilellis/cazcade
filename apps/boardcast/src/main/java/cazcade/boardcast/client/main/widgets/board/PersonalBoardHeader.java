/*
 * Copyright (c) 2009-2013 Cazcade Limited  - All Rights Reserved
 */

package cazcade.boardcast.client.main.widgets.board;

import cazcade.liquid.api.request.UpdatePoolRequest;
import cazcade.vortex.bus.client.Callback;
import cazcade.vortex.bus.client.Request;
import cazcade.vortex.widgets.client.form.fields.VortexEditableLabel;
import cazcade.vortex.widgets.client.profile.Bindable;
import cazcade.vortex.widgets.client.profile.EntityBackedFormPanel;
import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.HTMLPanel;

import javax.annotation.Nonnull;

import static cazcade.liquid.api.lsd.Dictionary.*;

/**
 * @author neilellis@cazcade.com
 */
public class PersonalBoardHeader extends EntityBackedFormPanel {
    interface PublicBoardHeaderUiBinder extends UiBinder<HTMLPanel, PersonalBoardHeader> {}

    private static final PublicBoardHeaderUiBinder ourUiBinder = GWT.create(PublicBoardHeaderUiBinder.class);
    @UiField VortexEditableLabel title;
    @UiField VortexEditableLabel description;
    @UiField VortexEditableLabel text;


    public PersonalBoardHeader() {
        super();
        final HTMLPanel rootElement = ourUiBinder.createAndBindUi(this);
        initWidget(rootElement);
        bind(title, TITLE);
        bind(description, DESCRIPTION);
        bind(text, TEXT_EXTENDED);
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
                Request.updatePool(field.getEntityDiff(), new Callback<UpdatePoolRequest>() {
                            @Override public void handle(UpdatePoolRequest message) throws Exception {
                                $(message.response().$());
                            }
                        }, new Callback<UpdatePoolRequest>() {
                            @Override public void handle(UpdatePoolRequest message) throws Exception {
                                field.setErrorMessage(message.response().$(DESCRIPTION));
                            }
                        }
                );
            }
        };
    }
}