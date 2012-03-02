package fr.letroll.mesmangas.plugin;

import java.io.ObjectInputStream.GetField;
import java.util.List;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.IBinder;
import android.os.RemoteException;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import fr.letroll.framework.Notification;
import fr.letroll.mesmangas.R;

public class InvokeMethode extends Activity implements OnClickListener {

    @Override public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent i = getIntent();
        if (i != null) {
            category = i.getStringExtra(ListPlugin.BUNDLE_EXTRAS_CATEGORY);
        }
        setContentView(R.layout.serviceview);
        bindOpService();
        textField = (EditText) findViewById(R.id.editText1);
        textField.setOnClickListener(this);
        l1 = (ListView) findViewById(R.id.listView1);

    }

    public void onDestroy() {
        super.onDestroy();
        releaseOpService();
    }

    private void bindOpService() {
        if (category != null) {
            opServiceConnection = new OpServiceConnection();
            Intent i = new Intent(ListPlugin.ACTION_PICK_PLUGIN);
            i.addCategory(category);
            bindService(i, opServiceConnection, Context.BIND_AUTO_CREATE);
        }
    }

    private void releaseOpService() {
        unbindService(opServiceConnection);
        opServiceConnection = null;
    }

    private void calculate() {
        new InternetTask().execute();
    }

    private EditText textField;
    private ListView l1;

    private OpServiceConnection opServiceConnection;
    private ISite opService;
    private static final String tag = GetField.class.getName();
    private String category;

    class InternetTask extends AsyncTask<Void, Void, Boolean> {
        List<String> mangaList;

        @Override protected Boolean doInBackground(Void... params) {
            try {
                mangaList = opService.getMangaList();
                if (mangaList != null) {
                    int size = mangaList.size();
                    Notification.log(tag, "" + size);
                    Boolean result = size > 2 ? true : false;
                    return result;
                }
            } catch (RemoteException e) {
                e.printStackTrace();
            }
            return false;
        }

        @Override protected void onPostExecute(Boolean result) {
            if (result)
                l1.setAdapter(new ArrayAdapter<String>(InvokeMethode.this, android.R.layout.simple_list_item_1, mangaList));
            super.onPostExecute(result);
        }

    }

    class OpServiceConnection implements ServiceConnection {
        public void onServiceConnected(ComponentName className, IBinder boundService) {
            opService = ISite.Stub.asInterface(boundService);
            Notification.log(tag, "onServiceConnected");
        }

        public void onServiceDisconnected(ComponentName className) {
            opService = null;
            Notification.log(tag, "onServiceDisconnected");
        }
    }

    @Override public void onClick(View v) {
        calculate();
    };

}