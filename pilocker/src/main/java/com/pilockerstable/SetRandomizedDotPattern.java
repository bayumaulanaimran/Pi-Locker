package com.pilockerstable;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.TypedValue;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

/**
 * Created by MY PC on 29/01/2018.
 */

public class SetRandomizedDotPattern extends Activity{

    TextView textView;

    Button confirm, cancel;

    GridView gridView;

    ArrayList<Dot> dotList;
    DotAdapter dotAdapter;

    String dots;

    boolean flag;

    int numOfRows, numOfColumns, counter;

    @Override
    protected void onCreate(@Nullable final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.gridview);

        flag=false;
        counter = 0;

        numOfColumns = super.getIntent().getIntExtra("numOfColumns", 3);
        numOfRows = super.getIntent().getIntExtra("numOfRows", 3);

        dotList = new ArrayList<>(numOfRows*numOfColumns);

        Dot dot = new Dot(R.drawable.pin1, 0, View.VISIBLE);

        for (int i = 0; i < numOfRows*numOfColumns; i++) {
            dotList.add(dot);
        }

        gridView = (GridView)findViewById(R.id.gridview);

        gridView.setNumColumns(numOfColumns);

        RelativeLayout rl = (RelativeLayout) findViewById(R.id.relativeLayoutButton);
        int dimension = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 60, getResources().getDisplayMetrics());
        rl.setLayoutParams(new LinearLayout.LayoutParams(dimension*numOfColumns,dimension*(numOfRows+1)));

        dotAdapter = new DotAdapter(this,dotList);

        gridView.setAdapter(dotAdapter);

        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView parent, View view, int position, long id) {
                if(flag){

                    if(dotList.get(position).getInvisibility()==View.VISIBLE){
                        if(dotList.get(position).getSequence()==0){
                            counter++;
                            dotList.set(position,new Dot(R.drawable.pin1,counter,View.VISIBLE));
                        }else{

                            int seqNow = dotList.get(position).getSequence();

                            for (int i = 0; i < dotList.size(); i++) {
                                if(i==position){
                                    dotList.set(i,new Dot(R.drawable.pin1,0, View.VISIBLE));
                                }else if(dotList.get(i).getSequence()>seqNow){
                                    dotList.set(i,new Dot(R.drawable.pin1,dotList.get(i).getSequence()-1, View.VISIBLE));
                                }
                            }

                            counter--;

                        }
                    }

                }else{
                    if(dotList.get(position).getDrawableId()==R.drawable.pin1){
                        dotList.set(position,new Dot(R.drawable.pin2,0,View.VISIBLE));
                    }else{
                        dotList.set(position,new Dot(R.drawable.pin1,0,View.VISIBLE));
                    }
                }

                // This tells the GridView to redraw itself
                // in turn calling your BooksAdapter's getView method again for each cell
                dotAdapter.notifyDataSetChanged();
            }
        });

        textView = (TextView)findViewById(R.id.textTitle);

        confirm = (Button)findViewById(R.id.confirm);
        cancel = (Button)findViewById(R.id.cancel);

        confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(flag){
                    StringBuilder sb = new StringBuilder(numOfRows*numOfColumns);
                    dots = "";

                    int sumDotSelected=0;

                    for (Dot dot : dotList) {
                        sb.append(dot.getSequence());
                        if (dot.getInvisibility()==View.VISIBLE){
                            sumDotSelected++;
                        }
                    }

                    if(sumDotSelected==counter){

                        dots = sb.toString();
                        Toast.makeText(SetRandomizedDotPattern.this,dots,Toast.LENGTH_SHORT).show();

                        Intent i = new Intent();
                        i.putExtra("dots", dots);
                        setResult(RESULT_OK, i);
                        finish();

                    }else{
                        Toast.makeText(SetRandomizedDotPattern.this,"You Must Give Number to All Selected Dots!",Toast.LENGTH_SHORT).show();
                    }

                }else{

                    int numDotSelected = 0;
                    for (Dot dot : dotList) {
                        if(dot.getDrawableId()==R.drawable.pin2){
                            numDotSelected++;
                        }
                    }

                    if(numDotSelected>=4){

                        confirm.setText("OK");
                        cancel.setText("Back");
                        flag = true;
                        textView.setText("Give Number to Dots");
                        counter=0;

                        for (int i = 0; i < dotList.size(); i++) {
                            if(dotList.get(i).getDrawableId()==R.drawable.pin1){
                                dotList.set(i,new Dot(R.drawable.pin1,0,View.INVISIBLE));
                            }else{
                                dotList.set(i,new Dot(R.drawable.pin1,0,View.VISIBLE));
                            }
                        }
                    }else{
                        Toast.makeText(SetRandomizedDotPattern.this,"Select At Least 4 Dots", Toast.LENGTH_SHORT).show();
                    }

                }

                dotAdapter.notifyDataSetChanged();
            }
        });

        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(flag){
                    confirm.setText("OK");
                    cancel.setText("Cancel");
                    flag = false;
                    textView.setText("Select Dots");
                    counter=0;
                    for (int i = 0; i < dotList.size(); i++) {
                        if(dotList.get(i).getInvisibility()==View.INVISIBLE){
                            dotList.set(i,new Dot(R.drawable.pin1,0,View.VISIBLE));
                        }else{
                            dotList.set(i,new Dot(R.drawable.pin2,0,View.VISIBLE));
                        }
                    }
                }else{

                    setResult(RESULT_CANCELED);

                    finish();

                }

                dotAdapter.notifyDataSetChanged();
            }
        });

    }
}
