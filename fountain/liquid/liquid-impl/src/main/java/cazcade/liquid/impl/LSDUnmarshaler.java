/*
 * Copyright (c) 2009-2013 Cazcade Limited  - All Rights Reserved
 */

package cazcade.liquid.impl;

import cazcade.liquid.api.lsd.LSDBaseEntity;
import cazcade.liquid.api.lsd.LSDTransferEntity;

import javax.annotation.Nonnull;
import java.io.InputStream;

/**
 * @author Neil Ellis
 */

public interface LSDUnmarshaler {
    void unmarshal(LSDBaseEntity lsdEntity, InputStream input);

    @Nonnull LSDTransferEntity unmarshal(InputStream input);
}