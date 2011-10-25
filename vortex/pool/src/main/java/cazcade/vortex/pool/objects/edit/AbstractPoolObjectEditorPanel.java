package cazcade.vortex.pool.objects.edit;

import cazcade.liquid.api.LiquidURI;
import cazcade.liquid.api.lsd.LSDAttribute;
import cazcade.liquid.api.request.CreatePoolObjectRequest;
import cazcade.liquid.api.request.UpdatePoolObjectRequest;
import cazcade.vortex.bus.client.AbstractResponseCallback;
import cazcade.vortex.widgets.client.profile.Bindable;
import cazcade.vortex.widgets.client.profile.EntityBackedFormPanel;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.Button;

/**
 * @author neilellis@cazcade.com
 */
public abstract class AbstractPoolObjectEditorPanel extends EntityBackedFormPanel {

    protected Runnable onFinishAction;
    private boolean create;


    @Override
    protected String getReferenceDataPrefix() {
        return "object";
    }



    @Override
    protected Runnable getUpdateEntityAction(final Bindable field) {
        return new Runnable() {
            @Override
            public void run() {

                getBus().send(new UpdatePoolObjectRequest(field.getEntityDiff()), new AbstractResponseCallback<UpdatePoolObjectRequest>() {
                    @Override
                    public void onSuccess(UpdatePoolObjectRequest message, UpdatePoolObjectRequest response) {
                        setEntity(response.getResponse());
                    }

                    @Override
                    public void onFailure(UpdatePoolObjectRequest message, UpdatePoolObjectRequest response) {
                        field.setErrorMessage(response.getResponse().getAttribute(LSDAttribute.DESCRIPTION));
                    }
                });
            }
        };
    }

    public void setOnFinishAction(Runnable onFinishAction) {
        this.onFinishAction = onFinishAction;
    }


    public abstract int getHeight();

    public abstract int getWidth();


    public void setCreate(boolean create) {
        this.create = create;
    }
}
