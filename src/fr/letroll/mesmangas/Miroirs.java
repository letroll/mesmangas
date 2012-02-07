package fr.letroll.mesmangas;

import java.util.ArrayList;

public class Miroirs {
    private static final String[][] adresse = { {"http://manga-access.com/","en"}, {"http://www.anime-story.com/search?","fr"},{"http://dbps.free.fr/","fr"},{"http://www.mangafox.com/directory/","en"},{"http://animextremist.com/mangas.htm","sp"}};

    public String getMiroir(int numsite){
        return adresse[numsite][0];
    }
    
    public ArrayList<String> getMiroirs(String pays){
        ArrayList<String> site= new ArrayList<String>();
        int i;
        for (i=0;i<adresse.length;i++) {
            if(adresse[i][1].equals(pays))site.add(adresse[i][0]);
        }
        return site;
    }
    
}
