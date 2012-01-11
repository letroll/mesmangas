package fr.letroll.mesmangas;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import fr.letroll.mesmangas.parcelle.Mesmangas;

public class Utilitaire {

    String path;

    public Utilitaire(String _path) {
        path = _path;
    }

    // =======================================================
    // Serialise
    // =======================================================

    public void serialiser(Mesmangas mesmangas) {
        ObjectOutputStream serialise;
        try {
            serialise = new ObjectOutputStream(new FileOutputStream(path));
            serialise.writeObject(mesmangas);
            serialise.flush();
            serialise.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        // Log.e("serialise","serialise");
    }

    // =======================================================
    // deserialise
    // =======================================================

    public Mesmangas deserializeObject() {
        try {
            ObjectInputStream in = new ObjectInputStream(new FileInputStream(path));
            Mesmangas mesmangas = (Mesmangas) in.readObject();
            in.close();
            return mesmangas;
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    // =======================================================
    // sauvegarde les favoris
    // =======================================================
    // public boolean saveFavorite(File file) {
    // // Recursively delete all contents.
    // File[] files = file.listFiles();
    // if (files != null) {
    // for (int x = 0; x < files.length; x++) {
    // File childFile = files[x];
    // if (childFile.isDirectory()) {
    // if (!recursiveDelete(childFile)) {
    // return false;
    // }
    // } else {
    // if (!childFile.delete()) {
    // return false;
    // }
    // }
    // }
    // }
    // if (!file.delete()) {
    // return false;
    // }
    // return true;
    // }

}
