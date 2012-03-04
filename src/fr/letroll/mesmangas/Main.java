package fr.letroll.mesmangas;

import java.io.File;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.util.List;

import roboguice.activity.RoboFragmentActivity;
import roboguice.inject.InjectView;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ViewFlipper;
import fr.letroll.framework.FileLt;
import fr.letroll.framework.IntentLt;
import fr.letroll.framework.Notification;
import fr.letroll.framework.SystemInformation;
import fr.letroll.framework.Web;
import fr.letroll.mesmangas.plugin.ListPlugin;

public class Main extends RoboFragmentActivity {
    private File ls;

    @InjectView(R.id.buttonLeft)
    ImageView before;
    @InjectView(R.id.buttonRight)
    ImageView next;
    @InjectView(R.id.flipper)
    ViewFlipper mFlipper;
    @InjectView(R.id.actionbar)
    LinearLayout ll;
    @InjectView(R.id.btn_title_back)
    ImageView imback;
    @InjectView(R.id.action_one_button)
    Button b1;
    @InjectView(R.id.action_two_button)
    Button b2;
    @InjectView(R.id.action_three_button)
    Button b3;
    @InjectView(R.id.action_four_button)
    Button b4;
    @InjectView(R.id.action_five_button)
    Button b5;
    @InjectView(R.id.action_six_button)
    Button b6;
    // @InjectView(R.id.textView1) TextView t2;
    // @InjectView(R.id.textView2) TextView t1;

    // constants
    // private static final String tag = "MesMangas";
    private static final int PICKFILE_RESULT_CODE = 5000;
    // variables
    private String path, mail;
    private Boolean policeperso, DEVELOPER_MODE;
    private SharedPreferences preferences;

    // view

