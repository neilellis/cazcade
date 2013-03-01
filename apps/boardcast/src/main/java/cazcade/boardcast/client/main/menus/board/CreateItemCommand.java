/*
 * Copyright (c) 2009-2013 Cazcade Limited  - All Rights Reserved
 */

package cazcade.boardcast.client.main.menus.board;

import cazcade.liquid.api.LURI;
import cazcade.liquid.api.lsd.*;
import cazcade.liquid.api.request.CreatePoolObjectRequest;
import cazcade.vortex.bus.client.AbstractMessageCallback;
import cazcade.vortex.bus.client.Bus;
import cazcade.vortex.pool.objects.edit.AbstractPoolObjectEditorPanel;
import cazcade.vortex.pool.objects.edit.PoolObjectEditor;
import com.google.gwt.user.client.Window;

import javax.annotation.Nonnull;


/**
 * @author neilellis@cazcade.com
 */
public abstract class CreateItemCommand extends AbstractCreateCommand {
    public CreateItemCommand(final LURI pool, final Types type, final Size size, final String theme) {
        super(pool, type, size, theme);
    }

    public CreateItemCommand(final LURI pool, final Types type) {
        super(pool, type);
    }

    @Override
    public void execute() {
        buildEntity(new BuildCallback() {
            @Override
            public void onBuilt(final TransferEntity entity) {
                create(entity, new CreateCallback() {
                    @Override
                    public void onCreate(final CreatePoolObjectRequest response) {

                    }
                });
            }
        });
    }

    protected abstract void buildEntity(BuildCallback onBuilt);

    protected void create(final TransferEntity entity, @Nonnull final CreateCallback callback) {
        Bus.get().send(new CreatePoolObjectRequest(pool, entity), new AbstractMessageCallback<CreatePoolObjectRequest>() {
            @Override
            public void onSuccess(final CreatePoolObjectRequest original, final CreatePoolObjectRequest message) {
                callback.onCreate(message);
            }

            @Override
            public void onFailure(final CreatePoolObjectRequest original, @Nonnull final CreatePoolObjectRequest message) {
                Window.alert("Failed to create object, permissions issue?");
            }
        });
    }

    @Nonnull TransferEntity createEntityWithDefaultView() {
        final TransferEntity entity = SimpleEntity.create(getType());
        addDefaultView(entity);
        return entity;
    }

    protected void addDefaultView(@Nonnull final Entity entity) {
        //todo: give a more sensible starting point
        final TransferEntity view = SimpleEntity.create(Types.T_VIEW);
        view.$(Dictionary.VIEW_X, "0");
        view.$(Dictionary.VIEW_Y, "0");
        if (theme != null) {
            view.$(Dictionary.THEME, theme);
        }
        if (size != null) {
            view.$(Dictionary.SIZE, size.name().toLowerCase());
        }
        entity.addAnonymousSubEntity(Dictionary.VIEW_ENTITY, view);
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
