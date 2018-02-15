package com.pilockerstable;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.PixelFormat;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.util.Log;
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

import java.lang.ref.WeakReference;
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

    DotClickedTask dotClickedTask;
    SequenceClickedTask sequenceClickedTask;

    ConfirmTask confirmTask;
    UnlockTask unlockTask;

    CancelTask cancelTask;

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

        fullscreen();

        setContentView(R.layout.gridview);

        flag=false;
        counter = 0;

        dotList = new ArrayList<>(numOfRows*numOfColumns);

        dot = new Dot(R.drawable.pin1, 0, View.VISIBLE);

        rd = new Random();
        for (int i = 0; i < numOfRows*numOfColumns; i++) {
            if(rd.nextBoolean()){
                dot.setDrawableId(R.drawable.pin2);
            }else{
                dot.setDrawableId(R.drawable.pin1);
            }
            dotList.add(dot);
        }

        gridView = (GridView)findViewById(R.id.gridview);

        gridView.setNumColumns(numOfColumns);

        RelativeLayout rl = (RelativeLayout) findViewById(R.id.relativeLayoutButton);
        int dimension = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 60, getResources().getDisplayMetrics());
        rl.setLayoutParams(new LinearLayout.LayoutParams(dimension*numOfColumns+10,dimension*(numOfRows+1)));

        dotAdapter = new DotAdapter(this,dotList);

        gridView.setAdapter(dotAdapter);

        textView = (TextView)findViewById(R.id.textTitle);

        confirm = (Button)findViewById(R.id.confirm);
        cancel = (Button)findViewById(R.id.cancel);

        sequenceClickedTask = new SequenceClickedTask(this);
        dotClickedTask = new DotClickedTask(this);

        confirmTask = new ConfirmTask(this);
        unlockTask = new UnlockTask(this);

        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView parent, View view, int position, long id) {

                if(flag){

                    sequenceClickedTask.execute(position,dotList.get(position).getSequence());

                }else{

                    dotClickedTask.execute(position);

                }

            }
        });

        confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(flag){

                    unlockTask.execute();

                }else{

                    confirmTask.execute();

                }

            }
        });

        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(flag){
                    cancelTask.execute();
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

        Thread th = new Thread(new Runnable() {
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

        th.setPriority(Thread.MIN_PRIORITY);
        th.start();

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

    public void fullscreen(){

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

    public static class DotClickedTask extends AsyncTask<Integer,String,String>{

        private WeakReference<RandomizedDotPattern> rDPWReference;

        public DotClickedTask(RandomizedDotPattern context) {
            rDPWReference = new WeakReference<>(context);
        }

        @Override
        protected String doInBackground(Integer... integers) {

            if(rDPWReference.get() == null || rDPWReference.get().isFinishing()){
                return "";
            }else{

                if(rDPWReference.get().dotList.get(integers[0]).getDrawableId()==R.drawable.pin1){
//                        dotList.set(position,new Dot(R.drawable.pin2,0,View.VISIBLE));
                    rDPWReference.get().dot.setDrawableId(R.drawable.pin2);
                }else{
//                        dotList.set(position,new Dot(R.drawable.pin1,0,View.VISIBLE));
                    rDPWReference.get().dot.setDrawableId(R.drawable.pin1);
                }
                rDPWReference.get().dot.setSequence(0);
                rDPWReference.get().dot.setInvisibility(View.VISIBLE);
                rDPWReference.get().dotList.set(integers[0],rDPWReference.get().dot);

                return "OK";
            }

        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);

            if(s.equalsIgnoreCase("OK")){

                // This tells the GridView to redraw itself
                // in turn calling your BooksAdapter's getView method again for each cell
                rDPWReference.get().dotAdapter.notifyDataSetChanged();

            }

        }
    }

    public static class SequenceClickedTask extends AsyncTask<Integer,String,String>{

        WeakReference<RandomizedDotPattern> rDPWReference;

        public SequenceClickedTask(RandomizedDotPattern context){
            rDPWReference = new WeakReference<>(context);
        }

        @Override
        protected String doInBackground(Integer... integers) {
            if(rDPWReference.get()==null||rDPWReference.get().isFinishing()){
                return "";
            }else{

                if(rDPWReference.get().dotList.get(integers[0]).getInvisibility()==View.VISIBLE){

                    if(integers[1]==0){

                        rDPWReference.get().counter++;

                        rDPWReference.get().dot.setDrawableId(R.drawable.pin1);
                        rDPWReference.get().dot.setSequence(rDPWReference.get().counter);
                        rDPWReference.get().dot.setInvisibility(View.VISIBLE);

                        rDPWReference.get().dotList.set(integers[0],rDPWReference.get().dot);

                    }else{

                        int seqNow = rDPWReference.get().dotList.get(integers[0]).getSequence();

                        for (int i = 0; i < rDPWReference.get().dotList.size(); i++) {

                            if(i==integers[0]){
                                rDPWReference.get().dot.setSequence(0);
                            }else if(rDPWReference.get().dotList.get(i).getSequence()>seqNow){
                                rDPWReference.get().dot.setSequence(rDPWReference.get().dotList.get(i).getSequence()-1);
                            }

                            rDPWReference.get().dot.setDrawableId(R.drawable.pin1);
                            rDPWReference.get().dot.setInvisibility(View.VISIBLE);

                            rDPWReference.get().dotList.set(i,rDPWReference.get().dot);

                        }

                        rDPWReference.get().counter--;

                    }

                }

                return "OK";

            }

        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);

            if(s.equalsIgnoreCase("OK")){
                rDPWReference.get().dotAdapter.notifyDataSetChanged();
            }
        }
    }

    public static class UnlockTask extends AsyncTask<String, String, String>{

        WeakReference<RandomizedDotPattern> rDPWReference;

        public UnlockTask(RandomizedDotPattern context){
            rDPWReference = new WeakReference<>(context);
        }

        @Override
        protected String doInBackground(String... strings) {

            if(rDPWReference.get() == null || rDPWReference.get().isFinishing()) {

                return "";

            }else{

                StringBuilder sb = new StringBuilder(rDPWReference.get().dotList.size());
                rDPWReference.get().dots = "";

                int sumDotSelected=0;

                for (Dot dot : rDPWReference.get().dotList) {
                    sb.append(dot.getSequence());
                    if (dot.getInvisibility()==View.VISIBLE){
                        sumDotSelected++;
                    }
                }

                if(sumDotSelected==rDPWReference.get().counter){

                    rDPWReference.get().dots = sb.toString();

                    return "OK";

                }else{

                    return "";

                }

            }

        }

        public void save(String key, String value) {

            SharedPreferences.Editor edit = rDPWReference.get().sec.edit();
            edit.putString(key, value);
            edit.commit();

        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);

            if(s.equalsIgnoreCase("OK")){

                if (BCrypt.checkpw(rDPWReference.get().dots,rDPWReference.get().hashedDots)) {

                    if (rDPWReference.get().browser.equals("true")) {

                        Uri uri = Uri.parse("http://www.google.com");
                        Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                        rDPWReference.get().startActivity(intent);

                        save("browser", "");

                    } else if (rDPWReference.get().camera.equals("true")) {

                        Intent intent = new Intent(
                                "android.media.action.IMAGE_CAPTURE");
                        rDPWReference.get().startActivityForResult(intent, 0);
                        save("camera", "");

                    } else if (rDPWReference.get().pkg.equals("true")) {

                        rDPWReference.get().sv = rDPWReference.get().getIntent().getStringExtra("sv");
                        Intent i = new Intent();
                        i.setClass(rDPWReference.get().getBaseContext(), LockerService.class);
                        rDPWReference.get().startService(i);
                        Intent LaunchIntent = rDPWReference.get().getPackageManager()
                                .getLaunchIntentForPackage(rDPWReference.get().sv);
                        LaunchIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        rDPWReference.get().startActivity(LaunchIntent);

                    }

                    rDPWReference.get().onCan();

                }

                else {

                    Toast.makeText(rDPWReference.get().getApplicationContext(),
                            "Wrong! Try Again!", Toast.LENGTH_SHORT).show();

                }

            }else{
                Toast.makeText(rDPWReference.get(),"You Must Give Number to All Selected Dots!",Toast.LENGTH_SHORT).show();
            }

        }
    }

    public static class ConfirmTask extends AsyncTask<String, String, String>{

        WeakReference<RandomizedDotPattern> rDPWReference;

        public ConfirmTask(RandomizedDotPattern context){
            rDPWReference = new WeakReference<>(context);
        }

        @Override
        protected String doInBackground(String... strings) {

            if(rDPWReference.get() == null || rDPWReference.get().isFinishing()) {

                return "";

            }else {

                int numDotSelected = 0;
                for (Dot dot : rDPWReference.get().dotList) {
                    if (dot.getDrawableId() == R.drawable.pin2) {
                        numDotSelected++;
                    }
                }

                if (numDotSelected >= 4) {

                    rDPWReference.get().confirm.setText("OK");

                    rDPWReference.get().cancel.setText("Back");
                    rDPWReference.get().cancel.setVisibility(View.VISIBLE);
                    rDPWReference.get().cancel.setEnabled(true);

                    rDPWReference.get().flag = true;
                    rDPWReference.get().textView.setText("Give Number to Dots");
                    rDPWReference.get().counter = 0;

                    for (int i = 0; i < rDPWReference.get().dotList.size(); i++) {

                        if (rDPWReference.get().dotList.get(i).getDrawableId() == R.drawable.pin1) {
//                                dotList.set(i,new Dot(R.drawable.pin1,0,View.INVISIBLE));
                            rDPWReference.get().dot.setInvisibility(View.INVISIBLE);
                        } else {
//                                dotList.set(i,new Dot(R.drawable.pin1,0,View.VISIBLE));
                            rDPWReference.get().dot.setInvisibility(View.VISIBLE);
                        }

                        rDPWReference.get().dot.setSequence(0);
                        rDPWReference.get().dot.setDrawableId(R.drawable.pin1);

                        rDPWReference.get().dotList.set(i, rDPWReference.get().dot);
                    }

                    return "OK";

                } else {

                    return "";

                }
            }

        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);

            if(s.equalsIgnoreCase("OK")){

                rDPWReference.get().dotAdapter.notifyDataSetChanged();

            }else{

                Toast.makeText(rDPWReference.get(),"Select At Least 4 Dots", Toast.LENGTH_SHORT).show();

            }

        }
    }

    public static class CancelTask extends AsyncTask<String,String,String>{

        public WeakReference<RandomizedDotPattern> rDPWReference;

        @Override
        protected String doInBackground(String... strings) {

            if(rDPWReference.get()==null||rDPWReference.get().isFinishing()){
                return "";
            }else{

                rDPWReference.get().confirm.setText("OK");

                rDPWReference.get().cancel.setEnabled(false);
                rDPWReference.get().cancel.setVisibility(View.INVISIBLE);

                rDPWReference.get().flag = false;

                rDPWReference.get().textView.setText("Select Dots");

                rDPWReference.get().counter=0;

                for (int i = 0; i < rDPWReference.get().dotList.size(); i++) {
                    if(rDPWReference.get().dotList.get(i).getInvisibility()==View.INVISIBLE){
//                            dotList.set(i,new Dot(R.drawable.pin1,0,View.VISIBLE));
                        rDPWReference.get().dot.setDrawableId(R.drawable.pin1);
                    }else{
//                            dotList.set(i,new Dot(R.drawable.pin2,0,View.VISIBLE));
                        rDPWReference.get().dot.setDrawableId(R.drawable.pin2);
                    }
                    rDPWReference.get().dot.setSequence(0);
                    rDPWReference.get().dot.setInvisibility(View.VISIBLE);
                    rDPWReference.get().dotList.set(i,rDPWReference.get().dot);
                }

                return "OK";

            }

        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);

            if(s.equalsIgnoreCase("OK")){
                rDPWReference.get().dotAdapter.notifyDataSetChanged();
            }

        }
    }

}