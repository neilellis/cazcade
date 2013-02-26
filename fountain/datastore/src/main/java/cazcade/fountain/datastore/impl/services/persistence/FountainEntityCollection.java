/*
 * Copyright (c) 2009-2013 Cazcade Limited  - All Rights Reserved
 */

package cazcade.fountain.datastore.impl.services.persistence;

import cazcade.fountain.datastore.impl.PersistedEntity;
import cazcade.liquid.api.lsd.TransferEntityCollection;

import java.util.List;

/**
 * @author <a href="http://uk.linkedin.com/in/neilellis">Neil Ellis</a>
 * @todo document.
 */
public class FountainEntityCollection extends TransferEntityCollection<PersistedEntity> {

    public FountainEntityCollection(List<PersistedEntity> values) {
        super(values);
    }
}
