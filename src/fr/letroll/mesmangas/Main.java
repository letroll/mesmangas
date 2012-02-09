package fr.letroll.mesmangas;

import java.io.File;

import roboguice.activity.RoboActivity;
import roboguice.inject.InjectView;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.ViewFlipper;
import fr.letroll.framework.FileLt;
import fr.letroll.framework.IntentLt;
import fr.letroll.framework.Notification;
import fr.letroll.framework.SystemInformation;
import fr.letroll.framework.Web;

public class Main extends RoboActivity {
    private File ls;

    @InjectView(R.id.buttonLeft) ImageView before;
    @InjectView(R.id.buttonRight) ImageView next;
    @InjectView(R.id.flipper) ViewFlipper mFlipper;
    @InjectView(R.id.actionbar) LinearLayout ll;
    @InjectView(R.id.btn_title_back) ImageView imback;
    @InjectView(R.id.action_one_button) Button b1;
    @InjectView(R.id.action_two_button) Button b2;
    @InjectView(R.id.action_three_button) Button b3;
    @InjectView(R.id.action_four_button) Button b4;
    @InjectView(R.id.action_five_button) Button b5;
    @InjectView(R.id.action_six_button) Button b6;

    private String path, mail;

    private SharedPreferences preferences;
    private Boolean policeperso;
    private static final int PICKFILE_RESULT_CODE = 5000;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dashboard);
        ls = this.getFilesDir();
        path = ls.getAbsolutePath() + "/mesmangas";
        AppRater.app_launched(this);

        preferences = PreferenceManager.getDefaultSharedPreferences(this);
        policeperso = preferences.getBoolean("policeperso", false);
        mail = preferences.getString("mail", "");
        int version = preferences.getInt("version", 0);
        boolean tuto = preferences.getBoolean("tuto1", true);

        if (tuto) {
            final Dialog monTuto = new Dialog(this);
            monTuto.setContentView(R.layout.tuto_dialog1);
            Button b1 = (Button) monTuto.findViewById(R.id.button1);
            b1.setText("quitter");
            b1.setOnClickListener(new OnClickListener() {
                public void onClick(View v) {
                    monTuto.cancel();
                }
            });

            TextView t1 = (TextView) findViewById(R.id.textView2);
            TextView t2 = (TextView) findViewById(R.id.textView1);
            t1.setText("(cette application nécessite un accés à  internet, l'utilisation du wifi est conseillé.) Bonjour et bienvenue dans mesmangas, faites défiler cette page vers le haut pour apprendre à  utiliser l'application.pour commencer, appuyer sur ");
            t2.setText("selectionner la langue à  votre convenance, et enfin patienté. L'application Mesmangas va chercher tous les titres disponibles sur le site internet sélectionné. Votre manga trouvé, appuyer sur son nom pour l'ajouter à votre liste de lecture. Il ne vous reste plus qu'à regarder vos manga en appuyant sur");
            monTuto.show();
            SharedPreferences.Editor editor = preferences.edit();
            editor.putBoolean("tuto1", false).commit();
        }

        if (version != SystemInformation.getVersion(this)) {
            FileLt.recursiveDelete(new File("sdcard/.mesmangas"));
            FileLt.recursiveDelete(new File(path));
            AlertDialog alerte = new AlertDialog.Builder(Main.this)
                    .setIcon(R.drawable.ic_title_refresh)
                    .setTitle(R.string.miseajour)
                    .setMessage(
                            "* reparation de la récupération depuis animes-story suite aux mises à  jour du site\n\n* correction d'un bug lors de l'affichage des pages\n\n* ajout d'un site source espagnol\n* Je recherche des personnes désirant m'aider à  la traduction de l'application dans d'autres langues\n\n"
                                    + "* Je cherche aussi un nouveau logo pour l'application, vous pouvez m'envoyer vos idée ;-)").setNeutralButton(R.string.fermer, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int whichButton) {
                            SharedPreferences.Editor editor = preferences.edit();
                            editor.putInt("version", SystemInformation.getVersion(Main.this)).commit();
                            editor.putString("mail", SystemInformation.getMailsUser(Main.this).get(0)).commit();
                        }
                    }).create();
            alerte.show();
        }

        else {
            SharedPreferences.Editor editor = preferences.edit();
            editor.putInt("version", 0).commit();
        }

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

        @Override protected Boolean doInBackground(Void... arg0) {
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

        @Override protected void onPostExecute(Boolean result) {
            if (test) {
                DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
                    @Override public void onClick(DialogInterface dialog, int which) {
                        switch (which) {
                        case DialogInterface.BUTTON_POSITIVE:
                            // Yes button clicked
                            IntentLt.openMarketApp(Main.this);
                            break;

                        case DialogInterface.BUTTON_NEGATIVE:
                            // No button clicked
                            break;
                        }
                    }
                };

                AlertDialog.Builder builder = new AlertDialog.Builder(Main.this);
                builder.setMessage(getString(R.string.miseajour2)).setPositiveButton(getString(R.string.oui), dialogClickListener).setNegativeButton(getString(R.string.non), dialogClickListener).show();
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
        /* Create the Intent */
        final Intent emailIntent = new Intent(android.content.Intent.ACTION_SEND);

        /* Fill it with Data */
        emailIntent.setType("plain/text");
        emailIntent.putExtra(android.content.Intent.EXTRA_EMAIL, new String[] { "julien.quievreux@gmail.com" });
        emailIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, "Mesmangas");
        // emailIntent.putExtra(android.content.Intent.EXTRA_TEXT, "Text");

        /* Send it off to the Activity-Chooser */
        this.startActivity(Intent.createChooser(emailIntent, "Send mail..."));
    }

    public void onActionBarButtonClick(View v) {
        Main.this.finish();
    }

    public void onActionBarButtonExitClick(View v) {
        Main.this.finish();
    }

    public void onActionBarButtonBackClick(View v) {}

    public void backhome(View v) {}

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
        case 1000:
            if (resultCode == 1)
                Main.this.finish();

            break;
        case 2000:
            if (resultCode == 1)
                Main.this.finish();
            break;
        case 3000:
            if (resultCode == 1)
                Main.this.finish();
            break;
        case 4000:
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
