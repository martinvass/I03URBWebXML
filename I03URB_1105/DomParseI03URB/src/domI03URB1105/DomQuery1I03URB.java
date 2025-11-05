package domI03URB1105;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class DomQuery1I03URB {

    public static void main(String[] args) {
        try {
            File inputFile = new File("orarendI03URB.xml");

            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document doc = builder.parse(inputFile);
            doc.getDocumentElement().normalize();

            NodeList oraList = doc.getElementsByTagName("ora");

            // 1.) Kurzusnevek listába
            List<String> kurzusNevek = new ArrayList<>();
            for (int i = 0; i < oraList.getLength(); i++) {
                Element ora = (Element) oraList.item(i);
                Element targy = (Element) ora.getElementsByTagName("targy").item(0);
                String nev = targy.getAttribute("nev");
                kurzusNevek.add(nev);
            }

            System.out.println("Kurzusnév: " + kurzusNevek);
            System.out.println("--------------------------------------------------");

            // 2.) Első példány kiírása strukturáltan (konzol + fájl)
            if (oraList.getLength() > 0) {
                Element elsoOra = (Element) oraList.item(0);
                System.out.println("Első <ora> példány strukturált kiírása:\n");

                printOraElem(elsoOra);

                // Kiírás fájlba
                TransformerFactory transformerFactory = TransformerFactory.newInstance();
                Transformer transformer = transformerFactory.newTransformer();
                transformer.setOutputProperty(OutputKeys.INDENT, "yes");
                transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "4");

                DOMSource source = new DOMSource(elsoOra);
                StreamResult fileResult = new StreamResult(new File("domQuery1.xml"));
                transformer.transform(source, fileResult);
            }

            System.out.println("--------------------------------------------------");

            // 3.) Oktatók neveinek listázása
            List<String> oktatok = new ArrayList<>();
            for (int i = 0; i < oraList.getLength(); i++) {
                Element ora = (Element) oraList.item(i);
                Element oktato = (Element) ora.getElementsByTagName("oktato").item(0);
                String nev = oktato.getAttribute("neve");
                oktatok.add(nev);
            }

            System.out.println("Oktatók nevei: " + oktatok);
            System.out.println("--------------------------------------------------");

            // 4.) Összetett lekérdezés: Kurzus neve + oktató neve + nap
            System.out.println("Összetett lekérdezés (Kurzus - Oktató - Nap):");
            for (int i = 0; i < oraList.getLength(); i++) {
                Element ora = (Element) oraList.item(i);
                String targyNev = ((Element) ora.getElementsByTagName("targy").item(0)).getAttribute("nev");
                String oktatoNev = ((Element) ora.getElementsByTagName("oktato").item(0)).getAttribute("neve");
                String napNev = ((Element) ora.getElementsByTagName("idopont").item(0))
                        .getElementsByTagName("nap").item(0).getAttributes()
                        .getNamedItem("napn").getNodeValue();

                System.out.println(targyNev + " - " + oktatoNev + " - " + napNev);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Segédfüggvény strukturált kiíráshoz
    private static void printOraElem(Element ora) {
        System.out.println("ID: " + ora.getAttribute("id"));
        System.out.println("Típus: " + ora.getAttribute("tipus"));
        Element targy = (Element) ora.getElementsByTagName("targy").item(0);
        System.out.println("Tárgy neve: " + targy.getAttribute("nev"));

        Element idopont = (Element) ora.getElementsByTagName("idopont").item(0);
        Element nap = (Element) idopont.getElementsByTagName("nap").item(0);
        Element tol = (Element) idopont.getElementsByTagName("tol").item(0);
        Element ig = (Element) idopont.getElementsByTagName("ig").item(0);
        System.out.println("Időpont: " + nap.getAttribute("napn") + ", " +
                tol.getAttribute("tolt") + " - " + ig.getAttribute("igt"));

        Element helyszin = (Element) ora.getElementsByTagName("helyszin").item(0);
        System.out.println("Helyszín: " + helyszin.getAttribute("hol"));

        Element oktato = (Element) ora.getElementsByTagName("oktato").item(0);
        System.out.println("Oktató: " + oktato.getAttribute("neve"));

        Element szak = (Element) ora.getElementsByTagName("szak").item(0);
        System.out.println("Szak: " + szak.getAttribute("sznev"));
    }
}