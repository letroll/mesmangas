package fr.letroll.mesmangas;

import java.io.File;
import java.io.IOException;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.xmlpull.v1.XmlSerializer;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.PixelFormat;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Xml;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.view.WindowManager.LayoutParams;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import fr.letroll.framework.Notification;
import fr.letroll.framework.ViewUtils;
import fr.letroll.framework.Web;
import fr.letroll.mesmangas.parcelle.Manga;
import fr.letroll.mesmangas.parcelle.Mesmangas;
import fr.letroll.mesmangas.parcelle.Miroir;
import fr.letroll.mesmangas.parcelle.Miroirs;
import fr.letroll.mesmangas.parcelle.Site;
import fr.letroll.mesmangas.site.AnimeStory;
import fr.letroll.mesmangas.site.Animextremist;
import fr.letroll.mesmangas.site.Dbps;
import fr.letroll.mesmangas.site.MangaAccess;
import fr.letroll.mesmangas.site.Mangafox;

public class Ajout2 extends Activity implements OnItemSelectedListener, OnItemClickListener, OnScrollListener, TextWatcher {

    // composants
    Spinner s1;
    ListView l1;
    EditText e1;

    // variables
    private Utilitaire monUtilitaire;
    private Mesmangas mesmangas;
    private String path, pays, site, CiDateTime;
    private static final String tag = "MesMangas";
    private int numsite;
    private File maSauvegarde;
    private ArrayList<String> lesTitres;
    private ArrayList<String> lesSites;
    private ArrayAdapter<String> adapter;
    private boolean trouve;
    private Miroirs lesMiroirs;
    private Miroir miror;

    private final class RemoveWindow implements Runnable {
        public void run() {
            removeWindow();
        }
    }

