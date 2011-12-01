package cazcade.vortex.pool.objects.richtext;

import cazcade.liquid.api.lsd.LSDAttribute;
import cazcade.liquid.api.lsd.LSDEntity;
import cazcade.liquid.api.request.UpdatePoolObjectRequest;
import cazcade.vortex.bus.client.AbstractResponseCallback;
import cazcade.vortex.gwt.util.client.VortexThreadSafeExecutor;
import cazcade.vortex.pool.AbstractPoolObjectPresenter;
import cazcade.vortex.pool.api.PoolPresenter;

import javax.annotation.Nonnull;

/**
 * @author neilellis@cazcade.com
 */
public class RichTextPresenter extends AbstractPoolObjectPresenter<RichTextView> {
    public RichTextPresenter(PoolPresenter poolPresenter, final LSDEntity entity, @Nonnull final RichTextView view, VortexThreadSafeExecutor threadSafeExecutor) {
        super(poolPresenter, entity, view, threadSafeExecutor);
        //make sure we set editable before setText (very important)
        view.setOnChangeAction(new Runnable() {
            @Override
            public void run() {
                LSDEntity minimalEntity = getEntity().asUpdateEntity();
                minimalEntity.setAttribute(LSDAttribute.TEXT_EXTENDED, view.getText());
                bus.send(new UpdatePoolObjectRequest(minimalEntity), new AbstractResponseCallback<UpdatePoolObjectRequest>() {
                    @Override
                    public void onSuccess(UpdatePoolObjectRequest message, UpdatePoolObjectRequest response) {
                    }
                });
            }
        });
    }

    @Override
    public void update(@Nonnull final LSDEntity newEntity, final boolean replaceEntity) {
        threadSafeExecutor.execute(new Runnable() {
            @Override
            public void run() {
                RichTextPresenter.super.update(newEntity, replaceEntity);
                getPoolObjectView().setText(newEntity.getAttribute(LSDAttribute.TEXT_EXTENDED));
            }
        });
    }

    @Override
    protected int getDefaultWidth() {
        return 200;
    }
}