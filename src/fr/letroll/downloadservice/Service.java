package fr.letroll.downloadservice;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

import fr.letroll.framework.Notification;

import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.os.RemoteException;
import android.util.Log;

public class Service extends android.app.Service {

    private Handler serviceHandler;
    private int counter = 0, total, downloaded,position;
    private ArrayList<String> mDownloads;
    private Task myTask = new Task();
    private final String tag = "downloadservice";
    private String DESTINATION_PATH = "", FILE;
    // private boolean succes = true;
    public static final String CUSTOM_INTENT = "fr.letroll.mesmangas.TEST";
    private Intent i;

    @Override
    public IBinder onBind(Intent arg0) {
        Log.e(tag, "onBind()");
        return myRemoteServiceStub;
    }

    private Iservice.Stub myRemoteServiceStub = new Iservice.Stub() {
        public int getCounter() throws RemoteException {
            return counter;
        }

        public void setDestinationPath(String aPath) {
            DESTINATION_PATH = aPath;
        }

        public void addDownload(String aString) {
            mDownloads.add(aString);
        }

        public void addDownloads(List<String> list) throws RemoteException {
            mDownloads.addAll(list);
        }

        public void setPos(int aPos){
            position=aPos;
        }
        
    };

    @Override
    public void onCreate() {
        super.onCreate();
        Log.e(tag, "onCreate()");
        mDownloads = new ArrayList<String>();
        i = new Intent();
        i.setAction(CUSTOM_INTENT);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        serviceHandler.removeCallbacks(myTask);
        serviceHandler = null;
        Log.e(tag, "onDestroy()");
    }

    @Override
    public void onStart(Intent intent, int startId) {
        super.onStart(intent, startId);
        serviceHandler = new Handler();
        serviceHandler.postDelayed(myTask, 100L);
        Log.e(tag, "onStart()");
    }

    class Task implements Runnable {
        public void run() {
            ++counter;
            if (mDownloads.size() > 0) {
                if (download(mDownloads.get(0))) {
                    // succes = true;
                    mDownloads.remove(0);
                    // Toast.makeText(getApplicationContext(), "Réussi", Toast.LENGTH_SHORT).show();
                } else {
                    // if(succes)Toast.makeText(getApplicationContext(), "Raté", Toast.LENGTH_SHORT).show();
                    // succes = false;

                }
            }
            serviceHandler.postDelayed(this, 1000L);
            // Log.e(tag, "Incrementing counter in the run method");
        }
    }

    public boolean download(String aUrl) {

        downloaded = 0;
        URL url;
//        String adresse="";
//        try {
//            adresse=URLEncoder.encode(aUrl, "UTF-8");
//        } catch (UnsupportedEncodingException e1) {
//            e1.printStackTrace();
//        }
        i.putExtra("url", aUrl);
        Notification.log(tag, aUrl);
        try {
            url = new URL(aUrl);
            HttpURLConnection connection;
            try {
                connection = (HttpURLConnection) url.openConnection();

                File file = new File(DESTINATION_PATH + url.getFile());
                if (file.exists()) {
                    downloaded = (int) file.length();
                    connection.setRequestProperty("Range", "bytes=" + (file.length()) + "-");
                } else {
                    connection.setRequestProperty("Range", "bytes=" + downloaded + "-");
                }
                connection.setDoInput(true);
                connection.setDoOutput(true);
                total = connection.getContentLength();

                getApplicationContext().sendBroadcast(i);
                i.putExtra("total", total);
                i.putExtra("position", position);
                getApplicationContext().sendBroadcast(i);
                BufferedInputStream in = new BufferedInputStream(connection.getInputStream());
                FILE = aUrl.substring(aUrl.lastIndexOf("/") + 1);
                FileOutputStream fos = (downloaded == 0) ? new FileOutputStream(DESTINATION_PATH + FILE) : new FileOutputStream(DESTINATION_PATH + url.getFile(), true);
                BufferedOutputStream bout = new BufferedOutputStream(fos, 1024);
                byte[] data = new byte[1024];
                int x = 0;
                while ((x = in.read(data, 0, 1024)) >= 0) {
                    bout.write(data, 0, x);
                    // downloaded += x;
                    // progressBar.setProgress(downloaded);
                    // i.putExtra("downloaded", downloaded);
                    // getApplicationContext().sendBroadcast(i);

                }
                bout.flush();
                bout.close();

                i.putExtra("downloaded", true);
                getApplicationContext().sendBroadcast(i);

                return true;

            } catch (MalformedURLException e) {
                e.printStackTrace();
                fr.letroll.framework.Notification.log("error", aUrl);
                return false;
            } catch (IOException e) {
                e.printStackTrace();
                i.putExtra("downloaded", false);
                getApplicationContext().sendBroadcast(i);
                return false;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }

    }

}
