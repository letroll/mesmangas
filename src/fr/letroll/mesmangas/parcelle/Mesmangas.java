package fr.letroll.mesmangas.parcelle;

import java.io.Serializable;
import java.util.ArrayList;


public class Mesmangas implements Serializable {

	private static final long serialVersionUID = 1L;
	private ArrayList<Manga> mesMangas = new ArrayList<Manga>();
	private ArrayList<Site> mesSite = new ArrayList<Site>();
	
	public ArrayList<Manga> getMesMangas() {
		return mesMangas;
	}

	public Mesmangas() {

	}

	public void add(Manga monmanga) {
		mesMangas.add(monmanga);
	}

	public void addSite(Site monsite) {
		mesSite.add(monsite);
	}
	
	public Boolean containSite(Site monsite){
		return mesSite.contains(monsite);
	}
	
	public void remove(int i) {
		mesMangas.remove(i);
	}

	public void removes(String monsite){
	    int sitepos=this.getSitePos(monsite);
	    if(sitepos!=-1)mesSite.remove(sitepos);
	}
	public int size(){
		if(mesMangas.isEmpty()||mesMangas==null){
			return 0;
		}else{
			return mesMangas.size();
		}
	}
	
	public int sizeSite() {
		if(mesSite.isEmpty()||mesSite==null){
			return 0;
		}else {
			return mesSite.size();
		}
	}

	public Manga get(int i) {
		return mesMangas.get(i);
	}
	
	public Site getS(int i) {
		return mesSite.get(i);
	}

	public void setMesSite(ArrayList<Site> lesSite) {
		mesSite = lesSite;
	}

	public ArrayList<Site> getMesSite() {
		return mesSite;
	}
	
	public int getSitePos(String site){
		int i;
		for(i=0;i<mesSite.size();i++){
			if(mesSite.get(i).getAdresse()==site){
				return i;
			}
			
		}
		return -1;
	}
	
	public ArrayList<String> getMesAdresseSite() {
		ArrayList<String> mesAdresses = new ArrayList<String>();
		int i;
		for(i=0;i<mesSite.size();i++){
			mesAdresses.add(mesSite.get(i).getAdresse());
		}
		return mesAdresses;
	}
	
	
}
