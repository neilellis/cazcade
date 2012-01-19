package cazcade.liquid.api.lsd;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;

/**
 * @author Neil Ellis
 */

public interface LSDNode {
    @Nonnull
    List<LSDNode> getChildren();

    @Nullable
    String getLeafValue();

    String getName();

    boolean isArray();

    boolean isLeaf();
}
