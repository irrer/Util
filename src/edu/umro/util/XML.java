package edu.umro.util;

/*
 * Copyright 2012 Regents of the University of Michigan
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.StringReader;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

/**
 * Interface method for reading XML.
 * 
 * @author Jim Irrer  irrer@umich.edu 
 *
 */
public class XML {

    /** List of characters in XML that require special treatment.  Note that the
     * processing loop that uses this list requires that the & be first in the list,
     * otherwise it could be interpreted recursively. */
    private static final char[] SPECIAL_CHARS = { '&',  '<','>', '"', '\'' };

    /** List of characters in XML that require special treatment. */
    private static final String[] SPECIAL_CHAR_NAME = { "&amp;", "&lt;", "&gt;", "&quot;", "&apos;" };

    /**
     * Use XPATH to grab a single text value from an XML node.
     * The path given is expected to end in text() or reference
     * an attribute.
     *
     * @param node XML node containing value.
     *
     * @param path XPATH specification.
     *
     * @return The value of the item in the node, or, null if
     *        the path was bad or the item had no value.
     */
    public static synchronized String getValue(Node node, String path) throws UMROException {
        if (node == null) {
            throw new UMROException("Util.getValue was given null node with path '" + path + ".");
        }
        String value = null;

        NodeList nodeList = getMultipleNodes(node, path);
        if ((nodeList != null) && (nodeList.getLength() > 0)) {
            value = nodeList.item(0).getNodeValue();
        }

        return value;
    }




    /**
     * Use XPATH to grab a single text value from an XML node. 
     * Require the value to exist, and throw an exception if it does not.
     * The path given is expected to end in text() or reference
     * an attribute.
     *
     * @param node XML node containing value.
     *
     * @param path XPATH specification.
     *
     * @return The value of the item in the node.
     */
    public static String getRequiredValue(Node node, String path) throws UMROException {
        String value = getValue(node, path);
        if (value == null) {
            throw new UMROException("Util.getRequiredValue expected to get a node with path '" + path + "' but found no matches.");
        }
        return value;
    }


    /**
     * Use XPATH to grab multiple node values from an XML node.
     *
     * @param node XML node containing value.
     *
     * @param path XPATH specification.
     *
     * @return A list of nodes.  If the path is invalid, then
     *         return an empty list.
     */
    public static synchronized NodeList getMultipleNodes(Node node, String path) throws UMROException {
        if (node == null) {
            throw new UMROException("getMultipleNodes was given null node with path '" + path + ".");
        }
        NodeList nodeList = null;
        try {
            XPathFactory factory = XPathFactory.newInstance();
            XPath xPath = factory.newXPath();
            XPathExpression expr = xPath.compile(path);
            nodeList = (NodeList)(expr.evaluate(node, XPathConstants.NODESET));
        }
        catch (XPathExpressionException ex) {
            throw new UMROException("The path for getMultipleNodes is not valid: " + path);
        }
        return nodeList;
    }



    /**
     * Get the first node from a list that is only supposed to
     * contain one node.  This is a convenience wrapper for 
     * <code>getMultipleNodes</code>.
     *
     * @param node XML node containing value.
     *
     * @param path XPATH specification.
     *
     * @return A single node.
     */
    public static Node getSingleNode(Node node, String path) throws UMROException {
        NodeList nodeList = getMultipleNodes(node, path);
        if (nodeList.getLength() == 0) {
            throw new UMROException("Did not find any nodes for path " + path);
        }
        if (nodeList.getLength() > 1) {
            throw new UMROException("Found " + nodeList.getLength() + " but expected only one node for path " + path);
        }
        return nodeList.item(0);
    }


    /**
     * Convert XML document text to a DOM.  Throw a
     * RemoteException if there is a problem.
     * 
     * @param xmlText Text to parse.
     * 
     * @return DOM version of XML text.
     */
    public static synchronized Document parseToDocument(String xmlText) throws UMROException {
        xmlText = xmlText.substring(xmlText.indexOf('<'));
        Document document = null;
        try {
            DocumentBuilderFactory domFactory = DocumentBuilderFactory.newInstance();
            domFactory.setNamespaceAware(true);
            DocumentBuilder builder = domFactory.newDocumentBuilder();
            document = builder.parse(new InputSource(new StringReader(xmlText)));
        }
        catch (IOException ex) {
            throw new UMROException("IOException while parsing document: " + ex);
        }
        catch (ParserConfigurationException ex) {
            throw new UMROException("ParserConfigurationException while parsing document: " + ex);
        }
        catch (SAXException ex) {
            throw new UMROException("SAXException while parsing document: " + ex);
        }
        return document;
    }



