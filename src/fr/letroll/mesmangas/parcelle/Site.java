package fr.letroll.mesmangas.parcelle;

import java.io.Serializable;
import java.util.ArrayList;

public class Site implements Serializable {
	private static final long serialVersionUID = 1L;
	private String adresse;
	private ArrayList<String> titre=new ArrayList<String>();
	
	public ArrayList<String> getTitre() {
		return titre;
	}
	public Site(String site, ArrayList<String> array_title) {
		adresse=site;
		titre=array_title;
	}
	public void setAdresse(String adresse) {
		this.adresse = adresse;
	}
	public String getAdresse() {
		return adresse;
	}
}
