package cazcade.vortex.pool.objects.checklist;

import cazcade.liquid.api.lsd.LSDAttribute;
import cazcade.liquid.api.lsd.LSDDictionaryTypes;
import cazcade.liquid.api.lsd.LSDEntity;
import cazcade.liquid.api.lsd.LSDTypeDef;
import cazcade.liquid.api.request.RetrievePoolRequest;
import cazcade.liquid.api.request.UpdatePoolRequest;
import cazcade.vortex.bus.client.AbstractResponseCallback;
import cazcade.vortex.bus.client.BusFactory;
import cazcade.vortex.common.client.FormatUtil;
import cazcade.vortex.gwt.util.client.VortexThreadSafeExecutor;
import cazcade.vortex.pool.AbstractContainerObjectPresenterImpl;
import cazcade.vortex.pool.api.PoolPresenter;
import cazcade.vortex.pool.objects.checklist.entry.ChecklistEntryView;

import javax.annotation.Nonnull;
import java.util.List;

/**
 * @author neilellis@cazcade.com
 */
public class ChecklistPresenter extends AbstractContainerObjectPresenterImpl<ChecklistView> {
    public ChecklistPresenter(PoolPresenter poolPresenter, @Nonnull final LSDEntity entity, @Nonnull final ChecklistView view, VortexThreadSafeExecutor threadSafeExecutor, FormatUtil features) {
        super(poolPresenter, entity, view, threadSafeExecutor, features);
        //make sure we set editable before setText (very important)
        view.setOnChangeAction(new Runnable() {
            @Override
            public void run() {
                LSDEntity minimalEntity = getEntity().asUpdateEntity();
                BusFactory.getInstance().send(new UpdatePoolRequest(minimalEntity), new AbstractResponseCallback<UpdatePoolRequest>() {
                    @Override
                    public void onSuccess(UpdatePoolRequest message, UpdatePoolRequest response) {
                    }
                });
            }
        });

        BusFactory.getInstance().send(new RetrievePoolRequest(getEntity().getURI(), true, false), new AbstractResponseCallback<RetrievePoolRequest>() {
            @Override
            public void onSuccess(RetrievePoolRequest message, @Nonnull RetrievePoolRequest response) {
                final List<LSDEntity> children = response.getResponse().getSubEntities(LSDAttribute.CHILD);
                for (LSDEntity child : children) {

                    final String text = child.getAttribute(LSDAttribute.TEXT_EXTENDED);
                    if (text != null) {
                        //todo: checklistview should take the ent
                        getPoolObjectView().addView(new ChecklistEntryView(child));
//                        getWidget().addView(new Label(text.replaceAll("<[^>]*>", " ").replaceAll("\n", " ")));
                    }
                }
            }
        });
    }

    @Override
    public void update(final LSDEntity newEntity, final boolean replaceEntity) {
        threadSafeExecutor.execute(new Runnable() {
            @Override
            public void run() {
                ChecklistPresenter.super.update(newEntity, replaceEntity);
            }
        });
    }

    @Override
    protected int getDefaultWidth() {
        return 200;
    }

    @Override
    protected int getDefaultHeight() {
        return 400;
    }

    @Nonnull
    @Override
    public LSDDictionaryTypes getType() {
        return LSDDictionaryTypes.CHECKLIST_POOL;
    }

    @Override
    public boolean willAccept(@Nonnull LSDTypeDef type) {
        return type.canBe(LSDDictionaryTypes.TEXT);
    }
}