package fr.letroll.adapter;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.widget.Toast;

public class DBAdapter {

    DatabaseHelper DBHelper;
    Context context;
    SQLiteDatabase db;

    public DBAdapter(Context context) {
        this.context = context;
        DBHelper = new DatabaseHelper(context);
    }

    public class DatabaseHelper extends SQLiteOpenHelper {

        Context context;

        public DatabaseHelper(Context context) {
            super(context, "mesmangas.db", null, 1);
            this.context = context;
        }

        @Override public void onCreate(SQLiteDatabase db) {
            db.execSQL("create table site (nom text primary key, adresse text not null, langue text not null, favicon text);");
        }

        @Override public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            Toast.makeText(context, "Mise à jour de la Base de données version " + oldVersion + " vers " + newVersion, Toast.LENGTH_SHORT).show();
            db.execSQL("DROP TABLE IF EXISTS site");
            onCreate(db);
        }

    }

    public DBAdapter open() {
        db = DBHelper.getWritableDatabase();
        return this;
    }

    public void close() {
        db.close();
    }

    public void Truncate() {
        db.execSQL("DELETE FROM mesmangas");
    }

    public void Truncate(String table) {
        db.execSQL("DELETE FROM " + table);
    }

    public void Drop() {
        db.execSQL("DROP TABLE IF EXISTS mesmangas");
    }

    public void Drop(String table) {
        db.execSQL("DROP TABLE IF EXISTS " + table);
    }

    public long insererUnSite(String nom, String adresse, String langue, String favicon) {
        ContentValues values = new ContentValues();
        values.put("nom", nom);
        values.put("adresse", adresse);
        values.put("langue", langue);
        if(favicon!=null)values.put("favicon", favicon);
        return db.insert("site", null, values);
    }

    public boolean supprimerSite(String nom) {
        return db.delete("site", "nom=" + nom, null) > 0;
    }

    public Cursor recupererLaListeDesSites() {
        return db.query("site", new String[] { "nom", "adresse", "langue", "favicon" }, null, null, null, null, null);
    }

}
