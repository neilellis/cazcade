package cazcade.liquid.impl;

import cazcade.liquid.api.lsd.LSDEntity;
import cazcade.liquid.api.lsd.LSDMarshaler;
import cazcade.liquid.api.lsd.LSDNode;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.*;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.OutputStream;
import java.util.List;

/**
 * @author Neil Ellis
 */

public class PListMarshaler implements LSDMarshaler {
    public void marshal(LSDEntity lsdEntity, OutputStream output) {
        if(lsdEntity == null) {
            throw new NullPointerException("A null lsdEntity was passed to be marshalled, this probably came from the datastore, maybe you want to see how it managed to return a null");
        }
        DocumentBuilderFactory dbfac = DocumentBuilderFactory.newInstance();
        DocumentBuilder docBuilder = null;
        try {
            docBuilder = dbfac.newDocumentBuilder();
        } catch (ParserConfigurationException e) {
            throw new RuntimeException(e);
        }
        Document doc = docBuilder.newDocument();
        Element plist = doc.createElement("plist");
        plist.setAttribute("version", "1.0");
        doc.appendChild(plist);
        Element root = doc.createElement("dict");
        plist.appendChild(root);
        final LSDNode lsdNode = lsdEntity.asFormatIndependentTree();
        process(doc, root, lsdNode);
        try {
            // Prepare the DOM document for writing
            Source source = new DOMSource(doc);

            // Prepare the output file

            Result result = new StreamResult(output);
            // Write the DOM document to the file
            Transformer xformer = TransformerFactory.newInstance().newTransformer();
            xformer.transform(source, result);
        } catch (TransformerConfigurationException e) {
            throw new RuntimeException(e);
        } catch (TransformerException e) {
            throw new RuntimeException(e);
        }
    }

    public String getMimeType() {
        return "text/plist";
    }

    private void process(Document doc, Element currentElement, LSDNode lsdNode) {
        for (LSDNode node : lsdNode.getChildren()) {
            final Element keyElement = doc.createElement("key");
            keyElement.setTextContent(node.getName());
            currentElement.appendChild(keyElement);
            if (node.isLeaf()) {
                if (node.isArray()) {
                    processArray(doc, currentElement, node);
                } else {
                    final Element valueElement = doc.createElement("string");
                    valueElement.setTextContent(node.getLeafValue());
                    currentElement.appendChild(valueElement);
                }
            } else {
                if (node.isArray()) {
                    processArray(doc, currentElement, node);
                } else {
                    Element root = doc.createElement("dict");
                    currentElement.appendChild(root);
                    process(doc, root, node);
                }
            }
        }
    }

    private void processArray(Document doc, Element currentElement, LSDNode node) {
        Element valueElement = doc.createElement("array");
        currentElement.appendChild(valueElement);
        final List<LSDNode> children = node.getChildren();
        for (LSDNode child : children) {
            if (child.isLeaf()) {
                final Element arrayElement = doc.createElement("string");
                arrayElement.setTextContent(child.getLeafValue());
                valueElement.appendChild(arrayElement);
            } else {
                Element root = doc.createElement("dict");
                valueElement.appendChild(root);
                process(doc, root, child);
            }
        }
    }
}
