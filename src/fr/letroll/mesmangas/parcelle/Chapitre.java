package fr.letroll.mesmangas.parcelle;

import java.io.Serializable;

public class Chapitre implements Serializable {
	private static final long serialVersionUID = 1L;
	private int nbpage,nbpagedl;
	private String nom, chemin;
	private Boolean isRead;

	public Chapitre(String _nom) {
		setNom(_nom);
		nbpage = 0;
		setNbpagedl(0);
		chemin = "";
		setIsRead(false);
	}

   public void init() {
       nbpage = 0;
       setNbpagedl(0);
       chemin = "";
       setIsRead(false);
    }
	
	public void setNom(String nom) {
		this.nom = nom;
	}

    public String getNom() {
        return nom;
    }

	public void setChemin(String chemin) {
		this.chemin = chemin;
	}

	public String getChemin() {
		return chemin;
	}

	public void setNbpage(int nbpage) {
		this.nbpage = nbpage;
	}

	public int getNbpage() {
		return nbpage;
	}

    public int getNbpagedl() {
        return nbpagedl;
    }

    public void setNbpagedl(int nbpagedl) {
        this.nbpagedl = nbpagedl;
    }

    public Boolean getIsRead() {
        return isRead;
    }

    public void setIsRead(Boolean isRead) {
        this.isRead = isRead;
    }
}
