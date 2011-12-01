package cazcade.liquid.api.lsd;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;

/**
 * @author Neil Ellis
 */

public interface LSDNode {
    boolean isLeaf();

    boolean isArray();

    @Nonnull
    List<LSDNode> getChildren();

    @Nullable
    String getLeafValue();

    String getName();
}