    /**
     * Convert a node into nicely formatted XML text.  The text is a
     * stand-alone XML document.
     * 
     * @param node The node to convert.
     * 
     * @return The text representation of the node.
     */
    public static String domToString(Node node) throws UMROException {

        // Use a Transformer for output
        TransformerFactory tFactory = TransformerFactory.newInstance();
        Transformer transformer = null;
        try {
            transformer = tFactory.newTransformer();
        }
        catch (TransformerConfigurationException ex) {
            throw new UMROException("Util.domToString: Unable to create transformer.  Exception: " + ex);
        }

        DOMSource source = new DOMSource(node);
        ByteArrayOutputStream byteOutStream = new ByteArrayOutputStream();
        StreamResult streamResult = new StreamResult(byteOutStream);
        try {
            transformer.transform(source, streamResult);
        } catch (TransformerException ex) {
            throw new UMROException("Util.domToString  Unable to transform DOM to text.  TransformerException: " + ex);
        }
        String xmlText = byteOutStream.toString();
        return xmlText;
    }

    /**
     * Translate XML special characters so that they will be interpreted literally.
     * 
     * @param value Text to be translated.
     * 
     * @return Text with special characters converted.
     */
    public static String escapeSpecialChars(String value) {
        if (value != null) {
            for (int c = 0; c < SPECIAL_CHARS.length; c++) {
                value = value.replaceAll(""+SPECIAL_CHARS[c], SPECIAL_CHAR_NAME[c]);
            }
        }
        return value;
    }


    private static boolean isRegularChar(byte c){
        return
        ((c >= ' ') && (c <= 126)) ||
        (c == '\r') ||
        (c == '\n') ||
        (c == '\t');
    }


    public static void replaceControlCharacters(Node node, char replacement) {
        String value = node.getNodeValue();
        if (value != null) {
            boolean modified = false;

            byte[] bytes = value.getBytes();
            for (int b = 0; b < bytes.length; b++) {
                if (!isRegularChar(bytes[b])) {
                    bytes[b] = (byte) replacement;
                    modified = true;
                }
            }
            if (modified) node.setNodeValue(new String(bytes));
        }

        NamedNodeMap attrList = node.getAttributes();
        for (int a = 0; (attrList != null) && (a < attrList.getLength()); a++) {
            replaceControlCharacters(attrList.item(a), replacement);
        }

        NodeList childNodeList = node.getChildNodes();
        for (int c = 0; (childNodeList != null) && (c < childNodeList.getLength()); c++) {
            replaceControlCharacters(childNodeList.item(c), replacement);
        }
    }


    /**
     * Get the value of the given attribute for the given node.  If the node has
     * no such attribute, then return null.
     * 
     * @param node Node to search that allegedly has this attribute.
     * 
     * @param attributeName Name of attribute to find.
     * 
     * @return The value of the attribute, or null if not found.
     */
    public static String getAttributeValue(Node node, String attributeName) {
        NamedNodeMap attributeList = node.getAttributes();
        if (attributeList != null) {
            Node attrNode = attributeList.getNamedItem(attributeName);
            if (attrNode != null) {
                String attributeValue = attrNode.getNodeValue();
                return attributeValue;
            }
        }
        return null;
    }


    public static void main(String[] args) {
        try {
            {
                boolean allSuccess = true;
                String stuff[] = {
                        "good",
                        "/ngood/t",
                        "aa\0bad\0",
                        "aa\127bad\0",
                        "aa\144bad\0",
                        "aa\126good",
                        "bad\6"
                };
                for (String value : stuff) {
                    boolean match = value.matches(".*[^\r\n\t -~].*");
                    boolean bad = value.matches(".*bad.*");
                    allSuccess = allSuccess && (match == bad);
                    System.out.println("    " + value + "   match: " + match + "    " + (match == bad ? "success" : "fail"));
                }
                System.out.println("allSuccess: " + allSuccess);
            }

            String xmlText =
                "<UMROEnvelope JarInfo='1_0_0' Time='2009 12 01 10:34:11.955'>\n" +
                "  <Call>\n"                                                      +
                "    <CDAConnect JarInfo='CDAConnect 1.0.0'>\n"                   +
                "      <connect>\n"                                               +
                "        <String Name='caseSet'>ClinicalDirectory</String>\n"     +
                "        <String Name='caseID'>200900005</String>\n"              +
                "      </connect>\n"                                              +
                "    </CDAConnect>\n"                                             +
                "  </Call>\n"                                                     +
                "</UMROEnvelope>\n";

            System.out.println("before:\n" + xmlText + "\n\n");
            Document document = parseToDocument(xmlText);
            String converted = domToString(document);
            System.out.println("after:\n" + converted + "\n\n");
        }
        catch (Exception ex) {
            System.out.println("ex: " + ex);
            ex.printStackTrace();
        }

    }

}
