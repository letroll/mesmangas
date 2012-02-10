package fr.letroll.mesmangas;

import java.io.File;
import java.util.ArrayList;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.IBinder;
import android.os.RemoteException;
import android.os.Vibrator;
import android.preference.PreferenceManager;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;

import com.google.ads.AdRequest;
import com.google.ads.AdView;

import fr.letroll.downloadservice.Iservice;
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

public class Afficher extends Activity implements OnClickListener {
    private String path, chapitre, manga, chemin, pub, url, urlpast = "";
    private int nbpages;
    private File monCache, monDossierManga, monChapitre;
    private AdView mAdView;
    private SharedPreferences preferences;
    private int numchapitre, nummanga;
    private ProgressBar p1;
    private ImageView next, before;
    private CustomWebView w1;
    private Mesmangas mesmangas;
    private Utilitaire monUtilitaire;
    private BackgroundPrepare maprepa;
    private Boolean pleinecran, swype, result;
    private int orientation, i = 0;
    private Vibrator vib;

    // \\//\\//\\//\\//\\//\\//\\//\\//\\//\\//\\//\\//\\//\\//\\//\\//\\//\\
    private Iservice remoteService;
    private boolean started = false;
    private RemoteServiceConnection conn = null;
    private String tag = "downloadservice";
    private final BroadcastReceiver mybroadcast = new DataReceiver();
    public static final String CUSTOM_INTENT = "fr.letroll.mesmangas.TEST";
    // \\//\\//\\//\\//\\//\\//\\//\\//\\//\\//\\//\\//\\//\\//\\//\\//\\//\\//\\//\\//\\//\\

    // site
    private Miroir miroir;

    protected void onCreate(android.os.Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        vib = (Vibrator) getSystemService(Afficher.VIBRATOR_SERVICE);
        preferences = PreferenceManager.getDefaultSharedPreferences(this);
        pub = preferences.getString("publicite", "test").toString();
        pleinecran = preferences.getBoolean("fullscreen", false);
        swype = preferences.getBoolean("swype", false);

        orientation = Integer.parseInt(preferences.getString("orientation", "3"));

        if (pleinecran) {
            requestWindowFeature(Window.FEATURE_NO_TITLE);
            this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                    WindowManager.LayoutParams.FLAG_FULLSCREEN);
        }

        setContentView(R.layout.affichage);

        if (!pub.equals("letroll59")) {
            // Create the adView
            mAdView = (AdView) findViewById(R.id.adView);
            mAdView.loadAd(new AdRequest());
        }

        path = this.getIntent().getExtras().getString("path");
        chemin = this.getIntent().getExtras().getString("chemin");
        manga = this.getIntent().getExtras().getString("manga");
        nummanga = this.getIntent().getExtras().getInt("nummanga");
        chapitre = this.getIntent().getExtras().getString("chapitre");
        numchapitre = this.getIntent().getExtras().getInt("numchapitre");

        maprepa = new BackgroundPrepare();

        mesmangas = new Mesmangas();
        monUtilitaire = new Utilitaire(path);

        // Notification.log("numchapitre", "" + numchapitre);
        if (!SystemInformation.IsConnectedToNetwork(this))
            Notification.toastc(this, getString(R.string.netnecessaire));
        mesmangas = monUtilitaire.deserializeObject();

        p1 = (ProgressBar) findViewById(R.id.progressBar1);
        w1 = (CustomWebView) findViewById(R.id.webView1);

        if (swype) {
            w1.setSwype(true);
        }

        next = (ImageView) findViewById(R.id.imageView3);
        before = (ImageView) findViewById(R.id.imageView2);

        w1.getSettings().setBuiltInZoomControls(true);
        w1.setInitialScale(0);
        w1.getSettings().setUseWideViewPort(true);

        // affiche refresh
        LinearLayout l2 = (LinearLayout) findViewById(R.id.actionbar);
        ImageView imrefresh = (ImageView) l2.findViewById(R.id.btn_title_refresh);
        imrefresh.setVisibility(View.VISIBLE);
        // ===============================

        next.setOnClickListener(this);
        before.setOnClickListener(this);

        nbpages = 0;
        try {
            monCache = new File("sdcard/.mesmangas");
            if (!monCache.exists())
                monCache.mkdir();
            monDossierManga = new File(monCache, manga);
            if (!monDossierManga.exists())
                monDossierManga.mkdir();

            monChapitre = new File(monDossierManga, chapitre);
            if (!monChapitre.exists())
                monChapitre.mkdir();
        } catch (Exception e) {
            e.printStackTrace();
        }
        // ========================================================================
        // verifie si le manga est deja dispo au quel cas on ne le retelecharge
        // pas
        // ========================================================================
        // calcul du nbr de page du chapitre
        try {
            nbpages = mesmangas.get(nummanga).get(numchapitre).getNbpage();
            // Notification.log("nbpages", ""+nbpages);
        } catch (Exception e) {
            nbpages = 0;
        }

