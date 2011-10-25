package cazcade.liquid.api.lsd;

import java.io.InputStream;

/**
 * @author Neil Ellis
 */

public interface LSDUnmarshaler {

    void unmarshal(LSDEntity lsdEntity, InputStream input);
    LSDEntity unmarshal(InputStream input);

}