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

    @Override
    protected String getReferenceDataPrefix() {
        return "board";
    }

    @Override
    protected Runnable getUpdateEntityAction(final Bindable field) {
        return new Runnable() {
            @Override
            public void run() {

                getBus().send(new UpdatePoolRequest(field.getEntityDiff()), new AbstractResponseCallback<UpdatePoolRequest>() {
                    @Override
                    public void onSuccess(UpdatePoolRequest message, UpdatePoolRequest response) {
                        setEntity(response.getResponse());
                    }

                    @Override
                    public void onFailure(UpdatePoolRequest message, UpdatePoolRequest response) {
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

    private static PublicBoardHeaderUiBinder ourUiBinder = GWT.create(PublicBoardHeaderUiBinder.class);

    public PersonalBoardHeader() {
        HTMLPanel rootElement = ourUiBinder.createAndBindUi(this);
        initWidget(rootElement);

    }
}