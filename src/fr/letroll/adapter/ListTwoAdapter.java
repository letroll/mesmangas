package fr.letroll.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import fr.letroll.mesmangas.R;

public class ListTwoAdapter extends BaseAdapter {

    private LayoutInflater mInflater;
    private String[] list;

    // private Boolean[] list2;

    public ListTwoAdapter(Context context, String[] mlist, Boolean[] mlist2) {
        mInflater = LayoutInflater.from(context);
        list = mlist;
        // list2 = mlist2;
    }

    @Override
    public int getCount() {
        return list.length;
    }

    @Override
    public Object getItem(int arg0) {
        return arg0;
    }

    @Override
    public long getItemId(int arg0) {
        return arg0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        ViewHolder holder;
        if (convertView == null) {
            convertView = mInflater.inflate(R.layout.row2, null);
            holder = new ViewHolder();
            holder.text = (TextView) convertView.findViewById(R.id.chapitre);

            // holder.img = (ImageView) convertView.findViewById(R.id.icon);
            convertView.setTag(holder);
            convertView.setMinimumHeight(64);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        holder.text.setText(list[position]);
        // if (list2.length > 0) {
        // if (list2[position].equals(true)) {
        // holder.img.setVisibility(View.VISIBLE);
        // }
        // }
        return convertView;
    }

    static class ViewHolder {
        TextView text;
        // ImageView img;
    }

}
