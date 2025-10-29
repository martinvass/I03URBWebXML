package domI03URB1029;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.File;

public class DomReadI03URB1 {

    public static void main(String[] args) {
        try {
            File xmlFile = new File("orarendI03URB.xml");
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document doc = builder.parse(xmlFile);

            doc.getDocumentElement().normalize();

            System.out.println("Gyökérelem: " + doc.getDocumentElement().getNodeName());
            System.out.println("=======================================");

            NodeList oraList = doc.getElementsByTagName("ora");

            for (int i = 0; i < oraList.getLength(); i++) {
                Node oraNode = oraList.item(i);
                if (oraNode.getNodeType() == Node.ELEMENT_NODE) {
                    Element oraElem = (Element) oraNode;
                    System.out.println("Óra ID: " + oraElem.getAttribute("id"));
                    System.out.println("Típus: " + oraElem.getAttribute("tipus"));

                    Element targy = (Element) oraElem.getElementsByTagName("targy").item(0);
                    System.out.println("  Tárgy neve: " + targy.getAttribute("nev"));

                    Element idopont = (Element) oraElem.getElementsByTagName("idopont").item(0);
                    Element nap = (Element) idopont.getElementsByTagName("nap").item(0);
                    Element tol = (Element) idopont.getElementsByTagName("tol").item(0);
                    Element ig = (Element) idopont.getElementsByTagName("ig").item(0);
                    System.out.println("  Időpont:");
                    System.out.println("    Nap: " + nap.getAttribute("napn"));
                    System.out.println("    Tól: " + tol.getAttribute("tolt"));
                    System.out.println("    Ig: " + ig.getAttribute("igt"));

                    Element helyszin = (Element) oraElem.getElementsByTagName("helyszin").item(0);
                    System.out.println("  Helyszín: " + helyszin.getAttribute("hol"));

                    Element oktato = (Element) oraElem.getElementsByTagName("oktato").item(0);
                    System.out.println("  Oktató: " + oktato.getAttribute("neve"));

                    Element szak = (Element) oraElem.getElementsByTagName("szak").item(0);
                    System.out.println("  Szak: " + szak.getAttribute("sznev"));

                    System.out.println("---------------------------------------");
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}