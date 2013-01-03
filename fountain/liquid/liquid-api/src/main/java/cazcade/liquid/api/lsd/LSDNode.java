package cazcade.liquid.api.lsd;

import javax.annotation.Nonnull;
import java.util.List;

/**
 * @author Neil Ellis
 */

public interface LSDNode {
    @Nonnull
    List<LSDNode> getChildren();

    @Nonnull
    String getLeafValue();

    String getName();

    boolean isArray();

    boolean isLeaf();
}
