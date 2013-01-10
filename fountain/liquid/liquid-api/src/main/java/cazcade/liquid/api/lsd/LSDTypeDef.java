/*
 * Copyright (c) 2009-2013 Cazcade Limited  - All Rights Reserved
 */

package cazcade.liquid.api.lsd;

import javax.annotation.Nonnull;
import java.io.Serializable;
import java.util.List;

/**
 * @author Neil Ellis
 */

public interface LSDTypeDef extends Serializable {
    String asString();

    boolean canBe(LSDDictionaryTypes type);

    LSDType getPrimaryType();

    @Nonnull List<LSDType> getSecondaryTypes();
}
