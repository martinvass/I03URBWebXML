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

public class DomModify1I03URB {

    public static void main(String[] args) {
        try {
            // XML beolvasása
            File xmlFile = new File("orarendI03URB.xml");
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document doc = builder.parse(xmlFile);
            //doc.getDocumentElement().normalize();

            // Összes <ora> bejárása
            NodeList oraList = doc.getElementsByTagName("ora");

            for (int i = 0; i < oraList.getLength(); i++) {
                Element ora = (Element) oraList.item(i);

                // 1. Új <oraado> elem hozzáadása az elsőhez
                if (i == 0) {
                    Element oraado = doc.createElement("oraado");
                    oraado.setTextContent("Vass Martin");
                    ora.appendChild(oraado);
                }

                // 2. tipus="gyakorlat" --> tipus="eloadas"
                if (ora.hasAttribute("tipus") && ora.getAttribute("tipus").equalsIgnoreCase("gyakorlat")) {
                    ora.setAttribute("tipus", "eloadas");
                }
            }

            // Transformer beállítása kiíráshoz
            TransformerFactory transformerFactory = TransformerFactory.newInstance();
            Transformer transformer = transformerFactory.newTransformer();

            // Formázott kimenet
            transformer.setOutputProperty(OutputKeys.INDENT, "yes");
            transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "4");

            DOMSource source = new DOMSource(doc);

            // Kiírás konzolra
            StreamResult consoleOut = new StreamResult(System.out);
            transformer.transform(source, consoleOut);

            // Kiírás fájlba
            StreamResult fileOut = new StreamResult(new File("orarendModify1I03URB.xml"));
            transformer.transform(source, fileOut);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
