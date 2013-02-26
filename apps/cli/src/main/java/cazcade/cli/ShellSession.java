/*
 * Copyright (c) 2009-2013 Cazcade Limited  - All Rights Reserved
 */

package cazcade.cli;

import cazcade.fountain.datastore.api.FountainDataStore;
import cazcade.liquid.api.SessionIdentifier;
import cazcade.liquid.api.lsd.Entity;
import cazcade.liquid.api.lsd.TransferEntity;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;

/**
 * @author neilellis@cazcade.com
 */
public class ShellSession {
    private SessionIdentifier identity;
    private TransferEntity    currentPool;

    private String lastCommand;
    @Nonnull
    private final List<TransferEntity> entityStack = new ArrayList<TransferEntity>();

    private FountainDataStore dataStore;

    public void setIdentity(final SessionIdentifier identity) {
        this.identity = identity;
    }

    public SessionIdentifier getIdentity() {
        return identity;
    }

    public TransferEntity getCurrentPool() {
        return currentPool;
    }

    public void setCurrentPool(final TransferEntity currentPool) {
        this.currentPool = currentPool;
    }

    public void setDataStore(final FountainDataStore dataStore) {
        this.dataStore = dataStore;
    }

    public FountainDataStore getDataStore() {
        return dataStore;
    }


    public void pushEntity(final TransferEntity entity) {
        entityStack.add(entity);
    }

    public TransferEntity popEntity() {
        if (entityStack.isEmpty()) {
            throw new IllegalStateException("Cannot pop entity as stack is empty, how did this happen?");
        }
        return entityStack.remove(entityStack.size() - 1);
    }

    public Entity getCurrentEntity() {
        if (entityStack.isEmpty()) {
            throw new IllegalStateException("Not within the context of an entity.");
        }
        return entityStack.get(entityStack.size() - 1);
    }

    public boolean hasEntityOnStack() {
        return !entityStack.isEmpty();
    }
}
