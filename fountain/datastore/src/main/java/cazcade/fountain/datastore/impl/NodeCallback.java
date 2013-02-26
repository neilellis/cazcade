/*
 * Copyright (c) 2009-2013 Cazcade Limited  - All Rights Reserved
 */

package cazcade.fountain.datastore.impl;


/**
 * @author neilellis@cazcade.com
 */
public interface NodeCallback {
    void call(PersistedEntity persistedEntity) throws Exception;
}
