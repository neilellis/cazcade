package cazcade.liquid.impl;

import cazcade.common.Logger;
import cazcade.liquid.api.lsd.LSDEntity;
import cazcade.liquid.api.lsd.LSDEntityFactory;
import cazcade.liquid.api.lsd.LSDSimpleEntity;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
import java.io.InputStream;

/**
 * @author Neil Ellis
 */

public class PListUnmarshaler implements LSDUnmarshaler {

    private final DocumentBuilderFactory dbfac = DocumentBuilderFactory.newInstance();

    @Nonnull
    private static final Logger log = Logger.getLogger(PListUnmarshaler.class);

    private LSDEntityFactory lsdEntityFactory;

    public void unmarshal(@Nullable LSDEntity lsdEntity, InputStream input) {
        if (lsdEntity == null) {
            throw new NullPointerException("A null lsdEntity was passed to be marshalled, this probably came from the datastore, maybe you want to see how it managed to return a null");
        }
        System.out.println("Unmarshalling.");
        DocumentBuilder docBuilder = null;
        Document document;
        try {
            docBuilder = dbfac.newDocumentBuilder();
            document = docBuilder.parse(input);
        } catch (ParserConfigurationException e) {
            throw new RuntimeException(e);
        } catch (SAXException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        Element rootElement = document.getDocumentElement();
        assertThat(rootElement.getNodeName().equals("plist"), "Root node of a plist must be 'plist'.");
        walk(lsdEntity, "", "", rootElement);


    }

    private void walk(@Nonnull LSDEntity entity, @Nonnull String prefix, String lastKey, @Nonnull Element rootElement) {
        NodeList childNodes = rootElement.getChildNodes();

        for (int i = 0; i < childNodes.getLength(); i++) {
            Node node = childNodes.item(i);
            assertThat(node.getNodeType() == Document.ELEMENT_NODE, "Elements should only contain XML elements as children in a plist.");
            String nodeName = node.getNodeName();
            if (nodeName.equals("dict")) {
                walk(entity, prefix.isEmpty() ? lastKey : prefix + "." + lastKey, lastKey, (Element) node);
            } else if (nodeName.equals("key")) {
                lastKey = node.getTextContent();
            } else if (nodeName.equals("string")) {
                entity.setValue(prefix.isEmpty() ? lastKey : prefix + "." + lastKey, node.getTextContent());
            } else if (nodeName.equals("array")) {
                NodeList grandChildNodes = node.getChildNodes();
                for (int j = 0; j < grandChildNodes.getLength(); j++) {
                    Node grandChildNode = grandChildNodes.item(i);
                    assertThat(node.getNodeType() == Document.ELEMENT_NODE, "Arrays should only contain XML elements in a plist.");
                    walk(entity, prefix.isEmpty() ? lastKey + "." + j : prefix + "." + lastKey + j, lastKey, (Element) grandChildNode);
                }
            } else {
                assertThat(false, "Found an element with a name of " + node.getNodeName() + " in a plist, it is not supported.");
            }
        }
    }

    @Nonnull
    public LSDEntity unmarshal(InputStream input) {
        LSDEntity lsdEntity = LSDSimpleEntity.createEmpty();
        unmarshal(lsdEntity, input);
        return lsdEntity;
    }

    private void assertThat(boolean condition, String message) {
        if (!condition) {
            throw new RuntimeException(message);
        }
    }

    public void setLsdFactory(LSDEntityFactory lsdEntityFactory) {
        this.lsdEntityFactory = lsdEntityFactory;
    }
}