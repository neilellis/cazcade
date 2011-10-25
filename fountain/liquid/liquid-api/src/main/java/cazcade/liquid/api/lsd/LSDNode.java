package cazcade.liquid.api.lsd;

import java.util.List;

/**
 * @author Neil Ellis
 */

public interface LSDNode {
    boolean isLeaf();
    boolean isArray();
    List<LSDNode> getChildren();
    String getLeafValue();

    String getName();
}
