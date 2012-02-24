package fr.letroll.mesmangas.site;

import java.io.IOException;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import fr.letroll.framework.Notification;
import fr.letroll.framework.Web;
import fr.letroll.mesmangas.parcelle.Miroir;

public class Animextremist implements Miroir {

    private String nomDuSite = "animextremist";
    private String adresseDuSite = "http://animextremist.com/mangas-online/";
    private String adresseDesMangas = "http://animextremist.com/mangas.htm";
    private String adresseSeul = "http://animextremist.com/";
    private String tag = "Animextremist";
    private Document doc;

    public Animextremist() {
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
            Elements titles = doc.select("tbody > tr > td > a");
            for (org.jsoup.nodes.Element element1 : titles) {
                array_manga.add(element1.text());
                // Notification.log("element1.text()", element1.text());
            }
            Collections.sort(array_manga);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
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
        // nomManga = nomManga.toLowerCase();
        nomEncode = nomManga;
        nomEncode = nomEncode.replace("1/2", "half");
        nomEncode = nomEncode.replace("1/3", "13");
        nomEncode = nomEncode.replaceAll("[!#$%&'() ~*+,\"\\:;\\[\\]<=>?]", "");

        // Notification.log(tag+" getNomEncode", nomEncode);
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
        String adr = adresseSeul + nomManga.toLowerCase() + "-manga.htm";
        // Notification.log(tag+" adr", adr);
        try {
            String source = Web.GetHTML(adr, null);
            // Notification.log(tag+" source", source);
            doc = Jsoup.parse(source);

            String chap;

            Elements chapters = doc.select("div > pre > span > a");
            for (Element element : chapters) {
                chap = element.attr("href");
                chap = chap.substring(chap.indexOf("/") + 1);
                chap = chap.substring(chap.indexOf("/") + 1);
                chap = chap.substring(0, chap.lastIndexOf("/"));
                lesChapitres.add(chap);
                // Notification.log(tag+" chap", chap);
            }

            Elements chapters2 = doc.select("div > pre > a");
            for (Element element : chapters2) {
                chap = element.attr("href");
                chap = chap.substring(chap.indexOf("/") + 1);
                chap = chap.substring(chap.indexOf("/") + 1);
                chap = chap.substring(0, chap.lastIndexOf("/"));
                lesChapitres.add(chap);
                // Notification.log(tag+" chap", chap);
            }

            Collections.sort(lesChapitres, new Comparator<String>() {
                public int compare(String s1, String s2) {
                    if (s1.length() == s2.length()) {
                        return s1.compareTo(s2);
                    } else {
                        if (s1.length() > s2.length()) {
                            return 1;
                        } else {
                            return -1;
                        }
                    }
                }
            });

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

        ArrayList<String> res = new ArrayList<String>();

        try {
            curUrl = curUrl.substring(25, curUrl.length() - 1);
            String name = curUrl.substring(0, curUrl.indexOf("/"));
            curUrl = adresseDuSite + curUrl.toLowerCase() + "/" + name.toLowerCase() + ".html";
            name = curUrl;

            // doc = Jsoup.connect(curUrl).timeout(3000).get();
            String source = Web.GetHTML(curUrl, null);
            doc = Jsoup.parse(source);
            Element iframe = doc.select("iframe[width=130]").get(1);
            curUrl = curUrl.substring(0, curUrl.lastIndexOf("/"));
            curUrl = curUrl + "/" + iframe.attr("src");
            source = Web.GetHTML(curUrl, null);
            doc = Jsoup.parse(source);
            Elements select = doc.select("select");
            Elements pages = select.select("option");
            pages.remove(0);
            res.add("" + pages.size());
            String idx;
            name = name.substring(0, name.lastIndexOf("/"));
            name = name.substring(0, name.lastIndexOf("/"));
            for (Element element : pages) {
                idx = name + "/" + element.attr("value");
                res.add(idx);
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
            Elements monImage = doc.select("div[id=photograph] > img");
            if (monImage.size() != 0) {
                tmp = monImage.get(0).attr("src");
                Notification.log(tag, tmp);
            }
        } catch (MalformedURLException e) {
        } catch (IOException e) {
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
//        String ext = "";
//        try {
//            doc = Jsoup.connect(chemin).timeout(3000).get();
//            Element monImage = doc.select("div[id=photograph] > img").get(0);
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
        // Notification.log("getImageAdr", "getImageAdr");
        // Notification.log("titre :", titre);
        // Notification.log("adresse :", adresse);
        // Notification.log("chapitre :", chapitre);

        adresse = adresseSeul + titre + "/" + chapitre + "/";
        return adresse;
    }

     public String getNomDuSite() {
        return nomDuSite;
    }
}