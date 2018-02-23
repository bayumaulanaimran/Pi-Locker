package com.pilockerstable;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import yuku.ambilwarna.AmbilWarnaDialog;
import yuku.ambilwarna.AmbilWarnaDialog.OnAmbilWarnaListener;
import android.app.AlertDialog;
import android.app.admin.DevicePolicyManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.database.Cursor;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.provider.Settings;
import android.support.v7.app.ActionBarActivity;
import android.text.Html;
import android.text.InputType;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import com.almas.ShortcutSettings;

import org.mindrot.jbcrypt.BCrypt;

import eu.janmuller.android.simplecropimage.CropImage;

public class MainActivity extends ActionBarActivity {

	/**
	 * 
	 * @author MohamedRashad
	 * 
	 */

	int numOfColumns, numOfRows, cost;
	String hashedDots;

	Button setDots, setNumofColumns, setNumofRows;

	public static final String TAG = "MainActivity";
	public static final String TEMP_PHOTO_FILE_NAME = "temp_photo.jpg";
	public static final int REQUEST_CODE_GALLERY = 0x1;
	public static final int REQUEST_CODE_TAKE_PICTURE = 0x2;
	public static final int REQUEST_CODE_CROP_IMAGE = 0x3;
	public static final int REQUEST_CODE_SET_RANDOMIZED_DOT_PATTERN = 0x4;
	public final int CROP_FROM_CAMERA = 0;
	
	
	private File mFileTemp;
	
	static String on, se;
	static SharedPreferences spf;
	
	boolean admin;
	int colors, height, width, size;
	
	Context context = this;
	CheckBox start, secret, skip, DoubleTap, enter, ges_hide, autoy;
	Button screentext, background, screentextcolor, help, donate, pin, button2, locknow, shortb;
	String picturePath, load, xx, DD, srt, skips, tap, lock, Pin, Pass, extStorageDirectory, jjk,auto;
	FileOutputStream out;
	EditText input1;
	DisplayMetrics displaymetrics = new DisplayMetrics();
	Uri selectedImage;
	Cursor cursor;
	DevicePolicyManager policyManager;
	ComponentName adminReceiver;
	File file;
	android.support.v7.app.ActionBar actionBar;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		actionBar = getSupportActionBar();
		actionBar.setBackgroundDrawable(new ColorDrawable(0xff00BCD4));
		actionBar.setDisplayShowTitleEnabled(false);
		actionBar.setDisplayShowTitleEnabled(true);
		actionBar.setTitle(Html.fromHtml("<font color='#ffffff'> <b>Pi Locker</b> </font>"));

		setContentView(R.layout.activity_main);


		
		
		for (int i = 0; i < 4; i++) {

			String s = Settings.System.getString(context.getContentResolver(), "PiSC" + i);

			if (s == null | s == "") {

				Settings.System.putString(context.getContentResolver(), "PiSC" + i, "com.pilockerstable");

			}
		}
		
		
		start = (CheckBox) findViewById(R.id.start);
		secret = (CheckBox) findViewById(R.id.checkBox1);
		skip = (CheckBox) findViewById(R.id.checkBox3);
		DoubleTap = (CheckBox) findViewById(R.id.checkBox4);
		autoy = (CheckBox) findViewById(R.id.checkBox6);
		
		screentext = (Button) findViewById(R.id.screentext);
		background = (Button) findViewById(R.id.background);
		screentextcolor = (Button) findViewById(R.id.screentextcolor);
		help = (Button) findViewById(R.id.help);
		pin = (Button) findViewById(R.id.button1);
		shortb = (Button) findViewById(R.id.shortcut);
		button2 = (Button) findViewById(R.id.button2);
		locknow = (Button) findViewById(R.id.button3);

		setDots = (Button)findViewById(R.id.buttonSetDots);
		setNumofColumns = (Button)findViewById(R.id.buttonSetColumns);
		setNumofRows = (Button)findViewById(R.id.buttonSetRows);

		policyManager = (DevicePolicyManager) context.getSystemService(Context.DEVICE_POLICY_SERVICE);
		adminReceiver = new ComponentName(context, DeviceAdmin.class);
		admin = policyManager.isAdminActive(adminReceiver);

