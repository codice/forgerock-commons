/*
 * The contents of this file are subject to the terms of the Common Development and
 * Distribution License (the License). You may not use this file except in compliance with the
 * License.
 *
 * You can obtain a copy of the License at legal/CDDLv1.0.txt. See the License for the
 * specific language governing permission and limitations under the License.
 *
 * When distributing Covered Software, include this CDDL Header Notice in each file and include
 * the License file at legal/CDDLv1.0.txt. If applicable, add the following below the CDDL
 * Header, with the fields enclosed by brackets [] replaced by your own identifying
 * information: "Portions copyright [year] [name of copyright owner]".
 *
 * Copyright 2013 ForgeRock Inc.
 */

package org.forgerock.jaspi.container.initialisation;

import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.xml.sax.SAXException;

import javax.xml.XMLConstants;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import java.io.IOException;
import java.io.InputStream;

/**
 * Provides methods to parse a xml file into an Object representation.
 *
 * @param <T> The type of object that will be returned once the xml file has been parsed.
 */
public abstract class AbstractXmlFileParser<T> {

    /**
     * Parses the xml file into an Object representation.
     *
     * @param fileName The name of the xml file.
     * @return An Object representation of the parsed xml file.
     * @throws ParserConfigurationException If there is a problem parsing the xml file.
     * @throws IOException If there is a problem reading the xml file.
     * @throws SAXException If there is a problem parsing the xml file.
     */
    public T parseXmlFile(String fileName) throws ParserConfigurationException,
            IOException, SAXException {

        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        dbf.setValidating(false);
        dbf.setNamespaceAware(true);
        dbf.setXIncludeAware(false);
        dbf.setFeature("http://xml.org/sax/features/external-general-entities", false);
        dbf.setFeature("http://xml.org/sax/features/external-parameter-entities", false);
        dbf.setFeature(XMLConstants.FEATURE_SECURE_PROCESSING, true);
        DocumentBuilder db = dbf.newDocumentBuilder();
        db.setEntityResolver(new XMLHandler());

        InputStream in = Thread.currentThread().getContextClassLoader()
                .getResourceAsStream(fileName);

        Document document = db.parse(in);

        in.close();

        return walk(document);
    }

    /**
     * Walks a given document node and extracts the required information from it.
     *
     * @param node The document node to walk.
     * @return An Object representation of the parsed xml node.
     */
    protected abstract T walk(Node node);

    /**
     * Extracts the given attribute name from the document node.
     *
     * @param node The document node.
     * @param name The attribute name.
     * @return The value of the node's attribute.
     */
    protected String getAttribute(Node node, String name) {
        NamedNodeMap nnm = node.getAttributes();
        if (nnm != null) {
            int len = nnm.getLength();
            Attr attr;
            for (int i = 0; i < len; i++) {
                attr = (Attr) nnm.item(i);
                if (attr.getNodeName().equals(name)) {
                    return attr.getNodeValue();
                }
            }
        }
        return null;
    }
}
