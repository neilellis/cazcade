package cazcade.cli;

import cazcade.fountain.datastore.api.FountainDataStore;
import cazcade.liquid.api.LiquidSessionIdentifier;
import cazcade.liquid.api.lsd.LSDBaseEntity;
import cazcade.liquid.api.lsd.LSDTransferEntity;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;

/**
 * @author neilellis@cazcade.com
 */
public class ShellSession {
    private LiquidSessionIdentifier identity;
    private LSDBaseEntity currentPool;

    private String lastCommand;
    @Nonnull
    private final List<LSDTransferEntity> entityStack = new ArrayList<LSDTransferEntity>();

    private FountainDataStore dataStore;

    public void setIdentity(final LiquidSessionIdentifier identity) {
        this.identity = identity;
    }

    public LiquidSessionIdentifier getIdentity() {
        return identity;
    }

    public LSDBaseEntity getCurrentPool() {
        return currentPool;
    }

    public void setCurrentPool(final LSDBaseEntity currentPool) {
        this.currentPool = currentPool;
    }

    public void setDataStore(final FountainDataStore dataStore) {
        this.dataStore = dataStore;
    }

    public FountainDataStore getDataStore() {
        return dataStore;
    }


    public void pushEntity(final LSDTransferEntity entity) {
        entityStack.add(entity);
    }

    public LSDTransferEntity popEntity() {
        if (entityStack.isEmpty()) {
            throw new IllegalStateException("Cannot pop entity as stack is empty, how did this happen?");
        }
        return entityStack.remove(entityStack.size() - 1);
    }

    public LSDBaseEntity getCurrentEntity() {
        if (entityStack.isEmpty()) {
            throw new IllegalStateException("Not within the context of an entity.");
        }
        return entityStack.get(entityStack.size() - 1);
    }

    public boolean hasEntityOnStack() {
        return !entityStack.isEmpty();
    }
}
