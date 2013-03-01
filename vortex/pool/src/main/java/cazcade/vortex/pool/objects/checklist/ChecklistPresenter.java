/*
 * Copyright (c) 2009-2013 Cazcade Limited  - All Rights Reserved
 */

package cazcade.vortex.pool.objects.checklist;

import cazcade.liquid.api.lsd.CollectionCallback;
import cazcade.liquid.api.lsd.TransferEntity;
import cazcade.liquid.api.lsd.TypeDef;
import cazcade.liquid.api.lsd.Types;
import cazcade.liquid.api.request.RetrievePoolRequest;
import cazcade.liquid.api.request.UpdatePoolRequest;
import cazcade.vortex.bus.client.Bus;
import cazcade.vortex.bus.client.Callback;
import cazcade.vortex.gwt.util.client.VortexThreadSafeExecutor;
import cazcade.vortex.pool.AbstractContainerObjectPresenterImpl;
import cazcade.vortex.pool.api.PoolPresenter;
import cazcade.vortex.pool.objects.checklist.entry.ChecklistEntryView;

import javax.annotation.Nonnull;
import java.util.List;

import static cazcade.liquid.api.lsd.Dictionary.*;

/**
 * @author neilellis@cazcade.com
 */
public class ChecklistPresenter extends AbstractContainerObjectPresenterImpl<ChecklistView> {
    public ChecklistPresenter(final PoolPresenter poolPresenter, @Nonnull final TransferEntity entity, @Nonnull final ChecklistView view, final VortexThreadSafeExecutor threadSafeExecutor) {
        super(poolPresenter, entity, view, threadSafeExecutor);
        //make sure we set editable before setText (very important)
        view.setOnChangeAction(new Runnable() {
            @Override
            public void run() {
                Bus.get().dispatch(new UpdatePoolRequest(entity().asUpdate()));
            }
        });

        Bus.get().send(new RetrievePoolRequest(entity().uri(), true, false), new Callback<RetrievePoolRequest>() {
            @Override
            public void handle(@Nonnull final RetrievePoolRequest message) {
                message.response().children().has(TEXT_EXTENDED).each(new CollectionCallback<TransferEntity>() {
                    @Override public void call(TransferEntity entity) {
                        view().add(new ChecklistEntryView(entity));
                    }
                });
            }
        });
    }

    @Override
    public void update(final TransferEntity newEntity, final boolean replaceEntity) {
        executor.execute(new Runnable() {
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

    @Nonnull @Override
    public Types getType() {
        return Types.T_CHECKLIST_POOL;
    }

    @Override
    public boolean willAccept(@Nonnull final TypeDef type) {
        return type.canBe(Types.T_TEXT);
    }
}