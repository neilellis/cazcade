package cazcade.liquid.impl;

import cazcade.liquid.api.lsd.LSDEntity;

import javax.annotation.Nonnull;
import java.io.OutputStream;

/**
 * @author Neil Ellis
 */

public interface LSDMarshaler {

    void marshal(LSDEntity lsdEntity, OutputStream output);

    @Nonnull
    String getMimeType();
}
