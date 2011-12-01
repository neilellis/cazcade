package cazcade.liquid.impl;

import cazcade.liquid.api.lsd.LSDEntity;

import javax.annotation.Nonnull;
import java.io.InputStream;

/**
 * @author Neil Ellis
 */

public interface LSDUnmarshaler {

    void unmarshal(LSDEntity lsdEntity, InputStream input);

    @Nonnull
    LSDEntity unmarshal(InputStream input);

}