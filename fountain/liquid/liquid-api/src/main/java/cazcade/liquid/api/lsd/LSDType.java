package cazcade.liquid.api.lsd;

import java.io.Serializable;
import java.util.List;

/**
 * @author neilelliz@cazcade.com
 */
public interface LSDType extends Serializable {

    /**
     * The coarsest grouping i.e. Genus.Family.TypeClass
     */
    String getGenus();

    /**
     * The next coarsest grouping after Genus i.e. Genus.Family.TypeClass
     */
    String getFamily();

    /**
     * The next coarsest grouping after Family i.e. Genus.Family.TypeClass
     */
    String getTypeClass();

    List<String> getFlavors();

    String asString();

    LSDType getClassOnlyType();

    LSDType getParentType();

    String toString();

    boolean isSystemType();

    boolean isA(LSDDictionaryTypes dictionaryType);

    boolean canBe(LSDDictionaryTypes type);
}
