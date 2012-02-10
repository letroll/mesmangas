package fr.letroll.mesmangas.site;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import fr.letroll.framework.Web;
import fr.letroll.mesmangas.parcelle.Miroir;

public class MangaAccess implements Miroir {
    public static String nomDuSite = "Manga-Access";
    private String adresseDesMangas = "http://manga-access.com/manga/list";
    private String adresseSeul = "http://manga-access.com/manga/0/";
    private Document doc;

    private String LastPage = "";

    public MangaAccess() {

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
            doc = Jsoup.connect(adresseDesMangas).timeout(4000).get();

            Elements table = doc.select("div[class^=c_h2]");
            for (org.jsoup.nodes.Element element : table) {
                array_manga.add(element.getElementsByTag("a").first().text());
            }
        } catch (Exception e) {
            array_manga.add("");
            e.printStackTrace();
        }
        return array_manga;
    }

    // =====================================================
    // retourne le nom du manga encode
    // =====================================================
    public String getNomEncode(String nomManga) {
        String nomEncode;
        nomEncode = nomManga;
        nomEncode = nomEncode.replaceAll(" ", "-");
        nomEncode = nomEncode.replaceAll("\\$", "");
        nomEncode = nomEncode.replaceAll("~", "");
        nomEncode = nomEncode.replaceAll("%", "");
        // nomEncode = nomEncode.replaceAll("[!#$%&'() ~*+,\"\\-:;\\[\\]<=>?]", "");
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
        String adr = adresseSeul + nomManga;
        // monUtilitaire.LOG("adresse creee pour mangaaccess",adr);
        try {
            String source = Web.GetHTML(adr, null);
            doc = Jsoup.parse(source);
            Elements chapters = doc.select("a[class=download-link]");
            // monUtilitaire.LOG("chapters", ""+chapters.size());
            Elements chap = chapters.select("strong");
            for (Element element : chap) {
                lesChapitres.add(element.text());
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
    public ArrayList<String> getListImages(String curUrl2) {
        // monUtilitaire.LOG("getListImages manga-access", curUrl2);
        ArrayList<String> res = new ArrayList<String>();
        try {
            doc = Jsoup.connect(curUrl2).timeout(3000).get();

            Element select = doc.select("select[id=page_switch]").get(0);
            Elements pages = select.select("option");

            res.add("" + pages.size());
            for (Element element : pages) {
                String idx = element.text();
                String url = curUrl2 + "/" + idx;
                res.add(url);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return res;
    }

    // ===========================================================
    // retourne le chemin de l'image a partir de l'adresse donnee
    // ===========================================================
    public String getImageFromPageAndWrite(String chemin) {
        String tmp = "";
        try {
            doc = Jsoup.connect(chemin).timeout(3000).get();
            Elements monImage = doc.select("div[id=pic]>img");
            if (monImage.size() != 0) {
                tmp = monImage.get(0).attr("src");
                LastPage = tmp;
            } else {
                int taille = LastPage.length(), numpage;
                String tmp2, lext;
                // Log.e("getlastpageuri lastpage",LastPage);
                tmp = LastPage.substring(0, taille - 6);
                tmp2 = LastPage.substring(taille - 6, taille - 4);
                numpage = Integer.parseInt(tmp2);
                numpage++;
                lext = LastPage.substring(taille - 4, taille);
                // Log.e("getlastpageuri numpage",""+numpage);
                // Log.e("getlastpageuri lext",""+lext);
                tmp = tmp + numpage + lext;
                // Log.e("derniere image :", "derniere image ");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return tmp;
    }

    // ===========================================================
    // retourne l'extension de l'image d'une adresse donnee
    // ===========================================================
    public String getImageExt(String chemin) {
        try {
            doc = Jsoup.connect(chemin).timeout(3000).get();
        } catch (IOException e) {
            e.printStackTrace();
        }
        Element monImage = doc.select("div[id=pic]>img").get(0);
        String src = monImage.attr("src");
        String ext = src.substring(src.length() - 3, src.length());
        return ext;
    }

    // ===========================================================
    // retourne l'adresse ou chercher les images
    // ===========================================================
    public String getImageAdr(String titre, String adresse, String chapitre) {
        String tmp = getNomEncode(titre);
        adresse = adresseSeul + tmp + "/chapter/" + chapitre;
        return adresse;
    }

    @Override public String getNomDuSite() {
        return nomDuSite;
    }

}
