package com.pilockerstable;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.graphics.Bitmap;
import android.graphics.PixelFormat;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.preference.PreferenceManager;
import android.support.v4.content.ContextCompat;
import android.view.Gravity;
import android.view.KeyCharacterMap;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TableLayout;
import android.widget.Toast;

import org.mindrot.jbcrypt.BCrypt;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;

@SuppressLint({ "SimpleDateFormat", "InlinedApi" })
public class PinActivity extends Activity {

	SharedPreferences sec;
	
	Runnable runnable;
	Window window ;

	ArrayList<Button> buttonList;
	ArrayList<Boolean> boolList;
	Random rand;
	Button thelock, randomize;
	String unlocker, camera, lock, browser, pin, pkg, sv, img,auto, hashedPin;
	StringBuilder sb;

	private Handler mainhandler;
	private HomeKeyLocker mHomeKeyLocker;

	static Bitmap bmImg;
	static TableLayout r0;

    boolean hasBackKey = KeyCharacterMap.deviceHasKey(KeyEvent.KEYCODE_BACK);
    boolean hasHomeKey = KeyCharacterMap.deviceHasKey(KeyEvent.KEYCODE_HOME);

	@SuppressLint({ "HandlerLeak", "SimpleDateFormat" })
	@Override
	protected void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);

		stopService(new Intent(PinActivity.this, LockerService.class));
		
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

		setContentView(R.layout.pin);

		buttonList = new ArrayList<Button>(9);

		thelock = (Button) findViewById(R.id.button1); // confirm button
		randomize = (Button) findViewById(R.id.back); // back to lock

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

		rand = new Random();

		randomize();

		onClickInitialize();

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

		if(hasBackKey & hasHomeKey){

			mHomeKeyLocker = new HomeKeyLocker();
			mHomeKeyLocker.lock(this);

		}
	}

	public void onClickInitialize(){
		for(int index=0;index<9;index++){
			final int i=index;
			buttonList.get(i).setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					dotClicked(i);
					if(auto.equals("true")){
						checkDots();
					}
				}
			});
		}
		thelock.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				checkDots();
			}
		});
		randomize.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				randomize();
			}
		});
	}

	public void checkDots(){
		if (BCrypt.checkpw(unlocker,hashedPin)) {

			if (unlocker.equals(pin)) {
				onCan();

			} else if (unlocker.equals(pin) && browser.equals("true")) {

				Uri uri = Uri.parse("http://www.google.com");
				Intent intent = new Intent(Intent.ACTION_VIEW, uri);
				startActivity(intent);

				save("browser", "");

				onCan();

			} else if (unlocker.equals(pin) && camera.equals("true")) {

				Intent intent = new Intent(
						"android.media.action.IMAGE_CAPTURE");
				startActivityForResult(intent, 0);
				save("camera", "");

				onCan();

			} else if (unlocker.equals(pin) && pkg.equals("true")) {

				sv = getIntent().getStringExtra("sv");
				Intent i = new Intent();
				i.setClass(getBaseContext(), LockerService.class);
				startService(i);
				Intent LaunchIntent = getPackageManager()
						.getLaunchIntentForPackage(sv);
				LaunchIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
				startActivity(LaunchIntent);

				onCan();

			}
		}else if(auto.equals("true")){

		}else{
			Toast.makeText(getApplicationContext(),
					"Wrong Dots try again", Toast.LENGTH_SHORT).show();
		}
	}

	public boolean sameDots(){
		if (BCrypt.checkpw(unlocker,hashedPin)) {
			return true;
		}else{
			return false;
		}
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
		updateUnlocker();
		if(sameDots()){
			randomize();
		}
		updateDots();
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

		Editor edit = sec.edit();
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
		startService(new Intent(PinActivity.this, LockerService.class));
		finish();
	}

	// make a list of randomized sequence of single digit numbers between the range of 0 <= x <= 9
	public ArrayList<Integer> randomSingleDigitNumbersGenerator(){

		ArrayList<Integer> randomNumbers = new ArrayList<Integer>();
		for (int i=0; i<10; i++) {
			randomNumbers.add(new Integer(i));
		}
		Collections.shuffle(randomNumbers);
		return randomNumbers;

	}

	// randomize number on buttons
	public void randomizeNumKey(){
		ArrayList<Integer> randomNumKey = randomSingleDigitNumbersGenerator();
		buttonList.get(0).setText(String.valueOf(randomNumKey.get(0)));
		buttonList.get(1).setText(String.valueOf(randomNumKey.get(1)));
		buttonList.get(2).setText(String.valueOf(randomNumKey.get(2)));
		buttonList.get(3).setText(String.valueOf(randomNumKey.get(3)));
		buttonList.get(4).setText(String.valueOf(randomNumKey.get(4)));
		buttonList.get(5).setText(String.valueOf(randomNumKey.get(5)));
		buttonList.get(6).setText(String.valueOf(randomNumKey.get(6)));
		buttonList.get(7).setText(String.valueOf(randomNumKey.get(7)));
		buttonList.get(8).setText(String.valueOf(randomNumKey.get(8)));
		buttonList.get(9).setText(String.valueOf(randomNumKey.get(9)));
	}

}