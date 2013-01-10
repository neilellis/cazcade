/*
 * Copyright (c) 2009-2013 Cazcade Limited  - All Rights Reserved
 */

package cazcade.liquid.api.lsd;

import javax.annotation.Nonnull;
import java.io.Serializable;
import java.util.Map;

/**
 * @author neilellis@cazcade.com
 */
public interface LSDPropertyStore extends Serializable {
    @Nonnull Map<String, String> asMap();

    boolean containsProperty(String property);

    @Nonnull LSDPropertyStore copy();

    String get(String property);

    Iterable<? extends String> getKeys();

    boolean isSerializable();

    void put(@Nonnull String property, @Nonnull String value);

    void remove(String property);

    Iterable<? extends String> valueIterator();
}
