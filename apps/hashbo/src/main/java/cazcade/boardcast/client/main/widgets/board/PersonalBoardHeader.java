package cazcade.boardcast.client.main.widgets.board;

import cazcade.liquid.api.lsd.LSDAttribute;
import cazcade.liquid.api.lsd.LSDEntity;
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

    @UiField
    VortexEditableLabel title;
    @UiField
    VortexEditableLabel description;
    @UiField
    VortexEditableLabel text;

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

                getBus().send(new UpdatePoolRequest(field.getEntityDiff()), new AbstractResponseCallback<UpdatePoolRequest>() {
                    @Override
                    public void onSuccess(UpdatePoolRequest message, @Nonnull UpdatePoolRequest response) {
                        setEntity(response.getResponse().copy());
                    }

                    @Override
                    public void onFailure(UpdatePoolRequest message, @Nonnull UpdatePoolRequest response) {
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
        addBinding(text, LSDAttribute.TEXT_EXTENDED);
    }

    interface PublicBoardHeaderUiBinder extends UiBinder<HTMLPanel, PersonalBoardHeader> {
    }

    private static final PublicBoardHeaderUiBinder ourUiBinder = GWT.create(PublicBoardHeaderUiBinder.class);

    public PersonalBoardHeader() {
        HTMLPanel rootElement = ourUiBinder.createAndBindUi(this);
        initWidget(rootElement);

    }
}