    protected void addApplicationModules(List<MonModule> modules) {
        // add your module with custom bindings
        modules.add(new MonModule());
    }

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dashboard);
        ls = this.getFilesDir();
        path = ls.getAbsolutePath() + "/mesmangas";
        AppRater.app_launched(this);

        preferences = PreferenceManager.getDefaultSharedPreferences(this);
        policeperso = preferences.getBoolean("policeperso", false);
        mail = preferences.getString("mail", "");
        // int version = preferences.getInt("version", 0);

        DEVELOPER_MODE = preferences.getBoolean("debug", false);
        if (DEVELOPER_MODE) {
            try {
                Class<?> strictModeClass = Class.forName("android.os.StrictMode", true, Thread.currentThread().getContextClassLoader());

                Class<?> threadPolicyClass = Class.forName("android.os.StrictMode$ThreadPolicy", true, Thread.currentThread().getContextClassLoader());

                Class<?> threadPolicyBuilderClass = Class.forName("android.os.StrictMode$ThreadPolicy$Builder", true, Thread.currentThread().getContextClassLoader());

                Method setThreadPolicyMethod = strictModeClass.getMethod("setThreadPolicy", threadPolicyClass);

                Method detectAllMethod = threadPolicyBuilderClass.getMethod("detectAll");
                Method penaltyMethod = threadPolicyBuilderClass.getMethod("penaltyLog");
                Method buildMethod = threadPolicyBuilderClass.getMethod("build");

                Constructor<?> threadPolicyBuilderConstructor = threadPolicyBuilderClass.getConstructor();
                Object threadPolicyBuilderObject = threadPolicyBuilderConstructor.newInstance();

                Object obj = detectAllMethod.invoke(threadPolicyBuilderObject);

                obj = penaltyMethod.invoke(obj);
                Object threadPolicyObject = buildMethod.invoke(obj);
                setThreadPolicyMethod.invoke(strictModeClass, threadPolicyObject);

            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }

        // boolean tuto = preferences.getBoolean("tuto1", true);
        //
        // if (tuto) {
        // final Dialog monTuto = new Dialog(this);
        // monTuto.setContentView(R.layout.tuto_dialog1);
        // Button b1 = (Button) monTuto.findViewById(R.id.button1);
        // b1.setText("quitter");
        // b1.setOnClickListener(new OnClickListener() {
        // public void onClick(View v) {
        // monTuto.cancel();
        // }
        // });
        // TextView t2= (TextView) monTuto.findViewById(R.id.textView1);
        // TextView t1= (TextView) monTuto.findViewById(R.id.textView2);
        // t1.setText("(cette application n�cessite un acc�s � internet, l'utilisation du wifi est conseill�.) Bonjour et bienvenue dans mesmangas, faites d�filer cette page vers le haut pour apprendre � utiliser l'application.pour commencer, appuyer sur ");
        // t2.setText("selectionner la langue � votre convenance, et enfin patient�. L'application Mesmangas va chercher tous les titres disponibles sur le site internet s�lectionn�. Votre manga trouv�, appuyer sur son nom pour l'ajouter � votre liste de lecture. Il ne vous reste plus qu'� regarder vos manga en appuyant sur");
        // monTuto.show();
        // SharedPreferences.Editor editor = preferences.edit();
        // editor.putBoolean("tuto1", false).commit();
        // }

        // if (version != SystemInformation.getVersion(Main.this)) {
        // FileLt.recursiveDelete(new File("sdcard/.mesmangas"));
        // FileLt.recursiveDelete(new File(path));
        // AlertDialog alerte = new AlertDialog.Builder(Main.this)
        // .setIcon(R.drawable.ic_title_refresh)
        // .setTitle(R.string.miseajour)
        // .setMessage(
        // "* reparation de la r�cup�ration depuis animes-story suite aux mises � jour du site\n\n* correction d'un bug lors de l'affichage des pages\n\n* ajout d'un site source espagnol\n* Je recherche des personnes d�sirant m'aider � la traduction de l'application dans d'autres langues\n\n"
        // +
        // "* Je cherche aussi un nouveau logo pour l'application, vous pouvez m'envoyer vos id�e ;-)")
        // .setNeutralButton(R.string.fermer, new
        // DialogInterface.OnClickListener() {
        // public void onClick(DialogInterface dialog, int whichButton) {
        // SharedPreferences.Editor editor = preferences.edit();
        // editor.putInt("version",
        // SystemInformation.getVersion(Main.this)).commit();
        // editor.putString("mail",
        // SystemInformation.getMailsUser(Main.this).get(0)).commit();
        // }
        // }).create();
        // alerte.show();
        // }

        // else {
        // SharedPreferences.Editor editor = preferences.edit();
        // editor.putInt("version", 0).commit();
        // }

        if (policeperso) {
            Typeface tf = Typeface.createFromAsset(getAssets(), "Another.ttf");
            b1.setTypeface(tf);
            b2.setTypeface(tf);
            b3.setTypeface(tf);
            b4.setTypeface(tf);
            b5.setTypeface(tf);
            b6.setTypeface(tf);
            b1.setTextSize(25);
            b2.setTextSize(25);
            b3.setTextSize(25);
            b4.setTextSize(25);
            b5.setTextSize(25);
            b6.setTextSize(25);
        }

        // cache la fleche retour pour main

        imback.setVisibility(View.GONE);
        // ===============================

        // \\ //\\ //\\ //\\ //\\ //\\ //\\ //\\ //\\ //\\ //\\ //\\ //\\
        if (!SystemInformation.IsConnectedToNetwork(this))
            Notification.toastl(this, getString(R.string.netnecessaire));
        if (!SystemInformation.isSdPresent())
            Notification.toastl(this, getString(R.string.sdnecessaire));

        CheckUpdate check = new CheckUpdate();
        check.execute();
        // \\ //\\ //\\ //\\ //\\ //\\ //\\ //\\ //\\ //\\ //\\ //\\ //\\
    }

    private class CheckUpdate extends AsyncTask<Void, Void, Boolean> {
        Boolean test;

        protected Boolean doInBackground(Void... arg0) {
            if (SystemInformation.getVersionName(Main.this).equals(Web.GetVersionOnMarket(Main.this)) && SystemInformation.getVersionName(Main.this) != "") {
                test = false;
            } else {
                test = true;
            }
            // Notification.log("test", test.toString());
            // Notification.log("test2",
            // "locale : "+SystemInformation.getVersionName(Main.this).toString()+" online : "+Web.GetVersionOnMarket(Main.this));
            return test;
        }

        protected void onPostExecute(Boolean result) {
            if (test) {
                IntentLt.openMarketApp(Main.this);
            }
            super.onPostExecute(result);
        }
    }

    public void onActionAdd(View v) {
        Intent intentAjout = new Intent(this, Ajout1.class);
        intentAjout.putExtra("path", path);
        intentAjout.putExtra("mail", mail);
        this.startActivityForResult(intentAjout, 1000);

    }

    public void onActionShow(View v) {
        Intent intentListe = new Intent(this, Maliste1.class);
        intentListe.putExtra("path", path);
        intentListe.putExtra("mail", mail);
        startActivityForResult(intentListe, 2000);
    }

    public void onActionShowSd(View v) {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("file/*");
        startActivityForResult(Intent.createChooser(intent, getString(R.string.selectionpremierepage)), PICKFILE_RESULT_CODE);
    }

    public void onActionTrashClick(View v) {
        Notification.toastc(this, getString(R.string.vidagecache));
        FileLt.recursiveDelete(new File("sdcard/.mesmangas"));
        FileLt.recursiveDelete(new File(path));
    }

    public void onActionPreferenceClick(View v) {
        Intent intentPreference = new Intent(this, Preference.class);
        this.startActivityForResult(intentPreference, 3000);
    }

    public void onActionSendClick(View v) {
        Intent intentPlugin = new Intent(this, ListPlugin.class);
        this.startActivityForResult(intentPlugin, 6000);
    }

    public void onActionBarButtonClick(View v) {
        Main.this.finish();
    }

    public void onActionBarButtonExitClick(View v) {
        Main.this.finish();
    }

    public void onActionBarButtonBackClick(View v) {
    }

    public void backhome(View v) {
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
        case 1000:
        case 2000:
        case 3000:
        case 4000:
        case 6000:
            if (resultCode == 1)
                Main.this.finish();
            break;
        case PICKFILE_RESULT_CODE:
            if (resultCode == RESULT_OK) {

                String FilePath = data.getData().getPath();
                if (FilePath.contains("mimetype"))
                    FilePath = FilePath.substring(10, FilePath.length());
                String FileName = data.getData().getLastPathSegment();
                int lastPos = FilePath.length() - FileName.length();
                String Folder = FilePath.substring(0, lastPos);

                Intent intent = new Intent(this, AfficherSd.class);
                intent.putExtra("folder", Folder);
                intent.putExtra("file", FilePath);
                intent.putExtra("path", path);
                intent.putExtra("mail", mail);
                startActivityForResult(intent, 4000);
                // monUtilitaire.showToast("filename "+FileName+" filepath "+FilePath+" folder "+Folder,
                // 1, this);

            }
            break;
        }
    }

    public void NextClick(View v) {
        mFlipper.stopFlipping();
        mFlipper.showNext();
    }

    public void BeforeClick(View v) {
        mFlipper.stopFlipping();
        mFlipper.showPrevious();
    }

    public void FlipperClick(View v) {
        switch (mFlipper.getDisplayedChild()) {
        case 0:
            IntentLt.openSite(this, "http://adep.comxa.com");
            break;
        case 1:
            final String APP_PNAME = "fr.letroll.mesmangas";
            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + APP_PNAME)));
            break;
        default:
            break;
        }
    }
}
