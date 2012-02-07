package fr.letroll.mesmangas;

import android.app.AlertDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.Preference.OnPreferenceClickListener;
import android.preference.PreferenceActivity;
import android.preference.PreferenceManager;
import android.view.KeyEvent;
import android.view.View;

public class Preference extends PreferenceActivity implements OnPreferenceClickListener {

    SharedPreferences preferences;
    Context context;
    
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.preferences);
        setContentView(R.layout.preferences);
        context = this.getBaseContext();
        preferences = PreferenceManager.getDefaultSharedPreferences(this);  
        android.preference.Preference partage = (android.preference.Preference)findPreference("partage");
        partage.setOnPreferenceClickListener(this);
    }

  //\\ //\\ //\\ //\\ //\\ //\\ //\\ //\\ //\\ //\\ //\\ //\\ //\\
//    public void onBackPressed() {
//        super.onBackPressed();
//        Preference.this.finish();
//    }
  //\\ //\\ //\\ //\\ //\\ //\\ //\\ //\\ //\\ //\\ //\\ //\\ //\\
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
            Preference.this.finish();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }
    
    public void onActionBarButtonExitClick(View v) {
        this.setResult(1);
        Preference.this.finish();
    }

    public void onActionBarButtonBackClick(View v) {
        Preference.this.finish();
    }

    public void backhome(View v) {
        Preference.this.finish();
    }

    public boolean onPreferenceClick(android.preference.Preference preference) {

        if (preference.getKey().equals("partage")) {
            AlertDialog.Builder ad = new AlertDialog.Builder(this);
            ad.setIcon(R.drawable.share);
            ad.setTitle(getString(R.string.app_name));
            ad.show();
        }
        return true;
    }
}