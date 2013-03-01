/*
 * Copyright (c) 2009-2013 Cazcade Limited  - All Rights Reserved
 */

package cazcade.liquid.api.lsd;

import javax.annotation.Nonnull;
import java.io.Serializable;
import java.util.Map;

/**
 * An {@link Entity} that can safely be transferred between store, web server and web browser.
 *
 * @author Neil Ellis
 */

public interface TransferEntity<T extends TransferEntity<T> & Entity<TransferEntity<T>, T>> extends Entity<TransferEntity<T>, T> {

    @Nonnull TransferEntityCollection<? extends TransferEntity> children(@Nonnull Attribute key);

    @Nonnull TransferEntityCollection<? extends TransferEntity> children();

    @Nonnull  TransferEntity<T> $();

    @Nonnull  TransferEntity<T> asUpdate();
}
