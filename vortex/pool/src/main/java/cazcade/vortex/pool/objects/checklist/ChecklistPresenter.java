/*
 * Copyright (c) 2009-2013 Cazcade Limited  - All Rights Reserved
 */

package cazcade.vortex.pool.objects.checklist;

import cazcade.liquid.api.lsd.Dictionary;
import cazcade.liquid.api.lsd.TransferEntity;
import cazcade.liquid.api.lsd.TypeDef;
import cazcade.liquid.api.lsd.Types;
import cazcade.liquid.api.request.RetrievePoolRequest;
import cazcade.liquid.api.request.UpdatePoolRequest;
import cazcade.vortex.bus.client.AbstractResponseCallback;
import cazcade.vortex.bus.client.BusFactory;
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
    public ChecklistPresenter(final PoolPresenter poolPresenter, @Nonnull final TransferEntity entity, @Nonnull final ChecklistView view, final VortexThreadSafeExecutor threadSafeExecutor) {
        super(poolPresenter, entity, view, threadSafeExecutor);
        //make sure we set editable before setText (very important)
        view.setOnChangeAction(new Runnable() {
            @Override
            public void run() {
                final TransferEntity minimalEntity = getEntity().asUpdateEntity();
                BusFactory.get().send(new UpdatePoolRequest(minimalEntity), new AbstractResponseCallback<UpdatePoolRequest>() {
                    @Override
                    public void onSuccess(final UpdatePoolRequest message, final UpdatePoolRequest response) {
                    }
                });
            }
        });

        BusFactory.get()
                  .send(new RetrievePoolRequest(getEntity().uri(), true, false), new AbstractResponseCallback<RetrievePoolRequest>() {
                      @Override
                      public void onSuccess(final RetrievePoolRequest message, @Nonnull final RetrievePoolRequest response) {
                          final List<TransferEntity> children = response.response().children(Dictionary.CHILD_A);
                          for (final TransferEntity child : children) {

                              if (child.has$(Dictionary.TEXT_EXTENDED)) {
                                  //                        final String text = child.$(Attribute.TEXT_EXTENDED);
                                  //todo: checklistview should take the ent
                                  getPoolObjectView().addView(new ChecklistEntryView(child));
                                  //                        getWidget().addView(new Label(text.replaceAll("<[^>]*>", " ").replaceAll("\n", " ")));
                              }
                          }
                      }
                  });
    }

    @Override
    public void update(final TransferEntity newEntity, final boolean replaceEntity) {
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

    @Nonnull @Override
    public Types getType() {
        return Types.T_CHECKLIST_POOL;
    }

    @Override
    public boolean willAccept(@Nonnull final TypeDef type) {
        return type.canBe(Types.T_TEXT);
    }
}