/*
 * Copyright (c) 2009-2013 Cazcade Limited  - All Rights Reserved
 */

package cazcade.boardcast.client.main.menus.board;

import cazcade.liquid.api.LiquidURI;
import cazcade.liquid.api.lsd.*;
import cazcade.liquid.api.request.CreatePoolObjectRequest;
import cazcade.vortex.bus.client.AbstractResponseCallback;
import cazcade.vortex.bus.client.BusFactory;
import cazcade.vortex.pool.objects.edit.AbstractPoolObjectEditorPanel;
import cazcade.vortex.pool.objects.edit.PoolObjectEditor;
import com.google.gwt.user.client.Window;

import javax.annotation.Nonnull;


/**
 * @author neilellis@cazcade.com
 */
public abstract class CreateItemCommand extends AbstractCreateCommand {
    public CreateItemCommand(final LiquidURI pool, final LSDDictionaryTypes type, final Size size, final String theme) {
        super(pool, type, size, theme);
    }

    public CreateItemCommand(final LiquidURI pool, final LSDDictionaryTypes type) {
        super(pool, type);
    }

    @Override
    public void execute() {
        buildEntity(new BuildCallback() {
            @Override
            public void onBuilt(final LSDTransferEntity entity) {
                create(entity, new CreateCallback() {
                    @Override
                    public void onCreate(final CreatePoolObjectRequest response) {

                    }
                });
            }
        });
    }

    protected abstract void buildEntity(BuildCallback onBuilt);

    protected void create(final LSDTransferEntity entity, @Nonnull final CreateCallback callback) {
        BusFactory.getInstance()
                  .send(new CreatePoolObjectRequest(pool, entity), new AbstractResponseCallback<CreatePoolObjectRequest>() {
                      @Override
                      public void onSuccess(final CreatePoolObjectRequest message, final CreatePoolObjectRequest response) {
                          callback.onCreate(response);
                      }

                      @Override
                      public void onFailure(final CreatePoolObjectRequest message, @Nonnull final CreatePoolObjectRequest response) {
                          Window.alert("Failed to create object, permissions issue?");
                      }
                  });
    }

    @Nonnull LSDTransferEntity createEntityWithDefaultView() {
        final LSDTransferEntity entity = LSDSimpleEntity.createNewEntity(getType());
        addDefaultView(entity);
        return entity;
    }

    protected void addDefaultView(@Nonnull final LSDBaseEntity entity) {
        //todo: give a more sensible starting point
        final LSDTransferEntity view = LSDSimpleEntity.createNewEntity(LSDDictionaryTypes.VIEW);
        view.setAttribute(LSDAttribute.VIEW_X, "0");
        view.setAttribute(LSDAttribute.VIEW_Y, "0");
        if (theme != null) {
            view.setAttribute(LSDAttribute.THEME, theme);
        }
        if (size != null) {
            view.setAttribute(LSDAttribute.SIZE, size.name().toLowerCase());
        }
        entity.addAnonymousSubEntity(LSDAttribute.VIEW, view);
    }

    protected void showEditorPanel(@Nonnull final AbstractPoolObjectEditorPanel editorPanel) {

        PoolObjectEditor.showForCreate(editorPanel, new Runnable() {
            @Override public void run() {
                create(editorPanel.getEntityForCreation(), new CreateCallback() {
                    @Override public void onCreate(CreatePoolObjectRequest response) {
                        //do nothing
                    }
                });
            }
        });

    }

    public interface CreateCallback {
        void onCreate(CreatePoolObjectRequest response);
    }
}
