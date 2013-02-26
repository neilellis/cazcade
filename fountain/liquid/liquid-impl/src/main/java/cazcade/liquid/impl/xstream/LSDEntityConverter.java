/*
 * Copyright (c) 2009-2013 Cazcade Limited  - All Rights Reserved
 */

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
    public boolean canConvert(@Nonnull final Class aClass) {
        return aClass.equals(SimpleEntity.class);
    }

    @Nullable
    public Object fromString(final String s) {
        return null;
    }

    public void marshal(final Object o, @Nonnull final HierarchicalStreamWriter hierarchicalStreamWriter, final MarshallingContext marshallingContext) {
        final TransferEntity entity = (TransferEntity) o;
        final Node node = entity.asFormatIndependentTree();
        marshal(hierarchicalStreamWriter, node);
    }

    @Nullable
    public String toString(final Object o) {
        return null;
    }

    @Nonnull
    public Object unmarshal(@Nonnull final HierarchicalStreamReader hierarchicalStreamReader, final UnmarshallingContext unmarshallingContext) {
        final Map<String, List> map = unmarshal(hierarchicalStreamReader);
        final SimpleNode lsdNode = new SimpleNode("root", Arrays.asList(map));
        final SimpleEntity<? extends TransferEntity> entity = SimpleEntity.createFromNode(lsdNode);
        return entity;
    }

    private void marshal(@Nonnull final HierarchicalStreamWriter hierarchicalStreamWriter, @Nonnull final Node lsdNode) {
        if (lsdNode.isLeaf()) {
            //            hierarchicalStreamWriter.startNode(lsdNode.name());
            hierarchicalStreamWriter.setValue(lsdNode.getLeafValue());
            //            hierarchicalStreamWriter.endNode();
        } else {
            final List<Node> nodeList = lsdNode.getChildren();
            for (final Node node : nodeList) {
                if (node.isArray()) {
                    final List<Node> children = node.getChildren();
                    for (final Node child : children) {
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
    private Map<String, List> unmarshal(@Nonnull final HierarchicalStreamReader hierarchicalStreamReader) {
        final Map<String, List> map = new HashMap<String, List>();
        while (hierarchicalStreamReader.hasMoreChildren()) {
            hierarchicalStreamReader.moveDown();
            final String name = hierarchicalStreamReader.getNodeName();
            if (!name.matches("[a-z]+[a-z0-9\\-_]*")) {
                throw new SerializationException("The XML node '" + name + "' contains an illegal charcter.");
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
}
