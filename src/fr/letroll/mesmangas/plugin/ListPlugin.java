package fr.letroll.mesmangas.plugin;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import android.app.ListActivity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.content.pm.ServiceInfo;
import android.os.Bundle;
import android.provider.Settings;
import android.view.KeyEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import fr.letroll.framework.IntentLt;
import fr.letroll.framework.Notification;
import fr.letroll.mesmangas.R;

public class ListPlugin extends ListActivity implements OnItemLongClickListener {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.listplugin);
        context = this;

        fillPluginList();
        itemAdapter = new SimpleAdapter(this, services, R.layout.services_row, new String[] { KEY_PKG, KEY_SERVICENAME, KEY_ACTIONS, KEY_CATEGORIES }, new int[] { R.id.pkg, R.id.servicename, R.id.actions, R.id.categories });
        setListAdapter(itemAdapter);

         activateUnknowSource();

        lv = getListView();
        lv.setOnItemLongClickListener(this);

        packageBroadcastReceiver = new PackageBroadcastReceiver();
        packageFilter = new IntentFilter();
        packageFilter.addAction(Intent.ACTION_PACKAGE_ADDED);
        packageFilter.addAction(Intent.ACTION_PACKAGE_REPLACED);
        packageFilter.addAction(Intent.ACTION_PACKAGE_REMOVED);
        packageFilter.addCategory(Intent.CATEGORY_DEFAULT);
        packageFilter.addDataScheme("package");
    }

    public boolean onKeyUp(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_MENU) {
            updateList();
        }
        return true;
    }

    private void updateList() {
        fillPluginList();
        itemAdapter = new SimpleAdapter(this, services, R.layout.services_row, new String[] { KEY_PKG, KEY_SERVICENAME, KEY_ACTIONS, KEY_CATEGORIES }, new int[] { R.id.pkg, R.id.servicename, R.id.actions, R.id.categories });
        setListAdapter(itemAdapter);
        Notification.log(tag, "fillPluginList()");
    }

    protected void onStart() {
        super.onStart();
        Notification.log(tag, "onStart");
        registerReceiver(packageBroadcastReceiver, packageFilter);
    }

    protected void onStop() {
        super.onStop();
        Notification.log(tag, "onStop");
        unregisterReceiver(packageBroadcastReceiver);
    }

    public void onActionBarButtonBackClick(View v) {
        ListPlugin.this.finish();
    }

    public void onActionBarButtonExitClick(View v) {
        this.setResult(1);
        ListPlugin.this.finish();
    }

    public void backhome(View v) {
        this.setResult(2);
        ListPlugin.this.finish();
    }

    protected void onListItemClick(ListView l, View v, int position, long id) {
        Notification.log(tag, "onListItemClick: " + position);
        String category = categories.get(position);
        if (category.length() > 0) {
            Intent intent = new Intent(ListPlugin.this, InvokeMethode.class);
            intent.putExtra(BUNDLE_EXTRAS_CATEGORY, category);
            startActivityForResult(intent, 100);
        }
    }

    private void fillPluginList() {
        services = new ArrayList<HashMap<String, String>>();
        categories = new ArrayList<String>();
        PackageManager packageManager = getPackageManager();
        Intent baseIntent = new Intent(ACTION_PICK_PLUGIN);
        baseIntent.setFlags(Intent.FLAG_DEBUG_LOG_RESOLUTION);
        List<ResolveInfo> list = packageManager.queryIntentServices(baseIntent, PackageManager.GET_RESOLVED_FILTER);
        Notification.log(tag, "fillPluginList: " + list);
        for (int i = 0; i < list.size(); ++i) {
            ResolveInfo info = list.get(i);
            ServiceInfo sinfo = info.serviceInfo;
            IntentFilter filter = info.filter;
            Notification.log(tag, "fillPluginList: i: " + i + "; sinfo: " + sinfo + ";filter: " + filter);
            if (sinfo != null) {
                HashMap<String, String> item = new HashMap<String, String>();
                item.put(KEY_PKG, sinfo.packageName);
                // item.put(KEY_SERVICENAME, StringLt.lastSegment(sinfo.name,
                // '.'));
                item.put(KEY_SERVICENAME, sinfo.name);
                String firstCategory = null;
                if (filter != null) {
                    StringBuilder actions = new StringBuilder();
                    for (Iterator<String> actionIterator = filter.actionsIterator(); actionIterator.hasNext();) {
                        String action = actionIterator.next();
                        if (actions.length() > 0)
                            actions.append(",");
                        actions.append(action);
                    }
                    StringBuilder categories = new StringBuilder();
                    for (Iterator<String> categoryIterator = filter.categoriesIterator(); categoryIterator.hasNext();) {
                        String category = categoryIterator.next();
                        if (firstCategory == null)
                            firstCategory = category;
                        if (categories.length() > 0)
                            categories.append(",");
                        categories.append(category);
                    }
                    // item.put(KEY_ACTIONS, new
                    // String(StringLt.lastSegment(actions.toString(), '.')));
                    // item.put(KEY_CATEGORIES, new
                    // String(StringLt.lastSegment(categories.toString(),
                    // '.')));
                    item.put(KEY_ACTIONS, new String(actions));
                    item.put(KEY_CATEGORIES, new String(categories));
                } else {
                    item.put(KEY_ACTIONS, "<null>");
                    item.put(KEY_CATEGORIES, "<null>");
                }
                if (firstCategory == null)
                    firstCategory = "";
                categories.add(firstCategory);
                services.add(item);
            }
        }
        Notification.log(tag, "services: " + services);
        Notification.log(tag, "categories: " + categories);
    }

    // constant
    static final String ACTION_PICK_PLUGIN = "letroll.intent.action.PICK_PLUGIN";
    final String tag = this.getClass().getSimpleName();
    static final String KEY_PKG = "pkg";
    static final String KEY_SERVICENAME = "servicename";
    static final String KEY_ACTIONS = "actions";
    static final String KEY_CATEGORIES = "categories";
    static final String BUNDLE_EXTRAS_CATEGORY = "category";

    // variable
    private Context context;
    private PackageBroadcastReceiver packageBroadcastReceiver;
    private IntentFilter packageFilter;
    private ArrayList<HashMap<String, String>> services;
    private ArrayList<String> categories;
    private SimpleAdapter itemAdapter;
    // view
    ListView lv;

    class PackageBroadcastReceiver extends BroadcastReceiver {
        public final String tag = this.getClass().getSimpleName();

        public void onReceive(Context context, Intent intent) {
            Notification.log(tag, "onReceive: " + intent);
            services.clear();
            updateList();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (resultCode) {
        case 1:
            this.setResult(1);
        case 2:
            ListPlugin.this.finish();
            break;
        }
    }

    @Override
    public boolean onItemLongClick(AdapterView<?> groupview, View view, int pos, long id) {

        TextView tv = (TextView) view.findViewById(R.id.servicename);
        String pack = tv.getText().toString();

        int pos2 = pack.lastIndexOf('.');
        Notification.log(tag, "pos ici:" + pos2);

        pack = pack.substring(0, pos2);
        Notification.log(tag, "package:" + pack);

        IntentLt.uninstallApplication(context, pack);
        return true;
    }

    public void activateUnknowSource() {
        int result = Settings.Secure.getInt(getContentResolver(), Settings.Secure.INSTALL_NON_MARKET_APPS, 0);
        if (result == 0) {
            // show some dialog here
            // ...
            // and may be show application settings dialog manually
            Intent intent = new Intent();
            intent.setAction(Settings.ACTION_APPLICATION_SETTINGS);
            startActivity(intent);
        }
    }

}