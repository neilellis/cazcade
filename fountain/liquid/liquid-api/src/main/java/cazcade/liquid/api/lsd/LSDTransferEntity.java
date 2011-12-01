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


    /**
     * The canonical format.
     *
     * @return a map of name/value pairs.
     */
    Map<String, String> getMap();

    /**
     * Use this for JSPs i.e. JSTL EL
     *
     * @return
     */
    @Nonnull
    Map<String, String> getCamelCaseMap();


    @Nonnull
    Map<String, String> asMapForPersistence(boolean ignoreType, boolean update);

    @Nonnull
    LSDNode asFormatIndependentTree();

    //    boolean isValidOrEmptyValue(LSDDictionary key);


    /**
     * @deprecated use toString() instead.
     */
    String dump();


    @Nonnull
    LSDTransferEntity copy();


    @Nonnull
    LSDTransferEntity asUpdateEntity();

    //For templating  they take camel case values like entity.set("imageUrl") ... useful for templates etc.


}
