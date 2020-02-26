package com.telefon.ufanet.MVP.VOIP;

import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.ApplicationInfo;
import android.media.AudioManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.util.Log;
import android.widget.Toast;

import androidx.core.app.NotificationCompat;

import com.telefon.ufanet.CallActivity;
import com.telefon.ufanet.ItemContactsWorkers;
import com.example.ufanet.myapplication.R;

import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.pjsip.pjsua2.AccountConfig;
import org.pjsip.pjsua2.AuthCredInfo;
import org.pjsip.pjsua2.AuthCredInfoVector;
import org.pjsip.pjsua2.CallInfo;
import org.pjsip.pjsua2.CallOpParam;
import org.pjsip.pjsua2.StringVector;
import org.pjsip.pjsua2.pjsip_inv_state;
import org.pjsip.pjsua2.pjsip_status_code;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by sadretdinov_a1109 on 04.07.2018.
 */

public class MyService extends Service
        implements Handler.Callback, MyAppObserver {

    public static MyApp app = null;
    public static MyCall currentCall = null;
    public static MyAccount account = null;
    public static AccountConfig accCfg = null;
    public static String msg_str;

    public String sip_log;
    public String sip_pass;
    public static String token;

    public static ArrayList<ItemContactsWorkers> contactListWorker;

    private final Handler handler = new Handler(this);


    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }


    @Override
    public void onCreate() {

    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.i("Test", "Service: onStartCommand");
        AudioManager audioManager = (AudioManager) getSystemService(AUDIO_SERVICE);
        audioManager.setSpeakerphoneOn(false);

        sip_log = intent.getStringExtra("sip_login");
        sip_pass = intent.getStringExtra("sip_pass");
        token = intent.getStringExtra("token");
        contactListWorker = new ArrayList<ItemContactsWorkers>();
        GetContacts();

        if (sip_log == "" || sip_pass == "") {

        } else {

            if (app == null) {
                app = new MyApp();
                //  Wait for GDB to init, for native debugging only
                if (false &&
                        (getApplicationInfo().flags &
                                ApplicationInfo.FLAG_DEBUGGABLE) != 0) {
                    try {
                        Thread.sleep(5000);
                    } catch (InterruptedException e) {
                    }
                }

                app.init(this, getFilesDir().getAbsolutePath());
            }

            if (app.accList.size() == 0) {
                accCfg = new AccountConfig();

                accCfg.setIdUri("sip:"+sip_log+"@92.50.152.146:5401");
                accCfg.getRegConfig().setRegistrarUri("sip:92.50.152.146:5401");
                AuthCredInfoVector creds = accCfg.getSipConfig().
                        getAuthCreds();
                creds.clear();

                creds.add(new AuthCredInfo("Digest", "*", sip_log, 0,
                        sip_pass));

                StringVector proxies = accCfg.getSipConfig().getProxies();
                proxies.clear();

                accCfg.getNatConfig().setIceEnabled(false);
                accCfg.getNatConfig().setViaRewriteUse(0);
                accCfg.getNatConfig().setContactRewriteUse(0);


                account = app.addAcc(accCfg);
            } else {

                accCfg = new AccountConfig();

                accCfg.setIdUri("sip:"+sip_log+"@92.50.152.146:5401");
                accCfg.getRegConfig().setRegistrarUri("sip:92.50.152.146:5401");
                AuthCredInfoVector creds = accCfg.getSipConfig().
                        getAuthCreds();
                creds.clear();

                creds.add(new AuthCredInfo("Digest", "*", sip_log, 0,
                        sip_pass));

                StringVector proxies = accCfg.getSipConfig().getProxies();
                proxies.clear();

                accCfg.getNatConfig().setIceEnabled(false);
                accCfg.getNatConfig().setViaRewriteUse(0);
                accCfg.getNatConfig().setContactRewriteUse(0);



                account = app.accList.get(0);
                try {
                    account.modify(accCfg);
                } catch (Exception e) {
                }

            }
            registerReceiver(mBatInfoReceiver, new IntentFilter(
                    ConnectivityManager.CONNECTIVITY_ACTION));

        }


        return START_REDELIVER_INTENT;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.i("Test", "Service: onDestroy");

    }


    @Override
    public boolean handleMessage(Message m)
    {
        if (m.what == 0) {
            app.deinit();
            Runtime.getRuntime().gc();
            stopService(new Intent(this, MyService.class));
            android.os.Process.killProcess(android.os.Process.myPid());
        } else if (m.what == MSG_TYPE.CALL_STATE) {
            CallInfo ci = (CallInfo) m.obj;

	    /* Forward the message to CallActivity */
            if (CallActivity.handler_ != null) {
                Message m2 = Message.obtain(CallActivity.handler_,
                        MSG_TYPE.CALL_STATE, ci);
                m2.sendToTarget();
            }

        } else if (m.what == MSG_TYPE.CALL_MEDIA_STATE) {

	    /* Forward the message to CallActivity */
            if (CallActivity.handler_ != null) {
                Message m2 = Message.obtain(CallActivity.handler_,
                        MSG_TYPE.CALL_MEDIA_STATE,
                        null);
                m2.sendToTarget();
            }

        }


        else if (m.what == MSG_TYPE.REG_STATE) {

            msg_str = (String) m.obj;


        } else if (m.what == MSG_TYPE.INCOMING_CALL) {

	    /* Incoming call */
            final MyCall call = (MyCall) m.obj;
            CallOpParam prm = new CallOpParam();

	    /* Only one call at anytime */
            if (currentCall != null) {
		/*
		prm.setStatusCode(pjsip_status_code.PJSIP_SC_BUSY_HERE);
		try {
		call.hangup(prm);
		} catch (Exception e) {}
		*/
                // TODO: set status code
                call.delete();
                return true;
            }

	    /* Answer with ringing */
            prm.setStatusCode(pjsip_status_code.PJSIP_SC_RINGING);
            try {
                call.answer(prm);
            } catch (Exception e) {}

            currentCall = call;
            showCallActivity();

        } else if (m.what == MSG_TYPE.CHANGE_NETWORK) {
            app.handleNetworkChange();
        } else {

	    /* Message not handled */
            return false;

        }

        return true;
    }





    public void notifyIncomingCall(MyCall call)
    {
        Message m = Message.obtain(handler, MSG_TYPE.INCOMING_CALL, call);
        m.sendToTarget();
    }

    public void notifyRegState(pjsip_status_code code, String reason,
                               int expiration)
    {
        String msg_str = "";
        if (expiration == 0)
            msg_str += "Unregistration";
        else
            msg_str += "Registration";

        if (code.swigValue()/100 == 2)
            msg_str += " successful";
        else
            msg_str += " failed: " + reason;

        Message m = Message.obtain(handler, MSG_TYPE.REG_STATE, msg_str);
        m.sendToTarget();
    }

    public void notifyCallState(MyCall call)
    {
        if (currentCall == null || call.getId() != currentCall.getId())
            return;

        CallInfo ci;
        try {
            ci = call.getInfo();
        } catch (Exception e) {
            ci = null;
        }
        Message m = Message.obtain(handler, MSG_TYPE.CALL_STATE, ci);
        m.sendToTarget();

        if (ci != null &&
                ci.getState() == pjsip_inv_state.PJSIP_INV_STATE_DISCONNECTED)
        {
            currentCall = null;
        }
    }

    public void notifyCallMediaState(MyCall call)
    {
        Message m = Message.obtain(handler, MSG_TYPE.CALL_MEDIA_STATE, null);
        m.sendToTarget();
    }

    public void notifyBuddyState(MyBuddy buddy)
    {
        Message m = Message.obtain(handler, MSG_TYPE.BUDDY_STATE, buddy);
        m.sendToTarget();
    }

    public void notifyChangeNetwork()
    {
        Message m = Message.obtain(handler, MSG_TYPE.CHANGE_NETWORK, null);
        m.sendToTarget();
    }

    public class MSG_TYPE
    {
        public final static int INCOMING_CALL = 1;
        public final static int CALL_STATE = 2;
        public final static int REG_STATE = 3;
        public final static int BUDDY_STATE = 4;
        public final static int CALL_MEDIA_STATE = 5;
        public final static int CHANGE_NETWORK = 6;

    }




    private HashMap<String, String> putData(String uri, String status)
    {
        HashMap<String, String> item = new HashMap<String, String>();
        item.put("uri", uri);
        item.put("status", status);
        return item;
    }

    public void showCallActivity()
    {
        Intent intent = new Intent(getApplicationContext(), CallActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.putExtra("from", "service");
        startActivity(intent);
    }



    public boolean isOnline() {
        ConnectivityManager cm =
                (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        return netInfo != null && netInfo.isConnectedOrConnecting();
    }




    public static void DeInit() {
        if (app != null) {
            app.delAcc(account);
            account.delete();
        }
    }

    public void ReRegister() {
        if (app == null) {
            app = new MyApp();
            // Wait for GDB to init, for native debugging only
            if (false &&
                    (getApplicationInfo().flags &
                            ApplicationInfo.FLAG_DEBUGGABLE) != 0) {
                try {
                    Thread.sleep(5000);
                } catch (InterruptedException e) {
                }
            }

            app.init(this, getFilesDir().getAbsolutePath());
        }


        if (app.accList.size() == 0) {
            accCfg = new AccountConfig();

            accCfg.setIdUri("sip:"+sip_log+"@92.50.152.146:5401");
            accCfg.getRegConfig().setRegistrarUri("sip:92.50.152.146:5401");
            AuthCredInfoVector creds = accCfg.getSipConfig().
                    getAuthCreds();
            creds.clear();

            creds.add(new AuthCredInfo("Digest", "*", sip_log, 0,
                    sip_pass));

            StringVector proxies = accCfg.getSipConfig().getProxies();
            proxies.clear();

            accCfg.getNatConfig().setIceEnabled(false);
            accCfg.getNatConfig().setViaRewriteUse(0);
            accCfg.getNatConfig().setContactRewriteUse(0);

            account = app.addAcc(accCfg);
        } else {

            accCfg = new AccountConfig();

            accCfg.setIdUri("sip:"+sip_log+"@92.50.152.146:5401");
            accCfg.getRegConfig().setRegistrarUri("sip:92.50.152.146:5401");
            AuthCredInfoVector creds = accCfg.getSipConfig().
                    getAuthCreds();
            creds.clear();

            creds.add(new AuthCredInfo("Digest", "*", sip_log, 0,
                    sip_pass));

            StringVector proxies = accCfg.getSipConfig().getProxies();
            proxies.clear();

            accCfg.getNatConfig().setIceEnabled(false);
            accCfg.getNatConfig().setViaRewriteUse(0);
            accCfg.getNatConfig().setContactRewriteUse(0);
            account = app.accList.get(0);
            try {
                account.modify(accCfg);
            } catch (Exception e) {
            }


        }
    }


    private BroadcastReceiver mBatInfoReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(final Context c, Intent i) {
            ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);

            NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
            if (null != activeNetwork) {
                if (activeNetwork.getType() == ConnectivityManager.TYPE_WIFI) {
                    DeInit();
                    Handler handler = new Handler();
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            ReRegister();
                            Handler handler1 = new Handler();
                            handler1.postDelayed(new Runnable() {
                                @Override
                                public void run() {


                                    Notification notification = new NotificationCompat.Builder(c)
                                            .setSmallIcon(R.mipmap.ico)
                                            .setContentTitle(sip_log)
                                            .setContentText(msg_str)
                                            .build();
                                    startForeground(1337, notification);



                                }
                            }, 2000);

                        }
                    }, 2000);

                }



                if (activeNetwork.getType() == ConnectivityManager.TYPE_MOBILE){
                    DeInit();
                    Handler handler = new Handler();
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            ReRegister();

                            Handler handler1 = new Handler();
                            handler1.postDelayed(new Runnable() {
                                @Override
                                public void run() {

                                    Notification notification = new NotificationCompat.Builder(c)
                                            .setSmallIcon(R.mipmap.ico)
                                            .setContentTitle(sip_log)
                                            .setContentText(msg_str)
                                            .build();

                                    startForeground(1337, notification);

                                }
                            }, 2000);
                        }
                    }, 2000);

                }
            }

            else {

                        Notification notification = new NotificationCompat.Builder(c)
                                .setSmallIcon(R.mipmap.ico)
                                .setContentTitle(sip_log)
                                .setContentText("Ожидание сети")
                                .build();

                        startForeground(1337, notification);
                
                Toast.makeText(getApplicationContext(),"Отсутствует активное подключение" + "\n    Переподключение как только появится доступная сеть", Toast.LENGTH_LONG).show();
            }


        }
    };


    @SuppressLint("StaticFieldLeak")
    public static void GetContacts() {

        new AsyncTask<Void, Integer, String>() {

            @Override
            protected String doInBackground(Void... voids) {
                publishProgress();
                return getServerResponse(token);

            }

            @Override
            protected void onProgressUpdate(Integer... values) {
                super.onProgressUpdate(values);

            }

            @Override
            protected void onPostExecute(String result) {
                contactListWorker.clear();
                try {
                    JSONArray parentArray = new JSONArray(result);

                    for (int i = 0; i < parentArray.length(); i++) {

                        JSONObject parentObject = parentArray.getJSONObject(i);
                        String name = parentObject.getString("firstName");
                        String last_name = parentObject.getString("lastName");
                        String company = parentObject.getString("company");
                        String position = parentObject.getString("position");
                        String email = parentObject.getString("email");

                        JSONArray parentArrayFinal = parentObject.getJSONArray("phoneNumbers");
                        JSONObject parentObjectFinal = parentArrayFinal.getJSONObject(0);
                        String phone_num = parentObjectFinal.getString("phoneNumber");
                        contactListWorker.add(new ItemContactsWorkers(name + " " + last_name, company + ", " + position + ", " + email , company, phone_num));
                    }


                } catch (JSONException e) {
                    e.printStackTrace();
                }

                }

        }.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }

    private static String getServerResponse(String token) {

        HttpGet post = new HttpGet("https://telefon.ufanet.ru/api/Contacts");
        try {
            post.setHeader("Content-Type", "application/json");
            post.setHeader("Authorization", token);

            DefaultHttpClient client = new DefaultHttpClient();
            BasicResponseHandler handler = new BasicResponseHandler();

            String response = client.execute(post, handler);

            return response;
        } catch (UnsupportedEncodingException e) {
            Log.d("JWP", e.toString());
        } catch (ClientProtocolException e) {
            Log.d("JWP", e.toString());
        } catch (IOException e) {
            Log.d("JWP", e.toString());
        }
        return "";
    }

}
