package cazcade.boardcast.client.main.menus.board;

import cazcade.liquid.api.LiquidURI;
import cazcade.liquid.api.lsd.LSDAttribute;
import cazcade.liquid.api.lsd.LSDDictionaryTypes;
import cazcade.liquid.api.lsd.LSDEntity;
import cazcade.liquid.api.lsd.LSDSimpleEntity;
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

    public CreateItemCommand(LiquidURI pool, LSDDictionaryTypes type) {
        super(pool, type);
    }

    public CreateItemCommand(LiquidURI pool, LSDDictionaryTypes type, Size size, String theme) {
        super(pool, type, size, theme);
    }


    protected abstract void buildEntity(BuildCallback onBuilt);

    @Nonnull
    LSDSimpleEntity createEntityWithDefaultView() {
        final LSDSimpleEntity entity = LSDSimpleEntity.createNewEntity(getType());
        addDefaultView(entity);
        return entity;
    }


    @Override
    public void execute() {
        buildEntity(new BuildCallback() {
            @Override
            public void onBuilt(LSDEntity entity) {
                create(entity, new CreateCallback() {
                    @Override
                    public void onCreate(CreatePoolObjectRequest response) {

                    }
                });
            }
        });
    }

    protected void create(LSDEntity entity, @Nonnull final CreateCallback callback) {
        BusFactory.getInstance().send(new CreatePoolObjectRequest(pool, entity), new AbstractResponseCallback<CreatePoolObjectRequest>() {
            @Override
            public void onSuccess(CreatePoolObjectRequest message, CreatePoolObjectRequest response) {
                callback.onCreate(response);
            }

            @Override
            public void onFailure(CreatePoolObjectRequest message, @Nonnull CreatePoolObjectRequest response) {
                Window.alert("Failed to create object, permissions issue?");
            }
        });
    }

    protected void addDefaultView(@Nonnull LSDEntity entity) {
        //todo: give a more sensible starting point
        final LSDSimpleEntity view = LSDSimpleEntity.createNewEntity(LSDDictionaryTypes.VIEW);
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

    public interface CreateCallback {

        void onCreate(CreatePoolObjectRequest response);
    }

    protected void showEditorPanel(@Nonnull final AbstractPoolObjectEditorPanel editorPanel) {
        create(editorPanel.getEntity(), new CreateCallback() {
            @Override
            public void onCreate(@Nonnull CreatePoolObjectRequest response) {
                editorPanel.setEntity(response.getResponse().copy());
                PoolObjectEditor.showForCreate(editorPanel, null);
            }
        });
    }

}
