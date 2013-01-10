/*
 * Copyright (c) 2009-2013 Cazcade Limited  - All Rights Reserved
 */

package cazcade.liquid.api.lsd;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.io.Serializable;
import java.util.List;

/**
 * @author neilelliz@cazcade.com
 */
public interface LSDType extends Serializable {
    @Nonnull String asString();

    boolean canBe(LSDDictionaryTypes type);

    LSDType getClassOnlyType();

    /**
     * The next coarsest grouping after Genus i.e. Genus.Family.TypeClass
     */
    @Nullable String getFamily();

    List<String> getFlavors();

    /**
     * The coarsest grouping i.e. Genus.Family.TypeClass
     */
    @Nonnull String getGenus();

    LSDType getParentType();

    /**
     * The next coarsest grouping after Family i.e. Genus.Family.TypeClass
     */
    @Nullable String getTypeClass();

    boolean isA(LSDDictionaryTypes dictionaryType);

    boolean isSystemType();

    @Nullable String toString();
}
