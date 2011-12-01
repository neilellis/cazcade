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
    public void marshal(Object o, @Nonnull HierarchicalStreamWriter hierarchicalStreamWriter, MarshallingContext marshallingContext) {
        LSDEntity entity = (LSDEntity) o;
        LSDNode lsdNode = entity.asFormatIndependentTree();
        marshal(hierarchicalStreamWriter, lsdNode);
    }

    private void marshal(@Nonnull HierarchicalStreamWriter hierarchicalStreamWriter, @Nonnull LSDNode lsdNode) {
        if (lsdNode.isLeaf()) {
//            hierarchicalStreamWriter.startNode(lsdNode.getName());
            hierarchicalStreamWriter.setValue(lsdNode.getLeafValue());
//            hierarchicalStreamWriter.endNode();
        } else {
            List<LSDNode> lsdNodeList = lsdNode.getChildren();
            for (LSDNode node : lsdNodeList) {

                if (node.isArray()) {
                    List<LSDNode> children = node.getChildren();
                    for (LSDNode child : children) {
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
    public Object unmarshal(@Nonnull HierarchicalStreamReader hierarchicalStreamReader, UnmarshallingContext unmarshallingContext) {
        Map<String, List> map = unmarshal(hierarchicalStreamReader);
        LSDSimpleNode lsdNode = new LSDSimpleNode("root", Arrays.asList(map));
        LSDSimpleEntity entity = LSDSimpleEntity.createFromNode(lsdNode);
        return entity;
    }

    @Nonnull
    private Map<String, List> unmarshal(@Nonnull HierarchicalStreamReader hierarchicalStreamReader) {
        Map<String, List> map = new HashMap<String, List>();
        while (hierarchicalStreamReader.hasMoreChildren()) {
            hierarchicalStreamReader.moveDown();
            String name = hierarchicalStreamReader.getNodeName();
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
    public String toString(Object o) {
        return null;
    }

    @Nullable
    public Object fromString(String s) {
        return null;
    }

    public boolean canConvert(@Nonnull Class aClass) {
        return aClass.equals(LSDSimpleEntity.class);
    }
}
