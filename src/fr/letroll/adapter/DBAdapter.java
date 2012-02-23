package fr.letroll.adapter;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.widget.Toast;

public class DBAdapter {

    DatabaseHelper  DBHelper;
    Context         context;
    SQLiteDatabase  db;
    
    public DBAdapter(Context context){
        this.context = context;
        DBHelper = new DatabaseHelper(context);
    }   
    
    public class DatabaseHelper extends SQLiteOpenHelper{

        Context         context;
        
        public DatabaseHelper(Context context) {
            super(context, "mesmangas", null, 1);
            this.context = context;
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
            db.execSQL("create table site (nom text primary key, adresse text not null, favicon text);");            
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            Toast.makeText(context, "Mise à jour de la Base de données version "+oldVersion+" vers "+newVersion, Toast.LENGTH_SHORT).show();
            db.execSQL("DROP TABLE IF EXISTS site");
            onCreate(db);
        }
        
    }
    
    public DBAdapter open(){
        db = DBHelper.getWritableDatabase();
        return this;
    }
    
    public void close(){
        db.close();
    }
    
    public void Truncate(){
        db.execSQL("DELETE FROM table");
    }
    
    public void Drop(){
        db.execSQL("DROP TABLE IF EXISTS mesmangas");
    }
    
//    public long insererUnProduit(String codeBarre, String titre, String description){
//        ContentValues values = new ContentValues();
//        values.put("codebarre", codeBarre);
//        values.put("titre", titre);
//        values.put("description", description);
//        return db.insert("produits", null, values);
//    }
//    
//    
//    public boolean supprimerProduit(long id){
//        return db.delete("produits", "_id="+id, null)>0;
//    }
//    
//    public Cursor recupererLaListeDesProduits(){
//        return db.query("produits", new String[]{
//                "_id",
//                "codebarre",
//                "titre",
//                "description"}, null, null, null, null, null);
//    }
    
}
