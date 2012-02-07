package fr.letroll.mesmangas;

import java.util.ArrayList;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class ListOneAdapter extends BaseAdapter {

    private LayoutInflater mInflater;
    private ArrayList<String> chapter;
    private ArrayList<String> manga;
    private ArrayList<String> site;

    public ListOneAdapter(Context context, ArrayList<String> _manga, ArrayList<String> _chapter, ArrayList<String> _site) {
        mInflater = LayoutInflater.from(context);
        manga = _manga;
        chapter = _chapter;
        site = _site;
    }

    @Override
    public int getCount() {
        return manga.size();
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
            convertView = mInflater.inflate(R.layout.row, null);
            holder = new ViewHolder();
            holder.text = (TextView) convertView.findViewById(R.id.manga);
            holder.text2 = (TextView) convertView.findViewById(R.id.chapter);

            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        holder.text.setText(manga.get(position));
        holder.text2.setText(chapter.get(position));
        holder.img = (ImageView) convertView.findViewById(R.id.icon);

        String s = site.get(position);
//        Log.e("t======", s);
        if (s.startsWith("http://manga")) {
            holder.img.setImageResource(R.drawable.bleachexile);
        }else if (s.startsWith("http://dbps")) {
            holder.img.setImageResource(R.drawable.dbps);
        }else if (s.startsWith("http://www.anime-")) {
            holder.img.setImageResource(R.drawable.animestory);
        }else if (s.startsWith("http://www.mangafox")) {
            holder.img.setImageResource(R.drawable.mangafox);
        }
        else {
            holder.img.setImageResource(R.drawable.icon);
        }

        return convertView;
    }

    static class ViewHolder {
        TextView text;
        TextView text2;
        ImageView img;
    }

}