        if (nbpages == 0) {
            p1.setProgress(0);
        } else {
            p1.setVisibility(View.GONE);

            // Notification.log("affichage", "chapitre deja present");
            if (monChapitre.list().length >= 1)
                w1.init(manga, chapitre, nbpages);
        }

        // \\//\\//\\//\\//\\//\\//\\
        startService();
        bindService();
        // \\//\\//\\//\\//\\//\\//\\//\\//\\//\\//\\//\\//\\//\\

    }

    protected void onResume() {
        super.onResume();
        switch (orientation) {
        case 1:
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
            break;
        case 2:
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
            break;
        case 3:
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR);
            break;
        }
        IntentFilter filter = new IntentFilter();
        filter.addAction(CUSTOM_INTENT);
        registerReceiver(mybroadcast, filter);
    }

    @Override protected void onDestroy() {
        super.onDestroy();
        releaseService();
        stopService();
        miroir = null;
        Notification.log(tag, "onDestroy()");
    }

    @Override protected void onPause() {
        super.onPause();
        unregisterReceiver(mybroadcast);
    }

    @Override public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
    }

    public void onActionBarButtonExitClick(View v) {
        this.setResult(1);
        maprepa.cancel(true);
        stopService();
        miroir = null;
        Afficher.this.finish();
    }

    public void onActionBarButtonBackClick(View v) {
        maprepa.cancel(true);
        stopService();
        miroir = null;
        Afficher.this.finish();
    }

    public void backhome(View v) {
        this.setResult(2);
        stopService();
        miroir = null;
        Afficher.this.finish();
    }

    // =======================================================
    // BackgroundPrepare
    // =======================================================

    public class BackgroundPrepare extends AsyncTask<Void, Integer, Void> {
        private ArrayList<String> lesAdresses = new ArrayList<String>();

        protected void onPreExecute() {
            p1.setVisibility(View.VISIBLE);
            
            if (new MangaAccess().isMe(chemin)) {
                miroir = new MangaAccess();
            }
            if (new Dbps().isMe(chemin)) {
                miroir = new Dbps();
            }
            if (new AnimeStory().isMe(chemin)) {
                miroir = new AnimeStory();
            }
            if (new Mangafox().isMe(chemin)) {
                miroir = new Mangafox();
            }
            if (new Animextremist().isMe(chemin)) {
                miroir = new Animextremist();
            }

            try {
                lesAdresses = miroir.getListImages(chemin);
                try {
                    nbpages = Integer.parseInt(StringLt.getOnlyNumerics(lesAdresses.get(0)));
                    p1.setMax(nbpages);
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
            mesmangas.get(nummanga).get(numchapitre).setNbpage(nbpages);
            mesmangas.get(nummanga).get(numchapitre).setChemin(chemin);
            monUtilitaire.serialiser(mesmangas);

            // \\//\\//\\//\\//\\//\\//\\
            prepareService(monChapitre.getAbsolutePath());
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

    public void onClick(View v) {

        if (v.getId() == next.getId()) {
            if (w1 != null) {
                if (!w1.loadNextPage()) {
                    vib.vibrate(500);
                }
            } else {
                w1 = (CustomWebView) findViewById(R.id.webView1);
                if (!w1.loadNextPage()) {
                    vib.vibrate(500);
                }
            }

        }
        if (v.getId() == before.getId()) {
            if (w1 != null) {
                if (!w1.loadPreviousPage()) {
                    vib.vibrate(500);
                }
            } else {
                w1 = (CustomWebView) findViewById(R.id.webView1);
                if (!w1.loadPreviousPage()) {
                    vib.vibrate(500);
                }
            }

        }
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == 1) {
            Afficher.this.finish();
        }
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
            Notification.log(tag, text);
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
            if (nbpages == 0) {
                maprepa.execute();
            }
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
                if (!url.equals(urlpast)) {
                    // Notification.log(tag, url);
                    urlpast = url;
                    if (result) {
                        i++;
                        p1.setProgress(i);
                        // Notification.log(tag, "" + i);
                        if (i == 1)
                            if (monChapitre.list().length > 0)
                                w1.init(manga, chapitre, nbpages);
                    } else {
                        // Notification.toastc(getApplicationContext(),
                        // "récupération d une image inachevé :-(");
                    }
                    if (i == nbpages - 1) {
                        Notification.toastc(Afficher.this, getString(R.string.recuperationfinie));
                        p1.setVisibility(View.GONE);
                    }
                }
            }
        }
    }

    public void onActionBarButtonRefreshClick(View v) {
        if (maprepa.isCancelled())
            maprepa.execute();
    }

}
