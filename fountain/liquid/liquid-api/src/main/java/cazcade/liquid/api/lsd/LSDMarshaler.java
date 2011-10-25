package cazcade.liquid.api.lsd;

import java.io.OutputStream;

/**
 * @author Neil Ellis
 */

public interface LSDMarshaler {

    void marshal(LSDEntity lsdEntity, OutputStream output);

    String getMimeType();
}
