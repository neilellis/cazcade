/*
 * Copyright (c) 2009-2013 Cazcade Limited  - All Rights Reserved
 */

package cazcade.liquid.api.lsd;

import javax.annotation.Nonnull;
import java.util.List;

/**
 * @author Neil Ellis
 */

public interface Node {
    @Nonnull List<Node> getChildren();

    @Nonnull String getLeafValue();

    String getName();

    boolean isArray();

    boolean isLeaf();
}
