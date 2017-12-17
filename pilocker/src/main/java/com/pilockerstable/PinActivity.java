package com.pilockerstable;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.ColorFilter;
import android.graphics.PixelFormat;
import android.graphics.PorterDuff;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.preference.PreferenceManager;
import android.support.v4.content.ContextCompat;
import android.text.Editable;
import android.text.InputFilter;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.KeyCharacterMap;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TableLayout;
import android.widget.Toast;

import org.mindrot.jbcrypt.BCrypt;

import java.util.ArrayList;
import java.util.Collections;

@SuppressLint({ "SimpleDateFormat", "InlinedApi" })
public class PinActivity extends Activity {

	SharedPreferences sec;
	
	EditText unlock;
	Runnable runnable;
	Window window ;

	ArrayList<Button> buttonList;
	Button thelock, back;
	String unlocker, camera, lock, browser, pin, pkg, sv, img,auto, hashedPin;

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

		buttonList = new ArrayList<Button>(10);

		unlock = (EditText) findViewById(R.id.pin); // edittext field for pin
		
		thelock = (Button) findViewById(R.id.button1); // confirm button
		back = (Button) findViewById(R.id.back); // back to lock

		buttonList.add((Button)findViewById(R.id.b0));
		buttonList.add((Button)findViewById(R.id.b1));
		buttonList.add((Button)findViewById(R.id.b2));
		buttonList.add((Button)findViewById(R.id.b3));
		buttonList.add((Button)findViewById(R.id.b4));
		buttonList.add((Button)findViewById(R.id.b5));
		buttonList.add((Button)findViewById(R.id.b6));
		buttonList.add((Button)findViewById(R.id.b7));
		buttonList.add((Button)findViewById(R.id.b8));
		buttonList.add((Button)findViewById(R.id.b9));

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

		// randomized number buttons
		randomizeNumKey();

		// initialize onClick() and onTextChanged()
		initializeOnEventListener();

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
		for(int i=0;i<10;i++){
			buttonList.get(i).setText(String.valueOf(randomNumKey.get(i)));
		}
	}

	public void initializeOnEventListener(){
		for (int index = 0; index<10; index++) {
			final int i=index;
			buttonList.get(index).setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					unlock.append(buttonList.get(i).getText());

					randomizeNumKey();
				}
			});
		}

		back.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {

				try{

					String lock = unlock.getText().toString();
					lock = lock.substring(0, lock.length() - 1);
					unlock.setText(lock);

					unlock.setSelection(lock.length());

					randomizeNumKey();

				} catch(Exception e){

					e.printStackTrace();

				}
			}
		});


		back.setOnLongClickListener(new OnLongClickListener() {

			@Override
			public boolean onLongClick(View v) {

				unlock.setText("");

				randomizeNumKey();

				return false;
			}
		});


		unlock.addTextChangedListener(new TextWatcher() {

			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {

				unlocker = unlock.getText().toString();

				if (BCrypt.checkpw(unlocker,hashedPin) && auto.equals("true")) {

					unlockScreen();

				}

			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,int after) {


			}

			@Override
			public void afterTextChanged(Editable s) {

			}
		});

		unlock.setFilters(new InputFilter[] { new InputFilter.LengthFilter(12) });

		thelock.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {

				unlocker = unlock.getText().toString();

				if (BCrypt.checkpw(unlocker,hashedPin)) {

					unlockScreen();

				}

				else {

					Toast.makeText(getApplicationContext(),
							"Wrong PIN try again", Toast.LENGTH_SHORT).show();
					unlock.setText("");

				}

			}

		});
	}

	public void unlockScreen(){

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
	}

}