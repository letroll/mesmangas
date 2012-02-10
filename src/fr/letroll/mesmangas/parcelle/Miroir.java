package fr.letroll.mesmangas.parcelle;

import java.util.ArrayList;


public interface Miroir {
    
    // =====================================================
    // retourne vrais si l'adresse correspond au site
    // =====================================================
    public Boolean isMe(String url);

    // =====================================================
    // retourne la liste des mangas disponible sur le site
    // =====================================================
    public ArrayList<String> getMangaList();

    // =====================================================
    // retourne le nom du manga encode 
    // =====================================================
    public String getNomEncode(String nomManga);
    
    // =====================================================
    // retourne le nom du manga encode hors adresse
    // =====================================================
    public String getNomEncode2(String nomManga);

    // =====================================================
    // retourne la liste des chapitres d'un manga
    // =====================================================
    public ArrayList<String> getChapitre(String nomManga);

    // =====================================================
    // retourne la liste des adresses des images
    // =====================================================
    public ArrayList<String> getListImages(String curUrl);

    // ===========================================================
    // retourne le chemin de l'image a partir de l'adresse donnee
    // ===========================================================
    public String getImageFromPageAndWrite(String chemin);

    // ===========================================================
    // retourne l'extension de l'image d'une adresse donnee
    // ===========================================================
    public String getImageExt(String chemin);

    // ===========================================================
    // retourne l'adresse ou chercher les images
    // ===========================================================
    public String getImageAdr(String titre, String adresse, String chapitre);

    // ===========================================================
    // retourne le nim du site
    // ===========================================================
    public String getNomDuSite();

}
