package domI03URB1029;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.File;
import java.io.IOException;

public class DomReadI03URB {

    public static void main(String argv[]) throws SAXException, IOException, ParserConfigurationException {
        File xmlFile = new File("I03URBhallgato.xml");

        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder dBuilder = factory.newDocumentBuilder();

        Document doc = dBuilder.parse(xmlFile);

        doc.getDocumentElement().normalize();

        System.out.println("Gyökér elem: " + doc.getDocumentElement().getNodeName());

        NodeList nList = doc.getElementsByTagName("hallgato");

        for (int i = 0; i < nList.getLength(); i++) {

            Node nNode = nList.item(i);
            System.out.println("\nAktuális elem: " + nNode.getNodeName());

            if(nNode.getNodeType() == Node.ELEMENT_NODE) {

                Element elem = (Element) nNode;
                String uid = elem.getAttribute("id");

                Node node1 = elem.getElementsByTagName("keresztnev").item(0);
                String fname = node1.getTextContent();

                Node node2 = elem.getElementsByTagName("vezeteknev").item(0);
                String lname = node2.getTextContent();

                Node node3 = elem.getElementsByTagName("foglalkozas").item(0);
                String prof = node3.getTextContent();

                System.out.println("Hallgató id: " + uid);
                System.out.println("Keresztnév: " + fname);
                System.out.println("Vezetéknév: " + lname);
                System.out.println("Foglalkozás: " + prof);
            }
        }
    }
}