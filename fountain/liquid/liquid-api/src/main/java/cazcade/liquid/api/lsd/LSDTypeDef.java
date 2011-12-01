package cazcade.liquid.api.lsd;

import javax.annotation.Nonnull;
import java.io.Serializable;
import java.util.List;

/**
 * @author Neil Ellis
 */

public interface LSDTypeDef extends Serializable {

    LSDType getPrimaryType();

    @Nonnull
    List<LSDType> getSecondaryTypes();

    String asString();

    boolean canBe(LSDDictionaryTypes type);
}
