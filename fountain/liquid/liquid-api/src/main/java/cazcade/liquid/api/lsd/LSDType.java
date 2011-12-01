package cazcade.liquid.api.lsd;

import javax.annotation.Nullable;
import java.io.Serializable;
import java.util.List;

/**
 * @author neilelliz@cazcade.com
 */
public interface LSDType extends Serializable {

    /**
     * The coarsest grouping i.e. Genus.Family.TypeClass
     */
    @Nullable
    String getGenus();

    /**
     * The next coarsest grouping after Genus i.e. Genus.Family.TypeClass
     */
    @Nullable
    String getFamily();

    /**
     * The next coarsest grouping after Family i.e. Genus.Family.TypeClass
     */
    @Nullable
    String getTypeClass();

    List<String> getFlavors();

    @Nullable
    String asString();

    LSDType getClassOnlyType();

    LSDType getParentType();

    @Nullable
    String toString();

    boolean isSystemType();

    boolean isA(LSDDictionaryTypes dictionaryType);

    boolean canBe(LSDDictionaryTypes type);
}
