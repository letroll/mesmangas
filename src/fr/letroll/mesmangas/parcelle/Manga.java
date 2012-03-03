package fr.letroll.mesmangas.parcelle;

import java.io.Serializable;
import java.util.ArrayList;

public class Manga implements Serializable {

	private static final long serialVersionUID = 1L;
	private String nom, adresse;
	private ArrayList<Chapitre> lesChapitres = new ArrayList<Chapitre>();

	public String getNom() {
		return nom;
	}

	public ArrayList<Chapitre> getLesChapitres() {
		return lesChapitres;
	}
	
	public void add(Chapitre monChap){
		lesChapitres.add(monChap);
	}
	
	public void removeAll(){
        lesChapitres.clear();
    }
	
	public Chapitre get(int pos){
		return lesChapitres.get(pos);
	}
	
	public int getChapPos(String monChap){
	    for (Chapitre unChap : lesChapitres) {
            if(monChap==unChap.getNom())return lesChapitres.indexOf(unChap);
        }
	    return 0;
	}

	public Manga(String _nom, String _adresse, ArrayList<Chapitre> _leschapitres) {
		nom = _nom;
		setAdresse(_adresse);
		lesChapitres = _leschapitres;
	}
	
	public Manga(String _nom, String _adresse) {
		nom = _nom;
		setAdresse(_adresse);
	}

	public void setAdresse(String adresse) {
		this.adresse = adresse + getNom();
	}

	public String getAdresse() {
		return adresse;
	}
}