    private RemoveWindow mRemoveWindow = new RemoveWindow();
    Handler mHandler = new Handler();
    private WindowManager mWindowManager;
    private TextView mDialogText;
    private boolean mShowing;
    private boolean mReady;
    private char mPrevLetter = Character.MIN_VALUE;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.ajout);

        // // affiche refresh
        // LinearLayout ll = (LinearLayout) findViewById(R.id.actionbar);
        // ImageView imrefresh = (ImageView)
        // ll.findViewById(R.id.btn_title_refresh);
        // imrefresh.setVisibility(View.VISIBLE);
        // // ===============================

        // using Calendar class
        Calendar ci = Calendar.getInstance();

        CiDateTime = "" + ci.get(Calendar.YEAR) + "-" + (ci.get(Calendar.MONTH) + 1) + "-" + ci.get(Calendar.DAY_OF_MONTH);// +
                                                                                                                           // " "
                                                                                                                           // +
        // ci.get(Calendar.HOUR) + ":" +
        // ci.get(Calendar.MINUTE) + ":" +
        // ci.get(Calendar.SECOND);
        Notification.log("date", CiDateTime);

        mWindowManager = (WindowManager) getSystemService(Context.WINDOW_SERVICE);

        path = this.getIntent().getExtras().getString("path");
        pays = this.getIntent().getExtras().getString("pays");

        monUtilitaire = new Utilitaire(path);
        // if (!SystemInformation.IsConnectedToNetwork(this))
        // Toast.makeText(this, getString(R.string.netnecessaire),
        // Toast.LENGTH_LONG).show();
        mesmangas = new Mesmangas();
        maSauvegarde = new File(path);
        lesTitres = new ArrayList<String>();
        lesSites = new ArrayList<String>();
        lesMiroirs = new Miroirs();

        // site
        if (pays.equals("fr")) {
            lesSites.addAll(lesMiroirs.getMiroirs("fr"));
        }
        if (pays.equals("en")) {
            lesSites.addAll(lesMiroirs.getMiroirs("en"));
        }
        if (pays.equals("sp")) {
            lesSites.addAll(lesMiroirs.getMiroirs("sp"));
        }

        if (maSauvegarde.exists()) {
            mesmangas = monUtilitaire.deserializeObject();
        }

        e1 = (EditText) findViewById(R.id.EditText01);
        e1.addTextChangedListener(this);

        l1 = (ListView) findViewById(R.id.listView1);
        l1.setOnScrollListener(this);
        s1 = (Spinner) findViewById(R.id.spinner1);
        adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, lesSites);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        s1.setAdapter(adapter);

        LayoutInflater inflate = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        mDialogText = (TextView) inflate.inflate(R.layout.list_position, null);
        mDialogText.setVisibility(View.INVISIBLE);
        mHandler.post(new Runnable() {
            public void run() {
                mReady = true;
                WindowManager.LayoutParams lp = new WindowManager.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT, WindowManager.LayoutParams.TYPE_APPLICATION, WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE
                        | WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE, PixelFormat.TRANSLUCENT);
                mWindowManager.addView(mDialogText, lp);
            }
        });

        s1.setOnItemSelectedListener(this);
        l1.setOnItemClickListener(this);
        l1.setTextFilterEnabled(true);
    }

    public void onActionBarButtonExitClick(View v) {
        this.setResult(1);
        Ajout2.this.finish();
    }

    public void onActionBarButtonBackClick(View v) {
        Ajout2.this.finish();
    }

    // public void onActionBarButtonRefreshClick(View v) {
    // //
    // }

    public void backhome(View v) {
        this.setResult(2);
        Ajout2.this.finish();
    }

    public void hideKeyboard(View v) {
        ViewUtils.hideKeyboard(this, e1);
    }

    public void onSiteWithSelect() {
        if (pays.equals("fr")) {
            switch (numsite) {
            case 0:
                miror = new AnimeStory();
                break;
            case 1:
                miror = new Dbps();
                break;
            }
        }
        if (pays.equals("en")) {
            switch (numsite) {
            case 0:
                miror = new MangaAccess();
                break;
            case 1:
                miror = new Mangafox();
                break;
            }
        }
        if (pays.equals("sp")) {
            switch (numsite) {
            case 0:
                miror = new Animextremist();
                break;
            // case 1:
            // miror = new Mangafox(path);
            // break;
            }
        }
        new getMangaTask().execute();
    }

    public class getMangaTask extends AsyncTask<Void, Void, Boolean> {
        ProgressDialog dialog = new ProgressDialog(Ajout2.this);

        protected void onPreExecute() {
            super.onPreExecute();
            // Dialogue d'attente
            dialog.setMessage(getString(R.string.chargement));
            dialog.setCancelable(true);
            dialog.show();
            lesTitres.clear();
        }

        protected Boolean doInBackground(Void... arg0) {

            // \\ //\\ //\\ //\\ //\\ //\\ //\\ //\\ //\\ //\\ //\\//\\
             List<NameValuePair> params = new ArrayList<NameValuePair>();
             params.add(new BasicNameValuePair("site", miror.getNomDuSite()));
             Notification.log(tag, miror.getNomDuSite());
             String test=Web.GetHTML("http://letroll.alwaysdata.net/getlist.php",params);
             Notification.log(tag, test);
            // \\ //\\ //\\ //\\ //\\ //\\ //\\ //\\ //\\ //\\ //\\//\\

            lesTitres = miror.getMangaList();
            if (lesTitres.size() > 2) {
                return true;
            } else {
                return false;
            }
        }

        protected void onPostExecute(Boolean result) {
            super.onPostExecute(result);
            dialog.dismiss();
            l1.setAdapter(new ArrayAdapter<String>(Ajout2.this, android.R.layout.simple_list_item_1, lesTitres));
            ViewUtils.hideKeyboard(Ajout2.this, e1);
            // Notification.log("remplissage","size:lesTitres= "+lesTitres.size());

            if (result) {

                // \\ //\\ //\\ //\\ //\\ //\\ //\\ //\\ //\\ //\\ //\\ //\\
                List<NameValuePair> params = new ArrayList<NameValuePair>();
                params.add(new BasicNameValuePair("date", CiDateTime));
                params.add(new BasicNameValuePair("site", miror.getNomDuSite()));
                String xml=writeUsingXMLSerializer(lesTitres);
                params.add(new BasicNameValuePair("list", xml));
                Web.GetHTML("http://letroll.alwaysdata.net/addlist.php", params,"android");
                // \\ //\\ //\\ //\\ //\\ //\\ //\\ //\\ //\\ //\\ //\\ //\\

                try {
                    mesmangas.addSite(new Site(site, lesTitres));
                } catch (Exception e) {
                    mesmangas = monUtilitaire.deserializeObject();
                    mesmangas.addSite(new Site(site, lesTitres));
                    // e.printStackTrace();
                }
                monUtilitaire.serialiser(mesmangas);
            } else {

            }

        }

    }

    public static String writeUsingXMLSerializer(ArrayList<String> list) {
        XmlSerializer xmlSerializer = Xml.newSerializer();
        StringWriter writer = new StringWriter();

        try {
            xmlSerializer.setOutput(writer);
            xmlSerializer.startDocument("UTF-8", true);

            for (String string : list) {
                xmlSerializer.startTag("", "manga");
                xmlSerializer.text(string);
                xmlSerializer.endTag("", "manga");
            }

            xmlSerializer.endDocument();
        } catch (IllegalArgumentException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IllegalStateException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        return writer.toString();
    }

    public void remplissage() {
        int l;
        try {
            l = mesmangas.sizeSite();
        } catch (Exception e) {
            l = 0;
        }

        if (l == 0) {
            // Notification.log("remplissage","pas de site deja trouve");
            onSiteWithSelect();
        } else {
            // Notification.log("remplissage","des sites ont deja etaient ajoute");
            trouve = false;
            int i = 0;
            mesmangas = monUtilitaire.deserializeObject();
            for (String monsite : mesmangas.getMesAdresseSite()) {
                if (site.contains(monsite)) {
                    trouve = true;
                    // Notification.log("remplissage","site retrouve");
                    lesTitres = mesmangas.getS(i).getTitre();
                    // Notification.log("remplissage","size:lesTitres= "+lesTitres.size());
                    l1.setAdapter(new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, lesTitres));
                    ViewUtils.hideKeyboard(Ajout2.this, e1);
                    break;
                }
                i++;
            }
            if (!trouve) {
                // Notification.log("remplissage","pas trouve ajoute");
                onSiteWithSelect();
            }
        }
    }

    public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
        site = parent.getItemAtPosition(pos).toString();
        // Notification.log("site", site);
        numsite = pos;
        remplissage();
    }

    public void onNothingSelected(AdapterView<?> parent) {
    }

    public void onItemClick(AdapterView<?> parent, View vue, int position, long id) {

        mesmangas.add(new Manga(l1.getItemAtPosition(position).toString(), site));

        monUtilitaire.serialiser(mesmangas);
        setResult(2);
        Ajout2.this.finish();
    }

    @Override
    protected void onResume() {
        super.onResume();
        mReady = true;
    }

    @Override
    protected void onPause() {
        super.onPause();
        removeWindow();
        mReady = false;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mWindowManager.removeView(mDialogText);
        mReady = false;
    }

    public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
        if (mReady) {
            char firstLetter = '0';
            try {
                firstLetter = lesTitres.get(firstVisibleItem).charAt(0);
            } catch (Exception e) {
            }

            if (!mShowing && firstLetter != mPrevLetter) {
                mShowing = true;
                mDialogText.setVisibility(View.VISIBLE);
            }
            mDialogText.setText(((Character) firstLetter).toString());
            mHandler.removeCallbacks(mRemoveWindow);
            mHandler.postDelayed(mRemoveWindow, 1500);
            mPrevLetter = firstLetter;
        }
    }

    public void onScrollStateChanged(AbsListView view, int scrollState) {
    }

    private void removeWindow() {
        if (mShowing) {
            mShowing = false;
            mDialogText.setVisibility(View.INVISIBLE);
        }
    }

    @Override
    public void afterTextChanged(Editable arg0) {
    }

    @Override
    public void beforeTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) {
    }

    @Override
    public void onTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) {
        int textlength = e1.getText().length();
        ArrayList<String> arr_sort = new ArrayList<String>();

        // lesTitres.clear();
        for (int i = 0; i < lesTitres.size(); i++) {
            if (textlength <= lesTitres.get(i).length()) {
                if (e1.getText().toString().equalsIgnoreCase(lesTitres.get(i).substring(0, textlength))) {
                    arr_sort.add(lesTitres.get(i));
                }
            }
        }

        l1.setAdapter(new ArrayAdapter<String>(Ajout2.this, android.R.layout.simple_list_item_1, arr_sort));
    }

}
