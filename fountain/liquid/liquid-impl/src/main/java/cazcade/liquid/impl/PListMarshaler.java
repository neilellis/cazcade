/*
 * Copyright (c) 2009-2013 Cazcade Limited  - All Rights Reserved
 */

package cazcade.liquid.impl;

import cazcade.liquid.api.lsd.LSDNode;
import cazcade.liquid.api.lsd.LSDTransferEntity;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
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
    @Nonnull
    public String getMimeType() {
        return "text/plist";
    }

    public void marshal(@Nullable final LSDTransferEntity lsdEntity, final OutputStream output) {
        if (lsdEntity == null) {
            throw new NullPointerException("A null lsdEntity was passed to be marshalled, this probably came from the datastore, maybe you want to see how it managed to return a null");
        }
        final DocumentBuilderFactory dbfac = DocumentBuilderFactory.newInstance();
        DocumentBuilder docBuilder = null;
        try {
            docBuilder = dbfac.newDocumentBuilder();
        } catch (ParserConfigurationException e) {
            throw new RuntimeException(e);
        }
        final Document doc = docBuilder.newDocument();
        final Element plist = doc.createElement("plist");
        plist.setAttribute("version", "1.0");
        doc.appendChild(plist);
        final Element root = doc.createElement("dict");
        plist.appendChild(root);
        final LSDNode lsdNode = lsdEntity.asFormatIndependentTree();
        process(doc, root, lsdNode);
        try {
            // Prepare the DOM document for writing
            final Source source = new DOMSource(doc);

            // Prepare the output file

            final Result result = new StreamResult(output);
            // Write the DOM document to the file
            final Transformer xformer = TransformerFactory.newInstance().newTransformer();
            xformer.transform(source, result);
        } catch (TransformerConfigurationException e) {
            throw new RuntimeException(e);
        } catch (TransformerException e) {
            throw new RuntimeException(e);
        }
    }

    private void process(@Nonnull final Document doc, @Nonnull final Element currentElement, @Nonnull final LSDNode lsdNode) {
        for (final LSDNode node : lsdNode.getChildren()) {
            final Element keyElement = doc.createElement("key");
            keyElement.setTextContent(node.getName());
            currentElement.appendChild(keyElement);
            if (node.isLeaf()) {
                if (node.isArray()) {
                    processArray(doc, currentElement, node);
                }
                else {
                    final Element valueElement = doc.createElement("string");
                    valueElement.setTextContent(node.getLeafValue());
                    currentElement.appendChild(valueElement);
                }
            }
            else {
                if (node.isArray()) {
                    processArray(doc, currentElement, node);
                }
                else {
                    final Element root = doc.createElement("dict");
                    currentElement.appendChild(root);
                    process(doc, root, node);
                }
            }
        }
    }

    private void processArray(@Nonnull final Document doc, @Nonnull final Element currentElement, @Nonnull final LSDNode node) {
        final Element valueElement = doc.createElement("array");
        currentElement.appendChild(valueElement);
        final List<LSDNode> children = node.getChildren();
        for (final LSDNode child : children) {
            if (child.isLeaf()) {
                final Element arrayElement = doc.createElement("string");
                arrayElement.setTextContent(child.getLeafValue());
                valueElement.appendChild(arrayElement);
            }
            else {
                final Element root = doc.createElement("dict");
                valueElement.appendChild(root);
                process(doc, root, child);
            }
        }
    }
}
