package fr.letroll.mesmangas;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.IBinder;
import android.os.RemoteException;
import android.os.Vibrator;
import android.preference.PreferenceManager;
import android.view.KeyEvent;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.LayoutAnimationController;
import android.view.animation.TranslateAnimation;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import fr.letroll.adapter.ListTwoAdapter;
import fr.letroll.downloadservice.Iservice;
import fr.letroll.framework.FileLt;
import fr.letroll.framework.Notification;
import fr.letroll.framework.StringLt;
import fr.letroll.framework.SystemInformation;
import fr.letroll.mesmangas.parcelle.Chapitre;
import fr.letroll.mesmangas.parcelle.Mesmangas;
import fr.letroll.mesmangas.parcelle.Miroir;
import fr.letroll.mesmangas.site.AnimeStory;
import fr.letroll.mesmangas.site.Animextremist;
import fr.letroll.mesmangas.site.Dbps;
import fr.letroll.mesmangas.site.MangaAccess;
import fr.letroll.mesmangas.site.Mangafox;

public class Maliste2 extends Activity implements OnItemClickListener, OnItemLongClickListener {

    // variable
    private String path, adresse, titre, chapitre;
    private Mesmangas mesmangas;
    private Utilitaire monUtilitaire;
    private String[] childrens;
    private Boolean[] reads;
    private int nummanga;
    private Boolean astuces, ordredeschapitres;
    private SharedPreferences preferences;
    private Vibrator vib;
    // composant
    private ListView l1;
    // site
    private Miroir miroir;

    // \\//\\//\\//\\//\\//\\//\\//\\//\\//\\//\\//\\//\\//\\//\\//\\//\\//\\
    private BackgroundPrepare preparation;
    private Iservice remoteService;
    private boolean started = false;
    private RemoteServiceConnection conn = null;
    private String tag = "downloadservice", url, urlpast = "";
    private Boolean result;
    private int i = 0, nbpages = 0, position, positionpast = 0;
    private ProgressBar progressbar;
    private final BroadcastReceiver mybroadcast = new DataReceiver();
    public static final String CUSTOM_INTENT = "fr.letroll.downloadservice.TEST";

