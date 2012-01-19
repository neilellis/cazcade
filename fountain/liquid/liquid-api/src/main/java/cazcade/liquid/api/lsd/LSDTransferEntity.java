package cazcade.liquid.api.lsd;

import javax.annotation.Nonnull;
import java.io.Serializable;
import java.util.Map;

/**
 * An {@link LSDBaseEntity} that can safely be transferred between store, web server and web browser.
 *
 * @author Neil Ellis
 */

public interface LSDTransferEntity extends Serializable, LSDBaseEntity {
    @Nonnull
    LSDNode asFormatIndependentTree();


    @Nonnull
    Map<String, String> asMapForPersistence(boolean ignoreType, boolean update);


    @Nonnull
    LSDTransferEntity asUpdateEntity();


    @Nonnull
    LSDTransferEntity copy();

    //    boolean isValidOrEmptyValue(LSDDictionary key);


    /**
     * @deprecated use toString() instead.
     */
    String dump();

    /**
     * Use this for JSPs i.e. JSTL EL
     *
     * @return
     */
    @Nonnull
    Map<String, String> getCamelCaseMap();


    /**
     * The canonical format.
     *
     * @return a map of name/value pairs.
     */
    Map<String, String> getMap();

    //For templating  they take camel case values like entity.set("imageUrl") ... useful for templates etc.
}
