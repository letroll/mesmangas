package fr.letroll.mesmangas.site;

import java.util.ArrayList;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import fr.letroll.framework.Notification;
import fr.letroll.framework.Web;
import fr.letroll.mesmangas.parcelle.Miroir;

public class Dbps implements Miroir{
    private String nomDuSite = "Dbps";
    private String adresseDuSite = "http://dbps.free.fr/";
    // mirrorIcon : "img/bleachexile.png",
    private Document doc;

    public Dbps() {
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
            doc = Jsoup.connect(adresseDuSite).timeout(4000).get();
            Elements titles = doc.select("select[name=manga] > option");
            int i = 0;
            for (org.jsoup.nodes.Element element : titles) {
                if (i > 0) {
                    array_manga.add(element.text());
                }
                i++;
            }
        }catch (Exception e) {
            e.printStackTrace();
        }
        return array_manga;
    }

    // =====================================================
    // retourne la liste des adresses des images
    // =====================================================
    public ArrayList<String> getListImages(String curUrl) {
        // Log.e("getListImages dbpss",curUrl);
        ArrayList<String> res = new ArrayList<String>();
        try {
            String source = Web.GetHTML(curUrl, null);
            doc = Jsoup.parse(source);
            Element select = doc.select("select[name=page]").get(0);
            Elements pages = select.select("option");

            // Notification.log("getlistimage", ""+pages.size());
            res.add("" + pages.size());
            for (Element element : pages) {
                String idx = element.attr("value");
                String url = curUrl + "/" + idx;
                // Notification.log("getlistimage url", url);
                res.add(url);
            }
        } catch (Exception e) {
            res.add("");
            e.printStackTrace();
        }
        return res;
    }

    // =====================================================
    // retourne le nom du manga encode
    // =====================================================
    public String getNomEncode(String nomManga) {
        String nomEncode;
        nomEncode = nomManga;
        nomEncode = nomEncode.replaceAll(" ", "_");
        nomEncode = nomEncode.replaceAll("~", "");
        nomEncode = nomEncode.replaceAll("%", "");
//        nomEncode = nomEncode.replaceAll("[!#$%&'() ~*+,\"\\-:;\\[\\]<=>?]", "");
        // Log.e("nom modifie",nomEncode);
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
        String adr = adresseDuSite + nomManga;
        // Log.e("adresse creee pour dbps",adr);
        try {
            String source = Web.GetHTML(adr, null);
            doc = Jsoup.parse(source);
            Elements select = doc.select("select[name=chapter]").eq(0);
            Elements chapters = select.select("option");
            for (Element element : chapters) {
                lesChapitres.add(element.text());
            }
        } catch (Exception e) {
            lesChapitres.add("");
            e.printStackTrace();
        }
        return lesChapitres;
    }

    // ===========================================================
    // retourne le chemin de l'image a partir de l'adresse donnae
    // ===========================================================
    public String getImageFromPageAndWrite(String chemin) {
        // Notification.log("chemin", chemin);
        String tmp = "";
        try {
            String source = Web.GetHTML(chemin, null);
            doc = Jsoup.parse(source);
            Element monImage = doc.select("img[class=picture]").first();
            tmp = monImage.attr("src");
            Notification.log("src init", tmp);
            if (tmp != "") {
                tmp = "http://dbps.free.fr/" + tmp;
                tmp = tmp.replaceAll(" ", "%20");
                return tmp;
            } else {
                return "";
            }
        } catch (Exception e) {
            e.printStackTrace();
            return "";
        }
    }

    // ===========================================================
    // retourne l'extension de l'image d'une adresse donn�e
    // ===========================================================
//    public String getImageExt(String chemin) {
//        String ext ="";
//        try {
//            doc = Jsoup.connect(chemin).timeout(3000).get();
//            Element monImage = doc.select("img[class=picture]").get(0);
//            String src = monImage.attr("src");
//            ext = src.substring(src.length() - 3, src.length());
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//
//        return ext;
//    }

    // ===========================================================
    // retourne l'adresse ou chercher les images
    // ===========================================================
    public String getImageAdr(String titre, String adresse, String chapitre) {
        String tmp = getNomEncode(titre);
        adresse = adresse.substring(0, adresse.length() - tmp.length());
        adresse = adresse + tmp + "/" + chapitre;
        return adresse;
    }

     public String getNomDuSite() {
        return nomDuSite;
    }
}