    // \\//\\//\\//\\//\\//\\//\\//\\//\\//\\//\\//\\//\\//\\//\\//\\//\\//\\//\\//\\//\\//\\

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.maliste);

        path = this.getIntent().getExtras().getString("path");
        monUtilitaire = new Utilitaire(path);

        if (!SystemInformation.IsConnectedToNetwork(this))
            Toast.makeText(this, getString(R.string.netnecessaire), Toast.LENGTH_LONG).show();
        adresse = this.getIntent().getExtras().getString("adresse");
        // Notification.log("adresse", adresse);
        titre = this.getIntent().getExtras().getString("titre");
        // Notification.log("titre", titre);
        nummanga = this.getIntent().getExtras().getInt("nummanga");

        preferences = PreferenceManager.getDefaultSharedPreferences(this);
        astuces = preferences.getBoolean("astuces", false);
        ordredeschapitres = preferences.getBoolean("ordredeschapitres", false);

        if (!astuces)
            Notification.toastc(this, getString(R.string.appuislongsurchapitrepoursuppr));

        // getupdate = new getUpdate();

        // composant
        l1 = (ListView) findViewById(R.id.listViewMaliste);
        l1.setOnItemClickListener(this);
        l1.setOnItemLongClickListener(this);

        vib = (Vibrator) getSystemService(Afficher.VIBRATOR_SERVICE);

        // affiche refresh
        LinearLayout l2 = (LinearLayout) findViewById(R.id.actionbar);
        ImageView imrefresh = (ImageView) l2.findViewById(R.id.btn_title_refresh);
        imrefresh.setVisibility(View.VISIBLE);
        // ===============================

        if (new MangaAccess().isMe(adresse)) {
            miroir = new MangaAccess();
        }
        if (new Dbps().isMe(adresse)) {
            miroir = new Dbps();
        }
        if (new AnimeStory().isMe(adresse)) {
            miroir = new AnimeStory();
        }
        if (new Mangafox().isMe(adresse)) {
            miroir = new Mangafox();
        }
        if (new Animextremist().isMe(adresse)) {
            miroir = new Animextremist();
        }
        try {
            titre = miroir.getNomEncode(titre);
            // Notification.log("titre encode", titre);
        } catch (Exception e) {
            titre = "";
            e.printStackTrace();
        }

        mesmangas = monUtilitaire.deserializeObject();
        if (mesmangas.get(nummanga).getLesChapitres().isEmpty()
                || mesmangas.get(nummanga).getLesChapitres() == null) {
            new PremierAffichageTast().execute();
            // Notification.log("premier lance", "premier lance");
        } else {
            // Notification.log("nouveau lance", "nouveau lance");
            try {
                ArrayList<Chapitre> deschap = mesmangas.get(nummanga).getLesChapitres();
                childrens = new String[deschap.size()];
                reads = new Boolean[deschap.size()];
                int i = 0;
                for (Chapitre chapitre : deschap) {
                    childrens[i] = chapitre.getNom();
                    reads[i] = (chapitre.getNbpage() > 0) ? true : false;
                    i++;
                }
                affichage(childrens, reads);
            } catch (Exception e) {
                e.printStackTrace();
                Notification.toastc(this, getString(R.string.problemelorsduchargement));
                e.printStackTrace();
            }
        }

        // \\//\\//\\//\\//\\//\\//\\
        startService();
        bindService();
        // \\//\\//\\//\\//\\//\\//\\//\\//\\//\\//\\//\\//\\//\\

    }

    public class PremierAffichageTast extends AsyncTask<Void, Void, Void> {
        String[] monTab;
        ProgressDialog dialog = new ProgressDialog(Maliste2.this);

        protected void onPreExecute() {
            super.onPreExecute();
            // Dialogue d'attente
            try {
                dialog.setMessage(getString(R.string.chargement));
                dialog.setCancelable(true);
                dialog.show();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        protected Void doInBackground(Void... arg0) {
            monTab = getChapitre();
            return null;
        }

        @Override protected void onPostExecute(Void result) {
            super.onPostExecute(result);
            dialog.dismiss();
            reads = new Boolean[monTab.length];
            for (int i = 0; i < monTab.length; i++) {
                reads[i] = false;
            }
            if (monTab.length > 0) {
                affichage(monTab, reads);
            } else {
                Notification.toastl(Maliste2.this, Maliste2.this.getString(R.string.pas2chapitredisponible));

                Intent intentAfficher = new Intent(Maliste2.this, Afficher.class);
                intentAfficher.putExtra("chapitre", chapitre);
                intentAfficher.putExtra("nummanga", nummanga);
                Maliste2.this.setResult(3, intentAfficher);
                Maliste2.this.finish();
            }
        }
    }

    private String[] getChapitre() {
        ArrayList<String> mesChapitres = new ArrayList<String>();

        // miroir.getClass().getSimpleName().toString()

        if (miroir.getClass().getSimpleName().toString().equals("AnimeStory")) {
            adresse = adresse.substring(0, adresse.lastIndexOf("/"));
            adresse = adresse + "/" + titre + "/";
            mesChapitres = miroir.getChapitre(titre);
        } else if (miroir.getClass().getSimpleName().toString().equals("Mangafox")) {
            adresse = adresse.replaceAll("directory", "manga");
            mesChapitres = miroir.getChapitre(titre);
        } else {
            mesChapitres = miroir.getChapitre(titre);
        }

        if (ordredeschapitres)
            Collections.reverse(mesChapitres);

        int h = mesChapitres.size(), i;
        childrens = new String[h];
        mesmangas.get(nummanga).removeAll();
        for (i = 0; i < h; i++) {
            childrens[i] = mesChapitres.get(i).toString();
            mesmangas.get(nummanga).add(new Chapitre(childrens[i]));
        }
        monUtilitaire.serialiser(mesmangas);
        return childrens;
    }

    private void affichage(String[] list, Boolean[] reads) {
        l1.setAdapter(new ListTwoAdapter(this, list, reads));
        l1.setTextFilterEnabled(true);
        AnimationSet set = new AnimationSet(true);
        Animation animation = new AlphaAnimation(0.0f, 1.0f);
        animation.setDuration(50);
        set.addAnimation(animation);
        animation = new TranslateAnimation(Animation.RELATIVE_TO_SELF, 0.0f,
                Animation.RELATIVE_TO_SELF, 0.0f, Animation.RELATIVE_TO_SELF, -1.0f,
                Animation.RELATIVE_TO_SELF, 0.0f);
        animation.setDuration(100);
        set.addAnimation(animation);
        LayoutAnimationController controller = new LayoutAnimationController(set, 0.5f);
        l1.setLayoutAnimation(controller);
    }

    public void onActionBarButtonRefreshClick(View v) {
        new PremierAffichageTast().execute();
    }

    // \\ //\\ //\\ //\\ //\\ //\\ //\\ //\\ //\\ //\\ //\\ //\\ //\\
    // public void onBackPressed() {
    // super.onBackPressed();
    // Maliste2.this.finish();
    // }
    // \\ //\\ //\\ //\\ //\\ //\\ //\\ //\\ //\\ //\\ //\\ //\\ //\\
    @Override public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
            Maliste2.this.finish();
            stopService();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    public void onActionBarButtonExitClick(View v) {
        this.setResult(1);
        Maliste2.this.finish();
        stopService();
    }

    public void onActionBarButtonBackClick(View v) {
        Maliste2.this.finish();
        stopService();
    }

    public void backhome(View v) {
        this.setResult(2);
        Maliste2.this.finish();
        stopService();
    }

    public void onItemClick(AdapterView<?> parent, View view, int pos, long id) {
        Intent intentAfficher = new Intent(this, Afficher.class);
        intentAfficher.putExtra("path", path);
        intentAfficher.putExtra("manga", titre);
        chapitre = ((TextView) view.findViewById(R.id.chapitre)).getText().toString();
        int numchap = mesmangas.get(nummanga).getChapPos(chapitre);

        intentAfficher.putExtra("numchapitre", numchap);

        if (miroir.getClass().getSimpleName().toString().equals("AnimeStory")
                || miroir.getClass().getSimpleName().toString().equals("Mangafox")) {
            adresse = miroir.getImageAdr(titre, adresse, chapitre);
        } else {
            chapitre = miroir.getNomEncode(chapitre);
            adresse = miroir.getImageAdr(titre, adresse, chapitre);
        }
        // Notification.log("adresse", adresse);
         intentAfficher.putExtra("chemin", adresse);
         intentAfficher.putExtra("chapitre", chapitre);
//         Notification.log("chapitre", ""+chapitre);
         intentAfficher.putExtra("nummanga", nummanga);
        // Notification.log("nummanga", ""+nummanga);

        // setResult(3, intentAfficher);

        startActivityForResult(intentAfficher, 2000);
    }

    public boolean onItemLongClick(AdapterView<?> parent, View vue, int position, long id) {
        File monDossier;
        String lechapitre = ((TextView) vue.findViewById(R.id.chapitre)).getText().toString();
        vue.findViewById(R.id.progress).setVisibility(View.INVISIBLE);
        vue.findViewById(R.id.progressBar1).setVisibility(View.GONE);
        vue.findViewById(R.id.icon).setVisibility(View.VISIBLE);

        String cheminduchapitre = "sdcard/.mesmangas/" + miroir.getNomEncode2(titre) + "/"
                + lechapitre;
        monDossier = new File(cheminduchapitre);
        FileLt.recursiveDelete(monDossier);

        int i = 0;
        for (Chapitre unchapitre : mesmangas.get(nummanga).getLesChapitres()) {
            if (unchapitre.getNom() == lechapitre) {
                mesmangas.get(nummanga).get(i).setNbpage(0);
                break;
            }
            i++;
        }
//        affichage(childrens, reads);
        vib.vibrate(500);
        // ImageView iv = (ImageView) vue.findViewById(R.id.icon);
        // iv.setVisibility(View.INVISIBLE);
        mesmangas.get(nummanga).get(position).setNbpage(0);
        monUtilitaire.serialiser(mesmangas);

        return true;
    }

    public void Download(View vue) {
        for (int i = 0; i < l1.getChildCount(); i++) {
            if (l1.getChildAt(i).findViewById(R.id.icon).equals(vue))
                position = i;
        }

        l1.getChildAt(position).findViewById(R.id.icon).setVisibility(View.GONE);
        l1.getChildAt(position).findViewById(R.id.progressBar1).setVisibility(View.VISIBLE);

        String test = ((TextView) l1.getChildAt(position).findViewById(R.id.chapitre)).getText()
                .toString();
        progressbar = (ProgressBar) l1.getChildAt(position).findViewById(R.id.progress);
        progressbar.setVisibility(View.VISIBLE);
        if (preparation != null)
            preparation = null;
        preparation = new BackgroundPrepare(test);
        preparation.execute();
        vib.vibrate(500);
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == 1) {
            this.setResult(1);
            Maliste2.this.finish();
        }
        if (resultCode == 2) {
            this.setResult(2);
            Maliste2.this.finish();
        }
    }

    // =======================================================
    // BackgroundPrepare
    // =======================================================

    public class BackgroundPrepare extends AsyncTask<Void, Integer, Void> {
        private ArrayList<String> lesAdresses = new ArrayList<String>();
        private String chapitre;

        public BackgroundPrepare(String mChapitre) {
            chapitre = mChapitre;
            // Notification.log(tag, "pos :" + position);
            try {
                remoteService.setPos(position);
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }

        protected void onPreExecute() {
            // l1.getChildAt(position).findViewById(R.id.progress).setVisibility(View.VISIBLE);
            if (miroir.getClass().getSimpleName().toString().equals("AnimeStory")
                    || miroir.getClass().getSimpleName().toString().equals("Mangafox")) {
                adresse = miroir.getImageAdr(titre, adresse, chapitre);
            } else {
                chapitre = miroir.getNomEncode(chapitre);
                adresse = miroir.getImageAdr(titre, adresse, chapitre);
            }
            if (new MangaAccess().isMe(adresse)) {
                miroir = new MangaAccess();
            }
            if (new Dbps().isMe(adresse)) {
                miroir = new Dbps();
            }
            if (new AnimeStory().isMe(adresse)) {
                miroir = new AnimeStory();
            }
            if (new Mangafox().isMe(adresse)) {
                miroir = new Mangafox();
            }
            if (new Animextremist().isMe(adresse)) {
                miroir = new Animextremist();
            }
            try {
                lesAdresses = miroir.getListImages(adresse);
                try {
                    nbpages = Integer.parseInt(StringLt.getOnlyNumerics(lesAdresses.get(0)));
                    progressbar.setMax(nbpages - 1);
                    // Notification.log("nbpages", ""+nbpages);
                } catch (Exception e) {
                    nbpages = 0;
                }
            } catch (Exception e) {
                lesAdresses.add("");
                lesAdresses.add("");
                e.printStackTrace();
            }

            if (lesAdresses.size() > 0)
                lesAdresses.remove(0);
            int numchapitre = mesmangas.get(nummanga).getChapPos(chapitre);
            mesmangas.get(nummanga).get(numchapitre).setNbpage(nbpages);
            mesmangas.get(nummanga).get(numchapitre).setChemin(adresse);
            monUtilitaire.serialiser(mesmangas);

            // \\//\\//\\//\\//\\//\\//\\
            // prepareService(monChapitre.getAbsolutePath());
            File monCache = new File("sdcard/.mesmangas");
            File monDossierManga = new File(monCache, titre);
            if (!monDossierManga.exists())
                monDossierManga.mkdir();
            File monChapitre = new File(monDossierManga, chapitre);
            if (!monChapitre.exists())
                monChapitre.mkdir();
            prepareService(monChapitre.getAbsolutePath());
            // Notification.log(tag, monChapitre.getAbsolutePath());
            // \\//\\//\\//\\//\\//\\//\\//\\//\\//\\//\\//\\//\\//\\

        }

        protected Void doInBackground(Void... params) {
            if (!this.isCancelled()) {
                mesmangas.get(nummanga).add(new Chapitre(chapitre));

                for (String string : lesAdresses) {
                    // Notification.log("adr", string);
                    String src = miroir.getImageFromPageAndWrite(string);

                    // \\//\\//\\//\\//\\//\\//\\//\\//\\//\\//\\//\\//\\//\\
                    try {
                        remoteService.addDownload(src);
                        // Notification.log(tag, "Ajout de: " + src);
                    } catch (RemoteException re) {
                        Notification.log(tag, "RemoteException");
                    }
                    // \\//\\//\\//\\//\\//\\//\\//\\//\\//\\//\\//\\//\\//\\

                }
            }
            return null;
        }

    }

    @Override protected void onResume() {
        super.onResume();
        IntentFilter filter = new IntentFilter();
        filter.addAction(CUSTOM_INTENT);
        registerReceiver(mybroadcast, filter);
    }

    @Override protected void onDestroy() {
        super.onDestroy();
        releaseService();
        stopService();
        Notification.log(tag, "onDestroy()");
    }

    @Override protected void onPause() {
        super.onPause();
        unregisterReceiver(mybroadcast);
    }

    private void startService() {
        if (!started) {
            Intent i = new Intent();
            i.setClassName("fr.letroll.mesmangas", "fr.letroll.downloadservice.Service");
            startService(i);
            started = true;
            // Notification.log(tag, "startService()");
        }

    }

    private void stopService() {
        if (started) {
            Intent i = new Intent();
            i.setClassName("fr.letroll.mesmangas", "fr.letroll.downloadservice.Service");
            stopService(i);
            started = false;
            // Notification.log(tag, "stopService()");
        }
    }

    private void bindService() {
        if (conn == null) {
            conn = new RemoteServiceConnection();
            Intent i = new Intent();
            i.setClassName("fr.letroll.mesmangas", "fr.letroll.downloadservice.Service");
            bindService(i, conn, Context.BIND_AUTO_CREATE);
            // Notification.log(tag, "bindService()");
        }
    }

    private void releaseService() {
        if (conn != null) {
            unbindService(conn);
            conn = null;
            Notification.log(tag, "releaseService()");
        }
    }

    private void prepareService(String text) {
        if (conn != null) {
            text = text + "/";
            // Notification.log(tag, text);
            try {
                remoteService.setDestinationPath(text);
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }
    }

    class RemoteServiceConnection implements ServiceConnection {
        public void onServiceConnected(ComponentName className, IBinder boundService) {
            remoteService = Iservice.Stub.asInterface((IBinder) boundService);
            Notification.log(tag, "onServiceConnected()");
            // if (nbpages == 0) {
            // maprepa.execute();
            // }
        }

        public void onServiceDisconnected(ComponentName className) {
            remoteService = null;
            Notification.log(tag, "onServiceDisconnected");
        }
    };

    class DataReceiver extends BroadcastReceiver {

        @Override public void onReceive(Context context, Intent intent) {
            Bundle bundle = intent.getExtras();
            // Notification.log(tag,"nbpages : "+nbpages);
            if (bundle != null) {
                url = bundle.getString("url");
                result = bundle.getBoolean("downloaded");
                position = bundle.getInt("position");
                if (!url.equals(urlpast)) {
                    // Notification.log(tag, url);
                    urlpast = url;
                    if (result) {
                        i++;
                        if (progressbar == null)
                            progressbar = (ProgressBar) l1.getChildAt(position).findViewById(
                                    R.id.progress);
                        progressbar.setProgress(i);
                        if (i == nbpages - 1) {
                            progressbar.setProgress(0);
                            progressbar.setVisibility(View.INVISIBLE);
                            l1.getChildAt(position).findViewById(R.id.progressBar1)
                                    .setVisibility(View.INVISIBLE);
                            progressbar = null;
                            i = 0;
                        }
                    } else {
                        // Notification.toastc(getApplicationContext(),
                        // "r�cup�ration d une image inachev� :-(");
                    }
                }
            }
        }
    }

}