package com.pilockerstable;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.view.View;
import android.widget.Button;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;

/**
 * Created by MY PC on 13/12/2017.
 */

public class DotChooseActivity extends Activity{

    SharedPreferences sec;

    ArrayList<Boolean> boolList;
    Random rand;
    ArrayList<Button> buttonList;

    Button thelock, cancel;
    String unlocker, camera, lock, browser, pin, pkg, sv, img,auto, hashedPin;
    StringBuilder sb;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        loadlock();

        setContentView(R.layout.pin);

        buttonList = new ArrayList<Button>(9);

        thelock = (Button) findViewById(R.id.button1); // confirm button
        thelock.setText("Confirm");
        cancel = (Button) findViewById(R.id.back); // back to lock
        cancel.setText("Cancel");

        buttonList.add((Button)findViewById(R.id.b1));
        buttonList.add((Button)findViewById(R.id.b2));
        buttonList.add((Button)findViewById(R.id.b3));
        buttonList.add((Button)findViewById(R.id.b4));
        buttonList.add((Button)findViewById(R.id.b5));
        buttonList.add((Button)findViewById(R.id.b6));
        buttonList.add((Button)findViewById(R.id.b7));
        buttonList.add((Button)findViewById(R.id.b8));
        buttonList.add((Button)findViewById(R.id.b9));

        boolList = new ArrayList<Boolean>(9);

        boolList.add(false);
        boolList.add(false);
        boolList.add(false);
        boolList.add(false);
        boolList.add(false);
        boolList.add(false);
        boolList.add(false);
        boolList.add(false);
        boolList.add(false);

        onClickInitialize();

    }

    public void onClickInitialize(){
        for(int index=0;index<9;index++){
            final int i=index;
            buttonList.get(i).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dotClicked(i);
                }
            });
        }
        thelock.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent returnIntent = new Intent();
                returnIntent.putExtra("dots",unlocker);
                setResult(Activity.RESULT_OK,returnIntent);
                finish();
            }
        });
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent returnIntent = new Intent();
                setResult(Activity.RESULT_CANCELED,returnIntent);
                finish();
            }
        });
    }

    // make a list of randomized sequence of single digit numbers between the range of 0 <= x <= 9
    public void randomBooleanGenerator(){

        boolList = new ArrayList<Boolean>(9);
        for (int i=0; i<9; i++) {
            boolList.add(rand.nextBoolean());
        }
        Collections.shuffle(boolList);

    }

    // randomize number on buttons
    public void randomize(){
        randomBooleanGenerator();
        updateDots();
        updateUnlocker();
    }

    public void updateDot(int index){
        if(boolList.get(index)){
            buttonList.get(index).setBackground(ContextCompat.getDrawable(this,R.drawable.pin2));
        }else{
            buttonList.get(index).setBackground(ContextCompat.getDrawable(this,R.drawable.pin1));
        }
    }

    public void updateDots(){
        updateDot(0);
        updateDot(1);
        updateDot(2);
        updateDot(3);
        updateDot(4);
        updateDot(5);
        updateDot(6);
        updateDot(7);
        updateDot(8);
    }

    public void updateUnlocker(){
        sb = new StringBuilder(9);
        for (boolean bool:boolList) {
            if(bool){
                sb.append(1);
            }else{
                sb.append(0);
            }
        }
        unlocker=sb.toString();
    }

    public void dotClicked(int index){
        if(boolList.get(index)){
            boolList.set(index,false);
        }else{
            boolList.set(index,true);
        }
        updateDot(index);
        updateUnlocker();
    }

    // load shared preferences
    public void loadlock() {

        sec = PreferenceManager.getDefaultSharedPreferences(this);
        pkg = sec.getString("pkg", "false");
        img = sec.getString("img", "");
        pin = sec.getString("pin", "");
        browser = sec.getString("browser", "");
        camera = sec.getString("camera", "");
        lock = sec.getString("lock", "");
        auto = sec.getString("auto", "");

        hashedPin = sec.getString("hashedpin", "");
    }

    // save value to shared preferences
    public void save(String key, String value) {

        SharedPreferences.Editor edit = sec.edit();
        edit.putString(key, value);
        edit.commit();

    }

}
