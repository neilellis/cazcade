package cazcade.vortex.pool.objects.edit;

import cazcade.liquid.api.lsd.LSDAttribute;
import cazcade.liquid.api.request.UpdatePoolObjectRequest;
import cazcade.vortex.bus.client.AbstractResponseCallback;
import cazcade.vortex.widgets.client.profile.Bindable;
import cazcade.vortex.widgets.client.profile.EntityBackedFormPanel;

import javax.annotation.Nonnull;

/**
 * @author neilellis@cazcade.com
 */
public abstract class AbstractPoolObjectEditorPanel extends EntityBackedFormPanel {

    protected Runnable onFinishAction;
    private boolean create;


    @Nonnull
    @Override
    protected String getReferenceDataPrefix() {
        return "object";
    }


    @Nonnull
    @Override
    protected Runnable getUpdateEntityAction(@Nonnull final Bindable field) {
        return new Runnable() {
            @Override
            public void run() {
                if (field.isValid()) {
                    getBus().send(new UpdatePoolObjectRequest(field.getEntityDiff()), new AbstractResponseCallback<UpdatePoolObjectRequest>() {
                        @Override
                        public void onSuccess(UpdatePoolObjectRequest message, @Nonnull UpdatePoolObjectRequest response) {
                            setEntity(response.getResponse().copy());
                            if (autoCloseField(field)) {
                                if (onFinishAction != null) {
                                    onFinishAction.run();
                                }
                            }
                        }

                        @Override
                        public void onFailure(UpdatePoolObjectRequest message, @Nonnull UpdatePoolObjectRequest response) {
                            field.setErrorMessage(response.getResponse().getAttribute(LSDAttribute.DESCRIPTION));
                        }
                    });
                } else {

                }
            }
        };
    }

    protected boolean autoCloseField(Bindable field) {
        return false;
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
