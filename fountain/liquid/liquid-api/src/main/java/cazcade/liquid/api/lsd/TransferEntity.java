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

public interface TransferEntity<T extends TransferEntity<T>> extends Serializable, Entity<T, TransferEntity<T>> {

    @Nonnull Node asFormatIndependentTree();


    @Nonnull Map<String, String> asMapForPersistence(boolean ignoreType, boolean update);


    @Nonnull TransferEntity asUpdateEntity();


    @Nonnull TransferEntity $();


    /**
     * @deprecated use toString() instead.
     */
    String dump();

    /**
     * Use this for JSPs i.e. JSTL EL
     *
     * @return
     */
    @Nonnull Map<String, String> getCamelCaseMap();


    /**
     * The canonical format.
     *
     * @return a map of name/value pairs.
     */
    @Nonnull Map<String, String> map();

    @Nonnull TransferEntityCollection<T> children(@Nonnull Attribute key);

    @Nonnull TransferEntityCollection<T> children();


}
