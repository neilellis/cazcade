package cazcade.liquid.impl;

import cazcade.common.Logger;
import cazcade.liquid.api.lsd.LSDBaseEntity;
import cazcade.liquid.api.lsd.LSDEntityFactory;
import cazcade.liquid.api.lsd.LSDSimpleEntity;
import cazcade.liquid.api.lsd.LSDTransferEntity;
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

    public void unmarshal(@Nullable final LSDBaseEntity lsdEntity, final InputStream input) {
        if (lsdEntity == null) {
            throw new NullPointerException("A null lsdEntity was passed to be marshalled, this probably came from the datastore, maybe you want to see how it managed to return a null");
        }
        System.out.println("Unmarshalling.");
        DocumentBuilder docBuilder = null;
        final Document document;
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
        final Element rootElement = document.getDocumentElement();
        assertThat("plist".equals(rootElement.getNodeName()), "Root node of a plist must be 'plist'.");
        walk(lsdEntity, "", "", rootElement);


    }

    private void walk(@Nonnull final LSDBaseEntity entity, @Nonnull final String prefix, String lastKey, @Nonnull final Element rootElement) {
        final NodeList childNodes = rootElement.getChildNodes();

        for (int i = 0; i < childNodes.getLength(); i++) {
            final Node node = childNodes.item(i);
            assertThat(node.getNodeType() == Document.ELEMENT_NODE, "Elements should only contain XML elements as children in a plist.");
            final String nodeName = node.getNodeName();
            if ("dict".equals(nodeName)) {
                walk(entity, prefix.isEmpty() ? lastKey : prefix + "." + lastKey, lastKey, (Element) node);
            } else if ("key".equals(nodeName)) {
                lastKey = node.getTextContent();
            } else if ("string".equals(nodeName)) {
                entity.setValue(prefix.isEmpty() ? lastKey : prefix + "." + lastKey, node.getTextContent());
            } else if ("array".equals(nodeName)) {
                final NodeList grandChildNodes = node.getChildNodes();
                for (int j = 0; j < grandChildNodes.getLength(); j++) {
                    final Node grandChildNode = grandChildNodes.item(i);
                    assertThat(node.getNodeType() == Document.ELEMENT_NODE, "Arrays should only contain XML elements in a plist.");
                    walk(entity, prefix.isEmpty() ? lastKey + "." + j : prefix + "." + lastKey + j, lastKey, (Element) grandChildNode);
                }
            } else {
                assertThat(false, "Found an element with a name of " + node.getNodeName() + " in a plist, it is not supported.");
            }
        }
    }

    @Nonnull
    public LSDTransferEntity unmarshal(final InputStream input) {
        final LSDTransferEntity lsdEntity = LSDSimpleEntity.createEmpty();
        unmarshal(lsdEntity, input);
        return lsdEntity;
    }

    private void assertThat(final boolean condition, final String message) {
        if (!condition) {
            throw new RuntimeException(message);
        }
    }

    public void setLsdFactory(final LSDEntityFactory lsdEntityFactory) {
        this.lsdEntityFactory = lsdEntityFactory;
    }
}