package domI03URB1105;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.File;

public class DomQueryI03URB {

    public static void main(String[] args) {
        try {
            File inputFile = new File("I03URBhallgato.xml");

            DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();

            Document doc = dBuilder.parse(inputFile);
            doc.getDocumentElement().normalize();

            Element root = doc.getDocumentElement();
            System.out.println("Gyökér elem: " + root.getNodeName());
            System.out.println("----------------------------");

            NodeList children = root.getChildNodes();

            for (int i = 0; i < children.getLength(); i++) {
                Node node = children.item(i);

                if (node.getNodeType() == Node.ELEMENT_NODE) {
                    Element elem = (Element) node;

                    NodeList vezeteknevList = elem.getElementsByTagName("vezeteknev");
                    NodeList foglalkozasList = elem.getElementsByTagName("foglalkozas");
                    if (vezeteknevList.getLength() > 0 || foglalkozasList.getLength() > 0) {
                        String vezeteknev = vezeteknevList.item(0).getTextContent();
                        String foglalkozas = foglalkozasList.item(0).getTextContent();
                        System.out.println("Aktuális elem: " + elem.getTagName());
                        System.out.println("vezeteknev: " + vezeteknev);
                        System.out.println("foglalkozas: " + foglalkozas);
                        System.out.println();
                    }
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}