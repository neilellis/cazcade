package cazcade.cli;

import cazcade.fountain.datastore.api.FountainDataStore;
import cazcade.liquid.api.LiquidSessionIdentifier;
import cazcade.liquid.api.lsd.LSDEntity;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;

/**
 * @author neilellis@cazcade.com
 */
public class ShellSession {
    private LiquidSessionIdentifier identity;
    private LSDEntity currentPool;

    private String lastCommand;
    @Nonnull
    private final List<LSDEntity> entityStack = new ArrayList<LSDEntity>();

    private FountainDataStore dataStore;

    public void setIdentity(final LiquidSessionIdentifier identity) {
        this.identity = identity;
    }

    public LiquidSessionIdentifier getIdentity() {
        return identity;
    }

    public LSDEntity getCurrentPool() {
        return currentPool;
    }

    public void setCurrentPool(final LSDEntity currentPool) {
        this.currentPool = currentPool;
    }

    public void setDataStore(final FountainDataStore dataStore) {
        this.dataStore = dataStore;
    }

    public FountainDataStore getDataStore() {
        return dataStore;
    }


    public void pushEntity(final LSDEntity entity) {
        entityStack.add(entity);
    }

    public LSDEntity popEntity() {
        if (entityStack.isEmpty()) {
            throw new IllegalStateException("Cannot pop entity as stack is empty, how did this happen?");
        }
        return entityStack.remove(entityStack.size() - 1);
    }

    public LSDEntity getCurrentEntity() {
        if (entityStack.isEmpty()) {
            throw new IllegalStateException("Not within the context of an entity.");
        }
        return entityStack.get(entityStack.size() - 1);
    }

    public boolean hasEntityOnStack() {
        return !entityStack.isEmpty();
    }
}
