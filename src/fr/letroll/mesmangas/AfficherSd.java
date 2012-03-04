package fr.letroll.mesmangas;

import java.io.File;
import java.util.ArrayList;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.preference.PreferenceManager;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.ads.AdRequest;
import com.google.ads.AdView;

import fr.letroll.framework.FileLt;
import fr.letroll.framework.Notification;

public class AfficherSd extends Activity implements OnClickListener {
    private String chemin, pub;
    private int nbpages, pageEnCour, orientation, zoom;
    private File monDossierManga;
    private SharedPreferences preferences;
    private ImageView next, before;
    private CustomWebView w1;
    private String[] desFichiers;
    private ArrayList<String> mesPages;

    protected void onCreate(android.os.Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.affichage);
        preferences = PreferenceManager.getDefaultSharedPreferences(this);
        pub = preferences.getString("publicite", "test");
        orientation = Integer.parseInt(preferences.getString("orientation", "3"));
        if (!pub.equals("letroll59")) {
            // Create the adView
            AdView adView = (AdView) findViewById(R.id.adView);
            adView.loadAd(new AdRequest());
        }

        chemin = this.getIntent().getExtras().getString("folder");

        zoom = 0;

        w1 = (CustomWebView) findViewById(R.id.webView1);
        next = (ImageView) findViewById(R.id.imageView3);
        before = (ImageView) findViewById(R.id.imageView2);
        ProgressBar p1 = (ProgressBar) findViewById(R.id.progressBar1);
        p1.setVisibility(View.GONE);

        w1.getSettings().setBuiltInZoomControls(true);

        // \\ //\\ //\\ //\\ //\\ //\\ //\\ //\\ //\\ //\\ //\\ //\\ //\\
        // w1.getSettings().setDefaultZoom(WebSettings.ZoomDensity.FAR);
        // \\ //\\ //\\ //\\ //\\ //\\ //\\ //\\ //\\ //\\ //\\ //\\ //\\ //\\
        w1.setInitialScale(100);
        w1.getSettings().setUseWideViewPort(true);

        next.setOnClickListener(this);
        before.setOnClickListener(this);
        mesPages = new ArrayList<String>();
        pageEnCour = 0;
        monDossierManga = new File(chemin);
        desFichiers = monDossierManga.list();

        filterFile();
        if (nbpages > 0) {
            loadFirstPage();
        } else {
            Notification.toastc(this, getString(R.string.pasdepage));
        }
    }

    private void filterFile() {
        for (String string : desFichiers) {
            String ext = FileLt.getExt(string);
            if (ext.equalsIgnoreCase("jpg") || ext.equalsIgnoreCase("bmp") || ext.equalsIgnoreCase("png") || ext.equalsIgnoreCase("gif")) {
                mesPages.add(string);
            }
        }
        nbpages = mesPages.size();
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

    }

    public void affichage(String page) {
        final String URI_PREFIX = "file:///";
        String html = new String();
        html = ("<html><body><img src=\"" + URI_PREFIX + page + "\" align=left></body></html>");
        w1.setInitialScale(zoom);
        w1.loadDataWithBaseURL(URI_PREFIX, html, "text/html", "utf-8", "");
    }

    public void onActionBarButtonExitClick(View v) {
        this.setResult(1);
        AfficherSd.this.finish();
    }

    public void onActionBarButtonBackClick(View v) {
        AfficherSd.this.finish();
    }

    public void backhome(View v) {
        this.setResult(2);
        AfficherSd.this.finish();
    }

    public void loadFirstPage() {
        affichage(chemin + mesPages.get(pageEnCour));
    }

    public void loadNextPage() {
        if (pageEnCour < nbpages - 1) {
            pageEnCour++;
            zoom = (int) (100 * w1.getScale());
            affichage(chemin + mesPages.get(pageEnCour));
        } else {
            Toast.makeText(AfficherSd.this, getString(R.string.dernierepage), Toast.LENGTH_SHORT).show();
        }
    }

    public void loadPreviousPage() {
        if (pageEnCour > 0) {
            pageEnCour--;
            zoom = (int) (100 * w1.getScale());
            affichage(chemin + mesPages.get(pageEnCour));
        } else {
            Toast.makeText(AfficherSd.this, getString(R.string.premierepage), Toast.LENGTH_SHORT).show();
        }

    }

    public void onClick(View v) {
        if (v.getId() == next.getId()) {
            loadNextPage();
        }
        if (v.getId() == before.getId()) {
            loadPreviousPage();
        }
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == 1) {
            AfficherSd.this.finish();
        }
    }

}
