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

    String dots, hashedDots;

    boolean flag;

    int numOfRows, numOfColumns, counter, tryCounter, emergencyCounter;

    SharedPreferences sec;

    Runnable runnable;
    Window window ;

    String camera, lock, browser, pin, pkg, sv, img, auto, emergency;

    private Handler mainhandler;
    private HomeKeyLocker mHomeKeyLocker;

    static Bitmap bmImg;
    static TableLayout r0;

    boolean hasBackKey = KeyCharacterMap.deviceHasKey(KeyEvent.KEYCODE_BACK);
    boolean hasHomeKey = KeyCharacterMap.deviceHasKey(KeyEvent.KEYCODE_HOME);

    @SuppressLint({ "HandlerLeak", "SimpleDateFormat" })
    @Override
    protected void onCreate(@Nullable final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        stopService(new Intent(RandomizedDotPattern.this, LockerService.class));

        loadlock();

        fullScreen();

        setContentView(R.layout.gridview);

        flag=false;
        counter = 0;
        tryCounter = 0;
        emergencyCounter = 0;

        dotList = randomizeDots(numOfRows,numOfColumns);

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

                dotAdapter.notifyDataSetChanged();

                if(auto.equals("true")&&flag){
                    authentication("dot");
                }

            }
        });

        textView = (TextView)findViewById(R.id.textTitle);

        confirm = (Button)findViewById(R.id.confirm);
        cancel = (Button)findViewById(R.id.cancel);

        cancel.setVisibility(View.INVISIBLE);
        cancel.setEnabled(false);

        confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(flag){

                    authentication("confirm");

                }else{

                    confirm.setText("OK");

                    cancel.setText("Back");
                    cancel.setVisibility(View.VISIBLE);
                    cancel.setEnabled(true);

                    flag = true;
                    textView.setText("Give Number to Each Dots");
                    counter=0;

                    for (int i = 0; i < dotList.size(); i++) {
                        if(dotList.get(i).getDrawableId()==R.drawable.pin1){
                            dotList.set(i,new Dot(R.drawable.pin1,0,View.INVISIBLE));
                        }else{
                            dotList.set(i,new Dot(R.drawable.pin1,0,View.VISIBLE));
                        }
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
                            dotList.set(i,new Dot(R.drawable.pin1,0,View.VISIBLE));
                        }else{
                            dotList.set(i,new Dot(R.drawable.pin2,0,View.VISIBLE));
                        }

                    }

                    dotAdapter.notifyDataSetChanged();

                }

            }
        });

        textView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(emergency.equals("true")){
                    emergencyCounter++;
                    if(emergencyCounter==numOfColumns||emergencyCounter==numOfRows){
                        Toast.makeText(RandomizedDotPattern.this,"Emergency Unlock Activated!",Toast.LENGTH_SHORT).show();
                        onCan();
                    }
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

        Thread thread = new Thread(new Runnable() {
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
        });

        thread.setPriority(Thread.MIN_PRIORITY);

        thread.start();

    }

    public void loadlock() {

        sec = PreferenceManager.getDefaultSharedPreferences(this);
        pkg = sec.getString("pkg", "false");
        img = sec.getString("img", "");
        pin = sec.getString("pin", "");
        emergency = sec.getString("emergencyb", "false");
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

    public void authentication(String clickedFrom){

        StringBuilder sb = new StringBuilder(numOfRows*numOfColumns);
        dots = "";

        int sumDotSelected=0;

        for (Dot dot : dotList) {
            sb.append(dot.getSequence());
            if (dot.getInvisibility()==View.VISIBLE){
                sumDotSelected++;
            }
        }

        if(sumDotSelected==counter && sumDotSelected != 0){

            dots = sb.toString();

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

                tryCounter++;

                if(tryCounter>=3){

                    confirm.setText("OK");
                    confirm.setEnabled(false);
                    confirm.setVisibility(View.INVISIBLE);

                    cancel.setEnabled(false);
                    cancel.setVisibility(View.INVISIBLE);

                    gridView.setClickable(false);
                    gridView.setEnabled(false);
                    gridView.setVisibility(View.INVISIBLE);

                    counter=0;
                    tryCounter = 0;
                    flag = false;

                    for (int i = 0; i < dotList.size(); i++) {

                        if(dotList.get(i).getInvisibility()==View.INVISIBLE){
                            dotList.set(i,new Dot(R.drawable.pin1,0,View.VISIBLE));
                        }else{
                            dotList.set(i,new Dot(R.drawable.pin2,0,View.VISIBLE));
                        }

                    }

                    dotAdapter.notifyDataSetChanged();

                    TryInNSecondsTask TryInNSecondsTask = new TryInNSecondsTask(RandomizedDotPattern.this);

                    TryInNSecondsTask.execute(30);

                }else{

                    if(sumDotSelected==0){
                        Toast.makeText(this,"You did'nt give number to any dots!",Toast.LENGTH_SHORT).show();
                    }else{
                        Toast.makeText(getApplicationContext(), "Wrong! Try Again!", Toast.LENGTH_SHORT).show();
                    }
                }

            }

        }else{

            if(clickedFrom.equalsIgnoreCase("confirm")){
                Toast.makeText(getApplicationContext(), "You Must Give a Number to Each Selected Dots!", Toast.LENGTH_SHORT).show();
            }

        }

    }

    public ArrayList<Dot> randomizeDots(int numRows, int numColumns){

        ArrayList<Dot> arrayDots = new ArrayList<>(numColumns*numRows);

        Random rd = new Random();

        boolean randomBoolean;

        for (int i = 0; i < numColumns*numRows; i++) {
            randomBoolean = rd.nextBoolean();
            if(randomBoolean){
                arrayDots.add(new Dot(R.drawable.pin2,0,View.VISIBLE));
            }else{
                arrayDots.add(new Dot(R.drawable.pin1,0,View.VISIBLE));
            }
        }

        return arrayDots;

    }

    public void fullScreen(){
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
    }

}
