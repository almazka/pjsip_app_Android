package com.telefon.ufanet;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.KeyguardManager;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Configuration;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.PowerManager;
import android.os.Vibrator;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.Display;
import android.view.Surface;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.telefon.ufanet.MVP.VOIP.PJSIPApp;
import com.telefon.ufanet.MVP.VOIP.Service;

import org.pjsip.pjsua2.AccountConfig;
import org.pjsip.pjsua2.CallInfo;
import org.pjsip.pjsua2.CallOpParam;
import org.pjsip.pjsua2.pjmedia_orient;
import org.pjsip.pjsua2.pjsip_inv_state;
import org.pjsip.pjsua2.pjsip_role_e;
import org.pjsip.pjsua2.pjsip_status_code;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;



public class CallActivity extends AppCompatActivity
		implements Handler.Callback
{

	public static Handler handler_;

	private final Handler handler = new Handler(this);
	private static CallInfo lastCallInfo;
	TextView time;

	public MediaPlayer ring;
	DateFormat df ;
	String date;
	SensorManager sensorManager;
	Sensor sensorLight;
	public String call_contact_name = "";
	public Intent intent;

	IncomingCallReceiver incomingCallReceiver;

	Thread t;

	int i = 0;
	int j = 0;

	String from_act;

	String remoteURI;

	Button b_end, i1;


	@SuppressLint({"ClickableViewAccessibility", "SimpleDateFormat"})
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.call_activity);
		b_end = (Button)findViewById(R.id.buttonend);
		df = new SimpleDateFormat("EEE, d MMM yyyy, HH:mm");
		date = df.format(Calendar.getInstance().getTime());

		final LinearLayout speakers = (LinearLayout) findViewById(R.id.speakers);
		final ImageButton speaker = (ImageButton) findViewById(R.id.speaker);
		time = (TextView) findViewById(R.id.text_time);
		final ImageButton microphone = (ImageButton) findViewById(R.id.micro);
		final ImageView imageView3, imageView4;

		ring = MediaPlayer.create(this,R.raw.ring);

		View decorView = getWindow().getDecorView();
		// Hide the status bar.
		int uiOptions = View.SYSTEM_UI_FLAG_FULLSCREEN;
		decorView.setSystemUiVisibility(uiOptions);

		androidx.appcompat.app.ActionBar bar = getSupportActionBar();
		bar.hide();

		final int[] y = new int[2];
		final int[] x = new int[2];
		final int[] a = new int[2];
		final int[] b = new int[2];
		i1 = (Button) findViewById(R.id.buttonacept);
		final Button call_end = (Button) findViewById(R.id.buttonHangup);
		final Button call_end1 = (Button) findViewById(R.id.buttonHangup1);


		i1.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				Vibrator vibrator = (Vibrator) getSystemService(VIBRATOR_SERVICE);
				b_end.setVisibility(View.GONE);

				vibrator.cancel();
				CallOpParam prm = new CallOpParam();
				prm.setStatusCode(pjsip_status_code.PJSIP_SC_OK);
