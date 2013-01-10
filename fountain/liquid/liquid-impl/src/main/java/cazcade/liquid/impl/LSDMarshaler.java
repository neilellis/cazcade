/*
 * Copyright (c) 2009-2013 Cazcade Limited  - All Rights Reserved
 */

package cazcade.liquid.impl;

import cazcade.liquid.api.lsd.LSDTransferEntity;

import javax.annotation.Nonnull;
import java.io.OutputStream;

/**
 * @author Neil Ellis
 */

public interface LSDMarshaler {
    @Nonnull String getMimeType();

    void marshal(LSDTransferEntity lsdEntity, OutputStream output);
}
