package fr.letroll.mesmangas.site;

import fr.letroll.framework.Notification;
import fr.letroll.framework.Web;
import fr.letroll.mesmangas.parcelle.Miroir;
import java.io.IOException;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.Collections;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class AnimeStory implements Miroir {

    private String nomDuSite = "Anime-Story";

    public String getNomDuSite() {
        return nomDuSite;
    }
    private String adresseDuSite = "http://www.anime-story.com/mangas/";
    private String adresseSeul = "http://www.anime-story.com/";
    private String tag = "mesmangas";
    private Document doc;

    // private String Icon="bleachexile.ico";
    public AnimeStory() {
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
            Elements titles = doc.select("ul[class=clear] > li:not(.title) >div[class=left] > a");
//             Notification.log("nbr titre", ""+titles.size());
            int i = 0;
            for (org.jsoup.nodes.Element element : titles) {
                if (i > 0) {
                    if (!element.text().contains("mise à") && !element.text().contains("Mise à")) {
                        array_manga.add(element.text().toLowerCase());
                    } else {
                        String tmp = element.text();
                        tmp = tmp.replace("(", "");
                        tmp = tmp.replace("mise à jour", "");
                        tmp = tmp.replace("Mise à jour", "");
                        tmp = tmp.replace(")", "");
                        array_manga.add(tmp.toLowerCase());
                    }

                }
                i++;
            }
            i = 0;
            Collections.sort(array_manga);
        } catch (MalformedURLException e) {
        } catch (IOException e) {
            e.printStackTrace();
        } catch (Exception e) {
        }

        return array_manga;
    }

    // =====================================================
    // retourne le nom du manga encode
    // =====================================================
    public String getNomEncode(String nomManga) {
        String nomEncode;
//        nomManga = nomManga.toLowerCase();
        nomEncode = nomManga;
        nomEncode = nomEncode.replaceAll("mise à jour", "");
        nomEncode = nomEncode.replace("1/2", "half");
        nomEncode = nomEncode.replace("1/3", "13");

        nomEncode = nomEncode.replaceAll("[!#$%&'() ~*,\"\\-:;\\[\\]<=>?]", "");

        // Notification.log("nom modifie par getNomEncode", nomEncode);
        return nomEncode;
    }

    // =====================================================
    // retourne le nom du manga encode hors adresse
    // =====================================================
    public String getNomEncode2(String nomManga) {
        nomManga = this.getNomEncode(nomManga);
        nomManga = nomManga.replaceAll("\\.", "");
        return nomManga;
    }

    // =====================================================
    // retourne la liste des chapitres d'un manga
    // =====================================================
    public ArrayList<String> getChapitre(String nomManga) {
        ArrayList<String> lesChapitres = new ArrayList<String>();
        String adr = adresseDuSite + nomManga + "/";
        String chap;
         Notification.log("source", adr);
        try {
            String source = Web.GetHTML(adr, null);
//             Notification.log("source", source);
            doc = Jsoup.parse(source);

            Elements liens = doc.select("div[class^=listchapseries] > ul > li > div");
            for (Element element : liens) {
                chap = element.select("a:eq(2").attr("title");
                chap = chap.replace("Lire ", "");
                chap = chap.substring(nomManga.length() + 1, chap.length() - 2);
                chap = chap.toLowerCase();
                chap = chap.trim();
                chap = chap.replace(" ", "-");
                lesChapitres.add(chap);
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
    public ArrayList<String> getListImages(String curUrl) {
        // Notification.log("getListImages AnimeStory", curUrl);
        ArrayList<String> res = new ArrayList<String>();

        try {
            doc = Jsoup.connect(curUrl).timeout(3000).get();
            Element select = doc.select("select").get(2);
            Elements pages = select.select("option");

            // Notification.log("getlistimage", "" + pages.size());
            res.add("" + pages.size());
            for (Element element : pages) {
                String idx = element.attr("value");
                res.add(idx);
            }

        } catch (MalformedURLException e) {
        } catch (IOException e) {
            e.printStackTrace();
            res.add("");
        } catch (Exception e) {
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
            Elements monImage = doc.select("img[title*=page]");
            if (monImage.size() != 0) {
                tmp = monImage.get(0).attr("src");
            }
        } catch (MalformedURLException e) {
            Notification.log(tag, "erreur due à : " + tmp);
        } catch (IOException e) {
            tmp = "";
            e.printStackTrace();
        } catch (Exception e) {
        }
        return tmp;
    }

    // ===========================================================
    // retourne l'extension de l'image d'une adresse donnee
    // ===========================================================
//    public String getImageExt(String chemin) {
//        String ext = "";
//        try {
//            doc = Jsoup.connect(chemin).timeout(3000).get();
//            Element monImage = doc.select("a[title=Aller a la page suivante] > img").get(0);
//            String src = monImage.attr("src");
//            ext = src.substring(src.length() - 3, src.length());
//        } catch (MalformedURLException e) {
//        } catch (IOException e) {
//            e.printStackTrace();
//        } catch (Exception e) {
//        }
//
//        return ext;
//    }

    // ===========================================================
    // retourne l'adresse ou chercher les images
    // ===========================================================
    public String getImageAdr(String titre, String adresse, String chapitre) {
//        Notification.log("getImageAdr", "getImageAdr");
//        Notification.log("titre :", titre);
//        Notification.log("adresse :", adresse);
//        Notification.log("chapitre :", chapitre);

        adresse = adresseSeul + titre + "/" + chapitre + "/";
        return adresse;
    }
}
