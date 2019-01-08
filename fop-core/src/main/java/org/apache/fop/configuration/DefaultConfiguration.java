package org.apache.fop.configuration;

import org.w3c.dom.*;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class DefaultConfiguration implements Configuration {

    static final DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();

    static {
        dbf.setNamespaceAware(false);
        dbf.setValidating(false);
        dbf.setIgnoringComments(true);
        dbf.setIgnoringElementContentWhitespace(true);
        dbf.setExpandEntityReferences(true);
    }

    /**
     * @deprecated For debug only.
     */
    public static String toString(Document document) {
        try {
            Transformer transformer = TransformerFactory.newInstance().newTransformer();
            transformer.setOutputProperty(OutputKeys.INDENT, "yes");
            transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "2");
            //initialize StreamResult with File object to save to file
            StreamResult result = new StreamResult(new StringWriter());
            DOMSource source = new DOMSource(document);
            transformer.transform(source, result);
            return result.getWriter().toString();
        } catch (TransformerException e) {
            throw new IllegalStateException(e);
        }
    }

    private Element element;

    public DefaultConfiguration(String key) {
        DocumentBuilder builder = null;
        try {
            builder = dbf.newDocumentBuilder();
        } catch (ParserConfigurationException e) {
            e.printStackTrace();
            throw new IllegalStateException(e);
        }
        Document doc = builder.newDocument();
        // create the root element node
        element = doc.createElement(key);
        doc.appendChild(element);
    }

    DefaultConfiguration(Element element) {
        this.element = element;
    }

    Element getElement() {
        return element;
    }

    public void addChild(DefaultConfiguration configuration) {
        Element node = (Element) element.getOwnerDocument().importNode(configuration.getElement(), true);
        element.appendChild(node);
    }

    String _getValue() {
        String result = element.getTextContent();
        if (result == null) {
            result = "";
        }
        return result;
    }

    @Override
    public Configuration getChild(String key) {
        NodeList nl = element.getElementsByTagName(key);
        for (int i = 0; i < nl.getLength(); ++i) {
            Node n = nl.item(i);
            if (n.getNodeName().equals(key)) {
                return new DefaultConfiguration((Element) n);
            }
        }
        return NullConfiguration.INSTANCE;
    }

    @Override
    public Configuration getChild(String key, boolean required) {
        Configuration result = getChild(key);
        if (!required && result == NullConfiguration.INSTANCE) {
            return null;
        }
        if (required && (result == null || result == NullConfiguration.INSTANCE)) {
            // throw new IllegalStateException("No child '" + key + "'");
            return NullConfiguration.INSTANCE;
        }
        return result;
    }

    @Override
    public Configuration[] getChildren(String key) {
        NodeList nl = element.getElementsByTagName(key);
        Configuration[] result = new Configuration[nl.getLength()];
        for (int i = 0; i < nl.getLength(); ++i) {
            Node n = nl.item(i);
            result[i] = new DefaultConfiguration((Element) n);
        }
        return result;
    }

    @Override
    public String[] getAttributeNames() {
        NamedNodeMap nnm = element.getAttributes();
        String[] result = new String[nnm.getLength()];
        for (int i = 0; i < nnm.getLength(); ++i) {
            Node n = nnm.item(i);
            result[i] = n.getNodeName();
        }
        return result;
    }

    @Override
    public String getAttribute(String key) {
        String result = element.getAttribute(key);
        if ("".equals(result)) {
            result = null;
        }
        return result;
    }

    @Override
    public String getAttribute(String key, String defaultValue) {
        String result = getAttribute(key);
        if (result == null || "".equals(result)) {
            result = defaultValue;
        }
        return result;
    }

    @Override
    public boolean getAttributeAsBoolean(String key, boolean defaultValue) {
        return Boolean.parseBoolean(getAttribute(key));
    }

    @Override
    public float getAttributeAsFloat(String key) throws ConfigurationException {
        return Float.parseFloat(getAttribute(key));
    }

    @Override
    public float getAttributeAsFloat(String key, float defaultValue) {
        String result = getAttribute(key);
        if (result == null || "".equals(result)) {
            return defaultValue;
        }
        return Float.parseFloat(result);
    }

    @Override
    public int getAttributeAsInteger(String key, int defaultValue) {
        String result = getAttribute(key);
        if (result == null || "".equals(result)) {
            return defaultValue;
        }
        return Integer.parseInt(result);
    }

    @Override
    public String getValue() throws ConfigurationException {
        String result = _getValue();
        if (result == null || "".equals(result)) {
            throw new ConfigurationException("No value in " + element.getNodeName());
        }
        return result;
    }

    @Override
    public String getValue(String defaultValue) {
        /*
        if (defaultValue == null) {
            defaultValue = "";
        }
         */
        String result = _getValue();
        if (result == null || "".equals(result)) {
            result = defaultValue;
        }
        return result;
    }

    @Override
    public boolean getValueAsBoolean() throws ConfigurationException {
        return Boolean.parseBoolean(_getValue());
    }

    @Override
    public boolean getValueAsBoolean(boolean defaultValue) {
        String result = _getValue();
        if (result == null || "".equals(result)) {
            return defaultValue;
        }
        return Boolean.parseBoolean(result);
    }

    @Override
    public int getValueAsInteger() throws ConfigurationException {
        try {
            return Integer.parseInt(_getValue());
        } catch (NumberFormatException e) {
            throw new ConfigurationException("Not an integer", e);
        }
    }

    @Override
    public int getValueAsInteger(int defaultValue) {
        String result = _getValue();
        if (result == null || "".equals(result)) {
            return defaultValue;
        }
        return Integer.parseInt(result);
    }

    @Override
    public float getValueAsFloat() throws ConfigurationException {
        try {
            return Float.parseFloat(_getValue());
        } catch (NumberFormatException e) {
            throw new ConfigurationException("Not a float", e);
        }
    }

    @Override
    public float getValueAsFloat(float defaultValue) {
        String result = _getValue();
        if (result == null || "".equals(result)) {
            return defaultValue;
        }
        return Float.parseFloat(_getValue());
    }

    @Override
    public String getLocation() {
        List<String> path = new ArrayList<>();
        for (Node el = element; el != null; el = el.getParentNode()) {
            if (el instanceof Element) {
                path.add(((Element) el).getTagName());
            }
        }
        Collections.reverse(path);
        return String.join("/", path);
    }
}
