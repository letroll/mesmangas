package fr.letroll.mesmangas.site;

import java.io.IOException;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.Collections;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import android.util.Log;
import fr.letroll.framework.Notification;
import fr.letroll.framework.Web;
import fr.letroll.mesmangas.Miroir;

public class Bleachexile implements Miroir{
    private String nomDuSite = "Bleachexile";
    private String adresseDuSite = "http://manga.bleachexile.com/";
    private Document doc;

    // private String Icon = "bleachexile.ico";
    private String LastPage = "";

    public Bleachexile(String _path) {
    }

    // =====================================================
    // retourne vrais si l'adresse correspond au site
    // =====================================================
    public Boolean isMe(String url) {
        // Log.e("isMe url :",url);
        // Log.e("isMe site :",adresseDuSite.toLowerCase());
        return url.contains(nomDuSite.toLowerCase());
    }

    // =====================================================
    // retourne la liste des mangas disponible sur le site
    // =====================================================
    public ArrayList<String> getMangaList() {
        ArrayList<String> array_manga = new ArrayList<String>();
        try {
            doc = Jsoup.connect(adresseDuSite).timeout(4000).get();
            Element liste = doc.select("select[name=series]").first();
            Elements titles = liste.select("option");
            int i = 0;
            // Log.e("getlistmanga dbps",""+titles.size());
            for (org.jsoup.nodes.Element element : titles) {
                if (i > 0) {
                    array_manga.add(element.text());
                }
                i++;
            }
        } catch (IOException e) {
            e.printStackTrace();
            array_manga.add("");
        } catch (NullPointerException e) {
            array_manga.add("");
            e.printStackTrace();
        }
        if (!array_manga.isEmpty())
            Notification.log("getlistmanga", "au moins un manga de trouve");
        return array_manga;
    }

    // =====================================================
    // retourne le nom du manga encode
    // =====================================================
    public String getNomEncode(String nomManga) {
        String nomEncode;
        nomManga = nomManga.toLowerCase();
        nomEncode = nomManga;
        nomEncode = nomEncode.replaceAll(" ", "-");
        nomEncode = nomEncode.replaceAll("\\$", "");
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
        String adr = adresseDuSite + nomManga + ".html";
        // Log.e("adresse creee pour bleachexile",adr);
        try {
            String source = Web.GetHTML(adr, null);
            doc = Jsoup.parse(source);
            Element select = doc.select("select[name=chapter]").first();
            Elements chapters = select.select("option");
            for (Element element : chapters) {
                lesChapitres.add(element.text());
            }
        } catch (Exception e) {
            lesChapitres.add("");
        }
        Collections.reverse(lesChapitres);
        return lesChapitres;
    }

    // =====================================================
    // retourne la liste des adresses des images
    // =====================================================
    public ArrayList<String> getListImages(String curUrl2) {
        Log.e("getListImages bleachexile", curUrl2);
        ArrayList<String> res = new ArrayList<String>();
        String curUrl = curUrl2;
        int posChap = curUrl.indexOf("-chapter-");
        String baseUrl;
        try {
            doc = Jsoup.connect(curUrl2).timeout(3000).get();
            if (posChap == -1) {
                Element monSelect = doc.select("select[name=chapter]").get(0);
                Element chapurl = monSelect.select("option[selected=selected]").get(0);
                int posDot = curUrl.indexOf(".html", posChap + 9);
                baseUrl = curUrl.substring(0, posDot) + "-chapter-" + chapurl;
            } else {
                int posTirNext = curUrl.indexOf("-", posChap + 9);
                if (posTirNext != -1) {
                    baseUrl = curUrl.substring(0, posTirNext);
                } else {
                    int posDot = curUrl.indexOf(".html", posChap + 9);
                    baseUrl = curUrl.substring(0, posDot);
                }
            }
            Element select = doc.select("select[name=pages]").get(0);
            Elements pages = select.select("option");

            res.add("" + pages.size());
            for (Element element : pages) {
                String idx = element.val().toString();
                String url = baseUrl + "-page-" + idx + ".html";
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
        try {
            doc = Jsoup.connect(chemin).timeout(3000).get();
            Elements monImage = doc.select("img[title=Click to advance to the next page!]");
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
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
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
        Element monImage = doc.select("img[title=Click to advance to the next page!]").get(0);
        String src = monImage.attr("src");
        String ext = src.substring(src.length() - 3, src.length());
        return ext;
    }

    // ===========================================================
    // retourne l'adresse ou chercher les images
    // ===========================================================
    public String getImageAdr(String titre, String adresse, String chapitre) {
        String tmp = getNomEncode(titre);
        adresse = adresse.substring(0, adresse.lastIndexOf("/") + 1);
        adresse = adresse + tmp + "-" + chapitre + ".html";
        return adresse;
    }

}
