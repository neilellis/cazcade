package cazcade.liquid.api.lsd;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

/**
 * @author Neil Ellis
 */

public class LSDSimpleNode implements LSDNode {
    private final String name;
    @Nullable
    private String value;
    @Nonnull
    private final List<LSDNode> children = new ArrayList<LSDNode>();
    private final boolean isArray;

    public LSDSimpleNode(final String name, @Nonnull final List list) {
        super();
        this.name = name;
        isArray = list.size() > 1;
        for (final Object o : list) {
            if (isArray) {
                children.add(new LSDSimpleNode(name, Arrays.asList(o)));
            } else {
                if (o instanceof Map) {
                    final Map<String, List> map = (Map<String, List>) o;
                    for (final Map.Entry<String, List> entry : map.entrySet()) {
                        children.add(new LSDSimpleNode(entry.getKey(), entry.getValue()));
                    }
                } else if (o instanceof String) {
                    value = (String) o;
                } else if (o == null) {
                    throw new NullPointerException("Tried to create LSDNode (" + name + ") with a null entry in the list.");
                } else {
                    throw new IllegalArgumentException("The only types in a node list are Strings and Maps not " + o.getClass());
                }
            }
        }
    }

    public boolean isLeaf() {
        return value != null;
    }

    public boolean isArray() {
        return isArray;
    }

    @Nonnull
    public List<LSDNode> getChildren() {
        if (isLeaf()) {
            throw new UnsupportedOperationException("Cannot get a child from a leaf node.");
        }
        return children;
    }

    @Nullable
    public String getLeafValue() {
        if (!isLeaf()) {
            throw new UnsupportedOperationException("Cannot get a value from a non leaf node.");
        }
        return value;
    }

    public String getName() {
        return name;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder();
        sb.append("LSDSimpleNode");
        sb.append("{name='").append(name).append('\'');
        sb.append(", value='").append(value).append('\'');
        sb.append(", children=").append(children);
        sb.append(", isArray=").append(isArray);
        sb.append('}');
        return sb.toString();
    }
}
