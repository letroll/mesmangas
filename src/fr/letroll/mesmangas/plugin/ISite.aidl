package fr.letroll.mesmangas.plugin;

interface ISite {
	int getPid();
	boolean isMe(in String mUrl);
	List<String> getMangaList();
	String getNomEncode(in String nomManga);
	String getNomEncode2(in String nomManga);
	List<String> getChapitre(in String nomManga);
	List<String> getListImages();
	String getImageFromPageAndWrite(in String chemin);
	String getImageAdr(in String titre, in String adresse, in String chapitre);
}