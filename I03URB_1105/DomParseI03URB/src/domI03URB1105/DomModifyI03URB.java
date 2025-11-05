package domI03URB1105;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.File;

public class DomModifyI03URB {

    public static void main(String[] args) {
        try {
            // XML beolvasása
            File xmlFile = new File("I03URBhallgato.xml");
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document doc = builder.parse(xmlFile);

            doc.getDocumentElement().normalize();

            // Módosítandó hallgató keresése
            NodeList hallgatoList = doc.getElementsByTagName("hallgato");

            for (int i = 0; i < hallgatoList.getLength(); i++) {
                Node hallgato = hallgatoList.item(i);

                if (hallgato.getNodeType() == Node.ELEMENT_NODE) {
                    Element elem = (Element) hallgato;

                    if ("01".equals(elem.getAttribute("id"))) {
                        // keresztnev módosítása
                        Node keresztnevNode = elem.getElementsByTagName("keresztnev").item(0);
                        keresztnevNode.setTextContent("Béla");

                        // vezeteknev módosítása
                        Node vezeteknevNode = elem.getElementsByTagName("vezeteknev").item(0);
                        vezeteknevNode.setTextContent("Nagy");
                    }
                }
            }

            // Módosított XML kiírása a konzolra
            TransformerFactory transformerFactory = TransformerFactory.newInstance();
            Transformer transformer = transformerFactory.newTransformer();

            // Szép formázás bekapcsolása
            transformer.setOutputProperty(OutputKeys.INDENT, "yes");
            transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "4");

            DOMSource source = new DOMSource(doc);
            transformer.transform(source, new StreamResult(System.out));

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}