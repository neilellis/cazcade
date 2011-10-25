package cazcade.liquid.api.lsd;

import java.io.Serializable;
import java.util.List;

/**
 * @author Neil Ellis
 */

public interface LSDTypeDef extends Serializable {

    LSDType getPrimaryType();

    List<LSDType> getSecondaryTypes();

    String asString();


    boolean canBe(LSDDictionaryTypes type);
}