		loadon();
		loadX();

		updateNumColumnsAndRows();
		
		shortb.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {

				startActivity(new Intent(MainActivity.this,	ShortcutSettings.class));

			}
		});

		
		
		


		
		locknow.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {

				if (skip.getText().equals("1")) {

					if(!spf.getString("hashedDots","").equalsIgnoreCase("")){
						startActivity(new Intent(MainActivity.this, RandomizedDotPattern.class));
						finish();
					}else if(!spf.getString("pin","").equalsIgnoreCase("")){
						startActivity(new Intent(MainActivity.this, PinActivity.class));
						finish();
					}

				} else {

					startActivity(new Intent(MainActivity.this, Lock.class));
					finish();

				}

			}
		});

		
		
		
		pin.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {

				if(!spf.getString("hashedDots", "").equalsIgnoreCase("")){
					new AlertDialog.Builder(MainActivity.this)
							.setTitle("Set PIN")
							.setMessage("Setting your PIN would reset your Dots Pattern. \nAre you sure you want to set PIN?")
							.setPositiveButton("YES", new DialogInterface.OnClickListener() {

								public void onClick(DialogInterface dialog, int which) {

									input1 = new EditText(context);
									final String getPas = getString("pass");

									AlertDialog.Builder alert = new AlertDialog.Builder(context);

									alert.setMessage("Please write here\n\nYou should only write numbers 0-9.");
									alert.setTitle("Enter New Pin");

									alert.setView(input1);
									alert.setPositiveButton("Confirm", new DialogInterface.OnClickListener() {

										public void onClick(DialogInterface dialog, int whichButton) {

											String p = input1.getEditableText().toString();

											if (p.contains("a") || p.contains("b")
													|| p.contains("c") || p.contains("d")
													|| p.contains("e") || p.contains("f")
													|| p.contains("g") || p.contains("h")
													|| p.contains("i") || p.contains("j")
													|| p.contains("k") || p.contains("l")
													|| p.contains("m") || p.contains("n")
													|| p.contains("o") || p.contains("p")
													|| p.contains("r") || p.contains("s")
													|| p.contains("t") || p.contains("u")
													|| p.contains("v") || p.contains("w")
													|| p.contains("q") || p.contains("x")
													|| p.contains("y") || p.contains("z")) {

												Toast.makeText(context, "The pin only can hold numbers from 0 to 9", Toast.LENGTH_LONG).show();

											} else {

												if (p.trim().length() < 4) {

													Toast.makeText(context, "Pin Must be atleast 4 Characters, try again", Toast.LENGTH_SHORT).show();

												} else if (p.trim().length() >= 4 && p.trim().length() <= 12) {

													if (getPas == "") {

														final String inputtedPIN = p;

														save("pass", "");
														save("pin", inputtedPIN);
														save("hashedDots", "");

														save("numOfColumns",3);
														save("numOfRows",3);

														updateNumColumnsAndRows();

														Toast.makeText(context, "PIN Updated", Toast.LENGTH_SHORT).show();

														skip.setEnabled(true);
														autoy.setEnabled(true);


													}


												} else if (p.trim().length() > 12) {

													Toast.makeText(context, "The password must be less than 12 characters", Toast.LENGTH_SHORT).show();

												}

											}
										}

									});


									alert.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {

										public void onClick(DialogInterface dialog, int whichButton) {

											dialog.cancel();

										}
									});


									AlertDialog alertDialog = alert.create();
									alertDialog.show();

								}
							})

							.setNegativeButton("NO", new DialogInterface.OnClickListener() {

								public void onClick(DialogInterface dialog, int which) {

									dialog.cancel();

								}

							}).setIcon(R.drawable.ic_launcher).show();
				}else{
					input1 = new EditText(context);
					final String getPas = getString("pass");

					AlertDialog.Builder alert = new AlertDialog.Builder(context);

					alert.setMessage("Please write here\n\nYou should only write numbers 0-9.");
					alert.setTitle("Enter New Pin");

					alert.setView(input1);
					alert.setPositiveButton("Confirm", new DialogInterface.OnClickListener() {

						public void onClick(DialogInterface dialog, int whichButton) {

							String p = input1.getEditableText().toString();

							if (p.contains("a") || p.contains("b")
									|| p.contains("c") || p.contains("d")
									|| p.contains("e") || p.contains("f")
									|| p.contains("g") || p.contains("h")
									|| p.contains("i") || p.contains("j")
									|| p.contains("k") || p.contains("l")
									|| p.contains("m") || p.contains("n")
									|| p.contains("o") || p.contains("p")
									|| p.contains("r") || p.contains("s")
									|| p.contains("t") || p.contains("u")
									|| p.contains("v") || p.contains("w")
									|| p.contains("q") || p.contains("x")
									|| p.contains("y") || p.contains("z")) {

								Toast.makeText(context, "The pin only can hold numbers from 0 to 9", Toast.LENGTH_LONG).show();

							} else {

								if (p.trim().length() < 4) {

									Toast.makeText(context, "Pin Must be atleast 4 Characters, try again", Toast.LENGTH_SHORT).show();

								} else if (p.trim().length() >= 4 && p.trim().length() <= 12) {

									if (getPas == "") {

										final String inputtedPIN = p;

										save("pass", "");
										save("pin", inputtedPIN);
										save("hashedDots", "");

										save("numOfColumns",3);
										save("numOfRows",3);

										updateNumColumnsAndRows();

										Toast.makeText(context, "PIN Updated", Toast.LENGTH_SHORT).show();

										skip.setEnabled(true);
										autoy.setEnabled(true);


									}


								} else if (p.trim().length() > 12) {

									Toast.makeText(context, "The password must be less than 12 characters", Toast.LENGTH_SHORT).show();

								}

							}
						}

					});


					alert.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {

						public void onClick(DialogInterface dialog, int whichButton) {

							dialog.cancel();

						}
					});


					AlertDialog alertDialog = alert.create();
					alertDialog.show();
				}


			}
		});

		setDots.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if(!spf.getString("pin","").equalsIgnoreCase("")){
					new AlertDialog.Builder(MainActivity.this)
							.setTitle("Set Dot Pattern")
							.setMessage("Setting dot pattern will reset PIN \nAre you sure to set dots pattern?")
							.setPositiveButton("OK", new DialogInterface.OnClickListener() {

								public void onClick(DialogInterface dialog, int which) {
									save("pin","");

									startSetDotsPattern();
								}
							})

							.setNegativeButton("NO", new DialogInterface.OnClickListener() {

								public void onClick(DialogInterface dialog, int which) {

									dialog.cancel();

								}

							}).setIcon(R.drawable.ic_launcher).show();

				}else{
					startSetDotsPattern();
				}
			}
		});

		setNumofColumns.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if(!spf.getString("hashedDots","").equalsIgnoreCase("")){
					new AlertDialog.Builder(MainActivity.this)
							.setTitle("Set Number of Columns")
							.setMessage("Setting number of columns will reset Security \nAre you sure to set number of columns?")
							.setPositiveButton("OK", new DialogInterface.OnClickListener() {

								public void onClick(DialogInterface dialog, int which) {

									AlertDialog.Builder builderSingle = new AlertDialog.Builder(context);
									builderSingle.setIcon(R.drawable.ic_launcher);
									builderSingle.setTitle("Select Number of Columns");

									final ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(context, android.R.layout.select_dialog_item);
									arrayAdapter.add("3");
									arrayAdapter.add("4");
									arrayAdapter.add("5");

									builderSingle.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
										@Override
										public void onClick(DialogInterface dialog, int which) {
											dialog.cancel();
										}
									});

									builderSingle.setAdapter(arrayAdapter, new DialogInterface.OnClickListener() {
										@Override
										public void onClick(DialogInterface dialog, int which) {
											String number = arrayAdapter.getItem(which);

											save("numOfColumns", Integer.parseInt(number));
											save("hashedDots","");
											save("pin","");

											skip.setEnabled(false);
											autoy.setEnabled(false);

											updateNumColumnsAndRows();
										}
									});
									builderSingle.show();

								}
							})

							.setNegativeButton("NO", new DialogInterface.OnClickListener() {

								public void onClick(DialogInterface dialog, int which) {

									dialog.cancel();

								}

							}).setIcon(R.drawable.ic_launcher).show();

				}else{
					AlertDialog.Builder builderSingle = new AlertDialog.Builder(context);
					builderSingle.setIcon(R.drawable.ic_launcher);
					builderSingle.setTitle("Select Number of Columns");

					final ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(context, android.R.layout.select_dialog_item);
					arrayAdapter.add("3");
					arrayAdapter.add("4");
					arrayAdapter.add("5");

					builderSingle.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog, int which) {
							dialog.cancel();
						}
					});

					builderSingle.setAdapter(arrayAdapter, new DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog, int which) {
							String number = arrayAdapter.getItem(which);

							save("numOfColumns", Integer.parseInt(number));
							save("hashedDots","");
							save("pin","");

							skip.setEnabled(false);
							autoy.setEnabled(false);

							updateNumColumnsAndRows();
						}
					});
					builderSingle.show();
				}
			}
		});
		
		setNumofRows.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if(!spf.getString("hashedDots","").equalsIgnoreCase("")){
					new AlertDialog.Builder(MainActivity.this)
							.setTitle("Set Number of Rows")
							.setMessage("Setting number of rows will reset Security \nAre you sure to set number of rows?")
							.setPositiveButton("OK", new DialogInterface.OnClickListener() {

								public void onClick(DialogInterface dialog, int which) {

									AlertDialog.Builder builderSingle = new AlertDialog.Builder(context);
									builderSingle.setIcon(R.drawable.ic_launcher);
									builderSingle.setTitle("Select Number of Rows");

									final ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(context, android.R.layout.select_dialog_item);
									arrayAdapter.add("3");
									arrayAdapter.add("4");
									arrayAdapter.add("5");

									builderSingle.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
										@Override
										public void onClick(DialogInterface dialog, int which) {
											dialog.cancel();
										}
									});

									builderSingle.setAdapter(arrayAdapter, new DialogInterface.OnClickListener() {
										@Override
										public void onClick(DialogInterface dialog, int which) {
											String number = arrayAdapter.getItem(which);

											save("numOfRows", Integer.parseInt(number));
											save("hashedDots","");
											save("pin","");

											skip.setEnabled(false);
											autoy.setEnabled(false);

											updateNumColumnsAndRows();
										}
									});
									builderSingle.show();

								}
							})

							.setNegativeButton("NO", new DialogInterface.OnClickListener() {

								public void onClick(DialogInterface dialog, int which) {

									dialog.cancel();

								}

							}).setIcon(R.drawable.ic_launcher).show();

				}else{
					AlertDialog.Builder builderSingle = new AlertDialog.Builder(context);
					builderSingle.setIcon(R.drawable.ic_launcher);
					builderSingle.setTitle("Select Number of Rows");

					final ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(context, android.R.layout.select_dialog_item);
					arrayAdapter.add("3");
					arrayAdapter.add("4");
					arrayAdapter.add("5");

					builderSingle.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog, int which) {
							dialog.cancel();
						}
					});

					builderSingle.setAdapter(arrayAdapter, new DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog, int which) {
							String number = arrayAdapter.getItem(which);

							save("numOfRows", Integer.parseInt(number));
							save("hashedDots","");
							save("pin","");

							skip.setEnabled(false);
							autoy.setEnabled(false);

							updateNumColumnsAndRows();
						}
					});
					builderSingle.show();
				}
			}
		});
		
		button2.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {

				new AlertDialog.Builder(MainActivity.this)
						.setTitle("Reset Pin/Password/Dot Pattern")
						.setMessage("Are you sure you want to reset security? \nthis will leave your phone UNSECURED")
						.setPositiveButton("Reset", new DialogInterface.OnClickListener() {

									public void onClick(DialogInterface dialog, int which) {

										save("pin", "");
										skip.setEnabled(false);
										autoy.setEnabled(false);

										save("hashedDots","");
										save("numOfColumns",3);
										save("numOfRows",3);

										updateNumColumnsAndRows();

									}
								})

						.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {

									public void onClick(DialogInterface dialog, int which) {
										dialog.cancel();
									}

								}).setIcon(R.drawable.ic_launcher).show();

			}

		});


		skip.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {

				if (skip.isChecked()) {

					save("skip", "1");

				} else {

					save("skip", "0");

				}

			}
		});

		
		
		DoubleTap.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {

				if (DoubleTap.isChecked()) {

					if (admin) {

						save("tap", "enable");

					} else {

						Toast.makeText(context,	"Device admin is not enabled!\nEnable from\nSetting > Security > Device admin\nin order to use this feature", Toast.LENGTH_LONG).show();
						DoubleTap.setChecked(false);
						
					}

				} else {

					save("tap", "disable");

				}

			}
		});

		autoy.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {

				if (autoy.isChecked()) {

					
					save("auto", "true");

				} else {

					save("auto", "no");

				}

			}
		});

		secret.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {

				if (secret.isChecked()) {

					save("secret", "true");

					new AlertDialog.Builder(MainActivity.this)
							.setTitle("Secret Emergency Unlock")
							.setMessage("This feature was made to give you an easier way to unlock screen when you are in the following situations:\n1. You're in a hurry.\n2. You're in an emergency.\n3. You're in danger.\n4. You forgot your dots pattern.\n\nUsage:\n\nA. Gesture\n-Press on the clock text.\n\nB. Dots Pattern\n-Press on the text multiple times as much as the number of columns or rows of dots.\n\nNote:\n1. This won't work when the pin security is active.\n2. Emergency unlock method in gesture mode (Usage A. Gesture) won't work if Dots Pattern is active.")
							.setPositiveButton("Activate!", new DialogInterface.OnClickListener() {
								
										public void onClick(DialogInterface dialog, int which) {

											save("emergency", "true");
											save("emergencyb", "true");
										}
									})

									
							.setNegativeButton("No", new DialogInterface.OnClickListener() {
								@Override
								public void onClick(DialogInterface dialog, int which) {
									secret.setChecked(false);

									save("emergency", "false");
									save("emergencyb", "false");
								}
							})
							.setCancelable(false)
							.show();

				} else {
					
					save("secret", "true");
					save("emergency", "false");
					save("emergencyb", "false");
				}

			}
		});



		help.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {

				startActivity(new Intent(MainActivity.this, Help.class));
			}
		});

		
		
		screentextcolor.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {

				if (start.isChecked()) {

					AmbilWarnaDialog dialog = new AmbilWarnaDialog(MainActivity.this, 0xffffffff, new OnAmbilWarnaListener() {

								@Override
								public void onOk(AmbilWarnaDialog dialog, int color) {

									save("color", color + "");

								}

								@Override
								public void onCancel(AmbilWarnaDialog dialog) {

								}
							});

					dialog.show();

				} else {

					Toast.makeText(getApplicationContext(), "You have to start Pi Locker first", Toast.LENGTH_SHORT).show();
				}
			}

		});

		
		background.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				if (start.isChecked()) {

					AlertDialog.Builder alert = new AlertDialog.Builder(context);
					
					alert.setTitle("Backround")
						 .setMessage("Select Background type")
						 .setPositiveButton("Picture", new DialogInterface.OnClickListener() {

										public void onClick(DialogInterface dialog, int whichButton) {

											openGallery();

										}
									})

									
							.setNeutralButton("Reset", new DialogInterface.OnClickListener() {

										public void onClick(DialogInterface dialog, int whichButton) {

											save("colorbg", "");
											save("img", "");
											save("color", "");

										}
									});
					
					AlertDialog alertDialog = alert.create();
					alertDialog.show();

				} else {
					
					Toast.makeText(getApplicationContext(), "You have to start Pi Locker first", Toast.LENGTH_SHORT).show();
				}
			}
		});

		
		screentext.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				
				if (start.isChecked()) {

					AlertDialog.Builder alert = new AlertDialog.Builder(context);
					alert.setTitle("Personal Text");
					alert.setMessage("Please write here");
					final EditText input1 = new EditText(context);
					alert.setView(input1);

					alert.setPositiveButton("Set text",
							new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface dialog,
										int whichButton) {

									srt = input1.getEditableText().toString();
									save("text", srt);
								}
							});

					alert.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {

								public void onClick(DialogInterface dialog, int whichButton) {

									dialog.cancel();
								}
							});

					AlertDialog alertDialog = alert.create();
					alertDialog.show();

				} else {
					
					Toast.makeText(getApplicationContext(), "You have to start Pi Locker first", Toast.LENGTH_SHORT).show();
					
				}
			}
		});

		
		start.setOnClickListener(new OnClickListener() {

			public void onClick(View v) {

				if (start.isChecked()) {

					Settings.System.putInt(getContentResolver(), "PiLocker", 1);
					startService(new Intent(MainActivity.this, LockerService.class));
					save("on", "true");
					secret.setEnabled(true);
					skip.setEnabled(true);
					DoubleTap.setEnabled(true);
					

				} else {

					Settings.System.putInt(getContentResolver(), "PiLocker", 0);
					stopService(new Intent(MainActivity.this,LockerService.class));
					startService(new Intent(MainActivity.this,LockerService.class));
					save("on", "false");
					secret.setEnabled(false);
					skip.setEnabled(false);
					DoubleTap.setEnabled(false);
					
					Intent i = new Intent();
					i.setAction("com.androidfire.Restore_Pi");
					sendBroadcast(i);

				}
			}
		});

	}

	public void updateNumColumnsAndRows(){

		numOfColumns = spf.getInt("numOfColumns", 3);
		numOfRows = spf.getInt("numOfRows", 3);
		setNumofColumns.setText("     Number of Columns = ".concat(String.valueOf(numOfColumns)));
		setNumofRows.setText("     Number of Rows = ".concat(String.valueOf(numOfRows)));

	}

	public void save(String key, String value) {
		
		spf = PreferenceManager.getDefaultSharedPreferences(this);
		Editor edit = spf.edit();
		edit.putString(key, value);
		edit.commit();

	}

	public void save(String key, int value) {

		spf = PreferenceManager.getDefaultSharedPreferences(this);
		Editor edit = spf.edit();
		edit.putInt(key, value);
		edit.commit();

	}

	public String getString(String key) {
		
		spf = PreferenceManager.getDefaultSharedPreferences(this);
		return spf.getString(key, "");

	}

	public void loadon() {

		spf = PreferenceManager.getDefaultSharedPreferences(this);
		on = spf.getString("on", "");
		xx = spf.getString("xx", "");
		se = spf.getString("emergencyb", "false");
		skips = spf.getString("skip", "0");
		tap = spf.getString("tap", "disable");
		lock = spf.getString("lock", "");
		Pass = spf.getString("pass", "");
		Pin = spf.getString("pin", "");
	    auto = spf.getString("auto", "");

		numOfColumns = spf.getInt("numOfColumns",3);
		numOfRows = spf.getInt("numOfRows",3);
		hashedDots = spf.getString("hashedDots","");

		cost = getOptimalBCryptCostParameter(150);

		if (Pass.equals("") && Pin.equals("") && hashedDots.equals("")) {

			autoy.setEnabled(false);

		}

		if (Pass.equals("") && Pin.equals("") && hashedDots.equals("")) {

			skip.setEnabled(false);

		}

		if (tap.equals("enable")) {
			
			DoubleTap.setChecked(true);

		} else {
			
			DoubleTap.setChecked(false);
		}

		if (skips.equals("1")) {
			
			skip.setChecked(true);

		} else {
			
			skip.setChecked(false);
		}

		if (se.equals("true")) {
			
			secret.setChecked(true);

		} else {
			
			secret.setChecked(false);
		}

		if (auto.equals("true")) {
			
			autoy.setChecked(true);

		} else {
			
			autoy.setChecked(false);
		}

		if (on.equals("false")) {
			
			secret.setEnabled(false);
			skip.setEnabled(false);
			DoubleTap.setEnabled(false);
			autoy.setEnabled(false);

		}
		if (on.equals("true")) {
			
			start.setChecked(true);
			startService(new Intent(MainActivity.this, LockerService.class));

		}

	}

	public void loadX() {

		if (xx.equals("one")) {

		} else {

			Intent i = new Intent(MainActivity.this, Help.class);
			startActivity(i);
			xx = "one";
			save("xx", "one");

		}

	}

	private void openGallery() {

		mFileTemp = new File(getFilesDir(), TEMP_PHOTO_FILE_NAME);
		Intent photoPickerIntent = new Intent(Intent.ACTION_PICK);
		photoPickerIntent.setType("image/*");
		startActivityForResult(photoPickerIntent, REQUEST_CODE_GALLERY);

	}

	private void startCropImage() {

		Intent intent = new Intent(this, CropImage.class);
		
		intent.putExtra(CropImage.IMAGE_PATH, mFileTemp.getPath());
		intent.putExtra(CropImage.SCALE, true);

		intent.putExtra(CropImage.ASPECT_X, 2);
		intent.putExtra(CropImage.ASPECT_Y, 4);

		startActivityForResult(intent, REQUEST_CODE_CROP_IMAGE);
	}

	private void startSetDotsPattern(){

		Intent intent = new Intent(this, SetRandomizedDotPattern.class);

		intent.putExtra("numOfColumns",numOfColumns);
		intent.putExtra("numOfRows",numOfRows);

		startActivityForResult(intent, REQUEST_CODE_SET_RANDOMIZED_DOT_PATTERN);

	}

	public int getOptimalBCryptCostParameter(long min_ms) {
		for (int i = 5; i < 31; i++) {
			long time_start = System.currentTimeMillis();
			BCrypt.hashpw("test",BCrypt.gensalt(i));
			long time_end = System.currentTimeMillis();
			if ((time_end - time_start) * 1000 > min_ms) {
				return i;
			}
		}
		return 10;
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {

		if (resultCode != RESULT_OK) {

			return;
		}

		switch (requestCode) {

		case REQUEST_CODE_GALLERY:

			try {

				InputStream inputStream = getContentResolver().openInputStream(data.getData());
				FileOutputStream fileOutputStream = new FileOutputStream(mFileTemp);
				copyStream(inputStream, fileOutputStream);
				fileOutputStream.close();
				inputStream.close();

				startCropImage();

			} catch (Exception e) {

				Log.e(TAG, "Error while creating temp file", e);
			}

			break;
			
		case REQUEST_CODE_TAKE_PICTURE:

			startCropImage();
			break;
			
		case REQUEST_CODE_CROP_IMAGE:

			String path = data.getStringExtra(CropImage.IMAGE_PATH);
			if (path == null) {

				return;
			}

			save("img", mFileTemp.getPath());

			break;

			//Set Pattern
			case REQUEST_CODE_SET_RANDOMIZED_DOT_PATTERN:

				save("hashedDots",BCrypt.hashpw(data.getStringExtra("dots"),BCrypt.gensalt(cost)));
				skip.setEnabled(true);
				autoy.setEnabled(true);

				break;

		}
		super.onActivityResult(requestCode, resultCode, data);
	}

	public static void copyStream(InputStream input, OutputStream output) throws IOException {

		byte[] buffer = new byte[1024];
		int bytesRead;
		
		while ((bytesRead = input.read(buffer)) != -1) {
			
			output.write(buffer, 0, bytesRead);
		}
	}
	
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		
		
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
		
	}

	
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {

		if (item.getItemId() == R.id.about) {

		
			startActivity(new Intent(MainActivity.this, About.class));

			
			finish();
			
		}

		if (item.getItemId() == R.id.ssss) {
			

			
			    Uri uri = Uri.parse("https://play.google.com/store/apps/developer?id=Pi-Developers");
			    Intent intent = new Intent(Intent.ACTION_VIEW, uri);
			    startActivity(intent);
			
			    finish();

		}

		if (item.getItemId() == R.id.more) {

		
				Intent sharingIntent = new Intent(Intent.ACTION_SEND);
				sharingIntent.setType("text/plain");
				String shareBody = "Get Pi Locker The Best Lockscreen for android now!!!\n\nGet it here : https://play.google.com/store/apps/details?id=com.pilockerstable";
				sharingIntent.putExtra(Intent.EXTRA_TEXT, shareBody);
				startActivity(Intent.createChooser(sharingIntent, "Share using"));

                finish();

		}

		

		return super.onOptionsItemSelected(item);

	}
	

}