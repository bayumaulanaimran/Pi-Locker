package com.pilockerstable;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by MY PC on 30/01/2018.
 */

public class DotAdapter extends BaseAdapter {

    Context mContext;
    ArrayList<Dot> list;

    public DotAdapter(Context context, ArrayList<Dot> array) {
        this.mContext = context;
        this.list = array;
    }

    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public Object getItem(int position) {
        return list.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public boolean isEnabled(int position) {
        // according to position return here true or false to enable or disable respectively
        if(list.get(position).getInvisibility()==View.VISIBLE){
            return true;
        }else{
            return false;
        }
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        // 1
        if (convertView == null) {
            final LayoutInflater layoutInflater = LayoutInflater.from(mContext);
            convertView = layoutInflater.inflate(R.layout.dot, null);
        }

        // 2
        final ImageView imageView = (ImageView)convertView.findViewById(R.id.imageviewBG);
        final TextView textView = (TextView)convertView.findViewById(R.id.textviewDot);

        // 3
        imageView.setImageResource(list.get(position).getDrawableId());

        if(list.get(position).getSequence()<1){
            textView.setText("");
        }else{
            textView.setText(String.valueOf(list.get(position).getSequence()));
        }

        convertView.setVisibility(list.get(position).getInvisibility());

        // 4
        return convertView;
    }

}
