/*
 * Copyright (c) 2009-2013 Cazcade Limited  - All Rights Reserved
 */

package cazcade.liquid.impl;

import cazcade.liquid.api.lsd.Entity;
import cazcade.liquid.api.lsd.TransferEntity;

import javax.annotation.Nonnull;
import java.io.InputStream;

/**
 * @author Neil Ellis
 */

public interface LSDUnmarshaler {
    void unmarshal(Entity lsdEntity, InputStream input);

    @Nonnull TransferEntity unmarshal(InputStream input);
}