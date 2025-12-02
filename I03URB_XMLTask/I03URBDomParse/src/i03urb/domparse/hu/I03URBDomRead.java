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

/**
 * Adatolvasás DOM segítségével.
 * A teljes I03URB_XML.xml dokumentum feldolgozása
 * és kiírása a konzolra blokk formában, majd mentés fájlba.
 */
public class I03URBDomRead {

    private static final String INPUT_XML = "I03URB_XML.xml";
    private static final String OUTPUT_XML = "I03URB_XML_out.xml";

    public static void main(String[] args) {
        try {
            // 1) XML dokumentum beolvasása DOM-ba
            Document document = parseXml(INPUT_XML);

            System.out.println("Gyökérelem: " + document.getDocumentElement().getNodeName());
            System.out.println("==============================================");

            // 2) Külön-külön kiírási blokkok
            printBeszallitok(document);
            printAlapanyagok(document);
            printTermekek(document);
            printUzletek(document);

            // 3) Dokumentum mentése (itt még módosítás nélkül)
            saveDocument(document, OUTPUT_XML);
            System.out.println("\nEredeti dokumentum elmentve: " + OUTPUT_XML);

        } catch (Exception e) {
            System.err.println("Hiba az XML feldolgozás közben: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * XML fájl beolvasása és DOM Document objektummá alakítása.
     */
    private static Document parseXml(String fileName) throws Exception {
        File xmlFile = new File(fileName);

        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        factory.setIgnoringElementContentWhitespace(true);

        DocumentBuilder builder = factory.newDocumentBuilder();
        Document doc = builder.parse(xmlFile);

        // normalizálás: összefűzi a szomszédos szöveges node-okat stb.
        doc.getDocumentElement().normalize();
        return doc;
    }

    /**
     * DOM dokumentum kiírása egy XML fájlba.
     * Itt még nincs módosítás, csak „átfolyatjuk” a dokumentumot.
     */
    private static void saveDocument(Document document, String fileName) throws Exception {
        TransformerFactory tf = TransformerFactory.newInstance();
        Transformer transformer = tf.newTransformer();

        transformer.setOutputProperty(OutputKeys.INDENT, "yes");

        DOMSource source = new DOMSource(document);
        StreamResult result = new StreamResult(new File(fileName));
        transformer.transform(source, result);
    }

    /**
     * Rövid segédfüggvény: egy adott parent elem alatti
     * első tagName nevű elem szövegét adja vissza.
     */
    private static String text(Element parent, String tagName) {
        Node node = parent.getElementsByTagName(tagName).item(0);
        return node != null ? node.getTextContent() : "";
    }

    /**
     * Beszállítók kiírása „blokk formában”.
     */
    private static void printBeszallitok(Document doc) {
        System.out.println("\n=== BESZÁLLÍTÓK ===");
        NodeList nodes = doc.getElementsByTagName("Beszallito");

        for (int i = 0; i < nodes.getLength(); i++) {
            Element b = (Element) nodes.item(i);

            System.out.println("Beszállító ID: " + b.getAttribute("id"));
            System.out.println("\tNév:        " + text(b, "Nev"));
            System.out.println("\tTelephely:  " + text(b, "Telephely"));
            System.out.println("\tTelefon:    " + text(b, "Telefonszam"));
            System.out.println("\tEmail:      " + text(b, "Email"));
            System.out.println("------------------------------------------");
        }
    }

    /**
     * Alapanyagok kiírása.
     * Itt látszik a komplexebb szerkezet: beágyazott elem (KeszletMennyiseg)
     * és ismétlődő elemek (Allergen).
     */
    private static void printAlapanyagok(Document doc) {
        System.out.println("\n=== ALAPANYAGOK ===");
        NodeList nodes = doc.getElementsByTagName("Alapanyag");

        for (int i = 0; i < nodes.getLength(); i++) {
            Element a = (Element) nodes.item(i);

            System.out.println("Alapanyag ID: " + a.getAttribute("id"));
            System.out.println("\tNév:           " + text(a, "Nev"));
            System.out.println("\tEgységár:      " + text(a, "Egysegar"));
            System.out.println("\tSzavatosság:   " + text(a, "SzavatossagNap") + " nap");

            // Beágyazott elem: <KeszletMennyiseg mertekegyseg="...">érték</KeszletMennyiseg>
            Element keszlet = (Element) a.getElementsByTagName("KeszletMennyiseg").item(0);
            System.out.println("\tKészlet:       " + keszlet.getTextContent()
                    + " " + keszlet.getAttribute("mertekegyseg"));

            // Ismétlődő elem: <Allergen>...</Allergen>
            System.out.print("\tAllergének:   ");
            NodeList allergenek = a.getElementsByTagName("Allergen");
            for (int j = 0; j < allergenek.getLength(); j++) {
                System.out.print(allergenek.item(j).getTextContent());
                if (j < allergenek.getLength() - 1) System.out.print(", ");
            }
            System.out.println();
            System.out.println("------------------------------------------");
        }
    }

    /**
     * Termékek kiírása + a hozzájuk tartozó Felhasznal (N:M kapcsolat) lista.
     */
    private static void printTermekek(Document doc) {
        System.out.println("\n=== TERMÉKEK ===");
        NodeList nodes = doc.getElementsByTagName("Termek");

        for (int i = 0; i < nodes.getLength(); i++) {
            Element t = (Element) nodes.item(i);

            System.out.println("Termék ID: " + t.getAttribute("id"));
            System.out.println("\tNév:       " + text(t, "Nev"));
            System.out.println("\tKategória: " + text(t, "Kategoria"));
            System.out.println("\tÁr:        " + text(t, "Ar"));
            System.out.println("\tMéret:     " + text(t, "Meret"));

            // a termékhez tartozó <Felhasznal> elemek bejárása
            System.out.println("\tFelhasznált alapanyagok:");
            NodeList felhasznalas = t.getElementsByTagName("Felhasznal");
            for (int j = 0; j < felhasznalas.getLength(); j++) {
                Element f = (Element) felhasznalas.item(j);
                Element menny = (Element) f.getElementsByTagName("Mennyiseg").item(0);

                System.out.println("\t\t- AlapanyagRef: " + f.getAttribute("alapanyagRef")
                        + " (" + menny.getTextContent() + " " + menny.getAttribute("mertekegyseg") + ")");
            }
            System.out.println("------------------------------------------");
        }
    }

    /**
     * Üzletek kiírása + beágyazott Dolgozo és KinalatElem elemek.
     */
    private static void printUzletek(Document doc) {
        System.out.println("\n=== ÜZLETEK ===");
        NodeList nodes = doc.getElementsByTagName("Uzlet");

        for (int i = 0; i < nodes.getLength(); i++) {
            Element u = (Element) nodes.item(i);

            System.out.println("Üzlet ID: " + u.getAttribute("id"));
            System.out.println("\tNév:   " + text(u, "Nev"));

            // összetett cím: külön elemekben tárolva
            Element cim = (Element) u.getElementsByTagName("Cim").item(0);
            System.out.println("\tCím:   " + text(cim, "Varos") + ", "
                    + text(cim, "Utca") + " " + text(cim, "Hazszam"));

            System.out.println("\tNyitvatartás: " + text(u, "Nyitvatartas"));
            System.out.println("\tKapacitás:    " + text(u, "KapacitasFo") + " fő");

            // Dolgozók listája az adott üzletben
            System.out.println("\tDolgozók:");
            NodeList dolgozok = u.getElementsByTagName("Dolgozo");
            for (int j = 0; j < dolgozok.getLength(); j++) {
                Element d = (Element) dolgozok.item(j);
                System.out.println("\t\t- " + text(d, "Nev")
                        + " (" + text(d, "Beosztas") + ")");
            }

            // Kínálat lista (N:M kapcsolat Üzlet–Termek)
            System.out.println("\tKínálat:");
            NodeList kinalat = u.getElementsByTagName("KinalatElem");
            for (int j = 0; j < kinalat.getLength(); j++) {
                Element ke = (Element) kinalat.item(j);
                System.out.println("\t\t- TermékRef: " + ke.getAttribute("termekRef")
                        + ", Elérhető: " + text(ke, "ElerhetoMennyiseg")
                        + ", Akciós ár: " + text(ke, "AkciosAr"));
            }

            System.out.println("------------------------------------------");
        }
    }
}