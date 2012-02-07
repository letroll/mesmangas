package fr.letroll.mesmangas;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

public class Ajout1 extends Activity{
    
    // variables
    private String path;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dashboardadd);
        path = this.getIntent().getExtras().getString("path");
    }

    public void onActionAddFrance(View v) {
        Intent intentAjout = new Intent(this, Ajout2.class);
        intentAjout.putExtra("path", path);
        intentAjout.putExtra("pays", "fr");
        this.startActivityForResult(intentAjout, 1000);
    }
    
    public void onActionAddAngleterre(View v) {
        Intent intentAjout = new Intent(this, Ajout2.class);
        intentAjout.putExtra("path", path);
        intentAjout.putExtra("pays", "en");
        this.startActivityForResult(intentAjout, 1000);
    }
    
    public void onActionAddEspagne(View v) {
        Intent intentAjout = new Intent(this, Ajout2.class);
        intentAjout.putExtra("path", path);
        intentAjout.putExtra("pays", "sp");
        this.startActivityForResult(intentAjout, 1000);
    }
    
    public void onActionBarButtonExitClick(View v) {
        this.setResult(1);
        Ajout1.this.finish();
    }

    public void onActionBarButtonBackClick(View v) {
        Ajout1.this.finish();
    }

    public void backhome(View v) {
        Ajout1.this.finish();
    }
    
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == 1) {
            this.setResult(1);
            Ajout1.this.finish();
        }
        if (resultCode == 2) {
            Ajout1.this.finish();
        }
    }
}
