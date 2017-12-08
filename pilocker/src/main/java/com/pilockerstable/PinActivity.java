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

import java.util.ArrayList;
import java.util.Collections;

@SuppressLint({ "SimpleDateFormat", "InlinedApi" })
public class PinActivity extends Activity {

	SharedPreferences sec;
	
	EditText unlock;
	Runnable runnable;
	Window window ;

	Button thelock, butt1, butt2, butt3, butt4, butt5, butt6, butt7, butt8, butt9, butt0, back;
	String unlocker, camera, lock, browser, pin, pkg, sv, img,auto;

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


	

		unlock = (EditText) findViewById(R.id.pin);
		
		thelock = (Button) findViewById(R.id.button1);
		back = (Button) findViewById(R.id.back);

		ArrayList<Integer> randomNumKey = randomSingleDigitNumbersGenerator();
		butt1 = (Button) findViewById(R.id.b1);
		butt1.setText(String.valueOf(randomNumKey.get(1)));
		butt2 = (Button) findViewById(R.id.b2);
		butt2.setText(String.valueOf(randomNumKey.get(2)));
		butt3 = (Button) findViewById(R.id.b3);
		butt3.setText(String.valueOf(randomNumKey.get(3)));
		butt4 = (Button) findViewById(R.id.b4);
		butt4.setText(String.valueOf(randomNumKey.get(4)));
		butt5 = (Button) findViewById(R.id.b5);
		butt5.setText(String.valueOf(randomNumKey.get(5)));
		butt6 = (Button) findViewById(R.id.b6);
		butt6.setText(String.valueOf(randomNumKey.get(6)));
		butt7 = (Button) findViewById(R.id.b7);
		butt7.setText(String.valueOf(randomNumKey.get(7)));
		butt8 = (Button) findViewById(R.id.b8);
		butt8.setText(String.valueOf(randomNumKey.get(8)));
		butt9 = (Button) findViewById(R.id.b9);
		butt9.setText(String.valueOf(randomNumKey.get(9)));
		butt0 = (Button) findViewById(R.id.b0);
		butt0.setText(String.valueOf(randomNumKey.get(0)));

		
		butt1.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {

				unlock.append(butt1.getText());

			}
		});

		butt2.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {

				unlock.append(butt2.getText());

			}
		});

		butt3.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {

				unlock.append(butt3.getText());

			}
		});

		butt4.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {

				unlock.append(butt4.getText());

			}
		});

		butt5.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {

				unlock.append(butt5.getText());

			}
		});

		butt6.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {

				unlock.append(butt6.getText());

			}
		});

		butt7.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {

				unlock.append(butt7.getText());

			}
		});

		butt8.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {

				unlock.append(butt8.getText());

			}
		});

		butt9.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {

				unlock.append(butt9.getText());

			}
		});

		butt0.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {

				unlock.append(butt0.getText());

			}
		});

		
		back.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {

				try{
					
				String lock = unlock.getText().toString();
				lock = lock.substring(0, lock.length() - 1);
				unlock.setText(lock);
				
				} catch(Exception e){
					
				    e.printStackTrace();
					
				  }
			}		
		});
		
		
		back.setOnLongClickListener(new OnLongClickListener() {
			
			@Override
			public boolean onLongClick(View v) {

				unlock.setText("");
				
				return false;
			}
		});

		
		unlock.addTextChangedListener(new TextWatcher() {
			
			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {
				
				unlocker = unlock.getText().toString();

				
				if (unlocker.equals(pin) && auto.equals("true")) {

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

					
						Intent i = new Intent();
						i.setClass(getBaseContext(), LockerService.class);
						startService(i);
						
						sv = getIntent().getStringExtra("sv");
						Intent LaunchIntent = getPackageManager().getLaunchIntentForPackage(sv);
						LaunchIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
						startActivity(LaunchIntent);

						onCan();

					}
				}
				
			}
			
			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,int after) {
				
				
			}
			
			@Override
			public void afterTextChanged(Editable s) {

			}
		});
		
		
		
		if(hasBackKey & hasHomeKey){
			
		mHomeKeyLocker = new HomeKeyLocker();
		mHomeKeyLocker.lock(this);

		}
		
		
		unlock.setFilters(new InputFilter[] { new InputFilter.LengthFilter(12) });

		thelock.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {

				unlocker = unlock.getText().toString();

				if (unlocker.equals(pin)) {

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

				else {

					Toast.makeText(getApplicationContext(),
							"Wrong PIN try again", Toast.LENGTH_SHORT).show();
					unlock.setText("");

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
	}

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

	public ArrayList<Integer> randomSingleDigitNumbersGenerator(){

		ArrayList<Integer> randomNumbers = new ArrayList<Integer>();
		for (int i=0; i<10; i++) {
			randomNumbers.add(new Integer(i));
		}
		Collections.shuffle(randomNumbers);
		return randomNumbers;

	}
}