package domI03URB1029;

import org.w3c.dom.*;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.File;

public class DomWriteI03URB {

    public static void main(String[] args) {
        try {
            File inputFile = new File("I03URBhallgato.xml");

            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            factory.setNamespaceAware(true);
            factory.setIgnoringElementContentWhitespace(true); // whitespace-ek ignorálása
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document doc = builder.parse(inputFile);
            doc.getDocumentElement().normalize();

            removeWhitespaceNodes(doc.getDocumentElement());

            TransformerFactory transformerFactory = TransformerFactory.newInstance();
            Transformer transformer = transformerFactory.newTransformer();

            transformer.setOutputProperty(OutputKeys.INDENT, "yes");
            transformer.setOutputProperty(OutputKeys.METHOD, "xml");
            transformer.setOutputProperty(OutputKeys.ENCODING, "UTF-8");
            transformer.setOutputProperty(OutputKeys.STANDALONE, "no");
            transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "4");

            DOMSource source = new DOMSource(doc);
            StreamResult result = new StreamResult(new File("hallgato1I30URB.xml"));
            StreamResult console = new StreamResult(System.out);

            transformer.transform(source, result);
            transformer.transform(source, console);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void removeWhitespaceNodes(Element element) {
        NodeList children = element.getChildNodes();
        for (int i = children.getLength() - 1; i >= 0; i--) {
            Node child = children.item(i);
            if (child.getNodeType() == Node.TEXT_NODE && child.getTextContent().trim().isEmpty()) {
                element.removeChild(child);
            } else if (child.getNodeType() == Node.ELEMENT_NODE) {
                removeWhitespaceNodes((Element) child);
            }
        }
    }
}