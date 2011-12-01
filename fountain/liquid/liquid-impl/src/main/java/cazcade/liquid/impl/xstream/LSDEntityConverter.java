package cazcade.liquid.impl.xstream;

import cazcade.liquid.api.lsd.*;
import com.thoughtworks.xstream.converters.Converter;
import com.thoughtworks.xstream.converters.MarshallingContext;
import com.thoughtworks.xstream.converters.UnmarshallingContext;
import com.thoughtworks.xstream.io.HierarchicalStreamReader;
import com.thoughtworks.xstream.io.HierarchicalStreamWriter;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.*;

/**
 * @author neilelliz@cazcade.com
 */
public class LSDEntityConverter implements Converter {
    public void marshal(final Object o, @Nonnull final HierarchicalStreamWriter hierarchicalStreamWriter, final MarshallingContext marshallingContext) {
        final LSDTransferEntity entity = (LSDTransferEntity) o;
        final LSDNode lsdNode = entity.asFormatIndependentTree();
        marshal(hierarchicalStreamWriter, lsdNode);
    }

    private void marshal(@Nonnull final HierarchicalStreamWriter hierarchicalStreamWriter, @Nonnull final LSDNode lsdNode) {
        if (lsdNode.isLeaf()) {
//            hierarchicalStreamWriter.startNode(lsdNode.getName());
            hierarchicalStreamWriter.setValue(lsdNode.getLeafValue());
//            hierarchicalStreamWriter.endNode();
        } else {
            final List<LSDNode> lsdNodeList = lsdNode.getChildren();
            for (final LSDNode node : lsdNodeList) {

                if (node.isArray()) {
                    final List<LSDNode> children = node.getChildren();
                    for (final LSDNode child : children) {
                        hierarchicalStreamWriter.startNode(child.getName());
                        marshal(hierarchicalStreamWriter, child);
                        hierarchicalStreamWriter.endNode();
                    }
                } else {
                    hierarchicalStreamWriter.startNode(node.getName());
                    marshal(hierarchicalStreamWriter, node);
                    hierarchicalStreamWriter.endNode();
                }
            }
        }
    }


    @Nonnull
    public Object unmarshal(@Nonnull final HierarchicalStreamReader hierarchicalStreamReader, final UnmarshallingContext unmarshallingContext) {
        final Map<String, List> map = unmarshal(hierarchicalStreamReader);
        final LSDSimpleNode lsdNode = new LSDSimpleNode("root", Arrays.asList(map));
        final LSDSimpleEntity entity = LSDSimpleEntity.createFromNode(lsdNode);
        return entity;
    }

    @Nonnull
    private Map<String, List> unmarshal(@Nonnull final HierarchicalStreamReader hierarchicalStreamReader) {
        final Map<String, List> map = new HashMap<String, List>();
        while (hierarchicalStreamReader.hasMoreChildren()) {
            hierarchicalStreamReader.moveDown();
            final String name = hierarchicalStreamReader.getNodeName();
            if (!name.matches("[a-z]+[a-z0-9\\-_]*")) {
                throw new LSDSerializationException("The XML node '" + name + "' contains an illegal charcter.");
            }
            List list = map.get(name);
            if (list == null) {
                list = new ArrayList();
                map.put(name, list);
            }

            if (hierarchicalStreamReader.hasMoreChildren()) {
                list.add(unmarshal(hierarchicalStreamReader));
            } else {
                list.add(hierarchicalStreamReader.getValue());
            }
            hierarchicalStreamReader.moveUp();
        }
        return map;
    }


    @Nullable
    public String toString(final Object o) {
        return null;
    }

    @Nullable
    public Object fromString(final String s) {
        return null;
    }

    public boolean canConvert(@Nonnull final Class aClass) {
        return aClass.equals(LSDSimpleEntity.class);
    }
}
