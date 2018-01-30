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
    public View getView(int position, View convertView, ViewGroup parent) {
        // 1
//        final Book book = books[position];

        // 2
        if (convertView == null) {
            final LayoutInflater layoutInflater = LayoutInflater.from(mContext);
            convertView = layoutInflater.inflate(R.layout.dot, null);
        }

        // 3
        final ImageView imageView = (ImageView)convertView.findViewById(R.id.imageviewBG);
        final TextView textView = (TextView)convertView.findViewById(R.id.textviewDot);

        // 4
        imageView.setImageResource(list.get(position).getDrawableId());
        if(list.get(position).getSequence()<1){
            textView.setText("");
        }else{
            textView.setText(String.valueOf(list.get(position).getSequence()));
        }
        if(list.get(position).getInvisibility()==View.INVISIBLE){
            textView.setVisibility(View.INVISIBLE);
            imageView.setVisibility(View.INVISIBLE);
            convertView.setEnabled(false);
        }else{
            textView.setVisibility(View.VISIBLE);
            imageView.setVisibility(View.VISIBLE);
            convertView.setEnabled(true);
        }

        return convertView;
    }

    public DotAdapter(Context context, ArrayList<Dot> array) {
        this.mContext = context;
        this.list = array;
    }

}
