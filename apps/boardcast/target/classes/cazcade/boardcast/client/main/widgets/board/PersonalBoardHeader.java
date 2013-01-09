package cazcade.boardcast.client.main.widgets.board;

import cazcade.liquid.api.lsd.LSDAttribute;
import cazcade.liquid.api.lsd.LSDTransferEntity;
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

    @UiField
    VortexEditableLabel title;
    @UiField
    VortexEditableLabel description;
    @UiField
    VortexEditableLabel text;

    public PersonalBoardHeader() {
        super();
        final HTMLPanel rootElement = ourUiBinder.createAndBindUi(this);
        initWidget(rootElement);
    }

    public void bind(final LSDTransferEntity entity) {
        super.bind(entity);
        addBinding(title, LSDAttribute.TITLE);
        addBinding(description, LSDAttribute.DESCRIPTION);
        addBinding(text, LSDAttribute.TEXT_EXTENDED);
    }

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
                    public void onSuccess(final UpdatePoolRequest message, @Nonnull final UpdatePoolRequest response) {
                        setEntity(response.getResponse().copy());
                    }

                    @Override
                    public void onFailure(final UpdatePoolRequest message, @Nonnull final UpdatePoolRequest response) {
                        field.setErrorMessage(response.getResponse().getAttribute(LSDAttribute.DESCRIPTION));
                    }
                }
                             );
            }
        };
    }

    interface PublicBoardHeaderUiBinder extends UiBinder<HTMLPanel, PersonalBoardHeader> {
    }
}