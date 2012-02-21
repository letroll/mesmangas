package fr.letroll.mesmangas.site;

import java.util.ArrayList;
import java.util.Collections;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import fr.letroll.framework.Web;
import fr.letroll.mesmangas.parcelle.Miroir;

public class Mangafox implements Miroir {
    private String nomDuSite = "Mangafox";
    private String adresseDuSite = "http://www.mangafox.com/directory/";
    private String adresseDesMangas = "http://www.mangafox.com/manga/";
    private String adresseSeul = "http://www.mangafox.com";
    private Document doc;

    // private String Icon="bleachexile.ico";

    public Mangafox() {
    }

    // =====================================================
    // retourne vrais si l'adresse correspond au site
    // =====================================================
    public Boolean isMe(String url) {
        return url.contains(nomDuSite.toLowerCase());
    }

    // =====================================================
    // retourne la liste des mangas disponible sur le site
    // =====================================================
    public ArrayList<String> getMangaList() {
        ArrayList<String> array_manga = new ArrayList<String>();
        try {
            String[] alpha = { "9/", "a/", "b/", "c/", "d/", "e/", "f/", "g/", "h/", "i/", "j/", "k/", "l/", "m/", "n/", "o/", "p/", "q/", "r/",
                    "s/", "t/", "u/", "v/", "w/", "x/", "y/", "z/" };
            for (String string : alpha) {
                doc = Jsoup.connect(adresseDuSite + string).timeout(4000).get();
                Element table = doc.select("table[id=listing]").first();
                Elements liens = table.getElementsByTag("a");
                Elements titles = liens.select("[class^=manga]");
                // Notification.LOG("nbr titre", ""+titles.size());
                int i = 0;
                for (org.jsoup.nodes.Element element : titles) {
                    if (i > 0) {
                        array_manga.add(element.text());
                    }
                    i++;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return array_manga;
    }

    // =====================================================
    // retourne le nom du manga encode
    // =====================================================
    public String getNomEncode(String nomManga) {
        String nomEncode;
        nomManga = nomManga.toLowerCase();
        nomEncode = nomManga;

        nomEncode = nomEncode.replaceAll(" - ", "_");
        nomEncode = nomEncode.replaceAll("[ '=:-]", "_");
        nomEncode = nomEncode.replaceAll("\\/{1,}", "_");
        nomEncode = nomEncode.replaceAll("[!#$%&()~*+,\"\\;\\[\\]<>?]", "");

        // Notification.LOG("nom modifie",nomEncode);
        return nomEncode;
    }

    // =====================================================
    // retourne le nom du manga encode hors adresse
    // =====================================================
    public String getNomEncode2(String nomManga) {
        nomManga = getNomEncode(nomManga);
        nomManga = nomManga.replaceAll("\\.", "");
        return nomManga;
    }

    // =====================================================
    // retourne la liste des chapitres d'un manga
    // =====================================================
    public ArrayList<String> getChapitre(String nomManga) {
        ArrayList<String> lesChapitres = new ArrayList<String>();
        String adr = adresseDesMangas + nomManga + "/";
        // Notification.LOG("adresse creee pour mangafox", adr);
        try {
            String source = Web.GetHTML(adr, null);
            // Notification.LOG("source", source);
            doc = Jsoup.parse(source);
            Elements chapters = doc.select("a.ch");
            for (Element element : chapters) {
                String chap = element.text();
                lesChapitres.add(chap);
            }
        } catch (Exception e) {
            lesChapitres.add("");
            e.printStackTrace();
        }
        Collections.reverse(lesChapitres);
        return lesChapitres;
    }

    // =====================================================
    // retourne la liste des adresses des images
    // =====================================================
    public ArrayList<String> getListImages(String curUrl) {
        // Notification.LOG("getListImages mangafox", curUrl);
        ArrayList<String> res = new ArrayList<String>();

        try {
            doc = Jsoup.connect(curUrl).timeout(3000).get();
            // Notification.LOG("getlistimage",
            // "cururl:"+curUrl+" cururl2"+curUrl2+" baseurl:"+baseUrl);
            Element select = doc.select("select.middle").get(0);
            Elements pages = select.select("option");

            // Notification.LOG("getlistimage", "" + pages.size());
            res.add("" + pages.size());
            curUrl = curUrl.substring(0, curUrl.lastIndexOf("/"));
            // Notification.LOG("getlistimage curl", curUrl);
            for (Element element : pages) {
                String idx = element.attr("value");
                String url = curUrl + "/" + idx + ".html";
                // Notification.LOG("getlistimage url", url);
                res.add(url);
            }

        } catch (Exception e) {
            e.printStackTrace();
            res.add("");
        }

        return res;
    }

    // ===========================================================
    // retourne le chemin de l'image a partir de l'adresse donnee
    // ===========================================================
    public String getImageFromPageAndWrite(String chemin) {
        String tmp = "";
        // Notification.LOG("image chemin:", chemin);
        try {
            String source = Web.GetHTML(chemin, null);
            doc = Jsoup.parse(source);
            Elements monImage = doc.select("a>img#image");
            if (monImage.size() != 0) {
                tmp = monImage.attr("src");
                // Notification.LOG("image tmp:", tmp);
            } else {
                tmp = "";
                // Notification.LOG("derniere image :", "derniere image ");
            }
        } catch (Exception e) {
            tmp = "";
            e.printStackTrace();
        }
        return tmp;
    }

    // ===========================================================
    // retourne l'extension de l'image d'une adresse donnee
    // ===========================================================
//    public String getImageExt(String chemin) {
//        String ext;
//        try {
//            doc = Jsoup.connect(chemin).timeout(3000).get();
//            Element monImage = doc.select("img#image").get(0);
//            String src = monImage.attr("src");
//            ext = src.substring(src.length() - 3, src.length());
//        } catch (Exception e) {
//            e.printStackTrace();
//            ext = "";
//        }
//
//        return ext;
//    }

    // ===========================================================
    // retourne l'adresse ou chercher les images
    // ===========================================================
    public String getImageAdr(String titre, String adresse, String chapitre) {
        // Notification.LOG("getImageAdr", "getImageAdr");
        // Notification.LOG("adresse :", adresse);
        adresse = adresseDesMangas + titre;
        // Notification.LOG("adresse :", adresse);
        try {
            doc = Jsoup.connect(adresse).timeout(3000).get();
            Elements lesChapitre = doc.select("a.ch");
            for (Element element : lesChapitre) {
                // Notification.LOG("leschapitres :", element.text());
                if (element.text().contains(chapitre)) {
                    // Notification.LOG("href :", element.attr("href"));
                    adresse = adresseSeul + "/" + element.attr("href");
                    // Notification.LOG("adresse :", adresse);
                    break;
                }
            }

            // Notification.LOG("adresse :", adresse);
        } catch (Exception e) {
            e.printStackTrace();
            adresse = "";
        }
        return adresse;
    }

    @Override public String getNomDuSite() {
        return nomDuSite;
    }
}
