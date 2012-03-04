package fr.letroll.mesmangas;

import java.io.File;
import java.util.ArrayList;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.KeyEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.ListView;
import android.widget.TextView;
import fr.letroll.adapter.ListOneAdapter;
import fr.letroll.framework.FileLt;
import fr.letroll.framework.Notification;
import fr.letroll.framework.SystemInformation;
import fr.letroll.mesmangas.parcelle.Manga;
import fr.letroll.mesmangas.parcelle.Mesmangas;

public class Maliste1 extends Activity implements OnItemLongClickListener, OnItemClickListener {

    // variable
    private String path;
    private File monDossier, maSauvegarde;
    private Mesmangas mesmangas;
    private Utilitaire monUtilitaire;
    private Boolean astuces;
    private SharedPreferences preferences;
    // composant
    private ListView l1;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.maliste);

        preferences = PreferenceManager.getDefaultSharedPreferences(this);
        astuces = preferences.getBoolean("astuces", false);
        // composant
        l1 = (ListView) findViewById(R.id.listViewMaliste);
        l1.setOnItemClickListener(this);
        l1.setOnItemLongClickListener(this);

        path = this.getIntent().getExtras().getString("path");

        // variable
        monDossier = new File("mnt/sdcard/.mesmangas");
        if (!monDossier.exists()) {
            monDossier.mkdir();
        }
        monUtilitaire = new Utilitaire(path);

        if (!astuces)
            Notification.toastc(this, this.getString(R.string.appuislongsurmangapoursuppr));

        mesmangas = new Mesmangas();
        maSauvegarde = new File(path);
        if (maSauvegarde.exists()) {
            mesmangas = monUtilitaire.deserializeObject();
        } else {
            monUtilitaire.serialiser(mesmangas);
        }

        affichage();
        if (!SystemInformation.IsConnectedToNetwork(this))
            Notification.toastc(this, this.getString(R.string.netnecessaire));

    }

    private void affichage() {
        mesmangas = new Mesmangas();
        mesmangas = monUtilitaire.deserializeObject();

        ArrayList<String> groupsMangas = new ArrayList<String>();
        ArrayList<String> groupsChapitre = new ArrayList<String>();
        ArrayList<String> groupsSite = new ArrayList<String>();
        try {
            for (Manga manga : mesmangas.getMesMangas()) {
                groupsMangas.add(manga.getNom());
                groupsChapitre.add("");
                groupsSite.add(manga.getAdresse());
            }
        } catch (Exception e) {
            groupsMangas.add("");
            groupsChapitre.add("");
            groupsSite.add("");
            e.printStackTrace();
        }

        l1.setAdapter(new ListOneAdapter(this, groupsMangas, groupsChapitre, groupsSite));
    }

    // public void onBackPressed() {
    // super.onBackPressed();
    // Maliste1.this.finish();
    // }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
            Maliste1.this.finish();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    public void onActionBarButtonExitClick(View v) {
        this.setResult(1);
        Maliste1.this.finish();
    }

    public void onActionBarButtonBackClick(View v) {
        Maliste1.this.finish();
    }

    public void backhome(View v) {
        this.setResult(2);
        Maliste1.this.finish();
    }

    public void removeManga(int position){
        monDossier = new File("sdcard/.mesmangas");
        File monDossierManga = new File(monDossier, mesmangas.get(position).getNom());
        FileLt.recursiveDelete(monDossierManga);
        mesmangas.remove(position);
        monUtilitaire.serialiser(mesmangas);
        affichage();
        Notification.toastc(this, this.getString(R.string.mangasupprime));
    }
    
    public boolean onItemLongClick(AdapterView<?> parent, View vue, int position, long id) {
        removeManga(position);
        return true;
    }

    public void onItemClick(AdapterView<?> parent, View view, int pos, long id) {
        String titre = ((TextView) view.findViewById(R.id.manga)).getText().toString();

        String adresse = mesmangas.get(pos).getAdresse();
        // monUtilitaire.LOG("clique sur le manga", titre);
        // monUtilitaire.LOG("d'adresse", adresse);
        // monUtilitaire.LOG("nummanga", ""+pos);

        Intent intentMaliste2 = new Intent(this, Maliste2.class);
        intentMaliste2.putExtra("path", path);
        intentMaliste2.putExtra("titre", titre);
        intentMaliste2.putExtra("adresse", adresse);
        intentMaliste2.putExtra("nummanga", pos);
        startActivityForResult(intentMaliste2, 1000);

    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (resultCode) {
        case 1:
            this.setResult(1);
        case 2:
            Maliste1.this.finish();
            break;
        case 3:
             Bundle extras = data.getExtras();
             removeManga(extras.getInt("nummanga"));
            break;
        }
    }
}
