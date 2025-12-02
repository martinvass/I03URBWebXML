package com.github.martinvass;

import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.io.FileInputStream;
import java.io.IOException;

public class JSONReadI03URB {

    public static void main(String[] args) {
        String fileName = "orarendI03URB.json";

        try (FileInputStream fis = new FileInputStream(fileName)) {

            // JSON beolvasása
            JSONTokener tokener = new JSONTokener(fis);
            JSONObject root = new JSONObject(tokener);

            // A saját neptun kódodnak megfelelő kulcs!
            JSONObject orarend = root.getJSONObject("I03URB_orarend");

            JSONArray orak = orarend.getJSONArray("ora");

            // Végigmegyünk az órákon és blokk-formátumban kiírjuk
            for (int i = 0; i < orak.length(); i++) {
                JSONObject ora = orak.getJSONObject(i);

                System.out.println("===== Óra " + (i + 1) + " =====");

                System.out.println("targy: " + ora.getString("targy"));

                JSONObject idopont = ora.getJSONObject("idopont");
                System.out.println("nap: " + idopont.getString("nap"));
                System.out.println("tol: " + idopont.getInt("tol"));
                System.out.println("ig: " + idopont.getInt("ig"));

                System.out.println("helyszin: " + ora.getString("helyszin"));
                System.out.println("oktato: " + ora.getString("oktato"));
                System.out.println("szak: " + ora.getString("szak"));
                System.out.println(); // üres sor a blokkok között
            }

        } catch (IOException e) {
            System.err.println("Hiba a fájl olvasásakor: " + e.getMessage());
        } catch (Exception e) {
            System.err.println("Hiba a JSON feldolgozásakor: " + e.getMessage());
        }
    }
}