package i03urb.domparse.hu;

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

public class I03URBDomModify {

    private static final String INPUT_XML = "I03URB_XML.xml";
    private static final String OUTPUT_XML = "I03URB_XML_modified.xml";

    public static void main(String[] args) {
        try {
            Document doc = parseXml(INPUT_XML);

            System.out.println("=== DOM Modify ===\n");

            // 1) T001 termék árának módosítása
            modifyTermekAr(doc, "T001", 999);

            // 2) Új dolgozó felvétele U001 üzlethez
            addNewDolgozo(doc, "U001");

            // 3) A001 alapanyag készletének növelése +500
            increaseAlapanyagKeszlet(doc, "A001", 500);

            // 4) U001 üzletben T002 termék akciós árának beállítása
            setAkciosAr(doc, "U001", "T002", 690);

            // Módosított dokumentum elmentése
            saveDocument(doc, OUTPUT_XML);
            System.out.println("\nMódosított dokumentum elmentve: " + OUTPUT_XML);

        } catch (Exception e) {
            System.err.println("Hiba az adatmódosítás során: " + e.getMessage());
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

    private static void saveDocument(Document doc, String fileName) throws Exception {
        TransformerFactory tf = TransformerFactory.newInstance();
        Transformer transformer = tf.newTransformer();
        transformer.setOutputProperty(OutputKeys.INDENT, "yes");

        transformer.transform(new DOMSource(doc),
                new StreamResult(new File(fileName)));
    }

    private static String text(Element parent, String tagName) {
        Node n = parent.getElementsByTagName(tagName).item(0);
        return n != null ? n.getTextContent() : "";
    }

    /**
     * 1) Termék árának módosítása.
     * Keresés a Termek listában ID szerint, majd az <Ar> elem átírása.
     */
    private static void modifyTermekAr(Document doc, String termekId, int ujAr) {
        NodeList termekek = doc.getElementsByTagName("Termek");
        for (int i = 0; i < termekek.getLength(); i++) {
            Element t = (Element) termekek.item(i);
            if (!termekId.equals(t.getAttribute("id"))) {
                continue;
            }

            t.getElementsByTagName("Ar").item(0)
                    .setTextContent(Integer.toString(ujAr));

            System.out.println("1) Termék ár módosítva: " + termekId + " → " + ujAr);
            return;
        }
        System.out.println("1) Nem található termék: " + termekId);
    }

    /**
     * 2) Új dolgozó hozzáadása a megadott üzlethez.
     * Az üzlet <Dolgozok> eleméhez fűzünk hozzá egy új <Dolgozo> gyermeket.
     */
    private static void addNewDolgozo(Document doc, String uzletId) {
        NodeList uzletek = doc.getElementsByTagName("Uzlet");
        for (int i = 0; i < uzletek.getLength(); i++) {
            Element uzlet = (Element) uzletek.item(i);
            if (!uzletId.equals(uzlet.getAttribute("id"))) {
                continue;
            }

            // meglévő <Dolgozok> szekció kikeresése
            Element dolgozokElem = (Element) uzlet.getElementsByTagName("Dolgozok").item(0);

            // új dolgozó összeállítása külön segédfüggvénnyel
            Element ujDolgozo = createDolgozoElement(doc,
                    "D999", "Teszt Elek", "BARISTA", "310000", "2025-01-01");

            // új dolgozó hozzáfűzése a listához
            dolgozokElem.appendChild(ujDolgozo);
            System.out.println("2) Új dolgozó hozzáadva: D999 - Teszt Elek (" + uzletId + ")");
            return;
        }
        System.out.println("2) Nem található üzlet: " + uzletId);
    }

    /**
     * Segédfüggvény egy új <Dolgozo> elem felépítésére.
     * Így a szerkezet egy helyen van definiálva, könnyebb módosítani/jegyzőkönyvben bemutatni.
     */
    private static Element createDolgozoElement(Document doc,
                                                String id, String nev, String beosztas,
                                                String fizetes, String belepesDatum) {

        Element dolgozo = doc.createElement("Dolgozo");
        dolgozo.setAttribute("id", id);

        Element nevElem = doc.createElement("Nev");
        nevElem.appendChild(doc.createTextNode(nev));
        dolgozo.appendChild(nevElem);

        Element beosztasElem = doc.createElement("Beosztas");
        beosztasElem.appendChild(doc.createTextNode(beosztas));
        dolgozo.appendChild(beosztasElem);

        Element fizetesElem = doc.createElement("Fizetes");
        fizetesElem.appendChild(doc.createTextNode(fizetes));
        dolgozo.appendChild(fizetesElem);

        Element datumElem = doc.createElement("BelepesDatum");
        datumElem.appendChild(doc.createTextNode(belepesDatum));
        dolgozo.appendChild(datumElem);

        return dolgozo;
    }

    /**
     * 3) Egy adott alapanyag készletének növelése.
     * Az <Alapanyag> alatt található <KeszletMennyiseg> elem értékét növeljük meg delta-val.
     */
    private static void increaseAlapanyagKeszlet(Document doc, String alapanyagId, int delta) {
        NodeList alapanyagok = doc.getElementsByTagName("Alapanyag");
        for (int i = 0; i < alapanyagok.getLength(); i++) {
            Element a = (Element) alapanyagok.item(i);
            if (!alapanyagId.equals(a.getAttribute("id"))) {
                continue;
            }

            Element keszletElem = (Element) a.getElementsByTagName("KeszletMennyiseg").item(0);
            int jelenlegi = Integer.parseInt(keszletElem.getTextContent());
            int ujErtek = jelenlegi + delta;

            keszletElem.setTextContent(Integer.toString(ujErtek));
            System.out.println("3) Készlet növelve: " + alapanyagId
                    + " (" + jelenlegi + " → " + ujErtek + ")");
            return;
        }
        System.out.println("3) Nem található alapanyag: " + alapanyagId);
    }

    /**
     * 4) Egy megadott üzletben egy megadott termék akciós árának beállítása.
     * Az <Uzlet>/<Kinalat>/<KinalatElem> struktúrát járjuk be.
     */
    private static void setAkciosAr(Document doc, String uzletId, String termekId, int akciosAr) {
        NodeList uzletek = doc.getElementsByTagName("Uzlet");
        for (int i = 0; i < uzletek.getLength(); i++) {
            Element uzlet = (Element) uzletek.item(i);
            if (!uzletId.equals(uzlet.getAttribute("id"))) {
                continue;
            }

            NodeList kinalatElemek = uzlet.getElementsByTagName("KinalatElem");
            for (int j = 0; j < kinalatElemek.getLength(); j++) {
                Element elem = (Element) kinalatElemek.item(j);
                if (!termekId.equals(elem.getAttribute("termekRef"))) {
                    continue;
                }

                elem.getElementsByTagName("AkciosAr").item(0)
                        .setTextContent(Integer.toString(akciosAr));

                System.out.println("4) Akciós ár módosítva: üzlet=" + uzletId
                        + ", termék=" + termekId + " → " + akciosAr);
                return;
            }
        }
        System.out.println("4) Nem található ilyen üzlet/termék kombináció: "
                + uzletId + " / " + termekId);
    }
}