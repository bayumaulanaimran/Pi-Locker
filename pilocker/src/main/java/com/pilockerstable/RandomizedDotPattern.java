package com.pilockerstable;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.PixelFormat;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.KeyCharacterMap;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TableLayout;
import android.widget.TextView;
import android.widget.Toast;

import org.mindrot.jbcrypt.BCrypt;

import java.util.ArrayList;
import java.util.Random;

/**
 * Created by MY PC on 29/01/2018.
 */

@SuppressLint({ "SimpleDateFormat", "InlinedApi" })
public class RandomizedDotPattern extends Activity{

    TextView textView;

    Button confirm, cancel;

    GridView gridView;

    ArrayList<Dot> dotList;
    DotAdapter dotAdapter;
    Dot dot;

    String dots, hashedDots;

    boolean flag;

    int numOfRows, numOfColumns, counter;

    SharedPreferences sec;

    Runnable runnable;
    Window window ;

    String camera, lock, browser, pin, pkg, sv, img, auto;

    private Handler mainhandler;
    private HomeKeyLocker mHomeKeyLocker;

    static Bitmap bmImg;
    static TableLayout r0;

    boolean hasBackKey = KeyCharacterMap.deviceHasKey(KeyEvent.KEYCODE_BACK);
    boolean hasHomeKey = KeyCharacterMap.deviceHasKey(KeyEvent.KEYCODE_HOME);

    Random rd;

    @SuppressLint({ "HandlerLeak", "SimpleDateFormat" })
    @Override
    protected void onCreate(@Nullable final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        stopService(new Intent(RandomizedDotPattern.this, LockerService.class));

        loadlock();


        window = getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED);
        window.addFlags(WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD);

