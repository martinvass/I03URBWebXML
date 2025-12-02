package i03urb.domparse.hu;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.File;

public class I03URBDomQuery {

    private static final String INPUT_XML = "I03URB_XML.xml";

    public static void main(String[] args) {
        try {
            Document doc = parseXml(INPUT_XML);

            System.out.println("=== DOM Query ===");
            listDolgozokEgyUzletben(doc, "U001");
            listKinalatEgyUzletben(doc, "U001");
            listTermekekAlapanyagSzerint(doc, "A002");
            listAkciosTermekek(doc);

        } catch (Exception e) {
            System.err.println("Hiba a lekérdezések során: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private static Document parseXml(String fileName) throws Exception {
        File xmlFile = new File(fileName);
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = factory.newDocumentBuilder();
        Document doc = builder.parse(xmlFile);
        doc.getDocumentElement().normalize();

        return doc;
    }

    private static String text(Element parent, String tagName) {
        Node n = parent.getElementsByTagName(tagName).item(0);
        return n != null ? n.getTextContent() : "";
    }

    /**
     * Keresés a Termek listában ID alapján, majd a termék nevének visszaadása.
     */
    private static String findTermekNev(Document doc, String termekId) {
        NodeList termekek = doc.getElementsByTagName("Termek");
        for (int i = 0; i < termekek.getLength(); i++) {
            Element t = (Element) termekek.item(i);
            if (termekId.equals(t.getAttribute("id"))) {
                return text(t, "Nev");
            }
        }
        return "Ismeretlen termék";
    }

    /**
     * 1) Egy üzlet összes dolgozójának listázása.
     * Paraméter: üzlet azonosító (id attribútum).
     */
    private static void listDolgozokEgyUzletben(Document doc, String uzletId) {
        System.out.println("\n1) Dolgozók az " + uzletId + " üzletben:");

        NodeList uzletek = doc.getElementsByTagName("Uzlet");
        for (int i = 0; i < uzletek.getLength(); i++) {
            Element uzlet = (Element) uzletek.item(i);

            // ha nem ez az a bizonyos üzlet, megyünk tovább
            if (!uzletId.equals(uzlet.getAttribute("id"))) {
                continue;
            }

            NodeList dolgozok = uzlet.getElementsByTagName("Dolgozo");
            for (int j = 0; j < dolgozok.getLength(); j++) {
                Element d = (Element) dolgozok.item(j);
                System.out.println("\t- " + text(d, "Nev")
                        + " (" + text(d, "Beosztas") + ")");
            }
        }
    }

    /**
     * 2) Egy üzlet kínálatának lekérdezése.
     * A KinalatElem-eket járjuk be, majd termékID alapján
     * külön kikeressük a termék nevét.
     */
    private static void listKinalatEgyUzletben(Document doc, String uzletId) {
        System.out.println("\n2) Kínálat az " + uzletId + " üzletben:");

        NodeList uzletek = doc.getElementsByTagName("Uzlet");
        for (int i = 0; i < uzletek.getLength(); i++) {
            Element uzlet = (Element) uzletek.item(i);
            if (!uzletId.equals(uzlet.getAttribute("id"))) {
                continue;
            }

            NodeList kinalatElemek = uzlet.getElementsByTagName("KinalatElem");
            for (int j = 0; j < kinalatElemek.getLength(); j++) {
                Element elem = (Element) kinalatElemek.item(j);

                String termekRef = elem.getAttribute("termekRef");
                String termekNev = findTermekNev(doc, termekRef);

                System.out.println("\t- " + termekNev + " (ID: " + termekRef + ")"
                        + ", elérhető: " + text(elem, "ElerhetoMennyiseg")
                        + ", akciós ár: " + text(elem, "AkciosAr"));
            }
        }
    }

    /**
     * 3) Mely termékek használnak egy adott alapanyagot?
     * A Termek/Felhasznal elemeket járjuk be és vizsgáljuk az alapanyagRef attribútumot.
     */
    private static void listTermekekAlapanyagSzerint(Document doc, String alapanyagId) {
        System.out.println("\n3) Termékek, amelyek használják ezt az alapanyagot: " + alapanyagId);

        NodeList termekek = doc.getElementsByTagName("Termek");
        for (int i = 0; i < termekek.getLength(); i++) {
            Element termek = (Element) termekek.item(i);
            NodeList felhasznalasok = termek.getElementsByTagName("Felhasznal");

            for (int j = 0; j < felhasznalasok.getLength(); j++) {
                Element f = (Element) felhasznalasok.item(j);

                // ha az adott termék bármelyik Felhasznal elemében szerepel az alapanyagRef,
                // már kiírhatjuk a terméket, és kilépünk a belső ciklusból
                if (alapanyagId.equals(f.getAttribute("alapanyagRef"))) {
                    System.out.println("\t- " + text(termek, "Nev")
                            + " (ID: " + termek.getAttribute("id") + ")");
                    break;
                }
            }
        }
    }

    /**
     * 4) Akciós termékek lekérdezése az összes üzletből.
     * Akciós terméknek az számít, ahol az AkciosAr > 0.
     */
    private static void listAkciosTermekek(Document doc) {
        System.out.println("\n4) Akciós termékek (ahol AkciosAr > 0):");

        NodeList uzletek = doc.getElementsByTagName("Uzlet");
        for (int i = 0; i < uzletek.getLength(); i++) {
            Element uzlet = (Element) uzletek.item(i);
            String uzletNev = text(uzlet, "Nev");

            NodeList kinalatElemek = uzlet.getElementsByTagName("KinalatElem");
            for (int j = 0; j < kinalatElemek.getLength(); j++) {
                Element elem = (Element) kinalatElemek.item(j);

                int akciosAr = Integer.parseInt(text(elem, "AkciosAr"));
                if (akciosAr <= 0) {
                    continue;   // nem akciós, lépünk tovább
                }

                String termekRef = elem.getAttribute("termekRef");
                String termekNev = findTermekNev(doc, termekRef);

                System.out.println("\t- " + termekNev + " (ID: " + termekRef + ")"
                        + ", üzlet: " + uzletNev + ", akciós ár: " + akciosAr);
            }
        }
    }
}