//
				try {
					Service.currentCall.answer(prm);
					ring.stop();
				} catch (Exception e) {
					System.out.println(e);
				}
				speakers.setVisibility(View.VISIBLE);
				i1.setVisibility(View.GONE);
				//imageView3.setVisibility(View.GONE);
				//imageView4.setVisibility(View.GONE);
				call_end1.setVisibility(View.VISIBLE);
			}
		});

		b_end.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				Vibrator vibrator = (Vibrator) getSystemService(VIBRATOR_SERVICE);
				handler_ = null;
				i1.setVisibility(View.GONE);

				if (Service.currentCall != null) {
					CallOpParam prm = new CallOpParam();
					prm.setStatusCode(pjsip_status_code.PJSIP_SC_DECLINE);
					try {
						Service.currentCall.hangup(prm);
						vibrator.cancel();
						ring.stop();
						//imageView3.setVisibility(View.GONE);
						//imageView4.setVisibility(View.GONE);
						b_end.setVisibility(View.GONE);

						long callTimeInMiliSecond = System.currentTimeMillis();
						String numberStr = remoteURI;
						ContentValues cv1 = new ContentValues();
						cv1.put("name", numberStr);
						cv1.put("number", numberStr);
						cv1.put("type", "Входящий");
						cv1.put("date", date);
						cv1.put("duration", i);
						MainApp.database.insert("RecentCalls", null, cv1);


					} catch (Exception e) {
						System.out.println(e);
					}
				}
				unregisterReceiver(incomingCallReceiver);
				finish();
			}

		});




		time.setText(String.valueOf(i));
		time.setVisibility(View.GONE);

		 t = new Thread() {

			@Override
			public void run() {
				try {
					while (!isInterrupted()) {
						Thread.sleep(1000);
						runOnUiThread(new Runnable() {
							@Override
							public void run() {
								time.setText(String.valueOf(i));
								i++;
							}
						});
					}
				} catch (InterruptedException e) {
				}
			}
		};



		intent = getIntent();
		call_contact_name = intent.getStringExtra("call_contact_name");
		from_act = intent.getStringExtra("from");

		if (call_contact_name == null) {
			call_contact_name = "";
		}


		if (Service.currentCall == null ||
				Service.currentCall.vidWin == null)
		{

		}

		KeyguardManager.KeyguardLock lock = ((KeyguardManager) getSystemService(Activity.KEYGUARD_SERVICE)).newKeyguardLock(KEYGUARD_SERVICE);
		PowerManager powerManager = ((PowerManager) getSystemService(Context.POWER_SERVICE));
		@SuppressLint("InvalidWakeLockTag") PowerManager.WakeLock wake = powerManager.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK | PowerManager.ACQUIRE_CAUSES_WAKEUP, "TAG");

		lock.disableKeyguard();
		wake.acquire();



		Window window = getWindow();
		window.addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);


		this.getWindow().setType(WindowManager.LayoutParams.TYPE_SYSTEM_OVERLAY);



		getWindow().addFlags(WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED
				| WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD
				| WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON
				| WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON
				| WindowManager.LayoutParams.FLAG_ALLOW_LOCK_WHILE_SCREEN_ON);


		incomingCallReceiver = new IncomingCallReceiver();
		registerReceiver(incomingCallReceiver, new IntentFilter(
				TelephonyManager.ACTION_PHONE_STATE_CHANGED));



		sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
		sensorLight = sensorManager.getDefaultSensor(Sensor.TYPE_PROXIMITY);

		SensorEventListener listenerLight = new SensorEventListener() {

			@Override
			public void onAccuracyChanged(Sensor sensor, int accuracy) {
			}

			@Override
			public void onSensorChanged(SensorEvent event) {
				if (event.values[0] < 3) {
					speaker.setClickable(false);
					microphone.setClickable(false);
					//sw.setEnabled(false);
					//sw1.setEnabled(false);
					call_end.setClickable(false);
					call_end1.setClickable(false);
					i1.setEnabled(false);
					b_end.setEnabled(false);

					WindowManager.LayoutParams params = getWindow().getAttributes();
					params.flags |= WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON;
					params.screenBrightness = 0;
					getWindow().setAttributes(params);

				}

				else {
					speaker.setClickable(true);
					microphone.setClickable(true);
					//sw.setEnabled(true);
					//sw1.setEnabled(true);
					call_end.setClickable(true);
					call_end1.setClickable(true);
					i1.setEnabled(true);
					b_end.setEnabled(true);



					WindowManager.LayoutParams params = getWindow().getAttributes();
					params.flags |= WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON;
					params.screenBrightness = 50;
					getWindow().setAttributes(params);
				}
			}
		};



		sensorManager.registerListener(listenerLight, sensorLight,
				SensorManager.SENSOR_DELAY_NORMAL);


		final AudioManager audioManager = (AudioManager) getSystemService(AUDIO_SERVICE);
		audioManager.setSpeakerphoneOn(false);

		handler_ = handler;
		if (Service.currentCall != null) {
			try {
				lastCallInfo = Service.currentCall.getInfo();
				if (lastCallInfo != null) {
					updateCallState(lastCallInfo);
				}
			} catch (Exception e) {
				System.out.println(e);
			}
		} else {
		}


		speaker.setOnClickListener(new View.OnClickListener() {
			@RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
			@Override
			public void onClick(View view) {

				if (audioManager.isSpeakerphoneOn() == false) {
					audioManager.setSpeakerphoneOn(true);
					speaker.setBackground(getResources().getDrawable(R.drawable.headphones));
				}
				else {
					speaker.setBackground(getResources().getDrawable(R.drawable.speaker1));
					audioManager.setSpeakerphoneOn(false);}

			}
		});

		microphone.setOnClickListener(new View.OnClickListener() {
			@RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
			@Override
			public void onClick(View view) {


				if (audioManager.isMicrophoneMute() == false) {
					audioManager.setMicrophoneMute(true);
					microphone.setBackground(getResources().getDrawable(R.drawable.micro_green));
				}
				else {
					microphone.setBackground(getResources().getDrawable(R.drawable.micro_red));
					audioManager.setMicrophoneMute(false);}

			}
		});


	}




	@Override
	protected void onPause() {
		super.onPause();
	}

	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		super.onConfigurationChanged(newConfig);

		WindowManager wm;
		Display display;
		int rotation;
		pjmedia_orient orient;

		wm = (WindowManager)this.getSystemService(Context.WINDOW_SERVICE);
		display = wm.getDefaultDisplay();
		rotation = display.getRotation();
		System.out.println("Device orientation changed: " + rotation);

		switch (rotation) {
			case Surface.ROTATION_0:   // Portrait
				orient = pjmedia_orient.PJMEDIA_ORIENT_ROTATE_270DEG;
				break;
			case Surface.ROTATION_90:  // Landscape, home button on the right
				orient = pjmedia_orient.PJMEDIA_ORIENT_NATURAL;
				break;
			case Surface.ROTATION_180:
				orient = pjmedia_orient.PJMEDIA_ORIENT_ROTATE_90DEG;
				break;
			case Surface.ROTATION_270: // Landscape, home button on the left
				orient = pjmedia_orient.PJMEDIA_ORIENT_ROTATE_180DEG;
				break;
			default:
				orient = pjmedia_orient.PJMEDIA_ORIENT_UNKNOWN;
		}

		if (PJSIPApp.ep != null && Service.account != null) {
			try {
				AccountConfig cfg = Service.account.cfg;
				int cap_dev = cfg.getVideoConfig().getDefaultCaptureDevice();
				PJSIPApp.ep.vidDevManager().setCaptureOrient(cap_dev, orient,
						true);
			} catch (Exception e) {
				System.out.println(e);
			}
		}
	}

	@Override
	protected void onDestroy()
	{
		super.onDestroy();
		handler_ = null;
	}



	public void acceptCall(View view)
	{
		CallOpParam prm = new CallOpParam();
		prm.setStatusCode(pjsip_status_code.PJSIP_SC_OK);
		try {
			Service.currentCall.answer(prm);
		} catch (Exception e) {
			System.out.println(e);
		}

		view.setVisibility(View.GONE);
	}


	public void hangupCall(View view)
	{
		Vibrator vibrator = (Vibrator) getSystemService(VIBRATOR_SERVICE);
		handler_ = null;

		if (Service.currentCall != null) {
			CallOpParam prm = new CallOpParam();
			prm.setStatusCode(pjsip_status_code.PJSIP_SC_DECLINE);
			try {

				Service.currentCall.hangup(prm);
				vibrator.cancel();
				long callTimeInMiliSecond = System.currentTimeMillis();
				String numberStr = remoteURI;
				if (from_act.contains("main")) {
					ContentValues cv1 = new ContentValues();
					cv1.put("name", numberStr);
					cv1.put("number", numberStr);
					cv1.put("type", "Исходящий");
					cv1.put("date", date);
					cv1.put("duration", i);
					MainApp.database.insert("RecentCalls", null, cv1);
				}
				else if (from_act.contains("service")) {
					ContentValues cv1 = new ContentValues();
					cv1.put("name", numberStr);
					cv1.put("number", numberStr);
					cv1.put("type", "Входящий");
					cv1.put("date", date);
					cv1.put("duration", i);
					MainApp.database.insert("RecentCalls", null, cv1);
				}

			} catch (Exception e) {
				System.out.println(e);
			}

		}
		unregisterReceiver(incomingCallReceiver);
		finish();

	}

	@Override
	public void onBackPressed() {
		if (j != 1) {
			Toast.makeText(this, "Нажмите еще раз чтобы завершить вызов и выйти", Toast.LENGTH_LONG).show();
			j = j + 1;
		}
		else {
			ring.stop();
			Vibrator vibrator = (Vibrator) getSystemService(VIBRATOR_SERVICE);
			handler_ = null;

			if (Service.currentCall != null) {
				CallOpParam prm = new CallOpParam();
				prm.setStatusCode(pjsip_status_code.PJSIP_SC_DECLINE);
				try {
					Service.currentCall.hangup(prm);
					vibrator.cancel();
				} catch (Exception e) {
					System.out.println(e);
				}

			}
			unregisterReceiver(incomingCallReceiver);
			finish();

		}

	}

	public void hangupCall1(View view)
	{
		Vibrator vibrator = (Vibrator) getSystemService(VIBRATOR_SERVICE);
		handler_ = null;

		if (Service.currentCall != null) {
			CallOpParam prm = new CallOpParam();
			prm.setStatusCode(pjsip_status_code.PJSIP_SC_DECLINE);
			try {
				Service.currentCall.hangup(prm);
				vibrator.cancel();
				long callTimeInMiliSecond = System.currentTimeMillis();
				String numberStr = remoteURI;
				if (from_act.contains("main")) {
					ContentValues cv1 = new ContentValues();
					cv1.put("name", numberStr);
					cv1.put("number", numberStr);
					cv1.put("type", "Исходящий");
					cv1.put("date", date);
					cv1.put("duration", i);
					MainApp.database.insert("RecentCalls", null, cv1);
				}
				else if (from_act.contains("service")) {
					ContentValues cv1 = new ContentValues();
					cv1.put("name", numberStr);
					cv1.put("number", numberStr);
					cv1.put("type", "Входящий");
					cv1.put("date", date);
					cv1.put("duration", i);
					MainApp.database.insert("RecentCalls", null, cv1);
				}

			} catch (Exception e) {
				System.out.println(e);
			}

		}
		unregisterReceiver(incomingCallReceiver);
		finish();
	}

	@Override
	public boolean handleMessage(Message m)
	{
		if (m.what == Service.MSG_TYPE.CALL_STATE) {

			lastCallInfo = (CallInfo) m.obj;
			updateCallState(lastCallInfo);

		} else if (m.what == Service.MSG_TYPE.CALL_MEDIA_STATE) {

			if (Service.currentCall.vidWin != null) {
		/* Set capture orientation according to current
		 * device orientation.
		 */
				onConfigurationChanged(getResources().getConfiguration());
		/* If there's incoming video, display it. */
			}

		} else {

	    /* Message not handled */
			return false;

		}

		return true;
	}

	private void updateCallState(CallInfo ci) {
		TextView tvPeer  = (TextView) findViewById(R.id.textViewPeer);
		TextView tvState = (TextView) findViewById(R.id.textViewCallState);
		TextView tvNamePeer = (TextView) findViewById(R.id.namepeer);
		Button buttonHangup = (Button) findViewById(R.id.buttonHangup);
		LinearLayout speakers = (LinearLayout) findViewById(R.id.speakers);
		String call_state = "";
		long[] pattern = { 500, 300, 400, 200 };
		Vibrator vibrator = (Vibrator) getSystemService(VIBRATOR_SERVICE);

		remoteURI = ci.getRemoteUri();
		Integer num = remoteURI.indexOf("sip:", 0);
		remoteURI = remoteURI.substring(num+4, remoteURI.length());
		num = remoteURI.indexOf("@", 0);
		remoteURI = remoteURI.substring(0, num);
		tvNamePeer.setText("");

		for (int i = 0; i < Service.contactListWorker.size(); i++ ) {
			if (remoteURI.contains(Service.contactListWorker.get(i).mail)) {
			    String namecontact = Service.contactListWorker.get(i).header;
				tvNamePeer.setText(namecontact);
				break;
			}
		}



		if (ci.getRole() == pjsip_role_e.PJSIP_ROLE_UAC) {
			//  buttonAccept.setVisibility(View.GONE);
		}

		if (ci.getState().swigValue() <
				pjsip_inv_state.PJSIP_INV_STATE_CONFIRMED.swigValue())
		{
			if (ci.getRole() == pjsip_role_e.PJSIP_ROLE_UAS) {
				call_state = "Входящий вызов";

				if (vibrator.hasVibrator()) {
					vibrator.vibrate(pattern, 2);
				}
				ring.start();
		/* Default button texts are already 'Accept' & 'Reject' */
			} else {
				buttonHangup.setVisibility(View.VISIBLE);

				i1.setVisibility(View.GONE);
				b_end.setVisibility(View.GONE);
				speakers.setVisibility(View.VISIBLE);
				buttonHangup.setText("Отмена");
				vibrator.cancel();
				call_state = ci.getStateText();
			}
		}
		else if (ci.getState().swigValue() >=
				pjsip_inv_state.PJSIP_INV_STATE_CONFIRMED.swigValue())
		{
			//  buttonAccept.setVisibility(View.GONE);
			vibrator.cancel();
			call_state = ci.getStateText();
			if (ci.getState() == pjsip_inv_state.PJSIP_INV_STATE_CONFIRMED) {
				vibrator.cancel();
				buttonHangup.setText("Сбросить");
			} else if (ci.getState() ==
					pjsip_inv_state.PJSIP_INV_STATE_DISCONNECTED)
			{
				ring.stop();
				vibrator.cancel();
				call_state = "Call disconnected: " + ci.getLastReason();
			}
		}

		if (call_state.contains("CONFIRMED")) {
			t.start();
			time.setVisibility(View.VISIBLE);

		}

		else if (call_state.contains("Decline")) {
				long callTimeInMiliSecond = System.currentTimeMillis();
				String numberStr = remoteURI;
				unregisterReceiver(incomingCallReceiver);
				finish();
		}

		else if (call_state.contains("Request")) {
			long callTimeInMiliSecond = System.currentTimeMillis();
			String numberStr = remoteURI;
			ContentValues cv1 = new ContentValues();
			cv1.put("name", numberStr);
			cv1.put("number", numberStr);
			cv1.put("type", "Пропущенный");
			cv1.put("date", date);
			cv1.put("duration", i);
			MainApp.database.insert("RecentCalls", null, cv1);
			unregisterReceiver(incomingCallReceiver);
			finish();
		}

		else if (call_state.contains("Not")) {
			ring.stop();
			time.setVisibility(View.GONE);
			buttonHangup.setVisibility(View.VISIBLE);
			//image_left.setVisibility(View.GONE);
			//image_right.setVisibility(View.GONE);
			i1.setVisibility(View.GONE);
			b_end.setVisibility(View.GONE);
			Toast.makeText(getApplicationContext(),call_state.toString(), Toast.LENGTH_LONG).show();
			buttonHangup.setText("Отмена");
			Handler handler = new Handler();
			handler.postDelayed(new Runnable() {
				@Override
				public void run() {
					unregisterReceiver(incomingCallReceiver);
					finish();
				}
			}, 2000);

		}

		else if (call_state.contains("Forbidden")) {
			ring.stop();
			time.setVisibility(View.GONE);
			buttonHangup.setVisibility(View.VISIBLE);
			i1.setVisibility(View.GONE);
			b_end.setVisibility(View.GONE);
			buttonHangup.setText("Отмена");
			Handler handler = new Handler();
			handler.postDelayed(new Runnable() {
				@Override
				public void run() {
					unregisterReceiver(incomingCallReceiver);
					finish();
				}
			}, 2000);

		}


		else if (call_state.contains("clearing")) {
			if (from_act.contains("main")) {
				long callTimeInMiliSecond = System.currentTimeMillis();
				String numberStr = remoteURI;
				ContentValues cv1 = new ContentValues();
				cv1.put("name", numberStr);
				cv1.put("number", numberStr);
				cv1.put("type", "Исходящий");
				cv1.put("date", date);
				cv1.put("duration", i);
				MainApp.database.insert("RecentCalls", null, cv1);
		}
			else if (from_act.contains("service")) {
				long callTimeInMiliSecond = System.currentTimeMillis();
				String numberStr = remoteURI;
				ContentValues cv1 = new ContentValues();
				cv1.put("name", numberStr);
				cv1.put("number", numberStr);
				cv1.put("type", "Входящий");
				cv1.put("date", date);
				cv1.put("duration", i);
				MainApp.database.insert("RecentCalls", null, cv1);
			}
			unregisterReceiver(incomingCallReceiver);
			finish();

		}

		else if (call_state.contains("Busy")) {
			if (from_act.contains("main")) {
				long callTimeInMiliSecond = System.currentTimeMillis();
				String numberStr = remoteURI;
				ContentValues cv1 = new ContentValues();
				cv1.put("name", numberStr);
				cv1.put("number", numberStr);
				cv1.put("type", "Занято");
				cv1.put("date", date);
				cv1.put("duration", i);
				MainApp.database.insert("RecentCalls", null, cv1);
			}
			else if (from_act.contains("service")) {
				long callTimeInMiliSecond = System.currentTimeMillis();
				String numberStr = remoteURI;
				ContentValues cv1 = new ContentValues();
				cv1.put("name", numberStr);
				cv1.put("number", numberStr);
				cv1.put("type", "Пропущенный");
				cv1.put("date", date);
				cv1.put("duration", i);
				MainApp.database.insert("RecentCalls", null, cv1);
			}
			unregisterReceiver(incomingCallReceiver);
			finish();

		}

		tvPeer.setText(remoteURI);

		tvState.setText(call_state);
		Log.d("Test", call_state.toString());


	}
}