        getWindow().getDecorView().setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                        | View.INVISIBLE);


        WindowManager wmanager = ((WindowManager) getApplicationContext().getSystemService(Context.WINDOW_SERVICE));

        // statusbar blocker configuration
        WindowManager.LayoutParams localLayoutParams = new WindowManager.LayoutParams();
        localLayoutParams.type = WindowManager.LayoutParams.TYPE_SYSTEM_ERROR;
        localLayoutParams.gravity = Gravity.TOP;
        localLayoutParams.flags = WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE |
                // this is to enable the notification to recieve touch events
                WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL |
                // Draws over status bar
                WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN;

        // set the size of statusbar blocker
        localLayoutParams.width = WindowManager.LayoutParams.MATCH_PARENT;
        localLayoutParams.height = (int) (35 * getResources()
                .getDisplayMetrics().scaledDensity);
        localLayoutParams.format = PixelFormat.TRANSLUCENT;

        View view = new customViewGroup(this);
        wmanager.addView(view, localLayoutParams);

        if(hasBackKey & hasHomeKey){

            mHomeKeyLocker = new HomeKeyLocker();
            mHomeKeyLocker.lock(this);

        }

        //mulai
        setContentView(R.layout.gridview);

        flag=false;
        counter = 0;

        dotList = new ArrayList<>(numOfRows*numOfColumns);

        dot = new Dot(R.drawable.pin1, 0, View.VISIBLE);

        rd = new Random();
        for (int i = 0; i < numOfRows*numOfColumns; i++) {
            dotList.add(dot);
            if(rd.nextBoolean()){
                dotList.get(i).setDrawableId(R.drawable.pin2);
            }else{
                dotList.get(i).setDrawableId(R.drawable.pin1);
            }
        }

        gridView = (GridView)findViewById(R.id.gridview);

        gridView.setNumColumns(numOfColumns);

        RelativeLayout rl = (RelativeLayout) findViewById(R.id.relativeLayoutButton);
        int dimension = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 60, getResources().getDisplayMetrics());
        rl.setLayoutParams(new LinearLayout.LayoutParams(dimension*numOfColumns+10,dimension*(numOfRows+1)));

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
//                                    dotList.set(i,new Dot(R.drawable.pin1,0, View.VISIBLE));
                                    dot.setSequence(0);
                                    dot.setDrawableId(R.drawable.pin1);
                                    dot.setInvisibility(View.VISIBLE);
                                    dotList.set(i,dot);
                                }else if(dotList.get(i).getSequence()>seqNow){
//                                    dotList.set(i,new Dot(R.drawable.pin1,dotList.get(i).getSequence()-1, View.VISIBLE));
                                    dot.setSequence(dotList.get(i).getSequence()-1);
                                    dot.setDrawableId(R.drawable.pin1);
                                    dot.setInvisibility(View.VISIBLE);
                                    dotList.set(i,dot);
                                }
                            }

                            counter--;

                        }
                    }

                }else{
                    if(dotList.get(position).getDrawableId()==R.drawable.pin1){
//                        dotList.set(position,new Dot(R.drawable.pin2,0,View.VISIBLE));
                        dot.setSequence(0);
                        dot.setDrawableId(R.drawable.pin2);
                        dot.setInvisibility(View.VISIBLE);
                        dotList.set(position,dot);
                    }else{
//                        dotList.set(position,new Dot(R.drawable.pin1,0,View.VISIBLE));
                        dot.setSequence(0);
                        dot.setDrawableId(R.drawable.pin2);
                        dot.setInvisibility(View.VISIBLE);
                        dotList.set(position,dot);
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

                        authentication();

                    }else{
                        Toast.makeText(RandomizedDotPattern.this,"You Must Give Number to All Selected Dots!",Toast.LENGTH_SHORT).show();
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
                        cancel.setVisibility(View.VISIBLE);
                        cancel.setEnabled(true);

                        flag = true;
                        textView.setText("Give Number to Dots");
                        counter=0;

                        for (int i = 0; i < dotList.size(); i++) {
                            if(dotList.get(i).getDrawableId()==R.drawable.pin1){
//                                dotList.set(i,new Dot(R.drawable.pin1,0,View.INVISIBLE));
                                dot.setSequence(0);
                                dot.setDrawableId(R.drawable.pin1);
                                dot.setInvisibility(View.INVISIBLE);
                                dotList.set(i,dot);
                            }else{
//                                dotList.set(i,new Dot(R.drawable.pin1,0,View.VISIBLE));
                                dot.setSequence(0);
                                dot.setDrawableId(R.drawable.pin1);
                                dot.setInvisibility(View.VISIBLE);
                                dotList.set(i,dot);
                            }
                        }
                    }else{
                        Toast.makeText(RandomizedDotPattern.this,"Select At Least 4 Dots", Toast.LENGTH_SHORT).show();
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

                    cancel.setEnabled(false);
                    cancel.setVisibility(View.INVISIBLE);

                    flag = false;
                    textView.setText("Select Dots");
                    counter=0;
                    for (int i = 0; i < dotList.size(); i++) {
                        if(dotList.get(i).getInvisibility()==View.INVISIBLE){
//                            dotList.set(i,new Dot(R.drawable.pin1,0,View.VISIBLE));
                            dot.setSequence(0);
                            dot.setDrawableId(R.drawable.pin1);
                            dot.setInvisibility(View.VISIBLE);
                            dotList.set(i,dot);
                        }else{
//                            dotList.set(i,new Dot(R.drawable.pin2,0,View.VISIBLE));
                            dot.setSequence(0);
                            dot.setDrawableId(R.drawable.pin2);
                            dot.setInvisibility(View.VISIBLE);
                            dotList.set(i,dot);
                        }
                    }

                    dotAdapter.notifyDataSetChanged();

                }

            }
        });

        mainhandler = new Handler() {

            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);

                Intent closeDialog = new Intent(Intent.ACTION_CLOSE_SYSTEM_DIALOGS);
                sendBroadcast(closeDialog);

            }
        };

        new Thread(new Runnable() {
            public void run() {

                while (true) {
                    try {

                        Thread.sleep(1000);
                        mainhandler.sendEmptyMessage(0);

                    } catch (InterruptedException e) {

                        e.printStackTrace();
                    }

                }

            }
        }).start();
    }

    public void loadlock() {

        sec = PreferenceManager.getDefaultSharedPreferences(this);
        pkg = sec.getString("pkg", "false");
        img = sec.getString("img", "");
        pin = sec.getString("pin", "");
        browser = sec.getString("browser", "");
        camera = sec.getString("camera", "");
        lock = sec.getString("lock", "");
        auto = sec.getString("auto", "");

        numOfColumns = sec.getInt("numOfColumns",3);
        numOfRows = sec.getInt("numOfRows",3);
        hashedDots = sec.getString("hashedDots","");

    }

    public void save(String key, String value) {

        SharedPreferences.Editor edit = sec.edit();
        edit.putString(key, value);
        edit.commit();

    }

    @Override
    public void onDestroy() {

        mainhandler.removeCallbacksAndMessages(runnable);

        try {

            System.exit(0);

        } catch (Exception e) {

            android.os.Process.killProcess(android.os.Process.myPid());

        }

        super.onDestroy();

    }

    public void onCan() {
        startService(new Intent(RandomizedDotPattern.this, LockerService.class));
        finish();
    }

    public void authentication(){

        if (BCrypt.checkpw(dots,hashedDots)) {

            if (browser.equals("true")) {

                Uri uri = Uri.parse("http://www.google.com");
                Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                startActivity(intent);

                save("browser", "");

            } else if (camera.equals("true")) {

                Intent intent = new Intent(
                        "android.media.action.IMAGE_CAPTURE");
                startActivityForResult(intent, 0);
                save("camera", "");

            } else if (pkg.equals("true")) {

                sv = getIntent().getStringExtra("sv");
                Intent i = new Intent();
                i.setClass(getBaseContext(), LockerService.class);
                startService(i);
                Intent LaunchIntent = getPackageManager()
                        .getLaunchIntentForPackage(sv);
                LaunchIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(LaunchIntent);

            }

            onCan();

        }

        else {

            Toast.makeText(getApplicationContext(),
                    "Wrong! Try Again!", Toast.LENGTH_SHORT).show();

        }

    }

}
